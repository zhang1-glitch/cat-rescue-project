package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String content;
    private Integer type;  // 0咨询 1建议 2投诉
    private Integer status;  // 0待回复 1已回复
    private String replyContent;
    private LocalDateTime replyTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String userName;
}