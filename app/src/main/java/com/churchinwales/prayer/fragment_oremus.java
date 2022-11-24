package com.churchinwales.prayer;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Html;

import android.text.method.ScrollingMovementMethod;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_oremus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_oremus extends Fragment implements app_BiblePericope_Callback<String>, Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //This should really be somewhere in the Android System, but I couldn't find it!
    private static final int REQUEST_CODE_ASK_PERMISSONS =1;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    TextView txt_Bible;
    BibleReadingsViewModel br_ViewModel= new BibleReadingsViewModel();


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_oremus, container, false);

        txt_Bible = (TextView) rootView.findViewById(R.id.txt_Bible);
        txt_Bible.setMovementMethod(new ScrollingMovementMethod());
        br_ViewModel = new BibleReadingsViewModel();

        br_ViewModel.getObservable().observe(getViewLifecycleOwner(), this);

        getOnlineBibleReading();

        return rootView;
    }

    public void getOnlineBibleReading() {

        if(checkPermissions()) {
            Helper myHelper = new Helper();

            JSONObject JSONObj_prayer = myHelper.getLectionaryJson(getContext(), "MorningPrayer");

            HttpReqTask myTask = new HttpReqTask(executorService);
            txt_Bible.setText(getString(R.string.app_loading));
            try {
                //br_ViewModel.postAppendValue(new SpannableStringBuilder(Html.fromHtml("<H2>"+getString(R.string.app_MorningPrayer)+" "+getString(R.string.NewTestamentReading)+ ":"+JSONObj_prayer.getString("NT")+" </H2>",Html.FROM_HTML_OPTION_USE_CSS_COLORS)));
                String bibleVerse =  myHelper.checkBibleReading(JSONObj_prayer.getString("OT"));
                br_ViewModel.setValue("OTIntro", "<br><br><H1>Old Testament Reading - Morning Prayer</H1><br><br>");
                br_ViewModel.setValue("OTTitle", "<H2>" + bibleVerse + "</H2><br>");
                br_ViewModel.setValue("OTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(bibleVerse, "OTVerse", this);

                bibleVerse =  myHelper.checkBibleReading(JSONObj_prayer.getString("NT"));
                br_ViewModel.setValue("NTIntro", "<H1>New Testament Reading - Morning Prayer</H1><br>");
                br_ViewModel.setValue("NTTitle", "<H2>" + bibleVerse + "</H2><br>");
                br_ViewModel.setValue("NTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(bibleVerse, "NTVerse", this);

                JSONObj_prayer = myHelper.getLectionaryJson(getContext(), "EveningPrayer");

                bibleVerse =  myHelper.checkBibleReading(JSONObj_prayer.getString("OT"));
                br_ViewModel.setValue("EP_OTIntro", "<br><br><H1>Old Testament Reading - Evening Prayer</H1><br><br>");
                br_ViewModel.setValue("EP_OTTitle", "<H2>" + bibleVerse + "</H2><br>");
                br_ViewModel.setValue("EP_OTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(bibleVerse, "EP_OTVerse", this);

                bibleVerse =  myHelper.checkBibleReading(JSONObj_prayer.getString("NT"));
                br_ViewModel.setValue("EP_NTIntro", "<H1>New Testament Reading -Evening Prayer</H1><br>");
                br_ViewModel.setValue("EP_NTTitle", "<H2>" + bibleVerse + "</H2><br>");
                br_ViewModel.setValue("EP_NTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(bibleVerse, "EP_NTVerse", this);

            } catch (Exception e) {
                txt_Bible.append("JSON Error");
                e.printStackTrace();
            }
        }
        else {
            br_ViewModel.setValue("NTVerse","... no permission to access the internet");
            br_ViewModel.setValue("OTVerse","... no permission to access the internet");
        }

    }

    public boolean checkPermissions()
    {
        if(PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PermissionChecker.PERMISSION_GRANTED ) {
            return true;
        } else {
            String[] permissionArrays = new String[]{Manifest.permission.INTERNET};
            requestPermissions(permissionArrays, REQUEST_CODE_ASK_PERMISSONS);
        }
        return false;
    }

    public void onRequestPermissions(int requestCode, @NonNull String permissions[]) {
        Toast.makeText(getContext(), "Permission Requested", Toast.LENGTH_SHORT).show();
    }


    public void onComplete(Result<String> result)
    {
        if(result instanceof Result.Success) {
          //  br_ViewModel.setValue(new SpannableStringBuilder(Html.fromHtml(((Result.Success<String>) result).data,Html.FROM_HTML_OPTION_USE_CSS_COLORS)));
            br_ViewModel.postValue((((Result.Success<String>)result).type),(((Result.Success<String>)result).data));
        } else {
            br_ViewModel.postValue("Error","There was an error");
        }

    }


    @Override
    public void onChanged(Object o) {
        txt_Bible.setText(Html.fromHtml(br_ViewModel.getPage(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING));
    }
}