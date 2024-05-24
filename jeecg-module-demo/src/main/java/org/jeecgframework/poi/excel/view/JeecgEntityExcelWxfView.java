package org.jeecgframework.poi.excel.view;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.modules.demo.wxf.entity.BizExportRecord;
import org.jeecg.modules.demo.wxf.service.IBizExportRecordService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.export.ExcelExportServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/6 17:27
 * @description:
 */
@Controller("jeecgEntityExcelWxfView")
public class JeecgEntityExcelWxfView  extends MiniAbstractExcelView {
    private IBizExportRecordService service;
    public JeecgEntityExcelWxfView() {
    }

    public JeecgEntityExcelWxfView(IBizExportRecordService service) {
        this.service = service;
    }



    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件";
//        Workbook workbook = null;
        String[] exportFields = null;
        Object exportFieldStr = model.get("exportFields");
        if (exportFieldStr != null && exportFieldStr != "") {
            exportFields = exportFieldStr.toString().split(",");
        }
        final String exportId = (String) model.get("exportId");
        if(exportId==null || StringUtils.isBlank(exportId.toString())){
            return ;
        }
        String id = exportId.toString();
        BizExportRecord record = service.getById(id);
        if(StringUtils.isBlank(record.getFileAddress())){
            return ;
        }
        final FileInputStream is = new FileInputStream(new File(record.getFileAddress()));











//        workbook =  new XSSFWorkbook(is);

        if (model.containsKey("fileName")) {
            codedFileName = (String)model.get("fileName");
        }

        codedFileName = codedFileName + ".xlsx";

        if (this.isIE(request)) {
            codedFileName = URLEncoder.encode(codedFileName, "UTF8");
        } else {
            codedFileName = new String(codedFileName.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
        ServletOutputStream out = response.getOutputStream();

        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }



        out.flush();
    }

}
