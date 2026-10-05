package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.Clue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ClueMapper extends BaseMapper<Clue> {

    @Select("SELECT c.*, u.nickname as user_name FROM clue c LEFT JOIN user u ON c.user_id = u.id WHERE c.status = #{status} ORDER BY c.create_time DESC")
    List<Clue> findByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM clue WHERE DATE(create_time) = CURDATE() AND user_id = #{userId}")
    Integer countTodayByUser(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM clue WHERE status = 0")
    Integer countPendingClues();
}