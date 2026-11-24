package edu.auk.sdt213;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class RestTaskServer {
    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskService taskService = new TaskService();


        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/tasks", new TasksHandler(taskService));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("REST server started on http://localhost:" + PORT + "/api/tasks");
    }

    static class TasksHandler implements HttpHandler {
        private final TaskService taskService;

        TasksHandler(TaskService taskService) {
            this.taskService = taskService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equals(method) && "/api/tasks".equals(path)) {

                    sendJson(exchange, 200, taskService.toJsonArray());
                } else if ("GET".equals(method) && path.matches("/api/tasks/\\d+")) {

                    int id = extractId(path);
                    Task task = taskService.getTaskById(id);
                    if (task != null) {
                        sendJson(exchange, 200, task.toJson());
                    } else {
                        sendJson(exchange, 404, "{\"error\":\"Task not found\"}");
                    }
                } else if ("POST".equals(method) && "/api/tasks".equals(path)) {

                    String body = readBody(exchange.getRequestBody()).trim();
                    String title = parseTitle(body);
                    if (title.isEmpty()) {
                        sendJson(exchange, 400, "{\"error\":\"Title is required\"}");
                        return;
                    }
                    Task created = taskService.createTask(title);
                    sendJson(exchange, 201, created.toJson());
                } else if ("DELETE".equals(method) && path.matches("/api/tasks/\\d+")) {

                    int id = extractId(path);
                    if (taskService.deleteTask(id)) {
                        sendJson(exchange, 200, "{\"deleted\":true,\"id\":" + id + "}");
                    } else {
                        sendJson(exchange, 404, "{\"error\":\"Task not found\"}");
                    }
                } else {
                    sendJson(exchange, 405, "{\"error\":\"Method or path not allowed\"}");
                }
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, "{\"error\":\"Invalid task ID\"}");
            }
        }

        private int extractId(String path) {
            String[] parts = path.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        }

        private String readBody(InputStream is) throws IOException {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Простой парсер заголовка задачи из тела запроса вида {"title":"..."} или просто строки
        private String parseTitle(String body) {
            if (body.startsWith("{") && body.contains("\"title\"")) {
                int idx = body.indexOf("\"title\"");
                int colon = body.indexOf(":", idx);
                int firstQuote = body.indexOf("\"", colon);
                int secondQuote = body.indexOf("\"", firstQuote + 1);
                if (firstQuote != -1 && secondQuote != -1) {
                    return body.substring(firstQuote + 1, secondQuote).trim();
                }
            }
            return body.replace("\"", "").trim();
        }

        private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}