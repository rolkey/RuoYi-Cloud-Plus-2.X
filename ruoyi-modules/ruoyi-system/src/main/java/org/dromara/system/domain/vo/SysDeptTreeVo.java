package org.dromara.system.domain.vo;

import cn.hutool.core.lang.tree.Tree;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门树视图对象
 *
 * @author Rolkey
 * @date 2026-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptTreeVo extends Tree<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否禁用
     */
    private Boolean disabled;

    /**
     * 标准部门ID
     */
    private String standDeptId;

    public SysDeptTreeVo setId(Long id) {
        super.setId(id);
        return this;
    }

    public SysDeptTreeVo setParentId(Long parentId) {
        super.setParentId(parentId);
        return this;
    }
}
