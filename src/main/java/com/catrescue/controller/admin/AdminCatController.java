package com.catrescue.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catrescue.common.Result;
import com.catrescue.entity.Cat;
import com.catrescue.entity.CatImage;
import com.catrescue.mapper.CatImageMapper;
import com.catrescue.service.CatService;
import com.catrescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;        // ← 添加这行
import java.time.format.DateTimeFormatter;  // ← 添加这行
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCatController {

    @Autowired
    private CatService catService;

    @Autowired
    private CatImageMapper catImageMapper;

    @GetMapping("/cats")
    public String cats(@RequestParam(defaultValue = "1") Integer page,
                       Model model) {
        Page<Cat> pageResult = catService.page(new Page<>(page, 10));
        model.addAttribute("cats", pageResult.getRecords());
        model.addAttribute("pageNum", page);
        model.addAttribute("totalPages", pageResult.getPages());
        return "admin/cats";
    }

    @GetMapping("/cat/add")
    public String addCatPage() {
        return "admin/cat-form";
    }

    @GetMapping("/cat/edit/{id}")
    public String editCatPage(@PathVariable Integer id, Model model) {
        Cat cat = catService.getById(id);
        if (cat != null) {
            LambdaQueryWrapper<CatImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CatImage::getCatId, id).orderByAsc(CatImage::getSortOrder);
            List<CatImage> images = catImageMapper.selectList(wrapper);
            cat.setCatImages(images);
        }
        model.addAttribute("cat", cat);
        return "admin/cat-form";
    }

    @PostMapping("/cat/save")
    @ResponseBody
    public Result<String> saveCat(@RequestParam(required = false) Integer id,
                                  @RequestParam String name,
                                  @RequestParam(required = false) String catNo,
                                  @RequestParam(required = false) Integer gender,
                                  @RequestParam(required = false) Integer age,
                                  @RequestParam(required = false) String breed,
                                  @RequestParam(required = false) String color,
                                  @RequestParam(required = false) String healthStatus,
                                  @RequestParam(required = false) Integer isNeutered,
                                  @RequestParam(required = false) Integer isVaccinated,
                                  @RequestParam(required = false) String rescueLocation,
                                  @RequestParam(required = false) String rescueTime,
                                  @RequestParam(required = false) String rescueStory,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) MultipartFile[] images) {

        Cat cat = new Cat();
        cat.setId(id);
        cat.setName(name);
        cat.setCatNo(catNo);
        cat.setGender(gender);
        cat.setAge(age);
        cat.setBreed(breed);
        cat.setColor(color);
        cat.setHealthStatus(healthStatus);
        cat.setIsNeutered(isNeutered);
        cat.setIsVaccinated(isVaccinated);
        cat.setRescueLocation(rescueLocation);
        cat.setRescueStory(rescueStory);
        cat.setStatus(status != null ? status : 0);

        // 处理 rescueTime 格式转换
        if (rescueTime != null && !rescueTime.isEmpty()) {
            try {
                cat.setRescueTime(LocalDateTime.parse(rescueTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            } catch (Exception e) {
                System.err.println("时间格式转换失败: " + rescueTime);
            }
        }

        List<String> imageUrls = null;
        if (images != null && images.length > 0) {
            try {
                imageUrls = FileUploadUtil.uploadFiles(images);
            } catch (IOException e) {
                return Result.error("图片上传失败");
            }
        }

        boolean success;
        if (id == null) {
            success = catService.addCat(cat, imageUrls);
        } else {
            success = catService.updateCat(cat, imageUrls);
        }
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }

    @PostMapping("/cat/delete/{id}")
    @ResponseBody
    public Result<String> deleteCat(@PathVariable Integer id) {
        boolean success = catService.deleteCat(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/cat/update-status/{id}")
    @ResponseBody
    public Result<String> updateCatStatus(@PathVariable Integer id,
                                          @RequestParam Integer status) {
        boolean success = catService.updateCatStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
}