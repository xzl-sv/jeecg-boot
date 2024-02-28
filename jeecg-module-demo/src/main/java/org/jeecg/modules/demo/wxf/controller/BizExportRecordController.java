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
import org.jeecg.modules.demo.wxf.entity.BizExportRecord;
import org.jeecg.modules.demo.wxf.service.IBizExportRecordService;

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
		QueryWrapper<BizExportRecord> queryWrapper = QueryGenerator.initQueryWrapper(bizExportRecord, req.getParameterMap());
		Page<BizExportRecord> page = new Page<BizExportRecord>(pageNo, pageSize);
		IPage<BizExportRecord> pageList = bizExportRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
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
    * 导出excel
    *
    * @param request
    * @param bizExportRecord
    */
    @RequiresPermissions("wxf:biz_export_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizExportRecord bizExportRecord) {
        return super.exportXls(request, bizExportRecord, BizExportRecord.class, "提取记录表");
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

}
