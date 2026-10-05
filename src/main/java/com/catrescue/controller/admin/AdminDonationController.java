package com.catrescue.controller.admin;

import com.catrescue.common.Result;
import com.catrescue.entity.FundApply;
import com.catrescue.entity.User;
import com.catrescue.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminDonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping("/donations")
    public String donations(Model model) {
        model.addAttribute("donations", donationService.getRecentDonations());
        return "admin/donations";
    }

    @GetMapping("/funds")
    public String funds(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // 统计数据
        model.addAttribute("stats", donationService.getFundStats());

        // 收入记录
        model.addAttribute("donations", donationService.getSuccessDonations());

        // 支出记录（已通过的）
        model.addAttribute("expenses", donationService.getApprovedExpenses());
        model.addAttribute("expenseCount", donationService.getApprovedExpenses().size());

        // 资金流水
        model.addAttribute("fundFlows", donationService.getFundFlows(1, 100));

        return "admin/funds";
    }

    @PostMapping("/fund/add")
    @ResponseBody
    public Result<String> addExpense(@RequestParam String expenseType,
                                     @RequestParam BigDecimal amount,
                                     @RequestParam String purpose,
                                     @RequestParam(required = false) String voucherUrl,
                                     HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("请输入有效的金额");
        }

        if (purpose == null || purpose.trim().isEmpty()) {
            return Result.error("请填写用途说明");
        }

        // 创建支出记录（直接设置为已通过状态）
        FundApply apply = new FundApply();
        apply.setApplicantId(user.getId());
        apply.setAmount(amount);
        apply.setPurpose(expenseType + " - " + purpose);
        apply.setVoucherUrl(voucherUrl);
        apply.setStatus(2);  // 直接设置为已通过/已公示
        apply.setPaymentTime(LocalDateTime.now());
        apply.setCreateTime(LocalDateTime.now());

        boolean success = donationService.addExpenseDirectly(apply);

        if (success) {
            System.out.println("支出记录已添加，金额：" + amount + "，用途：" + purpose + "，操作人：" + user.getNickname());
            return Result.success("支出记录已添加并公示");
        }
        return Result.error("添加失败");
    }

    @PostMapping("/fund/delete/{id}")
    @ResponseBody
    public Result<String> deleteExpense(@PathVariable Integer id) {
        boolean success = donationService.deleteExpense(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/fund/review/{id}")
    @ResponseBody
    public Result<String> reviewFund(@PathVariable Integer id,
                                     @RequestParam Boolean pass,
                                     @RequestParam(required = false) String opinion) {
        boolean success = donationService.reviewFundApply(id, pass, opinion);
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }

    @PostMapping("/fund/disburse/{id}")
    @ResponseBody
    public Result<String> disburseFund(@PathVariable Integer id) {
        boolean success = donationService.disburseFunds(id);
        return success ? Result.success("资金已拨付") : Result.error("拨付失败");
    }
}