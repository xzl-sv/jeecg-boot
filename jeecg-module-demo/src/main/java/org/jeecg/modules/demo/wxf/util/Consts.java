package org.jeecg.modules.demo.wxf.util;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/10 10:12
 * @description:
 */
public class Consts {
    /**
     * 批量插入的数量。解决一次性插入50w导致sql超时的问题（超过10秒）
     */
    public static Integer BATCH_INSERT_SIZE=20000;

    public static Integer DEFAULT_EXPORT_SIZE = 10000;
}
