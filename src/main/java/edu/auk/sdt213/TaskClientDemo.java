package edu.auk.sdt213;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TaskClientDemo {
    private static final String BASE_URL = "http://localhost:8080/api/tasks";

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        System.out.println("1. GET all tasks:");
        HttpResponse<String> getAllResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Status: " + getAllResponse.statusCode());
        System.out.println("Body: " + getAllResponse.body() + "\n");

        System.out.println("2. POST create new task:");
        String newTaskJson = "{\"title\":\"Demonstrate REST client in Java\"}";
        HttpResponse<String> postResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(newTaskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Status: " + postResponse.statusCode());
        System.out.println("Body: " + postResponse.body() + "\n");

        System.out.println("3. GET task by ID = 1:");
        HttpResponse<String> getOneResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/1"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Status: " + getOneResponse.statusCode());
        System.out.println("Body: " + getOneResponse.body() + "\n");

        System.out.println("4. DELETE task by ID = 2:");
        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/2"))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Status: " + deleteResponse.statusCode());
        System.out.println("Body: " + deleteResponse.body() + "\n");

        System.out.println("5. GET all tasks after changes:");
        HttpResponse<String> finalListResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Status: " + finalListResponse.statusCode());
        System.out.println("Body: " + finalListResponse.body());
    }
}