package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("clue_image")
public class ClueImage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer clueId;
    private String imageUrl;
}