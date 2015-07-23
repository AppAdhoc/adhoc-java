package com.appadhoc.javasdk;

/**
 * Created by dongyuangui on 15/7/20.
 */
public class TestSDK {

    public static void main(String[] args) {


        AdhocSdk.getInstance().init("ADHOC_50000000000000ad80c23462");


        String client_id = AdhocSdk.getInstance().generateClientId();



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
        for(int i=0;i<100;i++){
            System.out.println("position : " +i);
            AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());
        }
        AdhocSdk.getInstance().getExperimentFlags(id);
        AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());
        AdhocSdk.getInstance().getExperimentFlags(AdhocSdk.getInstance().generateClientId());

        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", "1");
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1l);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1.0f);
        AdhocSdk.getInstance().incrementStat(client_id, "buy_success", 1.0d);
    }
}
