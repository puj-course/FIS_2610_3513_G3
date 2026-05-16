package com.example.entregaya.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LatenciaInterceptorTest {

    @Test
    @DisplayName("CP_NEW_04: afterCompletion sin atributo de inicio no lanza excepción")
    void afterCompletion_SinAtributoInicio_RetornaInmediatamente() throws Exception {
        LatenciaInterceptor interceptor = new LatenciaInterceptor();

        HttpServletRequest  request  = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // Sin setear el atributo → getAttribute devuelve null
        Mockito.when(request.getAttribute("latencia_inicio_ms")).thenReturn(null);

        // No debe lanzar ninguna excepción; simplemente retorna
        interceptor.afterCompletion(request, response, new Object(), null);

        // Verificar que nunca se intentó leer el status (el método retornó antes)
        Mockito.verify(response, Mockito.never()).getStatus();
    }

    @Test
    @DisplayName("CP_NEW_05: afterCompletion con latencia alta ejecuta la rama de advertencia")
    void afterCompletion_LatenciaAltaSupeaUmbral_EjecutaRamaWarn() throws Exception {
        LatenciaInterceptor interceptor = new LatenciaInterceptor();

        HttpServletRequest  request  = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // Simular inicio 2 segundos en el pasado → latencia ≈ 2000 ms > 1000 ms
        long inicioSimulado = System.currentTimeMillis() - 2000L;
        Mockito.when(request.getAttribute("latencia_inicio_ms")).thenReturn(inicioSimulado);
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getRequestURI()).thenReturn("/trabajos/1/tareas");
        Mockito.when(response.getStatus()).thenReturn(200);

        // No debe lanzar excepción y debe ejecutar el log.warn interno
        interceptor.afterCompletion(request, response, new Object(), null);

        // Verificar que sí se consultó el status (confirmando que no retornó temprano)
        Mockito.verify(response, Mockito.atLeastOnce()).getStatus();
    }
}