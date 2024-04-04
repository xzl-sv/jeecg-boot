package org.jeecg.modules.demo.wxf.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.BizExportRecord;
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.service.IBizMidImportService;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.wxf.service.impl.BizPhoneServiceImpl;
import org.jeecg.modules.demo.wxf.util.BatchNoUtil;
import org.jeecg.modules.demo.wxf.util.PhoneUtil;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.entity.result.ExcelImportResult;
import org.jeecgframework.poi.excel.export.ExcelExportServer;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
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

import static org.jeecgframework.poi.excel.entity.enmus.ExcelType.*;

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

	 @Resource
	 private JeecgBaseConfig jeecgBaseConfig;


	@ApiOperation(value="号码资源表-分页列表查询", notes="号码资源表-分页列表查询")
	@GetMapping(value = "/listExport")
	public Result<IPage<BizPhone>> listExport(BizPhone bizPhone,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {


		final Map<String, String[]> parameterMap = req.getParameterMap();
		final String o = JSON.toJSONString(parameterMap);

		final QueryWrapper<BizPhone> queryWrapper = BizPhoneServiceImpl.buildQwWhenExport(parameterMap);

		Page<BizPhone> page = new Page<BizPhone>(pageNo, pageSize);
		IPage<BizPhone> pageList = bizPhoneService.page(page, queryWrapper);



		final String[] rwlxes = parameterMap.get("rwlx");
		if(rwlxes!=null && rwlxes.length>0 && rwlxes[0].equalsIgnoreCase("tj")){
			//选择了提交取数操作
			bizPhoneService.submitExportTask(BizExportRecord.class,o);
		}


		return Result.OK(pageList);
	}


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
		final Map<String, String[]> parameterMap = req.getParameterMap();
		final String provinceCode = bizPhone.getProvinceCode();
		bizPhone.setProvinceCode("");
		QueryWrapper<BizPhone> queryWrapper = QueryGenerator.initQueryWrapper(bizPhone, parameterMap);
		if(StringUtils.isNotBlank(provinceCode)){
			queryWrapper.eq("province_code",provinceCode.substring(0,provinceCode.length()-2)+"01");
		}
		Page<BizPhone> page = new Page<BizPhone>(pageNo, pageSize);
//		page.setSearchCount(false);
//		page.setTotal(11110000);
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
	 * 默认：
	 * 1.已成单（客户状态是成功客户）的不提
	 * 2.黑名单不提
	 * 3.女不提
	 *
	 *
	 * 查询条件：
	 * batchNo 提取指定批次（输入批次号）
	 * createTime 入库时间：（选择年月日区间，默认历史到今天，可以修改）
	 * tqsx 取料顺序：（选择随机or入库时间（先近后远））
	 * city_exclude 排除城市：（选择不需要的城市）
	 * 排除接通：
	 * jtcs 接通次数不大于（N）次（填写框输入）：外呼记录里次数不大于N次的可以提
	 * wjt 近（N）天无接通（填写框输入）：库里记录号码最近的外呼时间，近N天内无接通的数据可以提
	 * tqsj 近（N）天数据不取（填写框输入）：库里记录号码最近的提取时间，近N天已经提过的数据不取
	 *
	 * tqsl 缺失：提取数量
	 *
	 *
	 *
    * 导出excel
    *
    * @param request
    * @param bizPhone
    */
    @RequiresPermissions("wxf:biz_phone:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizPhone bizPhone) {
        return exportXlsCus(request, bizPhone, "号码资源表");
    }

	 public ModelAndView exportXlsCus(HttpServletRequest request, BizPhone object,  String title) {
		 // Step.1 组装查询条件
		 QueryWrapper<BizPhone> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 queryWrapper.in("id",selectionList);
		 }
		 // Step.2 获取导出数据
		 List<BizPhone> exportList = service.list(queryWrapper);

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效 ,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, title);
		 mv.addObject(NormalExcelConstants.CLASS, BizPhone.class);
		 //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
		 ExportParams exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
		 exportParams.setImageBasePath(jeecgBaseConfig.getPath().getUpload());
		 //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
		 mv.addObject(NormalExcelConstants.PARAMS,exportParams);
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
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
