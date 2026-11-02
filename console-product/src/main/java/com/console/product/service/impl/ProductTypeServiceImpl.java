package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.product.dto.ProductTypeReq;
import com.console.product.entity.ProductType;
import com.console.product.entity.TypeProduct;
import com.console.product.mapper.ProductTypeMapper;
import com.console.product.service.ProductTypeService;
import com.console.product.service.TypeProductService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductTypeServiceImpl extends BaseServiceImpl<ProductTypeMapper, ProductType> implements ProductTypeService {
    @Resource
    private TypeProductService typeProductService;
    @Override
    @CacheEvict(value = CacheConstants.PRODUCT_TYPE_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #createMediaReq.marketSiteId")
    public boolean addProductType(ProductTypeReq.AddProductTypeReq addProductTypeReq) {
        ProductType productType = addProductTypeReq.buildProductType();
        if (this.save(productType)) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.PRODUCT_TYPE, AdminProc.ProcType.ADD, productType.getId(),null,productType);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = CacheConstants.PRODUCT_TYPE_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #delProductTypeReq.marketSiteId")
    public boolean delProductType(ProductTypeReq.DelProductTypeReq delProductTypeReq) {
        if (this.removeById(delProductTypeReq.getProductTypeId())) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.PRODUCT_TYPE, AdminProc.ProcType.DELETE, delProductTypeReq.getProductTypeId(),null,null);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = CacheConstants.PRODUCT_TYPE_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateProductTypeReq.marketSiteId")
    public boolean updateProductType(String part, ProductTypeReq.UpdateProductTypeReq updateProductTypeReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part)).lambda().eq(ProductType::getId,updateProductTypeReq.getId());
        ProductType media = this.getOne(queryWrapper);
        updateProductTypeReq.buildUpdateParam(part,media,requestBackendUser);
        if (this.update(updateProductTypeReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateProductTypeReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    public IPage<ProductType> getMediaPage(Page<ProductType> page, ProductTypeReq.SearchProductTypeReq searchProductTypeReq) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ProductType::getMarketSiteId,searchProductTypeReq.getMarketSiteId())
                .eq(searchProductTypeReq.getTypeCode()  != null, ProductType::getTypeCode, searchProductTypeReq.getTypeCode())
                .eq(searchProductTypeReq.getTypeName() != null, ProductType::getTypeName,searchProductTypeReq.getTypeName())
                .eq(searchProductTypeReq.getType() != null, ProductType::getType, searchProductTypeReq.getType())
                .eq(searchProductTypeReq.getShowType() != null, ProductType::getShowType, searchProductTypeReq.getShowType())
                .eq(searchProductTypeReq.getIsShowInTop() != null, ProductType::getIsShowInTop, searchProductTypeReq.getIsShowInTop())
                .eq(searchProductTypeReq.getShowStatus() != null, ProductType::getShowStatus, searchProductTypeReq.getShowStatus());
        return this.page(page, queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.PRODUCT_TYPE_LIST,key = "#tenantId + ':' + #marketSiteId")
    public List<ProductType> getProductTypeList(Integer tenantId, Integer marketSiteId) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProductType::getMarketSiteId,marketSiteId).eq(ProductType::getShowStatus,1).orderByDesc(ProductType::getSort);
        List<ProductType> productTypeList = this.list(queryWrapper);
        if (!ObjectUtils.isEmpty(productTypeList)) {
            for (ProductType productType : productTypeList) {
                if (productType.getType() == ProductType.Type.HISTORY || productType.getType() == ProductType.Type.PLATFORM) continue;
                QueryWrapper<TypeProduct> typeProductQueryWrapper = new QueryWrapper<>();
                typeProductQueryWrapper.select("product_id").lambda().eq(TypeProduct::getTypeId,productType.getId()).orderByDesc(TypeProduct::getSort);
                List<TypeProduct>  typeProductList = typeProductService.list(typeProductQueryWrapper);
                if (!ObjectUtils.isEmpty(typeProductList)) {
                    productType.setTypeProductIdList(typeProductList.stream().map(TypeProduct::getProductId).toList());
                }
            }
        }
        return this.list(queryWrapper);
    }
}
