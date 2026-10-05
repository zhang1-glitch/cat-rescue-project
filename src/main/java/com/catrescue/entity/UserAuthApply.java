package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_auth_apply")
public class UserAuthApply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer authType;  // 1领养人 2捐款人
    private Integer status;    // 0待审核 1通过 2拒绝
    private String materials;
    private String reason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(exist = false)
    private String userName;
}