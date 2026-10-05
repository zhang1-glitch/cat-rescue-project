package com.catrescue.service;

import com.catrescue.entity.AdoptionApply;
import java.util.List;
import java.util.Map;

public interface AdoptionService {

    boolean submitApplication(AdoptionApply apply);

    boolean initialReview(Integer applyId, Boolean pass, String opinion);

    boolean recordInterview(Integer applyId, String interviewRecord);

    boolean finalReview(Integer applyId, Boolean pass, String rejectReason);

    boolean signProtocol(Integer applyId);

    AdoptionApply getApplicationDetail(Integer applyId);

    List<AdoptionApply> getUserApplications(Integer userId);

    List<AdoptionApply> getPendingInitialReviews();
    // 获取待回访的申请（初审通过但未回访，status=1）
    List<AdoptionApply> getPendingInterview();

    List<AdoptionApply> getPendingFinalReviews();

    Map<String, Object> getAdoptionStats();
}