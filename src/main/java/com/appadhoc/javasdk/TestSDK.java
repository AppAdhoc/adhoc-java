package com.appadhoc.javasdk;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dongyuangui on 15/7/20.
 */
public class TestSDK {

    public static void main(String[] args) {

        // flag内容有误
        // 过期没有清除adhoc

        AdhocSdk app1 = new AdhocSdk("ADHOC_50000000000000ad80c23462");
        AdhocSdk app2 = new AdhocSdk("ADHOC_5bc01c24-617e-44e8-a779-1301f12f5809");

        app1.setCustomPara(new HashMap<String, String>());

        HashMap map = new HashMap<String, String>();
        map.put("male", "男");

        app1.setCustomPara(map);

        String client_id = app1.generateClientId();
        app1.incrementStat(client_id, "buy_success", 1.0f);

        app1.getExperimentFlags(client_id, new OnAdHocReceivedData() {
            public void onReceivedData(JSONObject experimentFlags) {

                System.out.println("Get experiment flag by set listener " + experimentFlags.toString());
            }
        });
        app2.getExperimentFlags(client_id, new OnAdHocReceivedData() {
            public void onReceivedData(JSONObject experimentFlags) {

                System.out.println("Get experiment flag by set listener app2 " + experimentFlags.toString());
            }
        });

        ExperimentFlags flags = app1.getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags.getRawFlags().toString());

        ExperimentFlags flagsApp2 = app2.getExperimentFlags(client_id);

        System.out.println("app2 asyn method " + flagsApp2.getRawFlags().toString());
        ExperimentFlags flags000App2 = app2.getExperimentFlags(client_id);

        System.out.println("app2 asyn method second " + flags000App2.getRawFlags().toString());


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExperimentFlags flags1 = app1.getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags1.getRawFlags().toString());

        ExperimentFlags flags2 = app1.getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags2.getRawFlags().toString());

        ExperimentFlags flags3 = app1.getExperimentFlags(client_id);

        System.out.println("Get experiment flag by asyn method " + flags3.getRawFlags().toString());


        ExperimentFlags flags007 = app1.getSynExperimentFlag(app1.generateClientId(), 1000);

        System.out.println("get Flag by syn method" + flags007.getRawFlags().toString());

        ExperimentFlags flags007app2 = app2.getSynExperimentFlag(app2.generateClientId(),1000);

        System.out.println("get Flag by syn method" + flags007app2.getRawFlags().toString());

        ExperimentFlags flag = app1.getExperimentFlags(client_id);

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
        app1.getExperimentFlags(client_id);
        String id = app1.generateClientId();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println("position : " + i);
            app1.getExperimentFlags(app1.generateClientId());
        }
        for (int i = 0; i < 100; i++) {
            ExperimentFlags flagss = app1.getSynExperimentFlag(app1.generateClientId(), 2000);
            System.out.println("position : " + i + "flags is :" +flagss.getRawFlags().toString());
        }
        for (int i = 0; i < 100; i++) {
            app1.getExperimentFlags(app1.generateClientId(), new OnAdHocReceivedData() {
                @Override
                public void onReceivedData(JSONObject experimentFlags) {

                    System.out.println("experimentFlags : " + experimentFlags.toString());
                }
            });
        }
        for (int i = 0; i < 100; i++) {
            app2.getExperimentFlags(app2.generateClientId(), new OnAdHocReceivedData() {
                @Override
                public void onReceivedData(JSONObject experimentFlags) {

                    System.out.println("experimentFlags : " + experimentFlags.toString());
                }
            });
        }

        app1.getExperimentFlags(id);
        app1.getExperimentFlags(app1.generateClientId());
        app1.getExperimentFlags(app1.generateClientId());

        app1.incrementStat(client_id, "buy_success", 1);
        app1.incrementStat(client_id, "buy_success", 1l);
        app1.incrementStat(client_id, "buy_success", 1.0f);
        app1.incrementStat(client_id, "buy_success", 1.0d);
    }
}
