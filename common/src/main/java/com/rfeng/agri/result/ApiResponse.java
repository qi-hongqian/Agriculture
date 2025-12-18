package com.rfeng.agri.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18 13:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一API响应")
public class ApiResponse<T> {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> successData(T data) {
        return new ApiResponse<>(true, null, data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> ApiResponse<T> successData(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 失败响应（带消息和数据）
     */
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}