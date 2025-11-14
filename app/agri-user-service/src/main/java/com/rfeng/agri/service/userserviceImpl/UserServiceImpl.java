package com.rfeng.agri.service.userserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.userentity.User;
import com.rfeng.agri.service.UserService;
import com.rfeng.agri.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-11-14 15:13:29
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




