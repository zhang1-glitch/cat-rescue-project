package com.catrescue.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catrescue.common.Result;
import com.catrescue.entity.Clue;
import com.catrescue.entity.ClueImage;
import com.catrescue.mapper.ClueImageMapper;
import com.catrescue.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminClueController {

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueImageMapper clueImageMapper;  // 添加这行

    @GetMapping("/clues")
    public String clues(Model model) {
        model.addAttribute("pendingClues", clueService.getPendingClues());
        return "admin/clues";
    }

    @GetMapping("/clue/{id}")
    public String clueDetail(@PathVariable Integer id, Model model) {
        Clue clue = clueService.getById(id);

        // ========== 关键：获取线索的图片 ==========
        LambdaQueryWrapper<ClueImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClueImage::getClueId, id);
        List<ClueImage> images = clueImageMapper.selectList(wrapper);
        clue.setImages(images);  // 需要在 Clue 实体类中添加 images 字段

        model.addAttribute("clue", clue);
        return "admin/clue-detail";
    }

    @PostMapping("/clue/review/{id}")
    @ResponseBody
    public Result<String> reviewClue(@PathVariable Integer id,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String rejectReason) {
        boolean success = clueService.reviewClue(id, status, rejectReason);
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }

    @PostMapping("/clue/adopt/{id}")
    @ResponseBody
    public Result<String> adoptToCat(@PathVariable Integer id) {
        boolean success = clueService.adoptToCat(id);
        return success ? Result.success("已转为猫咪档案") : Result.error("操作失败");
    }
}