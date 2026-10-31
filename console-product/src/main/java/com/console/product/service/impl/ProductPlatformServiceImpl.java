package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.product.dto.ProductPlatformReq;
import com.console.product.entity.ProductPlatform;
import com.console.product.mapper.ProductPlatformMapper;
import com.console.product.service.ProductPlatformService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductPlatformServiceImpl extends BaseServiceImpl<ProductPlatformMapper,ProductPlatform> implements ProductPlatformService {

    @Override
    public boolean syncPlatformList() {
        return false;
    }

    @Override
    @CacheEvict(value = {CacheConstants.PRODUCT_PLATFORM,CacheConstants.PRODUCT_PLATFORM_LIST},allEntries = true)
    public boolean updateProductPlatform(String field, ProductPlatformReq.UpdateProductPlatformReq updateProductPlatformReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        QueryWrapper<ProductPlatform> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(field)).lambda().eq(ProductPlatform::getId,updateProductPlatformReq.getId());
        ProductPlatform productPlatform = this.getOne(queryWrapper);
        updateProductPlatformReq.buildUpdateParam(field,productPlatform,requestBackendUser);
//        if (StringUtils.hasText(updateProductPlatformReq.getErrorDesc())) {
//            throw new BusinessException(500, updateProductPlatformReq.getErrorDesc());
//        }
        if (this.update(updateProductPlatformReq.getUpdateWrapper())) {//更新产商信息成功
            adminProcMapper.insert(updateProductPlatformReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    @Cacheable(value = CacheConstants.PRODUCT_PLATFORM,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #platformId")
    public ProductPlatform getProductPlatformById(Integer platformId) {
        return this.getById(platformId);
    }
}
