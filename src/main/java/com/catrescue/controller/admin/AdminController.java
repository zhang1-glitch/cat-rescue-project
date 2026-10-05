package com.catrescue.controller.admin;

import com.catrescue.entity.User;
import com.catrescue.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 检查是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // 检查是否是管理员（用户名为admin或id为100）
        if (!"admin".equals(user.getUsername()) && user.getId() != 100) {
            return "redirect:/";
        }

        model.addAttribute("stats", dashboardService.getDashboardData());
        return "admin/dashboard";
    }
}