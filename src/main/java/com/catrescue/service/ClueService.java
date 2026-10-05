package com.catrescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.catrescue.entity.Clue;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;  // ← 添加这行导入

public interface ClueService extends IService<Clue> {

    boolean submitClue(Clue clue, MultipartFile[] images);

    boolean reviewClue(Integer clueId, Integer status, String rejectReason);

    boolean adoptToCat(Integer clueId);

    List<Clue> getPendingClues();

    List<Clue> getUserClues(Integer userId);

    Map<String, Object> getClueStats();  // 这里使用了 Map
}