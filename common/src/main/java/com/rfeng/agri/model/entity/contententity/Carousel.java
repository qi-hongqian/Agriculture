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
@TableName("carousel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 轮播图表 ")
public class Carousel {

  @Schema(description = "轮播图ID")
  private long id;
 

  @Schema(description = "轮播图标题")
  private String title;
 

  @Schema(description = "图片URL（存储在MinIO/OSS）")
  private String imageUrl;
 

  @Schema(description  = "点击跳转链接（如资讯详情）")
  private String linkUrl;
 

  @Schema(description = "排序（数字越小越靠前）")
  private long sort;
 

  @Schema(description = "状态（0-禁用，1-启用）")
  private long status;
 

  @Schema(description = "创建时间")
  private java.sql.Date createTime;
 

  @Schema(description = "更新时间")
  private java.sql.Date updateTime;
 

}
