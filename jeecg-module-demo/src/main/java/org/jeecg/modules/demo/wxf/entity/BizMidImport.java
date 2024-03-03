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
 * @Description: 导入中间表
 * @Author: jeecg-boot
 * @Date:   2024-03-02
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
	/**客户号码*/
	@Excel(name = "客户号码", width = 15)
    @ApiModelProperty(value = "客户号码")
    private java.lang.String clientName;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
	/**provinceCode*/
	@Excel(name = "provinceCode", width = 15)
    @ApiModelProperty(value = "provinceCode")
    private java.lang.String provinceCode;
	/**batchNo*/
	@Excel(name = "batchNo", width = 15)
    @ApiModelProperty(value = "batchNo")
    private java.lang.String batchNo;
	/**gender*/
	@Excel(name = "gender", width = 15)
    @ApiModelProperty(value = "gender")
    private java.lang.String gender;
	/**black*/
	@Excel(name = "black", width = 15)
    @ApiModelProperty(value = "black")
    private java.lang.String black;
	/**clientStatus*/
	@Excel(name = "clientStatus", width = 15)
    @ApiModelProperty(value = "clientStatus")
    private java.lang.String clientStatus;
	/**lastExportTime*/
	@Excel(name = "lastExportTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "lastExportTime")
    private java.util.Date lastExportTime;
	/**phone*/
	@Excel(name = "phone", width = 15)
    @ApiModelProperty(value = "phone")
    private java.lang.String phone;
}
