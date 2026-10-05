package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.AdoptionApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AdoptionApplyMapper extends BaseMapper<AdoptionApply> {

    @Select("SELECT a.*, u.nickname as applicant_name, c.name as cat_name, c.cat_no FROM adoption_apply a " +
            "LEFT JOIN user u ON a.applicant_id = u.id " +
            "LEFT JOIN cat c ON a.cat_id = c.id " +
            "WHERE a.status = #{status} ORDER BY a.create_time DESC")
    List<AdoptionApply> findByStatus(@Param("status") Integer status);

    @Select("SELECT a.*, c.name as cat_name, c.cat_no FROM adoption_apply a " +
            "LEFT JOIN cat c ON a.cat_id = c.id " +
            "WHERE a.applicant_id = #{userId} ORDER BY a.create_time DESC")
    List<AdoptionApply> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM adoption_apply WHERE status = 0")
    Integer countPendingReviews();
}