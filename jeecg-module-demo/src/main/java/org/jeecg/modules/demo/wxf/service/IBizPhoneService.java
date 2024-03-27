package org.jeecg.modules.demo.wxf.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.wxf.entity.BizExportRecord;
import org.jeecg.modules.demo.wxf.entity.BizImportTask;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-02-29
 * @Version: V1.0
 */
public interface IBizPhoneService extends IService<BizPhone> {

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcelee(HttpServletRequest request, HttpServletResponse response,Class clazz);

    Result<BizExportRecord> submitExportTask(Class clazz, String paramMapJson);

    void delBatch(String batchNo);

    void doimportPhone2(BizImportTask importTask);

    @Transactional(rollbackFor = Exception.class)
    void delBatchs(List<String> ids);
}
