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
import org.jeecg.modules.demo.wxf.entity.BizTransferRecordTmp;
import org.jeecg.modules.demo.wxf.service.IBizTransferRecordTmpService;

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
 * @Description: biz_transfer_record_tmp
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
@Api(tags="biz_transfer_record_tmp")
@RestController
@RequestMapping("/wxf/bizTransferRecordTmp")
@Slf4j
public class BizTransferRecordTmpController extends JeecgController<BizTransferRecordTmp, IBizTransferRecordTmpService> {
	@Autowired
	private IBizTransferRecordTmpService bizTransferRecordTmpService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizTransferRecordTmp
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "biz_transfer_record_tmp-分页列表查询")
	@ApiOperation(value="biz_transfer_record_tmp-分页列表查询", notes="biz_transfer_record_tmp-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizTransferRecordTmp>> queryPageList(BizTransferRecordTmp bizTransferRecordTmp,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizTransferRecordTmp> queryWrapper = QueryGenerator.initQueryWrapper(bizTransferRecordTmp, req.getParameterMap());
		Page<BizTransferRecordTmp> page = new Page<BizTransferRecordTmp>(pageNo, pageSize);
		IPage<BizTransferRecordTmp> pageList = bizTransferRecordTmpService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizTransferRecordTmp
	 * @return
	 */
	@AutoLog(value = "biz_transfer_record_tmp-添加")
	@ApiOperation(value="biz_transfer_record_tmp-添加", notes="biz_transfer_record_tmp-添加")
	@RequiresPermissions("wxf:biz_transfer_record_tmp:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizTransferRecordTmp bizTransferRecordTmp) {
		bizTransferRecordTmpService.save(bizTransferRecordTmp);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizTransferRecordTmp
	 * @return
	 */
	@AutoLog(value = "biz_transfer_record_tmp-编辑")
	@ApiOperation(value="biz_transfer_record_tmp-编辑", notes="biz_transfer_record_tmp-编辑")
	@RequiresPermissions("wxf:biz_transfer_record_tmp:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizTransferRecordTmp bizTransferRecordTmp) {
		bizTransferRecordTmpService.updateById(bizTransferRecordTmp);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "biz_transfer_record_tmp-通过id删除")
	@ApiOperation(value="biz_transfer_record_tmp-通过id删除", notes="biz_transfer_record_tmp-通过id删除")
	@RequiresPermissions("wxf:biz_transfer_record_tmp:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizTransferRecordTmpService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "biz_transfer_record_tmp-批量删除")
	@ApiOperation(value="biz_transfer_record_tmp-批量删除", notes="biz_transfer_record_tmp-批量删除")
	@RequiresPermissions("wxf:biz_transfer_record_tmp:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizTransferRecordTmpService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "biz_transfer_record_tmp-通过id查询")
	@ApiOperation(value="biz_transfer_record_tmp-通过id查询", notes="biz_transfer_record_tmp-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizTransferRecordTmp> queryById(@RequestParam(name="id",required=true) String id) {
		BizTransferRecordTmp bizTransferRecordTmp = bizTransferRecordTmpService.getById(id);
		if(bizTransferRecordTmp==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizTransferRecordTmp);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizTransferRecordTmp
    */
    @RequiresPermissions("wxf:biz_transfer_record_tmp:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizTransferRecordTmp bizTransferRecordTmp) {
        return super.exportXls(request, bizTransferRecordTmp, BizTransferRecordTmp.class, "biz_transfer_record_tmp");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_transfer_record_tmp:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizTransferRecordTmp.class);
    }

}
