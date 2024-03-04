package org.jeecg.modules.demo.wxf.service.impl;

import org.jeecg.modules.demo.wxf.entity.BizCallRecords;
import org.jeecg.modules.demo.wxf.mapper.BizCallRecordsMapper;
import org.jeecg.modules.demo.wxf.service.IBizCallRecordsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 外呼记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
@Service
public class BizCallRecordsServiceImpl extends ServiceImpl<BizCallRecordsMapper, BizCallRecords> implements IBizCallRecordsService {


    /**
     *
     * 更新号码资源表中的：性别、客户状态
     * @param batchNo 外呼记录的批次号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePhoneByCallRecords(String batchNo){
        final BizCallRecordsMapper callRecordsMapper = this.baseMapper;
        callRecordsMapper.updateGender(batchNo);
        callRecordsMapper.updateSbStatus(batchNo);
        callRecordsMapper.updateCgStatus(batchNo);

    }
}
