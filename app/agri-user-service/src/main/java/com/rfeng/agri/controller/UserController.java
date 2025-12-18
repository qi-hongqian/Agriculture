package com.rfeng.agri.controller;
import com.rfeng.agri.model.dto.UserProfileDTO;
import com.rfeng.agri.model.entity.userentity.ChinaRegion;
import com.rfeng.agri.model.entity.userentity.User;
import com.rfeng.agri.model.entity.userentity.UserProfile;
import com.rfeng.agri.service.ChinaRegionService;
import com.rfeng.agri.service.UserProfileService;
import com.rfeng.agri.service.UserService;
import com.rfeng.agri.util.MinioUtil;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/16 17:12
 */
@Slf4j
@RestController
@Tag(name = "用户管理", description = "用户注册、头像上传等相关接口")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private ChinaRegionService chinaRegionService;

    @Operation(summary = "临时上传头像", description = "注册前上传头像用于预览")
    @PostMapping("/avatar/temp-upload")
    public Map<String, Object> uploadTempAvatar(@RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        try {
            String avatarUrl = minioUtil.uploadTempAvatar(avatar);

            result.put("success", true);
            result.put("message", "临时头像上传成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            log.error("临时头像上传失败: ", e);
            result.put("success", false);
            result.put("message", "临时头像上传失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "删除临时头像", description = "用户取消注册时删除临时头像")
    @DeleteMapping("/avatar/temp-delete")
    public Map<String, Object> deleteTempAvatar(@RequestParam(required = false) String avatarUrl) {
        Map<String, Object> result = new HashMap<>();

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            result.put("success", true);
            result.put("message", "无需删除");
            return result;
        }

        try {
            boolean deleted = minioUtil.deleteAvatar(avatarUrl);
            
            if (deleted) {
                result.put("success", true);
                result.put("message", "临时头像已删除");
            } else {
                result.put("success", false);
                result.put("message", "删除失败或文件不存在");
            }
        } catch (Exception e) {
            log.error("删除临时头像失败: ", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "批量删除临时头像", description = "注册提交或异常关闭时批量删除未使用的临时头像")
    @PostMapping("/avatar/temp-delete-batch")
    public Map<String, Object> deleteTempAvatarBatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        try {
            @SuppressWarnings("unchecked")
            java.util.List<String> avatarUrls = (java.util.List<String>) request.get("avatarUrls");
            
            if (avatarUrls == null || avatarUrls.isEmpty()) {
                result.put("success", true);
                result.put("message", "无需删除");
                return result;
            }

            int successCount = 0;
            int failCount = 0;

            for (String avatarUrl : avatarUrls) {
                if (avatarUrl == null || avatarUrl.isEmpty()) {
                    continue;
                }
                
                try {
                    boolean deleted = minioUtil.deleteAvatar(avatarUrl);
                    if (deleted) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    log.warn("删除临时头像失败: {}, 错误: {}", avatarUrl, e.getMessage());
                    failCount++;
                }
            }

            result.put("success", true);
            result.put("message", String.format("批量删除完成，成功: %d, 失败: %d", successCount, failCount));
            result.put("successCount", successCount);
            result.put("failCount", failCount);
        } catch (Exception e) {
            log.error("批量删除临时头像失败: ", e);
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "用户注册", description = "用户注册接口，支持头像上传")
    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String nickname,
            @RequestParam(required = false) String avatarUrl) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 检查手机号是否已存在
            User existingUser = userService.getUserByPhone(phone);
            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "手机号已注册");
                return result;
            }

            // 创建新用户
            User user = new User();
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setRole("user");
            user.setStatus(1L);
            user.setCreateTime(new java.sql.Date(System.currentTimeMillis()));
            user.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
            
            // 保存用户（先保存获取ID）
            boolean saved = userService.save(user);

            if (saved) {
                // 如果有临时头像URL，需要转换为正式头像
                String finalAvatarUrl = null;
                if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.contains("temp_")) {
                    try {
                        // 调用MinioUtil的方法将临时头像重命名为正式头像
                        finalAvatarUrl = minioUtil.renameTempAvatarToFormal(avatarUrl, user.getId());
                        user.setAvatar(finalAvatarUrl);
                    } catch (Exception e) {
                        log.warn("临时头像重命名失败，保存原始临时URL: {}", e.getMessage());
                        user.setAvatar(avatarUrl);
                        finalAvatarUrl = avatarUrl;
                    }
                } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    user.setAvatar(avatarUrl);
                    finalAvatarUrl = avatarUrl;
                }
                
                userService.updateById(user);
                
                result.put("success", true);
                result.put("message", "注册成功");
                result.put("user", user);
                result.put("avatarUrl", finalAvatarUrl);
            } else {
                result.put("success", false);
                result.put("message", "注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败: ", e);
            result.put("success", false);
            result.put("message", "注册失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "上传头像", description = "为已登录用户上传头像")
    @PostMapping("/avatar/upload")
    public Map<String, Object> uploadAvatar(@RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从JWT Token中获取用户ID
            Long userId = UserContextUtil.getCurrentUserId();

            // 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            // 删除旧头像
            String oldAvatarUrl = user.getAvatar();
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                try {
                    minioUtil.deleteAvatar(oldAvatarUrl);
                } catch (Exception e) {
                    log.warn("删除旧头像失败: {}", e.getMessage());
                }
            }

            // 上传新头像
            String avatarUrl = minioUtil.uploadAvatar(avatar, userId);

            // 更新用户头像URL
            user.setAvatar(avatarUrl);
            user.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
            userService.updateById(user);

            result.put("success", true);
            result.put("message", "头像上传成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            log.error("头像上传失败: ", e);
            result.put("success", false);
            result.put("message", "头像上传失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "修改头像（登录后）", description = "已登录用户修改头像，需要Bearer Token认证")
    @PostMapping("/avatar/update")
    public Map<String, Object> updateAvatar(@RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从JWT Token中获取用户ID（必须有Token）
            Long userId = UserContextUtil.getCurrentUserId();

            // 获取用户信息
            User user = userService.getById(userId);

            // 删除旧头像
            String oldAvatarUrl = user.getAvatar();
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                try {
                    minioUtil.deleteAvatar(oldAvatarUrl);
                } catch (Exception e) {
                    log.warn("删除旧头像失败: {}", e.getMessage());
                }
            }

            // 上传新头像
            String avatarUrl = minioUtil.uploadAvatar(avatar, userId);

            // 更新用户头像URL
            user.setAvatar(avatarUrl);
            user.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
            userService.updateById(user);

            result.put("success", true);
            result.put("message", "头像修改成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            log.error("头像修改失败: ", e);
            result.put("success", false);
            result.put("message", "头像修改失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "获取性别选项", description = "获取性别选项列表")
    @GetMapping("/gender/options")
    public Map<String, Object> getGenderOptions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> genderOptions = new ArrayList<>();
            
//            Map<String, Object> unknown = new HashMap<>();
//            unknown.put("value", 0);
//            unknown.put("label", "未知");
//            genderOptions.add(unknown);
            
            Map<String, Object> male = new HashMap<>();
            male.put("value", 1);
            male.put("label", "男");
            genderOptions.add(male);
            
            Map<String, Object> female = new HashMap<>();
            female.put("value", 2);
            female.put("label", "女");
            genderOptions.add(female);
            
            result.put("success", true);
            result.put("data", genderOptions);
        } catch (Exception e) {
            log.error("获取性别选项失败: ", e);
            result.put("success", false);
            result.put("message", "获取性别选项失败: " + e.getMessage());
        }
        
        return result;
    }

    @Operation(summary = "获取省份列表", description = "获取所有省份列表")
    @GetMapping("/region/provinces")
    public Map<String, Object> getProvinces() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询所有不同的省份
            List<ChinaRegion> regions = chinaRegionService.list();
            
            // 提取唯一的省份信息
            List<Map<String, Object>> provinces = regions.stream()
                .collect(Collectors.toMap(
                    ChinaRegion::getProvinceId, 
                    region -> {
                        Map<String, Object> province = new HashMap<>();
                        province.put("provinceId", region.getProvinceId());
                        province.put("provinceName", region.getProvinceName());
                        return province;
                    }, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", provinces);
        } catch (Exception e) {
            log.error("获取省份列表失败: ", e);
            result.put("success", false);
            result.put("message", "获取省份列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Operation(summary = "根据省份ID获取城市列表", description = "根据省份ID获取城市列表")
    @GetMapping("/region/cities/{provinceId}")
    public Map<String, Object> getCitiesByProvinceId(@PathVariable String provinceId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询指定省份的所有城市
            List<ChinaRegion> regions = chinaRegionService.lambdaQuery()
                .eq(ChinaRegion::getProvinceId, provinceId)
                .list();
            
            // 提取唯一的城市信息
            List<Map<String, Object>> cities = regions.stream()
                .collect(Collectors.toMap(
                    ChinaRegion::getCityId, 
                    region -> {
                        Map<String, Object> city = new HashMap<>();
                        city.put("cityId", region.getCityId());
                        city.put("cityName", region.getCityName());
                        return city;
                    }, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", cities);
        } catch (Exception e) {
            log.error("获取城市列表失败: ", e);
            result.put("success", false);
            result.put("message", "获取城市列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Operation(summary = "根据城市ID获取区县列表", description = "根据城市ID获取区县列表")
    @GetMapping("/region/districts/{cityId}")
    public Map<String, Object> getDistrictsByCityId(@PathVariable String cityId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询指定城市的所有区县
            List<ChinaRegion> regions = chinaRegionService.lambdaQuery()
                .eq(ChinaRegion::getCityId, cityId)
                .list();
            
            // 提取区县信息
            List<Map<String, Object>> districts = regions.stream()
                .map(region -> {
                    Map<String, Object> district = new HashMap<>();
                    district.put("districtId", region.getDistrictId());
                    district.put("districtName", region.getDistrictName());
                    return district;
                })
                .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", districts);
        } catch (Exception e) {
            log.error("获取区县列表失败: ", e);
            result.put("success", false);
            result.put("message", "获取区县列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Operation(summary = "用户填写个人信息", description = "用户填写个人信息")
    @PostMapping("/info/fill")
    public Map<String, Object> fillInfo(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从JWT Token中获取用户ID
            Long userId = UserContextUtil.getCurrentUserId();

            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            // 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 创建或更新用户资料
            UserProfile userProfile = userProfileService.lambdaQuery()
                .eq(UserProfile::getUserId, userId)
                .one();
            
            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUserId(userId);
                userProfile.setGender(0L);
            }
            
            // 设置用户资料字段
            if (request.containsKey("realName")) {
                userProfile.setRealName((String) request.get("realName"));
            }
            
            // 性别字段处理
            if (request.containsKey("gender")) {
                Object genderObj = request.get("gender");
                if (genderObj == null) {
                    userProfile.setGender(0L); // null时设为未知
                } else if (genderObj instanceof Number) {
                    userProfile.setGender(((Number) genderObj).longValue());
                } else if (genderObj instanceof String) {
                    try {
                        userProfile.setGender(Long.parseLong((String) genderObj));
                    } catch (NumberFormatException e) {
                        userProfile.setGender(0L);
                    }
                }
            }
            
            // 地区字段处理（前端可能传入ID也可能传入文本）
            if (request.containsKey("region")) {
                Object regionObj = request.get("region");
                if (regionObj == null || regionObj.toString().isEmpty()) {
                    userProfile.setRegion("");
                } else {
                    String region = regionObj.toString();
                    // 判断是否已经是ID格式（包含数字和"-"）还是文本格式
                    if (region.matches("\\d{6}-\\d{6}-\\d{6}")) {
                        // 已经是ID格式，直接保存
                        userProfile.setRegion(region);
                    } else {
                        // 文本格式，需要转换为ID
                        String regionIds = convertRegionTextToIds(region);
                        userProfile.setRegion(regionIds);
                    }
                }
            }
            
            // 职业字段
            if (request.containsKey("profession")) {
                userProfile.setProfession((String) request.get("profession"));
            }
            
            // 个人简介字段
            if (request.containsKey("introduction")) {
                userProfile.setIntroduction((String) request.get("introduction"));
            }

            userProfile.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
            
            // 保存或更新用户资料
            userProfileService.saveOrUpdate(userProfile);

            result.put("success", true);
            result.put("message", "个人信息保存成功");
        } catch (Exception e) {
            log.error("保存个人信息失败: ", e);
            result.put("success", false);
            result.put("message", "保存个人信息失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 将地区文本转换为地区ID拼接字符串
     * 前端传入："山东省-济南市-历下区"
     * 转换为："370000-370100-370101"
     */
    private String convertRegionTextToIds(String regionText) {
        if (regionText == null || regionText.isEmpty()) {
            return "";
        }
        
        try {
            String[] parts = regionText.split("-");
            if (parts.length != 3) {
                return regionText; // 如果格式不正确，直接返回原文本
            }
            
            String provinceName = parts[0];
            String cityName = parts[1];
            String districtName = parts[2];
            
            // 查询对应的地区ID
            ChinaRegion region = chinaRegionService.lambdaQuery()
                .eq(ChinaRegion::getProvinceName, provinceName)
                .eq(ChinaRegion::getCityName, cityName)
                .eq(ChinaRegion::getDistrictName, districtName)
                .one();
            
            if (region != null) {
                return region.getProvinceId() + "-" + region.getCityId() + "-" + region.getDistrictId();
            }
        } catch (Exception e) {
            log.warn("转换地区文本到ID失败: {}, 错误: {}", regionText, e.getMessage());
        }
        
        return regionText; // 转换失败时返回原文本
    }

    @Operation(summary = "获取用户个人信息", description = "获取当前登录用户的个人信息")
    @GetMapping("/info")
    public Map<String, Object> getUserInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从JWT Token中获取用户ID
            Long userId = UserContextUtil.getCurrentUserId();
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            // 获取用户基本信息
            User user = userService.getById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 获取用户详细资料
            UserProfile userProfile = userProfileService.lambdaQuery()
                .eq(UserProfile::getUserId, userId)
                .one();
            
            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUserId(userId);
                userProfile.setGender(0L); // 默认未知性别
            }
            
            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("phone", user.getPhone());
            data.put("nickname", user.getNickname());
            data.put("avatar", user.getAvatar());
            
            data.put("realName", userProfile.getRealName());
            data.put("gender", userProfile.getGender());
            data.put("region", userProfile.getRegion());
            data.put("introduction", userProfile.getIntroduction());
            
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            log.error("获取用户信息失败: ", e);
            result.put("success", false);
            result.put("message", "获取用户信息失败: " + e.getMessage());
        }
        
        return result;
    }

    @Operation(summary = "获取用户个人信息编辑回调", description = "前端填写个人信息时获取当前信息和下拉选项")
    @GetMapping("/info/edit")
    public Map<String, Object> getUserInfoForEdit() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从JWT Token中获取用户ID
            Long userId = UserContextUtil.getCurrentUserId();
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            // 获取用户基本信息
            User user = userService.getById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 获取用户详细资料
            UserProfile userProfile = userProfileService.lambdaQuery()
                .eq(UserProfile::getUserId, userId)
                .one();
            
            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUserId(userId);
                userProfile.setGender(0L);
            }
            
            // 构造UserProfileDTO
            UserProfileDTO dto = new UserProfileDTO();
            dto.setUserId(userId);
            dto.setRealName(userProfile.getRealName());
            dto.setProfession(userProfile.getProfession());
            dto.setIntroduction(userProfile.getIntroduction());
            
            // 处理性别字段
            Map<String, Object> genderMap = new HashMap<>();
            genderMap.put("value", userProfile.getGender());
            genderMap.put("label", getGenderLabel(userProfile.getGender()));
            dto.setGender(genderMap);
            
            // 处理地区字段
            UserProfileDTO.RegionDTO regionDTO = parseRegionString(userProfile.getRegion());
            dto.setRegion(regionDTO);
            
            // 获取性别选项
            List<Map<String, Object>> genderOptions = new ArrayList<>();
            Map<String, Object> male = new HashMap<>();
            male.put("value", 1);
            male.put("label", "男");
            genderOptions.add(male);
            
            Map<String, Object> female = new HashMap<>();
            female.put("value", 2);
            female.put("label", "女");
            genderOptions.add(female);
            
            dto.setGenderOptions(genderOptions);
            
            // 获取所有省份列表
            List<ChinaRegion> regions = chinaRegionService.list();
            List<Map<String, Object>> provinces = regions.stream()
                .collect(Collectors.toMap(
                    ChinaRegion::getProvinceId, 
                    region -> {
                        Map<String, Object> province = new HashMap<>();
                        province.put("provinceId", region.getProvinceId());
                        province.put("provinceName", region.getProvinceName());
                        return province;
                    }, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());
            
            dto.setProvinces(provinces);
            
            result.put("success", true);
            result.put("data", dto);
        } catch (Exception e) {
            log.error("获取用户编辑信息失败: ", e);
            result.put("success", false);
            result.put("message", "获取用户编辑信息失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据性别值获取性别标签
     */
    private String getGenderLabel(Long genderValue) {
        if (genderValue == null) {
            return "未知";
        }
        switch (genderValue.intValue()) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }

    /**
     * 将地区字符串转换为RegionDTO
     * 输入格式: "370000-370100-370101" 或 null/""
     */
    private UserProfileDTO.RegionDTO parseRegionString(String regionString) {
        UserProfileDTO.RegionDTO dto = new UserProfileDTO.RegionDTO();
        
        if (regionString == null || regionString.isEmpty()) {
            return dto;
        }
        
        try {
            String[] ids = regionString.split("-");
            if (ids.length != 3) {
                return dto;
            }
            
            String provinceId = ids[0];
            String cityId = ids[1];
            String districtId = ids[2];
            
            // 查询对应的地区信息
            ChinaRegion region = chinaRegionService.lambdaQuery()
                .eq(ChinaRegion::getProvinceId, provinceId)
                .eq(ChinaRegion::getCityId, cityId)
                .eq(ChinaRegion::getDistrictId, districtId)
                .one();
            
            if (region != null) {
                dto.setProvinceId(provinceId);
                dto.setProvinceName(region.getProvinceName());
                dto.setCityId(cityId);
                dto.setCityName(region.getCityName());
                dto.setDistrictId(districtId);
                dto.setDistrictName(region.getDistrictName());
                dto.setFullRegion(region.getProvinceName() + "-" + region.getCityName() + "-" + region.getDistrictName());
            }
        } catch (Exception e) {
            log.warn("解析地区字符串失败: {}, 错误: {}", regionString, e.getMessage());
        }
        
        return dto;
    }

}
