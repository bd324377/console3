package com.console.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.user.entity.Label;
import org.apache.ibatis.annotations.Param;

public interface LabelMapper extends BaseMapper<Label> {
    IPage<Label> selectLabelPage(Page<Label> page, @Param("param") Label label);
}
