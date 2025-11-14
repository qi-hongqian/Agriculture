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
@TableName("question")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = " 题目表 ")
public class Question {

  @Schema(description = "题目ID")
  private long id;
 

  @Schema(description = "题目内容（如“小麦白粉病的防治药剂是？”）")
  private String content;
 

  @Schema(description = "题目类型（1-单选题，2-多选题）")
  private long type;
 

  @Schema(description = "选项（JSON格式，如[{'id':'A','content':'多菌灵'},...]）")
  private String options;
 

  @Schema(description = "正确答案（如'A'或'AB'）")
  private String answer;
 

  @Schema(description = "答案解析")
  private String explanation;
 

  @Schema(description = "分类（如“病虫害防治”“农药使用”）")
  private String category;
 

  @Schema(description = "难度（1-简单，2-中等，3-困难）")
  private long difficulty;
 

  @Schema(description = "每题分值")
  private long score;
 

  @Schema(description = "状态（0-禁用，1-启用）")
  private long status;
 

}
