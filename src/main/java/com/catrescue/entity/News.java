package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private String coverImage;
    private Integer type;  // 0救助动态 1科普文章 2活动通知
    private Integer viewCount;
    private Integer likeCount;
    private Integer status;  // 0草稿 1发布
    private Integer createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String typeText;

    // 格式化日期方法 - 完整日期时间
    public String getFormattedCreateTime() {
        if (createTime == null) return "";
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 格式化日期方法 - 仅日期
    public String getDateCreateTime() {
        if (createTime == null) return "";
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 获取类型文本
    public String getTypeText() {
        if (type == null) return "未知";
        switch (type) {
            case 0: return "救助动态";
            case 1: return "科普文章";
            case 2: return "活动通知";
            default: return "未知";
        }
    }
}