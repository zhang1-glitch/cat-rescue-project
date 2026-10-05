package com.catrescue.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUploadUtil {

    // 使用项目根目录的绝对路径
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public static String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 创建目录
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("创建目录: " + UPLOAD_DIR + " - " + (created ? "成功" : "失败"));
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String newName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        // 保存文件
        File dest = new File(UPLOAD_DIR + newName);
        file.transferTo(dest);

        System.out.println("文件保存位置: " + dest.getAbsolutePath());

        return "/uploads/" + newName;
    }

    public static List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = uploadFile(file);
                    if (url != null) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }
}