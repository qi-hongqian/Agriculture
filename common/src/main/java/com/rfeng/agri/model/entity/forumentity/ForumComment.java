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
@TableName("forum_comment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "论坛评论表")
public class ForumComment {

  @TableId
  @Schema(description = "评论ID")
  private Long id;

  @Schema(description = "关联帖子ID")
  private Long postId;

  @Schema(description = "评论用户ID")
  private Long userId;

  @Schema(description = "评论内容")
  private String content;

  @Schema(description = "父评论ID（0为一级评论）")
  private Long parentId;

  @Schema(description = "回复给哪个用户ID")
  private Long replyToUserId;

  @Schema(description = "回复给哪个评论ID")
  private Long replyToCommentId;

  @Schema(description = "评论层级（1-一级，2-二级）")
  private Integer level;

  @Schema(description = "评论路径（格式：0/1/2）用于快速查询子树")
  private String path;

  @Schema(description = "点赞数")
  private Integer likeCount;

  @Schema(description = "审核状态（0-待审核，1-已通过，2-审核拒绝）")
  private Integer status;

  @Schema(description = "可见状态（0-删除，1-正常，2-作者隐藏，3-管理员隐藏）")
  private Integer visibleStatus;

  @Schema(description = "审核管理员ID")
  private Long auditUserId;

  @Schema(description = "审核时间")
  private LocalDateTime auditTime;

  @Schema(description = "审核意见/拒绝原因")
  private String auditReason;

  @Schema(description = "是否删除（0-否，1-是）")
  private Integer isDeleted;

  @Schema(description = "删除时间")
  private LocalDateTime deleteTime;

  @Schema(description = "删除人ID（如果是用户自己删除）")
  private Long deletedByUserId;

  @Schema(description = "是否被帖子作者删除（0-否，1-是）")
  private Integer deletedByPostOwner;

  @Schema(description = "删除原因")
  private String deleteReason;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}
