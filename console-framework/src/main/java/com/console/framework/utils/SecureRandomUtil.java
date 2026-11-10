package com.console.framework.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToLongFunction;

public class SecureRandomUtil {
    /**
     * 复用单例 SecureRandom。
     * SecureRandom 本身是线程安全的，无需每次新建实例（初始化开销较大）。
     * 在 JDK 8u161+ 及现代 Linux 内核下，new SecureRandom() 默认会选用合适的非阻塞算法。
     */
    private static final SecureRandom RNG = new SecureRandom();

    private SecureRandomUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    /** 无偏的 [0, bound) 随机整数 */
    public static int nextInt(int bound) {
        if (bound <= 0) throw new IllegalArgumentException("bound 必须 > 0");
        return RNG.nextInt(bound);
    }

    /**
     * 不放回抽取 n 个（结果互不重复，顺序随机）
     * 返回副本，不修改原奖池。业务层拿到中奖名单后自行从奖池移除。
     */
    public static <T> List<T> drawMultiple(List<T> pool, int n) {
        if (pool == null) throw new IllegalArgumentException("奖池不能为空");
        if (n < 0 || n > pool.size()) throw new IllegalArgumentException("抽取数量非法");
        List<T> copy = new ArrayList<>(pool);
        // 部分 Fisher-Yates：只需洗前 n 位
        for (int i = 0; i < n; i++) {
            int j = i + RNG.nextInt(copy.size() - i);
            Collections.swap(copy, i, j);
        }
        return new ArrayList<>(copy.subList(0, n));
    }

    /** 全量洗牌（如生成中奖顺序、抽奖排期等） */
    public static <T> void shuffle(List<T> list) {
        if (list == null) return;
        for (int i = list.size() - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            Collections.swap(list, i, j);
        }
    }

    /* ==================== 概率判定 ==================== */

    /** 命中概率 probability ∈ [0, 1]，用于"这一抽是否中奖"的二元判定 */
    public static boolean hit(double probability) {
        if (probability <= 0.0) return false;
        if (probability >= 1.0) return true;
        return RNG.nextDouble() < probability;
    }

    /** 千分比判定，避免 double 精度问题，推荐业务层使用 */
    public static boolean hitPermille(int permille) {
        if (permille <= 0) return false;
        if (permille >= 1000) return true;
        return RNG.nextInt(1000) < permille;
    }

    /* ==================== 权重抽奖 ==================== */

    /**
     * 按权重抽取一个元素。
     * 使用 long 累加权重，避免 double 浮点误差导致的边界命中偏差；
     * 使用 Math.addExact 检测总权重溢出。
     */
    public static <T> T drawWeighted(List<T> items, ToLongFunction<T> weightFn) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("奖池不能为空");
        }
        long total = 0;
        for (T item : items) {
            long w = weightFn.applyAsLong(item);
            if (w < 0) throw new IllegalArgumentException("权重不能为负");
            total = Math.addExact(total, w);  // 溢出直接抛异常
        }
        if (total <= 0) throw new IllegalArgumentException("总权重必须 > 0");

        long r = RNG.nextLong(total);
        long cum = 0;
        for (T item : items) {
            cum += weightFn.applyAsLong(item);
            if (r < cum) return item;
        }
        // 理论上不可达，兜底返回最后一个非零权重项
        return items.get(items.size() - 1);
    }
}
