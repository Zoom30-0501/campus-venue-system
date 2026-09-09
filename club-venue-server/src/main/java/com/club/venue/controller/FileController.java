package com.club.venue.controller;

import com.club.venue.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${club.upload.path:uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return Result.error("文件名为空");
        }
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            ext = originalName.substring(dot);
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 相对路径：uploads/yyyy/MM/dd/xxx.jpg
        String relativePath = uploadPath + "/" + dateDir;

        try {
            Path dir = Paths.get(System.getProperty("user.dir"), relativePath);
            Files.createDirectories(dir);
            Path target = dir.resolve(newName);
            file.transferTo(target.toFile());

            // 返回对外访问的 URL 路径
            String url = "/" + relativePath.replace("\\", "/") + "/" + newName;
            return Result.ok(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
