package com.booleanuk.api.departments;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DepartmentRepository {
    DataSource dataSource;
    String dbUser;
    String dbURL;
    String dbPassword;
    String dbDatabase;
    Connection connection;

    public DepartmentRepository() throws SQLException {
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

    public List<Department> getAll() throws SQLException {
        List<Department> allDepartments = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM departments");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Department department = new Department(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("location"));
            allDepartments.add(department);
        }
        return allDepartments;
    }

    public Department get(int id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM departments WHERE id = ?");
        statement.setInt(1, id);
        ResultSet results = statement.executeQuery();
        Department department = null;

        if (results.next()){
            department = new Department(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("location"));
        }
        if (department == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No departments with that id were found.");
        }

        return department;
    }

    public Department add(Department department) throws SQLException {
        String SQL = "INSERT INTO departments(name, location) VALUES (?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, department.getName());
        statement.setString(2, department.getLocation());

        int rowsAffected = statement.executeUpdate();
        int newID = 0;

        if (rowsAffected > 0){
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    newID = rs.getInt(1);
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e);
            }
            department.setId(newID);
        } else {
            System.out.println("Something strange happened");
        }
        return department;
    }

    public Department update(int id, Department department) throws SQLException {
        String SQL = "UPDATE departments " +
                "SET name = ? ," +
                "location = ? " +
                "WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, department.getName());
        statement.setString(2, department.getLocation());
        statement.setInt(3, id);

        statement.executeUpdate();

        return this.get(id);
    }

    public Department delete(int id) throws SQLException {
        String SQL = "DELETE FROM departments WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.setInt(1, id);

        Department deletedDepartment = this.get(id);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            deletedDepartment = null;
        }
        return deletedDepartment;
    }
}
