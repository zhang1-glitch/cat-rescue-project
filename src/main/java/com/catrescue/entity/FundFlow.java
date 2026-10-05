package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fund_flow")
public class FundFlow {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String flowNo;
    private Integer type;  // 0收入 1支出
    private BigDecimal amount;
    private String source;
    private Integer relatedId;
    private BigDecimal balance;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}