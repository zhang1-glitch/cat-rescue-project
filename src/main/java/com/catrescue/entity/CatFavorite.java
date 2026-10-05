package com.catrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cat_favorite")
public class CatFavorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer catId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}