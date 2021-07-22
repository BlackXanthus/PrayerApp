package com.churchinwales.prayer;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.crosswire.common.util.CWProject;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HttpsURLConnection;

interface app_BiblePericope_Callback<T> {
    void onComplete(Result<T> result);
}

interface setJswordBible<T> {
    void setBible(Result<T> result);


}

interface setJswordVerse<T> {
    void setJswordVerse(Result<T> result);

}

public class HttpReqTask  {

    private final ExecutorService executor;

    public HttpReqTask(ExecutorService executor) {
        this.executor = executor;

    }


    protected Result<String> request(String pericope, String section) {
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

            Boolean ignore = true;

            while ((line = rd.readLine()) != null) {

                Log.v("TAG", line);

                if(line.toLowerCase().startsWith("<div class=\"bibletext\">")) {
                    ignore = false;
                }
                if(line.toLowerCase().startsWith("</div>")) {
                    ignore=true;
                }
                if(line.toLowerCase().startsWith("<cite")) {
                        myData = myData+"<br><br>";
                        ignore=false;
                }

                if(!ignore) {
                    myData = myData + line;

                }
            }
            if(myData.equals("")) {
                myData = "Error getting bible reading for:"+pericope;
            }

            myResult = new Result.Success<String>(section,myData);



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

    public Book getBible(String bibleName) throws BookException {

        Book welbible = Books.installed().getBook(bibleName);
        return welbible;
    }

    public void getBibleBook(final String bibleName, setJswordBible<Book> callback) {
        executor.execute(new Runnable() {
           public void run() {
               try {

                   Book bible = getBible(bibleName);
                   Result success = new Result.Success(bibleName, bible);
                   callback.setBible(success);

               }
               catch(BookException e) {
                   Result error = new Result.Error(e);
                   e.printStackTrace();
               }
           }
        });

    }

    public void getJswordVerse(final Book bible, String theKey, String section, setJswordVerse<String> callback) {
        executor.execute(new Runnable() {
            public void run() {
                try {
                        //this produces a series of keys

                        Key gen11 = bible.getKey(theKey);
                        Key test = gen11;
                /*
                We then get the key iterator (which is not listed in the docs!)
                and use that to pull out all the verses we need, verse by verse.
                */
                        Iterator<Key> testKey = test.iterator();

                        String text = "";
                        while (testKey.hasNext()) {
                            Key theKey = testKey.next();
                            Log.v("TAG", theKey.getName() + " " + bible.getRawText(theKey));
                            text = text + bible.getRawText(theKey);
                        }

                        Result<String> result = new Result.Success<String>(section, text);
                        callback.setJswordVerse(result);
                }
                catch(BookException e) {
                    Result error = new Result.Error(e);
                    e.printStackTrace();
                }
                catch(NoSuchKeyException e) {
                    Result error = new Result.Error(e);
                    e.printStackTrace();
                }
            }
        });

    }


    public void makeBibleRequest(final String pericope,String section, app_BiblePericope_Callback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<String> result = request(pericope, section);
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
