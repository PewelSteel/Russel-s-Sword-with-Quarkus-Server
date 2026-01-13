package org.example.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080";

    public static int login(String email, String password) {
        return send(email, password, "/auth/login");
    }

    public static int register(String email, String password) {
        return send(email, password, "/auth/register");
    }

    private static int send(String email, String password, String path) {
        try {
            String json = """
                {
                  "email": "%s",
                  "password": "%s"
                }
            """.formatted(email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return -1;

            return Integer.parseInt(
                    response.body().replaceAll("\\D+", "")
            );

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
