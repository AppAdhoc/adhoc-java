package com.appadhoc.javasdk;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dongyuangui on 15/7/20.
 */
public class TestSDK {

    public static void main(String[] args) {

        // flag内容有误
        // 过期没有清楚

        AdhocSdk.getInstance().init("ADHOC_50000000000000ad80c23462");

        AdhocSdk.getInstance().setCustomPara(new HashMap<String, String>());

        HashMap map = new HashMap<String, String>();
        map.put("male", "男");

        AdhocSdk.getInstance().setCustomPara(map);

        String client_id = AdhocSdk.getInstance().generateClientId();
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1.0f);

        AdhocSdk.getInstance().getExperimentFlags(client_id, new OnAdHocReceivedData() {
            public void onReceivedData(JSONObject experimentFlags) {

                System.out.println("Get experiment flag by set listener " + experimentFlags.toString());
            }
        });

        ExperimentFlags flags = AdhocSdk.getInstance().getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags.getRawFlags().toString());


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExperimentFlags flags1 = AdhocSdk.getInstance().getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags1.getRawFlags().toString());

        ExperimentFlags flags2 = AdhocSdk.getInstance().getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags2.getRawFlags().toString());

        ExperimentFlags flags3 = AdhocSdk.getInstance().getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags3.getRawFlags().toString());


        ExperimentFlags flags007 = AdhocSdk.getInstance().getSynExperimentFlag(AdhocSdk.getInstance().generateClientId(),1000);

        System.out.println("get Flag by syn method" + flags007.getRawFlags().toString());

        ExperimentFlags flag = AdhocSdk.getInstance().getExperimentFlags(client_id);

        T.i("flag is: " + flag.getFlagState().toString() + " values :" + flag.getRawFlags().toString());

        boolean value = flag.getBooleanFlag("btn_color", false);

        if (value) {
            T.i("flag is true");
        } else {
            T.i("flag is false");
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        AdhocSdk.getInstance().getExperimentFlags(client_id);
        String id = AdhocSdk.getInstance().generateClientId();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println("position : " + i);
            AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());
        }
        for (int i = 0; i < 100; i++) {
            ExperimentFlags flagss = AdhocSdk.getInstance().getSynExperimentFlag(AdhocSdk.getInstance().generateClientId(), 2000);
            System.out.println("position : " + i + "flags is :" +flagss.getRawFlags().toString());
        }
        for (int i = 0; i < 100; i++) {
            AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId(), new OnAdHocReceivedData() {
                @Override
                public void onReceivedData(JSONObject experimentFlags) {

                    System.out.println("experimentFlags : " +experimentFlags.toString());
                }
            });
        }

        AdhocSdk.getInstance().getExperimentFlags(id);
        AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());
        AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());

        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1l);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1.0f);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1.0d);
    }
}
