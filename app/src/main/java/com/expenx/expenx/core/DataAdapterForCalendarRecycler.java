package com.expenx.expenx.core;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expenx.expenx.R;

import java.util.ArrayList;

/**
 * Created by Imanshu on 5/8/2017.
 */

public class DataAdapterForCalendarRecycler extends RecyclerView.Adapter<DataAdapterForCalendarRecycler.ViewHolder> {
    private ArrayList<CalendarDataModel> dataSet;

    public DataAdapterForCalendarRecycler(ArrayList<CalendarDataModel> countries) {
        this.dataSet = countries;
    }

    @Override
    public DataAdapterForCalendarRecycler.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterForCalendarRecycler.ViewHolder viewHolder, int i) {

        viewHolder.rType.setText(dataSet.get(i).getTransactionType());
        viewHolder.rInfo.setText(dataSet.get(i).getTransactionInfo());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView rType;
        private TextView rInfo;
        public ViewHolder(View view) {
            super(view);

            rType = (TextView)view.findViewById(R.id.textType);
            rInfo = (TextView)view.findViewById(R.id.textInformation);
        }
    }

}
