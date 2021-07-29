package com.churchinwales.prayer;

import android.annotation.SuppressLint;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import android.text.SpannableStringBuilder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

        theHelper = new Helper();
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lectionary, container, false);
        getActivity().setTitle(getString(R.string.app_Lectionary));

        tv_Lectionary = (TextView)rootView.findViewById(R.id.txt_LectionaryOutput);

       // tv_Lectionary.append("Hello World");

        updatePage();

        return rootView;

    }

    /**
     * NOTE: Month is one digit down, January is month 0, not 1
     *
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updatePage(){

        SpannableStringBuilder contents = theHelper.getLectionaryText(getActivity().getApplicationContext());
        tv_Lectionary.append(contents);
    }
}