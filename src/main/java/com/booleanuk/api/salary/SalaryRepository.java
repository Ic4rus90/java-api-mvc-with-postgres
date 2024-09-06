package com.booleanuk.api.salary;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SalaryRepository {
    DataSource dataSource;
    String dbUser;
    String dbURL;
    String dbPassword;
    String dbDatabase;
    Connection connection;

    public SalaryRepository() throws SQLException {
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
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM salaries");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            String id = "" + results.getInt("id");
            String grade = results.getString("grade");
            String minSalary = results.getString("min_salary");
            String maxSalary = results.getString("max_salary");
            System.out.println(id + " - " + grade + " - " + minSalary + " - " + maxSalary);
        }
    }

    public List<Salary> getAll() throws SQLException {
        List<Salary> allSalaries = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM salaries");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Salary salary = new Salary(results.getInt("id"),
                    results.getString("grade"),
                    results.getInt("min_salary"),
                    results.getInt("max_salary"));
            allSalaries.add(salary);
        }
        return allSalaries;
    }

    public Salary get(int id) throws SQLException, ResponseStatusException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM salaries WHERE id = ?");
        statement.setInt(1, id);
        ResultSet results = statement.executeQuery();
        Salary salary = null;
        if (results.next()){
            salary = new Salary(results.getInt("id"),
                    results.getString("grade"),
                    results.getInt("min_salary"),
                    results.getInt("max_salary"));
        }

        if (salary == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No salaries with that id were found");
        }
        return salary;
    }

    public Salary update(int id, Salary salary) throws SQLException {
        String SQL = "UPDATE salaries " +
                "SET grade = ? ," +
                "min_salary = ? ," +
                "max_salary = ? " +
                "WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, salary.getGrade());
        statement.setInt(2, salary.getMinSalary());
        statement.setInt(3, salary.getMaxSalary());
        statement.setInt(4, id);
        int rowsAffected = statement.executeUpdate();
        Salary updatedSalary = this.get(id);

        return updatedSalary;
    }

    public Salary delete(int id) throws SQLException {
        String SQL = "DELETE FROM salaries WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        Salary deletedSalary = null;
        deletedSalary = this.get(id);

        statement.setInt(1, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            deletedSalary = null;
        }
        return deletedSalary;
    }

    public Salary add(Salary salary) throws SQLException {
        String SQL = "INSERT INTO salaries(grade, min_salary, max_salary) VALUES(?, ?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, salary.getGrade());
        statement.setInt(2, salary.getMinSalary());
        statement.setInt(3, salary.getMaxSalary());

        int rowsAffected = statement.executeUpdate();
        int newID = 0;
        if (rowsAffected > 0) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    newID = rs.getInt(1);
                }
            } catch (Exception e) {
                System.out.println("An error occured: " + e);
            }
            salary.setID(newID);
        } else {
            System.out.println("Something strange happened");
        }
        return salary;
    }
}
