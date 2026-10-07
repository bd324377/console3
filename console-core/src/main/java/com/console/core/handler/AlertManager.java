package com.console.core.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertManager {
//    private final AlertProperties alertProperties;

    /**
     * 对指定异常进行计数，并判断是否触发预警
     * @param exceptionType 异常类全名
     * @param nowMillis 当前毫秒时间戳
     */
    public void checkAndAlert(String exceptionType, long nowMillis) {

    }
}
