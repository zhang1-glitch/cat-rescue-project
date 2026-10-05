package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.Feedback;
import com.catrescue.entity.User;
import com.catrescue.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @PostMapping("/submit")
    @ResponseBody
    public Result<String> submitFeedback(Feedback feedback, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("请先登录");
        }

        feedback.setUserId(user.getId());
        feedback.setStatus(0);
        int result = feedbackMapper.insert(feedback);
        if (result > 0) {
            return Result.success("反馈已提交，我们会尽快回复");
        }
        return Result.error("提交失败");
    }
}