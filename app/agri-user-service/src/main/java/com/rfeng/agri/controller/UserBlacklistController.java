package com.rfeng.agri.controller;

import com.rfeng.agri.constant.ForumConstants;
import com.rfeng.agri.model.entity.userentity.UserBlacklist;
import com.rfeng.agri.result.ApiResponse;
import com.rfeng.agri.service.UserBlacklistService;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "用户黑名单管理", description = "用户黑名单相关接口")
@RequestMapping("/api/users/blacklist")
public class UserBlacklistController {

    @Autowired
    private UserBlacklistService userBlacklistService;

    @Operation(summary = "拉黑用户", description = "将指定用户加入黑名单")
    @PostMapping
    public ApiResponse<Object> addBlacklist(@RequestBody UserBlacklist request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            if (request.getBlockedUserId() == null) {
                return ApiResponse.fail("被拉黑用户ID不能为空");
            }

            if (userId.equals(request.getBlockedUserId())) {
                return ApiResponse.fail("不能拉黑自己");
            }

            UserBlacklist existBlacklist = userBlacklistService.lambdaQuery()
                    .eq(UserBlacklist::getUserId, userId)
                    .eq(UserBlacklist::getBlockedUserId, request.getBlockedUserId())
                    .one();

            if (existBlacklist != null && existBlacklist.getStatus() == ForumConstants.Status.ENABLED) {
                return ApiResponse.fail("该用户已在黑名单中");
            }

            if (existBlacklist != null) {
                existBlacklist.setStatus(ForumConstants.Status.ENABLED);
                existBlacklist.setRelationType(request.getRelationType() != null ? request.getRelationType() : ForumConstants.BlacklistRelationType.ONE_WAY);
                existBlacklist.setBlockType(request.getBlockType() != null ? request.getBlockType() : ForumConstants.BlacklistBlockType.HIDE_CONTENT);
                existBlacklist.setRemark(request.getRemark());
                existBlacklist.setUpdateTime(LocalDateTime.now());
                userBlacklistService.updateById(existBlacklist);
            } else {
                UserBlacklist blacklist = new UserBlacklist();
                blacklist.setUserId(userId);
                blacklist.setBlockedUserId(request.getBlockedUserId());
                blacklist.setRelationType(request.getRelationType() != null ? request.getRelationType() : ForumConstants.BlacklistRelationType.ONE_WAY);
                blacklist.setBlockType(request.getBlockType() != null ? request.getBlockType() : ForumConstants.BlacklistBlockType.HIDE_CONTENT);
                blacklist.setRemark(request.getRemark());
                blacklist.setStatus(ForumConstants.Status.ENABLED);
                blacklist.setCreateTime(LocalDateTime.now());
                blacklist.setUpdateTime(LocalDateTime.now());
                userBlacklistService.save(blacklist);
            }

            return ApiResponse.success("拉黑成功");
        } catch (Exception e) {
            log.error("拉黑用户失败: ", e);
            return ApiResponse.fail("拉黑失败: " + e.getMessage());
        }
    }

    @Operation(summary = "取消拉黑", description = "将指定用户从黑名单中移除")
    @DeleteMapping("/{blockedUserId}")
    public ApiResponse<Object> removeBlacklist(@PathVariable Long blockedUserId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            UserBlacklist blacklist = userBlacklistService.lambdaQuery()
                    .eq(UserBlacklist::getUserId, userId)
                    .eq(UserBlacklist::getBlockedUserId, blockedUserId)
                    .one();

            if (blacklist == null) {
                return ApiResponse.fail("该用户不在黑名单中");
            }

            blacklist.setStatus(ForumConstants.Status.DISABLED);
            blacklist.setUpdateTime(LocalDateTime.now());
            userBlacklistService.updateById(blacklist);

            return ApiResponse.success("取消拉黑成功");
        } catch (Exception e) {
            log.error("取消拉黑失败: ", e);
            return ApiResponse.fail("取消拉黑失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取我的黑名单列表", description = "获取当前用户的黑名单列表")
    @GetMapping
    public ApiResponse<List<UserBlacklist>> getMyBlacklist() {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            List<UserBlacklist> blacklist = userBlacklistService.lambdaQuery()
                    .eq(UserBlacklist::getUserId, userId)
                    .eq(UserBlacklist::getStatus, ForumConstants.Status.ENABLED)
                    .orderByDesc(UserBlacklist::getCreateTime)
                    .list();

            return ApiResponse.successData(blacklist);
        } catch (Exception e) {
            log.error("获取黑名单列表失败: ", e);
            return ApiResponse.fail("获取黑名单列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查是否拉黑某用户", description = "检查当前用户是否拉黑了指定用户")
    @GetMapping("/check-blocked/{targetUserId}")
    public ApiResponse<Boolean> checkBlocked(@PathVariable Long targetUserId) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return ApiResponse.fail("请先登录");
            }

            long count = userBlacklistService.lambdaQuery()
                    .eq(UserBlacklist::getUserId, userId)
                    .eq(UserBlacklist::getBlockedUserId, targetUserId)
                    .eq(UserBlacklist::getStatus, ForumConstants.Status.ENABLED)
                    .count();

            return ApiResponse.successData(count > 0);
        } catch (Exception e) {
            log.error("检查拉黑状态失败: ", e);
            return ApiResponse.fail("检查拉黑状态失败: " + e.getMessage());
        }
    }
}
