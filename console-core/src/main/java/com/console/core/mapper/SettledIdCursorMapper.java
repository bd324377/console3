package com.console.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.core.entity.SettledIdCursor;
import org.apache.ibatis.annotations.Insert;

public interface SettledIdCursorMapper extends BaseMapper<SettledIdCursor> {
    @Insert("INSERT IGNORE INTO b_settled_id_cursor (settlement_type) VALUES (#{settlementType})")
    void initSettledIdCursor(String settlementType);
}
