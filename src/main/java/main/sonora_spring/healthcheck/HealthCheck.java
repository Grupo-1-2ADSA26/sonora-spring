package main.sonora_spring.healthcheck;

import main.sonora_spring.dto.ResultadoHealthCheck;

import java.time.LocalDateTime;

public abstract class HealthCheck {

    private final String nomeServico;
    private final Integer timeout;

    protected HealthCheck(String nomeServico, Integer timeout) {
        this.nomeServico = nomeServico;
        this.timeout = timeout;
    }

    public final ResultadoHealthCheck verificar() {

        long inicio = System.nanoTime();
        boolean disponivel = false;

        try {
            disponivel = executarVerificacao();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.err.println(
                    "Falha ao verificar " + nomeServico
                            + ": " + e.getMessage()
            );
        }

        long tempoRespostaMs =
                (System.nanoTime() - inicio) / 1_000_000;

        return new ResultadoHealthCheck(
                nomeServico,
                disponivel ? "UP" : "DOWN",
                tempoRespostaMs,
                LocalDateTime.now()
        );
    }

    protected abstract boolean executarVerificacao()
            throws Exception;

    public String getNomeServico() {
        return nomeServico;
    }

    public Integer getTimeout() {
        return timeout;
    }
}
