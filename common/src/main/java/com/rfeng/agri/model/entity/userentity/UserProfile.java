package com.rfeng.agri.model.entity.userentity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description 
 * @Author  MXZ
 * @Date: 2025-11-14 11:49:52
 */

@Data
@TableName("user_profile")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "用户资料表 ")
public class UserProfile {

  @TableId
  @Schema(description = "用户ID（与user表一对一）")
  private long userId;
 

  @Schema(description = "真实姓名")
  private String realName;
 

  @Schema(description = "性别（1-男，2-女，0-未知）")
  private long gender;
 

  @Schema(description = "地区（如山东省-济南市）")
  private String region;
 

  @Schema(description = "职业（如种植户农资商）")
  private String profession;
 

  @Schema(description = "个人简介")
  private String introduction;
 

  @Schema(description = "更新时间")
  private java.sql.Date updateTime;
 

}
