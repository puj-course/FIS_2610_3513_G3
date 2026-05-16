package com.example.entregaya.repository;

import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Trabajo;
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
@DisplayName("HistorialEventoRepository - Tests con H2 y JUnit 5")
class HistorialEventoRepositoryTest {

    @Autowired private HistorialEventoRepository historialRepository;
    @Autowired private TrabajoRepository trabajoRepository;

    private Trabajo trabajo;

    @BeforeEach
    void setUp() {
        historialRepository.deleteAll();
        trabajoRepository.deleteAll();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Historial");
        trabajo.setDescripcion("Tests de historial");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoRepository.save(trabajo);
    }

    private HistorialEvento crear(HistorialEvento.TipoEvento tipo, String usuario, LocalDateTime fecha) {
        HistorialEvento h = new HistorialEvento();
        h.setTrabajo(trabajo);
        h.setTipoEvento(tipo);
        h.setDescripcion("evento de prueba");
        h.setUsuarioAccion(usuario);
        h.setFechaEvento(fecha);
        return historialRepository.save(h);
    }

    @Test
    @DisplayName("CP01: findByTrabajoIdOrderByFechaDesc retorna ordenado por fecha desc")
    void CP01_findByTrabajoIdOrderByFechaDesc_OrdenaDesc() {
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now().minusDays(2));
        crear(HistorialEvento.TipoEvento.EDICION_TRABAJO, "user2", LocalDateTime.now());

        List<HistorialEvento> resultado =
                historialRepository.findByTrabajoIdOrderByFechaDesc(trabajo.getId());

        assertEquals(2, resultado.size());
        assertEquals(HistorialEvento.TipoEvento.EDICION_TRABAJO, resultado.get(0).getTipoEvento());
        assertEquals(HistorialEvento.TipoEvento.CREACION_TAREA, resultado.get(1).getTipoEvento());
    }

    @Test
    @DisplayName("CP02: findByTrabajoIdAndFechaRange filtra por rango de fechas")
    void CP02_findByFechaRange_FiltraPorRango() {
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now().minusDays(10));
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now().minusDays(1));
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now().plusDays(5));

        LocalDateTime inicio = LocalDateTime.now().minusDays(5);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);

        List<HistorialEvento> resultado =
                historialRepository.findByTrabajoIdAndFechaRange(trabajo.getId(), inicio, fin);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("CP03: findByTrabajoIdAndTipoEvento filtra por tipo de evento")
    void CP03_findByTipoEvento_FiltraSoloElTipo() {
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now());
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "user1", LocalDateTime.now());
        crear(HistorialEvento.TipoEvento.EDICION_TRABAJO, "user1", LocalDateTime.now());

        List<HistorialEvento> resultado = historialRepository.findByTrabajoIdAndTipoEvento(
                trabajo.getId(), HistorialEvento.TipoEvento.CREACION_TAREA);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(h -> h.getTipoEvento() == HistorialEvento.TipoEvento.CREACION_TAREA));
    }

    @Test
    @DisplayName("CP04: findByTrabajoIdAndUsuarioAccion filtra por usuario que ejecutó la acción")
    void CP04_findByUsuarioAccion_FiltraSoloEseUsuario() {
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "alice", LocalDateTime.now());
        crear(HistorialEvento.TipoEvento.CREACION_TAREA, "bob", LocalDateTime.now());
        crear(HistorialEvento.TipoEvento.EDICION_TRABAJO, "alice", LocalDateTime.now());

        List<HistorialEvento> resultado = historialRepository.findByTrabajoIdAndUsuarioAccion(
                trabajo.getId(), "alice");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getUsuarioAccion().equals("alice")));
    }
}
