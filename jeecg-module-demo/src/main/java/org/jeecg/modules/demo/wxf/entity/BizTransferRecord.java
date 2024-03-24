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
 * @Date:   2024-03-04
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
	/**收件人手机*/
	@Excel(name = "收件人手机", width = 15)
    @ApiModelProperty(value = "收件人手机")
    private java.lang.String phone;
	/**运单号*/
	@Excel(name = "运单号", width = 15)
    @ApiModelProperty(value = "运单号")
    private java.lang.String transferNum;
	/**生成运单时间*/
	@Excel(name = "生成运单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生成运单时间")
    private java.util.Date transferCreateTime;
	/**签收时间*/
	@Excel(name = "签收时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "签收时间")
    private java.util.Date signTime;
	/**运单状态*/
	@Excel(name = "运单状态", width = 15)
    @ApiModelProperty(value = "运单状态")
    private java.lang.String transferStatus;
	/**实收金额*/
	@Excel(name = "实收金额", width = 15)
    @ApiModelProperty(value = "实收金额")
    private java.lang.String price;
	/**物流公司*/
	@Excel(name = "物流公司", width = 15,dicCode = "wuliu")
    @Dict(dicCode = "wuliu")
    @ApiModelProperty(value = "物流公司")
    private java.lang.String transferComp;
	/**收件人地址*/
	@Excel(name = "收件人地址", width = 15)
    @ApiModelProperty(value = "收件人地址")
    private java.lang.String clientAddress;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @ApiModelProperty(value = "批次号")
    private java.lang.String batchNo;

    /**收件人姓名*/
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String clientName;
}
