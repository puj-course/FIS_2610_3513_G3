package com.example.entregaya.controller;

import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.PdfExportService;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests adicionales para actualizarRolMiembro en TrabajoController.
 * El test existente solo cubre 3 ramas (success, IllegalArgument, generic exception).
 * Faltan 4 ramas que suman la mayoría de las 14 condiciones sin cubrir reportadas por Sonar:
 *   - usuario no es LIDER -> 403
 *   - rol null -> 400
 *   - rol en blanco -> 400
 *   - rol inválido (no es enum) -> 400
 */
@DisplayName("TrabajoController.actualizarRolMiembro - Cobertura de ramas de validación")
class TrabajoControllerRolCoverageTest {

    private TrabajoController trabajoController;
    private CustomTrabajoDetailsService customTrabajoDetailsService;
    private UserDetails userLider;

    @BeforeEach
    void setUp() {
        customTrabajoDetailsService = Mockito.mock(CustomTrabajoDetailsService.class);
        CustomTareaDetailsService tareaService = Mockito.mock(CustomTareaDetailsService.class);
        CustomInvitacionDetailsService invitacionService =
                Mockito.mock(CustomInvitacionDetailsService.class);
        PdfExportService pdfExportService = Mockito.mock(PdfExportService.class);
        Lideroeditorstrategy strategy = Mockito.mock(Lideroeditorstrategy.class);

        trabajoController = new TrabajoController(
                customTrabajoDetailsService, tareaService, invitacionService,
                pdfExportService, strategy);

        userLider = org.springframework.security.core.userdetails.User
                .withUsername("lider").password("pass").roles("USER").build();
    }

    @Test
    @DisplayName("CP01: usuario no es LIDER retorna 403 con mensaje de error")
    void CP01_actualizarRolMiembro_NoEsLider_Retorna403() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(false);
        Map<String, String> request = Map.of("rol", "EDITOR");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertTrue(resp.getBody().get("error").toString().contains("LIDER"));
        // Nunca debe llamar a cambiarRol porque el filtro de LIDER cortó antes
        Mockito.verify(customTrabajoDetailsService, Mockito.never())
                .cambiarRol(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    @DisplayName("CP02: request con rol null retorna 400")
    void CP02_actualizarRolMiembro_RolNull_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = new HashMap<>();
        request.put("rol", null);

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertTrue(resp.getBody().get("error").toString().toLowerCase().contains("requerido"));
    }

    @Test
    @DisplayName("CP03: request con rol en blanco retorna 400")
    void CP03_actualizarRolMiembro_RolBlanco_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = Map.of("rol", "   ");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("CP04: request con rol inválido (no es enum) retorna 400")
    void CP04_actualizarRolMiembro_RolInvalido_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = Map.of("rol", "SUPERADMIN");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertTrue(resp.getBody().get("error").toString().contains("Rol inválido"));
    }

    @Test
    @DisplayName("CP05: rol válido en lowercase se normaliza a mayúsculas y funciona")
    void CP05_actualizarRolMiembro_RolLowercase_SeNormaliza() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = Map.of("rol", "editor");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertEquals("EDITOR", resp.getBody().get("nuevoRol"));
    }
}
