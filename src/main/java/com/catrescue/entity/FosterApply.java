package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("foster_apply")
public class FosterApply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer catId;
    private LocalDate startTime;
    private LocalDate endTime;
    private String reason;
    private String environmentDesc;
    private Integer status;  // 0待审核 1通过 2拒绝 3进行中 4已完成
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}