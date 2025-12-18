package com.rfeng.agri.model.entity.userentity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18 10:27
 */
@Data
@TableName("china_region")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "中国行政区划表（省/市/区县三级）")
public class ChinaRegion {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "省份编码")
    private String provinceId;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市编码")
    private String cityId;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区县编码")
    private String districtId;

    @Schema(description = "区县名称")
    private String districtName;

    @Schema(description = "创建时间")
    private java.sql.Date createTime;

    @Schema(description = "更新时间")
    private java.sql.Date updateTime;
}