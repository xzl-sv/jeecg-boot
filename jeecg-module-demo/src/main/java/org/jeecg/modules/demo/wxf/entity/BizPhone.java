package org.jeecg.modules.demo.wxf.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-02-29
 * @Version: V1.0
 */
@Data
@TableName("biz_phone")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_phone对象", description="号码资源表")
public class BizPhone implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @ApiModelProperty(value = "客户名称")
    private java.lang.String clientName;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
	/**省编码*/
	@Excel(name = "省编码", width = 15)
    @ApiModelProperty(value = "省编码")
    private java.lang.String provinceCode;
	/**市编码*/
	@Excel(name = "市编码", width = 15)
    @ApiModelProperty(value = "市编码")
    private java.lang.String cityCode;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @ApiModelProperty(value = "批次号")
    private java.lang.String batchNo;
	/**性别*/
	@Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private java.lang.String gender;
	/**是否黑名单*/
	@Excel(name = "是否黑名单", width = 15)
    @ApiModelProperty(value = "是否黑名单")
    private java.lang.String black;
	/**客户状态*/
	@Excel(name = "客户状态", width = 15)
    @ApiModelProperty(value = "客户状态")
    private java.lang.String clientStatus;
	/**最近提取时间*/
	@Excel(name = "最近提取时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近提取时间")
    private java.util.Date lastExportTime;
	/**客户号码*/
	@Excel(name = "客户号码", width = 15)
    @ApiModelProperty(value = "客户号码")
    private java.lang.String phone;
}