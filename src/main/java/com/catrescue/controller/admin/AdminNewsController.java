package com.catrescue.controller.admin;

import com.catrescue.common.Result;
import com.catrescue.entity.News;
import com.catrescue.service.NewsService;
import com.catrescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminNewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public String news(Model model) {
        model.addAttribute("newsList", newsService.list());
        return "admin/news";
    }

    @GetMapping("/news/add")
    public String addNewsPage() {
        return "admin/news-form";
    }

    @GetMapping("/news/edit/{id}")
    public String editNewsPage(@PathVariable Integer id, Model model) {
        model.addAttribute("news", newsService.getById(id));
        return "admin/news-form";
    }

    @PostMapping("/news/save")
    @ResponseBody
    public Result<String> saveNews(News news,
                                   @RequestParam(required = false) MultipartFile coverImage) {
        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                String imageUrl = FileUploadUtil.uploadFile(coverImage);
                news.setCoverImage(imageUrl);
            } catch (IOException e) {
                return Result.error("图片上传失败");
            }
        }

        news.setCreateTime(LocalDateTime.now());
        news.setViewCount(0);
        news.setLikeCount(0);
        boolean success = newsService.saveOrUpdate(news);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }

    @PostMapping("/news/delete/{id}")
    @ResponseBody
    public Result<String> deleteNews(@PathVariable Integer id) {
        boolean success = newsService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/news/publish/{id}")
    @ResponseBody
    public Result<String> publishNews(@PathVariable Integer id) {
        News news = newsService.getById(id);
        news.setStatus(1);
        newsService.updateById(news);
        return Result.success("发布成功");
    }
}