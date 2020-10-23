package com.nonobank.scheduler.entity;

import java.io.Serializable;
import java.util.Date;

public class SchedulerTask implements Serializable {

    private static final long serialVersionUID = 1L;

    public SchedulerTask() {
    }

    public SchedulerTask(int id, String name, String group, String path,
                         String url, String cron, int status, Date currentFiredTime,
                         Date nextFiredTime, String describe, Date createTime,
                         Date updateTime) {
        super();
        this.id = id;
        this.name = name;
        this.group = group;
        this.path = path;
        this.url = url;
        this.cron = cron;
        this.status = status;
        this.currentFiredTime = currentFiredTime;
        this.nextFiredTime = nextFiredTime;
        this.describe = describe;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }


    /**
     * ����id
     */
    private int id;

    /**
     * ��������
     */
    private String name;

    /**
     * �����������
     */
    private String group;

    /**
     * ����ڵ�����
     */
    private String path;

    /**
     * ������ȵ�ַ
     */
    private String url;

    /**
     * �������corn���ʽ
     */
    private String cron;

    /**
     * 0:��ʾ�����Ѿ�׼������
     * 1����ʾ��������ִ��
     * 2����ʾ�����Ѿ�ִ�н���
     * 3����������쳣
     */
    private int status = 0;

    /**
     * ���ε���ִ��ʱ��
     */
    private Date currentFiredTime;

    /**
     * �ϴ�ִ��ʱ��
     */
    private Date nextFiredTime;

    /**
     * ��������
     */
    private String describe;

    /**
     * ���񴴽�ʱ��
     */
    private Date createTime;

    /**
     * �������ʱ��
     */
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCurrentFiredTime() {
        return currentFiredTime;
    }

    public void setCurrentFiredTime(Date currentFiredTime) {
        this.currentFiredTime = currentFiredTime;
    }

    public Date getNextFiredTime() {
        return nextFiredTime;
    }

    public void setNextFiredTime(Date nextFiredTime) {
        this.nextFiredTime = nextFiredTime;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}

	

