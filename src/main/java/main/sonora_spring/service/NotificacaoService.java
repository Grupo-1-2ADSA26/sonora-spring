package main.sonora_spring.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import main.sonora_spring.dto.NotificacaoRequestDTO;

import java.util.Properties;

@Service
public class NotificacaoService {

    private final String usuario;
    private final Session session;

    public NotificacaoService(
            @Value("${mail.username}") String usuario,
            @Value("${mail.password}") String senha,
            @Value("${mail.host}") String host,
            @Value("${mail.port}") String porta
    ) {
        this.usuario = usuario;

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", porta);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        this.session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(usuario, senha);
                    }
                }
        );
    }

    public void enviarNotificacao(NotificacaoRequestDTO notificacao)
            throws MessagingException {

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(usuario));

        InternetAddress[] destinatarios =
                new InternetAddress[notificacao.destinatarios().size()];

        for (int i = 0; i < destinatarios.length; i++) {
            destinatarios[i] = new InternetAddress(
                    notificacao.destinatarios().get(i),
                    true
            );
        }

        email.setRecipients(
                Message.RecipientType.BCC,
                destinatarios
        );

        email.setSubject(notificacao.assunto(), "UTF-8");
        email.setText(notificacao.mensagem(), "UTF-8");

        Transport.send(email);
    }
}
