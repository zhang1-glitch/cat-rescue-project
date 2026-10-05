package com.catrescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catrescue.entity.Comment;
import com.catrescue.entity.News;
import com.catrescue.mapper.CommentMapper;
import com.catrescue.mapper.NewsMapper;
import com.catrescue.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<News> getPublishedNews() {
        return newsMapper.findPublished();
    }

    @Override
    public News getNewsDetail(Integer id) {
        newsMapper.incrementViewCount(id);
        return this.getById(id);
    }

    @Override
    public boolean likeNews(Integer id) {
        newsMapper.incrementLikeCount(id);
        return true;
    }

    @Override
    public boolean addComment(Comment comment) {
        try {
            System.out.println("添加评论 - newsId: " + comment.getNewsId() + ", userId: " + comment.getUserId());
            System.out.println("评论内容: " + comment.getContent());

            comment.setCreateTime(LocalDateTime.now());
            comment.setLikeCount(0);

            int result = commentMapper.insert(comment);
            System.out.println("插入结果: " + result);
            return result > 0;
        } catch (Exception e) {
            System.err.println("评论失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Comment> getNewsComments(Integer newsId) {
        return commentMapper.findByNewsId(newsId);
    }

    @Override
    public Map<String, Object> getNewsStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", this.count());
        stats.put("published", newsMapper.findPublished().size());
        return stats;
    }
}