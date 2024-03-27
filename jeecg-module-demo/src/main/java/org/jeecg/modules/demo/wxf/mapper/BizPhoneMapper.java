package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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

    @Update("update biz_phone  set del_flag='1' where batch_no =  #{batchNo,jdbcType=VARCHAR}")
    void delBatch(@Param("batchNo")String batchNo);


}
