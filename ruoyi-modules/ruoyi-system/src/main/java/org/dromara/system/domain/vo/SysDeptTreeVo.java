package org.dromara.system.domain.vo;

import cn.hutool.core.lang.tree.Tree;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 部门树视图对象
 *
 * @author Rolkey
 * @date 2026-04-21
 */
public class SysDeptTreeVo extends Tree<Long> {

    /**
     * 是否禁用
     */
    private Boolean disabled;

    /**
     * 标准部门ID
     */
    private String standDeptId;

    public Boolean getDisabled() {
        return disabled;
    }

    public String getStandDeptId() {
        return standDeptId;
    }

    public void setDisabled(Boolean disabled) {
        this.put("disabled", disabled);
        this.disabled = disabled;
    }

    public void setStandDeptId(String standDeptId) {
        this.put("standDeptId", standDeptId);
        this.standDeptId = standDeptId;
    }

    @Override
    public String toString() {
        return "SysDeptTreeVo{" +
            "disabled=" + getDisabled() +
            ", standDeptId='" + getStandDeptId() + '\'' +
            ", id=" + getId() +
            ", parentId=" + getParentId() +
            ", name='" + getName() + '\'' +
            ", weight=" + getWeight() +
            ", children=" + getChildren() +
            '}';
    }
}
