package com.catrescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatImage;
import java.util.List;
import java.util.Map;

public interface CatService extends IService<Cat> {

    String generateCatNo();

    boolean addCat(Cat cat, List<String> imageUrls);

    boolean updateCat(Cat cat, List<String> imageUrls);

    boolean deleteCat(Integer id);

    boolean updateCatStatus(Integer catId, Integer status);

    List<Cat> getCatsByStatus(Integer status);

    Cat getCatDetail(Integer id);

    List<Cat> searchCats(String keyword);

    boolean favoriteCat(Integer userId, Integer catId);

    boolean unfavoriteCat(Integer userId, Integer catId);

    List<Integer> getUserFavorites(Integer userId);

    Map<String, Object> getCatStats();

    List<Cat> searchCats(String keyword, Integer status, Integer gender);
}