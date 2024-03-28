package org.jeecg.modules.demo.wxf.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 导入任务表
 * @Author: jeecg-boot
 * @Date:   2024-03-06
 * @Version: V1.0
 */
@Data
@TableName("biz_import_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_import_task对象", description="导入任务表")
public class BizImportTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**createBy*/
    @ApiModelProperty(value = "createBy")
    private java.lang.String createBy;
	/**createTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
    private java.util.Date createTime;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
    private java.lang.String filePath;
	/**任务状态*/
	@Excel(name = "任务状态", width = 15, dicCode = "task_status")
	@Dict(dicCode = "task_status")
    @ApiModelProperty(value = "任务状态")
    private java.lang.String taskStatus;
	/**汇总信息*/
	@Excel(name = "汇总信息", width = 15)
    @ApiModelProperty(value = "汇总信息")
    private java.lang.String taskSummary;
	/**任务类型*/
	@Excel(name = "任务类型", width = 15, dicCode = "task_type")
	@Dict(dicCode = "task_type")
    @ApiModelProperty(value = "任务类型")
    private java.lang.String taskType;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @ApiModelProperty(value = "批次号")
    private java.lang.String batchNo;


    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String msg;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "startTime")
    private java.util.Date startTime;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "endTime")
    private java.util.Date endTime;


    public BizImportTask() {
    }

    public BizImportTask(String filePath, String taskStatus, String taskType, String batchNo) {
        this.filePath = filePath;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.batchNo = batchNo;
    }

    public BizImportTask start(){
        this.startTime=new Date();
        return this;
    }

    public BizImportTask end(){
        this.endTime=new Date();
        return this;
    }

}
