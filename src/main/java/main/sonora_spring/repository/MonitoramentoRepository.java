package main.sonora_spring.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import main.sonora_spring.dto.ResultadoHealthCheck;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MonitoramentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public MonitoramentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int salvar(ResultadoHealthCheck resultado) {

        String sql = """
                INSERT INTO historico_monitoramento
                (servico, status, tempo_resposta_ms, data_hora)
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                resultado.servico(),
                resultado.status(),
                resultado.tempoRespostaMs(),
                Timestamp.valueOf(resultado.dataHora())
        );
    }

    public List<ResultadoHealthCheck> buscarAtual() {

        String sql = """
                SELECT servico, status, tempo_resposta_ms, data_hora
                FROM (
                    SELECT
                        servico,
                        status,
                        tempo_resposta_ms,
                        data_hora,
                        ROW_NUMBER() OVER (
                            PARTITION BY servico
                            ORDER BY data_hora DESC, id DESC
                        ) AS posicao
                    FROM historico_monitoramento
                ) AS registros
                WHERE posicao = 1
                ORDER BY servico
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new ResultadoHealthCheck(
                        rs.getString("servico"),
                        rs.getString("status"),
                        rs.getLong("tempo_resposta_ms"),
                        rs.getTimestamp("data_hora").toLocalDateTime()
                )
        );
    }

    public List<ResultadoHealthCheck> buscarHistorico(
            String servico,
            LocalDateTime inicio,
            LocalDateTime fim,
            Integer limite
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT servico, status, tempo_resposta_ms, data_hora
                FROM historico_monitoramento
                WHERE 1 = 1
                """);

        List<Object> parametros = new ArrayList<>();

        if (servico != null && !servico.isBlank()) {
            sql.append(" AND servico = ?");
            parametros.add(servico);
        }

        if (inicio != null) {
            sql.append(" AND data_hora >= ?");
            parametros.add(Timestamp.valueOf(inicio));
        }

        if (fim != null) {
            sql.append(" AND data_hora <= ?");
            parametros.add(Timestamp.valueOf(fim));
        }

        sql.append(" ORDER BY data_hora DESC, id DESC LIMIT ?");
        parametros.add(limite);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new ResultadoHealthCheck(
                        rs.getString("servico"),
                        rs.getString("status"),
                        rs.getLong("tempo_resposta_ms"),
                        rs.getTimestamp("data_hora").toLocalDateTime()
                ),
                parametros.toArray()
        );
    }
}
