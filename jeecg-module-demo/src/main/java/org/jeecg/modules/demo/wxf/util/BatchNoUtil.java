package org.jeecg.modules.demo.wxf.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/3 21:13
 * @description:
 */
public class BatchNoUtil {
    public static String generate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-SSSS");
        final String format = sdf.format(new Date());
        return format;//+ UUID.randomUUID().toString();
    }
}
