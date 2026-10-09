package com.console.framework.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisPlusConfig {
    @Value("${database.prefix:console}")
    private String dbPrefix;
    private final List<String> baseTables = List.of(//基础表（总租户表）
            "s_tenant","s_tenant_profile",
            "s_transfer",
            "s_product_platform",
            "s_product",
            "s_error_record");


    /**
     * 数据库插件添加插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor(com.baomidou.mybatisplus.annotation.DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //动态表名规则添加
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            // 如果表名已经包含前缀，直接返回
            if (tableName.contains(".")) {
                return tableName;
            }
            if (tableName.contains("temp_")) {
                return tableName;
            }
            // 如果表名在基础表列表中，直接加上前缀并返回
            if (baseTables.contains(tableName)) {
                return dbPrefix + "." + tableName;
            }

            // 获取当前上下文中的租户编码
            Integer tenantId = DynamicTableNameHandler.getTenantId();

            // 检查serverId是否有效
            if (tenantId == null || tenantId == 0) {
                return String.format("%s.%s", dbPrefix, tableName);
            }
            // 根据serverId拼接完整的表名
            return String.format("%s_%d.%s", dbPrefix, tenantId, tableName);
        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }
}
