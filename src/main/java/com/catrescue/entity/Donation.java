package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("donation")
public class Donation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer donorId;
    private String orderNo;
    private BigDecimal amount;
    private Integer type;  // 0单次 1月度
    private Integer targetType;  // 0通用基金 1医疗 2口粮 3定向猫咪
    private Integer targetId;
    private String message;
    private Integer isAnonymous;
    private String paymentMethod;
    private Integer status;  // 0待支付 1支付成功 2支付失败 3已退款
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String donorName;
    @TableField(exist = false)
    private String targetName;
}