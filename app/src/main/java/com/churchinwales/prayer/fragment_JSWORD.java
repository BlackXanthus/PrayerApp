package com.churchinwales.prayer;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import org.crosswire.common.util.CWProject;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;

import org.crosswire.jsword.passage.NoSuchKeyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.crosswire.jsword.passage.Key;

import static android.os.SystemClock.sleep;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_JSWORD#newInstance} factory method to
 * create an instance of this fragment.
 *
 * https://github.com/AndBible/jsword
 */
public class fragment_JSWORD extends Fragment implements app_BiblePericope_Callback<String>, Observer, setJswordBible<Book>, setJswordVerse<String> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //This should really be somewhere in the Android System, but I couldn't find it!
    private static final int REQUEST_CODE_ASK_PERMISSONS =1;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    Executor myExecutor;
    TextView txt_Bible;
    BibleReadingsViewModel br_ViewModel= new BibleReadingsViewModel();

    protected Key[] gen11;
    protected BookMetaData[] bmds;
    protected Book[] bibles;
    protected Book bible;
    protected Boolean bibleSet=Boolean.FALSE;
    protected Boolean bibleFound=Boolean.FALSE;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_JSWORD() {
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
    public static fragment_JSWORD newInstance(String param1, String param2) {
        fragment_JSWORD fragment = new fragment_JSWORD();
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
        View rootView = inflater.inflate(R.layout.fragment_jsword, container, false);

        txt_Bible = rootView.findViewById(R.id.txt_Bible);
        txt_Bible.setMovementMethod(new ScrollingMovementMethod());
        try {
            Helper myHelper = new Helper();
            JSONObject theOrder = new JSONObject(myHelper.readAsset(getContext(),"Order.json"));
            br_ViewModel = new BibleReadingsViewModel(theOrder, "Order");
        }
        catch(Exception e) {
            Log.v("TAG", "Failed to load the order");
            e.printStackTrace();
            br_ViewModel = new BibleReadingsViewModel();
        }

        br_ViewModel.getObservable().observe(getViewLifecycleOwner(), this);

        File location = new File(String.valueOf(getContext().getCacheDir()));
        File[] myFile = {location};
        CWProject.setHome(getContext().getFilesDir().getPath(),getContext().getFilesDir().getPath()+"/JSWORD",".Jsword");


        HttpReqTask myTask = new HttpReqTask(executorService);

        myTask.getBibleBook(getString(R.string.app_WelshBibleJswordName), this);

        //getOnlineBibleReading();
        getJSWORDBible();

        return rootView;
    }

    public void getJSWORDBible() {

        HttpReqTask myTask = new HttpReqTask(executorService);
        Helper myHelper = new Helper();
        Context app_context = this.getContext();
        String prayerType = "MorningPrayer";
        JSONObject prayer = myHelper.getLectionaryJson(app_context, prayerType);




        while (bibleFound == Boolean.FALSE) {
                sleep(100);
        }

        if(bibleSet == Boolean.TRUE & this.bible!= null) {
            Book welbible = this.bible;

            BookMetaData bmds = welbible.getBookMetaData();


            br_ViewModel.setValue("header", "<h1>Beible.net</H1><br><br>");

            Book bible = welbible;


            bmds = bible.getBookMetaData();
            try {


                br_ViewModel.setValue("OldTestament", "<H2>Hen Testament</h2><br><h2>" + prayer.getString("OT") + "</h2><br>");
                br_ViewModel.setValue(prayer.getString("OT"), getString(R.string.app_loading) );
                myTask.getJswordVerse(bible, prayer.getString("OT"), prayer.getString("OT"),this);

                /*
                gen11 = bible.getKey(prayer.getString("NT"));
                test = gen11;
                /*
                We then get the key iterator (which is not listed in the docs!)
                and use that to pull out all the verses we need, verse by verse.
                */
                br_ViewModel.setValue("NewTestament", "<br><br><H2>Testament Newydd</h2><br><h2>" + prayer.getString("NT") + "</h2><br>");
                br_ViewModel.setValue(prayer.getString("NT"),getString(R.string.app_loading));
                myTask.getJswordVerse(bible, prayer.getString("NT"), prayer.getString("NT"),this);


            } catch (JSONException e) {
                Log.e("ERROR", "No Such Key In Prayer JSON");
                e.getStackTrace();
            }

        }
        else {
            br_ViewModel.setValue("Error", "Ddim Beible Ar Gael");
        }

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

    @Override
    public void setBible(Result result) {

        if(result instanceof Result.Success) {
            this.bibleSet = Boolean.TRUE;
            this.bibleFound = Boolean.TRUE;
            this.bible = (Book)((Result.Success)result).data;

        }
        else {
            this.bibleFound = Boolean.TRUE;
            this.bibleSet = Boolean.FALSE;
        }
    }

    @Override
    public void setJswordVerse(Result<String> result) {
        if(result instanceof Result.Success) {
            //  br_ViewModel.setValue(new SpannableStringBuilder(Html.fromHtml(((Result.Success<String>) result).data,Html.FROM_HTML_OPTION_USE_CSS_COLORS)));
            br_ViewModel.postValue((String)(((Result.Success) result).type),((String)((Result.Success) result).data));
        } else {
            br_ViewModel.postValue("Error","There was an error");
        }


    }

}