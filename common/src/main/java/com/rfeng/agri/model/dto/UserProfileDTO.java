package com.rfeng.agri.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;
/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18 11:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户个人信息编辑DTO")
public class UserProfileDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别对象 {value: 性别值, label: 性别标签}")
    private Map<String, Object> gender;

    @Schema(description = "地区对象")
    private RegionDTO region;

    @Schema(description = "职业")
    private String profession;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "性别选项列表")
    private List<Map<String, Object>> genderOptions;

    @Schema(description = "省份列表")
    private List<Map<String, Object>> provinces;

    /**
     * 地区DTO 用于返回地区选择的详细信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "地区信息")
    public static class RegionDTO {
        @Schema(description = "省份ID")
        private String provinceId;

        @Schema(description = "省份名称")
        private String provinceName;

        @Schema(description = "城市ID")
        private String cityId;

        @Schema(description = "城市名称")
        private String cityName;

        @Schema(description = "区县ID")
        private String districtId;

        @Schema(description = "区县名称")
        private String districtName;

        @Schema(description = "完整地区字符串（用于显示）")
        private String fullRegion;
    }
}