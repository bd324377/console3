package com.console.framework.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RedisPublishMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long      id;
    private String    tenantCode;       //租户编码
    private Integer   userId;           //用户ID     存在userId则是单独发送，否则为公告发送
    private SocketMsg socketMsg;        //待发送消息
}
