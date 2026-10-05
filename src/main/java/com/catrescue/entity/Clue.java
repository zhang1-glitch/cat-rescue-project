package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data
@TableName("clue")
public class Clue {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String location;
    private Integer catCount;
    private String healthDesc;
    private String contact;
    private String supplement;
    private Integer status;  // 0待审核 1已采纳 2已拒绝 3已救助
    private String rejectReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String statusText;
    @TableField(exist = false)
    private String userName;
    // 在 Clue.java 中添加
    @TableField(exist = false)
    private List<ClueImage> images;
}