package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.FundApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

@Mapper
public interface FundApplyMapper extends BaseMapper<FundApply> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM fund_apply WHERE status = 2")
    BigDecimal sumPaidAmount();

    @Select("SELECT COUNT(*) FROM fund_apply WHERE status = 0")
    Integer countPendingReviews();
}