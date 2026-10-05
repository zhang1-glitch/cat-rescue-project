package com.catrescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatImage;
import com.catrescue.entity.Clue;
import com.catrescue.entity.ClueImage;
import com.catrescue.mapper.CatImageMapper;
import com.catrescue.mapper.CatMapper;
import com.catrescue.mapper.ClueImageMapper;
import com.catrescue.mapper.ClueMapper;
import com.catrescue.service.ClueService;
import com.catrescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private ClueImageMapper clueImageMapper;

    @Autowired
    private CatMapper catMapper;

    @Autowired
    private CatImageMapper catImageMapper;  // ← 添加这一行

    @Override
    @Transactional
    public boolean submitClue(Clue clue, MultipartFile[] images) {
        clue.setStatus(0);
        clue.setCreateTime(LocalDateTime.now());
        boolean saved = this.save(clue);

        if (saved && images != null) {
            try {
                List<String> imageUrls = FileUploadUtil.uploadFiles(images);
                for (String url : imageUrls) {
                    ClueImage image = new ClueImage();
                    image.setClueId(clue.getId());
                    image.setImageUrl(url);
                    clueImageMapper.insert(image);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean reviewClue(Integer clueId, Integer status, String rejectReason) {
        Clue clue = this.getById(clueId);
        clue.setStatus(status);
        if (status == 2) {
            clue.setRejectReason(rejectReason);
        }
        clue.setUpdateTime(LocalDateTime.now());
        return this.updateById(clue);
    }

    @Override
    @Transactional
    public boolean adoptToCat(Integer clueId) {
        Clue clue = this.getById(clueId);
        if (clue == null || clue.getStatus() != 0) {
            return false;
        }

        // 创建猫咪档案
        Cat cat = new Cat();
        cat.setCatNo("CAT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        cat.setName("待命名");  // 可以后续编辑
        cat.setRescueLocation(clue.getLocation());
        cat.setRescueTime(LocalDateTime.now());
        cat.setStatus(1);  // 待领养
        cat.setHealthStatus(clue.getHealthDesc());
        cat.setRescueStory("来自用户线索：" + clue.getLocation());
        catMapper.insert(cat);

        System.out.println("猫咪档案创建成功，ID: " + cat.getId());

        // ========== 关键：将线索的图片转为猫咪图片 ==========
        LambdaQueryWrapper<ClueImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClueImage::getClueId, clueId);
        List<ClueImage> clueImages = clueImageMapper.selectList(wrapper);

        if (clueImages != null && !clueImages.isEmpty()) {
            for (int i = 0; i < clueImages.size(); i++) {
                ClueImage clueImage = clueImages.get(i);
                CatImage catImage = new CatImage();
                catImage.setCatId(cat.getId());
                catImage.setImageUrl(clueImage.getImageUrl());  // 使用原图片URL
                catImage.setSortOrder(i);
                catImageMapper.insert(catImage);
                System.out.println("猫咪图片添加成功: " + clueImage.getImageUrl());
            }
        } else {
            // 如果没有图片，设置默认图片
            CatImage defaultImage = new CatImage();
            defaultImage.setCatId(cat.getId());
            defaultImage.setImageUrl("/uploads/default-cat.jpg");
            defaultImage.setSortOrder(0);
            catImageMapper.insert(defaultImage);
        }

        // 更新线索状态
        clue.setStatus(1);
        clue.setUpdateTime(LocalDateTime.now());
        return this.updateById(clue);
    }

    @Override
    public List<Clue> getPendingClues() {
        return clueMapper.findByStatus(0);
    }

    @Override
    public List<Clue> getUserClues(Integer userId) {
        LambdaQueryWrapper<Clue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clue::getUserId, userId).orderByDesc(Clue::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getClueStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", clueMapper.countPendingClues());
        stats.put("total", this.count());
        return stats;
    }
}