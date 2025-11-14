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
@TableName("user_point_summary")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 用户积分汇总表 ")
public class UserPointSummary {

  @Schema(description = "用户ID")
  private long userId;


  @Schema(description = "总积分")
  private long totalPoint;


  @Schema(description = "null")
  private java.sql.Date updateTime;
 

}
