package com.console.core.model.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.IUpdateField;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public abstract class BaseUpdateReq<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 待更新记录主键（若某些实体主键非 Integer/非 id 命名，可改为泛型或留给子类） */
    @NotNull
    private Integer id;

    /* ===== 以下为构建产物，非请求参数 ===== */

    private transient UpdateWrapper<E> updateWrapper;
    private transient AdminProc adminProc;
    private transient String errorDesc;

    /* ===== 子类必须实现 ===== */

    /** 实体主键 getter，用于 where id = ? */
    protected abstract SFunction<E, ?> idFunction();

    /** 审计归属 */
    protected abstract AdminProc.ProcBelong procBelong();

    /** 按 fieldName 解析字段元数据 */
    protected abstract IUpdateField<E> parseField(String fieldName);

    /**
     * 实体级前置校验钩子（默认空实现）。
     * 子类可覆写，例如：冻结用户禁止修改、状态机校验、外部依赖存在性检查等。
     */
    protected void beforeValidate(E entity) {
    }

    /**
     * 审计脱敏钩子（默认原样返回）。
     * 涉及密码类字段的子类应覆写，避免明文进入 AdminProc 审计日志。
     */
    protected Object maskForAudit(IUpdateField<E> field, Object value) {
        return value;
    }

    /* ===== 模板方法，禁止子类覆写 ===== */

    public final void buildUpdateParam(String part, E entity, RequestBackendUser backendUser) {
        if (entity == null) {
            errorDesc = "待修改数据不存在，请确认后重试";
            throw new BusinessException(500,"待修改数据不存在，请确认后重试");
//            return;
        }

        beforeValidate(entity);

        IUpdateField<E> field = parseField(part);
        Object newValue = FieldUtils.getFieldValue(this, field.getFieldName());
        Object oldValue = field.getFunction().apply(entity);

        if (Objects.equals(newValue, oldValue)) {
//            errorDesc = "修改值和原值一样，无需修改，请确认后重试";
//            return;   // ← 原代码漏掉的 return，务必保留
            throw new BusinessException(500,"修改值和原值一样，无需修改，请确认后重试");
        }

        updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(idFunction(), getId());
        WrapperUtils.setPropertyValue(updateWrapper, field.getFunction(), newValue);

        adminProc = new AdminProc(
                backendUser,
                procBelong(),
                AdminProc.ProcType.UPDATE,
                getId(),
                maskForAudit(field, oldValue),
                maskForAudit(field, newValue)
        );
    }
}
