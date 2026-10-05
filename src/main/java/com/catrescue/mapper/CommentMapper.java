package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.nickname as user_name, u.avatar as user_avatar FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id WHERE c.news_id = #{newsId} ORDER BY c.create_time DESC")
    List<Comment> findByNewsId(Integer newsId);
}