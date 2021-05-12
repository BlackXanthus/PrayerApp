package com.churchinwales.prayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class HttpReqTask extends AsyncTask<Void,Void,Void> {

    protected Void doInBackground(Void ... params) {
        URLConnection urlConnection = null;
        HttpURLConnection https = null;

        try {
            URL url = new URL("https://bible.oremus.org");


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
            }

            //txt_Bible.setText(line);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (https != null) {
                https.disconnect();
            }
        }

        return null;
    }

}
