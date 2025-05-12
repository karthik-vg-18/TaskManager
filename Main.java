package main;

import static spark.Spark.*;
import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;

import routes.AuthRoutes;
import routes.TaskRoutes;

public class Main {
    public static void main(String[] args) {
        port(8080); // Set backend port to 8080

        staticFiles.location("/public");
        
        // Enable CORS for all origins and headers
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Authorization,Content-Type");
        });

        // Respond to pre-flight requests
        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });

        // Initialize routes
        new AuthRoutes();
        new TaskRoutes();

        System.out.println("Server started at http://localhost:8080");
    }
}
