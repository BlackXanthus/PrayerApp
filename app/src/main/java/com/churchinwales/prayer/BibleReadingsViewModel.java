package com.churchinwales.prayer;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Iterator;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BibleReadingsViewModel extends ViewModel {

    MutableLiveData<ConcurrentHashMap> document = new MutableLiveData<ConcurrentHashMap>();
    JSONObject jsonobj_Order = new JSONObject();
    String type = "Order";
    Boolean order = false;

    public BibleReadingsViewModel(JSONObject theOrder, String JSONSubtype)
    {
        super();
        //Map temp = Collections.synchronizedMap(new HashMap());
        ConcurrentHashMap<String,String> temp = new ConcurrentHashMap<String,String>();
        document.setValue(temp);
        //jsonobj_Order = theOrder;
        jsonobj_Order = new JSONObject();

        type = JSONSubtype;
        order = true;
    }


    public BibleReadingsViewModel()
    {
        super();
        jsonobj_Order = new JSONObject();
       // Map temp = Collections.synchronizedMap(new HashMap());
        ConcurrentHashMap<String,String> temp = new ConcurrentHashMap<String,String>();
        document.setValue(temp);
        order = true;
    }

    public String getValue(@NotNull String key) {
        return String.valueOf(document.getValue().get(key));
    }

    public synchronized void setValue(String key, String value)
    {
        ConcurrentHashMap<String, String> temp = document.getValue();
        try{
            jsonobj_Order.put(key,value);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        if (temp == null) throw new AssertionError();
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
    public synchronized void postValue(String key, String value)
    {
        ConcurrentHashMap temp = document.getValue();
        temp.put(key,value);
        try{
            jsonobj_Order.put(key,value);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        document.postValue(temp);
    }

    public MutableLiveData<ConcurrentHashMap> getObservable()
    {
        return document;
    }

    public String getPage(){
        String page = "";
        String line = "";
        AppDebug.log("TAG", "Getting page...");

        if(order) {

                //JSONObject jsonOrder = jsonobj_Order.optJSONObject(type);
                JSONObject jsonOrder = jsonobj_Order;
                Iterator keys = jsonOrder.keys();

                while (keys.hasNext()) {
                    Object key = keys.next();

                    try {
                        String name = jsonOrder.getString((String) key);

                        page = page + document.getValue().get((String)key);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

        } else {
            Set s = document.getValue().keySet();
            synchronized (document.getValue()) {
                Iterator keys = s.iterator();
                AppDebug.log("TAG", "Iterating...");
                while (keys.hasNext()) {
                    String item = (String) keys.next();
                    AppDebug.log("TAG", page + item);
                    page = page + document.getValue().get(item);

                }
            }
        }

       return page;
    }


}
