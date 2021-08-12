package com.churchinwales.prayer.ui.home;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;


import com.churchinwales.prayer.AppDebug;
import com.churchinwales.prayer.Helper;
import com.churchinwales.prayer.R;

import java.io.IOException;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPrefs;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        /*
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
*/
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        textView = root.findViewById(R.id.text_home);
        Helper myHelper = new Helper();
        String todo="";
        try {
            todo = myHelper.readAsset(getActivity().getApplicationContext(), "Todo.txt");
        }
        catch(IOException e){
            todo = getString(R.string.Error)+" "+getString(R.string.FileNotFound)+": ToDo";
        }
        sharedPrefs = getDefaultSharedPreferences(getContext());
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        textView.setText(todo);
        textView.setMovementMethod(new ScrollingMovementMethod());
        /*
        homeViewModel.setText(todo);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                    public void onChanged(@Nullable String s) {
                    textView.setText(s);
                }
        });
*/

        textView.setText(new SpannableStringBuilder(Html.fromHtml(todo, Html.FROM_HTML_MODE_LEGACY)));
        textView.append("Version: "+getString(R.string.app_version));
        this.setDisplayFont();

        return root;
    }

    protected void setDisplayFont() {
        String currFont = sharedPrefs.getString("font", null);
        String currFontSize = sharedPrefs.getString("fontSize","14");


        AppDebug.log("TAG","Font Type "+currFont+ "Font Size:"+currFontSize);
        if(currFont != null) {
            if(currFont.contains(".otf")) {
                textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font/"+currFont));
                int currFontSizeInt = Integer.parseInt(currFontSize);
                textView.setTextSize(currFontSizeInt);
            }
            else {
                textView.setTypeface(Typeface.create(currFont, Typeface.NORMAL));
                textView.setTextSize(Integer.parseInt(currFontSize));
            }
        }
        else {
            textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            textView.setTextSize(Integer.parseInt(currFontSize));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        this.setDisplayFont();
    }

}