package com.example.emiandroid.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emiandroid.R;
import com.example.emiandroid.classes.Report;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ReportsAdapter extends  RecyclerView.Adapter<ReportsAdapter.MyViewHolder>  {

    private List<Report> reportList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView srNo, date, monthlyInstalment, interest, principal, balance;

        public MyViewHolder(View view) {
            super(view);
            srNo = (TextView) view.findViewById(R.id.srNo);
            date = (TextView) view.findViewById(R.id.date5);
            monthlyInstalment = (TextView) view.findViewById(R.id.monthlyInstalment);
            interest = (TextView) view.findViewById(R.id.interest);
            principal = (TextView) view.findViewById(R.id.principal);
            balance =(TextView) view.findViewById(R.id.balance);
        }
    }

    public ReportsAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.srNo.setText(report.getMonthNo());
        holder.monthlyInstalment.setText(report.getMonthlyInstalment());
        holder.interest.setText(report.getInterest());
        holder.principal.setText(report.getPrincipal());
        holder.balance.setText(report.getBalance());
        holder.date.setText(report.getDate());

    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}
