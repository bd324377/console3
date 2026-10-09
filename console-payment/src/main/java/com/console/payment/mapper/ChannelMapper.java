package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.Channel;

public interface ChannelMapper extends BaseMapper<Channel> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM s_channel WHERE id=#{id} FOR UPDATE")
    Channel lockById(Integer id);
}
