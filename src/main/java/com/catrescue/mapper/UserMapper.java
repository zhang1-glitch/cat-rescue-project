package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE phone = #{account} OR email = #{account}")
    User findByAccount(String account);

    @Select("SELECT COUNT(*) FROM user WHERE is_adopter = 1")
    Integer countAdopters();

    @Select("SELECT COUNT(*) FROM user WHERE is_donor = 1")
    Integer countDonors();

    @Select("SELECT COUNT(*) FROM user")
    Integer countTotalUsers();

    @Select("SELECT COUNT(*) FROM user WHERE DATE(create_time) = CURDATE()")
    Integer countTodayNewUsers();

    // ========== 添加以下方法 ==========

    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM user " +
            "WHERE DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getLast7DaysUserCount();
}