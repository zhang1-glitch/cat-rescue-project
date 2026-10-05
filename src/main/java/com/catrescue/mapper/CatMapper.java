package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.Cat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CatMapper extends BaseMapper<Cat> {

    @Select("SELECT * FROM cat WHERE is_deleted = 0 AND status = #{status} ORDER BY create_time DESC")
    List<Cat> findByStatus(@Param("status") Integer status);

    @Update("UPDATE cat SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM cat WHERE is_deleted = 0")
    Integer countTotalCats();

    @Select("SELECT COUNT(*) FROM cat WHERE is_deleted = 0 AND status = #{status}")
    Integer countByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM cat WHERE is_deleted = 0 AND (name LIKE CONCAT('%', #{keyword}, '%') OR cat_no LIKE CONCAT('%', #{keyword}, '%'))")
    List<Cat> search(@Param("keyword") String keyword);
    // 在 CatMapper.java 中添加
    @Select("SELECT * FROM cat WHERE is_deleted = 0 " +
            "AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%') OR cat_no LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND (#{gender} IS NULL OR gender = #{gender}) " +
            "ORDER BY create_time DESC")
    List<Cat> searchCats(@Param("keyword") String keyword,
                         @Param("status") Integer status,
                         @Param("gender") Integer gender);
}