package com.rfeng.agri.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfeng.agri.constant.ForumConstants;
import com.rfeng.agri.model.entity.forumentity.ForumPost;
import com.rfeng.agri.model.entity.forumentity.ForumPostPermission;
import com.rfeng.agri.model.entity.forumentity.ForumUserAction;
import com.rfeng.agri.result.ApiResponse;
import com.rfeng.agri.service.*;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
@Tag(name = "论坛帖子管理", description = "帖子发布、查询、互动相关接口")
@RequestMapping("/api/forum/posts")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumPostPermissionService forumPostPermissionService;

    @Autowired
    private ForumUserActionService forumUserActionService;

    @Autowired
    private ForumCategoryService forumCategoryService;

    @Autowired
    private com.rfeng.agri.util.MinioUtil minioUtil;

    @Autowired
    private com.rfeng.agri.service.ForumMediaService forumMediaService;

    @Operation(summary = "发布新帖子", description = "用户发布新帖子")
    @PostMapping
    public ApiResponse<ForumPost> createPost(@RequestBody Map<String, Object> request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            String title = (String) request.get("title");
            String content = (String) request.get("content");
            Integer categoryId = (Integer) request.get("categoryId");
            String categoryName = (String) request.get("categoryName");
            List<String> tempImageUrls = (List<String>) request.get("imageUrls");

            if (title == null || title.trim().isEmpty()) {
                return ApiResponse.fail("帖子标题不能为空");
            }

            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.fail("帖子内容不能为空");
            }

            if (content.length() > 400) {
                return ApiResponse.fail("帖子内容不能超过400个字");
            }

            ForumPost post = new ForumPost();
            post.setUserId(userId);
            post.setTitle(title);
            post.setContent(content);
            post.setCategoryId(categoryId);
            post.setCategoryName(categoryName);
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCollectCount(0);
            post.setCommentCount(0);
            post.setIsTop(ForumConstants.Flag.NO);
            post.setIsEssence(ForumConstants.Flag.NO);
            post.setIsRecommend(ForumConstants.Flag.NO);
            post.setStatus(ForumConstants.AuditStatus.APPROVED);
            post.setVisibleStatus(ForumConstants.VisibleStatus.NORMAL);
            post.setIsLocked(ForumConstants.Flag.NO);
            post.setIsDeleted(ForumConstants.Flag.NO);
            post.setCreateTime(LocalDateTime.now());
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.save(post);

            if (tempImageUrls != null && !tempImageUrls.isEmpty()) {
                List<String> formalUrls = minioUtil.batchRenameTempForumImagesToFormal(tempImageUrls, userId, post.getId());
                
                int sortOrder = 0;
                for (String formalUrl : formalUrls) {
                    com.rfeng.agri.model.entity.forumentity.ForumMedia media = new com.rfeng.agri.model.entity.forumentity.ForumMedia();
                    media.setPostId(post.getId());
                    media.setUserId(userId);
                    media.setType(ForumConstants.MediaType.IMAGE);
                    media.setUrl(formalUrl);
                    media.setSortOrder(sortOrder++);
                    media.setStatus(ForumConstants.Status.ENABLED);
                    media.setCreateTime(LocalDateTime.now());
                    forumMediaService.save(media);
                }
            }

            return ApiResponse.successData("发布成功", post);
        } catch (Exception e) {
            log.error("发布帖子失败: ", e);
            return ApiResponse.fail("发布帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "编辑帖子", description = "编辑已发布的帖子")
    @PutMapping("/{id}")
    public ApiResponse<Object> updatePost(@PathVariable Long id, @RequestBody ForumPost post) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost existPost = forumPostService.getById(id);
            if (existPost == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!existPost.getUserId().equals(userId)) {
                return ApiResponse.fail("无权编辑该帖子");
            }

            if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
                return ApiResponse.fail("帖子标题不能为空");
            }

            if (post.getTitle().length() > 50) {
                return ApiResponse.fail("帖子标题不能超过50个字");
            }

            if (post.getContent() == null || post.getContent().trim().isEmpty()) {
                return ApiResponse.fail("帖子内容不能为空");
            }

            if (post.getContent().length() > 400) {
                return ApiResponse.fail("帖子内容不能超过400个字");
            }

            existPost.setTitle(post.getTitle());
            existPost.setContent(post.getContent());
            existPost.setCategoryId(post.getCategoryId());
            existPost.setCategoryName(post.getCategoryName());
            existPost.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(existPost);

            return ApiResponse.success("编辑成功");
        } catch (Exception e) {
            log.error("编辑帖子失败: ", e);
            return ApiResponse.fail("编辑帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除帖子", description = "软删除帖子")
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deletePost(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权删除该帖子");
            }

            post.setIsDeleted(ForumConstants.Flag.YES);
            post.setVisibleStatus(ForumConstants.VisibleStatus.DELETED);
            post.setDeleteTime(LocalDateTime.now());
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(post);

            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除帖子失败: ", e);
            return ApiResponse.fail("删除帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "隐藏帖子", description = "作者隐藏自己的帖子")
    @PutMapping("/{id}/hide")
    public ApiResponse<Object> hidePost(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权隐藏该帖子");
            }

            post.setVisibleStatus(ForumConstants.VisibleStatus.AUTHOR_HIDDEN);
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(post);

            return ApiResponse.success("隐藏成功");
        } catch (Exception e) {
            log.error("隐藏帖子失败: ", e);
            return ApiResponse.fail("隐藏帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "恢复帖子", description = "恢复已删除或隐藏的帖子")
    @PutMapping("/{id}/recover")
    public ApiResponse<Object> recoverPost(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权恢复该帖子");
            }

            post.setIsDeleted(ForumConstants.Flag.NO);
            post.setVisibleStatus(ForumConstants.VisibleStatus.NORMAL);
            post.setDeleteTime(null);
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(post);

            return ApiResponse.success("恢复成功");
        } catch (Exception e) {
            log.error("恢复帖子失败: ", e);
            return ApiResponse.fail("恢复帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "置顶/取消置顶", description = "设置帖子置顶状态")
    @PutMapping("/{id}/top")
    public ApiResponse<Object> toggleTop(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            post.setIsTop(post.getIsTop() == ForumConstants.Flag.YES ? ForumConstants.Flag.NO : ForumConstants.Flag.YES);
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(post);

            return ApiResponse.success(post.getIsTop() == ForumConstants.Flag.YES ? "置顶成功" : "取消置顶成功");
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return ApiResponse.fail("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "设精华/取消精华", description = "设置帖子精华状态")
    @PutMapping("/{id}/essence")
    public ApiResponse<Object> toggleEssence(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            post.setIsEssence(post.getIsEssence() == ForumConstants.Flag.YES ? ForumConstants.Flag.NO : ForumConstants.Flag.YES);
            post.setUpdateTime(LocalDateTime.now());

            forumPostService.updateById(post);

            return ApiResponse.success(post.getIsEssence() == ForumConstants.Flag.YES ? "设为精华成功" : "取消精华成功");
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return ApiResponse.fail("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取帖子详情", description = "获取帖子详细信息")
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getPostById(@PathVariable Long id) {
        try {
            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            List<com.rfeng.agri.model.entity.forumentity.ForumMedia> mediaList = forumMediaService.lambdaQuery()
                    .eq(com.rfeng.agri.model.entity.forumentity.ForumMedia::getPostId, id)
                    .eq(com.rfeng.agri.model.entity.forumentity.ForumMedia::getStatus, ForumConstants.Status.ENABLED)
                    .orderByAsc(com.rfeng.agri.model.entity.forumentity.ForumMedia::getSortOrder)
                    .list();

            Map<String, Object> result = new HashMap<>();
            result.put("post", post);
            result.put("mediaList", mediaList);

            return ApiResponse.successData(result);
        } catch (Exception e) {
            log.error("获取帖子详情失败: ", e);
            return ApiResponse.fail("获取帖子详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "帖子列表", description = "获取帖子列表，支持分页和筛选")
    @GetMapping
    public ApiResponse<Page<ForumPost>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long userId) {
        try {
            Page<ForumPost> pageParam = new Page<>(page, size);

            var query = forumPostService.lambdaQuery()
                    .eq(ForumPost::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumPost::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL);

            if (categoryId != null) {
                query.eq(ForumPost::getCategoryId, categoryId);
            }

            if (userId != null) {
                query.eq(ForumPost::getUserId, userId);
            }

            if (ForumConstants.SortType.HOT.equals(sort)) {
                query.orderByDesc(ForumPost::getLikeCount);
            } else if (ForumConstants.SortType.TOP.equals(sort)) {
                query.orderByDesc(ForumPost::getIsTop).orderByDesc(ForumPost::getCreateTime);
            } else {
                query.orderByDesc(ForumPost::getCreateTime);
            }

            Page<ForumPost> result = query.page(pageParam);

            return ApiResponse.successData(result);
        } catch (Exception e) {
            log.error("获取帖子列表失败: ", e);
            return ApiResponse.fail("获取帖子列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索帖子", description = "根据标题和内容搜索帖子")
    @GetMapping("/search")
    public ApiResponse<Page<ForumPost>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<ForumPost> pageParam = new Page<>(page, size);

            Page<ForumPost> result = forumPostService.lambdaQuery()
                    .eq(ForumPost::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumPost::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL)
                    .eq(ForumPost::getStatus, ForumConstants.AuditStatus.APPROVED)
                    .and(wrapper -> wrapper
                            .like(ForumPost::getTitle, keyword)
                            .or()
                            .like(ForumPost::getContent, keyword))
                    .orderByDesc(ForumPost::getCreateTime)
                    .page(pageParam);

            return ApiResponse.successData(result);
        } catch (Exception e) {
            log.error("搜索帖子失败: ", e);
            return ApiResponse.fail("搜索帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取相似帖子", description = "获取同分类的相似帖子")
    @GetMapping("/{id}/similar")
    public ApiResponse<List<ForumPost>> getSimilarPosts(@PathVariable Long id) {
        try {
            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            List<ForumPost> similarPosts = forumPostService.lambdaQuery()
                    .eq(ForumPost::getCategoryId, post.getCategoryId())
                    .eq(ForumPost::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumPost::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL)
                    .eq(ForumPost::getStatus, ForumConstants.AuditStatus.APPROVED)
                    .ne(ForumPost::getId, id)
                    .orderByDesc(ForumPost::getCreateTime)
                    .last("LIMIT 10")
                    .list();

            return ApiResponse.successData(similarPosts);
        } catch (Exception e) {
            log.error("获取相似帖子失败: ", e);
            return ApiResponse.fail("获取相似帖子失败: " + e.getMessage());
        }
    }

    @Operation(summary = "增加浏览量", description = "记录帖子浏览")
    @PostMapping("/{id}/view")
    public ApiResponse<Object> increaseViewCount(@PathVariable Long id) {
        try {
            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            post.setViewCount(post.getViewCount() + 1);
            forumPostService.updateById(post);

            return ApiResponse.success("浏览量+1");
        } catch (Exception e) {
            log.error("增加浏览量失败: ", e);
            return ApiResponse.fail("增加浏览量失败: " + e.getMessage());
        }
    }

    @Operation(summary = "点赞/取消点赞帖子", description = "对帖子进行点赞或取消点赞")
    @PostMapping("/{id}/like")
    public ApiResponse<Object> toggleLikePost(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            ForumUserAction existAction = forumUserActionService.lambdaQuery()
                    .eq(ForumUserAction::getUserId, userId)
                    .eq(ForumUserAction::getPostId, id)
                    .eq(ForumUserAction::getActionType, ForumConstants.ActionType.LIKE_POST)
                    .one();

            if (existAction != null) {
                forumUserActionService.removeById(existAction);
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                forumPostService.updateById(post);
                return ApiResponse.success("取消点赞成功");
            } else {
                ForumUserAction action = new ForumUserAction();
                action.setUserId(userId);
                action.setPostId(id);
                action.setActionType(ForumConstants.ActionType.LIKE_POST);
                action.setActionTime(LocalDateTime.now());
                forumUserActionService.save(action);

                post.setLikeCount(post.getLikeCount() + 1);
                forumPostService.updateById(post);
                return ApiResponse.success("点赞成功");
            }
        } catch (Exception e) {
            log.error("点赞操作失败: ", e);
            return ApiResponse.fail("点赞操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "收藏/取消收藏帖子", description = "对帖子进行收藏或取消收藏")
    @PostMapping("/{id}/collect")
    public ApiResponse<Object> toggleCollectPost(@PathVariable Long id) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            ForumUserAction existAction = forumUserActionService.lambdaQuery()
                    .eq(ForumUserAction::getUserId, userId)
                    .eq(ForumUserAction::getPostId, id)
                    .eq(ForumUserAction::getActionType, ForumConstants.ActionType.COLLECT_POST)
                    .one();

            if (existAction != null) {
                forumUserActionService.removeById(existAction);
                post.setCollectCount(Math.max(0, post.getCollectCount() - 1));
                forumPostService.updateById(post);
                return ApiResponse.success("取消收藏成功");
            } else {
                ForumUserAction action = new ForumUserAction();
                action.setUserId(userId);
                action.setPostId(id);
                action.setActionType(ForumConstants.ActionType.COLLECT_POST);
                action.setActionTime(LocalDateTime.now());
                forumUserActionService.save(action);

                post.setCollectCount(post.getCollectCount() + 1);
                forumPostService.updateById(post);
                return ApiResponse.success("收藏成功");
            }
        } catch (Exception e) {
            log.error("收藏操作失败: ", e);
            return ApiResponse.fail("收藏操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "禁止用户评论", description = "帖子作者禁止指定用户评论")
    @PostMapping("/{id}/permissions/block")
    public ApiResponse<Object> blockUserComment(@PathVariable Long id, @RequestBody ForumPostPermission request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权设置该帖子的权限");
            }

            if (request.getUserId() == null) {
                return ApiResponse.fail("被禁言用户ID不能为空");
            }

            ForumPostPermission permission = new ForumPostPermission();
            permission.setPostId(id);
            permission.setUserId(request.getUserId());
            permission.setPermissionType(ForumConstants.PermissionType.BLOCK_COMMENT);
            permission.setCreatedBy(userId);
            permission.setExpireTime(request.getExpireTime());
            permission.setStatus(ForumConstants.Status.ENABLED);
            permission.setCreateTime(LocalDateTime.now());
            permission.setUpdateTime(LocalDateTime.now());

            forumPostPermissionService.save(permission);

            return ApiResponse.success("禁言成功");
        } catch (Exception e) {
            log.error("禁言失败: ", e);
            return ApiResponse.fail("禁言失败: " + e.getMessage());
        }
    }

    @Operation(summary = "取消禁言", description = "取消对指定用户的评论禁止")
    @DeleteMapping("/{id}/permissions/block/{targetUserId}")
    public ApiResponse<Object> unblockUserComment(@PathVariable Long id, @PathVariable Long targetUserId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(id);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权设置该帖子的权限");
            }

            ForumPostPermission permission = forumPostPermissionService.lambdaQuery()
                    .eq(ForumPostPermission::getPostId, id)
                    .eq(ForumPostPermission::getUserId, targetUserId)
                    .eq(ForumPostPermission::getPermissionType, ForumConstants.PermissionType.BLOCK_COMMENT)
                    .one();

            if (permission != null) {
                permission.setStatus(ForumConstants.Status.DISABLED);
                permission.setUpdateTime(LocalDateTime.now());
                forumPostPermissionService.updateById(permission);
            }

            return ApiResponse.success("取消禁言成功");
        } catch (Exception e) {
            log.error("取消禁言失败: ", e);
            return ApiResponse.fail("取消禁言失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取被禁言用户列表", description = "获取帖子的被禁言用户列表")
    @GetMapping("/{id}/permissions/blocked-users")
    public ApiResponse<List<ForumPostPermission>> getBlockedUsers(@PathVariable Long id) {
        try {
            List<ForumPostPermission> permissions = forumPostPermissionService.lambdaQuery()
                    .eq(ForumPostPermission::getPostId, id)
                    .eq(ForumPostPermission::getPermissionType, ForumConstants.PermissionType.BLOCK_COMMENT)
                    .eq(ForumPostPermission::getStatus, ForumConstants.Status.ENABLED)
                    .list();

            return ApiResponse.successData(permissions);
        } catch (Exception e) {
            log.error("获取禁言列表失败: ", e);
            return ApiResponse.fail("获取禁言列表失败: " + e.getMessage());
        }
    }
}
