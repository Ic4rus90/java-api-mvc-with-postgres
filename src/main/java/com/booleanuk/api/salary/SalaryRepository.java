package com.booleanuk.api.salary;

import com.booleanuk.api.database.DatabaseConnection;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryRepository {
    Connection connection;

    public SalaryRepository() throws SQLException {
        this.connection = new DatabaseConnection().getConnection();
    }

    public List<Salary> getAll() throws SQLException {
        List<Salary> allSalaries = new ArrayList<>();
        try (PreparedStatement statement = this.connection.prepareStatement("SELECT id, grade, min_salary, max_salary FROM salaries")){

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Salary salary = new Salary(results.getInt("id"),
                    results.getString("grade"),
                    results.getInt("min_salary"),
                    results.getInt("max_salary"));
            allSalaries.add(salary);
        }
        return allSalaries;

        } catch (SQLException e) {
            String exceptionMessage = "An error occurred when getting all salaries: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Salary get(int id) throws SQLException, ResponseStatusException {
        try (PreparedStatement statement = this.connection.prepareStatement("SELECT id, grade, min_salary, max_salary FROM salaries WHERE id = ?")) {

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
        } catch (SQLException e){
            String exceptionMessage = "An error occurred when getting a salary by id: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Salary update(int id, Salary salary) throws SQLException {
        String sqlStatement = "UPDATE salaries " +
                "SET grade = ? ," +
                "min_salary = ? ," +
                "max_salary = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sqlStatement)) {
            statement.setString(1, salary.getGrade());
            statement.setInt(2, salary.getMinSalary());
            statement.setInt(3, salary.getMaxSalary());
            statement.setInt(4, id);
            statement.executeUpdate();

            return this.get(id);

        } catch (SQLException e){
            String exceptionMessage = "An error occurred when updating salary: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Salary delete(int id) throws SQLException {
        String sqlStatement = "DELETE FROM salaries WHERE id = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sqlStatement)){
            statement.setInt(1, id);

            Salary deletedSalary = this.get(id);

            statement.executeUpdate();

            return deletedSalary;

        } catch (SQLException e) {
            String exceptionMessage = "An error occurred when deleting a salary: " + e.getMessage();
            throw new SQLException(exceptionMessage, e);
        }
    }

    public Salary add(Salary salary) throws SQLException {
        String sqlStatement = "INSERT INTO salaries(grade, min_salary, max_salary) VALUES(?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, salary.getGrade());
            statement.setInt(2, salary.getMinSalary());
            statement.setInt(3, salary.getMaxSalary());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                String exceptionMessage = "Inserting salary failed, no rows affected";
                throw new SQLException(exceptionMessage);
            }

            int newID = getGeneratedKeys(statement);
            salary.setId(newID);

            return salary;
        } catch (SQLException e) {
            String exceptionMessage = "An error occurred when attempting to insert salary into database: " + e.getMessage();
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
