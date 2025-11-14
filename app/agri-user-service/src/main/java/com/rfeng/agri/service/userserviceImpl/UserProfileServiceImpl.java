package com.rfeng.agri.service.userserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.userentity.UserProfile;
import com.rfeng.agri.service.UserProfileService;
import com.rfeng.agri.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【user_profile(用户资料表)】的数据库操作Service实现
* @createDate 2025-11-14 15:13:29
*/
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile>
    implements UserProfileService{

}




