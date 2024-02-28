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
 * @Date:   2024-02-28
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
	/**开始时间*/
	@Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date callTime;
	/**通话时长-秒*/
	@Excel(name = "通话时长-秒", width = 15)
    @ApiModelProperty(value = "通话时长-秒")
    private Integer callDuration;
	/**录音地址*/
	@Excel(name = "录音地址", width = 15)
    @ApiModelProperty(value = "录音地址")
    private String recordingAddress;
	/**是否拉黑y-n*/
	@Excel(name = "是否拉黑y-n", width = 15)
    @ApiModelProperty(value = "是否拉黑y-n")
    private String blackFlag;
	/**性别*/
	@Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private String gender;
	/**坐席工号*/
	@Excel(name = "坐席工号", width = 15)
    @ApiModelProperty(value = "坐席工号")
    private String seatsNum;
	/**坐席姓名*/
	@Excel(name = "坐席姓名", width = 15)
    @ApiModelProperty(value = "坐席姓名")
    private String seatsName;
	/**客户姓名*/
	@Excel(name = "客户姓名", width = 15)
    @ApiModelProperty(value = "客户姓名")
    private String clientName;
	/**客户地址*/
	@Excel(name = "客户地址", width = 15)
    @ApiModelProperty(value = "客户地址")
    private String clientAddress;
	/**金额、价格*/
	@Excel(name = "金额、价格", width = 15)
    @ApiModelProperty(value = "金额、价格")
    private Double price;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String jobName;
}
