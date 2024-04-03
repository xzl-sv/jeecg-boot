package org.jeecg.modules.demo.wxf.service;

import org.jeecg.modules.demo.wxf.entity.BizImportBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 导入批次表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
public interface IBizImportBatchService extends IService<BizImportBatch> {

    void updateBatch(BizImportBatch bizImportBatch);
}
