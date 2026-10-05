package com.catrescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.catrescue.entity.User;
import com.catrescue.entity.UserAuthApply;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    User login(String account, String password);

    boolean register(User user, String verifyCode);

    boolean applyForAdopter(Integer userId, String materials);

    boolean applyForDonor(Integer userId);

    boolean reviewAuthApply(Integer applyId, Integer status, String reason);

    UserAuthApply getPendingAuthApply(Integer userId, Integer authType);

    Map<String, Object> getUserStats();

    List<UserAuthApply> getAuthApplies();

    // 在 UserService.java 中添加
    List<UserAuthApply> getAuthAppliesByUserId(Integer userId);
    UserAuthApply getAuthApplyById(Integer id);

    // ========== 添加这个方法 ==========
    User findByPhone(String phone);
}