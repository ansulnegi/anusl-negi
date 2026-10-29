import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.Executors;

/**
 * ============================================================================
 * PortfolioServer.java
 * ============================================================================
 * Author: Ansul (Full Stack Java Developer)
 * Degree: B.Tech Computer Science & Engineering
 *
 * PURPOSE:
 * A lightweight, production-quality, zero-dependency HTTP server written in
 * modern Java (Java 21). It serves the frontend portfolio files (HTML, CSS, JS)
 * and exposes RESTful API endpoints for system metrics, developer profiles,
 * and contact form message dispatching.
 *
 * HOW TO RUN:
 * 1. Open Terminal in this folder
 * 2. Compile: javac PortfolioServer.java
 * 3. Run:     java PortfolioServer
 * 4. Open in browser: http://localhost:8080
 *
 * INTERVIEW TALKING POINTS:
 * - Built on the JDK's built-in HttpServer (com.sun.net.httpserver).
 * - Multi-threaded concurrency with a fixed thread pool worker model.
 * - Clean RESTful architecture (GET /api/status, GET /api/profile, POST /api/contact).
 * - Proper MIME type resolution and CORS header handling.
 * ============================================================================
 */
public class PortfolioServer {

    // Default port for the HTTP server
    private static final int PORT = 8080;
    private static final long SERVER_START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        try {
            // 1. Create HTTP Server listening on all interfaces at PORT 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // 2. Assign a multi-threaded executor to handle requests concurrently
            // In an interview, explain how this prevents slow requests from blocking other clients
            server.setExecutor(Executors.newFixedThreadPool(10));

            // 3. Register REST API Endpoints
            server.createContext("/api/status", new StatusApiHandler());
            server.createContext("/api/profile", new ProfileApiHandler());
            server.createContext("/api/projects", new ProjectsApiHandler());
            server.createContext("/api/contact", new ContactApiHandler());

            // 4. Register Static File Handler (Root context "/")
            server.createContext("/", new StaticFileHandler());

            // 5. Start the Server
            server.start();

            printBanner();

        } catch (IOException e) {
            System.err.println("❌ Failed to start Portfolio Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prints an interview-ready startup banner in the console.
     */
    private static void printBanner() {
        System.out.println("=============================================================");
        System.out.println("   🚀 ANSUL's FULL STACK JAVA PORTFOLIO SERVER IS RUNNING!   ");
        System.out.println("=============================================================");
        System.out.println("  • Local URL:      http://localhost:" + PORT);
        System.out.println("  • Java Version:   " + System.getProperty("java.version"));
        System.out.println("  • OS Platform:    " + System.getProperty("os.name"));
        System.out.println("  • REST API:       http://localhost:" + PORT + "/api/status");
        System.out.println("  • REST Profile:   http://localhost:" + PORT + "/api/profile");
        System.out.println("=============================================================");
        System.out.println("  Ready for Technical Interviews! Press Ctrl+C to stop.");
        System.out.println();
    }

    // ========================================================================
    // HANDLER 1: Static File Server (HTML, CSS, JS, etc.)
    // ========================================================================
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uriPath = exchange.getRequestURI().getPath();

            // Default to index.html for root path "/"
            if (uriPath.equals("/") || uriPath.isEmpty()) {
                uriPath = "/index.html";
            }

            // Clean path to prevent directory traversal vulnerabilities (Security Best Practice)
            String safePath = uriPath.replace("..", "");
            Path filePath = Paths.get("." + safePath);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                sendResponse(exchange, 404, "text/plain", "404 Not Found: " + uriPath);
                return;
            }

            // Determine MIME type based on file extension
            String mimeType = getMimeType(filePath.toString());
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", mimeType);
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String getMimeType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".java")) return "text/plain; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".svg")) return "image/svg+xml";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }

    // ========================================================================
    // HANDLER 2: /api/status - Server Health & System Metrics
    // ========================================================================
    static class StatusApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            Runtime runtime = Runtime.getRuntime();
            long uptimeSeconds = (System.currentTimeMillis() - SERVER_START_TIME) / 1000;
            long freeMemoryMB = runtime.freeMemory() / (1024 * 1024);
            long totalMemoryMB = runtime.totalMemory() / (1024 * 1024);

            String jsonResponse = String.format("""
                {
                  "status": "UP",
                  "service": "Ansul Portfolio Java Backend",
                  "javaVersion": "%s",
                  "uptimeSeconds": %d,
                  "freeMemoryMB": %d,
                  "totalMemoryMB": %d,
                  "activeProcessors": %d,
                  "timestamp": "%s"
                }
                """,
                System.getProperty("java.version"),
                uptimeSeconds,
                freeMemoryMB,
                totalMemoryMB,
                runtime.availableProcessors(),
                Instant.now().toString()
            );

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    // ========================================================================
    // HANDLER 3: /api/profile - Developer Profile Data Endpoint
    // ========================================================================
    static class ProfileApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            String jsonResponse = """
                {
                  "developer": "Ansul",
                  "role": "Full Stack Java Developer",
                  "education": "B.Tech Computer Science & Engineering (2029)",
                  "skills": {
                    "backend": ["Java 21", "Spring Boot", "REST APIs", "Hibernate", "JDBC"],
                    "frontend": ["HTML5", "CSS3", "JavaScript", "Responsive Design"],
                    "database": ["MySQL", "PostgreSQL"],
                    "core": ["Data Structures & Algorithms", "OOP", "DBMS", "Computer Networks"]
                  },
                  "interviewReady": true,
                  "contactEmail": "ansul.dev@example.com"
                }
                """;

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    // ========================================================================
    // HANDLER 4: /api/projects - Dynamic Projects Endpoint
    // ========================================================================
    static class ProjectsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            String jsonResponse = """
                [
                  {
                    "id": 1,
                    "title": "Custom Java Portfolio HTTP Server & REST API",
                    "category": "Full Stack",
                    "tech": ["Java 21", "HttpServer", "REST API", "HTML5", "CSS3"],
                    "description": "Zero-dependency multithreaded server delivering static assets & REST endpoints."
                  },
                  {
                    "id": 2,
                    "title": "Enterprise E-Commerce RESTful Web Service",
                    "category": "Backend",
                    "tech": ["Spring Boot", "Spring Security", "JWT", "Hibernate", "MySQL"],
                    "description": "Secured shopping backend with role-based access control and transactional order processing."
                  },
                  {
                    "id": 3,
                    "title": "Smart Task & Workflow Management Portal",
                    "category": "Full Stack Web",
                    "tech": ["Java", "Spring Boot", "HTML5", "CSS3", "JavaScript", "MySQL"],
                    "description": "Interactive workflow dashboard with asynchronous DOM updates via Fetch API."
                  },
                  {
                    "id": 4,
                    "title": "Student Academic & Placement Record System",
                    "category": "Database & Web",
                    "tech": ["Java", "JDBC", "MySQL", "HTML5", "CSS3"],
                    "description": "Campus recruitment eligibility and database querying system with SQL injection safeguards."
                  }
                ]
                """;

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    // ========================================================================
    // HANDLER 5: /api/contact - Handles POST requests from Contact Form
    // ========================================================================
    static class ContactApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            // Handle CORS preflight request
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed. Use POST.\"}");
                return;
            }

            // Read JSON body sent from HTML/JS frontend
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Log incoming inquiry on the Java console (Demonstrates server-side logging)
            System.out.println("-------------------------------------------------------------");
            System.out.println("📬 [NEW CONTACT MESSAGE RECEIVED]");
            System.out.println("Time: " + Instant.now());
            System.out.println("Payload:\n" + body);
            System.out.println("-------------------------------------------------------------");

            String jsonResponse = String.format("""
                {
                  "success": true,
                  "message": "Inquiry successfully logged by Ansul's Java Server!",
                  "timestamp": "%s"
                }
                """,
                Instant.now().toString()
            );

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    // ========================================================================
    // UTILITY METHODS
    // ========================================================================
    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}

