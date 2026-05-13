package com.example.entregaya.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TelegramNotificacionService - Tests unitarios")
class TelegramNotificacionServiceTest {

    private TelegramNotificacionService service;
    private HttpClient mockHttpClient;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        service = new TelegramNotificacionService();
        ReflectionTestUtils.setField(service, "botToken", "test-token-123");
        ReflectionTestUtils.setField(service, "apiUrl", "https://api.telegram.org");

        mockHttpClient = mock(HttpClient.class);
        Field field = TelegramNotificacionService.class.getDeclaredField("httpClient");
        field.setAccessible(true);
        field.set(service, mockHttpClient);
    }

    // CP01 - NORMAL: respuesta 200 -> mensaje enviado correctamente
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP01: enviarMensaje con respuesta 200 no lanza excepcion")
    void CP01_enviarMensaje_Respuesta200_NoLanzaExcepcion() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        doReturn(mockResponse).when(mockHttpClient).send(any(HttpRequest.class), any());

        assertDoesNotThrow(() -> service.enviarMensaje("123", "Hola!"));
        verify(mockHttpClient, times(1)).send(any(), any());
    }

    // CP02 - ERROR: respuesta 400 -> log de error, sin excepcion
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP02: enviarMensaje con respuesta 400 no lanza excepcion")
    void CP02_enviarMensaje_Respuesta400_NoLanzaExcepcion() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"error\":\"bad request\"}");
        doReturn(mockResponse).when(mockHttpClient).send(any(HttpRequest.class), any());

        assertDoesNotThrow(() -> service.enviarMensaje("456", "Mensaje con error"));
    }

    // CP03 - EXCEPCION: fallo de red -> captura excepcion, no propaga
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP03: enviarMensaje con excepcion de red no la propaga")
    void CP03_enviarMensaje_ExcepcionDeRed_NoPropagaExcepcion() throws Exception {
        doThrow(new RuntimeException("Network error"))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        assertDoesNotThrow(() -> service.enviarMensaje("789", "Mensaje fallido"));
    }

    // CP04 - NORMAL: la URI del request incluye el token y sendMessage
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP04: enviarMensaje construye request con token y endpoint sendMessage")
    void CP04_enviarMensaje_ConstruyeRequestConTokenYEndpoint() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        doReturn(mockResponse).when(mockHttpClient).send(requestCaptor.capture(), any());

        service.enviarMensaje("CHAT123", "Texto de prueba");

        HttpRequest captured = requestCaptor.getValue();
        String uriStr = captured.uri().toString();
        assertTrue(uriStr.contains("test-token-123"), "URI debe contener el token");
        assertTrue(uriStr.contains("sendMessage"),    "URI debe contener sendMessage");
    }

    // CP05 - BORDE: texto con comillas dobles se escapa y no lanza excepcion
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP05: texto con comillas dobles no lanza excepcion")
    void CP05_enviarMensaje_TextoConComillas_NoLanzaExcepcion() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        doReturn(mockResponse).when(mockHttpClient).send(any(), any());

        assertDoesNotThrow(() -> service.enviarMensaje("999", "Dijo \"Hola\" hoy"));
    }

    // CP06 - BORDE: respuesta 500 -> se maneja sin excepcion
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("CP06: respuesta 500 del servidor es manejada sin excepcion")
    void CP06_enviarMensaje_Respuesta500_ManejaSinExcepcion() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        doReturn(mockResponse).when(mockHttpClient).send(any(), any());

        assertDoesNotThrow(() -> service.enviarMensaje("111", "Mensaje servidor caido"));
    }
}