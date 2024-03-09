package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.jeecg.modules.demo.wxf.entity.BizTransferRecordTmp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: biz_transfer_record_tmp
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface BizTransferRecordTmpMapper extends BaseMapper<BizTransferRecordTmp> {


    /**
     * 插入新增的运单
     */
    @Insert("insert into biz_transfer_record " +
            "select  rt.* from biz_transfer_record_tmp   rt  left join biz_transfer_record r " +
            "         on rt.transfer_num=r.transfer_num " +
            "                 where r.id is null")
    void insertTransferRecordFromTmp();



    /**
     * TODO 使用该方法会出线连接超时的异常，待解决
     *  更新运单状态
     */
    @Update("update biz_transfer_record r,biz_transfer_record_tmp rt set r.transfer_status=rt.transfer_status " +
            " where r.transfer_num=rt.transfer_num  ")
    void updateTranseferStatusFromTmp();


    /**
     * 更新号码资源表中的地址
     */
    @Update("update biz_phone p ,biz_transfer_record_tmp rt set p.address=rt.client_address ,p.price=rt.price " +
            "where p.phone=rt.phone   ")
    void updatePhoneTransferStatusFromTmp();

    /**
     * 移除中间表
     */
    @Delete("TRUNCATE table biz_transfer_record_tmp")
    void truncateTable();
}
