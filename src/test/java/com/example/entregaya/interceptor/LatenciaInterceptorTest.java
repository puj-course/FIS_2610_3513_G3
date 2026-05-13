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
}