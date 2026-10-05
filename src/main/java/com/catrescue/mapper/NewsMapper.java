package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

    @Select("SELECT * FROM news WHERE status = 1 ORDER BY create_time DESC")
    List<News> findPublished();

    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Integer id);

    @Update("UPDATE news SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Integer id);
}