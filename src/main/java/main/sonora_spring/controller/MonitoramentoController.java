package main.sonora_spring.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import main.sonora_spring.dto.ResultadoHealthCheck;
import main.sonora_spring.service.MonitoramentoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoramento")
public class MonitoramentoController {

    private final MonitoramentoService monitoramentoService;

    public MonitoramentoController(
            MonitoramentoService monitoramentoService
    ) {
        this.monitoramentoService = monitoramentoService;
    }

    @PostMapping("/executar")
    public ResponseEntity<List<ResultadoHealthCheck>> executarMonitoramento() {

        return ResponseEntity.ok(
                monitoramentoService.executarMonitoramento()
        );
    }

    @GetMapping("/atual")
    public ResponseEntity<List<ResultadoHealthCheck>> buscarAtual() {

        return ResponseEntity.ok(
                monitoramentoService.buscarAtual()
        );
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ResultadoHealthCheck>> buscarHistorico(

            @RequestParam(required = false) String servico,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fim,

            @RequestParam(defaultValue = "50") Integer limite
    ) {

        return ResponseEntity.ok(
                monitoramentoService.buscarHistorico(
                        servico,
                        inicio,
                        fim,
                        limite
                )
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> tratarErroBanco(
            DataAccessException e
    ) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "mensagem",
                        "Histórico de monitoramento indisponível"
                ));
    }
}
