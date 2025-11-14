package com.rfeng.agri.model.entity.forumentity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description 
 * @Author  MXZ
 * @Date: 2025-11-14 11:49:37
 */

@Data
@TableName("forum_post")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 论坛帖子表 ")
public class ForumPost {

  @Schema(description = "帖子ID")
  private long id;
 

  @Schema(description = "发布用户ID（关联A_user库的user表）")
  private long userId;
 

  @Schema(description = "帖子标题")
  private String title;
 

  @Schema(description = "帖子内容")
  private String content;
 

  @Schema(description = "帖子分类（如“种植问题”“经验分享”）")
  private String category;
 

  @Schema(description = "浏览量")
  private long viewCount;
 

  @Schema(description = "评论数")
  private long commentCount;
 

  @Schema(description = "是否置顶（0-否，1-是）")
  private long isTop;
 

  @Schema(description = "状态（0-删除，1-正常，2-禁言）")
  private long status;


  @Schema(description = "null")
  private java.sql.Date createTime;


  @Schema(description = "null")
  private java.sql.Date updateTime;
 

}
