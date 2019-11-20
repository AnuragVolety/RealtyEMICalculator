package com.example.emiandroid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emiandroid.R;
import com.example.emiandroid.adapters.ReportsAdapter;
import com.example.emiandroid.classes.Report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.emiandroid.activities.MainActivity.mainLoanStartDate;

public class InstalmentDescriptionActivity extends AppCompatActivity {
    private List<Report> reportList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportsAdapter mAdapter;

    TextView instalmentName, loanAmount, roi, loanStartDate, monthlyInstalment, loanDuration,
            blankText, noInstalmentStartDate, noInstalmentEndDate, noInstalmentBalance;
    Date instalmentDate, mainDate;
    String mainLoanDuration, noInstalmentEndDateString;
    private double delayDouble;
    long instalment, currentLoanAmount;
    LinearLayout noinstalmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_instalment_description);
        onInit();

        instalmentName.setText("Instalment " + getIntent().getStringExtra("InstalmentNo"));
        try {
            loanStartDate.setText("" + new SimpleDateFormat("MMM yy")
                    .format(addMonth(new SimpleDateFormat("MMM yy")
                            .parse(getIntent().getStringExtra("date")), 1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        roi.setText(getIntent().getStringExtra("roi"));
        mainLoanDuration = getIntent().getStringExtra("loanDuration");
        loanDuration.setText(mainLoanDuration);

        try {
            instalmentDate = new SimpleDateFormat("MMM yy").parse(loanStartDate.getText().toString());
            mainDate = new SimpleDateFormat("MMM yy").parse(mainLoanStartDate);
            long difference = (instalmentDate.getTime() - mainDate.getTime())/1000*12;
            delayDouble = round(difference/31536000, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        loanDuration.setText(String.valueOf(Double.parseDouble(mainLoanDuration) - round(delayDouble/12, 1)));
        Log.e( "getInstalments: ", "" + mainLoanDuration + " " + roi.getText().toString() + " " + getIntent().getStringExtra("sum"));
        instalment = Math.round(round(pmt(Double.parseDouble(roi.getText().toString()), Double.parseDouble(mainLoanDuration), Double.parseDouble(getIntent().getStringExtra("sum"))),1));
        monthlyInstalment.setText(Long.toString(instalment));

        if(!getIntent().getBooleanExtra("loan", false)){
            loanAmount.setText("0");
            monthlyInstalment.setText("0");
            recyclerView.setVisibility(View.GONE);
            blankText.setVisibility(View.VISIBLE);
            noinstalmentLayout.setVisibility(View.GONE);
        }
        else{
            loanAmount.setText(getIntent().getStringExtra("sum"));
            currentLoanAmount = (Long.parseLong(getIntent().getStringExtra("sum")));
            recyclerView.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);
            noinstalmentLayout.setVisibility(View.VISIBLE);
            noInstalmentBalance.setText(loanAmount.getText().toString());
            mAdapter = new ReportsAdapter(reportList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            prepareReportData();
        }

    }

    public static double pmt(double rate, double nper, double pv) {
        double v = (1 + (rate/1200));
        BigDecimal vPowNper = new BigDecimal(Math.pow(v, nper*12));
        BigDecimal result = vPowNper
                .multiply((new BigDecimal(pv * (rate / 1200))
                        .divide(vPowNper
                                .subtract(new BigDecimal(1)),2, RoundingMode.HALF_UP)));

        return result.doubleValue();
    }


    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void onInit() {
        instalmentName = (TextView) findViewById(R.id.instalmentNo);
        loanAmount = (TextView) findViewById(R.id.flatValue);
        roi = (TextView) findViewById(R.id.roi);
        loanStartDate = (TextView) findViewById(R.id.loanStartDate);
        loanDuration = (TextView) findViewById(R.id.loanDuration);
        monthlyInstalment = (TextView) findViewById(R.id.maxMonthlyInterest);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankText = (TextView) findViewById(R.id.blankText);
        noinstalmentLayout= (LinearLayout) findViewById(R.id.noinstalmentLayout);
        noInstalmentStartDate = (TextView) findViewById(R.id.startDate);
        noInstalmentEndDate = (TextView) findViewById(R.id.endDate);
        noInstalmentBalance = (TextView) findViewById(R.id.balance);
    }

    private void prepareReportData() {
        int monthNo = 1;
        long interest;
        String date=mainLoanStartDate;

        if(compareStartDates()){
            noInstalmentStartDate.setText(date);
            try {
                if((int)delayDouble!=0)
                {
                    date = "" + new SimpleDateFormat("MMM yy")
                            .format(addMonth(new SimpleDateFormat("MMM yy")
                                    .parse(mainLoanStartDate) ,(int)delayDouble));
                    monthNo=(int)delayDouble;
                }
                else{
                    date = "" + new SimpleDateFormat("MMM yy")
                            .format(addMonth(new SimpleDateFormat("MMM yy")
                                    .parse(mainLoanStartDate) ,1));
                    monthNo = 2;
                }
                noInstalmentEndDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            noinstalmentLayout.setVisibility(View.GONE);
        }
        Report report;
        while(currentLoanAmount > 0){
            interest = (long) (Math.round(currentLoanAmount * Double.parseDouble(roi.getText().toString()) / 100 / 12));
            currentLoanAmount -= (instalment - interest);
            if (currentLoanAmount < 0) {
                currentLoanAmount = 0;
            }
            report = new Report(date,
                    "" + monthNo,
                    "" + instalment,
                    "" + interest,
                    "" + (instalment - interest),
                    "" + currentLoanAmount);
            try {
                date = "" + new SimpleDateFormat("MMM yy")
                        .format(addMonth(new SimpleDateFormat("MMM yy")
                                .parse(date), 1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            reportList.add(report);
            monthNo++;
        }


        //noInstalmentEndDate.setText(noInstalmentEndDateString);
        mAdapter.notifyDataSetChanged();
    }

    private boolean compareStartDates() {
        try {
            Date secondDate = new SimpleDateFormat("MMM yy")
                    .parse(mainLoanStartDate);
            return instalmentDate.after(secondDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }
}
