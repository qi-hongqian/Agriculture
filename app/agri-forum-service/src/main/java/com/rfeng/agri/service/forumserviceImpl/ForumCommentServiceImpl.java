package com.rfeng.agri.service.forumserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.forumentity.ForumComment;
import com.rfeng.agri.service.ForumCommentService;
import com.rfeng.agri.mapper.ForumCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【forum_comment(论坛评论表)】的数据库操作Service实现
* @createDate 2025-11-14 15:10:33
*/
@Service
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment>
    implements ForumCommentService{

}




