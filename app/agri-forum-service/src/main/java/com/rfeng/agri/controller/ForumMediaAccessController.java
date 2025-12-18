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
 * @time 2025/12/18
 */
@Slf4j
@RestController
@RequestMapping("/api/forum-media")
public class ForumMediaAccessController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.avatar}")
    private String avatarBucket;

    @GetMapping("/{fileName}")
    public void getForumMedia(@PathVariable String fileName, HttpServletResponse response) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build()
            );

            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build())) {
                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            log.error("获取论坛图片失败: ", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
