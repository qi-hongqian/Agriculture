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
@TableName("forum_post")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "论坛帖子表")
public class ForumPost {

  @TableId
  @Schema(description = "帖子ID")
  private Long id;

  @Schema(description = "发布用户ID")
  private Long userId;

  @Schema(description = "帖子标题")
  private String title;

  @Schema(description = "帖子内容")
  private String content;

  @Schema(description = "分类ID（关联分类表）")
  private Integer categoryId;

  @Schema(description = "分类名称（冗余字段）")
  private String categoryName;

  @Schema(description = "浏览量")
  private Integer viewCount;

  @Schema(description = "点赞数")
  private Integer likeCount;

  @Schema(description = "收藏数")
  private Integer collectCount;

  @Schema(description = "评论数")
  private Integer commentCount;

  @Schema(description = "是否置顶（0-否，1-是）")
  private Integer isTop;

  @Schema(description = "是否精华（0-否，1-是）")
  private Integer isEssence;

  @Schema(description = "是否推荐（0-否，1-是）")
  private Integer isRecommend;

  @Schema(description = "审核状态（0-待审核，1-已通过，2-审核拒绝）")
  private Integer status;

  @Schema(description = "可见状态（0-删除，1-正常，2-作者隐藏，3-管理员隐藏）")
  private Integer visibleStatus;

  @Schema(description = "是否锁定（0-否，1-是，锁定后不能回复）")
  private Integer isLocked;

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

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

}