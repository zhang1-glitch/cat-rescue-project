package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private String realName;
    private String idCard;
    private String address;
    private Integer isAdopter;  // 0否1是
    private Integer isDonor;    // 0否1是
    private Integer status;     // 1正常 0禁用
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}