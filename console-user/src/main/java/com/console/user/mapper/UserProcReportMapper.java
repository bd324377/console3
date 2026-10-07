package com.console.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.user.entity.UserProcReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserProcReportMapper extends BaseMapper<UserProcReport> {
    @Select("SELECT\n" +
            "\tIFNULL(SUM( t.recharge_amt ),0) recharge_amt,\n" +
            "\tIFNULL(SUM( t.order_amt ),0) order_amt \n" +
            "FROM\n" +
            "\ts_user u\n" +
            "\tINNER JOIN s_user_proc_report t ON t.user_id = u.id \n" +
            "WHERE\n" +
            "\tu.p_id = #{agentUserId}")
    UserProcReport selectSubUserReport(@Param("agentUserId") Integer agentUserId);
}
