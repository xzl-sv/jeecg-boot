package org.jeecg.modules.demo.wxf.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.wxf.entity.BizUtilPhone;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 号码来源：phone-qqzeng-202402-507481.xlsx
 * 版本：2024年2月
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/2/29 10:20
 * @description:
 */
@Slf4j
public class PhoneUtil {

    private static Map<String,BizUtilPhone> phoneMap = new HashMap<>();

    /**
     *  是否初始化标记
     */
    private static boolean initFlag = false;

    public static  Map<String,BizUtilPhone> f(){
        try {
            init();
        } catch (Exception e) {
//            throw new RuntimeException(e);
        }
        return phoneMap;
    }


    /**
     * 查找号段的所属地，查不到返回空白对象(非null)
     * @param phone 大于等于7，截取前面7位
     * @return
     */
    public static BizUtilPhone find(String phone){
        init();
        if(StringUtils.isBlank(phone) || phone.trim().length()<7){
            return new BizUtilPhone();
        }
        phone = phone.trim().substring(0,7);
        return phoneMap.get(phone)==null?new BizUtilPhone():phoneMap.get(phone);
    }


    /**
     * 初始化
     */

    private static void init() {
        if(initFlag ==true)return;
        long start = System.currentTimeMillis();
        Resource resource = new ClassPathResource("excel/phone-qqzeng.xlsx");
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
           e.printStackTrace();
        }

        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(true);
        List<BizUtilPhone> list = null;
        try {
            list = ExcelImportUtil.importExcel(inputStream, BizUtilPhone.class, params);
            for (BizUtilPhone p:list){
                phoneMap.put(p.getPhone(),p);
            }
            log.info("加载号码归属地,数量："+list.size()+" ,消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
            initFlag =true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }

    }
}
