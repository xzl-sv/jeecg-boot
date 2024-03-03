package org.jeecg.modules.demo.wxf.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.service.IBizMidImportService;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.wxf.util.BatchNoUtil;
import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.entity.result.ExcelImportResult;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-02-29
 * @Version: V1.0
 */
@Api(tags="号码资源表")
@RestController
@RequestMapping("/wxf/bizPhone")
@Slf4j
public class BizPhoneController extends JeecgController<BizPhone, IBizPhoneService> {
	@Autowired
	private IBizPhoneService bizPhoneService;

	@Autowired
	private IBizMidImportService bizMidImportService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizPhone
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "号码资源表-分页列表查询")
	@ApiOperation(value="号码资源表-分页列表查询", notes="号码资源表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizPhone>> queryPageList(BizPhone bizPhone,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizPhone> queryWrapper = QueryGenerator.initQueryWrapper(bizPhone, req.getParameterMap());
		Page<BizPhone> page = new Page<BizPhone>(pageNo, pageSize);
		IPage<BizPhone> pageList = bizPhoneService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizPhone
	 * @return
	 */
	@AutoLog(value = "号码资源表-添加")
	@ApiOperation(value="号码资源表-添加", notes="号码资源表-添加")
	@RequiresPermissions("wxf:biz_phone:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizPhone bizPhone) {
		bizPhoneService.save(bizPhone);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizPhone
	 * @return
	 */
	@AutoLog(value = "号码资源表-编辑")
	@ApiOperation(value="号码资源表-编辑", notes="号码资源表-编辑")
	@RequiresPermissions("wxf:biz_phone:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizPhone bizPhone) {
		bizPhoneService.updateById(bizPhone);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "号码资源表-通过id删除")
	@ApiOperation(value="号码资源表-通过id删除", notes="号码资源表-通过id删除")
	@RequiresPermissions("wxf:biz_phone:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizPhoneService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "号码资源表-批量删除")
	@ApiOperation(value="号码资源表-批量删除", notes="号码资源表-批量删除")
	@RequiresPermissions("wxf:biz_phone:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizPhoneService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "号码资源表-通过id查询")
	@ApiOperation(value="号码资源表-通过id查询", notes="号码资源表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizPhone> queryById(@RequestParam(name="id",required=true) String id) {
		BizPhone bizPhone = bizPhoneService.getById(id);
		if(bizPhone==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizPhone);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizPhone
    */
    @RequiresPermissions("wxf:biz_phone:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizPhone bizPhone) {
        return super.exportXls(request, bizPhone, BizPhone.class, "号码资源表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_phone:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return bizPhoneService.importExcelee(request, response,BizMidImport.class);
    }




 }
