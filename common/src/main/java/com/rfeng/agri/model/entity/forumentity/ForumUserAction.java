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
@TableName("forum_user_action")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "用户互动记录表")
public class ForumUserAction {

    @TableId
    @Schema(description = "互动ID")
    private Long id;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "操作类型（1-点赞帖子，2-收藏帖子，3-点赞评论）")
    private Integer actionType;

    @Schema(description = "操作时间")
    private LocalDateTime actionTime;
}