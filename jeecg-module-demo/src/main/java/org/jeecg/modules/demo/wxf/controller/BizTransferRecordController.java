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
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import org.jeecg.modules.demo.wxf.entity.BizTransferRecord;
import org.jeecg.modules.demo.wxf.service.IBizPhoneService;
import org.jeecg.modules.demo.wxf.service.IBizTransferRecordService;

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
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Api(tags="运单记录")
@RestController
@RequestMapping("/wxf/bizTransferRecord")
@Slf4j
public class BizTransferRecordController extends JeecgController<BizTransferRecord, IBizTransferRecordService> {

	@Autowired
	 private IBizPhoneService bizPhoneService;


	 @Autowired
	private IBizTransferRecordService bizTransferRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizTransferRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "运单记录-分页列表查询")
	@ApiOperation(value="运单记录-分页列表查询", notes="运单记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizTransferRecord>> queryPageList(BizTransferRecord bizTransferRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizTransferRecord> queryWrapper = QueryGenerator.initQueryWrapper(bizTransferRecord, req.getParameterMap());
		Page<BizTransferRecord> page = new Page<BizTransferRecord>(pageNo, pageSize);
		IPage<BizTransferRecord> pageList = bizTransferRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizTransferRecord
	 * @return
	 */
	@AutoLog(value = "运单记录-添加")
	@ApiOperation(value="运单记录-添加", notes="运单记录-添加")
	@RequiresPermissions("wxf:biz_transfer_record:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizTransferRecord bizTransferRecord) {
		bizTransferRecordService.save(bizTransferRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizTransferRecord
	 * @return
	 */
	@AutoLog(value = "运单记录-编辑")
	@ApiOperation(value="运单记录-编辑", notes="运单记录-编辑")
	@RequiresPermissions("wxf:biz_transfer_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizTransferRecord bizTransferRecord) {
		bizTransferRecordService.updateById(bizTransferRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "运单记录-通过id删除")
	@ApiOperation(value="运单记录-通过id删除", notes="运单记录-通过id删除")
	@RequiresPermissions("wxf:biz_transfer_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizTransferRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "运单记录-批量删除")
	@ApiOperation(value="运单记录-批量删除", notes="运单记录-批量删除")
	@RequiresPermissions("wxf:biz_transfer_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizTransferRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "运单记录-通过id查询")
	@ApiOperation(value="运单记录-通过id查询", notes="运单记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizTransferRecord> queryById(@RequestParam(name="id",required=true) String id) {
		BizTransferRecord bizTransferRecord = bizTransferRecordService.getById(id);
		if(bizTransferRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizTransferRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizTransferRecord
    */
    @RequiresPermissions("wxf:biz_transfer_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizTransferRecord bizTransferRecord) {
        return super.exportXls(request, bizTransferRecord, BizTransferRecord.class, "运单记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_transfer_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return bizPhoneService.importExcelee(request, response, BizTransferRecord.class);
    }

}
