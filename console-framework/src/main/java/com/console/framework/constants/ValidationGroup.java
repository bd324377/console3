package com.console.framework.constants;

public interface ValidationGroup {
    /**
     * 新增数据参数校验分组
     **/
    interface insert {}

    /**
     * 删除数据参数校验分组
     **/
    interface delete {}

    /**
     * 更新数据数校验分组
     **/
    interface update {}

    /**
     * 查询数据参数校验分组
     **/
    interface search {}

    /**
     * 报表数据查询
     **/
    interface report {}
}
