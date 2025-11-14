package com.rfeng.agri.service.contentserviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfeng.agri.model.entity.contententity.Information;
import com.rfeng.agri.service.InformationService;
import com.rfeng.agri.mapper.InformationMapper;
import org.springframework.stereotype.Service;

/**
* @author 22567
* @description 针对表【information(资讯表)】的数据库操作Service实现
* @createDate 2025-11-14 15:05:27
*/
@Service
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information>
    implements InformationService{

}




