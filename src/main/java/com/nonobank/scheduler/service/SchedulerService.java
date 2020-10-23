package com.nonobank.scheduler.service;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import com.google.common.hash.Hashing;
import com.nonobank.scheduler.entity.SchedulerTask;
import com.nonobank.scheduler.listener.DataListener;
import com.nonobank.scheduler.listener.SchedulerListener;
import com.nonobank.scheduler.listener.TaskListener;
import com.nonobank.scheduler.util.ConfigUtil;

/**
 * ���������Ҫ������
 *
 * @author geyingchao
 */
public class SchedulerService {

    private static SchedulerService zkService;

    public static ZkClient zkClient;

    public static List<SchedulerTask> schedulerTaskList = Collections.synchronizedList(new ArrayList<>());

    private static String HOST = ConfigUtil.getString("zookeeper.host");

    private static int CLIENT_SESSION_TIMEOUT = ConfigUtil.getInt("zookeeper.session.timeout");

    private static String ROOT_SCHEDULER = ConfigUtil.getString("zookeeper.root.scheduler");

    private static String ROOT_TASK = ConfigUtil.getString("zookeeper.root.taskpath");

    private static String path;

    private static Logger logger = Logger.getLogger(SchedulerService.class);

    private SchedulerService() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("connected to zookeeper error!");
        }
    }

    /**
     * ��ȡservice����
     *
     * @return
     */
    public static SchedulerService newInstance() {
        if (zkService == null) {
            return new SchedulerService();
        } else {
            return zkService;
        }
    }

    /**
     * ��ʼ��service��tasklist
     *
     * @throws Exception
     */
    private void init() throws Exception {
        zkClient = new ZkClient(HOST, CLIENT_SESSION_TIMEOUT);
        //�����־ýڵ�
        if (!zkClient.exists(ROOT_SCHEDULER)) {
            zkClient.createPersistent(ROOT_SCHEDULER);
        }
        if (!zkClient.exists(ROOT_TASK)) {
            zkClient.createPersistent(ROOT_TASK);
        } else {
            List<String> children = zkClient.getChildren(ROOT_TASK);
            for (int i = 0; i < children.size(); i++) {
                zkClient.subscribeDataChanges(ROOT_TASK + "/" + children.get(i), new DataListener(this));
            }
        }
        zkClient.subscribeChildChanges(ROOT_TASK, new TaskListener(this));
        //������ʱ�ڵ�
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
        }
        path = zkClient.createEphemeralSequential(ROOT_SCHEDULER + "/scheduler-", ip);
        zkClient.subscribeChildChanges(ROOT_SCHEDULER, new SchedulerListener(this));
        getSchedulerTaskList();
        logger.info(path + " scheduler start");

    }


    /**
     * ��ȡ�õ��ȷ����������б�ͨ��һ����hash�㷨ȷ���Ƿ���ȸ�����
     *
     * @return �����б�list
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<SchedulerTask> getSchedulerTaskList() throws KeeperException, InterruptedException {
        schedulerTaskList.clear();
        ArrayList<SchedulerTask> tasks = getTasksList();
        ArrayList<Long> schedulers = getSchedulersList();
        //������񵽽ڵ��б�
        for (int i = 0; i < tasks.size(); i++) {
            SchedulerTask task = tasks.get(i);
            long taskId = getSeq(task.getPath());
            int buckets = schedulers.size();
            int group = Hashing.consistentHash(taskId, buckets);
            long order = getOrder(getSeq(path), schedulers);
            if (group == order) {
                schedulerTaskList.add(task);
            }
        }
        return schedulerTaskList;
    }

    /**
     * ��������ڵ��Ż�ȡ�ڵ��
     *
     * @param path
     * @return
     */
    private long getSeq(String path) {
        return Long.parseLong(path.split("-")[1]);
    }

    /**
     * ��ȡscheduler�ڵ�����������ڵ����λ����С����
     *
     * @param value �ýڵ������нڵ��е����
     * @param total
     * @return
     */
    private long getOrder(long value, ArrayList<Long> total) {
        Collections.sort(total);
        int result = -1;
        if (total != null && total.size() > 0) {
            for (int i = 0; i < total.size(); i++) {
                if (total.get(i) == value) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * ��ȡ���ȷ������б�
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private ArrayList<Long> getSchedulersList() throws KeeperException, InterruptedException {
        ArrayList<Long> schedulers = new ArrayList<>();
        List<String> children = zkClient.getChildren(ROOT_SCHEDULER);
        if (children != null && children.size() > 0)
            for (int i = 0; i < children.size(); i++) {
                schedulers.add(getSeq(children.get(i)));
            }
        return schedulers;
    }


    /**
     * ��ȡ���������б�
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private ArrayList<SchedulerTask> getTasksList() throws KeeperException, InterruptedException {
        ArrayList<SchedulerTask> tasks = new ArrayList<>();
        List<String> children = zkClient.getChildren(ROOT_TASK);
        if (children != null && children.size() > 0)
            for (int i = 0; i < children.size(); i++) {
                //��ȡÿһ���ڵ��������Ϣ
                SchedulerTask task = zkClient.readData(ROOT_TASK + "/" + children.get(i));
                //���ڵ�·��Ҳ��ŵ�SchedulerTask�У����������ʱʹ��
                task.setPath(children.get(i));
                task.setGroup(ROOT_TASK);
                tasks.add(task);
            }
        return tasks;
    }

}
