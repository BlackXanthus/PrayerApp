package com.churchinwales.prayer;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

public class BibleReadingsViewModel extends ViewModel {

    MutableLiveData<Map> document = new MutableLiveData<Map>();


    public BibleReadingsViewModel()
    {
        super();
        Map temp = Collections.synchronizedMap(new HashMap());
        document.setValue(temp);
    }

    public String getValue(@NotNull String key) {
        return String.valueOf(document.getValue().get(key));
    }

    public void setValue(String key, String value)
    {
        Map temp = document.getValue();
        temp.put(key,value);
        document.setValue(temp);
    }
/**
    public void setAppendValue(String value) {
        document.setValue(document.getValue()+value);
    }

    public void postAppendValue(String value)
    {
        document.postValue(document.getValue()+value);
    }
**/
    public void postValue(String key, String value)
    {
        Map temp = document.getValue();
        temp.put(key,value);
        document.postValue(temp);
    }

    public MutableLiveData<Map> getObservable()
    {
        return document;
    }

    public String getPage(){
        String page = "";
        String line = "";
        Log.v("TAG", "Getting page...");
        Set s = document.getValue().keySet();
        synchronized(document.getValue()) {
            Iterator keys = s.iterator();
            Log.v("TAG", "Iterating...");
            while (keys.hasNext()) {
                String item = (String) keys.next();
                Log.v("TAG", page+item);
                page = page + document.getValue().get(item);

            }
        }

       return page;
    }


}