package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.User;
import com.catrescue.entity.UserAuthApply;
import com.catrescue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static Map<String, String> verifyCodeMap = new HashMap<>();

    @PostMapping("/login")
    @ResponseBody
    public Result<Map<String, Object>> doLogin(@RequestParam String account,
                                               @RequestParam String password,
                                               HttpServletRequest request) {
        User user = userService.login(account, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60);
            System.out.println("登录成功，用户：" + user.getNickname() + "，Session ID：" + session.getId());

            // 准备返回数据，包含跳转路径
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("user", user);

            // 判断是否是管理员（id=100 或 username=admin）
            if (user.getId() == 100 || "admin".equals(user.getUsername())) {
                resultData.put("redirect", "/admin/dashboard");
                System.out.println("管理员登录，跳转到管理后台");
            } else {
                resultData.put("redirect", "/");
                System.out.println("普通用户登录，跳转到首页");
            }

            return Result.success(resultData);
        }
        return Result.error("账号或密码错误");
    }

    @GetMapping("/check")
    @ResponseBody
    public Result<User> checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println("检查登录状态，Session ID：" + session.getId());
        if (user != null) {
            System.out.println("当前登录用户：" + user.getNickname());
            return Result.success(user);
        }
        System.out.println("未登录");
        return Result.error(401, "未登录");
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public Result<String> sendCode(@RequestParam String phone) {
        String code = String.format("%06d", new Random().nextInt(999999));
        verifyCodeMap.put(phone, code);
        System.out.println("验证码：" + code + " 已发送到手机：" + phone);
        return Result.success(code);
    }

    @PostMapping("/register")
    @ResponseBody
    public Result<String> doRegister(@RequestParam String phone,
                                     @RequestParam String nickname,
                                     @RequestParam String password,
                                     @RequestParam String verifyCode) {
        String savedCode = verifyCodeMap.get(phone);
        if (savedCode == null || !savedCode.equals(verifyCode)) {
            return Result.error("验证码错误");
        }

        User existingUser = userService.findByPhone(phone);
        if (existingUser != null) {
            return Result.error("手机号已注册");
        }

        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setUsername(phone);

        boolean success = userService.register(user, verifyCode);
        if (success) {
            verifyCodeMap.remove(phone);
            return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/update")
    @ResponseBody
    public Result<String> updateProfile(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }
        user.setId(currentUser.getId());
        boolean success = userService.updateById(user);
        if (success) {
            User updatedUser = userService.getById(currentUser.getId());
            session.setAttribute("user", updatedUser);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @PostMapping("/apply/adopter")
    @ResponseBody
    public Result<String> applyForAdopter(@RequestParam Integer userId,
                                          @RequestParam String materials,
                                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getId().equals(userId)) {
            return Result.error("请先登录");
        }
        boolean success = userService.applyForAdopter(userId, materials);
        if (success) {
            return Result.success("申请已提交，请等待审核");
        }
        return Result.error("已有待审核申请");
    }

    @GetMapping("/my-applications")
    public String myApplications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/applications";
    }

    @GetMapping("/applications")
    public String applications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 获取用户的认证申请记录
        List<UserAuthApply> authApplies = userService.getAuthAppliesByUserId(user.getId());
        model.addAttribute("authApplies", authApplies);
        model.addAttribute("user", user);
        return "user/applications";
    }
}