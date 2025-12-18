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
@TableName("forum_media")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "论坛多媒体资源表")
public class ForumMedia {

    @TableId
    @Schema(description = "媒体ID")
    private Long id;

    @Schema(description = "关联帖子ID")
    private Long postId;

    @Schema(description = "关联评论ID")
    private Long commentId;

    @Schema(description = "上传用户ID")
    private Long userId;

    @Schema(description = "媒体类型（1-图片，2-视频）")
    private Integer type;

    @Schema(description = "资源URL")
    private String url;

    @Schema(description = "缩略图URL（视频用）")
    private String thumbnailUrl;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "文件大小（KB）")
    private Integer fileSize;

    @Schema(description = "视频时长（秒）")
    private Integer duration;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态（0-删除，1-正常）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}