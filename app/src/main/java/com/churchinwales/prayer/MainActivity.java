package com.churchinwales.prayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    TextView tv_Prayer;
    TextView tv_Title;
    //Note: this should use Androids built-in language stuffs
    String language="EN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context app_Context = getApplicationContext();
        //Loading Dialogue Start Here
        ResourceLoader.unzipFromAssets(app_Context,"Prayer.zip","");

        tv_Prayer = (TextView)findViewById(R.id.txt_MainView);
        tv_Prayer.setMovementMethod(new ScrollingMovementMethod());
        tv_Title = (TextView)findViewById(R.id.txt_title);

        //This needs to be a translatable string. TODO
        tv_Title.setText("Morning Prayer");
        this.setUpPrayer();
        //Loading Dialogue End here
    }


    protected void setUpPrayer()
    {
        Context app_Context = getApplicationContext();

        SpannableStringBuilder myDocument = new SpannableStringBuilder("");


        String myData = readFile(app_Context, "/Prayer/Layout/MorningPrayer.json");



        StringBuilder data = new StringBuilder();
        try {
            JSONObject jsonRootObject = new JSONObject(myData);
            JSONObject jsonObject = jsonRootObject.optJSONObject("MorningPrayer");


            JSONObject data_JSOB = jsonObject.getJSONObject("Name");
            String name = data_JSOB.getString(language);

            myDocument.append(name);

            data_JSOB = jsonObject.getJSONObject("Introduction");

            myDocument.append(getSection(app_Context,data_JSOB));


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        
        myDocument.append(confessional());

        tv_Prayer.setText(myDocument, TextView.BufferType.NORMAL);


    }

    protected SpannableStringBuilder getSection(Context app_Context, JSONObject data_JSOB) throws JSONException
    {
        String location = "/Prayer/"+data_JSOB.getString("Location")+"/"+language+"_"+data_JSOB.getString("File")+".txt";
        Log.v("TAG","Looking for:"+location);
        String Introduction= readFile(app_Context,location);
        SpannableStringBuilder intro = new SpannableStringBuilder(Html.fromHtml(Introduction));

        return intro;
    }

    protected SpannableStringBuilder confessional()
    {

        String notFoundError = "<em><strong>Confessional Failed to Load</em></strong><br><br>";

        Context app_Context = getApplicationContext();

        String myData = readFile(app_Context,"/Prayer/Confessional/En_BasicConfessional.txt");

        SpannableStringBuilder myReturn;

        String confessional = "";

        if(myData.length() <= 0) {
            myReturn = new SpannableStringBuilder(Html.fromHtml(notFoundError));
        }
        else {
            myReturn = new SpannableStringBuilder(Html.fromHtml(myData));
        }

        return myReturn;

    }

    protected String readFile(Context app_Context, String relativePath)
    {
        String myData = "";

        try {

            //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

            String fileName = app_Context.getFilesDir().getPath()+relativePath;

            File file = new File(fileName);
            //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

            FileInputStream fis = new FileInputStream(file);
            //FileInputStream fis = openFileInput(fileName);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while((strLine = br.readLine())!= null) {
                myData = myData + strLine;
            }
//
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return myData;
    }
}