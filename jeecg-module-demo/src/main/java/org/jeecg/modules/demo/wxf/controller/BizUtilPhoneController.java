package org.jeecg.modules.demo.wxf.controller;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.StopWatch;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.entity.BizUtilPhone;
import org.jeecg.modules.demo.wxf.service.IBizUtilPhoneService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 号码所属区域表
 * @Author: jeecg-boot
 * @Date: 2024-02-29
 * @Version: V1.0
 */
@Api(tags = "号码所属区域表")
@RestController
@RequestMapping("/wxf/bizUtilPhone")
@Slf4j
public class BizUtilPhoneController extends JeecgController<BizUtilPhone, IBizUtilPhoneService> {
    @Autowired
    private IBizUtilPhoneService bizUtilPhoneService;

    /**
     * 分页列表查询
     *
     * @param bizUtilPhone
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "号码所属区域表-分页列表查询")
    @ApiOperation(value = "号码所属区域表-分页列表查询", notes = "号码所属区域表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<BizUtilPhone>> queryPageList(BizUtilPhone bizUtilPhone,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<BizUtilPhone> queryWrapper = QueryGenerator.initQueryWrapper(bizUtilPhone, req.getParameterMap());
        Page<BizUtilPhone> page = new Page<BizUtilPhone>(pageNo, pageSize);
        IPage<BizUtilPhone> pageList = bizUtilPhoneService.page(page, queryWrapper);
        final Map<String, BizUtilPhone> f = PhoneUtil.f();
        log.info("号码总量:{}", f.size());

        importExcelModify();
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param bizUtilPhone
     * @return
     */
    @AutoLog(value = "号码所属区域表-添加")
    @ApiOperation(value = "号码所属区域表-添加", notes = "号码所属区域表-添加")
    @RequiresPermissions("wxf:biz_util_phone:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody BizUtilPhone bizUtilPhone) {
        bizUtilPhoneService.save(bizUtilPhone);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param bizUtilPhone
     * @return
     */
    @AutoLog(value = "号码所属区域表-编辑")
    @ApiOperation(value = "号码所属区域表-编辑", notes = "号码所属区域表-编辑")
    @RequiresPermissions("wxf:biz_util_phone:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody BizUtilPhone bizUtilPhone) {
        bizUtilPhoneService.updateById(bizUtilPhone);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "号码所属区域表-通过id删除")
    @ApiOperation(value = "号码所属区域表-通过id删除", notes = "号码所属区域表-通过id删除")
    @RequiresPermissions("wxf:biz_util_phone:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        bizUtilPhoneService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "号码所属区域表-批量删除")
    @ApiOperation(value = "号码所属区域表-批量删除", notes = "号码所属区域表-批量删除")
    @RequiresPermissions("wxf:biz_util_phone:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bizUtilPhoneService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "号码所属区域表-通过id查询")
    @ApiOperation(value = "号码所属区域表-通过id查询", notes = "号码所属区域表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BizUtilPhone> queryById(@RequestParam(name = "id", required = true) String id) {
        BizUtilPhone bizUtilPhone = bizUtilPhoneService.getById(id);
        if (bizUtilPhone == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bizUtilPhone);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param bizUtilPhone
     */
    @RequiresPermissions("wxf:biz_util_phone:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizUtilPhone bizUtilPhone) {
        return super.exportXls(request, bizUtilPhone, BizUtilPhone.class, "号码所属区域表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("wxf:biz_util_phone:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizUtilPhone.class);
    }


    public void importExcelModify() {
        // 获取上传文件对象
        File file = new File("/Users/qianshihua/Downloads/测试data/1/导入.xlsx");
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedSave(true);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            log.info("即将开始解析excel");

            long start = System.currentTimeMillis();
            List<BizPhone> list = ExcelImportUtil.importExcel(inputStream, BizPhone.class, params);
            log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");

            ImportSummary is = new ImportSummary(list.size());
            StopWatch sw = new StopWatch();
            sw.start("分析列表");
            for (BizPhone p : list) {
                final String s = PhoneUtil.detectPhone(p.getPhone());
                if (s == null) {
                    is.invalid(1);
                }
            }
            sw.stop();
            log.info(sw.prettyPrint());


        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
