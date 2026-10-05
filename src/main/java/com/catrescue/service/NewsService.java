package com.catrescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.catrescue.entity.News;
import com.catrescue.entity.Comment;
import java.util.List;
import java.util.Map;

public interface NewsService extends IService<News> {

    List<News> getPublishedNews();

    News getNewsDetail(Integer id);

    boolean likeNews(Integer id);

    boolean addComment(Comment comment);

    List<Comment> getNewsComments(Integer newsId);

    Map<String, Object> getNewsStats();
}