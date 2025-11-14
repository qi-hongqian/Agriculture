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
@TableName("answer_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema( description=" 答题记录表 ")
public class AnswerRecord {

  @Schema(description = "记录ID")
  private long id;
 

  @Schema(description = "用户ID（关联A_user库的user表）")
  private long userId;
 

  @Schema(description = "题目ID")
  private long questionId;
 

  @Schema(description = "用户答案")
  private String userAnswer;
 

  @Schema(description = "是否正确（0-错，1-对）")
  private long isCorrect;
 

  @Schema(description = "答题时间")
  private java.sql.Date answerTime;
 

}
