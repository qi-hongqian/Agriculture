package com.rfeng.agri.model.entity.forumentity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
@Data
@TableName("forum_post_permission")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "帖子用户权限表")
public class ForumPostPermission {

    @TableId
    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "被限制的用户ID")
    private Long userId;

    @Schema(description = "权限类型：1-禁止评论，2-只看此人评论")
    private Integer permissionType;

    @Schema(description = "设置人ID（通常是发帖人）")
    private Long createdBy;

    @Schema(description = "过期时间（NULL为永久）")
    private LocalDateTime expireTime;

    @Schema(description = "状态（0-已取消，1-生效中）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}