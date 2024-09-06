package com.booleanuk.api.employee;
import com.booleanuk.api.database.DatabaseConnection;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EmployeeRepository {
    Connection connection;

    public EmployeeRepository() throws SQLException {
        this.connection = new DatabaseConnection().getConnection();
    }

    public List<Employee> getAll() throws SQLException {
        List<Employee> allEmployees = new ArrayList<>();

        try (PreparedStatement statement = this.connection.prepareStatement("SELECT id, name, job_name, salary_grade, department FROM employees")) {

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
        } catch (SQLException e) {
            String exceptionMessage = "An error occurred when getting all employees: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Employee get(long id) throws SQLException, ResponseStatusException {
        try (PreparedStatement statement = this.connection.prepareStatement("SELECT id, name, job_name, salary_grade, department FROM employees WHERE id = ?")) {
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
        } catch (SQLException e) {
            String exceptionMessage = "An error occurred when getting an employee by id: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Employee update(long id, Employee employee) throws SQLException {
        String sqlString = "UPDATE employees " +
                "SET name = ? ," +
                "job_name = ? ," +
                "salary_grade = ? ," +
                "department = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sqlString)) {
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getJobName());
            statement.setString(3, employee.getSalaryGrade());
            statement.setString(4, employee.getDepartment());
            statement.setLong(5, id);

            statement.executeUpdate();

            return this.get(id);
        } catch (SQLException e){
            String exceptionMessage = "An error occurred when updating an employee: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Employee delete(long id) throws SQLException {
        String sqlString = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sqlString)) {
            Employee deletedEmployee = this.get(id);

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                deletedEmployee = null;
            }
            return deletedEmployee;
        } catch (SQLException e) {
            String exceptionString = "An error occurred when deleting an employee: " + e.getMessage();
            throw new SQLException(exceptionString, e);
        }
    }

    public Employee add(Employee employee) throws SQLException {
        String sqlStatement = "INSERT INTO employees(name, job_name, salary_grade, department) VALUES(?, ?, ?, ?)";

        try (PreparedStatement statement = this.connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getJobName());
            statement.setString(3, employee.getSalaryGrade());
            statement.setString(4, employee.getDepartment());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                String exceptionMessage = "Employee creation failed, no rows affected.";
                throw new SQLException(exceptionMessage);
            }

            long newID = getGeneratedKeys(statement);
            employee.setId(newID);

            return employee;

        } catch (SQLException e){
            String exceptionMessage = "An error occurred while attempting to add a department to the database: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    private int getGeneratedKeys(Statement statement) throws SQLException {
        try (ResultSet rs = statement.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                String exceptionMessage = "An error occurred while getting the generated key: ";
                throw new SQLException(exceptionMessage);
            }
        }
    }
}
