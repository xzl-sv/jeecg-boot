package org.jeecg.modules.demo.wxf.service;

import org.jeecg.modules.demo.wxf.entity.BizMidImport;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 导入中间表
 * @Author: jeecg-boot
 * @Date:   2024-03-03
 * @Version: V1.0
 */
public interface IBizMidImportService extends IService<BizMidImport> {


    void truncateTable();

    /**
     * 查询excel中数据有多少在表里已经存在
     * @return
     */
    Integer phoneExistInDb();

    /**
     * 有价值的号码数量。原来表里面不存在.
     * 插入的数量应该也是这么多
     * valid的数量
     * @return
     */
    Integer phoneValueNum();

    void insertPhoneFromMidImport();

    @Transactional(rollbackFor = Exception.class)
    void insertBlackPhoneFromMidImport();
}
