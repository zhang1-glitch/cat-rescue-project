package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    @Select("SELECT f.*, u.nickname as user_name FROM feedback f LEFT JOIN user u ON f.user_id = u.id WHERE f.status = #{status} ORDER BY f.create_time DESC")
    List<Feedback> findByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM feedback WHERE status = 0")
    Integer countPendingReplies();
}