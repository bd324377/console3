package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.utils.RedisUtils;
import com.console.user.entity.UserId;
import com.console.user.mapper.UserIdMapper;
import com.console.user.merger.UserIdMerger;
import com.console.user.service.UserIdService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserIdServiceImpl extends BaseServiceImpl<UserIdMapper, UserId> implements UserIdService {
    @Resource
    private UserIdMerger userIdMerger;

    @Override
    public Integer initUserId() {
        int index = (int) (System.currentTimeMillis() % 9) + 1;
        String key = "UNIQUE_ID:ID:" + index;
        int originId = RedisUtils.generateUniqueId(key);
        if (originId == -1) {//redis中未缓存，从数据控中读取
            QueryWrapper<UserId> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("max(id) as id").lambda().eq(UserId::getPartitionCode,index);
            UserId maxUserId = this.getOne(queryWrapper);
            if (maxUserId != null) {
                originId = maxUserId.getId() + 1;
            } else {
                originId = index * 10000000;
            }
            RedisUtils.setValue(key,originId);
        }
        Integer userId = bitXOR(rightShift(originId,3),2385);
        UserId userIdRecord = new UserId();
        userIdRecord.setId(originId);
        userIdRecord.setUserId(userId);
        userIdRecord.setPartitionCode(index);
        userIdMerger.submit(userIdRecord);
        return userId;
    }

    public static int rightShift(int num, int shiftAmount) {
        int highestDigit = num / 10000000; // 获取原始数字的最高位
        String restDigits = Long.toString(num).substring(1); // 获取原始数字的其余位
        // 将其余位向右移动指定的位数
        restDigits = restDigits.substring(restDigits.length() - shiftAmount) +
                restDigits.substring(0, restDigits.length() - shiftAmount);
        // 组合得到右移后的数字
        return Integer.parseInt(highestDigit + restDigits);
    }

    // 计算两个整数的按位异或操作
    public static int bitXOR(long a, int b) {
        int result = 0; // 初始化结果变量用于存储异或结果
        int bit = 1; // 初始化位表示当前位的权值
        // 循环处理整数a和b的每一位
        while (a > 0 || b > 0) {
            long aBit = a % 2; // 获取a的最右边位
            int bBit = b % 2; // 获取b的最右边位

            // 如果这两位不同，异或结果应在这个位置为1
            if (aBit != bBit) {
                result += bit;
            }

            a = a / 2; // 右移a以处理下一位
            b = b / 2; // 右移b以处理下一位
            bit *= 2; // 更新下一次迭代的位位置
        }

        return result; // 返回最终异或结果
    }
}
