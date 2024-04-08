package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.demo.wxf.entity.BizPhone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 号码资源表
 * @Author: jeecg-boot
 * @Date:   2024-02-29
 * @Version: V1.0
 */
public interface BizPhoneMapper extends BaseMapper<BizPhone> {

//    @Update("update biz_phone  set del_flag='1' where batch_no =  #{batchNo,jdbcType=VARCHAR}")
//    void delBatch(@Param("batchNo")String batchNo);



    @Update("delete from biz_phone  where batch_no =  #{batchNo,jdbcType=VARCHAR}")
    void delBatch(@Param("batchNo")String batchNo);

    void updateExportTimeBatch(@Param("ids")List<String> ids);


    @Update("update biz_phone set batch_no= #{newBatch,jdbcType=VARCHAR} where batch_no=#{oldBatch,jdbcType=VARCHAR}")
    void updateBatch(String oldBatch,String newBatch);


    @Select("select zb_value from biz_summary where zb_type='统计值' and zb_name='号码总量' ")
    Integer cacheCount();

}
