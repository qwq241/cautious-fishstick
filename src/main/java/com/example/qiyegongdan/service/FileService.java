package com.example.qiyegongdan.service;

import com.example.qiyegongdan.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    private final Path uploadDir;

    public FileService(@Value("${app.file.upload-dir}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        safeFilename(originalName);
        String storedName = UUID.randomUUID() + "_" + originalName;

        Path targetPath = this.uploadDir.resolve(storedName).normalize();
        if (!targetPath.startsWith(this.uploadDir)) {
            throw new BusinessException("非法的文件路径");
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        return storedName;
    }

    public Path getFile(String storedName) {
        safeFilename(storedName);
        Path filePath = this.uploadDir.resolve(storedName).normalize();
        if (!filePath.startsWith(this.uploadDir)) {
            throw new BusinessException("非法的文件路径");
        }
        if (!Files.exists(filePath)) {
            throw new BusinessException("文件不存在");
        }
        return filePath;
    }

    private void safeFilename(String name) {
        if (name == null || name.contains("..") || name.contains("/") || name.contains("\\")) {
            throw new BusinessException("非法的文件名");
        }
    }
}