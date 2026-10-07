package com.console.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.console"})
@MapperScan({"com.console.*.*.mapper","com.console.*.mapper"})
public class ConsoleApiApplication {
}
