package com.console.core;

import com.github.xiaolyuh.cache.config.EnableLayeringCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.console"})
@MapperScan({"com.console.*.*.mapper","com.console.*.mapper"})
@EnableLayeringCache
public class ConsoleCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsoleCoreApplication.class);
    }
}
