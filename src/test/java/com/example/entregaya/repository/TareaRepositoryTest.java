package com.example.entregaya.repository;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TareaRepository - Tests con H2 y JUnit 5")
class TareaRepositoryTest {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private TrabajoRepository trabajoRepository;

    private Trabajo trabajo;

    @BeforeEach
    void setUp() {
        tareaRepository.deleteAll();
        trabajoRepository.deleteAll();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Test");
        trabajo.setDescripcion("Proyecto para testing");
        trabajo.setFechaInicio(LocalDateTime.of(2026, 5, 1, 10, 0));
        trabajo.setFechaEntrega(LocalDateTime.of(2026, 6, 1, 10, 0));
        trabajo = trabajoRepository.save(trabajo);
    }

    @Test
    @DisplayName("CP01: Obtener tareas por ID del trabajo")
    void CP01_findByTrabajoId_ConTareasExistentes_RetornaLista() {
        Tarea tarea1 = new Tarea();
        tarea1.setNombre("Tarea 1");
        tarea1.setTrabajo(trabajo);
        tarea1.setFechaInicio(LocalDateTime.now());
        tarea1.setFechaFinal(LocalDateTime.now().plusDays(1));

        Tarea tarea2 = new Tarea();
        tarea2.setNombre("Tarea 2");
        tarea2.setTrabajo(trabajo);
        tarea2.setFechaInicio(LocalDateTime.now());
        tarea2.setFechaFinal(LocalDateTime.now().plusDays(2));

        tareaRepository.save(tarea1);
        tareaRepository.save(tarea2);

        List<Tarea> resultado = tareaRepository.findBytrabajoId(trabajo.getId());

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(t -> t.getNombre().equals("Tarea 1")));
        assertTrue(resultado.stream().anyMatch(t -> t.getNombre().equals("Tarea 2")));
    }

    @Test
    @DisplayName("CP02: findBytrabajoId sin tareas retorna lista vacía")
    void CP02_findByTrabajoId_SinTareas_RetornaListaVacia() {
        List<Tarea> resultado = tareaRepository.findBytrabajoId(trabajo.getId());

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP03: Búsqueda nativa SQL por ID de trabajo")
    void CP03_buscarPorTrabajoNativo_ConTareas_RetornaResultados() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Nativa");
        tarea.setTrabajo(trabajo);
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(1));
        tareaRepository.save(tarea);

        List<Tarea> resultado = tareaRepository.buscarPorTrabajoNativo(trabajo.getId());

        assertEquals(1, resultado.size());
        assertEquals("Tarea Nativa", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("CP04: buscarPorTrabajoNativo con ID inválido retorna vacío")
    void CP04_buscarPorTrabajoNativo_ConIdInvalido_RetornaListaVacia() {
        List<Tarea> resultado = tareaRepository.buscarPorTrabajoNativo(9999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP05: Filtrar tareas por etiqueta")
    void CP05_findByTrabajoIdAndEtiqueta_ConEtiquetasValidas_RetornaFiltrado() {
        Tarea tarea1 = new Tarea();
        tarea1.setNombre("Tarea Backend");
        tarea1.setTrabajo(trabajo);
        tarea1.setFechaInicio(LocalDateTime.now());
        tarea1.setFechaFinal(LocalDateTime.now().plusDays(1));
        tarea1.setEtiquetas(List.of("backend", "urgente"));

        Tarea tarea2 = new Tarea();
        tarea2.setNombre("Tarea Frontend");
        tarea2.setTrabajo(trabajo);
        tarea2.setFechaInicio(LocalDateTime.now());
        tarea2.setFechaFinal(LocalDateTime.now().plusDays(1));
        tarea2.setEtiquetas(List.of("frontend", "urgente"));

        tareaRepository.save(tarea1);
        tareaRepository.save(tarea2);

        List<Tarea> resultado = tareaRepository.findByTrabajoIdAndEtiqueta(trabajo.getId(), "backend");

        assertEquals(1, resultado.size());
        assertEquals("Tarea Backend", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("CP06: findByTrabajoIdAndEtiqueta con case-insensitive")
    void CP06_findByTrabajoIdAndEtiqueta_CaseInsensitive_RetornaResultado() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Prueba");
        tarea.setTrabajo(trabajo);
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(1));
        tarea.setEtiquetas(List.of("BACKEND"));
        tareaRepository.save(tarea);

        List<Tarea> resultado = tareaRepository.findByTrabajoIdAndEtiqueta(trabajo.getId(), "backend");

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("CP07: Obtener tareas próximas a vencer")
    void CP07_findTareasProximasAVencer_ConTareasProximas_RetornaFiltrado() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(2);

        Tarea tareaProxima = new Tarea();
        tareaProxima.setNombre("Tarea Próxima a Vencer");
        tareaProxima.setTrabajo(trabajo);
        tareaProxima.setFechaInicio(ahora);
        tareaProxima.setFechaFinal(ahora.plusHours(1));
        tareaProxima.setCompletada(false);
        tareaProxima.setRecordatorioEnviado(false);

        Tarea tareaLejana = new Tarea();
        tareaLejana.setNombre("Tarea Lejana");
        tareaLejana.setTrabajo(trabajo);
        tareaLejana.setFechaInicio(ahora.plusDays(10));
        tareaLejana.setFechaFinal(ahora.plusDays(11));
        tareaLejana.setCompletada(false);
        tareaLejana.setRecordatorioEnviado(false);

        tareaRepository.save(tareaProxima);
        tareaRepository.save(tareaLejana);

        List<Tarea> resultado = tareaRepository.findTareasProximasAVencer(ahora, limite);

        assertEquals(1, resultado.size());
        assertEquals("Tarea Próxima a Vencer", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("CP08: findTareasProximasAVencer excluye completadas")
    void CP08_findTareasProximasAVencer_ExcluyeCompletadas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(2);

        Tarea tareaCompletada = new Tarea();
        tareaCompletada.setNombre("Tarea Completada");
        tareaCompletada.setTrabajo(trabajo);
        tareaCompletada.setFechaInicio(ahora);
        tareaCompletada.setFechaFinal(ahora.plusHours(1));
        tareaCompletada.setCompletada(true);
        tareaCompletada.setRecordatorioEnviado(false);

        tareaRepository.save(tareaCompletada);

        List<Tarea> resultado = tareaRepository.findTareasProximasAVencer(ahora, limite);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP09: findTareasProximasAVencer excluye con recordatorio enviado")
    void CP09_findTareasProximasAVencer_ExcluyeRecordatorioEnviado() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(2);

        Tarea tareaConRecordatorio = new Tarea();
        tareaConRecordatorio.setNombre("Tarea con Recordatorio");
        tareaConRecordatorio.setTrabajo(trabajo);
        tareaConRecordatorio.setFechaInicio(ahora);
        tareaConRecordatorio.setFechaFinal(ahora.plusHours(1));
        tareaConRecordatorio.setCompletada(false);
        tareaConRecordatorio.setRecordatorioEnviado(true);

        tareaRepository.save(tareaConRecordatorio);

        List<Tarea> resultado = tareaRepository.findTareasProximasAVencer(ahora, limite);

        assertTrue(resultado.isEmpty());
    }
}
