package com.booleanuk.api.employee;

public class Employee {
    private long id;
    private String name;
    private String jobName;
    private String salaryGrade;
    private String department;

    public Employee(long id, String name, String jobName, String salaryGrade, String department){
        this.id = id;
        this.name = name;
        this.jobName = jobName;
        this.salaryGrade = salaryGrade;
        this.department = department;
    }

    public String toString() {
        String result = "";
        result += this.id + " - ";
        result += this.name + " - ";
        result += this.jobName;
        return result;
    }

    public long getID(){
        return this.id;
    }

    public void setID(long id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public String getJobName(){
        return this.jobName;
    }

    public String getSalaryGrade(){
        return this.salaryGrade;
    }

    public String getDepartment(){
        return this.department;
    }
}
