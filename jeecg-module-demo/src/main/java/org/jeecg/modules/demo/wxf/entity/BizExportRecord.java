package org.jeecg.modules.demo.wxf.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 提取记录表
 * @Author: jeecg-boot
 * @Date:   2024-02-28
 * @Version: V1.0
 */
@Data
@TableName("biz_export_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="biz_export_record对象", description="提取记录表")
public class BizExportRecord implements Serializable {
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
	/**提取时间*/
	@Excel(name = "提取时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提取时间")
    private java.util.Date exportTime;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer size;
	/**文件地址*/
	@Excel(name = "文件地址", width = 15)
    @ApiModelProperty(value = "文件地址")
    private java.lang.String fileAddress;
	/**提取规则描述*/
	@Excel(name = "提取规则描述", width = 15)
    @ApiModelProperty(value = "提取规则描述")
    private java.lang.String exportRule;
	@Excel(name = "批次号", width = 80)
    @ApiModelProperty(value = "批次号")
    private java.lang.String batchNo;

    public void parseParam(String paramStr){
        try {
            this.parseParamInner(paramStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseParamInner(String paramStr){
        StringBuffer stringBuffer=new StringBuffer("");
        final Map<String, String[]> param = JSON.parseObject(paramStr, new TypeReference<Map<String, String[]>>() {
        });

        if(param.get("tqsx")!=null && StringUtils.equalsIgnoreCase("rksj",((String[])param.get("tqsx"))[0])){
            stringBuffer.append("提取顺序 = 入库时间").append(" , ");
        }else  if(param.get("tqsx")!=null && StringUtils.equalsIgnoreCase("sj",((String[])param.get("tqsx"))[0])){
            stringBuffer.append("提取顺序 = 随机").append(" , ");
        }

        //男1 女2  非女99
        if(param.get("gender")!=null && StringUtils.isNotBlank(((String[]) param.get("gender"))[0])) {
            final String gender = ((String[]) param.get("gender"))[0];
            if(gender.equalsIgnoreCase("99")){
                stringBuffer.append("性别 = 非女 , ");
                //非女
            }else{
                //男或者女
                stringBuffer.append("性别 =").append(gender).append(" , ");
            }
        }

        //        batchNo 提取指定批次（输入批次号）
        if(param.get("batchNo")!=null && StringUtils.isNotBlank(((String[]) param.get("batchNo"))[0]))
            stringBuffer.append("批次 =").append(((String[]) param.get("batchNo"))[0]).append(" , ");



//        jtcs 接通次数不大于（N）次（填写框输入）：外呼记录里次数不大于N次的可以提
        if(param.get("jtcs")!=null && StringUtils.isNotBlank(((String[]) param.get("jtcs"))[0])) {
            stringBuffer.append("次数小于 =").append(((String[]) param.get("jtcs"))[0]).append(" , ");
        }
//        wjt 近（N）天无接通（填写框输入）：库里记录号码最近的外呼时间，近N天内无接通的数据可以提
        if(param.get("wjt")!=null && StringUtils.isNotBlank(((String[]) param.get("wjt"))[0])) {
            stringBuffer.append("近 ").append(((String[]) param.get("wjt"))[0]).append(" 天无接通 , ");
        }
//        tqsj 近（N）天数据不取（填写框输入）：库里记录号码最近的提取时间，近N天已经提过的数据不取
        if(param.get("tqsj")!=null && StringUtils.isNotBlank(((String[]) param.get("tqsj"))[0])) {
            stringBuffer.append("近 ").append(((String[]) param.get("tqsj"))[0]).append(" 天不取 , ");


        }

        //提取时间范围createTime
        if(param.get("createTime[]")!=null  && StringUtils.isNotBlank(((String[]) param.get("createTime[]"))[0])  && ((String[]) param.get("createTime[]")).length>1 ) {
            final String createTimeBegin = ((String[]) param.get("createTime[]"))[0];
            final String createTimeEnd = ((String[]) param.get("createTime[]"))[1];
            stringBuffer.append("创建时间 = ").append(createTimeBegin).append(" ~ ").append(createTimeEnd).append(" , ");

        }

        if(param.get("excludeCity")!=null && StringUtils.isNotBlank(((String[]) param.get("excludeCity"))[0])) {
            stringBuffer.append("排除城市 =").append(Arrays.toString((String[]) param.get("excludeCity"))).append(" , ");

        }
//        excludeProvince 排除省份：（选择不需要的省份）
        if(param.get("excludeProvince")!=null && StringUtils.isNotBlank(((String[]) param.get("excludeProvince"))[0])) {
            stringBuffer.append("排除省份 =").append(Arrays.toString((String[]) param.get("excludeProvince"))).append(" , ");
        }

        this.setExportRule(stringBuffer.toString());
    }
}
