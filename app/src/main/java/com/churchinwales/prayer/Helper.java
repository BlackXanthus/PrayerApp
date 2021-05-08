package com.churchinwales.prayer;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Helper {

    public Helper() {

    }

    protected String readFile(Context app_Context, String relativePath) throws IOException
    {
        String myData = "";


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

        return myData;
    }

    protected String readAsset(Context app_Context, String relativePath) throws IOException
    {
        String myData = "";


        //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

        InputStream file= app_Context.getAssets().open(relativePath);

        int size = file.available();
        byte[] buffer = new byte[size];
        file.read(buffer);
        myData = new String(buffer);

        file.close();

        return myData;
    }
}
