package com.churchinwales.prayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lectionary, container, false);

        tv_Lectionary = (TextView)rootView.findViewById(R.id.txt_LectionaryOutput);

        tv_Lectionary.append("Hello World");

        getLectionary();

        return rootView;

    }


    public void getLectionary(){

    }
}