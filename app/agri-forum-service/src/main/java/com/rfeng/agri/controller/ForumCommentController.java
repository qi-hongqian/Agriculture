package com.rfeng.agri.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfeng.agri.constant.ForumConstants;
import com.rfeng.agri.model.entity.forumentity.ForumComment;
import com.rfeng.agri.model.entity.forumentity.ForumPost;
import com.rfeng.agri.model.entity.forumentity.ForumUserAction;
import com.rfeng.agri.result.ApiResponse;
import com.rfeng.agri.service.ForumCommentService;
import com.rfeng.agri.service.ForumPostService;
import com.rfeng.agri.service.ForumUserActionService;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
@Slf4j
@RestController
@Tag(name = "论坛评论管理", description = "评论发布、查询、互动相关接口")
@RequestMapping("/api/forum")
public class ForumCommentController {

    @Autowired
    private ForumCommentService forumCommentService;

    @Autowired
    private ForumUserActionService forumUserActionService;

    @Autowired
    private ForumPostService forumPostService;

    @Operation(summary = "发布评论", description = "对帖子发表一级评论")
    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<ForumComment> createComment(@PathVariable Long postId, @RequestBody ForumComment comment) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(postId);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ApiResponse.fail("评论内容不能为空");
            }

            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setParentId(0L);
            comment.setLevel(ForumConstants.CommentLevel.LEVEL_1);
            comment.setPath("0/");
            comment.setLikeCount(0);
            comment.setStatus(ForumConstants.AuditStatus.PENDING);
            comment.setVisibleStatus(ForumConstants.VisibleStatus.NORMAL);
            comment.setIsDeleted(ForumConstants.Flag.NO);
            comment.setCreateTime(LocalDateTime.now());
            comment.setUpdateTime(LocalDateTime.now());

            forumCommentService.save(comment);

            post.setCommentCount(post.getCommentCount() + 1);
            forumPostService.updateById(post);

            return ApiResponse.successData("评论成功", comment);
        } catch (Exception e) {
            log.error("发布评论失败: ", e);
            return ApiResponse.fail("发布评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "回复评论", description = "对评论进行回复")
    @PostMapping("/comments/{commentId}/reply")
    public ApiResponse<ForumComment> replyComment(@PathVariable Long commentId, @RequestBody ForumComment reply) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumComment parentComment = forumCommentService.getById(commentId);
            if (parentComment == null) {
                return ApiResponse.fail("评论不存在");
            }

            if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
                return ApiResponse.fail("回复内容不能为空");
            }

            reply.setPostId(parentComment.getPostId());
            reply.setUserId(userId);
            reply.setParentId(commentId);
            reply.setReplyToUserId(parentComment.getUserId());
            reply.setReplyToCommentId(commentId);
            reply.setLevel(ForumConstants.CommentLevel.LEVEL_2);
            reply.setPath(parentComment.getPath() + commentId + "/");
            reply.setLikeCount(0);
            reply.setStatus(ForumConstants.AuditStatus.PENDING);
            reply.setVisibleStatus(ForumConstants.VisibleStatus.NORMAL);
            reply.setIsDeleted(ForumConstants.Flag.NO);
            reply.setCreateTime(LocalDateTime.now());
            reply.setUpdateTime(LocalDateTime.now());

            forumCommentService.save(reply);

            ForumPost post = forumPostService.getById(parentComment.getPostId());
            if (post != null) {
                post.setCommentCount(post.getCommentCount() + 1);
                forumPostService.updateById(post);
            }

            return ApiResponse.successData("回复成功", reply);
        } catch (Exception e) {
            log.error("回复评论失败: ", e);
            return ApiResponse.fail("回复评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除评论", description = "用户删除自己的评论")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Object> deleteComment(@PathVariable Long commentId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumComment comment = forumCommentService.getById(commentId);
            if (comment == null) {
                return ApiResponse.fail("评论不存在");
            }

            if (!comment.getUserId().equals(userId)) {
                return ApiResponse.fail("无权删除该评论");
            }

            comment.setIsDeleted(ForumConstants.Flag.YES);
            comment.setVisibleStatus(ForumConstants.VisibleStatus.DELETED);
            comment.setDeletedByUserId(userId);
            comment.setDeleteTime(LocalDateTime.now());
            comment.setUpdateTime(LocalDateTime.now());

            forumCommentService.updateById(comment);

            ForumPost post = forumPostService.getById(comment.getPostId());
            if (post != null) {
                post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                forumPostService.updateById(post);
            }

            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除评论失败: ", e);
            return ApiResponse.fail("删除评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "帖子作者删除评论", description = "帖子作者删除帖子下的评论")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ApiResponse<Object> deleteCommentByPostOwner(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumPost post = forumPostService.getById(postId);
            if (post == null) {
                return ApiResponse.fail("帖子不存在");
            }

            if (!post.getUserId().equals(userId)) {
                return ApiResponse.fail("无权删除该评论");
            }

            ForumComment comment = forumCommentService.getById(commentId);
            if (comment == null) {
                return ApiResponse.fail("评论不存在");
            }

            comment.setIsDeleted(ForumConstants.Flag.YES);
            comment.setVisibleStatus(ForumConstants.VisibleStatus.DELETED);
            comment.setDeletedByPostOwner(ForumConstants.Flag.YES);
            comment.setDeleteTime(LocalDateTime.now());
            comment.setUpdateTime(LocalDateTime.now());

            forumCommentService.updateById(comment);

            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            forumPostService.updateById(post);

            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除评论失败: ", e);
            return ApiResponse.fail("删除评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "编辑评论", description = "编辑已发布的评论")
    @PutMapping("/comments/{commentId}")
    public ApiResponse<Object> updateComment(@PathVariable Long commentId, @RequestBody ForumComment comment) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumComment existComment = forumCommentService.getById(commentId);
            if (existComment == null) {
                return ApiResponse.fail("评论不存在");
            }

            if (!existComment.getUserId().equals(userId)) {
                return ApiResponse.fail("无权编辑该评论");
            }

            existComment.setContent(comment.getContent());
            existComment.setUpdateTime(LocalDateTime.now());

            forumCommentService.updateById(existComment);

            return ApiResponse.success("编辑成功");
        } catch (Exception e) {
            log.error("编辑评论失败: ", e);
            return ApiResponse.fail("编辑评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取帖子评论列表", description = "获取帖子的评论列表（树形结构）")
    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<Page<ForumComment>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort) {
        try {
            Page<ForumComment> pageParam = new Page<>(page, size);

            var query = forumCommentService.lambdaQuery()
                    .eq(ForumComment::getPostId, postId)
                    .eq(ForumComment::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumComment::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL)
                    .eq(ForumComment::getStatus, ForumConstants.AuditStatus.APPROVED);

            if (ForumConstants.SortType.HOT.equals(sort)) {
                query.orderByDesc(ForumComment::getLikeCount);
            } else {
                query.orderByDesc(ForumComment::getCreateTime);
            }

            Page<ForumComment> result = query.page(pageParam);

            return ApiResponse.successData(result);
        } catch (Exception e) {
            log.error("获取评论列表失败: ", e);
            return ApiResponse.fail("获取评论列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取评论的回复列表", description = "获取指定评论的所有回复")
    @GetMapping("/comments/{commentId}/replies")
    public ApiResponse<List<ForumComment>> getCommentReplies(@PathVariable Long commentId) {
        try {
            List<ForumComment> replies = forumCommentService.lambdaQuery()
                    .eq(ForumComment::getParentId, commentId)
                    .eq(ForumComment::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumComment::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL)
                    .eq(ForumComment::getStatus, ForumConstants.AuditStatus.APPROVED)
                    .orderByAsc(ForumComment::getCreateTime)
                    .list();

            return ApiResponse.successData(replies);
        } catch (Exception e) {
            log.error("获取回复列表失败: ", e);
            return ApiResponse.fail("获取回复列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户的所有评论", description = "获取指定用户的所有评论")
    @GetMapping("/users/{userId}/comments")
    public ApiResponse<Page<ForumComment>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<ForumComment> pageParam = new Page<>(page, size);

            Page<ForumComment> result = forumCommentService.lambdaQuery()
                    .eq(ForumComment::getUserId, userId)
                    .eq(ForumComment::getIsDeleted, ForumConstants.Flag.NO)
                    .eq(ForumComment::getVisibleStatus, ForumConstants.VisibleStatus.NORMAL)
                    .orderByDesc(ForumComment::getCreateTime)
                    .page(pageParam);

            return ApiResponse.successData(result);
        } catch (Exception e) {
            log.error("获取用户评论失败: ", e);
            return ApiResponse.fail("获取用户评论失败: " + e.getMessage());
        }
    }

    @Operation(summary = "点赞/取消点赞评论", description = "对评论进行点赞或取消点赞")
    @PostMapping("/comments/{commentId}/like")
    public ApiResponse<Object> toggleLikeComment(@PathVariable Long commentId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            ForumComment comment = forumCommentService.getById(commentId);
            if (comment == null) {
                return ApiResponse.fail("评论不存在");
            }

            ForumUserAction existAction = forumUserActionService.lambdaQuery()
                    .eq(ForumUserAction::getUserId, userId)
                    .eq(ForumUserAction::getCommentId, commentId)
                    .eq(ForumUserAction::getActionType, ForumConstants.ActionType.LIKE_COMMENT)
                    .one();

            if (existAction != null) {
                forumUserActionService.removeById(existAction);
                comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
                forumCommentService.updateById(comment);
                return ApiResponse.success("取消点赞成功");
            } else {
                ForumUserAction action = new ForumUserAction();
                action.setUserId(userId);
                action.setCommentId(commentId);
                action.setActionType(ForumConstants.ActionType.LIKE_COMMENT);
                action.setActionTime(LocalDateTime.now());
                forumUserActionService.save(action);

                comment.setLikeCount(comment.getLikeCount() + 1);
                forumCommentService.updateById(comment);
                return ApiResponse.success("点赞成功");
            }
        } catch (Exception e) {
            log.error("点赞操作失败: ", e);
            return ApiResponse.fail("点赞操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取点赞用户列表", description = "获取给评论点赞的用户列表")
    @GetMapping("/comments/{commentId}/likers")
    public ApiResponse<List<ForumUserAction>> getCommentLikers(@PathVariable Long commentId) {
        try {
            List<ForumUserAction> likers = forumUserActionService.lambdaQuery()
                    .eq(ForumUserAction::getCommentId, commentId)
                    .eq(ForumUserAction::getActionType, ForumConstants.ActionType.LIKE_COMMENT)
                    .orderByDesc(ForumUserAction::getActionTime)
                    .list();

            return ApiResponse.successData(likers);
        } catch (Exception e) {
            log.error("获取点赞列表失败: ", e);
            return ApiResponse.fail("获取点赞列表失败: " + e.getMessage());
        }
    }
}
