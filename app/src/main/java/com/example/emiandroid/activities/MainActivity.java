package com.example.emiandroid.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.emiandroid.R;
import com.example.emiandroid.adapters.InstalmentsOverviewAdapter;
import com.example.emiandroid.classes.InstalmentOverview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.emiandroid.activities.InstalmentDescriptionActivity.addMonth;
import static com.example.emiandroid.activities.InstalmentDescriptionActivity.round;

public class MainActivity extends AppCompatActivity{

    public static String paymentStartDates[]; //unused
    public static long loanAmounts[]; //unused
    public static double instalments[]; //unused
    public static boolean instalmentLoanTaken[]; //unused
    public static double rateOfInterest;
    public static int mainLoanDuration;
    public static String mainLoanStartDate="";
    public static boolean isCustomTest=false;
    @SuppressLint("StaticFieldLeak")
    public static TextView  blinkText;
    public static ImageView done;
    public static boolean isBlinking=false;
    public static int tempWeightsArray[];
    public static  int lastLoanTakenInsNo = 11;
    public static boolean isPreEMI = false;


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    EditText flatValue, roi, loanDuration;
    private TextView loanTaken;
    private int totalLoanTaken;
    private Switch testSwitch, emiSwitch;
    private TextView loanStartDate, progressText, helpText;

    Date currentDate;
    SimpleDateFormat df;

    ProgressDialog progressDialog;
    ProgressBar progressBar;


    RecyclerView recyclerView;
    private InstalmentsOverviewAdapter instalmentsOverviewAdapter;
    private List<InstalmentOverview> instalmentOverviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        if(pref.getBoolean("FirstTime", true)) {
            appFirstTime();
        }

        onInit();

        roi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                try{

                }
                catch (Exception e){
                    Log.e( "onTextChanged: ",""+e );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    rateOfInterest = Double.parseDouble(editable.toString());
                }
                catch (Exception e){
                    Log.e( "onTextChanged: ",""+e );
                }
            }
        });

        loanDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{

                }
                catch (Exception e){
                    Log.e( "onTextChanged: ",""+e );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    mainLoanDuration = Integer.parseInt(editable.toString());
                }
                catch (Exception e){
                    Log.e( "onTextChanged: ",""+e );
                }
            }
        });

        emiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                helpText.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        isPreEMI = b;
                        if(b){

                        }
                        else{
                            helpText.setVisibility(View.VISIBLE);
                        }
                    }
                }, 2000);

            }
        });

        emiSwitch.setChecked(true);
        testSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                totalLoanTaken = Integer.parseInt(flatValue.getText().toString());
                isCustomTest=b;
                isBlinking=false;
                if(b){
                    generateInstalmentOverviewData("C");
                    setLoanStartDate();
                }
                else{
                    generateInstalmentOverviewData("P");
                    setLoanStartDate();
                }
                calculateTotalLoanAmount();
            }
        });
        
        
        flatValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    totalLoanTaken = Integer.parseInt(charSequence.toString());
                    if(testSwitch.isChecked()){
                        generateInstalmentOverviewData("C");
                    }
                    else{
                        generateInstalmentOverviewData("P");
                    }
                }
                catch (Exception e){
                    Log.e( "onTextChanged: ", "empty Input");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(testSwitch.isChecked()){
                    totalLoanTaken = Integer.parseInt(editable.toString());
                    generateInstalmentOverviewData("C");

                }
                else{
                    generateInstalmentOverviewData("P");
                }
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calculateTotalPercent()!=100){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Weightage Error")
                            .setMessage("Weightage(%) should total to 100")
                            .setNegativeButton("Ok", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
                    if(testSwitch.isChecked()){
                        for(int i=0; i<11; i++){
                            editor.putString("Cw"+(i+1),tempWeightsArray[i]+"");
                        }
                        editor.apply();
                        generateInstalmentOverviewData("C");
                    }
                    else{
                        for(int i=0; i<11; i++){
                            editor.putString("Pw"+(i+1),tempWeightsArray[i]+"");
                        }
                        editor.apply();
                        generateInstalmentOverviewData("P");
                    }
                    isBlinking = false;
                }

            }
        });


        Button generateMonthlyReport = (Button) findViewById(R.id.viewInstalments);
        generateMonthlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Generating Complete Report.",
                        "Please Wait...", true);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        setLoanStartDate();
                        if(getPaymentStartDates()){
                            getLoanAmounts(); //need
                            getInstalmentLoanTaken(); //need

                            if(!isPreEMI)
                            {
                                getFullEMIInstalments();
                            }
                            else {
                                getPreEMIInstalments();
                            }


                            Intent intent = new Intent(MainActivity.this, MonthlyReportActivity.class);
                            intent.putExtra("loanStartDate", loanStartDate.getText().toString());
                            intent.putExtra("totalLoanTaken", totalLoanTaken);
                            intent.putExtra("roi", Double.parseDouble(roi.getText().toString()));
                            long difference = 0;
                            if(isPreEMI){

                                intent.putExtra("pmt", Math.round(round(InstalmentDescriptionActivity.pmt(
                                        Double.parseDouble(roi.getText().toString()),
                                        Math.round(Double.parseDouble("" + mainLoanDuration)),
                                        getTotalLoanAount()), 1)));
                            }
                            if(!testSwitch.isChecked()){
                                intent.putExtra("preference", "Piramal");
                            }
                            else{
                                intent.putExtra("preference", "Custom");
                            }
                            progressDialog.dismiss();

                            for(int i=0; i<11; i++){
                                Log.e("run: ", i + " " + instalmentLoanTaken[i] + " " + paymentStartDates[i] + " " + loanAmounts[i] + " " + instalments[i] );
                            }
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                        }

                    }
                }, 2000);
            }
        });


    }

    private int calculateTotalPercent() {
        int total = 0;
        for(int i=0; i<11; i++){
            Log.e("calculateTotalPercent: ", "" + tempWeightsArray[i]);
            total+=tempWeightsArray[i];
        }

        return total;
    }

    private void getInstalmentLoanTaken() {
        for(int i=0; i<11; i++){
            instalmentLoanTaken[i] = pref.getBoolean("loan"+(i+1),true);
        }
    }

    private void getFullEMIInstalments() {
        Date instalmentDate, mainDate;
        double delayDouble = 0;
        for(int i=0; i<11; i++){
            try {
                if(testSwitch.isChecked()){
                    instalmentDate = new SimpleDateFormat("MMM yy").parse(pref.getString("Cd"+(i+1),""));

                }
                else{
                    instalmentDate = new SimpleDateFormat("MMM yy").parse(pref.getString("Pd"+(i+1),""));
                }
                instalmentDate = addMonth(instalmentDate,1);
                mainDate = new SimpleDateFormat("MMM yy").parse(mainLoanStartDate);
                delayDouble = round(calculateDiff(mainDate, instalmentDate),3);

            } catch (ParseException e) {
                Log.e( "getInstalments: ", i + " "+e );
            }
            instalments[i] = Math.round(round(InstalmentDescriptionActivity.pmt(
                                    Double.parseDouble(roi.getText().toString()),
                                    Double.parseDouble("" + mainLoanDuration) - delayDouble,
                                    loanAmounts[i]), 1));


        }
    }

    private void getPreEMIInstalments(){
        long total =0;
        for(int i=0; i<11; i++){
            if(instalmentLoanTaken[i])
            {
                total+=loanAmounts[i];
                instalments[i] = Math.round(Double.parseDouble(roi.getText().toString())/12*total/100);
            }
            else{
                instalments[i]=0;
            }
        }
    }

    private void getLoanAmounts() {
        if(testSwitch.isChecked()){
            for(int i=0; i<11; i++){
                loanAmounts[i] = Long.parseLong(pref.getString("Cw"+(i+1), "" ))*Long.parseLong(flatValue.getText().toString())/100;
            }
        }
        else{
            for(int i=0; i<11; i++){
                loanAmounts[i] = Long.parseLong(pref.getString("Pw"+(i+1), "" ))*Long.parseLong(flatValue.getText().toString())/100;
            }
        }
    }


    private boolean getPaymentStartDates() {
        for(int i=1; i<=11; i++){
            if (testSwitch.isChecked()) {
                paymentStartDates[i-1] = (pref.getString("Cd" + i, ""));
            } else {
                paymentStartDates[i-1] = (pref.getString("Pd" + i, ""));
            }

        }
        for (int i=1; i<11; i++){
            if(!compareDates(paymentStartDates[i], paymentStartDates[i-1] )){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Instalment Dates")
                        .setMessage("Instalment Dates should be in ascending order only. Please check your Instalment dates and try again.")
                        .setNegativeButton("Ok", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
            Date d1= null, d2 =null;
            try {
                d1 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[0]);
                d2 = new SimpleDateFormat("MMM yy").parse(paymentStartDates[10]);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(calculateDiff(d1, d2)>15){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Instalment Dates")
                        .setMessage("Instalment Dates should be in a margin of less than or equal to 180 months only. Please check your Instalment dates and try again.")
                        .setNegativeButton("Ok", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        }
        return true;
    }

    public static boolean compareDates(String date1, String date2) {
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

    private void setLoanStartDate() {
        for(int i=1; i<=11; i++){
            if(pref.getBoolean("loan"+i,false)){
                try {
                if (testSwitch.isChecked()) {
                    mainLoanStartDate = new SimpleDateFormat("MMM yy")
                            .format(InstalmentDescriptionActivity.addMonth(new SimpleDateFormat("MMM yy")
                                    .parse(pref.getString("Cd" + i, "")),1) );
                } else {
                    mainLoanStartDate = new SimpleDateFormat("MMM yy")
                            .format(InstalmentDescriptionActivity.addMonth(new SimpleDateFormat("MMM yy")
                                    .parse(pref.getString("Pd" + i, "")),1) );
                }
                loanStartDate.setText("" + mainLoanStartDate);
                break;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void appFirstTime() {
        editor.putBoolean("FirstTime", false);

        editor.putString("flatValue", "10000000");

        editor.putString("Pw1", "5");
        editor.putString("Pw2", "5");
        editor.putString("Pw3", "10");
        editor.putString("Pw4", "10");
        editor.putString("Pw5", "15");
        editor.putString("Pw6", "5");
        editor.putString("Pw7", "15");
        editor.putString("Pw8", "5");
        editor.putString("Pw9", "15");
        editor.putString("Pw10", "10");
        editor.putString("Pw11", "5");

        editor.putString("Cw1", "5");
        editor.putString("Cw2", "5");
        editor.putString("Cw3", "10");
        editor.putString("Cw4", "10");
        editor.putString("Cw5", "15");
        editor.putString("Cw6", "5");
        editor.putString("Cw7", "15");
        editor.putString("Cw8", "5");
        editor.putString("Cw9", "15");
        editor.putString("Cw10", "10");
        editor.putString("Cw11", "5");

        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("MMM yy");

        for(int i=1; i<=11; i++){
            currentDate = c.getTime();
            editor.putString("Pd" + i, df.format(currentDate));
            editor.putString("Cd" + i, df.format(currentDate));
            c.add(Calendar.DATE, 180);
        }
        editor.apply();
    }

    private void onInit() {
        flatValue = (EditText) findViewById(R.id.flatValue);
        flatValue.setText(pref.getString("flatValue","0"));
        loanTaken = (TextView) findViewById(R.id.totalLoanAmount);
        totalLoanTaken = Integer.parseInt(flatValue.getText().toString());
        testSwitch = (Switch) findViewById(R.id.testSwitch);
        emiSwitch = (Switch) findViewById(R.id.emiSwitch);
        roi = (EditText) findViewById(R.id.roi);
        loanDuration =(EditText) findViewById(R.id.loanDuration);
        mainLoanDuration = Integer.parseInt(loanDuration.getText().toString());
        loanStartDate = (TextView) findViewById(R.id.loanStartDate);
        blinkText = (TextView) findViewById(R.id.blinkText);
        blinkText.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        helpText = (TextView) findViewById(R.id.helpText);

        done = (ImageView) findViewById(R.id.done);
        done.setVisibility(View.INVISIBLE);


        recyclerView = (RecyclerView) findViewById(R.id.instalmentRecyclerView);
        instalmentsOverviewAdapter = new InstalmentsOverviewAdapter
                (MainActivity.this,
                        instalmentOverviewList,
                        loanTaken,
                        loanStartDate);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(instalmentsOverviewAdapter);
        rateOfInterest = Double.parseDouble(roi.getText().toString());


        setPrefs();

        paymentStartDates = new String[11];
        loanAmounts = new long[11];
        instalments = new double[11];
        instalmentLoanTaken = new boolean[11];
        tempWeightsArray = new int[11];
        for(int i=0; i<11; i++){
            tempWeightsArray[i] = 0;
        }
        setLoanStartDate();
    }

    private void setPrefs() {
        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("MMM yy");

        for(int i=1; i<=11; i++){
            currentDate = c.getTime();
            editor.putString("Pd"+i, df.format(currentDate));
            editor.putBoolean("loan"+i, true);
            c.add(Calendar.DATE, 180);
        }
        editor.apply();
    }

    private void generateInstalmentOverviewData(String preference) {
        String startDate, weightage, loanAmount;
        Boolean isLoanTaken;
        if(instalmentOverviewList.size()!=0)
        {instalmentOverviewList.clear();}

        for(int i=1; i<=11; i++){
            weightage = pref.getString(preference + "w"+i, "" );
            tempWeightsArray[i-1] = Integer.parseInt(pref.getString(preference + "w"+i, ""));
            startDate = pref.getString(preference + "d"+i, "Jan 19");
            loanAmount = Integer.toString(Integer.parseInt(weightage) * totalLoanTaken/100);
            isLoanTaken = pref.getBoolean("loan"+i, false);
            instalmentOverviewList.add(new InstalmentOverview
                    (""+i, startDate, weightage, loanAmount, isLoanTaken));
        }
        loanTaken.setText(String.valueOf(totalLoanTaken));
        instalmentsOverviewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(testSwitch.isChecked()){
            generateInstalmentOverviewData("C");
        }
        else{
            generateInstalmentOverviewData("P");
        }
        calculateTotalLoanAmount();
    }

    public static double calculateDiff(Date instalmentDate, Date mainDate)
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

        return years + (months + days/30.4375)/12;
    }

    public static void blink(){
        isBlinking=true;
        done.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isBlinking) {

                            if (blinkText.getVisibility() == View.VISIBLE) {
                                blinkText.setVisibility(View.INVISIBLE);
                            } else {
                                blinkText.setVisibility(View.VISIBLE);
                            }
                            blink();
                        }
                        else{
                            done.setVisibility(View.INVISIBLE);
                            blinkText.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    private void calculateTotalLoanAmount(){
        int totalLoanTaken=0;
        for(int i=1; i<=11; i++){
            if(pref.getBoolean("loan"+i, false)){
                totalLoanTaken+=Integer.parseInt(instalmentOverviewList.get(i-1).getInstalmentAmount());
            }
        }
        loanTaken.setText(""+totalLoanTaken);
    }

    private int getTotalLoanAount(){
        int totalLoanTaken=0;
        for(int i=1; i<=11; i++){
            if(pref.getBoolean("loan"+i, false)){
                totalLoanTaken+=Integer.parseInt(instalmentOverviewList.get(i-1).getInstalmentAmount());
            }
        }
        return totalLoanTaken;
    }

    private int getFinalLoanTakenInstalment(){
        for(int i=10; i>=0; i--){
            if(instalmentLoanTaken[i]){
                return i;
            }
        }
        return -1;
    }
}
