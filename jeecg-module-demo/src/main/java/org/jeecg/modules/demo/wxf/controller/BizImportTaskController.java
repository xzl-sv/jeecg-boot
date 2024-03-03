package org.jeecg.modules.demo.wxf.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.wxf.entity.BizImportTask;
import org.jeecg.modules.demo.wxf.service.IBizImportTaskService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
 * @Description: biz_import_task
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
@Api(tags="biz_import_task")
@RestController
@RequestMapping("/wxf/bizImportTask")
@Slf4j
public class BizImportTaskController extends JeecgController<BizImportTask, IBizImportTaskService> {
	@Autowired
	private IBizImportTaskService bizImportTaskService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizImportTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "biz_import_task-分页列表查询")
	@ApiOperation(value="biz_import_task-分页列表查询", notes="biz_import_task-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizImportTask>> queryPageList(BizImportTask bizImportTask,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizImportTask> queryWrapper = QueryGenerator.initQueryWrapper(bizImportTask, req.getParameterMap());
		Page<BizImportTask> page = new Page<BizImportTask>(pageNo, pageSize);
		IPage<BizImportTask> pageList = bizImportTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizImportTask
	 * @return
	 */
	@AutoLog(value = "biz_import_task-添加")
	@ApiOperation(value="biz_import_task-添加", notes="biz_import_task-添加")
	@RequiresPermissions("wxf:biz_import_task:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizImportTask bizImportTask) {
		bizImportTaskService.save(bizImportTask);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizImportTask
	 * @return
	 */
	@AutoLog(value = "biz_import_task-编辑")
	@ApiOperation(value="biz_import_task-编辑", notes="biz_import_task-编辑")
	@RequiresPermissions("wxf:biz_import_task:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizImportTask bizImportTask) {
		bizImportTaskService.updateById(bizImportTask);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "biz_import_task-通过id删除")
	@ApiOperation(value="biz_import_task-通过id删除", notes="biz_import_task-通过id删除")
	@RequiresPermissions("wxf:biz_import_task:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizImportTaskService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "biz_import_task-批量删除")
	@ApiOperation(value="biz_import_task-批量删除", notes="biz_import_task-批量删除")
	@RequiresPermissions("wxf:biz_import_task:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizImportTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "biz_import_task-通过id查询")
	@ApiOperation(value="biz_import_task-通过id查询", notes="biz_import_task-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizImportTask> queryById(@RequestParam(name="id",required=true) String id) {
		BizImportTask bizImportTask = bizImportTaskService.getById(id);
		if(bizImportTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizImportTask);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizImportTask
    */
    @RequiresPermissions("wxf:biz_import_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizImportTask bizImportTask) {
        return super.exportXls(request, bizImportTask, BizImportTask.class, "biz_import_task");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_import_task:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizImportTask.class);
    }

}
