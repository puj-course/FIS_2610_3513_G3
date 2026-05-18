package com.example.entregaya.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TwilioSmsService - Tests unitarios")
class TwilioSmsServiceTest {

    private TwilioSmsService service;

    @BeforeEach
    void setUp() {
        service = new TwilioSmsService();
        ReflectionTestUtils.setField(service, "accountSid", "ACtest000000000000000000000000000");
        ReflectionTestUtils.setField(service, "authToken", "test_auth_token_123456789");
        ReflectionTestUtils.setField(service, "fromNumber", "+10000000001");
    }

    // CP01 - NORMAL: SMS enviado correctamente -> no lanza excepcion
    @Test
    @DisplayName("CP01: enviarSms con respuesta exitosa de Twilio no lanza excepcion")
    void CP01_enviarSms_Exitoso_NoLanzaExcepcion() {
        try (MockedStatic<Twilio> twilioMock = Mockito.mockStatic(Twilio.class);
             MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {

            MessageCreator creatorMock = mock(MessageCreator.class);
            Message messageMock2 = mock(Message.class);

            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(creatorMock);
            when(creatorMock.create()).thenReturn(messageMock2);
            when(messageMock2.getSid()).thenReturn("SMtest12345");
            when(messageMock2.getStatus()).thenReturn(Message.Status.QUEUED);

            assertDoesNotThrow(() -> service.enviarSms("+573001234567", "Hola desde EntregaYa!"));
            verify(creatorMock, times(1)).create();
        }
    }

    // CP02 - ERROR: Twilio ApiException -> captura excepcion, no propaga
    @Test
    @DisplayName("CP02: enviarSms con ApiException de Twilio no propaga la excepcion")
    void CP02_enviarSms_ApiException_NoPropagaExcepcion() {
        try (MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenThrow(new ApiException("Invalid phone number", 21211));

            assertDoesNotThrow(() -> service.enviarSms("+00000000000", "Mensaje fallido"));
        }
    }

    // CP03 - ERROR: excepcion generica de red -> captura excepcion, no propaga
    @Test
    @DisplayName("CP03: enviarSms con excepcion generica no la propaga")
    void CP03_enviarSms_ExcepcionGenerica_NoPropagaExcepcion() {
        try (MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenThrow(new RuntimeException("Network error"));

            assertDoesNotThrow(() -> service.enviarSms("+573009999999", "Mensaje red caida"));
        }
    }

    // CP04 - NORMAL: init() llama a Twilio.init con las credenciales configuradas
    @Test
    @DisplayName("CP04: init() inicializa Twilio con accountSid y authToken correctos")
    void CP04_init_InicializaTwilioConCredenciales() {
        try (MockedStatic<Twilio> twilioMock = Mockito.mockStatic(Twilio.class)) {
            assertDoesNotThrow(() -> service.init());
            twilioMock.verify(() ->
                    Twilio.init("ACtest000000000000000000000000000", "test_auth_token_123456789"),
                    times(1));
        }
    }

    // CP05 - BORDE: numero null -> ApiException capturada, no propaga
    @Test
    @DisplayName("CP05: enviarSms con numero null no propaga excepcion")
    void CP05_enviarSms_NumeroNull_NoPropagaExcepcion() {
        try (MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(any(), any(PhoneNumber.class), anyString()))
                    .thenThrow(new ApiException("Null phone number", 21211));

            assertDoesNotThrow(() -> service.enviarSms(null, "Mensaje sin destino"));
        }
    }

    // CP06 - BORDE: mensaje con caracteres especiales -> no lanza excepcion
    @Test
    @DisplayName("CP06: enviarSms con texto que contiene caracteres especiales funciona correctamente")
    void CP06_enviarSms_TextoConCaracteresEspeciales_NoLanzaExcepcion() {
        try (MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {
            MessageCreator creatorMock = mock(MessageCreator.class);
            Message msgMock = mock(Message.class);

            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(creatorMock);
            when(creatorMock.create()).thenReturn(msgMock);
            when(msgMock.getSid()).thenReturn("SMspecial");
            when(msgMock.getStatus()).thenReturn(Message.Status.QUEUED);

            assertDoesNotThrow(() ->
                    service.enviarSms("+573001234567", "La tarea \"Entrega Final\" vence mañana ✓"));
        }
    }
}
