package com.example.entregaya.repository;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("NotificacionRepository - Tests con H2 y JUnit 5")
class NotificacionRepositoryTest {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        notificacionRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("repo-noti-user");
        user.setPassword("password123");
        user.setEmail("repo-noti-user@example.com");
        user = userRepository.save(user);
    }

    private Notificacion crearNotificacion(String mensaje, boolean leida, LocalDateTime fecha) {
        Notificacion n = new Notificacion();
        n.setDestinatario(user);
        n.setMensaje(mensaje);
        n.setLeida(leida);
        n.setFechaCreacion(fecha);
        n.setTipo(Notificacion.TipoNotificacion.TAREA);
        return notificacionRepository.save(n);
    }

    @Test
    @DisplayName("CP01: findByDestinatarioOrderByFechaCreacionDesc retorna todas ordenadas por fecha desc")
    void CP01_findByDestinatario_OrdenaPorFechaDesc() {
        crearNotificacion("antigua", false, LocalDateTime.now().minusDays(2));
        crearNotificacion("reciente", true, LocalDateTime.now());

        List<Notificacion> resultado =
                notificacionRepository.findByDestinatarioOrderByFechaCreacionDesc(user);

        assertEquals(2, resultado.size());
        assertEquals("reciente", resultado.get(0).getMensaje());
        assertEquals("antigua", resultado.get(1).getMensaje());
    }

    @Test
    @DisplayName("CP02: findByDestinatarioAndLeidaFalseOrderByFechaCreacionDesc filtra solo no leídas")
    void CP02_findNoLeidas_Ordenadas_FiltraSoloNoLeidas() {
        crearNotificacion("leida", true, LocalDateTime.now().minusDays(1));
        crearNotificacion("noLeida", false, LocalDateTime.now());

        List<Notificacion> resultado =
                notificacionRepository.findByDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(user);

        assertEquals(1, resultado.size());
        assertEquals("noLeida", resultado.get(0).getMensaje());
        assertFalse(resultado.get(0).isLeida());
    }

    @Test
    @DisplayName("CP03: findByDestinatarioAndLeidaFalse retorna no leídas sin ordenamiento")
    void CP03_findNoLeidas_SinOrden_RetornaSoloNoLeidas() {
        crearNotificacion("leida1", true, LocalDateTime.now().minusDays(1));
        crearNotificacion("noLeida1", false, LocalDateTime.now().minusDays(2));
        crearNotificacion("noLeida2", false, LocalDateTime.now());

        List<Notificacion> resultado =
                notificacionRepository.findByDestinatarioAndLeidaFalse(user);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().noneMatch(Notificacion::isLeida));
    }
}
