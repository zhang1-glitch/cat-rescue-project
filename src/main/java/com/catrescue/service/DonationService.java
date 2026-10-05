package com.catrescue.service;

import com.catrescue.entity.Donation;
import com.catrescue.entity.FundApply;
import com.catrescue.entity.FundFlow;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DonationService {

    String createDonation(Donation donation);

    boolean processPaymentCallback(String orderNo, boolean success);

    List<Donation> getUserDonations(Integer userId);

    Map<String, Object> getDonationStats();

    boolean applyFundUsage(FundApply apply);

    boolean reviewFundApply(Integer applyId, Boolean pass, String opinion);

    boolean disburseFunds(Integer applyId);

    // 获取总收入
    BigDecimal getTotalIncome();

    // 获取总支出
    BigDecimal getTotalExpense();

    // 获取余额
    BigDecimal getBalance();

    // 获取成功的捐款记录
    List<Donation> getSuccessDonations();

    // 获取已通过的支出记录
    List<FundApply> getApprovedExpenses();

    List<FundApply> getPendingFundApplies();

    List<FundFlow> getFundFlows(Integer page, Integer size);

    Map<String, Object> getFundStats();

    boolean generateFundReport(String month);

    // ========== 新增方法 ==========

    boolean addExpenseDirectly(FundApply apply);
    // 删除支出记录
    boolean deleteExpense(Integer id);
    /**
     * 获取最近的捐款记录
     */
    List<Donation> getRecentDonations();
}