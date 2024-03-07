package org.jeecgframework.poi.excel.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.export.ExcelExportServer;
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

    public JeecgEntityExcelWxfView() {
    }

    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件";
        Workbook workbook = null;
        String[] exportFields = null;
        Object exportFieldStr = model.get("exportFields");
        if (exportFieldStr != null && exportFieldStr != "") {
            exportFields = exportFieldStr.toString().split(",");
        }

        workbook =  new XSSFWorkbook(new FileInputStream(new File("/Users/qianshihua/Documents/develop/code/hui/jeecg-boot/jeecg-module-system/jeecg-system-start/target/classes/upload/excelUpload/BizBalckPhone/20240304195119_64292.XLSX")));

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
        workbook.write(out);
        out.flush();
    }

}
