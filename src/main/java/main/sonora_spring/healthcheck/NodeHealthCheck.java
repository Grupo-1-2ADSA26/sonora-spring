package main.sonora_spring.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class NodeHealthCheck extends HealthCheck {

    private final HttpClient httpClient;
    private final String url;

    public NodeHealthCheck(
            @Value("${sonora.node.health-url}") String url,
            @Value("${sonora.node.timeout:5000}") Integer timeout
    ) {
        super("NODE_JS", timeout);

        this.url = url;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofMillis(getTimeout())
                )
                .build();
    }

    @Override
    protected boolean executarVerificacao() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(getTimeout()))
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.discarding()
        );

        return response.statusCode() == 200;
    }
}
