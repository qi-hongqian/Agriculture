package com.rfeng.agri.controller;

import com.rfeng.agri.constant.ForumConstants;
import com.rfeng.agri.model.entity.forumentity.ForumCategory;
import com.rfeng.agri.result.ApiResponse;
import com.rfeng.agri.service.ForumCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
@Slf4j
@RestController
@Tag(name = "论坛分类管理", description = "论坛分类相关接口")
@RequestMapping("/api/forum/categories")
public class ForumCategoryController {

    @Autowired
    private ForumCategoryService forumCategoryService;

    @Operation(summary = "获取全部分类列表", description = "获取所有启用的分类列表")
    @GetMapping
    public ApiResponse<List<ForumCategory>> getAllCategories() {
        try {
            List<ForumCategory> categories = forumCategoryService.lambdaQuery()
                    .eq(ForumCategory::getStatus, ForumConstants.Status.ENABLED)
                    .orderByAsc(ForumCategory::getSortOrder)
                    .list();

            return ApiResponse.successData(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败: ", e);
            return ApiResponse.fail("获取分类列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息")
    @GetMapping("/{id}")
    public ApiResponse<ForumCategory> getCategoryById(@PathVariable Integer id) {
        try {
            ForumCategory category = forumCategoryService.getById(id);
            if (category == null) {
                return ApiResponse.fail("分类不存在");
            }

            return ApiResponse.successData(category);
        } catch (Exception e) {
            log.error("获取分类详情失败: ", e);
            return ApiResponse.fail("获取分类详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取热门分类", description = "按帖子数量排序获取热门分类")
    @GetMapping("/hot")
    public ApiResponse<List<ForumCategory>> getHotCategories() {
        try {
            List<ForumCategory> categories = forumCategoryService.lambdaQuery()
                    .eq(ForumCategory::getStatus, ForumConstants.Status.ENABLED)
                    .orderByDesc(ForumCategory::getPostCount)
                    .last("LIMIT 10")
                    .list();

            return ApiResponse.successData(categories);
        } catch (Exception e) {
            log.error("获取热门分类失败: ", e);
            return ApiResponse.fail("获取热门分类失败: " + e.getMessage());
        }
    }
}
