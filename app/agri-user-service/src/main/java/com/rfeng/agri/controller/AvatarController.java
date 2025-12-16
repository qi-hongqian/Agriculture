package com.rfeng.agri.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/16 17:10
 */

@Slf4j
@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.avatar}")
    private String avatarBucket;

    @GetMapping("/{fileName}")
    public void getAvatar(@PathVariable String fileName, HttpServletResponse response) {
        try {
            // 检查文件是否存在
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build()
            );

            // 设置响应头
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

            // 获取文件流并写入响应
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build())) {
                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            log.error("获取头像失败: ", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
