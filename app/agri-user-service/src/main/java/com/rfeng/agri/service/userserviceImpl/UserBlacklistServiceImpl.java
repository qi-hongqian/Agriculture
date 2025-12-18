package com.rfeng.agri.service.userserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.mapper.UserBlacklistMapper;
import com.rfeng.agri.model.entity.userentity.UserBlacklist;
import com.rfeng.agri.service.UserBlacklistService;
import org.springframework.stereotype.Service;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
@Service
public class UserBlacklistServiceImpl extends ServiceImpl<UserBlacklistMapper, UserBlacklist> implements UserBlacklistService {
}
