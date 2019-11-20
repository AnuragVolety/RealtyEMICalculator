package com.example.emiandroid.classes;

import android.widget.Switch;

public class InstalmentOverview {

    String instalmentNo, instalmentStartMonth, instalmentWeightage, instalmentAmount;
    Boolean isLoanTaken;


    public InstalmentOverview(){

    }

    public InstalmentOverview(String instalmentNo, String instalmentStartMonth,
                              String instalmentWeightage, String instalmentAmount,
                              Boolean isLoanTaken) {
        this.instalmentNo = instalmentNo;
        this.instalmentStartMonth = instalmentStartMonth;
        this.instalmentWeightage = instalmentWeightage;
        this.instalmentAmount = instalmentAmount;
        this.isLoanTaken = isLoanTaken;
    }


    public String getInstalmentNo() {
        return instalmentNo;
    }

    public void setInstalmentNo(String instalmentNo) {
        instalmentNo = instalmentNo;
    }

    public String getInstalmentStartMonth() {
        return instalmentStartMonth;
    }

    public void setInstalmentStartMonth(String instalmentStartMonth) {
        this.instalmentStartMonth = instalmentStartMonth;
    }

    public String getInstalmentWeightage() {
        return instalmentWeightage;
    }

    public void setInstalmentWeightage(String instalmentWeightage) {
        this.instalmentWeightage = instalmentWeightage;
    }

    public String getInstalmentAmount() {
        return instalmentAmount;
    }

    public void setInstalmentAmount(String instalmentAmount) {
        this.instalmentAmount = instalmentAmount;
    }

    public Boolean getIsLoanTaken() {
        return isLoanTaken;
    }

    public void setIsLoanTaken(Boolean isLoanTaken) {
        this.isLoanTaken = isLoanTaken;
    }
}
