package com.nonobank.scheduler.util;

import java.io.InputStream;
import java.util.Properties;

//import org.apache.log4j.Logger;

/**
 * �򵥵�properties�ļ�����
 *
 * @author geyingchao
 */
public class ConfigUtil {

    //private static Logger logger = Logger.getLogger(ConfigUtil.class);

    /**
     * ����String����
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("config/zookeeper.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            in.close();
            String value = (String) properties.get(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ����int����
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        String value = getString(key);
        if (value != null && value != "") {
            return Integer.valueOf(value);
        }
        return 0;
    }

    /**
     * ����boolean����
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        String value = getString(key);
        if (value != null && value.equals("true")) {
            return true;
        }
        return false;
    }
}
