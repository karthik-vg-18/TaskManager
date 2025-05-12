package routes;

import static spark.Spark.*;

import com.google.gson.Gson;
import models.Task;
import utils.DBUtil;
import utils.JWTUtil;
import io.jsonwebtoken.Claims;

import java.sql.*;
import java.util.List;

public class TaskRoutes {
    private Gson gson = new Gson();

    public TaskRoutes() {
        // Route for creating a new task (only accessible by managers)
        post("/tasks", (req, res) -> {
            res.type("application/json");

            String token = req.headers("Authorization"); // Extract token from the header

            // Check if token is present
            if (token == null) {
                res.status(401);
                return gson.toJson(new Response("Unauthorized: Missing token"));
            }

            try {
                // Verify the JWT token
                Claims claims = JWTUtil.verifyToken(token);

                // Extract role and email from the claims
                String role = claims.get("role", String.class);
                String email = claims.getSubject();  // This is the user's email

                // If role is not "MANAGER", return a forbidden response
                if (!role.equals("MANAGER")) {
                    res.status(403);
                    return gson.toJson(new Response("Forbidden: Only managers can create tasks"));
                }

                // Parse the task from the request body
                Task task = gson.fromJson(req.body(), Task.class);

                // Insert the task into the database
                try (Connection conn = DBUtil.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO tasks (title, description, assignee, status) VALUES (?, ?, ?, ?)"
                    );
                    stmt.setString(1, task.getTitle());
                    stmt.setString(2, task.getDescription());
                    stmt.setString(3, task.getAssignee());
                    stmt.setString(4, "PENDING");  // New tasks are "PENDING"
                    stmt.executeUpdate();

                    return gson.toJson(new Response("Task created successfully"));
                } catch (Exception e) {
                    e.printStackTrace();
                    res.status(500);
                    return gson.toJson(new Response("Internal server error"));
                }

            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new Response("Unauthorized: Invalid or expired token"));
            }
        });

        // **Fetch tasks assigned to the logged-in user**
        get("/tasks", (req, res) -> {
            res.type("application/json");

            // Get JWT token from the request header
            String token = req.headers("Authorization");

            // Check if token is present
            if (token == null) {
                res.status(401);
                return gson.toJson(new Response("Unauthorized: Missing token"));
            }

            try {
                // Verify the JWT token
                Claims claims = JWTUtil.verifyToken(token);

                // Extract role and email from the claims
                String role = claims.get("role", String.class);
                String email = claims.getSubject();  // This is the logged-in user's email

                // Fetch tasks from the database depending on the role
                if (role.equals("MANAGER")) {
                    // Managers can view all tasks
                    try (Connection conn = DBUtil.getConnection()) {
                        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks");
                        ResultSet rs = stmt.executeQuery();

                        // Convert the ResultSet into a list of Task objects
                        List<Task> tasks = DBUtil.convertToTaskList(rs);
                        return gson.toJson(tasks);
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.status(500);
                        return gson.toJson(new Response("Internal server error"));
                    }
                } else if (role.equals("USER")) {
                    // Users can only view their own assigned tasks
                    try (Connection conn = DBUtil.getConnection()) {
                        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE assignee = ?");
                        stmt.setString(1, email);  // Get tasks assigned to the logged-in user
                        ResultSet rs = stmt.executeQuery();

                        // Convert the ResultSet into a list of Task objects
                        List<Task> tasks = DBUtil.convertToTaskList(rs);
                        return gson.toJson(tasks);
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.status(500);
                        return gson.toJson(new Response("Internal server error"));
                    }
                } else {
                    res.status(403);
                    return gson.toJson(new Response("Forbidden: Invalid role"));
                }

            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new Response("Unauthorized: Invalid or expired token"));
            }
        });

        // Additional routes for updating tasks, deleting tasks, etc., can go here
    }

    // Response class to structure JSON responses
    class Response {
        String message;

        Response(String message) {
            this.message = message;
        }
    }
}
