package com.churchinwales.prayer;

import android.Manifest;
import android.net.Uri;
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
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordBookPath;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.crosswire.jsword.passage.Key;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_JSWORD#newInstance} factory method to
 * create an instance of this fragment.
 *
 * https://github.com/AndBible/jsword
 */
public class fragment_JSWORD extends Fragment implements app_BiblePericope_Callback<String>, Observer {

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

        txt_Bible = (TextView) rootView.findViewById(R.id.txt_Bible);
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

        //getOnlineBibleReading();
        getJSWORDBible();

        return rootView;
    }

    public void getJSWORDBible() {

        File location = new File(String.valueOf(getContext().getCacheDir()));
        File[] myFile = {location};
        CWProject.setHome(getContext().getFilesDir().getPath(),getContext().getFilesDir().getPath()+"/JSword",".Jsword");


        File configFile = new File(getContext().getFilesDir().getPath()+"/mods.d/welbeiblnet.conf");
        //File bibleFile = new File(getContext().getFilesDir().getPath()+"/modules/texts/ztext/welbeiblnet/");
        File bibleFile = new File(getContext().getFilesDir().getPath());

        //Uri bibleLocation = Uri.fromFile(bibleFile);


        try {
            java.net.URI bibleLocation = new java.net.URI("File://"+bibleFile.getAbsolutePath());
            Log.v("TAG",bibleLocation.getPath());
            SwordBookMetaData sbmd = new SwordBookMetaData(configFile, bibleLocation);
            Log.v("TAG",sbmd.getName());
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }



        List<Book> lbmds = Books.installed().getBooks(BookFilters.getOnlyBibles());
        int numBibles = lbmds.size();
        Log.v("TAG","Total Bibles Found:"+numBibles);
        bibles = new Book[numBibles];
        bmds = new BookMetaData[numBibles];
        gen11 = new Key[numBibles];


        int i = 0;
        for (Book book : lbmds) {
            bibles[i] = book;
            bmds[i] = book.getBookMetaData();
            try {
                gen11[i] = book.getKey("Gen 1:1");
            }
            catch(NoSuchKeyException e) {
                e.getStackTrace();
            }
            i++;
            Log.v("TAG",bibles[i].getName());
        }
    }

    public void getOnlineBibleReading() {

        if(checkPermissions()) {
            Helper myHelper = new Helper();
            try {
                JSONObject JSOnObj_order = new JSONObject(myHelper.readAsset(getContext(), "Order.json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject JSONObj_prayer = myHelper.getLectionaryJson(getContext(), "MorningPrayer");

            HttpReqTask myTask = new HttpReqTask(executorService);
            txt_Bible.setText("... loading");
            try {
                //br_ViewModel.postAppendValue(new SpannableStringBuilder(Html.fromHtml("<H2>"+getString(R.string.app_MorningPrayer)+" "+getString(R.string.NewTestamentReading)+ ":"+JSONObj_prayer.getString("NT")+" </H2>",Html.FROM_HTML_OPTION_USE_CSS_COLORS)));

                br_ViewModel.setValue("OTIntro", "<br><br><H1>Old Testament Reading - Morning Prayer</H1><br><br>");
                br_ViewModel.setValue("OTTitle", "<H2>" + JSONObj_prayer.getString("OT") + "</H2><br>");
                br_ViewModel.setValue("OTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(JSONObj_prayer.getString("OT"), "OTVerse", this);
                br_ViewModel.setValue("NTIntro", "<H1>New Testament Reading - Morning Prayer</H1><br>");
                br_ViewModel.setValue("NTTitle", "<H2>" + JSONObj_prayer.getString("NT") + "</H2><br>");
                br_ViewModel.setValue("NTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(JSONObj_prayer.getString("NT"), "NTVerse", this);

                JSONObj_prayer = myHelper.getLectionaryJson(getContext(), "EveningPrayer");

                br_ViewModel.setValue("EP_OTIntro", "<br><br><H1>Old Testament Reading - Evening Prayer</H1><br><br>");
                br_ViewModel.setValue("EP_OTTitle", "<H2>" + JSONObj_prayer.getString("OT") + "</H2><br>");
                br_ViewModel.setValue("EP_OTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(JSONObj_prayer.getString("OT"), "EP_OTVerse", this);
                br_ViewModel.setValue("EP_NTIntro", "<H1>New Testament Reading -Evening Prayer</H1><br>");
                br_ViewModel.setValue("EP_NTTitle", "<H2>" + JSONObj_prayer.getString("NT") + "</H2><br>");
                br_ViewModel.setValue("EP_NTVerse", "..." + getString(R.string.app_loading));
                myTask.makeBibleRequest(JSONObj_prayer.getString("NT"), "EP_NTVerse", this);

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