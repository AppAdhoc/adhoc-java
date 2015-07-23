package com.appadhoc.javasdk;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dongyuangui on 15-3-31.
 */
public class ClientImpl implements ClientInterface{

    private static int TIMEOUT = 20000;
    public static final String UNKNOWN = "UNKNOWN";
    @Override
    public String send(String targetURL, String urlParameters, OnAdHocReceivedData listener) {
        URL url;

        JSONObject jsonResp;
        HttpURLConnection connection = null;
        String result = null;
        T.i(urlParameters);
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // Replace all invisible unicode characters with empty string.
            urlParameters = urlParameters.replaceAll("\\p{C}", "");
//            connection.setRequestProperty("Content-Length", "" +
//                    Integer.toString(urlParameters.getBytes("utf-8").length));
            connection.setRequestProperty("Content-Language", "utf-8");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
//            connection.setConnectTimeout(contimeout);
            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(urlParameters.getBytes("utf-8"));
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            // No exception, we assume server is working and responding.
//            Long timestamp = System.currentTimeMillis();
//            if (timestamp > mCoarseLastSentTimestamp + TIME_GAP) {
//                // The different of the current SENT and the recorded SENT is large enough.
//                // Consider update the "mCoarseLastSentTimestamp".
//                WriteCoarseLastSendTimestamp(timestamp);
//            }
            // NOTE: we only keep the last response returned from server.
            // Do not use this for stateless concurrent requests.
            result = response.toString();

            T.i("req -- > " + targetURL + " \n result -- > " + result);

            if (result != null && !result.equals("")) {

                // 有响应
                jsonResp = new JSONObject(result);

                if (jsonResp.has("error")) {
                    T.e("error message " +result);
                }

                if(listener != null){

                    listener.onReceivedData(jsonResp);

                }

                T.i(result.toString());

            } else { // 无响应


                return UNKNOWN;

            }

            return result;

        } catch (Exception e) {
            T.i(targetURL + "  URL --->  +Cannot connect to SDK server..");
            T.e(e);
            return UNKNOWN;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
