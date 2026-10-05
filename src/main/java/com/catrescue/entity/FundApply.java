package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fund_apply")
public class FundApply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer applicantId;
    private BigDecimal amount;
    private String purpose;
    private String voucherUrl;
    private Integer status;  // 0待审核 1审核通过 2已拨付 3驳回
    private String reviewOpinion;
    private LocalDateTime paymentTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 添加支出类型字段（非数据库字段，用于前端显示）
    @TableField(exist = false)
    private String expenseType;

    @TableField(exist = false)
    private String applicantName;
}