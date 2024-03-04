package org.jeecg.modules.demo.wxf.service;

import org.jeecg.modules.demo.wxf.entity.BizCallRecords;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 外呼记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface IBizCallRecordsService extends IService<BizCallRecords> {

    /**
     *
     * 更新号码资源表中的：性别、客户状态
     * @param batchNo 外呼记录的批次号
     */
    @Transactional(rollbackFor = Exception.class)
    void updatePhoneByCallRecords(String batchNo);
}
