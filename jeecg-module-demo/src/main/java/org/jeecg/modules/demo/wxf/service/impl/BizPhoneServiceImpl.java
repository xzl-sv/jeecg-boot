package org.jeecg.modules.demo.wxf.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.*;
import org.jeecg.modules.demo.wxf.mapper.BizPhoneMapper;
import org.jeecg.modules.demo.wxf.service.*;
import org.jeecg.modules.demo.wxf.util.BatchNoUtil;
import org.jeecg.modules.demo.wxf.util.GlobalTaskStatus;
import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecg.modules.system.entity.SysDict;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.entity.result.ExcelImportResult;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.jeecg.modules.demo.wxf.util.Consts.DEFAULT_EXPORT_SIZE;

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
    private ISysDictService sysDictSsysDictServiceervice;
    @Autowired
    private IBizUtilPhoneService utilPhoneService;
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

    @Autowired
    private IBizExportRecordService exportRecordService;

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
        final String absolutePath = file.getAbsolutePath();
        log.info("文件成功缓存到：{}", absolutePath);
        if(absolutePath.toLowerCase().endsWith("csv")){
            final File newExcelFileFromCsv = convertCsvToExcel(file);
            log.info("csv 文件转换成功：{}",newExcelFileFromCsv.getAbsolutePath());
            return newExcelFileFromCsv;
        }


        return file;
    }


    public static File convertCsvToExcel(File csvFile) {
        final String newFileName = csvFile.getParentFile().getAbsolutePath() + File.separator + csvFile.getName().split("\\.")[0] + ".XLSX";
        try {
            //创建SXSSFWorkbook对象，参数表示要保持在内存中的行数
            XSSFWorkbook workbook = new XSSFWorkbook();
            CSVReader reader = new CSVReader(new FileReader(csvFile.getCanonicalPath()));
            Sheet sheet = workbook.createSheet("Sheet1");
            XSSFCellStyle strCellStyle= workbook.createCellStyle();
            //CellType.STRING，里面写着1就是String格式
//            strCellStyle.setDataFormat(1);
            strCellStyle.setBorderBottom(BorderStyle.HAIR);
            Row row;
            Cell cell;
            String[] dataRow;
            while ((dataRow = reader.readNext()) != null) {
                row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int j = 0; j < dataRow.length; j++) {
                    cell = row.createCell(j);
                    DateUtil.parse("");
                    cell.setCellValue(formatDate(dataRow[j]));
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(strCellStyle);
                }
            }
            File xlsxFile = new File(newFileName);
            FileOutputStream fos = new FileOutputStream(xlsxFile);
            workbook.write(fos);
            workbook.close();
//            workbook.dispose();
            fos.close();
//            csvFile.delete();
            return xlsxFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    static final String UTF8_BOM = "\uFEFF";

    private static String formatDate(String mayDate){
        try {
            final DateTime parse = DateUtil.parse(mayDate);
            if(parse==null){
                return mayDate;
            }
            final int year = DateUtil.year(parse);
            if(year >3000 || year<1500){
                return mayDate;
            }
            return DateUtil.formatDateTime(parse);
        } catch (Exception e) {

            if (mayDate!=null && mayDate.startsWith(UTF8_BOM)) {
                mayDate = mayDate.substring(1);
            }
            return mayDate;
        }
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
        final MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();

        //一个文件一个批次
        final String batchno = BatchNoUtil.generate();
        String dir ="";
        String files="";
        for (Map.Entry<String, List<MultipartFile>> entity : multiFileMap.entrySet()) {

            // 获取上传文件对象
            List<MultipartFile> file = entity.getValue();
            for (MultipartFile mf:file){
                File newFile = null;
                try {
                    newFile = multiPartFileToFile(mf,clazz);
                    dir=newFile.getParentFile().getAbsolutePath();
                    files +=newFile.getName()+"@@@";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        BizImportTask importTask = new BizImportTask(dir+"####"+files,"1",translateTaskType(clazz),batchno);
        importTaskService.save(importTask);
        return Result.ok("文件上传成功，稍后将自动执行导入操作！");
    }

    @Override
    public Result<BizExportRecord> submitExportTask(Class clazz, String paramMapJson){
        final String batchno = BatchNoUtil.generate();
        BizImportTask importTask = new BizImportTask("filePath","1",translateTaskType(clazz),batchno);
        importTask.setTaskSummary(paramMapJson);
        importTaskService.save(importTask);
        BizExportRecord ber = new BizExportRecord();
        ber.setBatchNo(batchno);
        ber.parseParam(paramMapJson);
        exportRecordService.save(ber);
        return Result.ok("任务提交成功，稍后将自动执行导出操作！");
    }

    @Override
    public void delBatch(String batchNo) {
        baseMapper.delBatch(batchNo);
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
                        PhoneUtil.fillPhoneArea(p,utilPhoneService);
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
                .eq(BizImportTask::getTaskStatus, "1").orderByDesc(BizImportTask::getCreateTime);
        final List<BizImportTask> list = importTaskService.list(query);
        if(list.size()==0){
//            log.info("不存在待执行的任务。");
            return null;
        }
        final BizImportTask importTask = list.get(0);
        importTask.start();
        log.info("待执行任务数量：{}，即将执行任务类型：{}，批次号：{} 的任务。",list.size(),importTask.getTaskType(),importTask.getBatchNo());
        if(GlobalTaskStatus.run(importTask)==false){
            log.info("很不幸，任务枪战失败，等待下次机会。");
            return importTask;
        }
        try {
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
        } catch (Throwable e) {
            e.printStackTrace();
            importTask.setTaskStatus(TASK_STATUS_ERROR);
            importTask.setMsg(e.getMessage()+"-->"+importTask.getTaskSummary());
        }finally {
            importTask.end();
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
        StopWatch sw = new StopWatch();
        // 2正常结束。99异常
        String taskStatus = TASK_STATUS_NORMAL;
        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();
         int excelTotalSize = 0;

        final ImportExcelFilter<BizMidImport> importExcelFilter = buildFilter();
        midImportService.truncateTable();

        List<BizMidImport> totalDataFromFiles = new ArrayList<>();

        final List<String> multiFiles = importTask.multiFilePath();
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(false);

        try{
            sw.start("解析文件");

            for (String mf:multiFiles){
                File file = new File(mf);
                List<BizMidImport> list = parseDataFromFile(file, params);
                excelTotalSize+=list.size();
                list.forEach(a->a.setBatchNo(batchno));
                //过滤后的excel。有效的手机号码。有效的手机号码还得和库里的号码比对
                importExcelFilter.doFilter(list);
                midImportService.saveBatch(list);

//                totalDataFromFiles.addAll(list);
            }
            sw.stop();



            //TODO 处理数据
            sw.start("计算重复、数据");
            final Integer existInDb = midImportService.phoneExistInDb();
            final Integer valueNum = midImportService.phoneValueNum();
            sw.stop();;
            sw.start("插入号码表");
            midImportService.insertPhoneFromMidImport();
            sw.stop();
            importSummary.setTotal(excelTotalSize);
            //非法的数据=excel数据 - 识别出来的号码总数
//            importSummary.setInvalidNotDup(excelTotalSize-totalDataFromFiles.size());
            importSummary.setValid(valueNum);
            importSummary.setDup(existInDb);
            batchService.save(importSummary.toBatch(batchno));
        }catch (Exception e) {
            //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
            String msg = e.getMessage();
            log.error(msg, e);
            taskStatus = TASK_STATUS_ERROR;
            importSummary.setMsg(e.getMessage());
        } finally {
            importTask.setTaskStatus(taskStatus);
            importTask.setTaskSummary(JSON.toJSONString(importSummary));
            importTask.setMsg(sw.prettyPrint(TimeUnit.SECONDS));
            try {
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
                list.forEach(p->p.autoFillPhone());
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
    @Transactional(rollbackFor = Exception.class)
    public void doimportCallRecord(BizImportTask importTask) {

        StopWatch sw = new StopWatch();
        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();
        importSummary.setBeginTime(new Date());

        // 获取上传文件对象
        File file = new File((importTask.singleFilePath()));
        FileInputStream inputstream = null;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(false);
        String taskStatus = TASK_STATUS_NORMAL;
        try {

            // 2正常结束。99异常
            if(file.exists()){
                sw.start("解析文件");
                inputstream = new FileInputStream(file);
                final ExcelImportResult<BizCallRecords> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizCallRecords.class, params);
//                final Workbook workbook = objectExcelImportResult.getWorkbook();
                //原始的excel数据
                List<BizCallRecords> list = objectExcelImportResult.getList();
                sw.stop();
                list.forEach(a->a.setBatchNo(batchno));
//                final ImportExcelFilter<BizMidImport> importExcelFilter = buildFilter();
                //过滤后的excel。有效的手机号码。有效的手机号码还得和库里的号码比对

                long start = System.currentTimeMillis();
                sw.start("插入数据");
                callRecordsService.saveBatch(list);
                sw.stop();
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //1，更新客户类型：成功客户、失败客户
                //2，更新客户姓名（非空）
                //3.更新地址，2024-03-27 20:17:10 新增
                sw.start("更新号码表");
                callRecordsService.updatePhoneByCallRecords(batchno);
                sw.stop();
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
            importTask.setMsg(sw.prettyPrint(TimeUnit.SECONDS));
            try {
                if(inputstream!=null)
                    inputstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 执行导入运单
     * @param importTask
     */
    @Transactional(rollbackFor = Exception.class)
    public void doimportTransferRecord(BizImportTask importTask) {
        StopWatch sw = new StopWatch();
        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();
        importSummary.setBeginTime(new Date());

        // 获取上传文件对象
        File file = new File((importTask.singleFilePath()));
        FileInputStream inputstream = null;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(false);
        try {

            // 2正常结束。99异常
            String taskStatus = TASK_STATUS_NORMAL;
            if(file.exists()){
                sw.start("解析文件");
                inputstream = new FileInputStream(file);
                final ExcelImportResult<BizTransferRecordTmp> objectExcelImportResult = ExcelImportUtil.importExcelVerify(inputstream, BizTransferRecordTmp.class, params);
                final Workbook workbook = objectExcelImportResult.getWorkbook();
                //原始的excel数据
                List<BizTransferRecordTmp> list = objectExcelImportResult.getList();
                sw.stop();
                list.forEach(a->a.setBatchNo(batchno));

                long start = System.currentTimeMillis();
                sw.start("保存中间表");
                //先清空数据，避免之前的bug遗留数据
                transferRecordTmpService.truncate();
                transferRecordTmpService.saveBatch(list);
                sw.stop();
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //1，更新客户地址：
                sw.start("更新号码表");
                transferRecordTmpService.insertFromTmpUpdatePhoneTableUpdateTransferStatus();
                sw.stop();


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
            importTask.setMsg(sw.prettyPrint(TimeUnit.SECONDS));
            try {
                if(inputstream!=null)
                    inputstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)

    public void doimportBlackPhone(BizImportTask importTask) {

        final String batchno = importTask.getBatchNo();
        ImportSummary importSummary = new ImportSummary();

        // 获取上传文件对象
        File file = new File((importTask.singleFilePath()));
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


    /**
     * http://localhost:3100/jeecgboot/wxf/bizExportRecord/list?column=createTime&order=desc&pageNo=1&pageSize=10&batchNo=12&createTime[]=2024-03-11+17:03:12&createTime[]=2024-03-16+17:03:18&excludeCity=130100,130400&tqsx=2&jtcs=4&wjt=9&tqsj=8&tqsl=6&_t=1710149348334
     *
     *
     * @param importTask
     */
    private void doexportPhone(BizImportTask importTask) {
        StopWatch sw = new StopWatch();
        sw.start("准备工作");
        String taskStatus = TASK_STATUS_NORMAL;
        final int pageSize = 10000;


        List<BizPhone> totalData = new ArrayList<>();
        final Map<String, String[]> param = JSON.parseObject(importTask.getTaskSummary(), new TypeReference<Map<String, String[]>>() {
        });

        QueryWrapper<BizPhone> queryWrapper = buildQwWhenExport(param);

        //提取顺序是否按照入库时间，否：随机，是：根据入库时间
        boolean isOrderByInserTime = false;
        if(param.get("tqsx")!=null && StringUtils.equalsIgnoreCase("rksj",((String[])param.get("tqsx"))[0])){
            queryWrapper.orderByDesc(" create_time ");
            isOrderByInserTime = true;
        }



        Page<BizPhone> pageParam = new Page<BizPhone>(1, pageSize);
        Page<BizPhone> pageData = this.page(pageParam, queryWrapper);
        final long pages = pageData.getPages();
        //记录最后一页的数量
        int lastPageNo = (int)pages;
        final long total = pageData.getTotal();


//        tqsl 缺失：提取数量
        //默认提取数量10000
        //计算球提取数量
        int tqsl = calTqsl(param, (int) pageParam.getTotal());

        




        Workbook workbook = null;
        FileOutputStream out = null;

        // 最后一页需要取多少数据，大于0才有效
        final int lastPageSize = tqsl % pageSize;
        //数据库查询次数
        final int queryTimes = tqsl / pageSize + (lastPageSize == 0 ? 0 : 1);
        List<Integer> pageNos = new ArrayList<>();
        if(isOrderByInserTime==true){
            //根据时间取前面几个
            for (int i = 1; i <= queryTimes; i++) {
                pageNos.add(i);
            }
        }else{
            List<Integer> allPageNo = new ArrayList<>();
            //随机
            int pagesToRandom = (int) pages;

            if(queryTimes>pages){
                //要查询的次数大于页码总数，去掉最后一页（解决取到最后一页数量不足的bug）
                pagesToRandom = pagesToRandom -1;
            }
            for (int i = 1; i <= pagesToRandom; i++) {
                allPageNo.add(i);
            }
            Collections.shuffle(allPageNo);
            Collections.shuffle(pageData.getRecords());
            pageNos = allPageNo.subList(0, queryTimes);
            Collections.sort(pageNos);
        }



        ExportParams exportParams=new ExportParams(null, null , "sheet1");
        exportParams.setType(ExcelType.XSSF);
//        if( pageData.getTotal()==0L){
//            return ;
//        }
//        final Class<?> pojoClass = pageData.getRecords().get(0).getClass();

        //获取文件名
        final String filePath = generateFileName(BizPhone.class);


        sw.stop();
        sw.start("取数");
        for (int i = 0; i < queryTimes; i++) {
//            if(i!=0){
            //2024-04-13 22:58:24 第一次循环的时候不进行查询，把前面查询的第一页作为最终数据。但是会导致偶尔出现第一页被查询了2次的问题
                pageParam.setCurrent(pageNos.get(i));
                pageParam.setSearchCount(false);
                pageParam.setTotal(total);
                pageData = this.page(pageParam, queryWrapper);
//            }
            //处理每次查出来的数据
            if(i==queryTimes-1 && lastPageSize>0){
                //最后一页
                totalData.addAll(pageData.getRecords().subList(0,lastPageSize));
            }else{

                totalData.addAll(pageData.getRecords());
            }

        }
        sw.stop();

        try {
            out = new FileOutputStream(new File(filePath));
            final int listSize = totalData.size();
            //更新最近提取时间
            final List<String> idsToBeUpdate = totalData.stream().map(p -> p.getId()).collect(Collectors.toList());
            sw.start("写excel");
            totalData.forEach(p->{if(StringUtils.isBlank(p.getPrice())){
                p.setPrice("39");
            }
            });
            workbook=exportDataToExcel(workbook,out, totalData);
            sw.stop();

            sw.start("更新导出时间");

            //导出成功后更新最近导出时间，仅仅更新最近导出时间
//            this.saveOrUpdateBatch(toBeUpdateExportTime,5000);
            final List<List<String>> partition = Lists.partition(idsToBeUpdate, 10000);
            partition.forEach(p->this.baseMapper.updateExportTimeBatch(p));


            sw.stop();
//            this.updateBatchById(toBeUpdateExportTime);
            importTask.setFilePath(filePath);
            QueryWrapper<BizExportRecord> p = new QueryWrapper<>();
            p.eq("batch_no",importTask.getBatchNo());
            BizExportRecord one = exportRecordService.getOne(p, false);
            if(one==null){
                one = new BizExportRecord();
            }
            one.setSize(listSize);
            one.setFileAddress(filePath);
            one.setExportTime(new Date());
            one.setBatchNo(importTask.getBatchNo());
            exportRecordService.saveOrUpdate(one);

            log.info(sw.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            taskStatus = TASK_STATUS_ERROR;
        }finally {
            try {
                if(workbook!=null){
                    workbook.close();
                    if (workbook instanceof SXSSFWorkbook) {
                        SXSSFWorkbook w = (SXSSFWorkbook) workbook;
                        w.dispose();
                    }
                }
                if(out!=null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            importTask.setTaskStatus(taskStatus);
            importTask.setMsg(sw.prettyPrint(TimeUnit.SECONDS));
        }



//
//        if(isOrderByInserTime){
//            //非随机，只需要取符合条件的数量即可
//            Page<BizPhone> page = new Page<BizPhone>(1, tqsl);
//            list = this.page(page, queryWrapper).getRecords();
//        }else{
//            //随机取数
//            list = list(queryWrapper);
//            if(list.size()>tqsl){
//                //数量超过要取的数量，需要随机操作
//                Collections.shuffle(list);
//                list = list.subList(0, tqsl);
//            }
//        }



        //上面是列表数量，下面是excel操作


    }

    private static int calTqsl(Map<String, String[]> param, int dbTotal) {
        int tqsl = DEFAULT_EXPORT_SIZE;
        if(param.get("tqsl")!=null && StringUtils.isNotBlank(((String[]) param.get("tqsl"))[0])) {
            tqsl = Integer.parseInt(((String[]) param.get("tqsl"))[0]);
        }
        if(tqsl> dbTotal){
            //要取的数量大于总量，取全部
            tqsl = dbTotal;
        }
        return tqsl;
    }

    @NotNull
    private String generateFileName(Class<?> pojoClass) {
        String dir = PoiPublicUtil.getWebRootPath(getSaveExcelUrl(pojoClass, "download/excelDownload"));
        SimpleDateFormat format = new SimpleDateFormat("yyyMMddHHmmss");
        final String filePath = dir + "/" + format.format(new Date()) + "_" + Math.round(Math.random() * 100000) +".xlsx";
        autoCreateDirAndFile(new File(filePath));
        return filePath;
    }

    /**
     *
     * @param param request中的参数
     * @return
     */
    @NotNull
    public static QueryWrapper<BizPhone> buildQwWhenExport(Map<String, String[]> param) {
        // Step.1 组装查询条件
        QueryWrapper<BizPhone> queryWrapper =  new QueryWrapper<BizPhone>();

//        默认：
//        1.已成单（客户状态是成功客户）的不提
//        2.黑名单不提
//        3.女不提  2024-03-24 22:49:30改成前台传参
//        查询条件：
//        batchNo 提取指定批次（输入批次号）
//        createTime 入库时间：（选择年月日区间，默认历史到今天，可以修改）
//        tqsx 取料顺序：（选择随机or入库时间（先近后远））
//        excludeCity 排除城市：（选择不需要的城市）
//        排除接通：
//        jtcs 接通次数不大于（N）次（填写框输入）：外呼记录里次数不大于N次的可以提
//        wjt 近（N）天无接通（填写框输入）：库里记录号码最近的外呼时间，近N天内无接通的数据可以提
//        tqsj 近（N）天数据不取（填写框输入）：库里记录号码最近的提取时间，近N天已经提过的数据不取
//        tqsl 缺失：提取数量

        //        默认：
        //        1.已成单（客户状态是成功客户）的不提
        //        2.黑名单不提
        //        3.女不提 2024-03-24 22:49:30改成前台传参
        ;
        queryWrapper.eq("del_flag","0");
        queryWrapper.and(w->{w.ne("client_status","cg").or().isNull("client_status");});
        queryWrapper.and(w->{w.eq("black","0").or().isNull("black");});
//        queryWrapper.and(w->w.ne("gender","2").or().isNull("gender"));//2024-03-24 22:49:30改成前台传参

        //男1 女2  非女99
        if(param.get("gender")!=null && StringUtils.isNotBlank(((String[]) param.get("gender"))[0])) {
            final String gender = ((String[]) param.get("gender"))[0];
            if(gender.equalsIgnoreCase("99")){
                //非女
                queryWrapper.and(w->w.ne("gender","2").or().isNull("gender"));//2024-03-24 22:49:30改成前台传参
            }else{
                //男或者女
                queryWrapper.eq("gender",gender);
            }

        }

        //        batchNo 提取指定批次（输入批次号）
        if(param.get("batchNo")!=null && StringUtils.isNotBlank(((String[]) param.get("batchNo"))[0]))queryWrapper.eq("batch_no", param.get("batchNo")[0]);
//        createTime 入库时间：（选择年月日区间，默认历史到今天，可以修改）
//        if(param.get("createTime")!=null && StringUtils.isNotBlank(param.get("batchNo").toString()))queryWrapper.eq("batchNo",param.get("batchNo").toString());
        //        tqsx 取料顺序：（选择随机or入库时间（先近后远））

//        city_exclude 排除城市：（选择不需要的城市）excludeCity
        if(param.get("excludeCity")!=null && StringUtils.isNotBlank(((String[]) param.get("excludeCity"))[0])) {
            final String cityExclude = ((String[]) param.get("excludeCity"))[0];
            List<String> citys = new ArrayList<>();
            final String[] split = cityExclude.split(",");
            for (int i = 0; i < split.length; i++) {
                String cityShort = split[i];
                if(cityShort.length()<6){
                    continue;
                }
//                final StringBuffer citylong = new StringBuffer("").append(cityShort.substring(0, 2)).append("0000,").append(cityShort.substring(0,4)).append("00,").append(cityShort.substring(0, 5)).append("1");
//                citys.add(citylong.toString());
                //2024-03-24 22:58:15 城市改成短码了
                citys.add(cityShort);
            }
            if(citys.size()>0){
                queryWrapper.notIn("city_code",citys);
            }
        }
//        excludeProvince 排除省份：（选择不需要的省份）
        if(param.get("excludeProvince")!=null && StringUtils.isNotBlank(((String[]) param.get("excludeProvince"))[0])) {
            final String province_exclude = ((String[]) param.get("excludeProvince"))[0];
            List<String> ps = new ArrayList<>();
            final String[] split = province_exclude.split(",");
            for (int i = 0; i < split.length; i++) {
                String p = split[i];
                if(p.length()<6){
                    continue;
                }
                ps.add(p);
            }
            if(ps.size()>0){
                queryWrapper.notIn("province_code",ps);
            }
        }
        //提取时间范围createTime
        if(param.get("createTime[]")!=null  && StringUtils.isNotBlank(((String[]) param.get("createTime[]"))[0])  && ((String[]) param.get("createTime[]")).length>1 ) {
            final String createTimeBegin = ((String[]) param.get("createTime[]"))[0];
            final String createTimeEnd = ((String[]) param.get("createTime[]"))[1];
            queryWrapper.between("create_time",createTimeBegin,createTimeEnd);
        }

//        jtcs 接通次数不大于（N）次（填写框输入）：外呼记录里次数不大于N次的可以提
        if(param.get("jtcs")!=null && StringUtils.isNotBlank(((String[]) param.get("jtcs"))[0])) {
            queryWrapper.le("on_count",Integer.parseInt(((String[]) param.get("jtcs"))[0]));
        }
//        wjt 近（N）天无接通（填写框输入）：库里记录号码最近的外呼时间，近N天内无接通的数据可以提
        if(param.get("wjt")!=null && StringUtils.isNotBlank(((String[]) param.get("wjt"))[0])) {
            final int daysRecent = Integer.parseInt(((String[]) param.get("wjt"))[0]);
            final Date dateBeforeDays = DateUtils.addDays(new Date(), daysRecent * -1);
            queryWrapper.and(w->{w.lt("recent_on_time", dateBeforeDays).or().isNull("recent_on_time");});
        }
//        tqsj 近（N）天数据不取（填写框输入）：库里记录号码最近的提取时间，近N天已经提过的数据不取
        if(param.get("tqsj")!=null && StringUtils.isNotBlank(((String[]) param.get("tqsj"))[0])) {
            final int daysRecent = Integer.parseInt(((String[]) param.get("tqsj"))[0]);
            final Date dateBeforeDays = DateUtils.addDays(new Date(), daysRecent * -1);
            queryWrapper.and(w->{w.lt("last_export_time", dateBeforeDays).or().isNull("last_export_time");});
        }
        return queryWrapper;
    }


    /**
     * 数据导出为excel
     * @param dataList
     * @throws Exception
     */
    public Workbook exportDataToExcel(Workbook workbook,FileOutputStream out,List dataList) throws Exception {
        ExportParams exportParams=new ExportParams(null, null , "sheet1");
        exportParams.setType(ExcelType.XSSF);

        try {
            workbook = ExcelExportUtil.exportExcel(exportParams, BizPhone.class, dataList, new String[]{"clientName","phone","address","price","zjbz","gender"});
            workbook.write(out);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchs(List<String> ids){
        final List<BizImportBatch> bizImportBatches = batchService.listByIds(ids);
        batchService.removeByIds(ids);
        for (BizImportBatch bib :bizImportBatches){
            delBatch(bib.getBatchNo());
        }

    }


    @Override
    public void updateBatch(String oldBatchNo, String newBatchNo){
        this.baseMapper.updateBatch(oldBatchNo,newBatchNo);
    }


    @Override
    public void  buildPage(Page page){
        try {
            QueryWrapper<SysDict> qw = new QueryWrapper();
            qw.eq("dict_name","系统配置");
            final SysDict sd = sysDictSsysDictServiceervice.getOne(qw,false);
            if(sd==null || "是".equalsIgnoreCase(sd.getDescription())){
                //开启分页
            }else{
                //关闭分页
                final Integer total = this.baseMapper.cacheCount();
                page.setSearchCount(false);
                page.setTotal(total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
