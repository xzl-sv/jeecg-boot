package org.jeecg.modules.demo.wxf.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.entity.BizUtilPhone;
import org.jeecg.modules.demo.wxf.service.IBizUtilPhoneService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    /**
     * 正则表达式匹配手机号码的模式
     *
     */
    private static String patternStr = "(13\\d|14\\d|15\\d|16\\d|17\\d|18\\d|19\\d)+\\d{8}";

    /**
     * 编译正则表达式
     */
    private static Pattern pattern = Pattern.compile(patternStr);

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
     * 查找号段的所属地，查不到返回空白对象(非null)
     * @param phone 大于等于7，截取前面7位
     * @return
     */
    public static void fillPhoneArea(BizPhone phone){
        init();
        if(StringUtils.isBlank(phone.getPhone()) || phone.getPhone().trim().length()<7){
            return ;
        }
        String phonePre = phone.getPhone().trim().substring(0,7);
        final BizUtilPhone bizUtilPhone = phoneMap.get(phonePre) == null ? new BizUtilPhone() : phoneMap.get(phonePre);
        final String areaCode = bizUtilPhone.getAreaCode();
        if(areaCode==null){
            return;
        }

//        final StringBuffer sb = new StringBuffer("").append(areaCode.substring(0, 2)).append("0000,").append(areaCode.substring(0,4)).append("00,").append(areaCode.substring(0, 5)).append("1");
//        phone.setProvinceCode(areaCode==null?"": sb.toString());

        phone.setProvinceCode(new StringBuffer(areaCode.substring(0, 2)).append("0000").toString());
        phone.setCityCode(new StringBuffer(areaCode.substring(0,4)).append("00,").toString());
    }
    public static void fillPhoneArea(BizMidImport phone, IBizUtilPhoneService bizUtilPhoneService){
        initFromDb(bizUtilPhoneService);
        fillPhoneArea(phone);
    }



    /**
     * 查找号段的所属地，查不到返回空白对象(非null)
     * @param phone 大于等于7，截取前面7位
     * @return
     */
    public static void fillPhoneArea(BizMidImport phone){
//        init();
        if(StringUtils.isBlank(phone.getPhone()) || phone.getPhone().trim().length()<7){
            return ;
        }
        String phonePre = phone.getPhone().trim().substring(0,7);
        final BizUtilPhone bizUtilPhone = phoneMap.get(phonePre) == null ? new BizUtilPhone() : phoneMap.get(phonePre);
        final String areaCode = bizUtilPhone.getCity();
        if(areaCode==null){
            return;
        }

//        final StringBuffer sb = new StringBuffer("").append(areaCode.substring(0, 2)).append("0000,").append(areaCode.substring(0,4)).append("00,").append(areaCode.substring(0, 5)).append("1");
//        phone.setProvinceCode(areaCode==null?"": sb.toString());

        phone.setProvinceCode(new StringBuffer(areaCode.substring(0, 2)).append("0000").toString());
        phone.setCityCode(new StringBuffer(areaCode.substring(0, 4)).append("00").toString());

        phone.setAddress(bizUtilPhone.getPn()+(bizUtilPhone.getPn()!=null && !bizUtilPhone.getPn().contains("市")?bizUtilPhone.getCn():""));

    }

    private static void initFromDb(IBizUtilPhoneService bizUtilPhoneService) {
        if(initFlag ==true)return;
        long start = System.currentTimeMillis();
        List<BizUtilPhone> list = null;
//        Resource resource = new ClassPathResource("excel/phone-qqzeng.xlsx");
//        InputStream inputStream = null;
//        try {
////            inputStream = resource.getInputStream();
//            inputStream = new FileInputStream("/Users/qianshihua/Downloads/hui/data/phone-qqzeng.xlsx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ImportParams params = new ImportParams();
//        params.setTitleRows(0);
//        params.setHeadRows(1);
//        params.setNeedSave(true);
//        try {
//            list = ExcelImportUtil.importExcel(inputStream, BizUtilPhone.class, params);
//            for (BizUtilPhone p:list){
//                phoneMap.put(p.getPhone(),p);
//            }
//            log.info("加载号码归属地,数量："+list.size()+" ,消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
//            initFlag =true;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//            }
//        }


        list = bizUtilPhoneService.list();
        for (BizUtilPhone p:list){
            phoneMap.put(p.getPhone(),p);
        }
        log.info("加载号码归属地,数量："+list.size()+" ,消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        initFlag =true;
    }

    /**
     * 初始化
     * /Users/qianshihua/Downloads/hui/data/phone-qqzeng.xlsx
     */

    private static void init() {
        if(initFlag ==true)return;
        long start = System.currentTimeMillis();
//        Resource resource = new ClassPathResource("excel/phone-qqzeng.xlsx");
        InputStream inputStream = null;
        try {
//            inputStream = resource.getInputStream();
            inputStream = new FileInputStream("/Users/qianshihua/Downloads/hui/data/phone-qqzeng.xlsx");
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

    /**
     * 从字符串中提取有效的手机号码，如果无效的话，返回null
     * @param s
     */
    public static String detectPhone(String s){
        if(s==null)return null;
        Matcher matcher = pattern.matcher(s);
        while(matcher.find()){
            final String phoneNum = matcher.group();
            return phoneNum;
        }
        return null;
    }



}
