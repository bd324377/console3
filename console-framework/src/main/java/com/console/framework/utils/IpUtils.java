package com.console.framework.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IpUtils {
    private static final String ATTR_CLIENT_IP = "IpUtils.CLIENT_REAL_IP";

    // ---------- 可信代理网段（按需调整，支持 IPv4 CIDR） ----------
    private static final List<String> TRUSTED_CIDRS = Arrays.asList(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1",      // IPv6 localhost
            "10.0.0.0/8",
            "172.16.0.0/12",
            "192.168.0.0/16"
    );

    private static final List<SubnetMatcher> TRUSTED_MATCHERS = new ArrayList<>();
    static {
        for (String cidr : TRUSTED_CIDRS) {
            try {
                TRUSTED_MATCHERS.add(new SubnetMatcher(cidr));
            } catch (IllegalArgumentException ignored) {
                // 忽略非法格式（仅影响该条规则）
            }
        }
    }

    // ---------- 非标准请求头列表 ----------
    private static final List<String> LEGACY_HEADERS = Arrays.asList(
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED"
    );

    // ---------- 正则表达式（解析 Forwarded 和剥离端口） ----------
    private static final Pattern FORWARDED_PATTERN = Pattern.compile("for\\s*=\\s*\"?([^;\",]+)\"?");
    private static final Pattern IPV4_PORT = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::\\d+)?$");
    private static final Pattern IPV6_BRACKET_PORT = Pattern.compile("^\\[([0-9a-fA-F:]+)](?::\\d+)?$");
    private static final Pattern IPV6_PLAIN_PORT = Pattern.compile("^([0-9a-fA-F:]+):\\d+$");

    private IpUtils() {}

    /**
     * 获取客户端真实 IP（主入口）
     *
     * @param request HttpServletRequest
     * @return 客户端 IP 字符串，无法获取时返回 null
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 1. 缓存检查
        String cached = (String) request.getAttribute(ATTR_CLIENT_IP);
        if (cached != null) {
            return cached;
        }

        // 2. 解析
        String ip = resolveIp(request);

        // 3. 缓存并返回
        request.setAttribute(ATTR_CLIENT_IP, ip);
        return ip;
    }

    // ------------------------- 核心解析逻辑 -------------------------

    private static String resolveIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        // 若非可信代理，直接返回直连 IP（防止伪造）
        if (!isTrustedProxy(remoteAddr)) {
            return remoteAddr;
        }

        // 尝试标准 Forwarded 头
        String ip = resolveForwarded(request);
        if (ip != null) return ip;

        // 尝试 X-Forwarded-For
        ip = resolveXForwardedFor(request);
        if (ip != null) return ip;

        // 尝试其他非标准头
        ip = resolveLegacyHeaders(request);
        if (ip != null) return ip;

        // 最终回退
        return remoteAddr;
    }

    // ------------------------- 各种头部解析 -------------------------

    private static String resolveForwarded(HttpServletRequest request) {
        String forwarded = request.getHeader("Forwarded");
        if (forwarded == null || forwarded.isBlank()) {
            return null;
        }
        Matcher m = FORWARDED_PATTERN.matcher(forwarded);
        if (m.find()) {
            String ip = m.group(1).trim();
            ip = stripPort(ip);
            if (!ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static String resolveXForwardedFor(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null || xff.isBlank()) {
            return null;
        }

        String[] ips = xff.split(",");
        // 从右向左遍历，跳过所有可信代理
        for (int i = ips.length - 1; i >= 0; i--) {
            String candidate = ips[i].trim();
            if (candidate.isBlank() || "unknown".equalsIgnoreCase(candidate)) {
                continue;
            }
            if (!isTrustedProxy(candidate)) {
                return candidate;   // 第一个非信任 IP 即为真实客户端
            }
        }
        // 若全部都是可信代理，返回最左边（最接近客户端）的 IP
        String first = ips[0].trim();
        return (first.isBlank() || "unknown".equalsIgnoreCase(first)) ? null : first;
    }

    private static String resolveLegacyHeaders(HttpServletRequest request) {
        for (String header : LEGACY_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.trim();
            }
        }
        return null;
    }

    // ------------------------- 辅助方法 -------------------------

    private static boolean isTrustedProxy(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }
        for (SubnetMatcher matcher : TRUSTED_MATCHERS) {
            if (matcher.matches(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去除 IP 地址中的端口部分（IPv4 / IPv6 均支持）
     */
    private static String stripPort(String ip) {
        if (ip == null) return null;
        Matcher m4 = IPV4_PORT.matcher(ip);
        if (m4.matches()) return m4.group(1);
        Matcher m6b = IPV6_BRACKET_PORT.matcher(ip);
        if (m6b.matches()) return m6b.group(1);
        Matcher m6p = IPV6_PLAIN_PORT.matcher(ip);
        if (m6p.matches()) return m6p.group(1);
        return ip;
    }

    // ------------------------- CIDR 匹配器（仅 IPv4） -------------------------

    private static class SubnetMatcher {
        private final int network;
        private final int subnetMask;

        SubnetMatcher(String cidr) {
            String[] parts = cidr.split("/");
            String ipPart = parts[0];
            int maskBits = (parts.length > 1) ? Integer.parseInt(parts[1]) : 32;
            try {
                InetAddress inet = InetAddress.getByName(ipPart);
                byte[] addr = inet.getAddress();
                if (addr.length != 4) {
                    // IPv6 暂不支持，置零使其永不匹配
                    network = 0;
                    subnetMask = 0;
                    return;
                }
                network = ((addr[0] & 0xFF) << 24) |
                        ((addr[1] & 0xFF) << 16) |
                        ((addr[2] & 0xFF) << 8) |
                        (addr[3] & 0xFF);
                subnetMask = (maskBits == 0) ? 0 : (0xFFFFFFFF << (32 - maskBits));
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid IP address: " + ipPart, e);
            }
        }

        boolean matches(String ip) {
            try {
                InetAddress inet = InetAddress.getByName(ip);
                byte[] addr = inet.getAddress();
                if (addr.length != 4) return false;   // 不匹配 IPv6
                int ipInt = ((addr[0] & 0xFF) << 24) |
                        ((addr[1] & 0xFF) << 16) |
                        ((addr[2] & 0xFF) << 8) |
                        (addr[3] & 0xFF);
                return (ipInt & subnetMask) == (network & subnetMask);
            } catch (UnknownHostException e) {
                return false;
            }
        }
    }
}
