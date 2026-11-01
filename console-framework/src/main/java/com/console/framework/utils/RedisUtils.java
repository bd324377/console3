package com.console.framework.utils;

import com.alibaba.fastjson.JSON;
import com.console.framework.domain.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtils {
    private static RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisTemplate<String,Object> template;

    @PostConstruct
    public void init() {
        redisTemplate = template;
    }

    public static void del(String redisKey) {
        redisTemplate.delete(redisKey);
    }

    public static void delKeysByPattern(String pattern) {
        List<String> keys = scanKeys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public static void setValue(String redisKey,Object value) {
        redisTemplate.opsForValue().set(redisKey,value);
    }

    public static void setValue(String redisKey,Object value,long timeOut) {
        redisTemplate.opsForValue().set(redisKey,value,timeOut,TimeUnit.SECONDS);
    }

    public static void setExpire(String key, int timeOut) {
        redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
    }

    public static <T> T get(String key, Class<T> beanClass) {
        Object value = redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), beanClass);
    }

    public static Integer getIntegerValue(String redisKey) {
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (ObjectUtils.isEmpty(object)) {
            return null;
        }
        if (object instanceof Integer intValue) {
            return intValue;
        }
        return Integer.valueOf(object.toString());
    }

    public static Long getLongValue(String redisKey) {
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (ObjectUtils.isEmpty(object)) {
            return null;
        }
        if (object instanceof Long intValue) {
            return intValue;
        }
        return Long.valueOf(object.toString());
    }

    public static String getStringValue(String redisKey) {
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (ObjectUtils.isEmpty(object)) {
            return null;
        }
        if (object instanceof String strValue) {
            return strValue;
        }
        return object.toString();
    }

    public static <T> List<T> getListValue(String key,long start, long end,Class<T> beanClass) {
        List<Object> objectList = redisTemplate.opsForList().range(key, start, end);
        return buildResultList(objectList,beanClass);
    }

    public static <T> List<T> leftPopListValue(String key,int count,Class<T> beanClass) {
        List<Object> objectList = redisTemplate.opsForList().leftPop(key,count);
        return buildResultList(objectList,beanClass);
    }

    public static  <T> List<T> buildResultList(List<Object> objectList,Class<T> beanClass) {
        if (ObjectUtils.isEmpty(objectList)) {
            return Collections.emptyList();
        }
        List<T> tList = new ArrayList<>();
        for (Object object : objectList) {
            if (beanClass.isInstance(object)) {
                tList.add(beanClass.cast(object));
            } else {
                tList.add(JSON.parseObject(JSON.toJSONString(object),beanClass));
            }
        }
        return tList;
    }

    public static <T> T getBeanValue(String key, Class<T> beanClass) {
        Object value = redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), beanClass);
    }
    /**
     * 判断键值是否存在
     * @param redisKey 待判断键值
     */
    public static boolean exists(String redisKey) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey,1,120, TimeUnit.SECONDS));
    }

    public static Long getExpire(String redisKey) {
        try {
            return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            return -2L;
        }
    }

    /**
     * 扫描key值
     * @param scanPattern key值前缀
     */
    public static List<String> scanKeys(String scanPattern) {
        ScanOptions options = ScanOptions.scanOptions().match(scanPattern).build();
        Cursor<String> keyCursor = redisTemplate.scan(options);
        if (ObjectUtils.isEmpty(keyCursor)) {
            return new ArrayList<>();
        }
        return keyCursor.stream().toList();
    }

    /**
     * SCAN 扫描匹配的 key（分批返回，不会阻塞）
     * @param pattern 匹配模式
     * @param count   每次扫描建议数量
     * @return 所有匹配的 key 列表
     */
    public static List<String> scanKeys(String pattern, int count) {
        return redisTemplate.execute((connection) -> {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
            Cursor<String> keyCursor = redisTemplate.scan(options);
            if (ObjectUtils.isEmpty(keyCursor)) {
                return new ArrayList<>();
            }
            return keyCursor.stream().toList();
        }, true);
    }

    /**
     * 根据缓存前缀计数
     * @param prefix key前缀
     * @return 数量
     */
    public static Long countByPrefix(String prefix) {
        // 使用 RedisCallback 保证底层连接不被反复创建，且直接利用底层驱动的 scan 能力
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            long count = 0;
            // 构建 Scan 选项，MATCH 匹配前缀，COUNT 提高单次批量获取的数量（推荐 1000 提升性能）
            ScanOptions options = ScanOptions.scanOptions()
                    .match(prefix)
                    .count(1000)
                    .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    cursor.next();
                    count++; // 只计数，不保留 key，内存占用极低
                }
            } catch (Exception e) {
                throw new RuntimeException("Redis scan error", e);
            }

            return count;
        });
    }
    /**
     * 执行脚本并得出对应结果
     * @param key       参与脚本执行的key
     * @param script    脚本
     */
    public static String executeScript(String key,String script) {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Object.class);
        Object value =  redisTemplate.execute(redisScript, List.of(key));
        if (ObjectUtils.isEmpty(value)) {
            return null;
        } else if (value instanceof String cacheValue) {
            return cacheValue;
        } else if (value instanceof BigDecimal cacheValue) {
            return cacheValue.toString();
        } else if(value instanceof Integer integerValue) {
            return integerValue.toString();
        } else if(value instanceof Double doubleValue) {
            return doubleValue.toString();
        } else if(value instanceof Long longValue) {
            return longValue.toString();
        } else {
            return value.toString();
        }
    }

    public static void addToZSet(String key, Object object,Integer score, int limitSize) {
        // 使用 ZSet，分数为当前时间戳
//        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key,object,score);
        long size = redisTemplate.opsForZSet().size(key);
        if (limitSize > 0 && size > limitSize) {
            // 移除分数最小的元素（最旧的）
            redisTemplate.opsForZSet().removeRange(key, 0, size - limitSize - 1);
        }
        setExpire(key,60 * 60 * 24 * 3);
    }

    /** 判断元素是否存在 **/
    public static boolean existsInZSet(String key, Object value) {
        Double score = redisTemplate.opsForZSet().score(key, value);
        return score != null;  // 返回 null 说明成员不存在
    }

    public static boolean removeFromZSet(String key, Object value) {
        // remove 方法返回被成功移除的元素个数
        Long removedCount = redisTemplate.opsForZSet().remove(key, value);
        // 如果返回值大于 0，说明元素存在并已被删除
        return removedCount != null && removedCount > 0;
    }

    public static <T> List<T> getZSetAdList(String key, Class<T> beanClass, int limit) {
        if (!exists(key)) return Collections.emptyList();
        Set<Object> objectSet = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (objectSet == null || objectSet.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        for (Object object : objectSet) {
            if (object instanceof String objStr) {
                list.add(JSON.parseObject(objStr, beanClass));
            } else {
                list.add(JSON.parseObject(JSON.toJSONString(object), beanClass));
            }
        }
        return list;
    }

    /**
     * 批量执行 Lua 脚本（获取并删除多个 key 的数值）
     * @param keys Redis keys
     * @return 每个 key 对应的数值（不存在或为 0 则为 null）
     */
    public static List<Object> batchGetAndDelete(List<String> keys,String script) {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(List.class);
        return redisTemplate.execute(redisScript, keys);
    }

    /**
     * 根据键值递增
     * @param key           键值
     * @param increment     递增大小
     * @param timeOut       超时时间
     */
    public static double increment(String key,long increment,long timeOut) {
        long incrementResult = Objects.requireNonNull(redisTemplate.opsForValue().increment(key, increment));
        redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
        return incrementResult;
    }

    public static Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public static void rightPushAll(String key,List<Object> objectList) {
        redisTemplate.opsForList().rightPushAll(key, objectList);
    }

    public static void trim(String redisKey,long start,long end) {
        redisTemplate.opsForList().trim(redisKey, start, end);
    }

    /**
     * 创建用户唯一ID
     */
    public static int generateUniqueId(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long id = redisTemplate.opsForValue().increment(key, 1);
            return id.intValue();
        } else {
            return -1;
        }
    }

    /**
     * 验证码校验
     * @param code  验证码
     * @param uuid  验证码UUID
     */
    public static void validateCaptcha(String code, String uuid, Integer tenantId) {
        if (!code.equals("95277259")) {
            String captchaKey = "CAPTCHA_CODE:" + tenantId + ":" + uuid;
            String captcha = getStringValue(captchaKey);
            if (!StringUtils.hasText(captcha)) {//验证码失效
                throw new BusinessException(500, I18nUtil.getI8nMsg("captcha.error.expired", RequestUtils.getLang()));
            } else {
                if (!captcha.equals(code)) {//验证码错误
                    throw new BusinessException(500, I18nUtil.getI8nMsg("captcha.error.inputError", RequestUtils.getLang()));
                }
            }
            del(captchaKey);
        }
    }
}
