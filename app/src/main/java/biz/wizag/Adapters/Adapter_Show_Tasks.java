package biz.wizag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import biz.wizag.Models.Model_Show_Tasks;
import biz.wizag.timesheets.R;

public class Adapter_Show_Tasks extends RecyclerView.Adapter<Adapter_Show_Tasks.MyViewHolder>  {

    private List<Model_Show_Tasks> dataSet;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, startTime,endTime, project, task;




        public MyViewHolder(View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.date);
            this.startTime = itemView.findViewById(R.id.startTime);
            this.endTime = itemView.findViewById(R.id.endTime);
            this.project = itemView.findViewById(R.id.project);
            this.task = itemView.findViewById(R.id.task);
        }


    }

    public Adapter_Show_Tasks(List<Model_Show_Tasks> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }




    @Override
    public Adapter_Show_Tasks.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_show_tasks, parent, false);
        Adapter_Show_Tasks.MyViewHolder myViewHolder = new Adapter_Show_Tasks.MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final Adapter_Show_Tasks.MyViewHolder holder, final int listPosition) {
        final Model_Show_Tasks indivindual_sites= dataSet.get(listPosition);
        TextView date = holder.date;
        TextView startTime = holder.startTime;
        TextView endTime = holder.endTime;
        TextView project = holder.project;
        TextView task = holder.task;

        date.setText(indivindual_sites.getDate());
        startTime.setText(indivindual_sites.getStart_time());
        endTime.setText(indivindual_sites.getEnd_time());
        project.setText(indivindual_sites.getProject());
        task.setText(indivindual_sites.getTask());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
