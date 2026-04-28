// DataSaleEntity.java
package com.company.project.entity;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("data_sale")
public class DataSaleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    @ExcelIgnore // 通常主键ID不需要导入导出
    private String id;

    @TableField("agent")
    @ExcelProperty(value = "经销商", index = 0) // index 从 0 开始，必须唯一
    private String agent;

    @TableField("buyer")
    @ExcelProperty(value = "用户姓名", index = 1)
    private String buyer;

    @TableField("phone")
    @ExcelProperty(value = "电话", index = 2)
    private String phone;

    @TableField("machine_name")
    @ExcelProperty(value = "机器名称", index = 3)
    private String machineName;

    @TableField("machine_model")
    @ExcelProperty(value = "机器型号", index = 4)
    private String machineModel;

    @TableField("brand_number")
    @ExcelProperty(value = "名牌编号", index = 5)
    private String brandNumber;

    @TableField("machine_lines")
    @ExcelProperty(value = "行数", index = 6)
    private Integer machineLines;

    @TableField("address_info")
    @ExcelProperty(value = "详细地址", index = 7) // 注意：这里index=7，和上面的createTime冲突了！需要调整顺序。
    private String addressInfo;
    
    @TableField("product_code")
    @ExcelProperty(value = "监控器号", index = 8)
    private String productCode;
    
    @TableField("sale_date")
    @ExcelProperty(value = "销售时间", index = 9)
    private String saleDate;
    @TableField("sale_desc")
    @ExcelProperty(value = "备注", index = 10)
    private String saleDesc;

    // 如果 createTime 已提升到基类，则此处删除
    // @TableField("create_time")
    // @ExcelProperty(value = "创建时间", index = 7) // 注意：这个index要和基类以及其他字段协调！
    // private Date createTime;

    @TableField("agent_id")
    @ExcelIgnore // 内部ID，不需要导出
    private String agentId;

    @TableField("province_id")
    @ExcelIgnore // 内部ID，不需要导出
    private String provinceId;

    @TableField("city_id")
    @ExcelIgnore // 内部ID，不需要导出
    private String cityId;

    @TableField("country_id")
    @ExcelIgnore // 内部ID，不需要导出
    private String countryId;

  

    @TableField("machine_model_id")
    @ExcelIgnore
    private String machineModelId;

    @TableField("sale_status")
    @ExcelIgnore // 状态码可能不需要，或者需要转换成状态名称导出，这需要更复杂的处理（转换器）
    private String saleStatus;

    // 非数据库字段，用于查询和显示，根据需要决定是否导出
    @TableField(exist = false)
    @ExcelIgnore // 通常查询条件不导出
    private String provinceName;

    @TableField(exist = false)
    @ExcelIgnore
    private String cityName;

    @TableField(exist = false)
    @ExcelIgnore
    private String countryName;
}
