package org.jeecg.common.system.base;

import java.util.List;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/3 18:55
 * @description:
 */
public interface ImportExcelFilter<T> {
    /**
     * 默认返回excel解析之后的全部数据。
     * 注意：直接移除了list中的元素。如果需要记录原始数量，需要先获取数据
     * @param list
     * @return
     */
    default void doFilter(List<T> list){

    }
}
