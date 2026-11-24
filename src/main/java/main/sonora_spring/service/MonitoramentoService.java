package main.sonora_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import main.sonora_spring.dto.ResultadoHealthCheck;
import main.sonora_spring.healthcheck.HealthCheck;
import main.sonora_spring.repository.MonitoramentoRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoramentoService {

    private static final Logger logger =
            LoggerFactory.getLogger(MonitoramentoService.class);

    private final List<HealthCheck> healthChecks;
    private final MonitoramentoRepository repository;

    public MonitoramentoService(
            List<HealthCheck> healthChecks,
            MonitoramentoRepository repository
    ) {
        this.healthChecks = healthChecks;
        this.repository = repository;
    }

    public List<ResultadoHealthCheck> executarMonitoramento() {

        List<ResultadoHealthCheck> resultados = new ArrayList<>();

        // Executa todos os Health Checks
        for (HealthCheck healthCheck : healthChecks) {
            ResultadoHealthCheck resultado = healthCheck.verificar();
            resultados.add(resultado);
        }

        // Persiste os resultados
        for (ResultadoHealthCheck resultado : resultados) {
            try {
                repository.salvar(resultado);

                logger.info(
                        "Monitoramento salvo: {} - {}",
                        resultado.servico(),
                        resultado.status()
                );

            } catch (DataAccessException e) {

                logger.error(
                        "Erro ao salvar monitoramento do serviço {}: {}",
                        resultado.servico(),
                        e.getMessage()
                );
            }
        }

        return resultados;
    }

    public List<ResultadoHealthCheck> buscarAtual() {
        return repository.buscarAtual();
    }

    public List<ResultadoHealthCheck> buscarHistorico(
            String servico,
            LocalDateTime inicio,
            LocalDateTime fim,
            Integer limite
    ) {

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data inicial não pode ser posterior à final"
            );
        }

        if (limite == null) {
            limite = 50;
        }

        if (limite < 1 || limite > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O limite deve estar entre 1 e 500"
            );
        }

        if (servico != null) {
            servico = servico.trim().toUpperCase();
        }

        return repository.buscarHistorico(
                servico,
                inicio,
                fim,
                limite
        );
    }
}
