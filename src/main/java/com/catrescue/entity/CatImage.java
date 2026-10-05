package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("cat_image")
public class CatImage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer catId;
    private String imageUrl;
    private Integer sortOrder;
}