package org.jeecg.modules.demo.wxf.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 导入中间表
 * @Author: jeecg-boot
 * @Date:   2024-03-03
 * @Version: V1.0
 */
@Data
@TableName("biz_mid_import")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_mid_import对象", description="导入中间表")
public class BizMidImport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @ApiModelProperty(value = "客户名称")
    private java.lang.String clientName;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
    /**省*/
    @Excel(name = "省", width = 15,dicCode = "province")
    @ApiModelProperty(value = "省")
    private java.lang.String provinceCode;
    /**市*/
    @Excel(name = "市", width = 15,dicCode = "4")
    @ApiModelProperty(value = "市")
    private java.lang.String cityCode;
	/**batchNo*/
	@Excel(name = "batchNo", width = 15)
    @ApiModelProperty(value = "batchNo")
    private java.lang.String batchNo;
	/**gender*/
    @Excel(name = "性别", width = 15, dicCode = "sex")
    @ApiModelProperty(value = "gender")
    private java.lang.String gender;
	/**black*/
    @Excel(name = "是否黑名单", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "black")
    private java.lang.String black;
	/**clientStatus*/
    @Excel(name = "客户状态", width = 15, dicCode = "client_status")
    @ApiModelProperty(value = "clientStatus")
    private java.lang.String clientStatus;
	/**lastExportTime*/
	@Excel(name = "lastExportTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "lastExportTime")
    private java.util.Date lastExportTime;
	/**客户号码*/
	@Excel(name = "客户号码", width = 15)
    @ApiModelProperty(value = "客户号码")
    private java.lang.String phone;

    @Excel(name = "被叫号码", width = 15)
    @TableField(exist = false)
    private java.lang.String phoneBj;

    @Excel(name = "呼入被叫", width = 15)
    @TableField(exist = false)
    private java.lang.String phoneHrbj;


    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String bz;

    public void autoFillPhone(){
        if(phoneBj!=null && phoneBj.length()>0){
            phone=phoneBj;
        }else if(phoneHrbj!=null && phoneHrbj.length()>0){
            phone=phoneHrbj;
        }
    }
}
