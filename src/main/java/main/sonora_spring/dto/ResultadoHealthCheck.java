package main.sonora_spring.dto;

import java.time.LocalDateTime;

public record ResultadoHealthCheck(
        String servico,
        String status,
        Long tempoRespostaMs,
        LocalDateTime dataHora
) {
}