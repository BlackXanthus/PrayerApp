package com.churchinwales.prayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context app_Context = getApplicationContext();
        //Loading Dialogue Start Here
        ResourceLoader.unzipFromAssets(app_Context,"Prayer.zip","");

        tv_Prayer = (TextView)findViewById(R.id.txt_MainView);
        tv_Title = (TextView)findViewById(R.id.txt_title);

        //This needs to be a translatable string. TODO
        tv_Title.setText("Morning Prayer");
        this.setUpPrayer();
        //Loading Dialogue End here
    }


    protected void setUpPrayer()
    {
        SpannableStringBuilder myDocument = new SpannableStringBuilder("Morning Prayer<br>");

        SpannableStringBuilder opening = new SpannableStringBuilder("Opening Responsorial<br>");

        myDocument.append(opening);

        myDocument.append(confessional());

        tv_Prayer.setText(myDocument, TextView.BufferType.NORMAL);


    }

    protected SpannableStringBuilder confessional()
    {

        String notFoundError = "<em><strong>Confessional Failed to Load</em></strong><br><br>";

        Context app_Context = getApplicationContext();

        SpannableStringBuilder myData = readFile(app_Context,"Prayer/Confessional/En_BasicConfessional.txt");

        String confessional = "";

        if(myData.length() <= 0) {
            myData = new SpannableStringBuilder(Html.fromHtml(notFoundError));
        }

        return myData;

    }

    protected SpannableStringBuilder readFile(Context app_Context, String relativePath)
    {
        String myData = "";

        try {

            //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

            String fileName = app_Context.getFilesDir().getPath()+"/Prayer/Confessional/En_BasicConfessional.txt";

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

        SpannableStringBuilder myReturn = new SpannableStringBuilder(Html.fromHtml(myData));

        return myReturn;
    }
}