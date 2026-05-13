package com.example.entregaya.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.example.entregaya.interceptor.LatenciaInterceptor;
import org.mockito.Mockito;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LatenciaInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    private static final long UMBRAL_MS = LatenciaInterceptor.UMBRAL_MS;

    @Test
    @DisplayName("GET /login responde en menos de 500 ms")
    void loginDebeResponderDentroDelUmbral() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/login"))
               .andExpect(status().isOk());

        long latencia = System.currentTimeMillis() - inicio;

        assertTrue(latencia < UMBRAL_MS,
                "El endpoint /login tardó " + latencia + " ms, "
                + "superando el umbral de " + UMBRAL_MS + " ms");
    }

    @Test
    @DisplayName("GET /dashboard responde en menos de 500 ms")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void dashboardDebeResponderDentroDelUmbral() throws Exception {
        long inicio = System.currentTimeMillis();

        // En test la BD es H2 vacía; se espera redirect o 200, no importa el status exacto
        mockMvc.perform(get("/dashboard"));

        long latencia = System.currentTimeMillis() - inicio;

        assertTrue(latencia < UMBRAL_MS,
                "El endpoint /dashboard tardó " + latencia + " ms, "
                + "superando el umbral de " + UMBRAL_MS + " ms");
    }

    @Test
    @DisplayName("GET /trabajos responde en menos de 500 ms")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void trabajosDebeResponderDentroDelUmbral() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/trabajos"));

        long latencia = System.currentTimeMillis() - inicio;

        assertTrue(latencia < UMBRAL_MS,
                "El endpoint /trabajos tardó " + latencia + " ms, "
                + "superando el umbral de " + UMBRAL_MS + " ms");
    }
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