package com.catrescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catrescue.entity.Donation;
import com.catrescue.entity.FundApply;
import com.catrescue.entity.FundFlow;
import com.catrescue.mapper.DonationMapper;
import com.catrescue.mapper.FundApplyMapper;
import com.catrescue.mapper.FundFlowMapper;
import com.catrescue.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationMapper donationMapper;

    @Autowired
    private FundApplyMapper fundApplyMapper;

    @Autowired
    private FundFlowMapper fundFlowMapper;

    @Override
    @Transactional
    public String createDonation(Donation donation) {
        try {
            String orderNo = "D" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            donation.setOrderNo(orderNo);
            donation.setStatus(0);
            donation.setCreateTime(LocalDateTime.now());

            // 设置默认支付方式（如果没有设置）
            if (donation.getPaymentMethod() == null) {
                donation.setPaymentMethod("微信支付");
            }

            System.out.println("准备插入捐款记录：");
            System.out.println("  - donorId: " + donation.getDonorId());
            System.out.println("  - amount: " + donation.getAmount());
            System.out.println("  - orderNo: " + donation.getOrderNo());
            System.out.println("  - targetType: " + donation.getTargetType());
            System.out.println("  - targetId: " + donation.getTargetId());
            System.out.println("  - type: " + donation.getType());
            System.out.println("  - isAnonymous: " + donation.getIsAnonymous());
            System.out.println("  - paymentMethod: " + donation.getPaymentMethod());

            int result = donationMapper.insert(donation);
            System.out.println("插入结果: " + result);

            if (result > 0) {
                System.out.println("捐款记录创建成功，订单号：" + orderNo);
                return orderNo;
            }
            System.out.println("捐款记录创建失败，插入返回0");
            return null;
        } catch (Exception e) {
            System.err.println("插入捐款记录异常：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public boolean processPaymentCallback(String orderNo, boolean success) {
        try {
            LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Donation::getOrderNo, orderNo);
            Donation donation = donationMapper.selectOne(wrapper);

            if (donation == null) {
                System.err.println("未找到订单：" + orderNo);
                return false;
            }

            if (success) {
                donation.setStatus(1);
                System.out.println("订单支付成功，订单号：" + orderNo);

                // 生成资金流水
                FundFlow flow = new FundFlow();
                flow.setFlowNo("F" + System.currentTimeMillis());
                flow.setType(0);
                flow.setAmount(donation.getAmount());
                flow.setSource("用户捐款");
                flow.setRelatedId(donation.getId());
                flow.setCreateTime(LocalDateTime.now());
                fundFlowMapper.insert(flow);
                System.out.println("资金流水创建成功");
            } else {
                donation.setStatus(2);
            }

            int result = donationMapper.updateById(donation);
            return result > 0;
        } catch (Exception e) {
            System.err.println("处理支付回调异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Donation> getUserDonations(Integer userId) {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getDonorId, userId)
                .orderByDesc(Donation::getCreateTime);
        return donationMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getDonationStats() {
        Map<String, Object> stats = new HashMap<>();
        BigDecimal totalAmount = donationMapper.sumSuccessDonations();
        stats.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        stats.put("todayAmount", donationMapper.sumTodayDonations());
        stats.put("totalCount", donationMapper.countSuccessDonations());
        stats.put("pendingFundApplies", fundApplyMapper.countPendingReviews());
        return stats;
    }

    @Override
    @Transactional
    public boolean applyFundUsage(FundApply apply) {
        apply.setStatus(0);
        apply.setCreateTime(LocalDateTime.now());
        return fundApplyMapper.insert(apply) > 0;
    }

    @Override
    @Transactional
    public boolean reviewFundApply(Integer applyId, Boolean pass, String opinion) {
        FundApply apply = fundApplyMapper.selectById(applyId);
        if (apply == null) return false;

        if (pass) {
            apply.setStatus(1);
        } else {
            apply.setStatus(3);
            apply.setReviewOpinion(opinion);
        }
        return fundApplyMapper.updateById(apply) > 0;
    }

    @Override
    @Transactional
    public boolean disburseFunds(Integer applyId) {
        FundApply apply = fundApplyMapper.selectById(applyId);
        if (apply == null) return false;

        apply.setStatus(2);
        apply.setPaymentTime(LocalDateTime.now());

        // 生成支出流水
        FundFlow flow = new FundFlow();
        flow.setFlowNo("F" + System.currentTimeMillis());
        flow.setType(1);
        flow.setAmount(apply.getAmount());
        flow.setSource(apply.getPurpose());
        flow.setRelatedId(apply.getId());
        flow.setCreateTime(LocalDateTime.now());
        fundFlowMapper.insert(flow);

        return fundApplyMapper.updateById(apply) > 0;
    }

    @Override
    public List<FundApply> getPendingFundApplies() {
        LambdaQueryWrapper<FundApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundApply::getStatus, 0)
                .orderByAsc(FundApply::getCreateTime);
        return fundApplyMapper.selectList(wrapper);
    }

    @Override
    public List<FundFlow> getFundFlows(Integer page, Integer size) {
        LambdaQueryWrapper<FundFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FundFlow::getCreateTime);
        return fundFlowMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getFundStats() {
        Map<String, Object> stats = new HashMap<>();
        BigDecimal totalIncome = donationMapper.sumSuccessDonations();
        BigDecimal totalExpense = fundApplyMapper.sumPaidAmount();

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("balance", totalIncome.subtract(totalExpense));
        return stats;
    }

    @Override
    public boolean generateFundReport(String month) {
        // 生成资金公示报告
        // 实际项目中可以生成PDF或者保存为HTML
        return true;
    }

    @Override
    @Transactional
    public boolean addExpenseDirectly(FundApply apply) {
        try {
            // 保存支出记录
            int result = fundApplyMapper.insert(apply);
            if (result > 0) {
                // 生成资金流水（支出）
                FundFlow flow = new FundFlow();
                flow.setFlowNo("F" + System.currentTimeMillis());
                flow.setType(1);  // 支出
                flow.setAmount(apply.getAmount());
                flow.setSource(apply.getPurpose());
                flow.setRelatedId(apply.getId());
                flow.setCreateTime(LocalDateTime.now());
                fundFlowMapper.insert(flow);

                System.out.println("支出记录添加成功，金额：" + apply.getAmount());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("添加支出记录失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteExpense(Integer id) {
        // 删除支出记录
        int result = fundApplyMapper.deleteById(id);
        if (result > 0) {
            // 删除对应的资金流水
            LambdaQueryWrapper<FundFlow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FundFlow::getRelatedId, id).eq(FundFlow::getType, 1);
            fundFlowMapper.delete(wrapper);
            return true;
        }
        return false;
    }
    // ========== 新增方法实现 ==========

    @Override
    public List<Donation> getRecentDonations() {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getStatus, 1)
                .orderByDesc(Donation::getCreateTime)
                .last("limit 10");
        return donationMapper.selectList(wrapper);
    }
    @Override
    public BigDecimal getTotalIncome() {
        return donationMapper.sumSuccessDonations();
    }

    @Override
    public BigDecimal getTotalExpense() {
        return fundApplyMapper.sumPaidAmount();
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal income = getTotalIncome();
        BigDecimal expense = getTotalExpense();
        if (income == null) income = BigDecimal.ZERO;
        if (expense == null) expense = BigDecimal.ZERO;
        return income.subtract(expense);
    }

    @Override
    public List<Donation> getSuccessDonations() {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getStatus, 1)
                .orderByDesc(Donation::getCreateTime)
                .last("limit 50");
        return donationMapper.selectList(wrapper);
    }

    @Override
    public List<FundApply> getApprovedExpenses() {
        LambdaQueryWrapper<FundApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundApply::getStatus, 2)  // 2表示已拨付/已公示
                .orderByDesc(FundApply::getPaymentTime);
        return fundApplyMapper.selectList(wrapper);
    }
}