package com.example.entregaya.controller;

import com.example.entregaya.service.CustomInvitacionDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 * HU-49 – Pruebas unitarias JUnit 5 para InvitacionController.
 * El controlador se instancia directamente con su dependencia mockeada.
 * Se verifican redirecciones y atributos flash para los flujos:
 * invitar, aceptar, rechazar y cancelar.
 */
class InvitacionControllerTest {

    private InvitacionController invitacionController;
    private CustomInvitacionDetailsService customInvitacionDetailsService;
    private UserDetails userLider;

    @BeforeEach
    void setUp() {
        customInvitacionDetailsService = Mockito.mock(CustomInvitacionDetailsService.class);
        invitacionController = new InvitacionController(customInvitacionDetailsService);

        userLider = User.withUsername("lider")
                .password("pass")
                .roles("USER")
                .build();
    }

    // CP01 – invitar() exitoso: redirige al detalle del trabajo
    // CP01 - NORMAL: invitar() con datos válidos redirige al detalle del trabajo.
    // Entrada: trabajoId=1, destinatario="user2", usuario autenticado
    // Resultado esperado: "redirect:/trabajos/1", flash "succesInv" presente
    @Test
    void CP01_invitar_Exitoso_RedireccionaAlTrabajo_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = invitacionController.invitar(1L, "user2", userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "invitar() exitoso debe redirigir a '/trabajos/1'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("succesInv"),
                "Debe existir el atributo flash 'succesInv'");
        Mockito.verify(customInvitacionDetailsService)
                .enviarInvitacion(1L, "lider", "user2");
    }

    // CP02 – invitar() con excepción: redirige al trabajo con error
    // CP02 - NEGATIVA: invitar() cuando el servicio lanza excepción agrega flash "errorInv".
    // Entrada: destinatario inexistente que causa excepción
    // Resultado esperado: "redirect:/trabajos/1", flash "errorInv" presente
    @Test
    void CP02_invitar_ConExcepcion_RedireccionaAlTrabajo_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new RuntimeException("Usuario no encontrado"))
                .when(customInvitacionDetailsService)
                .enviarInvitacion(1L, "lider", "inexistente");

        // Act
        String resultado = invitacionController.invitar(1L, "inexistente", userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "invitar() con error debe redirigir igualmente a '/trabajos/1'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorInv"),
                "Debe existir el atributo flash 'errorInv'");
    }

    // CP03 – aceptar() exitoso: redirige al dashboard
    // CP03 - NORMAL: aceptar() con invitación válida redirige al dashboard.
    // Entrada: id=5, usuario autenticado
    // Resultado esperado: "redirect:/dashboard", flash "success" presente
    @Test
    void CP03_aceptar_Exitoso_RedireccionaAlDashboard_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = invitacionController.aceptar(5L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/dashboard", resultado,
                "aceptar() exitoso debe redirigir a '/dashboard'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("success"),
                "Debe existir el atributo flash 'success'");
        Mockito.verify(customInvitacionDetailsService).aceptar(5L, "lider");
    }

    // CP04 – aceptar() con excepción: redirige al dashboard con error
    // CP04 - NEGATIVA: aceptar() cuando el servicio lanza excepción agrega flash "error".
    // Entrada: id de invitación que genera excepción
    // Resultado esperado: "redirect:/dashboard", flash "error" presente
    @Test
    void CP04_aceptar_ConExcepcion_RedireccionaAlDashboard_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("Invitacion no existe"))
                .when(customInvitacionDetailsService).aceptar(99L, "lider");

        // Act
        String resultado = invitacionController.aceptar(99L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/dashboard", resultado,
                "aceptar() con error debe redirigir a '/dashboard'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"),
                "Debe existir el atributo flash 'error'");
    }

    // CP05 – rechazar() exitoso: redirige al dashboard con info
    // CP05 - NORMAL: rechazar() con invitación válida redirige al dashboard.
    // Entrada: id=5, usuario autenticado
    // Resultado esperado: "redirect:/dashboard", flash "info" presente
    @Test
    void CP05_rechazar_Exitoso_RedireccionaAlDashboard_ConFlashInfo() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = invitacionController.rechazar(5L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/dashboard", resultado,
                "rechazar() exitoso debe redirigir a '/dashboard'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("info"),
                "Debe existir el atributo flash 'info'");
        Mockito.verify(customInvitacionDetailsService).rechazar(5L, "lider");
    }

    // CP06 – rechazar() con IllegalArgumentException: redirige con error
    // CP06 - NEGATIVA: rechazar() cuando el servicio lanza IllegalArgumentException.
    // Entrada: id de invitación inválida
    // Resultado esperado: "redirect:/dashboard", flash "error" presente
    @Test
    void CP06_rechazar_ConExcepcion_RedireccionaAlDashboard_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("No puedes rechazar esta invitacion"))
                .when(customInvitacionDetailsService).rechazar(99L, "lider");

        // Act
        String resultado = invitacionController.rechazar(99L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/dashboard", resultado,
                "rechazar() con error debe redirigir a '/dashboard'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"),
                "Debe existir el atributo flash 'error'");
    }

    // CP07 – cancelar() exitoso: redirige al trabajo con flash success
    // CP07 - NORMAL: cancelar() exitoso retorna el trabajoId correcto en la redirección.
    // Entrada: id=3, servicio retorna trabajoId=7
    // Resultado esperado: "redirect:/trabajos/7", flash "succesInv" presente
    @Test
    void CP07_cancelar_Exitoso_RedireccionaAlTrabajo_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customInvitacionDetailsService.cancelar(3L, "lider")).thenReturn(7L);

        // Act
        String resultado = invitacionController.cancelar(3L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/7", resultado,
                "cancelar() exitoso debe redirigir al trabajo con el id retornado por el servicio");
        Assertions.assertNotNull(ra.getFlashAttributes().get("succesInv"),
                "Debe existir el atributo flash 'succesInv'");
    }

    // CP08 – cancelar() con IllegalArgumentException: redirige a /trabajos
    // CP08 - NEGATIVA: cancelar() con IllegalArgumentException redirige a la lista de trabajos.
    // Entrada: id de invitación no cancelable
    // Resultado esperado: "redirect:/trabajos", flash "errorInv" presente
    @Test
    void CP08_cancelar_ConIllegalArgumentException_RedireccionaATrabajos_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customInvitacionDetailsService.cancelar(99L, "lider"))
                .thenThrow(new IllegalArgumentException("Solo se pueden cancelar invitaciones en estado PENDIENTE"));

        // Act
        String resultado = invitacionController.cancelar(99L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos", resultado,
                "cancelar() con IllegalArgumentException debe redirigir a '/trabajos'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorInv"),
                "Debe existir el atributo flash 'errorInv'");
    }

    // CP09 – cancelar() con Exception genérica: redirige a /trabajos
    // CP09 - BORDE: cancelar() con excepción genérica también redirige a /trabajos.
    // Entrada: excepción inesperada en el servicio
    // Resultado esperado: "redirect:/trabajos", flash "errorInv" presente
    @Test
    void CP09_cancelar_ConExcepcionGenerica_RedireccionaATrabajos_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customInvitacionDetailsService.cancelar(88L, "lider"))
                .thenThrow(new RuntimeException("Error inesperado"));

        // Act
        String resultado = invitacionController.cancelar(88L, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos", resultado,
                "cancelar() con excepción genérica debe redirigir a '/trabajos'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorInv"),
                "Debe existir el atributo flash 'errorInv'");
    }
}