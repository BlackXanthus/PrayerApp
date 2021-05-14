package com.churchinwales.prayer;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.churchinwales.prayer.ui.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HttpsURLConnection;

interface app_BiblePericope_Callback<T> {
    void onComplete(Result<T> result);
}


public class HttpReqTask  {

    private final ExecutorService executor;

    public HttpReqTask(ExecutorService executor) {
        this.executor = executor;

    }


    protected Result<String> request(String pericope) {
        URLConnection urlConnection = null;
        HttpURLConnection https = null;
        String myData ="";

        Result myResult;

        try {
            URL url = new URL("https://bible.oremus.org?version=NRSVAE&passage="+ TextUtils.htmlEncode(pericope));


            https = (HttpsURLConnection) url.openConnection();

            // https.connect();

            int code = https.getResponseCode();
            if (code != 200) {
                Log.v("TAG", "No Connection");
                //txt_Bible.setText("No Connection");
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(https.getInputStream()));

            String line = "";

            while ((line = rd.readLine()) != null) {
                Log.v("TAG", line);
                myData = myData+line;
            }

            myResult = new Result.Success<String>(myData);


        } catch (Exception e) {
            e.printStackTrace();
            myData = "Exception!";
            myResult = new Result.Error(e);
        } finally {
            if (https != null) {
                https.disconnect();
            }
        }

        return myResult;
    }

    public void makeBibleRequest(final String pericope,app_BiblePericope_Callback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<String> result = request(pericope);
                    callback.onComplete(result);
                }
                catch(Exception e) {
                    Result<String> errorResult = new Result.Error<>(e);
                    callback.onComplete(errorResult);
                }
            }
        });

    }

}
