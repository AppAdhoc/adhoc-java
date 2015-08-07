package com.appadhoc.javasdk;


import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dongyuangui on 15/7/15.
 */
public class AdhocSdk {

    private String protocol = "http://";
    private String DHOC_GETFLAGS_HOST = "api.appadhoc.com";
    private String ADHOC_GETFLAGS_PATH = "/optimizer/api/getflags.php";
    private String ADHOC_TRACKING_HOST = "tracking.appadhoc.com:23462";
    private static AdhocSdk instance = null;
    private static final String JSON_ERROR_STR = "Failed to get experiment flags.";
    private static HashMap<String, FlagBean> map = new HashMap<String, FlagBean>();


    private static long GAPTIME = 30;
    private static long ONEDAY = 86400000;
    private AdhocSdk() {
    }

    public static AdhocSdk getInstance() {
        if (instance == null) {
            instance = new AdhocSdk();
        }
        return instance;
    }

    private String appkey;


    public void init(String appkey) {
        this.appkey = appkey;
        if (T.DEBUG)
            fastRemovalTest();
        else dailyRemovalSchedule();
    }

    private void sendRequest(String url, String client_id, String type, OnAdHocReceivedData listener, String statkey, Object value) {

        String values = "{" + "\"" + KeyFields.EVENT_TYPE + "\"" + ":" + "\"" + type + "\"" +
                "," + "\"" + KeyFields.TIMESTAMP + "\"" + ":" + "\"" + System.currentTimeMillis() / 1000 + "\"" +
                "," + "\"" + "client_id" + "\"" + ":" + "\"" + client_id + "\"" +
                "," + "\"" + "summary" + "\"" + ":{}" +
                "," + "\"" + "adhoc_app_track_id" + "\"" + ":" + "\"" + appkey;
        if (statkey != null && value != null) {
            if (value instanceof String) {

                values = values + "," + "\"" + "stat_key:" + "\"" + statkey +
                        "," + "\"" + "stat_value" + "\"" + ":" + "\"" + value + "\"" + "}";
            } else {
                values = values + "," + "\"" + "stat_key:" + "\"" + statkey +
                        "," + "\"" + "stat_value" + "\"" + ":" + value + "}";
            }
        } else {
            values += "\"" + "}";
        }
        ClientImpl impl = new ClientImpl();
        impl.send(url, values, listener);

    }


    /**
     * 获取模块开关
     * callBack
     * 返回 json String
     **/
    public ExperimentFlags getExperimentFlags(String client_id) {

        if (appkey == null) {

            T.e("appkey is null 请初始化 Adhocsdk");

            return null;
        }

        // 取内存数据
        FlagBean bean = getMemoryFlags(client_id);

        ExperimentFlags flag = null;

        if (bean == null) {

            flag = getNullExperFlag();
            T.i("bean is null");

        } else {
            T.i("取内存结果：" + bean.flag);
            String flagStr = bean.flag;
//            todo
            if (flagStr != null && !flagStr.equals("") && flagStr.indexOf("error") == -1) {
                try {
                    flag = new ExperimentFlags(new JSONObject(flagStr));

                    flag.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());

                    T.i("取内存flags 结果：" + flagStr);

                } catch (JSONException e) {
                    T.e("error ! flag value : " + flagStr + " when parse jsonobj");
                }
            } else {

                flag = getNullExperFlag();

            }
        }

        boolean isRequestFast =  bean == null ? false:((System.currentTimeMillis() - bean.timeLast) < GAPTIME * 1000);

        T.i("isRequestFast :" + isRequestFast);
        // 是测试手机

        if (isRequestFast) {
            T.i("未从网络获取flag，距离上次取flag不足" + GAPTIME + "秒" + "duration is : "

                    + (System.currentTimeMillis() - bean.timeLast));
        } else {
            // 获取flag from network
            getExperimentFlagsFromNetWork(client_id);
        }
        return flag;
    }

    private void getExperimentFlagsFromNetWork(final String client_id) {

        T.i("从网络获取flag------------------------------------------------------------>");

        sendRequest(protocol + DHOC_GETFLAGS_HOST + ADHOC_GETFLAGS_PATH, client_id, String.valueOf(EventType.GET_EXPERIMENT_FLAGS), new OnAdHocReceivedData() {
            @Override
            public void onReceivedData(JSONObject response) {


                if (response != null && response.has("error")) {
                    String errMesg = null;
                    try {
                        errMesg = response.getString("error");
                    } catch (JSONException e) {
                        T.e("error !访问api出错：返回结果：" + response + " errorMessage: " + errMesg + "请处理");
                    }
                    T.e(new Exception(errMesg));
                    return;
                }
                // 缓存起来
                map.put(client_id, new FlagBean(response.toString(), System.currentTimeMillis()));
            }
        }, null, null);

    }

    private ExperimentFlags getNullExperFlag() {
        // 返回空的flag
        ExperimentFlags flag = new ExperimentFlags(new JSONObject());
        // init flag state
        flag.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_NULL.toString());
        return flag;
    }

    // 从本地去模块开关
    private FlagBean getMemoryFlags(String client_id) {

        T.i(client_id);

        FlagBean bean = map.get(client_id);

        return bean;
    }

    /**
     * 生成client_id
     **/
    public String generateClientId() {

        return UUID.randomUUID().toString().toLowerCase();
    }

    /**
     * 测试HashMap的删除
     */
    public static void fastRemovalTest() {
        System.out.println("removal test will start in 10 seconds.\n");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredClients();
            }
        }, 30000);
    }

    /**
     * 每天的HashMap删除
     * 指定时间是凌晨3点
     */
    private static void dailyRemovalSchedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredClients();
            }
        }, time, ONEDAY);
    }
    /**
     * 从HashMap中删除一天前的Client_id
     **/
    private static void removeExpiredClients() {
        Iterator iterator = map.entrySet().iterator();
        T.i("now starting remove expired clients.\n");
        long removalCount = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String client_id = (String) entry.getKey();
            FlagBean flagBean = (FlagBean) entry.getValue();
            if (System.currentTimeMillis() - flagBean.timeLast >= ONEDAY) {
                //System.out.println("client_id: " + client_id + "  is removed from HashMap.");
                T.i("client_id: " + client_id + "is removed from HashMap.");
                iterator.remove(); // to avoid java.util.ConcurrentModificationException
                removalCount++;
            }
        }
        //System.out.println("removal end. " + "removed "+ removalCount + " entries.");
        T.i("removal end. " + "removed "+ removalCount + " entries.");
    }

    /**
     * 上报指标统计
     **/
    public void incrementStat(String client_id, String stat, String value) {
        if (appkey == null) {
            T.e("appkey is null 请初始化 Adhocsdk");
            return;
        }
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, String.valueOf(EventType.REPORT_STAT), null, stat, value);
    }

    /**
     * 上报指标统计（int）
     **/
    public void incrementStat(String client_id, String stat, int value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, String.valueOf(EventType.REPORT_STAT), null, stat, value);
    }

    /**
     * 上报指标统计（long）
     **/
    public void incrementStat(String client_id, String stat, long value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, String.valueOf(EventType.REPORT_STAT), null, stat, value);
    }

    /**
     * 上报指标统计（float）
     **/
    public void incrementStat(String client_id, String stat, float value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, String.valueOf(EventType.REPORT_STAT), null, stat, value);
    }

    /**
     * 上报指标统计（double）
     **/
    public void incrementStat(String client_id, String stat, double value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, String.valueOf(EventType.REPORT_STAT), null, stat, value);
    }


    final class FlagBean {
        // flag str
        String flag;
        // 保存上次请求时间
        long timeLast;

        FlagBean(String flag, long timeLast) {
            this.flag = flag;
            this.timeLast = timeLast;
        }
    }
}
