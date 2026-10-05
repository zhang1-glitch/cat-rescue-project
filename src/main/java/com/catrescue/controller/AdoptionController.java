package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.AdoptionApply;
import com.catrescue.entity.User;           // ← 添加这行
import com.catrescue.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping("/apply/{catId}")
    public String applyPage(@PathVariable Integer catId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getIsAdopter() != 1) {
            return "redirect:/user/profile?needAuth=true";
        }
        model.addAttribute("catId", catId);
        return "adoption/apply";
    }

    @PostMapping("/apply")
    @ResponseBody
    public Result<String> submitApplication(AdoptionApply apply, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        System.out.println("收到领养申请:");
        System.out.println("  - 申请人ID: " + user.getId());
        System.out.println("  - 申请人昵称: " + user.getNickname());
        System.out.println("  - 猫咪ID: " + apply.getCatId());
        System.out.println("  - 申请类型: " + apply.getApplyType());
        System.out.println("  - 居住类型: " + apply.getHousingType());
        System.out.println("  - 养宠经验: " + apply.getHasExperience());
        System.out.println("  - 领养动机: " + apply.getReason());

        apply.setApplicantId(user.getId());
        boolean success = adoptionService.submitApplication(apply);

        if (success) {
            System.out.println("领养申请提交成功");
            return Result.success("领养申请已提交，请等待审核");
        }
        System.out.println("领养申请提交失败");
        return Result.error("提交失败");
    }

    @GetMapping("/my-applications")
    public String myApplications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<AdoptionApply> applications = adoptionService.getUserApplications(user.getId());
        System.out.println("用户 " + user.getNickname() + " 的领养申请数量: " + applications.size());
        model.addAttribute("applications", applications);
        return "adoption/my-applications";
    }

    @GetMapping("/progress/{id}")
    public String progress(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("用户未登录，跳转到登录页");
            return "redirect:/login";
        }

        System.out.println("查看进度，用户ID：" + user.getId() + "，申请ID：" + id);

        AdoptionApply apply = adoptionService.getApplicationDetail(id);
        if (apply == null) {
            System.out.println("申请不存在，ID：" + id);
            return "redirect:/adoption/my-applications";
        }

        if (!apply.getApplicantId().equals(user.getId())) {
            System.out.println("无权查看他人的申请");
            return "redirect:/adoption/my-applications";
        }

        model.addAttribute("apply", apply);
        return "adoption/progress";
    }
}