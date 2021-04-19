package com.churchinwales.prayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
        tv_Prayer = (TextView)findViewById(R.id.txt_MainView);
        tv_Title = (TextView)findViewById(R.id.txt_title);

        //This needs to be a translatable string. TODO
        tv_Title.append("Morning Prayer");
        this.setUpPrayer();
    }


    protected void setUpPrayer()
    {
        SpannableStringBuilder myDocument = new SpannableStringBuilder("Morning Prayer");

        SpannableStringBuilder opening = new SpannableStringBuilder("Opening Responsorial");

        myDocument.append(opening);

        myDocument.append(responsorials());

        tv_Prayer.setText(myDocument, TextView.BufferType.NORMAL);




    }

    protected SpannableStringBuilder responsorials()
    {
        String myString = "";
        myString = myString + "O Lord, open our lips,<br><br>";
        myString = myString +"<em>And our mouth shall proclaim your praise.</em><br><br>";

        myString = myString +"    Glory to the Father, and to the Son,";
        myString = myString +"    and to the Holy Spirit;";
        myString = myString +"as it was in the beginning, is now,";
        myString = myString +"    and shall be for ever. Amen.";

        myString = myString +"    Silent prayer/reflection on the coming day.";

        myString = myString +"Early in the morning";
        myString = myString +"my prayer comes before you.";
        myString = myString +"    Lord, have mercy.";
        myString = myString +"    Lord, have mercy.";

        myString = myString +"    You speak in my heart and say";
        myString = myString +"‘Seek my face’;";
        myString = myString +"your face, Lord, will I seek.";
        myString = myString +"Christ, have mercy.";
        myString = myString +"    Christ, have mercy.";

        myString = myString +"    Let the words of my mouth and the meditation of my heart";
        myString = myString +" be acceptable in your sight, O Lord,";
        myString = myString +"    my strength and my redeemer.";
        myString = myString +"Lord, have mercy.";
        myString = myString +"Lord, have mercy.";


        Context app_Context = getApplicationContext();


        String myData = "";

        try {
            InputStream fis = app_Context.getAssets().open("MorningPrayer/Responsorial/Responsorial1.txt");

            //FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while((strLine = br.readLine())!= null) {
                myData = myData + strLine;
            }

            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String responsorial= "";

        if(myData == "") {
            responsorial = myString;
        }
        else {
            responsorial = myData;
        }


        SpannableStringBuilder myResponsorial = new SpannableStringBuilder(Html.fromHtml(responsorial));

        return myResponsorial;

    }
}