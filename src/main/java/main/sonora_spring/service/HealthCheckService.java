package main.sonora_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class HealthCheckService {

    private final HttpClient httpClient;
    private final String nodeHealthUrl;

    public HealthCheckService(
            @Value("${sonora.node.health-url}") String nodeHealthUrl
    ) {
        this.nodeHealthUrl = nodeHealthUrl;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public boolean verificarNode() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(nodeHealthUrl))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            return response.statusCode() == 200;

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return false;
        }
    }
}