package org.jeecg.modules.demo.wxf.service;

import org.jeecg.modules.demo.wxf.entity.BizTransferRecordTmp;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: biz_transfer_record_tmp
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface IBizTransferRecordTmpService extends IService<BizTransferRecordTmp> {

    @Transactional(rollbackFor = Exception.class)
    void insertFromTmpUpdatePhoneTableUpdateTransferStatus();

    void truncate();
}
