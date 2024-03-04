package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.demo.wxf.entity.BizTransferRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface BizTransferRecordMapper extends BaseMapper<BizTransferRecord> {

    @Select("select r.id,rt.transfer_status from  biz_transfer_record r,biz_transfer_record_tmp rt " +
            "where r.transfer_num=rt.transfer_num  and  r.transfer_status<>rt.transfer_status")
    public List<BizTransferRecord> findTransferStatusToBeUpdate();


}
