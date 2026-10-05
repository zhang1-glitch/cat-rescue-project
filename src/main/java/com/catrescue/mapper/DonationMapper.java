package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.Donation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DonationMapper extends BaseMapper<Donation> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM donation WHERE status = 1")
    BigDecimal sumSuccessDonations();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM donation WHERE status = 1 AND DATE(create_time) = CURDATE()")
    BigDecimal sumTodayDonations();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM donation WHERE status = 1 AND DATE_FORMAT(create_time, '%Y-%m') = #{month}")
    BigDecimal sumMonthDonations(@Param("month") String month);

    @Select("SELECT COUNT(*) FROM donation WHERE status = 1")
    Integer countSuccessDonations();

    @Select("SELECT DATE(create_time) as date, COALESCE(SUM(amount), 0) as total FROM donation " +
            "WHERE status = 1 AND DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getLast7DaysDonations();
}