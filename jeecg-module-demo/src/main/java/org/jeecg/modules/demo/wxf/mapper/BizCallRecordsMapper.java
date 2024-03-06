package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.demo.wxf.entity.BizCallRecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 外呼记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface BizCallRecordsMapper extends BaseMapper<BizCallRecords> {

//    @Update("update sys_dict set del_flag = #{flag,jdbcType=INTEGER} where id = #{id,jdbcType=VARCHAR}")
//    public void updateDictDelFlag(@Param("flag") int delFlag, @Param("id") String id);

    /**
     * 更新号码资源包的性别
     * @param batchNo
     */
    @Update("update  biz_phone p , biz_call_records cr set p.gender=cr.gender,p.client_name=if(p.client_name is null or p.client_name='',cr.client_name,p.client_name)" +
            "          where p.phone=cr.phone and cr.batch_no=#{batchNo,jdbcType=VARCHAR} and cr.gender is not null")
    void updateGender(@Param("batchNo")String batchNo);

    /**
     * 更新客户状态失败的记录。 先更新一次失败客户，然后再更新一次成功客户。解决那种2条通话记录，有一条失败一条成功的情况
     * @param batchNo
     */
    @Update("update  biz_phone p , biz_call_records cr set p.client_status=cr.client_status " +
            "          where p.phone=cr.phone and cr.batch_no=#{batchNo,jdbcType=VARCHAR} and cr.client_status ='sb'  and p.client_status<>'cg' ")
    void updateSbStatus(@Param("batchNo")String batchNo);


    /**
     * 更新客户状态失败的记录。 先更新一次失败客户，然后再更新一次成功客户。解决那种2条通话记录，有一条失败一条成功的情况
     * @param batchNo
     */
    @Update("update  biz_phone p , biz_call_records cr set p.client_status=cr.client_status " +
            "          where p.phone=cr.phone and cr.batch_no=#{batchNo,jdbcType=VARCHAR} and cr.client_status ='cg'")
    void updateCgStatus(@Param("batchNo")String batchNo);


}
