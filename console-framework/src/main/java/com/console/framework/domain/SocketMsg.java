package com.console.framework.domain;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class SocketMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;
    private String msg;
    private String token;
    private Object data;

    public SocketMsg() {}

    public static SocketMsg heartBeat() {
        SocketMsg socketMsg = new SocketMsg();
        socketMsg.setType("ping");
        socketMsg.setMsg("");
        return socketMsg;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
