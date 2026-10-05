package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@TableName("cat")
public class Cat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String catNo;
    private String name;
    private Integer gender;  // 0母 1公
    private Integer age;     // 年龄(月)
    private String breed;
    private String color;
    private String healthStatus;
    private Integer isNeutered;  // 0否 1是
    private Integer isVaccinated; // 0否 1是
    private String rescueLocation;
    private LocalDateTime rescueTime;
    private String rescueStory;
    private Integer status;  // 0待救助 1待领养 2已领养 3寄养中 4治疗中
    private Integer viewCount;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String statusText;
    @TableField(exist = false)
    private String genderText;
    @TableField(exist = false)
    private String ageText;
    @TableField(exist = false)
    private List<CatImage> catImages;

    // 格式化日期方法 - 完整日期时间
    public String getFormattedRescueTimeForInput() {
        if (rescueTime == null) return "";
        return rescueTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    // 格式化日期方法 - 仅日期
    public String getDateCreateTime() {
        if (createTime == null) return "";
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 格式化救助时间
    public String getFormattedRescueTime() {
        if (rescueTime == null) return "";
        return rescueTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 获取状态文本
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待救助";
            case 1: return "待领养";
            case 2: return "已领养";
            case 3: return "寄养中";
            case 4: return "治疗中";
            default: return "未知";
        }
    }

    // 获取性别文本
    public String getGenderText() {
        if (gender == null) return "未知";
        return gender == 1 ? "公猫" : "母猫";
    }

    // 获取年龄文本
    public String getAgeText() {
        if (age == null) return "未知";
        if (age < 12) {
            return age + "个月";
        } else {
            int years = age / 12;
            int months = age % 12;
            if (months == 0) {
                return years + "岁";
            } else {
                return years + "岁" + months + "个月";
            }
        }
    }
}