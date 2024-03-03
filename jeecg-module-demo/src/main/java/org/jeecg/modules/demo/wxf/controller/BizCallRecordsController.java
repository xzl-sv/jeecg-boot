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
import org.jeecg.modules.demo.wxf.entity.BizCallRecords;
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.service.IBizCallRecordsService;

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
 * @Description: 外呼记录
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Api(tags="外呼记录")
@RestController
@RequestMapping("/wxf/bizCallRecords")
@Slf4j
public class BizCallRecordsController extends JeecgController<BizCallRecords, IBizCallRecordsService> {
	@Autowired
	private IBizCallRecordsService bizCallRecordsService;

	 @Autowired
	 private IBizPhoneService bizPhoneService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizCallRecords
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "外呼记录-分页列表查询")
	@ApiOperation(value="外呼记录-分页列表查询", notes="外呼记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizCallRecords>> queryPageList(BizCallRecords bizCallRecords,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizCallRecords> queryWrapper = QueryGenerator.initQueryWrapper(bizCallRecords, req.getParameterMap());
		Page<BizCallRecords> page = new Page<BizCallRecords>(pageNo, pageSize);
		IPage<BizCallRecords> pageList = bizCallRecordsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizCallRecords
	 * @return
	 */
	@AutoLog(value = "外呼记录-添加")
	@ApiOperation(value="外呼记录-添加", notes="外呼记录-添加")
	@RequiresPermissions("wxf:biz_call_records:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizCallRecords bizCallRecords) {
		bizCallRecordsService.save(bizCallRecords);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizCallRecords
	 * @return
	 */
	@AutoLog(value = "外呼记录-编辑")
	@ApiOperation(value="外呼记录-编辑", notes="外呼记录-编辑")
	@RequiresPermissions("wxf:biz_call_records:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizCallRecords bizCallRecords) {
		bizCallRecordsService.updateById(bizCallRecords);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "外呼记录-通过id删除")
	@ApiOperation(value="外呼记录-通过id删除", notes="外呼记录-通过id删除")
	@RequiresPermissions("wxf:biz_call_records:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizCallRecordsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "外呼记录-批量删除")
	@ApiOperation(value="外呼记录-批量删除", notes="外呼记录-批量删除")
	@RequiresPermissions("wxf:biz_call_records:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizCallRecordsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "外呼记录-通过id查询")
	@ApiOperation(value="外呼记录-通过id查询", notes="外呼记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizCallRecords> queryById(@RequestParam(name="id",required=true) String id) {
		BizCallRecords bizCallRecords = bizCallRecordsService.getById(id);
		if(bizCallRecords==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizCallRecords);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizCallRecords
    */
    @RequiresPermissions("wxf:biz_call_records:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizCallRecords bizCallRecords) {
        return super.exportXls(request, bizCallRecords, BizCallRecords.class, "外呼记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_call_records:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return bizPhoneService.importExcelee(request, response, BizCallRecords.class);
    }

}
