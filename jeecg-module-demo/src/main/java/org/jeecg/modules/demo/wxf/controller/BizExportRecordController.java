package org.jeecg.modules.demo.wxf.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.demo.wxf.entity.BizExportRecord;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import org.jeecg.modules.demo.wxf.service.IBizExportRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.wxf.service.IBizPhoneService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelWxfView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 提取记录表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Api(tags="提取记录表")
@RestController
@RequestMapping("/wxf/bizExportRecord")
@Slf4j
public class BizExportRecordController extends JeecgController<BizExportRecord, IBizExportRecordService> {
	@Autowired
	private IBizExportRecordService bizExportRecordService;
	 @Resource
	 private JeecgBaseConfig jeecgBaseConfig;

	 @Autowired
	 private IBizPhoneService bizPhoneService;

	 /**
	/**
	 * 分页列表查询
	 *
	 * @param bizExportRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "提取记录表-分页列表查询")
	@ApiOperation(value="提取记录表-分页列表查询", notes="提取记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizExportRecord>> queryPageList(BizExportRecord bizExportRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		final Map<String, String[]> parameterMap = req.getParameterMap();
//		parameterMap.remove()
		final String o = JSON.toJSONString(parameterMap);
		log.info("param is :{}", o);
		final Map<String, String[]> stringMap = JSON.parseObject(o, new TypeReference<Map<String, String[]>>() {
		});
		QueryWrapper<BizExportRecord> queryWrapper = QueryGenerator.initQueryWrapper(bizExportRecord, req.getParameterMap());
		Page<BizExportRecord> page = new Page<BizExportRecord>(pageNo, pageSize);
		bizPhoneService.buildPage(page);
		IPage<BizExportRecord> pageList = bizExportRecordService.page(page, queryWrapper);


		final String[] rwlxes = parameterMap.get("rwlx");
		if(rwlxes!=null && rwlxes.length>0 && rwlxes[0].equalsIgnoreCase("tj")){
			//选择了提交取数操作
			bizPhoneService.submitExportTask(BizExportRecord.class,o);
		}


		return Result.OK(pageList);
	}

	 /**
	  * 提交导出任务
	  * @param req
	  * @return
	  */
	 @ApiOperation(value="提交记录任务", notes="提交记录任务")
	 @GetMapping(value = "/submit")
	 public Result<String> submit(HttpServletRequest req) {

		 final Map<String, String[]> parameterMap = req.getParameterMap();
		 final String o = JSON.toJSONString(parameterMap);

		 bizPhoneService.submitExportTask(BizExportRecord.class,o);
		 return Result.OK("导出任务已提交，稍微将自动执行！");
	 }

	/**
	 *   添加
	 *
	 * @param bizExportRecord
	 * @return
	 */
	@AutoLog(value = "提取记录表-添加")
	@ApiOperation(value="提取记录表-添加", notes="提取记录表-添加")
	@RequiresPermissions("wxf:biz_export_record:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizExportRecord bizExportRecord) {
		bizExportRecordService.save(bizExportRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizExportRecord
	 * @return
	 */
	@AutoLog(value = "提取记录表-编辑")
	@ApiOperation(value="提取记录表-编辑", notes="提取记录表-编辑")
	@RequiresPermissions("wxf:biz_export_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizExportRecord bizExportRecord) {
		bizExportRecordService.updateById(bizExportRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "提取记录表-通过id删除")
	@ApiOperation(value="提取记录表-通过id删除", notes="提取记录表-通过id删除")
	@RequiresPermissions("wxf:biz_export_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizExportRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "提取记录表-批量删除")
	@ApiOperation(value="提取记录表-批量删除", notes="提取记录表-批量删除")
	@RequiresPermissions("wxf:biz_export_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizExportRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "提取记录表-通过id查询")
	@ApiOperation(value="提取记录表-通过id查询", notes="提取记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizExportRecord> queryById(@RequestParam(name="id",required=true) String id) {
		BizExportRecord bizExportRecord = bizExportRecordService.getById(id);
		if(bizExportRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizExportRecord);
	}

	 /**
	  * 点击下载文件
	  *
	  * @param id
	  * @return
	  */
    @RequiresPermissions("wxf:biz_export_record:exportXls")
    @RequestMapping(value = "/downloadExcel")
    public ModelAndView downloadExcel(@RequestParam(name="id",required=true) String id,HttpServletRequest request) {
		request.setAttribute("exportId",id);
		return exportXlsCust(request);
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_export_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizExportRecord.class);
    }


	 /**
	  * 导出excel
	  *
	  */
	 private ModelAndView exportXlsCust(HttpServletRequest request) {
		 final Object exportId = request.getAttribute("exportId");
		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelWxfView(bizExportRecordService));
		 String title = "号码资源";


		 //此处设置的filename无效 ,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, title);
		 mv.addObject(NormalExcelConstants.CLASS, BizPhone.class);
		 //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
		 ExportParams exportParams=new ExportParams(title + "报表", "导出人:wxf" , title);
		 exportParams.setImageBasePath(jeecgBaseConfig.getPath().getUpload());
		 //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
		 mv.addObject(NormalExcelConstants.PARAMS,exportParams);
		 mv.addObject("exportId",exportId);
//		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
	 }

}
