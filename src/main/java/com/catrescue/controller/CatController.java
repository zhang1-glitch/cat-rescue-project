package com.catrescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catrescue.common.Result;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatImage;
import com.catrescue.entity.User;
import com.catrescue.mapper.CatImageMapper;
import com.catrescue.service.CatService;
import com.catrescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cat")
public class CatController {

    @Autowired
    private CatService catService;

    @Autowired
    private CatImageMapper catImageMapper;

    @GetMapping("/list")
    public String list(Model model) {
        List<Cat> cats = catService.list();

        for (Cat cat : cats) {
            // 设置文本属性
            setCatTextProperties(cat);

            // ========== 关键：加载猫咪图片 ==========
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, cat.getId()).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            cat.setCatImages(images != null ? images : new ArrayList<>());

            // 调试输出
            System.out.println("猫咪: " + cat.getName() + ", 图片数量: " + (cat.getCatImages() != null ? cat.getCatImages().size() : 0));
        }

        model.addAttribute("cats", cats);
        return "cat/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model, HttpSession session) {
        Cat cat = catService.getCatDetail(id);
        setCatTextProperties(cat);
        model.addAttribute("cat", cat);
        return "cat/detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Integer status,
                         @RequestParam(required = false) Integer gender,
                         Model model) {
        List<Cat> cats = catService.searchCats(keyword, status, gender);

        for (Cat cat : cats) {
            setCatTextProperties(cat);
            // 加载图片
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, cat.getId()).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            cat.setCatImages(images != null ? images : new ArrayList<>());
        }

        model.addAttribute("cats", cats);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("gender", gender);
        return "cat/list";
    }

    @PostMapping("/favorite")
    @ResponseBody
    public Result<String> favorite(@RequestParam Integer catId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = catService.favoriteCat(user.getId(), catId);
        if (success) {
            return Result.success("收藏成功");
        }
        return Result.error("已收藏过");
    }

    @PostMapping("/unfavorite")
    @ResponseBody
    public Result<String> unfavorite(@RequestParam Integer catId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = catService.unfavoriteCat(user.getId(), catId);
        return Result.success("取消收藏");
    }

    @GetMapping("/favorites")
    public String favorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Integer> favoriteIds = catService.getUserFavorites(user.getId());
        List<Cat> favorites = new ArrayList<>();
        if (favoriteIds != null && !favoriteIds.isEmpty()) {
            favorites = catService.listByIds(favoriteIds);
            for (Cat cat : favorites) {
                setCatTextProperties(cat);
                // 加载图片
                LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CatImage::getCatId, cat.getId()).orderByAsc(CatImage::getSortOrder);
                List<CatImage> images = catImageMapper.selectList(wrapper);
                cat.setCatImages(images != null ? images : new ArrayList<>());
            }
        }
        model.addAttribute("favorites", favorites);
        return "cat/favorites";
    }

    // 辅助方法：设置猫咪的文本属性
    private void setCatTextProperties(Cat cat) {
        if (cat == null) return;

        // 年龄文本
        if (cat.getAge() != null) {
            if (cat.getAge() < 12) {
                cat.setAgeText(cat.getAge() + "个月");
            } else {
                int years = cat.getAge() / 12;
                int months = cat.getAge() % 12;
                if (months == 0) {
                    cat.setAgeText(years + "岁");
                } else {
                    cat.setAgeText(years + "岁" + months + "个月");
                }
            }
        } else {
            cat.setAgeText("未知");
        }

        // 性别文本
        if (cat.getGender() != null) {
            cat.setGenderText(cat.getGender() == 1 ? "公猫" : "母猫");
        } else {
            cat.setGenderText("未知");
        }

        // 状态文本
        cat.setStatusText(cat.getStatusText());
    }
}