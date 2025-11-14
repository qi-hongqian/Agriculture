package com.rfeng.agri.model.entity.answerentity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description 
 * @Author  MXZ
 * @Date: 2025-11-14 11:48:26
 */

@Data
@TableName("user_point")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 积分表 ")
public class UserPoint {

  @Schema(description = "积分记录ID")
  private long id;
 

  @Schema(description = "用户ID（关联A_user库的user表）")
  private long userId;
 

  @Schema(description = "积分变动（正为增加，负为减少）")
  private long point;
 
  @Schema(description = "变动原因（如“答题正确+10分”“兑换商品-50分”）")
  private String reason;


  @Schema(description = "来源（如“答题”“论坛”“活动”）")
  private String source;


  @Schema(description = "变动后总积分")
  private long currentTotal;


  @Schema(description = "null")
  private java.sql.Date createTime;
 

}
