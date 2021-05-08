package com.churchinwales.prayer;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_Lectionary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_Lectionary extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Helper theHelper;

    TextView tv_Lectionary;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_Lectionary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nav_Lectionary.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_Lectionary newInstance(String param1, String param2) {
        fragment_Lectionary fragment = new fragment_Lectionary();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        theHelper = new Helper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lectionary, container, false);

        tv_Lectionary = (TextView)rootView.findViewById(R.id.txt_LectionaryOutput);

       // tv_Lectionary.append("Hello World");

        getLectionary();

        return rootView;

    }

    /**
     * NOTE: Month is one digit down, January is month 0, not 1
     *
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getLectionary(){

        SpannableStringBuilder contents = new SpannableStringBuilder("");

        try {

            Context app_Context = getActivity().getApplicationContext();
            String myData = theHelper.readAsset(app_Context,"lectionary.json");

            String season="ADVENT";
            int weekOfSeason=1;
            String dayOfWeek="Monday";

            JSONObject jsonRootObject = new JSONObject(myData);

            Calendar cal = Calendar.getInstance();
            Calendar easter = new Calendar.Builder()
                    .setDate(2021, 3, 4)
                    .build();

            if( cal.compareTo(easter) > 0) {
                contents.append(Html.fromHtml("Date is after Easter<br>"));

             //   cal.add(Calendar.YEAR, - easter.get(Calendar.YEAR));
            //    cal.add(Calendar.MONTH, - easter.get(Calendar.MONTH));
            //    cal.add(Calendar.DAY_OF_MONTH, - easter.get(Calendar.DAY_OF_MONTH));
                contents.append(Html.fromHtml("Current Date: "+cal.get(Calendar.YEAR)+":"+cal.get(Calendar.MONTH)+ ":"+cal.get(Calendar.DAY_OF_MONTH)+"<BR>"));

                long weeks = cal.getTimeInMillis() - easter.getTimeInMillis();

                Calendar newCal = new Calendar.Builder().setInstant(weeks).build();

                int weeksSinceEaster = newCal.get(Calendar.WEEK_OF_YEAR);

                dayOfWeek = newCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

               // weeksSinceEaster = weeksSinceEaster / (24 * 60 * 60 * 1000);

                if(weeksSinceEaster <= 6) {
                    season = "EASTER";
                    weekOfSeason = weeksSinceEaster;
                }
                else {
                    season = "TRINITY";
                    weekOfSeason = weeksSinceEaster -6;
                }

                contents.append(Html.fromHtml("Weeks Since Easter: "+weeksSinceEaster+"<br> Date:"+newCal.get(Calendar.YEAR)+":"+newCal.get(Calendar.MONTH)+ ":"+newCal.get(Calendar.DAY_OF_MONTH)+"<BR>"));
                contents.append(Html.fromHtml("Season: "+season+"<BR>"));
            } else {
                if (cal.compareTo(easter) < 0) {
                    tv_Lectionary.append("Date is before Easter");
                }
            }

            Log.v("TAG","Season:"+season+" Week:"+String.valueOf(weekOfSeason)+ " Day:"+dayOfWeek);

            JSONObject jsonObject = jsonRootObject.optJSONObject(season);
            JSONObject week =jsonObject.optJSONObject(String.valueOf(weekOfSeason));
            JSONObject day = week.optJSONObject(dayOfWeek);
            JSONObject prayer =  day.optJSONObject("MorningPrayer");

            contents.append(Html.fromHtml("<br>"));
            contents.append(Html.fromHtml("Morning Prayer<br>"));
            contents.append(Html.fromHtml("Psalm: "+prayer.getString("Psalm")+"<BR>"));
            contents.append(Html.fromHtml("OT: "+prayer.getString("OT")+"<br>"));
            contents.append(Html.fromHtml("NT: "+prayer.getString("NT")+"<BR>"));
            contents.append(Html.fromHtml("<br><br>"));

            prayer =  day.optJSONObject("EveningPrayer");

            contents.append(Html.fromHtml("Evening Prayer<br>"));
            contents.append(Html.fromHtml("Psalm: "+prayer.getString("Psalm")+"<BR>"));
            contents.append(Html.fromHtml("OT: "+prayer.getString("OT")+"<br>"));
            contents.append(Html.fromHtml("NT: "+prayer.getString("NT")+"<BR>"));

            tv_Lectionary.append(contents);

        }

        catch(IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}