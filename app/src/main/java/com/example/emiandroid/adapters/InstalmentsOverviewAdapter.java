package com.example.emiandroid.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emiandroid.R;
import com.example.emiandroid.activities.InstalmentDescriptionActivity;
import com.example.emiandroid.activities.MainActivity;
import com.example.emiandroid.classes.InstalmentOverview;
import com.example.emiandroid.fragments.NumberPickerFragment;
import com.shawnlin.numberpicker.NumberPicker;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class InstalmentsOverviewAdapter extends  RecyclerView.Adapter<InstalmentsOverviewAdapter.MyViewHolder>  {
    public List<InstalmentOverview> instalmentOverviewList;
    private Context viewContext, applicationContext;
    private ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private TextView loanAmountTaken, loanStartDate;
    SimpleDateFormat format = new SimpleDateFormat("MMM yy");
    SimpleDateFormat format2 = new SimpleDateFormat("MM yyyy");
    Date date, oldDate;
    @NonNull
    @Override
    public InstalmentsOverviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.instalment_overview_list_item, parent, false);
        return new InstalmentsOverviewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InstalmentsOverviewAdapter.MyViewHolder holder, int position) {
        final InstalmentOverview instalmentOverview = instalmentOverviewList.get(position);
        holder.instalmentNo.setText("Instalment "+instalmentOverview.getInstalmentNo());
        holder.instalmentIsLoanTaken.setChecked(instalmentOverview.getIsLoanTaken());
        holder.instalmentStartDate.setText(instalmentOverview.getInstalmentStartMonth());
        holder.instalmentWeightage.setText(instalmentOverview.getInstalmentWeightage() + "%");
        holder.instalmentAmount.setText(instalmentOverview.getInstalmentAmount());
        holder.numberPickerLayout.setVisibility(GONE);
        holder.textView8.setVisibility(GONE);
        holder.numberPicker.setVisibility(GONE);
        holder.instalmentNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MainActivity.isPreEMI){
                    progressDialog = ProgressDialog.show(applicationContext, "Generating Report for Instalment "+instalmentOverview.getInstalmentNo(),
                            "Please Wait...", true);
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Intent intent = new Intent(applicationContext, InstalmentDescriptionActivity.class);
                            intent.putExtra("InstalmentNo", instalmentOverview.getInstalmentNo());
                            intent.putExtra("sum", instalmentOverview.getInstalmentAmount());
                            intent.putExtra("date", instalmentOverview.getInstalmentStartMonth());
                            intent.putExtra("roi", "" + MainActivity.rateOfInterest);
                            intent.putExtra("loanDuration", ""+MainActivity.mainLoanDuration);
                            intent.putExtra("loan", instalmentOverview.getIsLoanTaken());
                            applicationContext.startActivity(intent);
                        }
                    }, 1000);
                }
            }
        });
        holder.instalmentIsLoanTaken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("loan" + instalmentOverview.getInstalmentNo(),b);
                editor.apply();
                instalmentOverview.setIsLoanTaken(b);
                calculateTotalLoanAmount();
                setLoanStartDate();
                findLastLoanTakenInsNo();
            }
        });
        viewContext = holder.instalmentStartDate.getContext();
        holder.instalmentStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    oldDate = format.parse(instalmentOverview.getInstalmentStartMonth());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createDialogWithoutDateField(
                        instalmentOverview.getInstalmentNo(),
                        viewContext,
                        instalmentOverview.getInstalmentStartMonth(),
                        holder.instalmentStartDate);
            }
        });
        holder.instalmentWeightage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.numberPicker.getVisibility() == VISIBLE){
                    holder.numberPickerLayout.setVisibility(GONE);
                    holder.textView8.setVisibility(GONE);
                    holder.numberPicker.setVisibility(GONE);
                }
                else{
                    holder.numberPickerLayout.setVisibility(View.VISIBLE);
                    holder.textView8.setVisibility(View.VISIBLE);
                    holder.numberPicker.setVisibility(View.VISIBLE);
                    holder.numberPicker.setValue(Integer.parseInt(instalmentOverview.getInstalmentWeightage()));
                    holder.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            holder.instalmentWeightage.setText(""+newVal+"%");
                            MainActivity
                                    .tempWeightsArray[
                                            Integer.parseInt(instalmentOverview
                                                            .getInstalmentNo())-1] = newVal;
                            if(!MainActivity.isBlinking)
                            {
                                MainActivity.blink();
                            }
                        }
                    });
                }

            }
        });
    }

    private void findLastLoanTakenInsNo() {
        for(int i=11; i>=1; i--){
            if(pref.getBoolean("loan"+i, false)){
                MainActivity.lastLoanTakenInsNo = i;
                Log.e(TAG, "findLastLoanTakenInsNo: " + MainActivity.lastLoanTakenInsNo);
                break;
            }
        }
    }


    private void setLoanStartDate() {
        for(int i=1; i<=11; i++){
            if(pref.getBoolean("loan"+i, false)){
                try {
                    MainActivity.mainLoanStartDate = new SimpleDateFormat("MMM yy")
                .format(InstalmentDescriptionActivity.addMonth(new SimpleDateFormat("MMM yy")
                .parse(instalmentOverviewList.get(i-1).getInstalmentStartMonth()), 1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        loanStartDate.setText(MainActivity.mainLoanStartDate);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView instalmentNo, instalmentStartDate, instalmentWeightage, instalmentAmount, textView8;
        public Switch instalmentIsLoanTaken;
        public NumberPicker numberPicker;
        public LinearLayout numberPickerLayout;

        public MyViewHolder(View view) {
            super(view);
            instalmentNo = (TextView) view.findViewById(R.id.instalmentNo);
            instalmentStartDate = (TextView) view.findViewById(R.id.date);
            instalmentWeightage = (TextView) view.findViewById(R.id.percent);
            instalmentAmount = (TextView) view.findViewById(R.id.sum);
            instalmentIsLoanTaken = (Switch) view.findViewById(R.id.isLoanTaken);
            numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
            numberPickerLayout = (LinearLayout) view.findViewById(R.id.numberPickerLayout);
            textView8 = (TextView) view.findViewById(R.id.textView8);
        }
    }

    public InstalmentsOverviewAdapter(Context applicationContext, List<InstalmentOverview> instalmentOverviewList, TextView loanAmountTaken, TextView loanStartDate) {
        pref = applicationContext.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        this.applicationContext = applicationContext;
        this.instalmentOverviewList = instalmentOverviewList;
        this.loanAmountTaken = loanAmountTaken;
        this.loanStartDate = loanStartDate;
    }

    @Override
    public int getItemCount() {
        return instalmentOverviewList.size();
    }

    private void createDialogWithoutDateField(final String InstalmentNo, final Context context, final String instalmentDate, final TextView textView) {
        final int[] newMonth = new int[1];
        final int[] newYear = new int[1];
        try {
            date = format.parse(instalmentDate);
            newMonth[0] = date.getMonth()+1;
            newYear[0] = date.getYear()+1900;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {}
                }, 2019, 9);
        try {
            builder.setActivatedMonth(oldDate.getMonth())
                    .setActivatedYear(oldDate.getYear()+1900)
                    .setMinYear(2000)
                    .setMaxYear(2200)
                    .setTitle("Select start month for Instalment "+InstalmentNo)
                    .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                        @Override
                        public void onMonthChanged(int selectedMonth) {
                            newMonth[0] = selectedMonth + 1;
                            displayChangedDate(InstalmentNo, textView, newMonth[0], newYear[0]);
                        }
                    })
                    .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                        @Override
                        public void onYearChanged(int selectedYear) {
                            newYear[0] = selectedYear;
                            displayChangedDate(InstalmentNo, textView, newMonth[0], newYear[0]);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e){

            Log.e(TAG, "createDialogWithoutDateField: " + resetDate(textView));
        }


    }

    private boolean resetDate(TextView textView) {
        return true;
    }

    private void calculateTotalLoanAmount(){
        int totalLoanTaken=0;
        for(int i=1; i<=11; i++){
            if(pref.getBoolean("loan"+i, false)){
                totalLoanTaken+=Integer.parseInt(instalmentOverviewList.get(i-1).getInstalmentAmount());
            }
        }
        loanAmountTaken.setText(""+totalLoanTaken);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private  void displayChangedDate(String InstalmentNo, TextView textView, int month, int year){
        try {
            if(month<9) {
                date = format2.parse("0"+month+ " "+year);
            }
            else {
                date = format2.parse(month+ " "+year);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateString = ""+format.format(date);
        textView.setText(dateString);
        instalmentOverviewList.get(Integer.parseInt(InstalmentNo)-1).setInstalmentStartMonth(dateString);
        if(MainActivity.isCustomTest){
            editor.putString("Cd"+InstalmentNo, dateString);
        }
        else {
            editor.putString("Pd"+InstalmentNo, dateString);
        }
        editor.apply();
    }
}
