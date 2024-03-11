package org.jeecg.modules.demo.wxf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.*;
import org.jeecg.modules.demo.wxf.mapper.BizPhoneMapper;
import org.jeecg.modules.demo.wxf.service.*;
import org.jeecg.modules.demo.wxf.util.BatchNoUtil;
import org.jeecg.modules.demo.wxf.util.GlobalTaskStatus;
import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-03-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class BizPhoneServiceImpl extends ServiceImpl<BizPhoneMapper, BizPhone> implements IBizPhoneService {

    private static final String IMPORT_PHONE = "phone";
    private static final String IMPORT_CALL_RECORD = "callRecord";
    private static final String IMPORT_TRANSFER_RECORD = "transferRecord";
    private static final String IMPORT_BLACK_RECORD = "black";
    private static final String EXPORT_PHONE = "export_phone";


    private static final String TASK_STATUS_NORMAL = "2";
    private static final String TASK_STATUS_ERROR = "99";

    @Autowired
    private IBizImportBatchService batchService;
    @Autowired
    private IBizImportTaskService importTaskService;

    @Autowired
    private IBizMidImportService midImportService;

    @Autowired
    private IBizCallRecordsService callRecordsService;


    @Autowired
    private IBizTransferRecordService transferRecordService;
    @Autowired
    private IBizTransferRecordTmpService transferRecordTmpService;

    private  File multiPartFileToFile(MultipartFile multipartFile,Class clazz) throws IOException {

        //获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase();
        }

        String dir = PoiPublicUtil.getWebRootPath(getSaveExcelUrl( clazz, null));
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
            final String filePath = newFile != null ? newFile.getAbsolutePath() : "";

            BizImportTask importTask = new BizImportTask(filePath,"1",translateTaskType(clazz),batchno);
            importTaskService.save(importTask);
            return Result.ok("文件上传成功，稍后将自动执行导入操作！");
        }
        return Result.error("文件上传失败！");
    }

    @Override
    public Result<BizExportRecord> exportExcel(Class clazz){
        final String batchno = BatchNoUtil.generate();
        BizImportTask importTask = new BizImportTask("filePath","1",translateTaskType(clazz),batchno);
        importTaskService.save(importTask);
        return Result.ok("任务提交成功，稍后将自动执行导出操作！");
    }



    private String translateTaskType(Class clazz){
        if(clazz.equals(BizMidImport.class)){
            return IMPORT_PHONE;
        }else if(clazz.equals(BizCallRecords.class)){
            return IMPORT_CALL_RECORD;
        }else if(clazz.equals(BizTransferRecord.class)){
            return IMPORT_TRANSFER_RECORD;
        }else if(clazz.equals(BizBalckPhone.class)){
            return IMPORT_BLACK_RECORD;
        }else if(clazz.equals(BizExportRecord.class)){
            return EXPORT_PHONE;
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


    /**
     *
     * @param pojoClass
     * @param baseDir 上传时传null对应默认目录为:upload/excelUpload，下载时手工指定目录为：download/excelDownload
     * @return
     */
    public String getSaveExcelUrl(Class<?> pojoClass, String baseDir)  {

        final String defaultDir = baseDir!=null?baseDir:"upload/excelUpload";
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
        BizImportTask bizImportTask=null;
        try {
             bizImportTask = innerRun();
        } catch (Throwable e) {
            //不抛出异常
            e.printStackTrace();
        }finally {
            if(bizImportTask!=null){
                importTaskService.updateById(bizImportTask);
            }
            GlobalTaskStatus.end();
        }
    }

//    @Transactional(rollbackFor = Exception.class)
    public BizImportTask innerRun() {
        if(GlobalTaskStatus.isRunning()==true){
            log.info(GlobalTaskStatus.descCurTask());
            return null;
        }

        LambdaQueryWrapper<BizImportTask> query = new LambdaQueryWrapper<BizImportTask>()
                .eq(BizImportTask::getTaskStatus, "1").orderByAsc(BizImportTask::getCreateTime);
        final List<BizImportTask> list = importTaskService.list(query);
        if(list.size()==0){
            log.info("不存在待执行的任务。");
            return null;
        }
        final BizImportTask importTask = list.get(0);
        log.info("待执行任务数量：{}，即将执行任务类型：{}，批次号：{} 的任务。",list.size(),importTask.getTaskType(),importTask.getBatchNo());
        if(GlobalTaskStatus.run(importTask)==false){
            log.info("很不幸，任务枪战失败，等待下次机会。");
            return importTask;
        }
        if(IMPORT_PHONE.equalsIgnoreCase(importTask.getTaskType())){
            //号码资源列表导入任务
            doimportPhone(importTask);
        }else if(IMPORT_CALL_RECORD.equalsIgnoreCase(importTask.getTaskType())){
            //号码资源列表导入任务
            doimportCallRecord(importTask);
        }else if(IMPORT_TRANSFER_RECORD.equalsIgnoreCase(importTask.getTaskType())){
            //运单记录列表导入任务
            doimportTransferRecord(importTask);
        }else if(IMPORT_BLACK_RECORD.equalsIgnoreCase(importTask.getTaskType())){
            //运单记录列表导入任务
            doimportBlackPhone(importTask);
        }else if(EXPORT_PHONE.equalsIgnoreCase(importTask.getTaskType())){
            //导出记录
            doexportPhone(importTask);
        }
        return importTask;

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doimportPhone2(BizImportTask importTask) {
        try {
            midImportService.insertPhoneFromMidImport();
            importTask.setTaskStatus(TASK_STATUS_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            importTask.setTaskStatus(TASK_STATUS_ERROR);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public void doimportPhone(BizImportTask importTask) {

            // 2正常结束。99异常
            String taskStatus = TASK_STATUS_NORMAL;
            final String batchno = importTask.getBatchNo();
            ImportSummary importSummary = new ImportSummary();

            // 获取上传文件对象
            File file = new File((importTask.getFilePath()));
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {

                if(file.exists()){
                    List<BizMidImport> list = parseDataFromFile(file, params);
                    final int excelTotalSize = list.size();
                    list.forEach(a->a.setBatchNo(batchno));
                    final ImportExcelFilter<BizMidImport> importExcelFilter = buildFilter();
                    //过滤后的excel。有效的手机号码。有效的手机号码还得和库里的号码比对
                    importExcelFilter.doFilter(list);

                    long start = System.currentTimeMillis();
                    midImportService.truncateTable();
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
                    batchService.save(importSummary.toBatch(batchno));
                }else{
                    taskStatus = TASK_STATUS_ERROR;
                }
                importSummary.setEndTime(new Date());

//                Thread.sleep(15000L);
//                if(true){
//                    throw new RuntimeException("我是手工抛出的异常，验证是否会吧任务更新成失败。");
//                }


            } catch (Exception e) {
                //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                String msg = e.getMessage();
                log.error(msg, e);
                taskStatus = TASK_STATUS_ERROR;
                importSummary.setMsg(e.getMessage());
            } finally {
//                midImportService.truncateTable();
                importTask.setTaskStatus(taskStatus);
                importTask.setTaskSummary(JSON.toJSONString(importSummary));
                try {
//                    inputstream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private static List<BizMidImport> parseDataFromFile(File file, ImportParams params) throws Exception {
        List<BizMidImport> list = new ArrayList<>();
        FileInputStream inputstream=null;
        try {
            inputstream = new FileInputStream(file);

            if(file.getAbsoluteFile().getAbsolutePath().toLowerCase().endsWith("txt")){
                //txt文件
                FileUtils fu = new FileUtils();
                list =  FileUtils.readLines(file, Charsets.UTF_8).stream().map(a->{
                    final BizMidImport bmi = new BizMidImport();
                    bmi.setPhone(a);
                    return bmi;
                }).collect(Collectors.toList());
            }else{
                final ExcelImportResult<BizMidImport> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizMidImport.class, params);
//                    final Workbook workbook = objectExcelImportResult.getWorkbook();
                //原始的excel数据
                list = objectExcelImportResult.getList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(inputstream!=null)
                inputstream.close();
        }
        return list;

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
        String taskStatus = TASK_STATUS_NORMAL;
        try {

            // 2正常结束。99异常
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


        } catch (Exception e) {
            //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
            String msg = e.getMessage();
            log.error(msg, e);
            taskStatus=TASK_STATUS_ERROR;
            importSummary.setMsg(e.getMessage());
        } finally {
            importTask.setTaskStatus(taskStatus);
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
    public void doimportTransferRecord(BizImportTask importTask) {

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
                final ExcelImportResult<BizTransferRecordTmp> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizTransferRecordTmp.class, params);
                final Workbook workbook = objectExcelImportResult.getWorkbook();
                //原始的excel数据
                List<BizTransferRecordTmp> list = objectExcelImportResult.getList();
                list.forEach(a->a.setBatchNo(batchno));

                long start = System.currentTimeMillis();
                //先清空数据，避免之前的bug遗留数据
                transferRecordTmpService.truncate();
                transferRecordTmpService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //1，更新客户地址：
                transferRecordTmpService.insertFromTmpUpdatePhoneTableUpdateTransferStatus();


                importSummary.setTotal(list.size());
                //非法的数据=excel数据 - 识别出来的号码总数
            }else{
                taskStatus = TASK_STATUS_ERROR;
            }
            importSummary.setEndTime(new Date());
            importTask.setTaskStatus(taskStatus);


        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
            importTask.setTaskStatus(TASK_STATUS_ERROR);
            importSummary.setMsg(e.getMessage());
        } finally {
            transferRecordTmpService.truncate();
            importTask.setTaskSummary(JSON.toJSONString(importSummary));
            try {
                if(inputstream!=null)
                    inputstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void doimportBlackPhone(BizImportTask importTask) {

        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();

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

                midImportService.truncateTable();
                midImportService.saveBatch(list);
                //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                log.info("doimportBlackPhone 消耗时间" + (System.currentTimeMillis() - start) + "毫秒");

                midImportService.insertBlackPhoneFromMidImport();
                importSummary.setTotal(list.size());
                //非法的数据=excel数据 - 识别出来的号码总数
            }else{
                taskStatus = TASK_STATUS_ERROR;
            }
            importSummary.setEndTime(new Date());
            importTask.setTaskStatus(taskStatus);
//            midImportService.truncateTable();

        } catch (Throwable e) {
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



    private void autoCreateDirAndFile(File f){
        if(f.getParentFile().exists()==false){
            f.getParentFile().mkdirs();
        }
        if(f.exists()==false){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void doexportPhone(BizImportTask importTask) {
        String taskStatus = TASK_STATUS_NORMAL;

//        // Step.1 组装查询条件
//        QueryWrapper<BizExportRecord> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//
//        // 过滤选中数据
//        String selections = request.getParameter("selections");
//        if (oConvertUtils.isNotEmpty(selections)) {
//            List<String> selectionList = Arrays.asList(selections.split(","));
//            queryWrapper.in("id",selectionList);
//        }
//        // Step.2 获取导出数据
//        List<BizExportRecord> exportList = service.list(queryWrapper);
        final List<BizPhone> list = this.list();
        try {
            final String filePath = exportDataToExcel(list);
            importTask.setFilePath(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            taskStatus = TASK_STATUS_ERROR;
        }finally {
            importTask.setTaskStatus(taskStatus);
        }


    }


    /**
     * 数据导出为excel
     * @param dataList
     * @throws Exception
     */
    public String exportDataToExcel(List dataList) throws Exception {
        Workbook workbook = null;
        FileOutputStream out = null;
        File file = null;
        try {
            ExportParams exportParams=new ExportParams("报表", "导出人:123" , "title is me!");
            if(dataList==null || dataList.size()==0){
                return "";
            }
            final Class<?> pojoClass = dataList.get(0).getClass();
            workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, dataList, null);


            //获取文件名
            String dir = PoiPublicUtil.getWebRootPath(getSaveExcelUrl( pojoClass, "download/excelDownload"));
            SimpleDateFormat format = new SimpleDateFormat("yyyMMddHHmmss");
            final String filePath = dir + "/" + format.format(new Date()) + "_" + Math.round(Math.random() * 100000) +".xls";

            file = new File(filePath);
            autoCreateDirAndFile(file);

            out = new FileOutputStream(file);
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(workbook!=null){
                    workbook.close();
                }
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

}
