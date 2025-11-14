package com.rfeng.agri.model.entity.userentity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @Description 
 * @Author  MXZ
 * @Date: 2025-11-14 11:49:52
 */

@Data
@TableName("user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 用户表 ")
public class User {

  @Schema(description = "用户ID")
  private long id;
 

  @Schema(description = "手机号（登录账号，唯一）")
  private String phone;
 

  @Schema(description = "密码")
  private String password;
 

  @Schema(description = "昵称")
  private String nickname;
 

  @Schema(description = "头像URL")
  private String avatar;
 

  @Schema(description = "角色（user-普通用户，admin-管理员）")
  private String role;
 

  @Schema(description = "状态（0-禁用，1-正常）")
  private long status;
 

  @Schema(description = "最后登录时间")
  private java.sql.Date lastLoginTime;


  @Schema(description = "null")
  private java.sql.Date createTime;


  @Schema(description = "null")
  private java.sql.Date updateTime;
 

}
