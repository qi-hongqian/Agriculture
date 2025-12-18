package com.rfeng.agri.model.entity.userentity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 用户黑名单表
 *
 * @author 齐洪乾
 * @version 1.00
 * @since 2025/12/18
 */
@Data
@TableName("user_blacklist")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "用户黑名单表")
public class UserBlacklist {

    @TableId
    @Schema(description = "黑名单ID")
    private Long id;

    @Schema(description = "用户ID（主动拉黑人）")
    private Long userId;

    @Schema(description = "被拉黑的用户ID")
    private Long blockedUserId;

    @Schema(description = "关系类型（1-单向拉黑，2-双向拉黑）")
    private Integer relationType;

    @Schema(description = "拉黑类型（1-隐藏内容，2-完全屏蔽）")
    private Integer blockType;

    @Schema(description = "拉黑备注")
    private String remark;

    @Schema(description = "状态（0-已解除，1-生效中）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
