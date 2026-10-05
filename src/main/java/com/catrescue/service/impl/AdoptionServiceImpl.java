package com.catrescue.service.impl;

import com.catrescue.entity.AdoptionApply;
import com.catrescue.entity.Cat;
import com.catrescue.mapper.AdoptionApplyMapper;
import com.catrescue.mapper.CatMapper;
import com.catrescue.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdoptionServiceImpl implements AdoptionService {

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private CatMapper catMapper;

    @Override
    @Transactional
    public boolean submitApplication(AdoptionApply apply) {
        apply.setStatus(0); // 待初审
        apply.setCreateTime(LocalDateTime.now());
        return adoptionApplyMapper.insert(apply) > 0;
    }

    @Override
    @Transactional
    public boolean initialReview(Integer applyId, Boolean pass, String opinion) {
        AdoptionApply apply = adoptionApplyMapper.selectById(applyId);
        if (pass) {
            apply.setStatus(1); // 初审通过
            apply.setInterviewRecord(opinion);
        } else {
            apply.setStatus(4); // 已拒绝
            apply.setRejectReason(opinion);
        }
        apply.setUpdateTime(LocalDateTime.now());
        return adoptionApplyMapper.updateById(apply) > 0;
    }

    @Override
    @Transactional
    public boolean recordInterview(Integer applyId, String interviewRecord) {
        AdoptionApply apply = adoptionApplyMapper.selectById(applyId);
        apply.setStatus(2); // 待回访
        apply.setInterviewRecord(interviewRecord);
        apply.setUpdateTime(LocalDateTime.now());
        return adoptionApplyMapper.updateById(apply) > 0;
    }

    @Override
    @Transactional
    public boolean finalReview(Integer applyId, Boolean pass, String rejectReason) {
        AdoptionApply apply = adoptionApplyMapper.selectById(applyId);
        if (pass) {
            apply.setStatus(3); // 已通过
        } else {
            apply.setStatus(4); // 已拒绝
            apply.setRejectReason(rejectReason);
        }
        apply.setUpdateTime(LocalDateTime.now());
        return adoptionApplyMapper.updateById(apply) > 0;
    }

    @Override
    @Transactional
    public boolean signProtocol(Integer applyId) {
        try {
            AdoptionApply apply = adoptionApplyMapper.selectById(applyId);
            if (apply == null) {
                System.err.println("领养申请不存在，ID：" + applyId);
                return false;
            }

            apply.setProtocolSigned(1);
            apply.setSignTime(LocalDateTime.now());
            apply.setStatus(5); // 已完成
            apply.setUpdateTime(LocalDateTime.now());

            // 更新猫咪状态为已领养
            Cat cat = catMapper.selectById(apply.getCatId());
            if (cat != null) {
                cat.setStatus(2); // 已领养
                catMapper.updateById(cat);
                System.out.println("猫咪状态已更新为已领养，猫咪：" + cat.getName() + "，ID：" + cat.getId());
            } else {
                System.err.println("猫咪不存在，ID：" + apply.getCatId());
            }

            int result = adoptionApplyMapper.updateById(apply);
            System.out.println("领养协议签订完成，申请ID：" + applyId + "，结果：" + result);
            return result > 0;
        } catch (Exception e) {
            System.err.println("签订协议失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public AdoptionApply getApplicationDetail(Integer applyId) {
        return adoptionApplyMapper.selectById(applyId);
    }

    @Override
    public List<AdoptionApply> getUserApplications(Integer userId) {
        return adoptionApplyMapper.findByUserId(userId);
    }

    @Override
    public List<AdoptionApply> getPendingInitialReviews() {
        return adoptionApplyMapper.findByStatus(0);
    }

    @Override
    public List<AdoptionApply> getPendingFinalReviews() {
        return adoptionApplyMapper.findByStatus(2);
    }
    public List<AdoptionApply> getPendingInterview() {
        return adoptionApplyMapper.findByStatus(1);  // 待回访
    }


    @Override
    public Map<String, Object> getAdoptionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingInitial", adoptionApplyMapper.countPendingReviews());
        stats.put("totalApplications", adoptionApplyMapper.selectCount(null));
        return stats;
    }

}