package com.catrescue.service.impl;

import com.catrescue.mapper.*;
import com.catrescue.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CatMapper catMapper;

    @Autowired
    private DonationMapper donationMapper;

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private NewsMapper newsMapper;  // ← 添加这行，注入 NewsMapper

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 用户统计
        data.put("totalUsers", userMapper.countTotalUsers());
        data.put("adopters", userMapper.countAdopters());
        data.put("donors", userMapper.countDonors());
        data.put("todayNewUsers", userMapper.countTodayNewUsers());

        // 猫咪统计
        data.put("totalCats", catMapper.countTotalCats());
        data.put("availableCats", catMapper.countByStatus(1));
        data.put("adoptedCats", catMapper.countByStatus(2));

        // 捐款统计
        BigDecimal totalAmount = donationMapper.sumSuccessDonations();
        data.put("totalDonation", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        data.put("todayDonation", donationMapper.sumTodayDonations());

        // 资讯统计 - 使用注入的 newsMapper
        data.put("totalNews", newsMapper.selectCount(null));

        // 待处理数量
        data.put("pendingClues", clueMapper.countPendingClues());
        data.put("pendingAdoptions", adoptionApplyMapper.countPendingReviews());
        data.put("pendingFeedbacks", feedbackMapper.countPendingReplies());

        return data;
    }

    @Override
    public Map<String, Object> getUserTrendData() {
        Map<String, Object> data = new HashMap<>();
        data.put("trend", userMapper.getLast7DaysUserCount());
        return data;
    }

    @Override
    public Map<String, Object> getDonationTrendData() {
        Map<String, Object> data = new HashMap<>();
        data.put("trend", donationMapper.getLast7DaysDonations());
        return data;
    }
}