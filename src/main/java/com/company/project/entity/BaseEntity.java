// BaseEntity.java
package com.company.project.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * BaseEntity
 *
 * @author wenbin
 * @version V1.0
 * @date 2020年3月18日
 */
@Data
@JsonIgnoreProperties(value = {"page", "limit","createTimeBegin","createTimeEnd", "getQueryPage"})
public class BaseEntity {
    // 使用 @ExcelIgnore 明确排除这些字段，它们绝不应该出现在 Excel 中
    @ExcelIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    private Integer page = 1;

    @ExcelIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    private Integer limit = 10;
    
    @ExcelIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    private Date createTimeBegin ;
    
    @ExcelIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    private Date createTimeEnd;
    
    /**
     * 数据权限：用户id
     */
    @ExcelIgnore
    @TableField(exist = false)
    private List<String> createIds;

    /**
     * page条件
     *
     * @param <T>
     * @return
     */
    @JSONField(serialize = false)
    public <T> Page getQueryPage() {
        return new Page<T>(page, limit);
    }

    // 对公共字段 createTime 添加 Excel 注解
    // 注意：这个字段在您的 DataSaleEntity 中也有，并且没有在基类中定义。
    // 如果希望所有子类都有这个字段并导出，应该将其提升到基类中。
    // 根据您的 DataSaleEntity，它已经有自己的 createTime 字段，所以这里暂时注释掉。
    // 如果您决定统一，可以取消注释并删除子类中的同名段。
    /*
    @ExcelProperty(value = "创建时间", index = 7) // 假设的索引，需要根据实际调整
    @TableField("create_time")
    private Date createTime;
    */
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}