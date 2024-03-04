package org.jeecg.modules.demo.wxf.service.impl;

import org.jeecg.modules.demo.wxf.entity.BizTransferRecordTmp;
import org.jeecg.modules.demo.wxf.mapper.BizTransferRecordTmpMapper;
import org.jeecg.modules.demo.wxf.service.IBizTransferRecordService;
import org.jeecg.modules.demo.wxf.service.IBizTransferRecordTmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: biz_transfer_record_tmp
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
@Service
public class BizTransferRecordTmpServiceImpl extends ServiceImpl<BizTransferRecordTmpMapper, BizTransferRecordTmp> implements IBizTransferRecordTmpService {


    @Autowired
    private IBizTransferRecordService transferRecordService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFromTmpUpdatePhoneTableUpdateTransferStatus(){
        final BizTransferRecordTmpMapper transferRecordTmpMapper = this.baseMapper;
//        transferRecordTmpMapper.updateTranseferStatusFromTmp();
        transferRecordService.updateTransferStatusToBeUpdate();
        transferRecordTmpMapper.updatePhoneTransferStatusFromTmp();
        transferRecordTmpMapper.insertTransferRecordFromTmp();
    }

    @Override
    public void truncate(){
        this.baseMapper.truncateTable();
    }
}
