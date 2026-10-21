package com.console.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.console")
@MapperScan({"com.console.*.*.mapper", "com.console.*.mapper"})
public class ConsoleProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsoleProductApplication.class, args);
    }
}
