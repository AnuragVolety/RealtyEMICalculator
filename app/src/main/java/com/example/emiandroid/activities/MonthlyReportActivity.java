package com.example.emiandroid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.emiandroid.R;
import com.example.emiandroid.adapters.MonthlyInstalmentAdapter;
import com.example.emiandroid.classes.MonthlyInstalment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.emiandroid.activities.InstalmentDescriptionActivity.addMonth;
import static com.example.emiandroid.activities.MainActivity.instalmentLoanTaken;
import static com.example.emiandroid.activities.MainActivity.instalments;
import static com.example.emiandroid.activities.MainActivity.lastLoanTakenInsNo;
import static com.example.emiandroid.activities.MainActivity.mainLoanDuration;
import static com.example.emiandroid.activities.MainActivity.mainLoanStartDate;
import static com.example.emiandroid.activities.MainActivity.paymentStartDates;

public class MonthlyReportActivity extends AppCompatActivity {

    private List<MonthlyInstalment> monthlyInstalmentList = new ArrayList<>();
    private MonthlyInstalmentAdapter instalmentAdapter;
    private String loanStartDate;
    private int[] totalMonthlyInstalments;
    private Date[] allMonthlyInstalmentDates;
    TextView maxMonthlyInstalment, preferenceType2;
    GraphView linegraph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_monthly_report);

        loanStartDate = getIntent().getStringExtra("loanStartDate");
        long totalLoanTaken = getIntent().getIntExtra("totalLoanTaken", 0);
        double roi = getIntent().getDoubleExtra("roi", 0);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linegraph = (GraphView) findViewById(R.id.line_graph);
        maxMonthlyInstalment = (TextView) findViewById(R.id.maxMonthlyInstalment);
        preferenceType2 = (TextView) findViewById(R.id.preferenceType2); 
        preferenceType2.setText("Showing Report for "+getIntent().getStringExtra("preference")+" test");

        instalmentAdapter = new MonthlyInstalmentAdapter(monthlyInstalmentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(instalmentAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        if(!MainActivity.isPreEMI){
            totalMonthlyInstalments = new int[mainLoanDuration*12];
            allMonthlyInstalmentDates = new Date[mainLoanDuration*12];
            prepareFullEMIReportData();
            maxMonthlyInstalment.setText(""+totalMonthlyInstalments[totalMonthlyInstalments.length - 1]);
        }
        else{
            Date d1 = null, d2 = null;
            try{
                d1 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[10]);
                d2 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[0]);
                Log.e("date diffs: ", calculateDiff(d1, d2) + "");
            }
            catch (Exception e){

            }
            totalMonthlyInstalments = new int[((int) calculateDiff(d2, d1) + mainLoanDuration*12)];
            allMonthlyInstalmentDates = new Date[200 + mainLoanDuration*12];
            preparePreEMIReportData();
        }


        GridLabelRenderer gridLabel = linegraph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Month Number");
        gridLabel.setVerticalAxisTitle("Monthly Instalment");
        DataPoint[] dataPoints = new DataPoint[totalMonthlyInstalments.length]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < totalMonthlyInstalments.length; i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, totalMonthlyInstalments[i]); // not sure but I think the second argument should be of type double
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setColor(Color.RED);
        linegraph.addSeries(series);

        linegraph.getViewport().setMaxX(totalMonthlyInstalments.length);
        linegraph.getViewport().setMaxY(fetchMaxYValue(totalMonthlyInstalments[totalMonthlyInstalments.length - 1]));

        linegraph.getViewport().setYAxisBoundsManual(true);
        linegraph.getViewport().setXAxisBoundsManual(true);
        linegraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        linegraph.getViewport().setScalableY(true);
    }

    private int fetchMaxYValue(int number) {
        int tens=1;
        while (number > 0){
            number = number/10;
            tens = tens*10;
        }
        return tens;
    }

    private void prepareFullEMIReportData() {
        int monthNo = 1;
        String date = loanStartDate;
        MonthlyInstalment monthlyInstalment;
        boolean isLastInstalmentStarted = false;
        while (monthNo<=mainLoanDuration*12){
            totalMonthlyInstalments[monthNo-1] = 0;
            try {
                allMonthlyInstalmentDates[monthNo-1] = new SimpleDateFormat("MMM yy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i=0; i<11; i++){
                if(instalmentLoanTaken[i] && (compareStartDates(date, paymentStartDates[i]) || isLastInstalmentStarted)){
                    if(i == MainActivity.lastLoanTakenInsNo -1 && !isLastInstalmentStarted){
                        isLastInstalmentStarted = true;
                    }
                    totalMonthlyInstalments[monthNo-1]+=instalments[i];
                }
            }

            monthlyInstalment = new MonthlyInstalment(date,
                    ""+monthNo,
                    ""+totalMonthlyInstalments[monthNo-1]);
            try {
                date = "" + new SimpleDateFormat("MMM yy")
                        .format(addMonth(new SimpleDateFormat("MMM yy")
                                .parse(date), 1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthNo++;
            monthlyInstalmentList.add(monthlyInstalment);
        }

        instalmentAdapter.notifyDataSetChanged();
    }

    private void preparePreEMIReportData() {
        int monthNo =1;
        String date = loanStartDate;
        MonthlyInstalment monthlyInstalment;
        Date d1 = null, d2 = null;
        try{
            d1 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[10]);
            d2 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[0]);
            Log.e("date diffs: ", calculateDiff(d1, d2) + "");
        }
        catch (Exception e){
            
        }
        
        while(monthNo<= (int) calculateDiff(d2, d1))
        {
            totalMonthlyInstalments[monthNo-1]=0;
            try {
                allMonthlyInstalmentDates[monthNo-1] = new SimpleDateFormat("MMM yy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i=0; i<11; i++){
                Log.e( "if bracket: ", monthNo + " " + i + " " + date + " " + instalmentLoanTaken[10] + " " + totalMonthlyInstalments[62]);
                if(instalmentLoanTaken[i] && (compareStartDates(date, paymentStartDates[i]))){
                    totalMonthlyInstalments[monthNo-1]= (int) instalments[i];
                    if(i >= MainActivity.lastLoanTakenInsNo-1){
                        Log.e( "last loan taken: ", MainActivity.lastLoanTakenInsNo + "" );
                    }
                }
                else if(!instalmentLoanTaken[i] && i>0 && totalMonthlyInstalments[monthNo-1]==0){
                    Log.e( "else bracket: ", monthNo + " " + i + " " + date );
                    totalMonthlyInstalments[monthNo-1] = 1; // need to change

                }
            }

            monthlyInstalment = new MonthlyInstalment(date,
                    ""+monthNo,
                    ""+totalMonthlyInstalments[monthNo-1]);

            try {
                date = "" + new SimpleDateFormat("MMM yy")
                        .format(addMonth(new SimpleDateFormat("MMM yy")
                                .parse(date), 1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthNo++;
            monthlyInstalmentList.add(monthlyInstalment);
        }
        instalmentAdapter.notifyDataSetChanged();
        Log.e("61st monthly ", "" + totalMonthlyInstalments[60]);



        if(true){
            long pmt = getIntent().getLongExtra("pmt",0);
            for(int i=1; i<=mainLoanDuration*12; i++){
                monthlyInstalment = new MonthlyInstalment(date,
                        ""+monthNo,
                        ""+pmt);
                try {
                    date = "" + new SimpleDateFormat("MMM yy")
                            .format(addMonth(new SimpleDateFormat("MMM yy")
                                    .parse(date), 1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                totalMonthlyInstalments[monthNo-1] = Integer.parseInt(pmt + "");
                monthNo++;
                monthlyInstalmentList.add(monthlyInstalment);
            }
            instalmentAdapter.notifyDataSetChanged();
            maxMonthlyInstalment.setText(""+pmt);
        }
    }

    private boolean compareStartDates(String date1, String date2) {
        try {
            Date first = new SimpleDateFormat("MMM yy")
                    .parse(date1);
            Date second = new SimpleDateFormat("MMM yy")
                    .parse(date2);
            return first.after(second);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    double calculateDiff(Date instalmentDate, Date mainDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar instalment = Calendar.getInstance();
        instalment.setTimeInMillis(instalmentDate.getTime());

        //create calendar object for current day
        Calendar main = Calendar.getInstance();
        main.setTimeInMillis(mainDate.getTime());

        //Get difference between years
        years = main.get(Calendar.YEAR) - instalment.get(Calendar.YEAR);
        int currMonth = main.get(Calendar.MONTH) + 1;
        int birthMonth = instalment.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one
        //and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (main.get(Calendar.DATE) < instalment.get(Calendar.DATE))
                months--;
        } else if (months == 0 && main.get(Calendar.DATE) < instalment.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        //Calculate the days
        if (main.get(Calendar.DATE) > instalment.get(Calendar.DATE))
            days = main.get(Calendar.DATE) - instalment.get(Calendar.DATE);
        else if (main.get(Calendar.DATE) < instalment.get(Calendar.DATE))
        {
            int today = main.get(Calendar.DAY_OF_MONTH);
            main.add(Calendar.MONTH, -1);
            days = main.getActualMaximum(Calendar.DAY_OF_MONTH) - instalment.get(Calendar.DAY_OF_MONTH) + today;
        }
        else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object

        return years*12 + (months) + days/30.4375;
    }
}
