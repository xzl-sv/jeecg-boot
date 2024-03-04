package org.jeecg.modules.demo.wxf.service.impl;

import org.jeecg.modules.demo.wxf.entity.BizTransferRecord;
import org.jeecg.modules.demo.wxf.mapper.BizTransferRecordMapper;
import org.jeecg.modules.demo.wxf.service.IBizTransferRecordService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
@Service
public class BizTransferRecordServiceImpl extends ServiceImpl<BizTransferRecordMapper, BizTransferRecord> implements IBizTransferRecordService {

    @Override
    public void updateTransferStatusToBeUpdate(){
        final List<BizTransferRecord> transferStatusToBeUpdate = this.baseMapper.findTransferStatusToBeUpdate();
        if(CollectionUtils.isEmpty(transferStatusToBeUpdate)){
            return;
        }
        for (BizTransferRecord b:transferStatusToBeUpdate){
            baseMapper.updateById(b);
        }
    }

}
