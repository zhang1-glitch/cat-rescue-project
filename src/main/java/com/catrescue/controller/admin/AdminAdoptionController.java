package com.catrescue.controller.admin;

import com.catrescue.common.Result;
import com.catrescue.entity.AdoptionApply;
import com.catrescue.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminAdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping("/adoptions")
    public String adoptions(Model model) {
        // 待初审 (status=0)
        model.addAttribute("pendingInitial", adoptionService.getPendingInitialReviews());
        // 待回访 (status=1)
        model.addAttribute("pendingInterview", adoptionService.getPendingInterview());
        // 待终审 (status=2)
        model.addAttribute("pendingFinal", adoptionService.getPendingFinalReviews());
        return "admin/adoptions";
    }

    @GetMapping("/adoption/{id}")
    public String adoptionDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("apply", adoptionService.getApplicationDetail(id));
        return "admin/adoption-detail";
    }

    // 记录回访接口
    @PostMapping("/adoption/record-interview/{id}")
    @ResponseBody
    public Result<String> recordInterview(@PathVariable Integer id,
                                          @RequestParam String interviewRecord) {
        boolean success = adoptionService.recordInterview(id, interviewRecord);
        return success ? Result.success("回访记录已保存") : Result.error("操作失败");
    }

    @PostMapping("/adoption/initial-review/{id}")
    @ResponseBody
    public Result<String> initialReview(@PathVariable Integer id,
                                        @RequestParam Boolean pass,
                                        @RequestParam(required = false) String opinion) {
        boolean success = adoptionService.initialReview(id, pass, opinion);
        return success ? Result.success("审核完成") : Result.error("操作失败");
    }

    @PostMapping("/adoption/final-review/{id}")
    @ResponseBody
    public Result<String> finalReview(@PathVariable Integer id,
                                      @RequestParam Boolean pass,
                                      @RequestParam(required = false) String rejectReason) {
        boolean success = adoptionService.finalReview(id, pass, rejectReason);
        return success ? Result.success("审核完成") : Result.error("操作失败");
    }

    @PostMapping("/adoption/sign/{id}")
    @ResponseBody
    public Result<String> signProtocol(@PathVariable Integer id) {
        boolean success = adoptionService.signProtocol(id);
        return success ? Result.success("协议已签订，领养完成") : Result.error("操作失败");
    }
}