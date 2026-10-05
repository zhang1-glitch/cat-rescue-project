package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("adoption_apply")
public class AdoptionApply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer applicantId;
    private Integer catId;
    private Integer applyType;  // 0直接领养 1寄养领养
    private String housingType;
    private Integer hasExperience;
    private String experienceDesc;
    private String familyMembers;
    private String reason;
    private Integer status;  // 0待初审 1初审通过 2待回访 3已通过 4已拒绝 5已完成
    private String rejectReason;
    private String interviewRecord;
    private Integer protocolSigned;
    private LocalDateTime signTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String statusText;
    @TableField(exist = false)
    private String applicantName;
    @TableField(exist = false)
    private String catName;
    @TableField(exist = false)
    private String catNo;
}