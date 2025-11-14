package com.rfeng.agri.service.contentserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.contententity.News;
import com.rfeng.agri.service.NewsService;
import com.rfeng.agri.mapper.NewsMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【news(新闻表)】的数据库操作Service实现
* @createDate 2025-11-14 15:05:27
*/
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News>
    implements NewsService{

}




