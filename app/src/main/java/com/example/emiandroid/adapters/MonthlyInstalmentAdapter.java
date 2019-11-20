package com.example.emiandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emiandroid.R;
import com.example.emiandroid.classes.MonthlyInstalment;

import java.util.List;

public class MonthlyInstalmentAdapter extends  RecyclerView.Adapter<MonthlyInstalmentAdapter.MyViewHolder>  {
    public List<MonthlyInstalment> monthlyInstalmentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView srNo, date, monthlyInstalment;

        public MyViewHolder(View view) {
            super(view);
            srNo = (TextView) view.findViewById(R.id.srNo);
            date = (TextView) view.findViewById(R.id.date);
            monthlyInstalment = (TextView) view.findViewById(R.id.monthlyInstalment);

        }
    }

    public MonthlyInstalmentAdapter(List<MonthlyInstalment> monthlyInstalmentList) {
        this.monthlyInstalmentList = monthlyInstalmentList;
    }

    @NonNull
    @Override
    public MonthlyInstalmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monthly_instalment_list_item, parent, false);

        return new MonthlyInstalmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyInstalmentAdapter.MyViewHolder holder, int position) {
        MonthlyInstalment monthlyInstalment = monthlyInstalmentList.get(position);
        holder.srNo.setText(monthlyInstalment.getMonthNo());
        holder.date.setText(monthlyInstalment.getDate());
        holder.monthlyInstalment.setText(monthlyInstalment.getMonthlyInstalment());
    }

    @Override
    public int getItemCount() {
        return monthlyInstalmentList.size();
    }
}
