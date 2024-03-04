package org.jeecg.modules.demo.wxf.service;

import org.jeecg.modules.demo.wxf.entity.BizTransferRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface IBizTransferRecordService extends IService<BizTransferRecord> {

    void updateTransferStatusToBeUpdate();
}
