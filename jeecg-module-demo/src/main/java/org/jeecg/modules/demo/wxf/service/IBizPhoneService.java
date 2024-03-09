package org.jeecg.modules.demo.wxf.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.wxf.entity.BizImportTask;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    void doimportPhone2(BizImportTask importTask);
}
