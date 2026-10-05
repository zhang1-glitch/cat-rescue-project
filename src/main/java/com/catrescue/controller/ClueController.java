package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.Clue;
import com.catrescue.entity.ClueImage;
import com.catrescue.entity.User;
import com.catrescue.mapper.ClueImageMapper;
import com.catrescue.service.ClueService;
import com.catrescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/clue")
public class ClueController {

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueImageMapper clueImageMapper;

    @GetMapping("/submit")
    public String submitPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "clue/submit";
    }

    @PostMapping("/submit")
    @ResponseBody
    public Result<String> submitClue(
            @RequestParam String location,
            @RequestParam Integer catCount,
            @RequestParam(required = false) String healthDesc,
            @RequestParam(required = false) String supplement,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) MultipartFile[] images,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            System.out.println("收到线索提交请求");
            System.out.println("  - 用户: " + user.getNickname());
            System.out.println("  - 位置: " + location);
            System.out.println("  - 猫咪数量: " + catCount);
            System.out.println("  - 图片数量: " + (images != null ? images.length : 0));

            // 创建 Clue 对象
            Clue clue = new Clue();
            clue.setUserId(user.getId());
            clue.setLocation(location);
            clue.setCatCount(catCount);
            clue.setHealthDesc(healthDesc);
            clue.setSupplement(supplement);
            clue.setContact(contact);
            clue.setStatus(0);
            clue.setCreateTime(LocalDateTime.now());

            // 先保存线索
            boolean saved = clueService.save(clue);
            System.out.println("线索保存结果: " + saved + ", ID: " + clue.getId());

            // 保存图片
            if (saved && images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    if (file != null && !file.isEmpty()) {
                        String imageUrl = FileUploadUtil.uploadFile(file);
                        System.out.println("图片上传: " + imageUrl);

                        ClueImage clueImage = new ClueImage();
                        clueImage.setClueId(clue.getId());
                        clueImage.setImageUrl(imageUrl);
                        clueImageMapper.insert(clueImage);
                    }
                }
            }

            if (saved) {
                System.out.println("线索提交成功");
                return Result.success("线索已提交，感谢您的爱心");
            }
            return Result.error("提交失败");
        } catch (Exception e) {
            System.err.println("提交线索异常：" + e.getMessage());
            e.printStackTrace();
            return Result.error("提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/my-clues")
    public String myClues(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        java.util.List<Clue> clues = clueService.getUserClues(user.getId());
        model.addAttribute("clues", clues);
        return "clue/my-clues";
    }
}