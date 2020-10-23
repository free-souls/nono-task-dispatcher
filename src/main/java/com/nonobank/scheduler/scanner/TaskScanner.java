package com.nonobank.scheduler.scanner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

import com.nonobank.scheduler.entity.SchedulerTask;
import com.nonobank.scheduler.service.SchedulerService;
import com.nonobank.scheduler.util.ConfigUtil;
import com.nonobank.scheduler.util.CronParserUtil;

/**
 * @author panda
 */
public class TaskScanner implements Runnable {

    private static Logger logger = Logger.getLogger(TaskScanner.class);
    public static SchedulerService service = SchedulerService.newInstance();

    @Override
    public void run() {
        List<SchedulerTask> taskList = SchedulerService.schedulerTaskList;
        ZkClient zkClient = SchedulerService.zkClient;
        Date time = new Date();
        if (taskList != null && taskList.size() > 0) {
            for (int i = 0; i < taskList.size(); i++) {
                //ɨ��task�б�����cron���ʽ���ж������Ƿ�Ӧ��ִ��
                SchedulerTask task = taskList.get(i);
                //�ж��Ƿ���Ҫ���½ڵ�����
                int status = CronParserUtil.isExecutable(task, time);
                if (status > 0) {
                    //��������
                    if (status == 1) {
                        CronParserUtil.updateTask(task);
                    }
                    //�жϵ������ʱ���Ƿ���Ե���
                    else if (status == 2) {
                        int adapt = CronParserUtil.adaptTask(task, time);
                        if (adapt == 1) {
                            status = 1;
                        }
                    }
                    if (status == 1) {
                        //ִ�е��ȣ�����״̬
                        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String next = s.format(task.getCurrentFiredTime());
                        logger.error(next + "-- fire time" + task.getPath());
                        System.out.println(next + "-- fire time" + task.getPath());
                    }
                    zkClient.writeData(task.getGroup() + "/" + task.getPath(), task);
                }
            }
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExec = Executors.newScheduledThreadPool(1);
        scheduledExec.scheduleAtFixedRate(new TaskScanner(), 0, ConfigUtil.getInt("scan.period"), TimeUnit.MILLISECONDS);
    }
}
