package com.console.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.product.entity.UserProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserProductMapper extends BaseMapper<UserProduct> {
    void replaceBatchUserProduct(@Param("userProducts") List<UserProduct> userProducts);
}
