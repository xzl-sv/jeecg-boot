package org.jeecg.modules.demo.wxf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.*;
import org.jeecg.modules.demo.wxf.mapper.BizPhoneMapper;
import org.jeecg.modules.demo.wxf.service.IBizCallRecordsService;
import org.jeecg.modules.demo.wxf.service.IBizImportTaskService;
import org.jeecg.modules.demo.wxf.service.IBizMidImportService;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;
import org.jeecg.modules.demo.wxf.util.BatchNoUtil;
import org.jeecg.modules.demo.wxf.util.GlobalTaskStatus;
import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.result.ExcelImportResult;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-03-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class BizPhoneServiceImpl extends ServiceImpl<BizPhoneMapper, BizPhone> implements IBizPhoneService {

    private static final String PHONE = "phone";
    private static final String CALL_RECORD = "callRecord";
    private static final String TRANSFER_RECORD = "transferRecord";
    private static final String TASK_STATUS_NORMAL = "2";
    private static final String TASK_STATUS_ERROR = "99";
    @Autowired
    private IBizImportTaskService importTaskService;

    @Autowired
    private IBizMidImportService midImportService;

    @Autowired
    private IBizCallRecordsService callRecordsService;


    private  File multiPartFileToFile(MultipartFile multipartFile,Class clazz) throws IOException {

        //获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase();
        }

        String dir = PoiPublicUtil.getWebRootPath(getSaveExcelUrl( clazz));
        SimpleDateFormat format = new SimpleDateFormat("yyyMMddHHmmss");
        final String filePath = dir + "/" + format.format(new Date()) + "_" + Math.round(Math.random() * 100000) +"."+extension;

        File file = new File(filePath);

        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
        log.info("文件成功缓存到：{}",file.getAbsolutePath());

        return file;
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcelee(HttpServletRequest request, HttpServletResponse response,Class clazz) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            //一个文件一个批次
            final String batchno = BatchNoUtil.generate();

            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            File newFile = null;
            try {
                newFile = multiPartFileToFile(file,clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BizImportTask importTask = new BizImportTask();
            importTask.setBatchNo(batchno);
            importTask.setTaskStatus("1");
            importTask.setFilePath(newFile!=null?newFile.getAbsolutePath():"");
            importTask.setTaskType(translateTaskType(clazz));
            importTaskService.save(importTask);
            return Result.ok("文件上传成功，稍后将自动执行导入操作！");
        }
        return Result.error("文件上传失败！");
    }

    private String translateTaskType(Class clazz){
        if(clazz.equals(BizMidImport.class)){
            return PHONE;
        }else if(clazz.equals(BizCallRecords.class)){
            return CALL_RECORD;
        }else if(clazz.equals(BizTransferRecord.class)){
            return TRANSFER_RECORD;
        }else{
            return "unknow";
        }
    }

    @NotNull
    private  ImportExcelFilter<BizMidImport> buildFilter() {
        final ImportExcelFilter<BizMidImport> importExcelFilter = new ImportExcelFilter<BizMidImport>() {
            /**
             * 默认返回excel解析之后的全部数据。
             *
             * @param list
             * @return
             */
            @Override
            public void doFilter(List<BizMidImport> list) {
                final Iterator<BizMidImport> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final BizMidImport p = iterator.next();
                    final String validPhone = PhoneUtil.detectPhone(p.getPhone());
                    if (validPhone != null) {
                        p.setPhone(validPhone);
                        PhoneUtil.fillPhoneArea(p);
                    } else {
                        iterator.remove();
                    }

                }
            }
        };
        return importExcelFilter;
    }




    public String getSaveExcelUrl(Class<?> pojoClass)  {
        final String defaultDir = "upload/excelUpload";
        String url = "";
        url = pojoClass.getName().split("\\.")[pojoClass.getName().split("\\.").length - 1];
        final String dir = defaultDir + "/" + url;
        File savefile = new File(dir);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        return dir;
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void exampleSchedule() throws Exception{
        try {
            innerRun();
        } catch (Exception e) {
            //不抛出异常
            e.printStackTrace();
        }
    }

    private void innerRun() {
        if(GlobalTaskStatus.isRunning()==true){
            log.info(GlobalTaskStatus.descCurTask());
            return;
        }

        LambdaQueryWrapper<BizImportTask> query = new LambdaQueryWrapper<BizImportTask>()
                .eq(BizImportTask::getTaskStatus, "1").orderByAsc(BizImportTask::getCreateTime);
        final List<BizImportTask> list = importTaskService.list(query);
        if(list.size()==0){
            log.info("不存在待执行的任务。");
            return;
        }
        final BizImportTask importTask = list.get(0);
        log.info("待执行任务数量：{}，即将执行任务类型：{}，批次号：{} 的任务。",list.size(),importTask.getTaskType(),importTask.getBatchNo());
        if(GlobalTaskStatus.run(importTask)==false){
            log.info("很不幸，任务枪战失败，等待下次机会。");
            return;
        }
        if(PHONE.equalsIgnoreCase(importTask.getTaskType())){
            //号码资源列表导入任务
            doimportPhone(importTask);
        }else if(CALL_RECORD.equalsIgnoreCase(importTask.getTaskType())){
            //号码资源列表导入任务
            doimportCallRecord(importTask);
        }
        importTaskService.updateById(importTask);
        GlobalTaskStatus.end();
    }


    public void doimportPhone(BizImportTask importTask) {

            final String batchno = importTask.getBatchNo();
            ImportSummary importSummary = new ImportSummary();

            // 获取上传文件对象
            File file = new File((importTask.getFilePath()));
            FileInputStream inputstream = null;
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {

                // 2正常结束。99异常
                String taskStatus = TASK_STATUS_NORMAL;
                if(file.exists()){
                    inputstream = new FileInputStream(file);
                    final ExcelImportResult<BizMidImport> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizMidImport.class, params);
                    final Workbook workbook = objectExcelImportResult.getWorkbook();
                    //原始的excel数据
                    List<BizMidImport> list = objectExcelImportResult.getList();
                    final int excelTotalSize = list.size();
                    list.forEach(a->a.setBatchNo(batchno));
                    final ImportExcelFilter<BizMidImport> importExcelFilter = buildFilter();
                    //过滤后的excel。有效的手机号码。有效的手机号码还得和库里的号码比对
                    importExcelFilter.doFilter(list);

                    long start = System.currentTimeMillis();
                    midImportService.saveBatch(list);
                    //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                    //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                    log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                    //update-end-author:taoyan date:20190528 for:批量插入数据
                    //TODO 处理数据

                    final Integer existInDb = midImportService.phoneExistInDb();
                    final Integer valueNum = midImportService.phoneValueNum();
                    midImportService.insertPhoneFromMidImport();
                    importSummary.setTotal(list.size());
                    //非法的数据=excel数据 - 识别出来的号码总数
                    importSummary.setInvalidNotDup(excelTotalSize-list.size());
                    importSummary.setValid(valueNum);
                    importSummary.setDup(existInDb);
                }else{
                    taskStatus = TASK_STATUS_ERROR;
                }
                importSummary.setEndTime(new Date());
                importTask.setTaskStatus(taskStatus);

//                Thread.sleep(15000L);
                midImportService.truncateTable();
//                if(true){
//                    throw new RuntimeException("我是手工抛出的异常，验证是否会吧任务更新成失败。");
//                }


            } catch (Exception e) {
                //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                String msg = e.getMessage();
                log.error(msg, e);
                importTask.setTaskStatus(TASK_STATUS_ERROR);
                importSummary.setMsg(e.getMessage());
            } finally {
                importTask.setTaskSummary(JSON.toJSONString(importSummary));
                try {
                    inputstream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }


    /**
     * 执行导入通话记录
     * @param importTask
     */
    public void doimportCallRecord(BizImportTask importTask) {

        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();
        importSummary.setBeginTime(new Date());

        // 获取上传文件对象
        File file = new File((importTask.getFilePath()));
        FileInputStream inputstream = null;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(false);
        try {

            // 2正常结束。99异常
            String taskStatus = TASK_STATUS_NORMAL;
            if(file.exists()){
                inputstream = new FileInputStream(file);
                final ExcelImportResult<BizCallRecords> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizCallRecords.class, params);
                final Workbook workbook = objectExcelImportResult.getWorkbook();
                //原始的excel数据
                List<BizCallRecords> list = objectExcelImportResult.getList();
                final int excelTotalSize = list.size();
                list.forEach(a->a.setBatchNo(batchno));
                final ImportExcelFilter<BizMidImport> importExcelFilter = buildFilter();
                //过滤后的excel。有效的手机号码。有效的手机号码还得和库里的号码比对

                long start = System.currentTimeMillis();
                callRecordsService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //1，更新客户类型：成功客户、失败客户
                //2，更新客户姓名（非空）
                callRecordsService.updatePhoneByCallRecords(batchno);
                importSummary.setTotal(list.size());
                //非法的数据=excel数据 - 识别出来的号码总数
            }else{
                taskStatus = TASK_STATUS_ERROR;
            }
            importSummary.setEndTime(new Date());
            importTask.setTaskStatus(taskStatus);


        } catch (Exception e) {
            //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
            String msg = e.getMessage();
            log.error(msg, e);
            importTask.setTaskStatus(TASK_STATUS_ERROR);
            importSummary.setMsg(e.getMessage());
        } finally {
            importTask.setTaskSummary(JSON.toJSONString(importSummary));
            try {
                inputstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
