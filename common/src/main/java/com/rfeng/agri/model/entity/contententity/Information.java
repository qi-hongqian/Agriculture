package com.rfeng.agri.model.entity.contententity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description 
 * @Author  MXZ
 * @Date: 2025-11-14 11:49:04
 */

@Data
@TableName("information")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "资讯表")
public class Information {

  @Schema(description = "资讯ID")
  private long id;
 

  @Schema(description = "资讯标题")
  private String title;
 

  @Schema(description = "分类（如“种植技术”“政策法规”“市场行情”）")
  private String category;
 

  @Schema(description = "封面图URL")
  private String coverImage;
 

  @Schema(description = "资讯内容（富文本）")
  private String content;
 

  @Schema(description = "作者")
  private String author;
 

  @Schema(description = "阅读量")
  private long readCount;
 

  @Schema(description = "状态（0-草稿，1-发布，2-下架）")
  private long status;
 

  @Schema(description = "发布时间")
  private java.sql.Date publishTime;
 

  @Schema(description = "null")
  private java.sql.Date createTime;
 

  @Schema(description = "null")
  private java.sql.Date updateTime;
 

}
