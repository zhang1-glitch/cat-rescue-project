package com.catrescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catrescue.entity.User;
import com.catrescue.entity.UserAuthApply;
import com.catrescue.mapper.UserMapper;
import com.catrescue.mapper.UserAuthApplyMapper;
import com.catrescue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthApplyMapper authApplyMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String account, String password) {
        User user = userMapper.findByAccount(account);
        if (user != null && user.getStatus() == 1) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setPassword(null);
                return user;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean register(User user, String verifyCode) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setIsAdopter(0);
        user.setIsDonor(0);
        user.setUsername(user.getPhone());  // 设置用户名为手机号
        return this.save(user);
    }

    @Override
    @Transactional
    public boolean applyForAdopter(Integer userId, String materials) {
        UserAuthApply existing = getPendingAuthApply(userId, 1);
        if (existing != null) {
            return false;
        }

        UserAuthApply apply = new UserAuthApply();
        apply.setUserId(userId);
        apply.setAuthType(1);
        apply.setStatus(0);
        apply.setMaterials(materials);
        apply.setCreateTime(LocalDateTime.now());
        return authApplyMapper.insert(apply) > 0;
    }

    @Override
    @Transactional
    public boolean applyForDonor(Integer userId) {
        User user = this.getById(userId);
        if (user != null && user.getIsDonor() == 1) {
            return true;
        }

        user.setIsDonor(1);
        return this.updateById(user);
    }

    @Override
    @Transactional
    public boolean reviewAuthApply(Integer applyId, Integer status, String reason) {
        UserAuthApply apply = authApplyMapper.selectById(applyId);
        if (apply == null) return false;

        apply.setStatus(status);
        apply.setReason(reason);
        apply.setUpdateTime(LocalDateTime.now());
        authApplyMapper.updateById(apply);

        if (status == 1) {
            User user = this.getById(apply.getUserId());
            if (apply.getAuthType() == 1) {
                user.setIsAdopter(1);
            } else if (apply.getAuthType() == 2) {
                user.setIsDonor(1);
            }
            return this.updateById(user);
        }
        return true;
    }

    @Override
    public UserAuthApply getPendingAuthApply(Integer userId, Integer authType) {
        LambdaQueryWrapper<UserAuthApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthApply::getUserId, userId)
                .eq(UserAuthApply::getAuthType, authType)
                .eq(UserAuthApply::getStatus, 0);
        return authApplyMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.countTotalUsers());
        stats.put("adopters", userMapper.countAdopters());
        stats.put("donors", userMapper.countDonors());
        stats.put("todayNew", userMapper.countTodayNewUsers());
        stats.put("pendingAuth", authApplyMapper.countPendingAuth());
        return stats;
    }

    @Override
    public List<UserAuthApply> getAuthApplies() {
        LambdaQueryWrapper<UserAuthApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthApply::getStatus, 0)
                .orderByAsc(UserAuthApply::getCreateTime);
        return authApplyMapper.selectList(wrapper);
    }

    @Override
    public UserAuthApply getAuthApplyById(Integer id) {
        return authApplyMapper.selectById(id);
    }

    // ========== 添加这个方法 ==========
    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return this.getOne(wrapper);
    }
    // 在 UserServiceImpl.java 中添加
    @Override
    public List<UserAuthApply> getAuthAppliesByUserId(Integer userId) {
        LambdaQueryWrapper<UserAuthApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthApply::getUserId, userId)
                .orderByDesc(UserAuthApply::getCreateTime);
        return authApplyMapper.selectList(wrapper);
    }
}