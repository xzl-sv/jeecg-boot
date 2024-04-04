package org.jeecg.modules.demo.wxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.demo.wxf.entity.BizImportBatch;
import org.jeecg.modules.demo.wxf.mapper.BizImportBatchMapper;
import org.jeecg.modules.demo.wxf.service.IBizImportBatchService;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 导入批次表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Service
public class BizImportBatchServiceImpl extends ServiceImpl<BizImportBatchMapper, BizImportBatch> implements IBizImportBatchService {

    @Lazy
    @Autowired
    private IBizPhoneService phoneService;

    @Override
    public void updateBatch(BizImportBatch bizImportBatch){
        final BizImportBatch oldOne = getById(bizImportBatch.getId());
        String oldBatchNo = oldOne.getBatchNo();
        final String newBatchNo = bizImportBatch.getBatchNo();

        QueryWrapper qw = new QueryWrapper();
        qw.eq("batch_no",newBatchNo);
        final long count = this.count(qw);
        if(count>0){
            //新的批次原来已经存在。删除掉这个，更新号码表中老的批次号为新的批次号
            this.removeById(bizImportBatch.getId());
        }else{
            updateById(bizImportBatch);
        }
        phoneService.updateBatch(oldBatchNo,newBatchNo);
    }

}
