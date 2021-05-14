package com.churchinwales.prayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.churchinwales.prayer.ui.Result;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_oremus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_oremus extends Fragment implements app_BiblePericope_Callback<String> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    Executor myExecutor;
    TextView txt_Bible;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_oremus() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_oremus.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_oremus newInstance(String param1, String param2) {
        fragment_oremus fragment = new fragment_oremus();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_oremus, container, false);

        txt_Bible = (TextView) rootView.findViewById(R.id.txt_Bible);
        txt_Bible.setMovementMethod(new ScrollingMovementMethod());

        getOnlineBibleReading();

        return rootView;
    }

    public void getOnlineBibleReading() {

        Helper myHelper = new Helper();
        JSONObject JSONObj_prayer = myHelper.getLectionaryJson(getContext(),"MorningPrayer");

        HttpReqTask myTask = new HttpReqTask(executorService);
        txt_Bible.setText("... loading");
        try {
            txt_Bible.append(new SpannableStringBuilder(Html.fromHtml("<H2>"+getString(R.string.app_MorningPrayer)+" "+getString(R.string.NewTestamentReading)+ ":"+JSONObj_prayer.getString("NT")+" </H2>",Html.FROM_HTML_OPTION_USE_CSS_COLORS)));
            myTask.makeBibleRequest(JSONObj_prayer.getString("NT"), this);
        }
        catch(Exception e) {
            txt_Bible.append("JSON Error");
        }

    }

    public void onComplete(Result<String> result)
    {
        if(result instanceof Result.Success) {
            txt_Bible.setText(new SpannableStringBuilder(Html.fromHtml(((Result.Success<String>) result).data,Html.FROM_HTML_OPTION_USE_CSS_COLORS)));
        } else {
            txt_Bible.setText("There was an error");
        }

    }


}