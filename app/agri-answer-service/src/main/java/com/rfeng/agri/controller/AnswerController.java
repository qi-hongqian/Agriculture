package com.rfeng.agri.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.rfeng.agri.model.entity.answerentity.AnswerRecord;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/11/14 22:32
 */
@RestController
@Tag(name = "答题管理", description = "答题相关接口")
@RequestMapping("/api/answer")
public class AnswerController {

    @Operation(summary = "获取答题记录", description = "根据用户ID获取答题记录")
    @Parameters({
            @Parameter(name = "userId", description = "用户ID", required = true)
    })
    @GetMapping("/records")
    public List<AnswerRecord> getAnswerRecords(@RequestParam Long userId) {
        // 业务逻辑
        return  null;
    }
}