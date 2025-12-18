package com.rfeng.agri.controller;

import com.rfeng.agri.constant.ForumConstants;
import com.rfeng.agri.model.entity.forumentity.ForumMedia;
import com.rfeng.agri.result.ApiResponse;
import com.rfeng.agri.service.ForumMediaService;
import com.rfeng.agri.util.MinioUtil;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
@Slf4j
@RestController
@Tag(name = "论坛媒体管理", description = "图片视频上传和管理相关接口")
@RequestMapping("/api/forum")
public class ForumMediaController {

    @Autowired
    private ForumMediaService forumMediaService;

    @Autowired
    private MinioUtil minioUtil;

    @Operation(summary = "批量上传临时图片", description = "发布帖子前批量上传临时图片用于预览")
    @PostMapping("/upload/temp-images")
    public ApiResponse<List<Map<String, Object>>> uploadTempImages(@RequestParam MultipartFile[] files) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            List<String> urls = minioUtil.batchUploadTempForumImages(files);

            List<Map<String, Object>> results = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                Map<String, Object> data = new HashMap<>();
                data.put("url", urls.get(i));
                data.put("fileName", files[i].getOriginalFilename());
                data.put("fileSize", files[i].getSize() / 1024);
                results.add(data);
            }

            return ApiResponse.successData("批量上传临时图片成功", results);
        } catch (Exception e) {
            log.error("批量上传临时图片失败: ", e);
            return ApiResponse.fail("批量上传临时图片失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除临时图片", description = "用户取消发布或更换图片时删除临时图片")
    @DeleteMapping("/upload/temp-images")
    public ApiResponse<Object> deleteTempImages(@RequestBody List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return ApiResponse.success("无需删除");
        }

        try {
            boolean success = minioUtil.batchDeleteForumImages(imageUrls);
            if (success) {
                return ApiResponse.success("删除临时图片成功");
            } else {
                return ApiResponse.fail("部分临时图片删除失败");
            }
        } catch (Exception e) {
            log.error("删除临时图片失败: ", e);
            return ApiResponse.fail("删除临时图片失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传图片", description = "上传论坛图片（帖子或评论）")
    @PostMapping("/upload/image")
    public ApiResponse<Map<String, Object>> uploadImage(@RequestParam MultipartFile file) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            String url = minioUtil.uploadAvatar(file, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            data.put("fileName", file.getOriginalFilename());
            data.put("fileSize", file.getSize() / 1024);

            return ApiResponse.successData("上传成功", data);
        } catch (Exception e) {
            log.error("上传图片失败: ", e);
            return ApiResponse.fail("上传图片失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传视频", description = "上传论坛视频")
    @PostMapping("/upload/video")
    public ApiResponse<Map<String, Object>> uploadVideo(@RequestParam MultipartFile file) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            String url = minioUtil.uploadAvatar(file, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            data.put("fileName", file.getOriginalFilename());
            data.put("fileSize", file.getSize() / 1024);

            return ApiResponse.successData("上传成功", data);
        } catch (Exception e) {
            log.error("上传视频失败: ", e);
            return ApiResponse.fail("上传视频失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量上传", description = "批量上传图片或视频")
    @PostMapping("/upload/batch")
    public ApiResponse<List<Map<String, Object>>> batchUpload(@RequestParam MultipartFile[] files) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            List<Map<String, Object>> results = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    String url = minioUtil.uploadAvatar(file, userId);

                    Map<String, Object> data = new HashMap<>();
                    data.put("url", url);
                    data.put("fileName", file.getOriginalFilename());
                    data.put("fileSize", file.getSize() / 1024);
                    results.add(data);
                } catch (Exception e) {
                    log.warn("上传文件失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                }
            }

            return ApiResponse.successData("批量上传完成", results);
        } catch (Exception e) {
            log.error("批量上传失败: ", e);
            return ApiResponse.fail("批量上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除媒体文件", description = "删除指定的媒体文件")
    @DeleteMapping("/media/{id}")
    public ApiResponse<Object> deleteMedia(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumMedia media = forumMediaService.getById(id);
            if (media == null) {
                return ApiResponse.fail("媒体文件不存在");
            }

            if (!media.getUserId().equals(userId)) {
                return ApiResponse.fail("无权删除该文件");
            }

            media.setStatus(ForumConstants.Status.DISABLED);
            forumMediaService.updateById(media);

            try {
                minioUtil.deleteAvatar(media.getUrl());
            } catch (Exception e) {
                log.warn("删除MinIO文件失败: {}", e.getMessage());
            }

            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除媒体文件失败: ", e);
            return ApiResponse.fail("删除媒体文件失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取帖子的媒体列表", description = "获取指定帖子的所有媒体文件")
    @GetMapping("/posts/{postId}/media")
    public ApiResponse<List<ForumMedia>> getPostMedia(@PathVariable Long postId) {
        try {
            List<ForumMedia> mediaList = forumMediaService.lambdaQuery()
                    .eq(ForumMedia::getPostId, postId)
                    .eq(ForumMedia::getStatus, ForumConstants.Status.ENABLED)
                    .orderByAsc(ForumMedia::getSortOrder)
                    .list();

            return ApiResponse.successData(mediaList);
        } catch (Exception e) {
            log.error("获取媒体列表失败: ", e);
            return ApiResponse.fail("获取媒体列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取评论的媒体列表", description = "获取指定评论的所有媒体文件")
    @GetMapping("/comments/{commentId}/media")
    public ApiResponse<List<ForumMedia>> getCommentMedia(@PathVariable Long commentId) {
        try {
            List<ForumMedia> mediaList = forumMediaService.lambdaQuery()
                    .eq(ForumMedia::getCommentId, commentId)
                    .eq(ForumMedia::getStatus, ForumConstants.Status.ENABLED)
                    .orderByAsc(ForumMedia::getSortOrder)
                    .list();

            return ApiResponse.successData(mediaList);
        } catch (Exception e) {
            log.error("获取媒体列表失败: ", e);
            return ApiResponse.fail("获取媒体列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "调整媒体排序", description = "调整媒体文件的显示顺序")
    @PutMapping("/media/{id}/sort")
    public ApiResponse<Object> updateMediaSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumMedia media = forumMediaService.getById(id);
            if (media == null) {
                return ApiResponse.fail("媒体文件不存在");
            }

            if (!media.getUserId().equals(userId)) {
                return ApiResponse.fail("无权修改该文件");
            }

            media.setSortOrder(sortOrder);
            forumMediaService.updateById(media);

            return ApiResponse.success("排序调整成功");
        } catch (Exception e) {
            log.error("调整排序失败: ", e);
            return ApiResponse.fail("调整排序失败: " + e.getMessage());
        }
    }
}
