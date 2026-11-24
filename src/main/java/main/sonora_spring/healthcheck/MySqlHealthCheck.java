package main.sonora_spring.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class MySqlHealthCheck extends HealthCheck {

    private final DataSource dataSource;

    public MySqlHealthCheck(
            DataSource dataSource,
            @Value("${sonora.mysql.timeout:5000}") Integer timeout
    ) {
        super("MYSQL", timeout);
        this.dataSource = dataSource;
    }

    @Override
    protected boolean executarVerificacao() throws Exception {

        try (
                Connection conexao = dataSource.getConnection();
                Statement statement = conexao.createStatement()
        ) {
            int timeoutSegundos =
                    Math.max(1, (getTimeout() + 999) / 1000);

            statement.setQueryTimeout(timeoutSegundos);

            try (var resultado = statement.executeQuery("SELECT 1")) {

                return resultado.next()
                        && resultado.getInt(1) == 1;
            }
        }
    }
}