package com.catrescue.controller;

import com.catrescue.common.Result;
import com.catrescue.entity.Comment;
import com.catrescue.entity.News;
import com.catrescue.entity.User;
import com.catrescue.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/list")
    public String list(Model model) {
        List<News> newsList = newsService.getPublishedNews();
        model.addAttribute("newsList", newsList);
        return "news/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model, HttpSession session) {
        News news = newsService.getNewsDetail(id);
        List<Comment> comments = newsService.getNewsComments(id);

        model.addAttribute("news", news);
        model.addAttribute("comments", comments);

        return "news/detail";
    }

    @PostMapping("/like/{id}")
    @ResponseBody
    public Result<String> like(@PathVariable Integer id) {
        boolean success = newsService.likeNews(id);
        return Result.success("点赞成功");
    }

    @PostMapping("/comment")
    @ResponseBody
    public Result<String> addComment(Comment comment, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        comment.setUserId(user.getId());
        boolean success = newsService.addComment(comment);
        if (success) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }
}