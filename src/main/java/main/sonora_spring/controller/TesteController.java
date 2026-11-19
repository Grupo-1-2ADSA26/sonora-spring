package main.sonora_spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TesteController {

    @GetMapping("/api/teste")
    public Map<String, String> testarConexao() {
        return Map.of(
                "mensagem", "Conexão com Spring Boot realizada!"
        );
    }
}