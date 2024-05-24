package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.demo.wxf.dto.WxfDict;
import org.jeecg.modules.demo.wxf.entity.BizTransferRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 运单记录
 * @Author: jeecg-boot
 * @Date:   2024-03-04
 * @Version: V1.0
 */
public interface BizTransferRecordMapper extends BaseMapper<BizTransferRecord> {

    @Select("select r.id,rt.transfer_status,rt.price,rt.sign_time,rt.transfer_comp from  biz_transfer_record r,biz_transfer_record_tmp rt " +
            "            where r.transfer_num=rt.transfer_num  and ( r.transfer_status<>rt.transfer_status or r.price<>rt.price or r.sign_time<>rt.sign_time  or r.transfer_comp<>rt.transfer_comp)")
    public List<BizTransferRecord> findTransferStatusToBeUpdate();

    @Update("update biz_transfer_record t,biz_transfer_record_tmp tmp " +
            "set t.price=tmp.price,t.transfer_comp=tmp.transfer_comp" +
            ",t.transfer_status=tmp.transfer_status,t.bz=tmp.bz" +
            ",t.sender_name=tmp.sender_name,t.ysdsje=tmp.ysdsje,t.gjje=tmp.gjje,t.mjly=tmp.mjly,t.zxlyxz=tmp.zxlyxz" +
            ",t.sign_time=tmp.sign_time  " +
            "where  t.transfer_num=tmp.transfer_num")
    void updateTransferFromTransferTmp();


    @Select("    select  distinct  transfer_status id,transfer_status name from biz_transfer_record where transfer_status is not null")
    public List<WxfDict> transferStatus();


    @Select("    select  distinct  transfer_comp id,transfer_comp name from biz_transfer_record where transfer_comp is not null")
    public List<WxfDict> transferComp();

    @Select("   select  distinct  price id,price name from biz_transfer_record where price is not null")
    public List<WxfDict> price();



}
