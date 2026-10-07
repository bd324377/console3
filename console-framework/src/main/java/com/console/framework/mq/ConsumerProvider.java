package com.console.framework.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ConsumerProvider {
    @Value("${redis.stream.consumer-prefix:consumer}")
    private String consumerPrefix;

    public String getConsumerName() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = "unknown-host";
        }
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String instanceId = System.getProperty("instance.id", hostName + "-" + pid);
        return consumerPrefix + "-" + instanceId;
    }
}
