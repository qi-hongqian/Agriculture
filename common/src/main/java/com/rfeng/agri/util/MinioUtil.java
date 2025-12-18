package com.rfeng.agri.util;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.avatar}")
    private String avatarBucket;

    /**
     * 上传用户头像
     *
     * @param file     头像文件
     * @param userId   用户ID
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file, Long userId) {
        try {
            // 检查并创建桶
            createBucketIfNotExists(avatarBucket);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // 返回文件访问URL
            return getAvatarUrl(fileName);
        } catch (Exception e) {
            log.error("上传头像失败: ", e);
            throw new RuntimeException("上传头像失败: " + e.getMessage());
        }
    }

    /**
     * 获取头像访问URL
     *
     * @param fileName 文件名
     * @return 访问URL
     */
    public String getAvatarUrl(String fileName) {
        return "/api/avatar/" + fileName;
    }

    /**
     * 获取论坛媒体访问URL
     *
     * @param fileName 文件名
     * @return 访问URL
     */
    public String getForumMediaUrl(String fileName) {
        return "/api/forum-media/" + fileName;
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 是否存在
     */
    public boolean isAvatarExists(String fileName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build()
            );
            return stat != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除头像文件
     *
     * @param avatarUrl 头像URL路径（如：/api/avatar/avatar_123.png）
     * @return 是否删除成功
     */
    public boolean deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(fileName)
                            .build()
            );
            log.info("删除临时头像文件: {}", fileName);
            return true;
        } catch (Exception e) {
            log.error("删除头像文件失败: ", e);
            return false;
        }
    }

    /**
     * 上传临时头像
     *
     * @param file 头像文件
     * @return 文件访问URL
     */
    public String uploadTempAvatar(MultipartFile file) {
        try {
            // 检查并创建桶
            createBucketIfNotExists(avatarBucket);

            // 生成唯一文件名（临时文件加temp_前缀）
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "temp_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            log.info("上传临时头像: {}", fileName);
            return getAvatarUrl(fileName);
        } catch (Exception e) {
            log.error("上传临时头像失败: ", e);
            throw new RuntimeException("上传临时头像失败: " + e.getMessage());
        }
    }

    /**
     * 将临时头像重命名为正式头像
     *
     * @param tempAvatarUrl 临时头像URL (如：/api/avatar/temp_xxx.jpg)
     * @param userId 用户ID
     * @return 正式头像URL (如：/api/avatar/avatar_userId_xxx.jpg)
     */
    public String renameTempAvatarToFormal(String tempAvatarUrl, Long userId) {
        try {
            String tempFileName = tempAvatarUrl.substring(tempAvatarUrl.lastIndexOf("/") + 1);
            
            String fileExtension = "";
            if (tempFileName.contains(".")) {
                fileExtension = tempFileName.substring(tempFileName.lastIndexOf("."));
            }
            String formalFileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
            
            InputStream tempFileStream = minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(tempFileName)
                            .build()
            );
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(formalFileName)
                            .stream(tempFileStream, tempFileStream.available(), -1)
                            .build()
            );
            
            try {
                minioClient.removeObject(
                        io.minio.RemoveObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(tempFileName)
                                .build()
                );
                log.info("删除临时头像文件: {}", tempFileName);
            } catch (Exception e) {
                log.warn("删除临时头像文件失败: {}", tempFileName);
            }
            
            log.info("临时头像重命名为正式头像: {} -> {}", tempFileName, formalFileName);
            return getAvatarUrl(formalFileName);
        } catch (Exception e) {
            log.error("重命名临时头像失败: ", e);
            throw new RuntimeException("重命名临时头像失败: " + e.getMessage());
        }
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("创建桶失败: ", e);
        }
    }

    /**
     * 批量上传临时论坛图片
     *
     * @param files 图片文件数组
     * @return 文件访问URL列表
     */
    public java.util.List<String> batchUploadTempForumImages(MultipartFile[] files) {
        java.util.List<String> urls = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                createBucketIfNotExists(avatarBucket);
                
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = "temp_forum_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;
                
                try (InputStream inputStream = file.getInputStream()) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(avatarBucket)
                                    .object(fileName)
                                    .stream(inputStream, file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
                }
                
                String url = getForumMediaUrl(fileName);
                urls.add(url);
                log.info("上传临时论坛图片: {}", fileName);
            } catch (Exception e) {
                log.error("上传临时论坛图片失败: {}", file.getOriginalFilename(), e);
            }
        }
        
        return urls;
    }

    /**
     * 将临时论坛图片批量重命名为正式图片
     *
     * @param tempImageUrls 临时图片URL列表
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 正式图片URL列表
     */
    public java.util.List<String> batchRenameTempForumImagesToFormal(java.util.List<String> tempImageUrls, Long userId, Long postId) {
        java.util.List<String> formalUrls = new java.util.ArrayList<>();
        
        for (String tempUrl : tempImageUrls) {
            try {
                String tempFileName = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
                
                String fileExtension = "";
                if (tempFileName.contains(".")) {
                    fileExtension = tempFileName.substring(tempFileName.lastIndexOf("."));
                }
                String formalFileName = "forum_" + postId + "_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
                
                StatObjectResponse stat = minioClient.statObject(
                        io.minio.StatObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(tempFileName)
                                .build()
                );
                
                InputStream tempFileStream = minioClient.getObject(
                        io.minio.GetObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(tempFileName)
                                .build()
                );
                
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(formalFileName)
                                .stream(tempFileStream, stat.size(), -1)
                                .build()
                );
                
                try {
                    minioClient.removeObject(
                            io.minio.RemoveObjectArgs.builder()
                                    .bucket(avatarBucket)
                                    .object(tempFileName)
                                    .build()
                    );
                    log.info("删除临时论坛图片: {}", tempFileName);
                } catch (Exception e) {
                    log.warn("删除临时论坛图片失败: {}", tempFileName);
                }
                
                String formalUrl = getForumMediaUrl(formalFileName);
                formalUrls.add(formalUrl);
                log.info("临时论坛图片重命名为正式: {} -> {}", tempFileName, formalFileName);
            } catch (Exception e) {
                log.error("重命名临时论坛图片失败: {}", tempUrl, e);
            }
        }
        
        return formalUrls;
    }

    /**
     * 批量删除论坛图片
     *
     * @param imageUrls 图片URL列表
     * @return 是否全部删除成功
     */
    public boolean batchDeleteForumImages(java.util.List<String> imageUrls) {
        boolean allSuccess = true;
        
        for (String imageUrl : imageUrls) {
            if (imageUrl == null || imageUrl.isEmpty()) {
                continue;
            }
            
            try {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                
                minioClient.removeObject(
                        io.minio.RemoveObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(fileName)
                                .build()
                );
                log.info("删除论坛图片: {}", fileName);
            } catch (Exception e) {
                log.error("删除论坛图片失败: {}", imageUrl, e);
                allSuccess = false;
            }
        }
        
        return allSuccess;
    }
}
