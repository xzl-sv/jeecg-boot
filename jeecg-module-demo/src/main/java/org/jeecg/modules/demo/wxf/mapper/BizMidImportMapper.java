package org.jeecg.modules.demo.wxf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 导入中间表
 * @Author: jeecg-boot
 * @Date:   2024-03-03
 * @Version: V1.0
 */
public interface BizMidImportMapper extends BaseMapper<BizMidImport> {

    /**
     * 查询excel中数据有多少在表里已经存在
     * @return
     */
    @Select("select  count(distinct p.phone) from biz_phone p,biz_mid_import m where p.phone=m.phone ")
    public Integer phoneExistInDb();

    /**
     * 有价值的号码数量。原来表里面不存在.
     * valid的数量
     * @return
     */
    @Select("select  count(distinct m.phone) from biz_mid_import m left join biz_phone p on p.phone=m.phone " +
            "where p.phone is null ")
    public Integer phoneValueNum();

    @Insert("insert into biz_phone (id,province_code,batch_no,phone,create_time,create_by) " +
            "select  min(m.id),min(m.province_code),min(m.batch_no),  m.phone,now(),'admin' from biz_mid_import m left join biz_phone p on p.phone=m.phone\n" +
            "where p.phone is null group by  m.phone limit 10000")
    void insertPhoneFromMidImport();

    @Select("select count(*) from (select m.phone from biz_mid_import m left join biz_phone p on p.phone=m.phone " +
            "where p.phone is null group by  m.phone ) t")
    Integer checkIfShouldInsert();



    @Insert(" insert into biz_balck_phone (id,phone,create_time,create_by,import_time) " +
            "            select  min(m.id), m.phone,now(),'admin',now()  from biz_mid_import m left join biz_balck_phone p on p.phone=m.phone " +
            "            where p.phone is null group by  m.phone ")
    void insertBlackPhoneFromMidImport();

    @Delete("TRUNCATE table biz_mid_import")
    void truncateTable();
}
