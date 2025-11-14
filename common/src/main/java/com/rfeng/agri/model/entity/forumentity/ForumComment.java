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
@TableName("forum_comment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 论坛评论表 ")
public class ForumComment {

  @Schema(description = "评论ID")
  private long id;
 

  @Schema(description = "关联帖子ID")
  private long postId;
 

  @Schema(description = "评论用户ID（关联A_user库的user表）")
  private long userId;
 

  @Schema(description = "评论内容")
  private String content;
 

  @Schema(description = "父评论ID（用于回复功能，0为一级评论）")
  private long parentId;
 

  @Schema(description = "状态（0-删除，1-正常）")
  private long status;
 

  @Schema(description = "null")
  private java.sql.Date createTime;
 

  @Schema(description = "null")
  private java.sql.Date updateTime;
 

}
