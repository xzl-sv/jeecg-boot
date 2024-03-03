package org.jeecg.modules.demo.wxf.controller;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.ImportExcelFilter;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.wxf.dto.ImportSummary;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;

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
        return this.importExcel(request, response, BizPhone.class,customImportExcelFilter());
    }


	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @param clazz
	  * @return
	  */
	 @Override
	 protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<BizPhone> clazz) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<T> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
				 //update-begin-author:taoyan date:20190528 for:批量插入数据
				 final List listAfterFilter = customImportExcelFilter().doFilter(list);
				 long start = System.currentTimeMillis();
				 service.saveBatch(listAfterFilter);
				 //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
				 //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 //update-end-author:taoyan date:20190528 for:批量插入数据
				 return Result.ok("文件导入成功！数据行数：" + list.size()+" 过滤后行数:"+listAfterFilter.size());
			 } catch (Exception e) {
				 //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
				 String msg = e.getMessage();
				 log.error(msg, e);
				 if(msg!=null && msg.indexOf("Duplicate entry")>=0){
					 return Result.error("文件导入失败:有重复数据！");
				 }else{
					 return Result.error("文件导入失败:" + e.getMessage());
				 }
				 //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
	 }

	 @Override
	 public ImportExcelFilter customImportExcelFilter() {

		 ImportSummary importSummary;
		 return new ImportExcelFilter<BizPhone>() {
			 /**
			  * 默认返回excel解析之后的全部数据。
			  *
			  * @param list
			  * @return
			  */
			 @Override
			 public List<BizPhone> doFilter(List<BizPhone> list) {
				 final Iterator<BizPhone> iterator = list.iterator();
				 while (iterator.hasNext()){
					 final BizPhone p = iterator.next();
 					 final String validPhone = PhoneUtil.detectPhone(p.getPhone());
					 if(validPhone!=null){
						 p.setPhone(validPhone);
						 PhoneUtil.fillPhoneArea(p);
					 }else{
						 iterator.remove();
					 }

				 }
				 return ImportExcelFilter.super.doFilter(list);
			 }
		 };
	 }
 }
