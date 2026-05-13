package com.example.entregaya.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TelegramNotificacionService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificacionService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.api.url}")
    private String apiUrl;

    @SuppressWarnings("java:S2095")
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void enviarMensaje(String chatId, String texto) {
        try {
            String url = apiUrl + "/bot" + botToken + "/sendMessage";

            String body = String.format(
                    "{\"chat_id\":\"%s\",\"text\":\"%s\"}",
                    chatId,
                    texto.replace("\"", "\\\"")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                log.info("[Telegram] Mensaje enviado a chatId={}", chatId);
            } else {
                log.error("[Telegram] Error al enviar. Status={} Body={}",
                        response.statusCode(), response.body());
            }

        } catch (Exception e) {
            log.error("[Telegram] Excepción al enviar mensaje a chatId={}: {}", chatId, e.getMessage());
        }
    }
}