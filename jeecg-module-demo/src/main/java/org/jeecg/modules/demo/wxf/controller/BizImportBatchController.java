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
import org.jeecg.modules.demo.wxf.entity.BizImportBatch;
import org.jeecg.modules.demo.wxf.service.IBizImportBatchService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.wxf.service.IBizPhoneService;
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
 * @Description: 导入批次表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Api(tags="导入批次表")
@RestController
@RequestMapping("/wxf/bizImportBatch")
@Slf4j
public class  BizImportBatchController extends JeecgController<BizImportBatch, IBizImportBatchService> {
	@Autowired
	private IBizImportBatchService bizImportBatchService;

	@Autowired
	private IBizPhoneService bizPhoneService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizImportBatch
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "导入批次表-分页列表查询")
	@ApiOperation(value="导入批次表-分页列表查询", notes="导入批次表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizImportBatch>> queryPageList(BizImportBatch bizImportBatch,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizImportBatch> queryWrapper = QueryGenerator.initQueryWrapper(bizImportBatch, req.getParameterMap());
		Page<BizImportBatch> page = new Page<BizImportBatch>(pageNo, pageSize);
		IPage<BizImportBatch> pageList = bizImportBatchService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizImportBatch
	 * @return
	 */
	@AutoLog(value = "导入批次表-添加")
	@ApiOperation(value="导入批次表-添加", notes="导入批次表-添加")
	@RequiresPermissions("wxf:biz_import_batch:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizImportBatch bizImportBatch) {
		bizImportBatchService.save(bizImportBatch);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizImportBatch
	 * @return
	 */
	@AutoLog(value = "导入批次表-编辑")
	@ApiOperation(value="导入批次表-编辑", notes="导入批次表-编辑")
	@RequiresPermissions("wxf:biz_import_batch:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizImportBatch bizImportBatch) {
		bizImportBatchService.updateBatch(bizImportBatch);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "导入批次表-通过id删除")
	@ApiOperation(value="导入批次表-通过id删除", notes="导入批次表-通过id删除")
	@RequiresPermissions("wxf:biz_import_batch:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		this.bizPhoneService.delBatchs(Arrays.asList(id));
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "导入批次表-批量删除")
	@ApiOperation(value="导入批次表-批量删除", notes="导入批次表-批量删除")
	@RequiresPermissions("wxf:biz_import_batch:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.bizImportBatchService.removeByIds(Arrays.asList(ids.split(",")));
		bizPhoneService.delBatchs(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "导入批次表-通过id查询")
	@ApiOperation(value="导入批次表-通过id查询", notes="导入批次表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizImportBatch> queryById(@RequestParam(name="id",required=true) String id) {
		BizImportBatch bizImportBatch = bizImportBatchService.getById(id);
		if(bizImportBatch==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizImportBatch);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizImportBatch
    */
    @RequiresPermissions("wxf:biz_import_batch:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizImportBatch bizImportBatch) {
        return super.exportXls(request, bizImportBatch, BizImportBatch.class, "导入批次表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_import_batch:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizImportBatch.class);
    }

}
