package com.expenx.expenx.core;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expenx.expenx.R;

import java.util.ArrayList;

/**
 * Created by Samintha on 5/10/2017.
 */

public class DataAdapterForNotesRecycler extends RecyclerView.Adapter<DataAdapterForNotesRecycler.ViewHolder> {
    private ArrayList<NoteDataModel> dataSet;
    private Activity activity;

    public DataAdapterForNotesRecycler(ArrayList<NoteDataModel> data, Activity activity) {
        this.dataSet = data;
        this.activity = activity;
    }

    @Override
    public DataAdapterForNotesRecycler.ViewHolder onCreateViewHolder(final ViewGroup viewGroup,final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout_notes, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapterForNotesRecycler.ViewHolder viewHolder, int i) {

        viewHolder.rType.setText(dataSet.get(i).getTitle());
        viewHolder.rInfo.setText(dataSet.get(i).getDescription());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDisplayDialog noteDisplayDialog = new NoteDisplayDialog();
                noteDisplayDialog.showDialog(activity, dataSet.get(viewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rType;
        private TextView rInfo;

        public ViewHolder(View view) {
            super(view);

            rType = (TextView) view.findViewById(R.id.textType);
            rInfo = (TextView) view.findViewById(R.id.textInformation);
        }
    }

}
