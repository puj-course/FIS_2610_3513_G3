package com.example.entregaya.service;

import com.example.entregaya.model.*;
import com.example.entregaya.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomTareaDetailsService - Tests adicionales de cobertura")
class CustomTareaDetailsServiceExtraTest {

    @Autowired
    private CustomTareaDetailsService tareaService;

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private Trabajo trabajo;
    private User lider;
    private User colaborador;

    @BeforeEach
    void setUp() {
        userDetailsService.register("lider_extra", "pass123456", "lider_extra@test.com");
        userDetailsService.register("colab_extra", "pass123456", "colab_extra@test.com");

        lider = userRepository.findByUsername("lider_extra").orElseThrow();
        colaborador = userRepository.findByUsername("colab_extra").orElseThrow();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Trabajo Extra " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoService.crearTrabajo(trabajo, "lider_extra");
    }

    // CP01 - NORMAL: Clonar tarea siendo LIDER funciona
    @Test
    @DisplayName("CP01: clonarTarea siendo LIDER crea copia correcta")
    void CP01_clonarTarea_SiendoLider_CreaCopiaConcorrecta() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Original a Clonar");
        tarea.setDificultad(Tarea.Dificultad.ALTA);
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        Tarea clon = tareaService.clonarTarea(guardada.getId(), trabajo.getId(), "lider_extra");

        assertNotNull(clon.getId());
        assertNotEquals(guardada.getId(), clon.getId());
        assertTrue(clon.getNombre().contains("[Copia]"));
    }

    // CP02 - NEGATIVA: Clonar tarea sin permiso lanza SecurityException
    @Test
    @DisplayName("CP02: clonarTarea sin permiso lanza SecurityException")
    void CP02_clonarTarea_SinPermiso_LanzaSecurityException() {
        trabajoService.agregarColaborador(trabajo.getId(), "colab_extra");

        Tarea tarea = new Tarea();
        tarea.setNombre("No Clonable");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        assertThrows(SecurityException.class, () ->
            tareaService.clonarTarea(guardada.getId(), trabajo.getId(), "colab_extra")
        );
    }

    // CP03 - NORMAL: tareasPorEtiqueta con etiqueta válida filtra correctamente
    @Test
    @DisplayName("CP03: tareasPorEtiqueta filtra tareas por etiqueta")
    void CP03_tareasPorEtiqueta_ConEtiquetaValida_FiltroTareas() {
        Tarea t1 = new Tarea();
        t1.setNombre("Con Etiqueta");
        t1.setFechaInicio(LocalDateTime.now());
        t1.setFechaFinal(LocalDateTime.now().plusDays(3));
        tareaService.crearTarea(t1, trabajo.getId(), null, List.of("backend"));

        Tarea t2 = new Tarea();
        t2.setNombre("Sin Etiqueta");
        t2.setFechaInicio(LocalDateTime.now());
        t2.setFechaFinal(LocalDateTime.now().plusDays(3));
        tareaService.crearTarea(t2, trabajo.getId(), null, List.of("frontend"));

        List<Tarea> resultado = tareaService.tareasPorEtiqueta(trabajo.getId(), "backend");

        assertEquals(1, resultado.size());
        assertEquals("Con Etiqueta", resultado.get(0).getNombre());
    }

    // CP04 - BORDE: tareasPorEtiqueta con etiqueta null retorna todas
    @Test
    @DisplayName("CP04: tareasPorEtiqueta con etiqueta null retorna todas las tareas")
    void CP04_tareasPorEtiqueta_ConEtiquetaNull_RetornaTodas() {
        Tarea t1 = new Tarea();
        t1.setNombre("Tarea Uno");
        t1.setFechaInicio(LocalDateTime.now());
        t1.setFechaFinal(LocalDateTime.now().plusDays(3));
        tareaService.crearTarea(t1, trabajo.getId());

        Tarea t2 = new Tarea();
        t2.setNombre("Tarea Dos");
        t2.setFechaInicio(LocalDateTime.now());
        t2.setFechaFinal(LocalDateTime.now().plusDays(3));
        tareaService.crearTarea(t2, trabajo.getId());

        List<Tarea> resultado = tareaService.tareasPorEtiqueta(trabajo.getId(), null);

        assertEquals(2, resultado.size());
    }

    // CP05 - NORMAL: findByIdConEtiqueta retorna el DTO con etiqueta aplicada
    @Test
    @DisplayName("CP05: findByIdConEtiqueta retorna el DTO correcto")
    void CP05_findByIdConEtiqueta_RetornaDTOCorrecto() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Con Etiqueta DTO");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(10));
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        var dto = tareaService.findByIdConEtiqueta(guardada.getId());

        assertNotNull(dto);
        assertEquals("Con Etiqueta DTO", dto.nombre());
        assertNotNull(dto.etiquetaUrgencia());
    }

    // CP06 - NORMAL: actualizarResponsables con lista vacía limpia los responsables
    @Test
    @DisplayName("CP06: actualizarResponsables con lista vacía limpia responsables")
    void CP06_actualizarResponsables_ConListaVacia_LimpiaResponsables() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Con Responsables");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId(), List.of(lider.getId()));

        tareaService.actualizarResponsables(guardada.getId(), null);

        Tarea actualizada = tareaService.findById(guardada.getId());
        assertEquals(0, actualizada.getResponsables().size());
    }

    // CP07 - NORMAL: calcularProgreso sin tareas retorna 0
    @Test
    @DisplayName("CP07: calcularProgreso sin tareas retorna 0")
    void CP07_calcularProgreso_SinTareas_RetornaCero() {
        int progreso = tareaService.calcularProgreso(trabajo.getId());
        assertEquals(0, progreso);
    }

    // CP08 - NORMAL: toggleCompletada invierte el estado dos veces
    @Test
    @DisplayName("CP08: toggleCompletada invierte el estado correctamente")
    void CP08_toggleCompletada_InvierteEstado() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Toggle Test");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(3));
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        // false -> true
        tareaService.toggleCompletada(guardada.getId(), "lider_extra");
        assertTrue(tareaService.findById(guardada.getId()).getIsCompletada());

        // true -> false
        tareaService.toggleCompletada(guardada.getId(), "lider_extra");
        assertFalse(tareaService.findById(guardada.getId()).getIsCompletada());
    }

    // CP09 - NORMAL: Registrar y eliminar observer
    @Test
    @DisplayName("CP09: registrarObserver y eliminarObserver modifican la lista de observers")
    void CP09_registrarYEliminarObserver() {
        var observer = (com.example.entregaya.observer.TareaObserver) evento -> {};

        assertDoesNotThrow(() -> {
            tareaService.registrarObserver(observer);
            tareaService.eliminarObserver(observer);
        });
    }
}
