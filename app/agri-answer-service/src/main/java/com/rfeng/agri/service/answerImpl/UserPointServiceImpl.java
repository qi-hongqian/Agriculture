package com.rfeng.agri.service.answerImpl;

import com.rfeng.agri.model.entity.answerentity.UserPoint;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.service.UserPointService;
import com.rfeng.agri.mapper.UserPointMapper;
import org.springframework.stereotype.Service;


@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint>
    implements UserPointService{

}




