package com.example.emiandroid.classes;

public class Report {
    private String date, monthNo, monthlyInstalment, interest, principal, balance;

    public Report(){

    }

    public Report(String date, String monthNo, String monthlyInstalment, String interest, String principal, String balance){
        this.date = date;
        this.monthNo = monthNo;
        this.monthlyInstalment = monthlyInstalment;
        this.interest = interest;
        this.principal = principal;
        this.balance = balance;
    }

    public String getMonthNo() {
        return monthNo;
    }

    public void setMonthNo(String monthNo) {
        this.monthNo = monthNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonthlyInstalment() {
        return monthlyInstalment;
    }

    public void setMonthlyInstalment(String monthlyInstalment) {
        this.monthlyInstalment = monthlyInstalment;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
