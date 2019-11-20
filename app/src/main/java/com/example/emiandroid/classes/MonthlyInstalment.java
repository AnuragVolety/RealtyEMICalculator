package com.example.emiandroid.classes;

public class MonthlyInstalment {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonthNo() {
        return monthNo;
    }

    public void setMonthNo(String monthNo) {
        this.monthNo = monthNo;
    }

    public String getMonthlyInstalment() {
        return monthlyInstalment;
    }

    public void setMonthlyInstalment(String monthlyInstalment) {
        this.monthlyInstalment = monthlyInstalment;
    }

    private String date, monthNo, monthlyInstalment;

    public MonthlyInstalment(){

    }

    public MonthlyInstalment(String date, String monthNo, String monthlyInstalment) {
        this.date = date;
        this.monthNo = monthNo;
        this.monthlyInstalment = monthlyInstalment;
    }


}
