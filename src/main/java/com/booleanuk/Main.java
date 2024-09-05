package com.booleanuk;
import com.booleanuk.api.employee.Employee;
import com.booleanuk.api.employee.EmployeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Main.class, args);
        /*
        try {
            repository.connectToDatabase();


        } catch (Exception e) {
            System.out.println("An error occured: " + e);
        }
        try {
            for (Employee e: repository.getAll())
                System.out.println(e);
        } catch (Exception e) {
            System.out.println("An error occured: " + e);
        }
        */

    }


}
