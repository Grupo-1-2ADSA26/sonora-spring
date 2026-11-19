package main.sonora_spring.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import main.sonora_spring.dto.NotificacaoRequestDTO;
import main.sonora_spring.service.NotificacaoService;

import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(
            NotificacaoService notificacaoService
    ) {
        this.notificacaoService = notificacaoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> enviarNotificacao(
            @Valid @RequestBody NotificacaoRequestDTO notificacao
    ) {

        try {
            notificacaoService.enviarNotificacao(notificacao);

            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Notificação enviada ao servidor SMTP"
                    )
            );

        } catch (MessagingException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Erro ao enviar notificação"
                            )
                    );
        }
    }
}
