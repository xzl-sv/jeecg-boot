package org.jeecg.modules.demo.wxf.service.impl;

import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.mapper.BizMidImportMapper;
import org.jeecg.modules.demo.wxf.service.IBizMidImportService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 导入中间表
 * @Author: jeecg-boot
 * @Date:   2024-03-03
 * @Version: V1.0
 */
@Service
public class BizMidImportServiceImpl extends ServiceImpl<BizMidImportMapper, BizMidImport> implements IBizMidImportService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void truncateTable(){
        this.baseMapper.truncateTable();
    }


    /**
     * 查询excel中数据有多少在表里已经存在
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer phoneExistInDb(){
        return this.baseMapper.phoneExistInDb();
    }

    /**
     * 有价值的号码数量。原来表里面不存在.
     * 插入的数量应该也是这么多
     * valid的数量
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer phoneValueNum(){
        return this.baseMapper.phoneValueNum();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertPhoneFromMidImport(){
        this.baseMapper.insertPhoneFromMidImport();
    }
}