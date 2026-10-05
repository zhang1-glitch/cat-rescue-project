package com.catrescue.service;

import java.util.Map;

public interface DashboardService {

    Map<String, Object> getDashboardData();

    Map<String, Object> getUserTrendData();

    Map<String, Object> getDonationTrendData();
}