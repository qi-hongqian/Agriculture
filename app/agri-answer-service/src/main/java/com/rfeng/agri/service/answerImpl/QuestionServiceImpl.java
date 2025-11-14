package com.rfeng.agri.service.answerImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.answerentity.Question;
import com.rfeng.agri.service.QuestionService;
import com.rfeng.agri.mapper.QuestionMapper;
import org.springframework.stereotype.Service;


@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




