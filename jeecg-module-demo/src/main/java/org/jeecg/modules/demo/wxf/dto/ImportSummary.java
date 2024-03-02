package org.jeecg.modules.demo.wxf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/2/29 22:54
 * @description:
 */
@Data
@NoArgsConstructor
@ToString
public class ImportSummary {

    /**
     * 全部数据
     */
    private Integer total=0;
    /**
     * 重复数据(和数据库里已有的)
     */
    private Integer dup=0;

    /**
     *  有效数据
     */
    private Integer valid=0;

    /**
     * 非法数据（不包含库里已有的重复号码）
     */
    private Integer invalidNotDup=0;

    public ImportSummary valid(int i){
        valid+=i;
        return this;
    }

    public ImportSummary invalid(int i){
        invalidNotDup+=i;
        return this;
    }

    public ImportSummary dup(int i){
        dup+=i;
        return  this;
    }

    public ImportSummary(Integer total) {
        this.total = total;
    }
}
