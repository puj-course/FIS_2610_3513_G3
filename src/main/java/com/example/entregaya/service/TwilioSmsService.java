package com.example.entregaya.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones SMS utilizando Twilio.
 *
 * Reemplaza la integración anterior de Telegram. Envía mensajes SMS reales
 * al número de teléfono registrado en el perfil del usuario.
 *
 * Configuración requerida en application.properties:
 *   twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *   twilio.auth.token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *   twilio.from.number=+1XXXXXXXXXX
 */
@Service
public class TwilioSmsService {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.number}")
    private String fromNumber;

    /**
     * Inicializa el cliente Twilio una sola vez al arrancar el contexto de Spring.
     * Equivale al Twilio.init() que debe hacerse antes de cualquier llamada a la API.
     */
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("[TwilioSMS] Cliente Twilio inicializado correctamente (fromNumber={})", fromNumber);
    }

    /**
     * Envía un SMS al número de teléfono indicado.
     *
     * @param toNumber número destino en formato E.164, ej. +573001234567
     * @param texto    contenido del mensaje (máx. 160 chars para SMS simple)
     */
    public void enviarSms(String toNumber, String texto) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(fromNumber),
                    texto
            ).create();

            log.info("[TwilioSMS] SMS enviado a={} sid={} estado={}",
                    toNumber, message.getSid(), message.getStatus());

        } catch (ApiException e) {
            log.error("[TwilioSMS] Error de API Twilio al enviar a={}: código={} mensaje={}",
                    toNumber, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[TwilioSMS] Excepción inesperada al enviar SMS a={}: {}", toNumber, e.getMessage());
        }
    }
}
