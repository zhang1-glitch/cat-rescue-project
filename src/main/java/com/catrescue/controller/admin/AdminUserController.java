package com.catrescue.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catrescue.common.Result;
import com.catrescue.entity.User;
import com.catrescue.entity.UserAuthApply;
import com.catrescue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        Model model) {
        Page<User> pageResult = userService.page(new Page<>(page, size));
        model.addAttribute("users", pageResult.getRecords());
        model.addAttribute("pageNum", page);
        model.addAttribute("totalPages", pageResult.getPages());
        return "admin/users";
    }

    // ========== 添加这个缺失的方法 ==========
    @GetMapping("/auth-applies")
    public String authApplies(Model model) {
        LambdaQueryWrapper<UserAuthApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthApply::getStatus, 0).orderByAsc(UserAuthApply::getCreateTime);
        List<UserAuthApply> applies = userService.getAuthApplies();

        // 为每个申请设置用户名
        for (UserAuthApply apply : applies) {
            User user = userService.getById(apply.getUserId());
            if (user != null) {
                apply.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            } else {
                apply.setUserName("未知用户");
            }
        }

        model.addAttribute("applies", applies);
        return "admin/auth-applies";
    }

    @GetMapping("/auth-apply/{id}")
    @ResponseBody
    public Result<UserAuthApply> getAuthApply(@PathVariable Integer id) {
        UserAuthApply apply = userService.getAuthApplyById(id);
        return Result.success(apply);
    }

    @PostMapping("/user/toggle/{id}")
    @ResponseBody
    public Result<String> toggleUserStatus(@PathVariable Integer id) {
        User user = userService.getById(id);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userService.updateById(user);
        return Result.success("操作成功");
    }

    @PostMapping("/auth/review/{id}")
    @ResponseBody
    public Result<String> reviewAuth(@PathVariable Integer id,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String reason) {
        boolean success = userService.reviewAuthApply(id, status, reason);
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }
}