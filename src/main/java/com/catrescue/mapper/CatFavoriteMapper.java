package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.CatFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatFavoriteMapper extends BaseMapper<CatFavorite> {
}