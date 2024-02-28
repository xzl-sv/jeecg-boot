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
import org.jeecg.modules.demo.wxf.entity.BizBalckPhone;
import org.jeecg.modules.demo.wxf.service.IBizBalckPhoneService;

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
 * @Description: 黑名单表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Api(tags="黑名单表")
@RestController
@RequestMapping("/wxf/bizBalckPhone")
@Slf4j
public class BizBalckPhoneController extends JeecgController<BizBalckPhone, IBizBalckPhoneService> {
	@Autowired
	private IBizBalckPhoneService bizBalckPhoneService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bizBalckPhone
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "黑名单表-分页列表查询")
	@ApiOperation(value="黑名单表-分页列表查询", notes="黑名单表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BizBalckPhone>> queryPageList(BizBalckPhone bizBalckPhone,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BizBalckPhone> queryWrapper = QueryGenerator.initQueryWrapper(bizBalckPhone, req.getParameterMap());
		Page<BizBalckPhone> page = new Page<BizBalckPhone>(pageNo, pageSize);
		IPage<BizBalckPhone> pageList = bizBalckPhoneService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bizBalckPhone
	 * @return
	 */
	@AutoLog(value = "黑名单表-添加")
	@ApiOperation(value="黑名单表-添加", notes="黑名单表-添加")
	@RequiresPermissions("wxf:biz_balck_phone:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BizBalckPhone bizBalckPhone) {
		bizBalckPhoneService.save(bizBalckPhone);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bizBalckPhone
	 * @return
	 */
	@AutoLog(value = "黑名单表-编辑")
	@ApiOperation(value="黑名单表-编辑", notes="黑名单表-编辑")
	@RequiresPermissions("wxf:biz_balck_phone:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BizBalckPhone bizBalckPhone) {
		bizBalckPhoneService.updateById(bizBalckPhone);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "黑名单表-通过id删除")
	@ApiOperation(value="黑名单表-通过id删除", notes="黑名单表-通过id删除")
	@RequiresPermissions("wxf:biz_balck_phone:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bizBalckPhoneService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "黑名单表-批量删除")
	@ApiOperation(value="黑名单表-批量删除", notes="黑名单表-批量删除")
	@RequiresPermissions("wxf:biz_balck_phone:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bizBalckPhoneService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "黑名单表-通过id查询")
	@ApiOperation(value="黑名单表-通过id查询", notes="黑名单表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BizBalckPhone> queryById(@RequestParam(name="id",required=true) String id) {
		BizBalckPhone bizBalckPhone = bizBalckPhoneService.getById(id);
		if(bizBalckPhone==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bizBalckPhone);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bizBalckPhone
    */
    @RequiresPermissions("wxf:biz_balck_phone:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizBalckPhone bizBalckPhone) {
        return super.exportXls(request, bizBalckPhone, BizBalckPhone.class, "黑名单表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wxf:biz_balck_phone:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BizBalckPhone.class);
    }

}
