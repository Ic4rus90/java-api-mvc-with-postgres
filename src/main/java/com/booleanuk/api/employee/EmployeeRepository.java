package com.booleanuk.api.employee;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository {
    DataSource dataSource;
    String dbUser;
    String dbURL;
    String dbPassword;
    String dbDatabase;
    Connection connection;

    public EmployeeRepository() throws SQLException {
        this.getDatabaseCredentials();
        this.dataSource = this.createDataSource();
        this.connection = this.dataSource.getConnection();
    }

    public void getDatabaseCredentials() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        }   catch (Exception e) {
            System.out.println("An error occured: " + e);
        }
    }

    private DataSource createDataSource() {
        final String url = "jdbc:postgresql://" + this.dbURL + ":5432/" + this.dbDatabase + "?user=" + this.dbUser + "&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }

    public void connectToDatabase() throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM employees");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            String id = "" + results.getLong("id");
            String name = results.getString("name");
            String jobName = results.getString("job_name");
            String salaryGrade = results.getString("salary_grade");
            String department = results.getString("department");
            System.out.println(id + " - " + name + " - " + jobName + " - " + salaryGrade + " - " + department);
        }
    }

    public List<Employee> getAll() throws SQLException {
        List<Employee> allEmployees = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM employees");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Employee employee = new Employee(results.getLong("id"),
                    results.getString("name"),
                    results.getString("job_name"),
                    results.getString("salary_grade"),
                    results.getString("department"));
                    allEmployees.add(employee);
        }
        return allEmployees;
    }

    public Employee get(long id) throws SQLException, ResponseStatusException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM employees WHERE id = ?");
        statement.setLong(1, id);
        ResultSet results = statement.executeQuery();
        Employee employee = null;
        if (results.next()){
            employee = new Employee(results.getLong("id"),
                    results.getString("name"),
                    results.getString("job_name"),
                    results.getString("salary_grade"),
                    results.getString("department"));
        }

        if (employee == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No employees with that id were found");
        }
        return employee;
    }

    public Employee update(long id, Employee employee) throws SQLException {
        String SQL = "UPDATE employees " +
                "SET name = ? ," +
                "job_name = ? ," +
                "salary_grade = ? ," +
                "department = ? " +
                "WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        statement.setLong(5, id);
        int rowsAffected = statement.executeUpdate();
        Employee updatedEmployee = this.get(id);
        /*
        if (rowsAffected > 0) {
            updatedEmployee = this.get(id);
        }
        */

        return updatedEmployee;
    }

    public Employee delete(long id) throws SQLException {
        String SQL = "DELETE FROM employees WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        Employee deletedEmployee = null;
        deletedEmployee = this.get(id);

        statement.setLong(1, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            deletedEmployee = null;
        }
        return deletedEmployee;
    }

    public Employee add(Employee employee) throws SQLException {
        String SQL = "INSERT INTO employees(name, job_name, salary_grade, department) VALUES(?, ?, ?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());

        int rowsAffected = statement.executeUpdate();
        long newID = 0;
        if (rowsAffected > 0) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    newID = rs.getLong(1);
                }
            } catch (Exception e) {
                System.out.println("An error occured: " + e);
            }
            employee.setID(newID);
        } else {
            System.out.println("Something strange happened");
        }
        return employee;
    }
}
