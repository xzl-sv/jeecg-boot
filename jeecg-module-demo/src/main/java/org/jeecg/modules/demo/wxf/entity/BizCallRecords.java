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
 * @Description: 外呼记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
@Data
@TableName("biz_call_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_call_records对象", description="外呼记录")
public class BizCallRecords implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;



    /**开始时间*/
    @Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date callTime;

    /**通话时长*/
    @Excel(name = "通话时长", width = 15)
    @ApiModelProperty(value = "通话时长")
    private java.lang.Integer callDuration;

    /**性别*/
    @Excel(name = "性别", width = 15, dicCode = "sex")
    @Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String gender;
    /**坐席工号*/
    @Excel(name = "坐席工号", width = 15)
    @ApiModelProperty(value = "坐席工号")
    private java.lang.String seatsNum;
    /**坐席姓名*/
    @Excel(name = "坐席姓名", width = 15)
    @ApiModelProperty(value = "坐席姓名")
    private java.lang.String seatsName;
    /**客户姓名*/
    @Excel(name = "客户姓名", width = 15)
    @ApiModelProperty(value = "客户姓名")
    private java.lang.String clientName;
    /**客户地址*/
    @Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String clientAddress;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String price;
    /**批次号*/
    @Excel(name = "批次号", width = 15)
    @ApiModelProperty(value = "批次号")
    private java.lang.String batchNo;
    /**客户状态*/
    @Excel(name = "客户状态", width = 15, dicCode = "client_status")
    @Dict(dicCode = "client_status")
    @ApiModelProperty(value = "客户状态")
    private java.lang.String clientStatus;
    /**客户号码*/
    @Excel(name = "客户号码", width = 15)
    @ApiModelProperty(value = "客户号码")
    private java.lang.String phone;






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

	/**录音地址*/
	@Excel(name = "录音地址", width = 15)
    @ApiModelProperty(value = "录音地址")
    private java.lang.String recordingAddress;
	/**是否拉黑*/
	@Excel(name = "是否拉黑", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否拉黑")
    private java.lang.String blackFlag;

	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String jobName;

}