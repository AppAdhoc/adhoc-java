package com.appadhoc.javasdk;


import org.json.JSONObject;

/**
 * Created by dongyuangui on 15-3-13..
 */
public interface OnAdHocReceivedData {
    void onReceivedData(JSONObject experimentFlags);
}
