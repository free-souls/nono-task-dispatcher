package com.nonobank.scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.nonobank.scheduler.entity.SchedulerTask;


public class CronParserUtil {

    /**
     * �ж������Ƿ���Ա����Ⱥ��Ƿ���Ҫ����
     *
     * @param task
     * @param time
     * @return 0����ʾ��������δ����1����ʾ���Ա����ȣ�2����ʾ���Ա�������
     */
    public static int isExecutable(SchedulerTask task, Date time) {
        if (task != null) {
            if (task.getCurrentFiredTime() == null) {
                task.setCurrentFiredTime(time);
            }
            long current = task.getCurrentFiredTime().getTime();
            long cycle = getCycle(task);
            long now = time.getTime();
            //ʱ���
            long interval = current - now;
            //ʱ�����(-period<=interval<=period) �ɹ�����
            if ((Math.abs(interval) <= ConfigUtil.getInt("scan.period"))) {
                return 1;
            } //����һ��ʱ��������Ҫ����ִ��ʱ��
            else if (interval <= (-cycle)) {
                return 2;
            }
        }
        return 0;
    }

    /**
     * ��ǰ����ʱ������ϴε���ʱ�����
     * �´ε���ʱ����ݵ�ǰ����ʱ�����
     */
    public static void updateTask(SchedulerTask task) {
        String cron = task.getCron();
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron quartzCron = parser.parse(cron);
        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        DateTime updateCurrentFireTime = executionTime.nextExecution(new DateTime(task.getCurrentFiredTime()));
        //���µ�ǰ����ʱ��
        task.setCurrentFiredTime(updateCurrentFireTime.toDate());
        DateTime nextFireTime = executionTime.nextExecution(updateCurrentFireTime);
        //�����´ε���ʱ��
        task.setNextFiredTime(nextFireTime.toDate());
    }

    /***
     * ���������ʱ�����Ϊ��time��ʼ�ģ��������ʱ��
     * @param task  �����ȵ�����
     * @param time  ��������ʼʱ��
     * @return
     */
    public static int adaptTask(SchedulerTask task, Date time) {
        String cron = task.getCron();
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron quartzCron = parser.parse(cron);
        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        DateTime updateCurrentFireTime = new DateTime(time);
        updateCurrentFireTime = executionTime.nextExecution(new DateTime());
        //���µ�ǰ����ʱ��
        task.setCurrentFiredTime(updateCurrentFireTime.toDate());
        DateTime nextFireTime = executionTime.nextExecution(updateCurrentFireTime);
        //�����´ε���ʱ��
        task.setNextFiredTime(nextFireTime.toDate());
        int result = isExecutable(task, time);
        return result;
    }

    /**
     * ��ȡ��������ĵ�������
     */
    public static long getCycle(SchedulerTask task) {
        long cycle = 0L;
        DateTime start;
        DateTime end;
        if (task != null && task.getCron() != null) {
            String cron = task.getCron();
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
            CronParser parser = new CronParser(cronDefinition);
            Cron quartzCron = parser.parse(cron);
            ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
            start = executionTime.nextExecution(new DateTime());
            end = executionTime.nextExecution(start);
            cycle = end.toDate().getTime() - start.toDate().getTime();
        }
        return cycle;
    }


    public static void main(String[] args) {

        String cron = "*/15 * * * * *";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 5);
        //����ʱ���ʽ
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String defaultStartDate = sdf.format(c.getTime());
        Date now = c.getTime();
        //	System.out.println(defaultStartDate);
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron quartzCron = parser.parse(cron);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);

        for (int i = 0; i < 100; i++) {
            now = executionTime.nextExecution(new DateTime(now)).toDate();
            //	System.out.println(sdf.format(now));
        }
    }

}
