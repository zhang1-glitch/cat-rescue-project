package com.catrescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatImage;
import com.catrescue.entity.News;
import com.catrescue.mapper.CatImageMapper;
import com.catrescue.service.CatService;
import com.catrescue.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private CatService catService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private CatImageMapper catImageMapper;

    @GetMapping("/")
    public String index(Model model) {
        // 获取推荐猫咪（待领养状态 status=1）
        List<Cat> recommendedCats = catService.getCatsByStatus(1);

        System.out.println("首页推荐猫咪数量: " + recommendedCats.size());

        // 为每只猫咪设置文本属性和图片
        for (Cat cat : recommendedCats) {
            // 设置年龄文本
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

            // 设置性别文本
            if (cat.getGender() != null) {
                cat.setGenderText(cat.getGender() == 1 ? "公猫" : "母猫");
            } else {
                cat.setGenderText("未知");
            }

            // 设置状态文本
            cat.setStatusText(cat.getStatusText());

            // ========== 关键：加载猫咪图片 ==========
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, cat.getId()).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            cat.setCatImages(images != null ? images : new ArrayList<>());

            System.out.println("首页猫咪: " + cat.getName() + ", 图片数量: " + (cat.getCatImages() != null ? cat.getCatImages().size() : 0));
        }

        // 获取最新资讯（前3条）
        List<News> latestNews = newsService.getPublishedNews();
        if (latestNews.size() > 3) {
            latestNews = latestNews.subList(0, 3);
        }

        model.addAttribute("recommendedCats", recommendedCats);
        model.addAttribute("latestNews", latestNews);
        return "index";
    }

    // ========== 添加登录和注册页面映射 ==========
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}