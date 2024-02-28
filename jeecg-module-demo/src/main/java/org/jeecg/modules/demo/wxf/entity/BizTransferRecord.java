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
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Data
@TableName("biz_transfer_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_transfer_record对象", description="运单记录")
public class BizTransferRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**客户号码*/
	@Excel(name = "客户号码", width = 15)
    @ApiModelProperty(value = "客户号码")
    private String phoneNum;
	/**运单号*/
	@Excel(name = "运单号", width = 15)
    @ApiModelProperty(value = "运单号")
    private String transferNum;
	/**生成运单时间*/
	@Excel(name = "生成运单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生成运单时间")
    private Date transferCreateTime;
	/**签收时间*/
	@Excel(name = "签收时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "签收时间")
    private Date signTime;
	/**运单状态*/
	@Excel(name = "运单状态", width = 15)
    @ApiModelProperty(value = "运单状态")
    private String transferStatus;
	/**实收金额*/
	@Excel(name = "实收金额", width = 15)
    @ApiModelProperty(value = "实收金额")
    private Double price;
	/**快递公司-字典项*/
	@Excel(name = "快递公司-字典项", width = 15)
    @ApiModelProperty(value = "快递公司-字典项")
    private String transferComp;
}
