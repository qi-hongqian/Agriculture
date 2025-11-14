package com.rfeng.agri.service.forumserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.forumentity.ForumPost;
import com.rfeng.agri.service.ForumPostService;
import com.rfeng.agri.mapper.ForumPostMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【forum_post(论坛帖子表)】的数据库操作Service实现
* @createDate 2025-11-14 15:10:33
*/
@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost>
    implements ForumPostService{

}




