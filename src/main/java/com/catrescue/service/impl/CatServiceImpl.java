package com.catrescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatFavorite;
import com.catrescue.entity.CatImage;
import com.catrescue.mapper.CatFavoriteMapper;
import com.catrescue.mapper.CatImageMapper;
import com.catrescue.mapper.CatMapper;
import com.catrescue.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;  // ← 添加这行
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CatServiceImpl extends ServiceImpl<CatMapper, Cat> implements CatService {

    @Autowired
    private CatMapper catMapper;

    @Autowired
    private CatImageMapper catImageMapper;

    @Autowired
    private CatFavoriteMapper favoriteMapper;

    @Override
    public String generateCatNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Cat> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Cat::getCatNo, "CAT" + date);
        long count = this.count(wrapper);
        return String.format("CAT%s%03d", date, count + 1);
    }

    @Override
    @Transactional
    public boolean addCat(Cat cat, List<String> imageUrls) {
        cat.setCatNo(generateCatNo());
        cat.setViewCount(0);
        boolean saved = this.save(cat);

        if (saved && imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                CatImage image = new CatImage();
                image.setCatId(cat.getId());
                image.setImageUrl(imageUrls.get(i));
                image.setSortOrder(i);
                catImageMapper.insert(image);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean updateCat(Cat cat, List<String> imageUrls) {
        // 删除原图片
        LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CatImage::getCatId, cat.getId());
        catImageMapper.delete(wrapper);

        // 添加新图片
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                CatImage image = new CatImage();
                image.setCatId(cat.getId());
                image.setImageUrl(imageUrls.get(i));
                image.setSortOrder(i);
                catImageMapper.insert(image);
            }
        }

        return this.updateById(cat);
    }

    @Override
    public boolean deleteCat(Integer id) {
        return this.removeById(id);
    }

    @Override
    public boolean updateCatStatus(Integer catId, Integer status) {
        Cat cat = this.getById(catId);
        cat.setStatus(status);
        return this.updateById(cat);
    }

    @Override
    public List<Cat> getCatsByStatus(Integer status) {
        return catMapper.findByStatus(status);
    }

    @Override
    public Cat getCatDetail(Integer id) {
        catMapper.incrementViewCount(id);
        Cat cat = this.getById(id);
        if (cat != null) {
            // 获取图片
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, id).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            // 确保不为 null，即使是空列表
            cat.setCatImages(images != null ? images : new ArrayList<>());
        }
        return cat;
    }

    @Override
    public List<Cat> searchCats(String keyword) {
        return catMapper.search(keyword);
    }

    // ========== 新增：批量获取猫咪图片的方法 ==========
    public List<Cat> loadCatImages(List<Cat> cats) {
        for (Cat cat : cats) {
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, cat.getId()).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            cat.setCatImages(images != null ? images : new ArrayList<>());
        }
        return cats;
    }

    @Override
    @Transactional
    public boolean favoriteCat(Integer userId, Integer catId) {
        try {
            System.out.println("收藏操作 - 用户ID: " + userId + ", 猫咪ID: " + catId);

            // 检查是否已经收藏
            LambdaQueryWrapper<CatFavorite> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(CatFavorite::getUserId, userId)
                    .eq(CatFavorite::getCatId, catId);
            Long count = favoriteMapper.selectCount(checkWrapper);
            if (count > 0) {
                System.out.println("已经收藏过了");
                return false;
            }

            CatFavorite favorite = new CatFavorite();
            favorite.setUserId(userId);
            favorite.setCatId(catId);
            favorite.setCreateTime(LocalDateTime.now());

            int result = favoriteMapper.insert(favorite);
            System.out.println("插入结果: " + result);
            return result > 0;
        } catch (Exception e) {
            System.err.println("收藏失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unfavoriteCat(Integer userId, Integer catId) {
        LambdaQueryWrapper<CatFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CatFavorite::getUserId, userId).eq(CatFavorite::getCatId, catId);
        return favoriteMapper.delete(wrapper) > 0;
    }

    @Override
    public List<Integer> getUserFavorites(Integer userId) {
        LambdaQueryWrapper<CatFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CatFavorite::getUserId, userId);
        return favoriteMapper.selectList(wrapper).stream()
                .map(CatFavorite::getCatId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCatStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", catMapper.countTotalCats());
        stats.put("waitingRescue", catMapper.countByStatus(0));
        stats.put("waitingAdoption", catMapper.countByStatus(1));
        stats.put("adopted", catMapper.countByStatus(2));
        stats.put("fostering", catMapper.countByStatus(3));
        stats.put("treating", catMapper.countByStatus(4));
        return stats;
    }

    // 在 CatServiceImpl.java 中添加
    @Override
    public List<Cat> searchCats(String keyword, Integer status, Integer gender) {
        return catMapper.searchCats(keyword, status, gender);
    }
}