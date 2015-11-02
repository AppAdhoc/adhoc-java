package com.appadhoc.javasdk;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dongyuangui on 15/7/15.
 */
public class AdhocSdk {

    private String protocol = "http://";
    private String ADHOC_GETFLAGS_PATH = "experiment.appadhoc.com/get_flags";
    private String ADHOC_TRACKING_HOST = "tracker.appadhoc.com/tracker";
    private static AdhocSdk instance = null;
    private static final String JSON_ERROR_STR = "Failed to get experiment flags.";
    private static ConcurrentHashMap<String, FlagBean> map = new ConcurrentHashMap<String, FlagBean>();

    private JSONObject customPara = new JSONObject();

    private static long GAPTIME = 300 * 1000;
//    private static long ONEDAY = 86400000;

    private AdhocSdk() {
    }

    public static AdhocSdk getInstance() {
        if (instance == null) {
            instance = new AdhocSdk();
        }
        return instance;
    }

    /**
     * 设置自定义用户属性
     **/
    public void setCustomPara(HashMap<String, String> custommap) {
        if (custommap == null) {
            return;
        }
        for (Map.Entry<String, String> entry : custommap.entrySet()) {

            try {
                customPara.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                T.e(e);
            }
        }
    }

    private String appkey;

    /**
     * 单例模式初始化需要用户的AppKey
     **/
    public void init(String appkey) {
        this.appkey = appkey;
        // 每隔5分钟清理一次缓存
        fastRemovalTest();
//        else dailyRemovalSchedule();
    }

    /**
     * 异步请求模块开关
     **/

    public void getExperimentFlags(String client_id, OnAdHocReceivedData listener) {
//        request

        FlagBean bean = getMemoryFlags(client_id);

//        ExperimentFlags flag = new ExperimentFlags(new JSONObject());

        if (bean != null) {
            try {
                if (listener != null) {
                    listener.onReceivedData(new JSONObject(bean.flag));
                }
            } catch (JSONException e) {
                T.e(e);
            }
        } else {
            JSONObject requestJson = getRequestJsonObj(client_id, null, null);
            ClientImpl net = new ClientImpl(map, client_id);
            net.send(protocol + ADHOC_GETFLAGS_PATH, requestJson.toString(), listener);
        }

    }

    /**
     * 同步方法获取模块开关 毫秒单位
     **/
    public ExperimentFlags getSynExperimentFlag(final String client_id, final int timeout) {


        // 取内存数据
        FlagBean bean = getMemoryFlags(client_id);
        final ExperimentFlags mFlags = getNullExperFlag();
        if (bean != null) {

            mFlags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());
            try {
                mFlags.setmFlags(new JSONObject(bean.flag));
            } catch (JSONException e) {
                T.e(e);
            }
            return mFlags;
        }

        final ClientImpl net = new ClientImpl(map, client_id);
        net.setResponseString(null);
        JSONObject requestJson = getRequestJsonObj(client_id, null, null);
        net.sendForResult(protocol + ADHOC_GETFLAGS_PATH, requestJson.toString());


        int timeoutMillis = timeout;
        do {
            try {
                Thread.sleep(50);

                T.i(timeoutMillis + net.getResponseString());

                timeoutMillis -= 50;

            } catch (InterruptedException e) {
                T.e(e);
            }
        } while (timeoutMillis > 0 && net.getResponseString() == null);

        if (net.getResponseString() != null && !net.getResponseString().equals("UNKNOWN")) {
            try {

                JSONObject object = new JSONObject(net.getResponseString());

                mFlags.setmFlags(object);

                mFlags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());


            } catch (JSONException e) {
                T.i(JSON_ERROR_STR);
            }
        } else {

            T.w("请求" + timeout + "内超时,取本地flag");
            FlagBean fbCache = getMemoryFlags(client_id);

            if (!fbCache.flag.equals("")) {
                try {

                    JSONObject jsObj = new JSONObject(fbCache.flag);

                    mFlags.setmFlags(jsObj);
                    mFlags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());

                } catch (JSONException e) {

                    T.i(JSON_ERROR_STR);

                }
            }
        }
        return mFlags;
    }


    private void sendRequest(String url, String client_id, OnAdHocReceivedData listener, String statkey, Object value) {

        JSONObject obj = getRequestJsonObj(client_id, statkey, value);
        T.i("request is " + obj.toString());
        ClientImpl impl = null;
        if (statkey == null) {
            impl = new ClientImpl(map, client_id);
        } else {
            impl = new ClientImpl(null, null);
        }
        impl.send(url, obj.toString(), listener);

    }

    private JSONObject getRequestJsonObj(String client_id, String statkey, Object value) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(Constants.app_key, appkey);
            obj.put(Constants.client_id, client_id);
            obj.put(Constants.summary, new JSONObject());
            obj.put(Constants.custom, customPara);

            if (statkey != null && value != null) {

                JSONArray array = new JSONArray();
                JSONObject stats = new JSONObject();
                stats.put(Constants.key, statkey);
                stats.put(Constants.value, value);
                stats.put(Constants.timestamp, System.currentTimeMillis() / 1000);
                array.put(stats);
                obj.put(Constants.stats, array);

            }

        } catch (JSONException e) {
            T.e(e);
        }
        return obj;
    }


    /**
     * 获取模块开关
     * 返回 @ExperimentFlags
     **/
    public ExperimentFlags getExperimentFlags(String client_id) {

        if (appkey == null) {

            T.e("appkey is null 请初始化 Adhocsdk");

            return null;
        }
        // 取内存数据
        FlagBean bean = getMemoryFlags(client_id);

        ExperimentFlags flag = getNullExperFlag();

        if (bean != null) {

            T.i("取内存结果：" + bean.flag);
            String flagStr = bean.flag;
//            todo
            if (flagStr != null && !flagStr.equals("") && flagStr.indexOf("error") == -1) {
                try {
                    flag.setmFlags(new JSONObject(flagStr));

                    flag.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());


                } catch (JSONException e) {
                    T.e("error ! flag value : " + flagStr + " when parse jsonobj");
                }
            }

            boolean isRequestFast = (System.currentTimeMillis() - bean.timeLast) < GAPTIME;

            T.i("isRequestFast :" + isRequestFast);
            // 是测试手机
            if (isRequestFast) {
                T.i("未从网络获取flag，距离上次取flag不足" + GAPTIME + "秒" + "duration is : "
                        + (System.currentTimeMillis() - bean.timeLast));
                return flag;
            } else {
                // 缓存过期，重新请求
                getExperimentFlagsFromNetWork(client_id);
            }

        } else {
            // 获取flag from network
            getExperimentFlagsFromNetWork(client_id);
        }

        return flag;
    }

    private void getExperimentFlagsFromNetWork(final String client_id) {

        T.i("从网络获取flag------------------------------------------------------------>");

        sendRequest(protocol + ADHOC_GETFLAGS_PATH, client_id, new OnAdHocReceivedData() {
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

        if(bean!=null){
            System.out.println("取缓存成功！" + bean.flag);
        }

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
    private static void fastRemovalTest() {
        System.out.println("removal test will start in 10 seconds.\n");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredClients();
            }
        }, 10000, GAPTIME);
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
        }, time, GAPTIME);
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
            if (System.currentTimeMillis() - flagBean.timeLast >= GAPTIME) {
                //System.out.println("client_id: " + client_id + "  is removed from HashMap.");
                T.i("client_id: " + client_id + "is removed from HashMap.");
                iterator.remove(); // to avoid java.util.ConcurrentModificationException
                removalCount++;
            }
        }
        //System.out.println("removal end. " + "removed "+ removalCount + " entries.");
        T.i("removal end. " + "removed " + removalCount + " entries.");
    }
    /**
     * 上报指标统计（int）
     **/
    public void incrementStat(String client_id, String stat, int value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, null, stat, value);
    }

    /**
     * 上报指标统计（long）
     **/
    public void incrementStat(String client_id, String stat, long value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, null, stat, value);
    }

    /**
     * 上报指标统计（float）
     **/
    public void incrementStat(String client_id, String stat, float value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, null, stat, value);
    }

    /**
     * 上报指标统计（double）
     **/
    public void incrementStat(String client_id, String stat, double value) {
        sendRequest(protocol + ADHOC_TRACKING_HOST, client_id, null, stat, value);
    }

    public static final class FlagBean {
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
