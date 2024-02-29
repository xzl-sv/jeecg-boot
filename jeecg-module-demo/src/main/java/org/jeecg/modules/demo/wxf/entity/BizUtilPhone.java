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
 * @Description: 号码所属区域表
 * @Author: jeecg-boot
 * @Date:   2024-02-29
 * @Version: V1.0
 */
@Data
@TableName("biz_util_phone")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_util_phone对象", description="号码所属区域表")
public class BizUtilPhone implements Serializable {
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
	/**前缀*/
	@Excel(name = "前缀", width = 15)
    @ApiModelProperty(value = "前缀")
    private java.lang.String prefix;
	/**号段*/
	@Excel(name = "号段", width = 15)
    @ApiModelProperty(value = "号段")
    private java.lang.String phone;
	/**省*/
	@Excel(name = "省", width = 15)
    @ApiModelProperty(value = "省")
    private java.lang.String province;
	/**城市*/
	@Excel(name = "城市", width = 15)
    @ApiModelProperty(value = "城市")
    private java.lang.String city;
	/**运营商*/
	@Excel(name = "运营商", width = 15)
    @ApiModelProperty(value = "运营商")
    private java.lang.String isp;
	/**邮编*/
	@Excel(name = "邮编", width = 15)
    @ApiModelProperty(value = "邮编")
    private java.lang.String postCode;
	/**区号*/
	@Excel(name = "区号", width = 15)
    @ApiModelProperty(value = "区号")
    private java.lang.String cityCode;
	/**区域编码*/
	@Excel(name = "区域编码", width = 15)
    @ApiModelProperty(value = "区域编码")
    private java.lang.String areaCode;
}
