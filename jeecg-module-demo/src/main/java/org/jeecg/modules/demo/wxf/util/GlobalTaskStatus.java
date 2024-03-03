package org.jeecg.modules.demo.wxf.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.wxf.entity.BizImportTask;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局任务状态管理器
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/3 23:19
 * @description:
 */
@Slf4j
public class GlobalTaskStatus {

    private static GlobalTaskStatus single = new GlobalTaskStatus();
    /**
     * 当前任务。taskId
     */
     private BizImportTask curRunningTask;
    /**
     * 任务开始时间
     */
    private Date startTime;

    public static GlobalTaskStatus getInstanse(){
        return single;
    }


    public synchronized static boolean isRunning(){
        final boolean notRunning = getInstanse().curRunningTask == null;
        return !notRunning;
    }

    /**
     * 执行任务。返回true表示执行成功，false标识被其他线程抢占了
     * @param task
     * @return
     */
    public synchronized static boolean run(BizImportTask task){
        if(getInstanse().curRunningTask==null){
            getInstanse().curRunningTask=task;
            getInstanse().startTime=new Date();
            return true;
        }else {
            return false;
        }
    }

    public synchronized  static void end(){
        getInstanse().curRunningTask=null;
        getInstanse().startTime=null;
    }

    /**
     * 描述当前任务
     * @return
     */
    public static String descCurTask(){
        if(isRunning()==false){
            return "没有执行中的任务";
        }
        final BizImportTask cur = getInstanse().curRunningTask;
        return "正在执行的任务 = batchNo:"+ cur.getBatchNo()+",taskType:"+ cur.getTaskType()+",beginTime:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getInstanse().startTime);
    }







}
