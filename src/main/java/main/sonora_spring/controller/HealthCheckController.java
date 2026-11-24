package main.sonora_spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import main.sonora_spring.service.HealthCheckService;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping("/node")
    public ResponseEntity<Map<String, String>> verificarNode() {

        boolean disponivel = healthCheckService.verificarNode();

        if (disponivel) {
            return ResponseEntity.ok(
                    Map.of(
                            "servico", "sonora-web",
                            "status", "UP"
                    )
            );
        }

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "servico", "sonora-web",
                                "status", "DOWN"
                        )
                );
    }
}