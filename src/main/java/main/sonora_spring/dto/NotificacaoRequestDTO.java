package main.sonora_spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NotificacaoRequestDTO(

        @NotEmpty(message = "Informe pelo menos um destinatário")
        List<@NotBlank @Email String> destinatarios,

        @NotBlank(message = "O assunto é obrigatório")
        String assunto,

        @NotBlank(message = "A mensagem é obrigatória")
        String mensagem

) {
}
