package com.game.sirhowtask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends BaseAdapter {

    private final List<String> hotList;
    private final Context context;


    public HotelAdapter(Context contex, ArrayList<String> items) {
        hotList = items;
        context = contex;
    }

    @Override
    public int getCount() {
        return hotList.size();
    }

    @Override
    public Object getItem(int position) {
        return hotList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Log.d("dfdad","Enetred in getView");

        if(view==null)
            view= LayoutInflater.from(context).inflate(R.layout.hotel_list_layout, parent,false);

        TextView tv2 = view.findViewById(R.id.name);

        tv2.setText(hotList.get(position));

        return view;
    }
}
