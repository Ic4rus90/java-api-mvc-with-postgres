package com.booleanuk.api.salary;

public class Salary {
    private int id;
    private String grade;
    private int minSalary;
    private int maxSalary;

    public Salary(int id, String grade, int minSalary, int maxSalary){
        this.id = id;
        this.grade = grade;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getID(){
        return this.id;
    }

    public String getGrade(){
        return this.grade;
    }

    public int getMinSalary(){
        return this.minSalary;
    }

    public int getMaxSalary(){
        return this.maxSalary;
    }
}
