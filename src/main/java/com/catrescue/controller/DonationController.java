package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.Donation;
import com.catrescue.entity.FundApply;
import com.catrescue.entity.User;
import com.catrescue.service.UserService;
import com.catrescue.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @Autowired
    private UserService userService;

    @GetMapping("/donate")
    public String donatePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "donation/donate";
    }

    @PostMapping("/create")
    @ResponseBody
    public Result<String> createDonation(@RequestParam Integer targetType,
                                         @RequestParam(required = false) String targetId,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam Integer type,
                                         @RequestParam(required = false) String message,
                                         @RequestParam Integer isAnonymous,
                                         HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        // 验证金额
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("请输入有效的捐款金额");
        }

        try {
            // 创建捐款记录
            Donation donation = new Donation();
            donation.setDonorId(user.getId());
            donation.setTargetType(targetType);
            // 处理 targetId 为空的情况
            if (targetId != null && !targetId.isEmpty()) {
                donation.setTargetId(Integer.parseInt(targetId));
            }
            donation.setAmount(amount);
            donation.setType(type);
            donation.setMessage(message);
            donation.setIsAnonymous(isAnonymous);
            donation.setStatus(0); // 待支付状态

            String orderNo = donationService.createDonation(donation);

            // 检查订单是否创建成功
            if (orderNo == null || orderNo.isEmpty()) {
                System.err.println("创建捐款订单失败");
                return Result.error("创建捐款订单失败，请重试");
            }

            System.out.println("订单创建成功，订单号：" + orderNo);

            // 模拟支付成功
            boolean paid = donationService.processPaymentCallback(orderNo, true);

            if (!paid) {
                System.err.println("支付处理失败，订单号：" + orderNo);
                return Result.error("支付处理失败，请重试");
            }

            System.out.println("支付成功，订单号：" + orderNo);

            // 如果是捐款人，更新用户身份
            if (user.getIsDonor() == 0) {
                user.setIsDonor(1);
                userService.updateById(user);
                session.setAttribute("user", user);
                System.out.println("用户已升级为捐款人");
            }

            return Result.success("捐款成功，感谢您的爱心！");

        } catch (Exception e) {
            System.err.println("捐款异常：" + e.getMessage());
            e.printStackTrace();
            return Result.error("捐款失败：" + e.getMessage());
        }
    }

    @GetMapping("/records")
    public String records(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Donation> donations = donationService.getUserDonations(user.getId());
        model.addAttribute("donations", donations);

        // 计算总额
        BigDecimal totalAmount = donations.stream()
                .filter(d -> d.getStatus() == 1)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalAmount", totalAmount);

        return "donation/records";
    }

    @GetMapping("/report")
    public String fundReport(Model model) {
        // 总收入
        BigDecimal totalIncome = donationService.getTotalIncome();
        model.addAttribute("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);

        // 总支出
        BigDecimal totalExpense = donationService.getTotalExpense();
        model.addAttribute("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);

        // 余额
        model.addAttribute("balance", donationService.getBalance());

        // 捐款记录（匿名处理）
        List<Donation> donations = donationService.getSuccessDonations();
        for (Donation donation : donations) {
            if (donation.getIsAnonymous() == 1) {
                donation.setDonorName("爱心人士");
            } else {
                User donor = userService.getById(donation.getDonorId());
                donation.setDonorName(donor != null ? donor.getNickname() : "匿名用户");
            }
        }
        model.addAttribute("donations", donations);

        // 获取已通过的支出记录
        List<FundApply> expenses = donationService.getApprovedExpenses();
        model.addAttribute("expenses", expenses);

        return "donation/report";
    }
}