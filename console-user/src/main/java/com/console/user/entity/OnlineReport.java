package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName(value = "s_online_report")
public class OnlineReport implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private LocalDate reportDate;
    private LocalDateTime reportTime;
    private Integer tenantId;
    private Integer marketSiteId;
    private Integer maketTeamId;
    private Integer maketerId;
    private Long onlineUserCount;
}
