package com.example.entregaya.controller;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

/**
 * HU-49 – Pruebas unitarias JUnit 5 para NotificacionController.
 * El controlador se instancia directamente con sus dependencias mockeadas.
 * Se verifican: vista retornada, atributos del modelo y códigos de respuesta
 * HTTP para los endpoints verNotificaciones, marcarLeida y marcarTodasLeidas.
 */
class NotificacionControllerTest {

    private NotificacionController notificacionController;
    private NotificacionRepository notificacionRepository;
    private UserRepository userRepository;

    /** UserDetails simulado para pruebas. */
    private org.springframework.security.core.userdetails.UserDetails userDetails;
    /** Entidad User del modelo simulada. */
    private User userModel;

    @BeforeEach
    void setUp() {
        notificacionRepository = Mockito.mock(NotificacionRepository.class);
        userRepository         = Mockito.mock(UserRepository.class);

        notificacionController = new NotificacionController(
                notificacionRepository, userRepository);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();

        userModel = new User();
        userModel.setUsername("testuser");

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userModel));
    }

    // CP01 – verNotificaciones(): retorna vista "notificaciones"
    // CP01 - NORMAL: verNotificaciones() retorna la vista correcta.
    // Entrada: usuario autenticado con notificaciones
    // Resultado esperado: String "notificaciones"
    @Test
    void CP01_verNotificaciones_RetornaVistaNotificaciones() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(notificacionRepository
                        .findByDestinatarioOrderByFechaCreacionDesc(userModel))
                .thenReturn(List.of());

        // Act
        String vista = notificacionController.verNotificaciones(model, userDetails);

        // Assert
        Assertions.assertEquals("notificaciones", vista,
                "verNotificaciones() debe retornar la vista 'notificaciones'");
    }

    // CP02 – verNotificaciones(): modelo contiene "notificaciones" y "noLeidas"
    // CP02 - NORMAL: verNotificaciones() agrega "notificaciones" y "noLeidas" al modelo.
    // Entrada: lista con 2 notificaciones (1 leída, 1 no leída)
    // Resultado esperado: atributos no nulos, noLeidas == 1
    @Test
    void CP02_verNotificaciones_AgregaAtributosAlModelo() {
        // Arrange
        Model model = new ExtendedModelMap();

        Notificacion leida = Mockito.mock(Notificacion.class);
        Mockito.when(leida.isLeida()).thenReturn(true);

        Notificacion noLeida = Mockito.mock(Notificacion.class);
        Mockito.when(noLeida.isLeida()).thenReturn(false);

        Mockito.when(notificacionRepository
                        .findByDestinatarioOrderByFechaCreacionDesc(userModel))
                .thenReturn(List.of(leida, noLeida));

        // Act
        notificacionController.verNotificaciones(model, userDetails);

        // Assert
        Assertions.assertNotNull(model.asMap().get("notificaciones"),
                "El modelo debe contener 'notificaciones'");
        Assertions.assertNotNull(model.asMap().get("noLeidas"),
                "El modelo debe contener 'noLeidas'");
        Assertions.assertEquals(1L, model.asMap().get("noLeidas"),
                "noLeidas debe ser 1 cuando hay una notificación sin leer");
    }

    // CP03 – verNotificaciones(): sin notificaciones, noLeidas es 0
    // CP03 - BORDE: verNotificaciones() con lista vacía retorna noLeidas == 0.
    // Entrada: usuario sin notificaciones
    // Resultado esperado: noLeidas == 0
    @Test
    void CP03_verNotificaciones_SinNotificaciones_NoLeidasEsCero() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(notificacionRepository
                        .findByDestinatarioOrderByFechaCreacionDesc(userModel))
                .thenReturn(List.of());

        // Act
        notificacionController.verNotificaciones(model, userDetails);

        // Assert
        Assertions.assertEquals(0L, model.asMap().get("noLeidas"),
                "Con lista vacía, noLeidas debe ser 0");
    }

    // CP04 – marcarLeida(): notificación no encontrada → 404
    // CP04 - NEGATIVA: marcarLeida() con id inexistente retorna 404 Not Found.
    // Entrada: id=999 que no existe en el repositorio
    // Resultado esperado: ResponseEntity con status 404
    @Test
    void CP04_marcarLeida_NotificacionNoEncontrada_Retorna404() {
        // Arrange
        Mockito.when(notificacionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = notificacionController.marcarLeida(999L, userDetails);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "marcarLeida() con id inexistente debe retornar 404");
    }

    // CP05 – marcarLeida(): usuario no es destinatario → 403
    // CP05 - NEGATIVA: marcarLeida() por usuario que no es destinatario retorna 403.
    // Entrada: notificación cuyo destinatario es otro usuario
    // Resultado esperado: ResponseEntity con status 403
    @Test
    void CP05_marcarLeida_UsuarioNoEsDestinatario_Retorna403() {
        // Arrange
        User otroUser = new User();
        otroUser.setUsername("otro");

        Notificacion notificacion = Mockito.mock(Notificacion.class);
        Mockito.when(notificacion.getDestinatario()).thenReturn(otroUser);

        Mockito.when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        // Act
        ResponseEntity<Void> response = notificacionController.marcarLeida(1L, userDetails);

        // Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "marcarLeida() por no-destinatario debe retornar 403");
    }

    // CP06 – marcarLeida(): destinatario correcto → 200 y guarda
    // CP06 - NORMAL: marcarLeida() por el destinatario correcto retorna 200 y llama a save().
    // Entrada: notificación cuyo destinatario coincide con el usuario autenticado
    // Resultado esperado: ResponseEntity con status 200, notificacionRepository.save() invocado
    @Test
    void CP06_marcarLeida_DestinatarioCorrecto_Retorna200_YGuarda() {
        // Arrange
        Notificacion notificacion = Mockito.mock(Notificacion.class);
        Mockito.when(notificacion.getDestinatario()).thenReturn(userModel);

        Mockito.when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        // Act
        ResponseEntity<Void> response = notificacionController.marcarLeida(1L, userDetails);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "marcarLeida() por el destinatario debe retornar 200");
        Mockito.verify(notificacion).setLeida(true);
        Mockito.verify(notificacionRepository).save(notificacion);
    }

    // CP07 – marcarTodasLeidas(): retorna 200 con el conteo correcto
    // CP07 - NORMAL: marcarTodasLeidas() retorna 200 con la cantidad de notificaciones marcadas.
    // Entrada: usuario con 3 notificaciones no leídas
    // Resultado esperado: ResponseEntity con status 200 y body == 3
    @Test
    void CP07_marcarTodasLeidas_Retorna200_ConConteoNotificaciones() {
        // Arrange
        Notificacion n1 = Mockito.mock(Notificacion.class);
        Notificacion n2 = Mockito.mock(Notificacion.class);
        Notificacion n3 = Mockito.mock(Notificacion.class);

        Mockito.when(notificacionRepository.findByDestinatarioAndLeidaFalse(userModel))
                .thenReturn(List.of(n1, n2, n3));

        // Act
        ResponseEntity<Integer> response = notificacionController.marcarTodasLeidas(userDetails);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "marcarTodasLeidas() debe retornar 200");
        Assertions.assertEquals(3, response.getBody(),
                "El body debe indicar que se marcaron 3 notificaciones");
        Mockito.verify(notificacionRepository).saveAll(Mockito.anyList());
    }

    // CP08 – marcarTodasLeidas(): sin notificaciones pendientes → 200 con body 0
    // CP08 - BORDE: marcarTodasLeidas() sin notificaciones pendientes retorna body == 0.
    // Entrada: usuario sin notificaciones no leídas
    // Resultado esperado: ResponseEntity con status 200 y body == 0
    @Test
    void CP08_marcarTodasLeidas_SinPendientes_Retorna200_ConCuerpoEnCero() {
        // Arrange
        Mockito.when(notificacionRepository.findByDestinatarioAndLeidaFalse(userModel))
                .thenReturn(List.of());

        // Act
        ResponseEntity<Integer> response = notificacionController.marcarTodasLeidas(userDetails);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "marcarTodasLeidas() con lista vacía debe retornar 200");
        Assertions.assertEquals(0, response.getBody(),
                "Con lista vacía el body debe ser 0");
    }
}