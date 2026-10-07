package com.console.framework.utils;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
}
