package routes;

import static spark.Spark.*;
import com.google.gson.Gson;
import utils.JWTUtil;

public class AuthRoutes {
    private Gson gson = new Gson();

    public AuthRoutes() {
        // Test route to verify Spark is working
        get("/", (req, res) -> "Spark is working ✅");

        // The signup route
        post("/signup", (req, res) -> {
            res.type("application/json");
            return gson.toJson(new Response("User created successfully"));
        });

        // The login route
        post("/login", (req, res) -> {
            // Parse the incoming JSON request body
            String body = req.body();  // Read the JSON body
            UserCredentials credentials = new Gson().fromJson(body, UserCredentials.class);  // Deserialize JSON

            String email = credentials.getEmail();
            String password = credentials.getPassword();

            // Validate credentials (hardcoded for now)
            if ("user@example.com".equals(email) && "password".equals(password)) {
                String token = JWTUtil.generateToken(email, "USER");
                res.status(200);
                return gson.toJson(new Response(token));  // Return token in JSON format
            } else {
                res.status(401);
                return gson.toJson(new Response("Invalid credentials"));
            }
        });
    }

    // Helper class to hold response messages
    class Response {
        String message;

        Response(String message) {
            this.message = message;
        }
    }

    // User credentials class for parsing JSON
    class UserCredentials {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
