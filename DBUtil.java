package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Task;

public class DBUtil {

    // Establishes a connection to the database
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/taskmanager";
        String username = "root";
        String password = "root";

        // Register JDBC driver (if not already registered by the system)
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(url, username, password);
    }

    // Converts ResultSet to a List of Task objects
    public static List<Task> convertToTaskList(ResultSet rs) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        while (rs.next()) {
            // Extract task data from the ResultSet
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String assignee = rs.getString("assignee");
            String status = rs.getString("status");

            // Create Task object and add it to the list
            Task task = new Task(id, title, description, assignee, status);
            tasks.add(task);
        }

        return tasks;
    }
}
