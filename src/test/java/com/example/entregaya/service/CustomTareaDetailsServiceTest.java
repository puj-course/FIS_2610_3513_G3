package com.example.entregaya.service;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.strategy.Lideroeditorstrategy;
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
@DisplayName("CustomTareaDetailsService - Tests con @SpringBootTest")
class CustomTareaDetailsServiceTest {

    @Autowired
    private CustomTareaDetailsService customTareaDetailsService;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private TrabajoRepository trabajoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomTrabajoDetailsService customTrabajoDetailsService;

    @Autowired
    private Lideroeditorstrategy lideroeditorstrategy;

    private Trabajo trabajo;
    private User usuario;

    @BeforeEach
    void setUp() {
        // Crear usuario
        usuario = new User();
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setEmail("test@example.com");
        userRepository.save(usuario);

        // Crear trabajo
        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Test Service");
        trabajo.setDescripcion("Proyecto para service testing");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = customTrabajoDetailsService.crearTrabajo(trabajo, "testuser");
    }

    // ========== CP01: Crear tarea sin etiquetas ==========
    @Test
    @DisplayName("CP01: Crear tarea básica sin etiquetas")
    void CP01_crearTarea_SinEtiquetas_RetornaTareaGuardada() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea de Prueba");
        tarea.setDescripcion("Descripción de prueba");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        tarea.setDificultad(Tarea.Dificultad.MEDIA);

        // Act
        Tarea resultado = customTareaDetailsService.crearTarea(tarea, trabajo.getId());

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("Tarea de Prueba", resultado.getNombre());
        assertEquals(trabajo.getId(), resultado.getTrabajo().getId());
    }

    // ========== CP02: Crear tarea con etiquetas válidas ==========
    @Test
    @DisplayName("CP02: Crear tarea con etiquetas válidas")
    void CP02_crearTarea_ConEtiquetasValidas_GuardaEtiquetas() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea con Etiquetas");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(3));

        List<String> etiquetas = List.of("backend", "urgente");

        // Act
        Tarea resultado = customTareaDetailsService.crearTarea(tarea, trabajo.getId(), null, etiquetas);

        // Assert
        assertEquals(2, resultado.getEtiquetas().size());
        assertTrue(resultado.getEtiquetas().contains("backend"));
        assertTrue(resultado.getEtiquetas().contains("urgente"));
    }

    // ========== CP03: Validación de etiquetas - Máximo 5 ==========
    @Test
    @DisplayName("CP03: Validar que máximo 5 etiquetas lanza excepción")
    void CP03_crearTarea_ConMasDe5Etiquetas_LanzaExcepcion() {
        // Arrange
        List<String> etiquetas = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customTareaDetailsService.validarEtiquetas(etiquetas);
        });
    }

    // ========== CP04: Validación de etiquetas - Máximo 20 caracteres ==========
    @Test
    @DisplayName("CP04: Validar que etiqueta >20 caracteres lanza excepción")
    void CP04_crearTarea_ConEtiquetaLarga_LanzaExcepcion() {
        // Arrange
        List<String> etiquetas = List.of("etiquetamuylargarealmentelajar");  // 30 caracteres

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customTareaDetailsService.validarEtiquetas(etiquetas);
        });
        assertTrue(exception.getMessage().contains("supera los 20 caracteres"));
    }

    // ========== CP05: Validación de fechas - Fecha final antes que inicial ==========
    @Test
    @DisplayName("CP05: Validar cronología de fechas - fecha final antes de inicial")
    void CP05_crearTarea_ConCronologiaInvalida_LanzaExcepcion() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Inválida");
        tarea.setFechaInicio(LocalDateTime.now().plusDays(10));
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        tarea.setDificultad(Tarea.Dificultad.MEDIA);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            customTareaDetailsService.crearTarea(tarea, trabajo.getId());
        });
    }

    // ========== CP06: Validación de fechas - Fecha final igual a inicial ==========
    @Test
    @DisplayName("CP06: Validar que fecha final igual a inicial es permitido")
    void CP06_crearTarea_ConFechasIguales_PermiteCreacion() {
        // Arrange
        LocalDateTime momento = LocalDateTime.now();
        Tarea tarea = new Tarea();
        tarea.setNombre("Hito Instantáneo");
        tarea.setFechaInicio(momento);
        tarea.setFechaFinal(momento);
        tarea.setDificultad(Tarea.Dificultad.SIMPLE);

        // Act
        Tarea resultado = customTareaDetailsService.crearTarea(tarea, trabajo.getId());

        // Assert
        assertNotNull(resultado.getId());
        assertEquals(momento, resultado.getFechaInicio());
        assertEquals(momento, resultado.getFechaFinal());
    }

    // ========== CP07: Editar tarea con nuevas etiquetas ==========
    @Test
    @DisplayName("CP07: Editar tarea actualizando etiquetas")
    void CP07_editarTarea_ConNuevasEtiquetas_ActualizaEtiquetas() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Original");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(3));
        Tarea guardada = customTareaDetailsService.crearTarea(tarea, trabajo.getId());

        Tarea tareaActualizada = new Tarea();
        tareaActualizada.setNombre("Tarea Modificada");
        tareaActualizada.setFechaInicio(LocalDateTime.now());
        tareaActualizada.setFechaFinal(LocalDateTime.now().plusDays(5));
        tareaActualizada.setDificultad(Tarea.Dificultad.ALTA);

        List<String> nuevasEtiquetas = List.of("importante", "revisión");

        // Act
        Tarea resultado = customTareaDetailsService.editarTarea(
                guardada.getId(), tareaActualizada, null, nuevasEtiquetas
        );

        // Assert
        assertEquals("Tarea Modificada", resultado.getNombre());
        assertEquals(2, resultado.getEtiquetas().size());
        assertTrue(resultado.getEtiquetas().contains("importante"));
    }

    // ========== CP08: Toggle Completada ==========
    @Test
    @DisplayName("CP08: Toggle estado de completada (false -> true)")
    void CP08_toggleCompletada_DefalsoAVerdadero() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Para Completar");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(1));
        Tarea guardada = customTareaDetailsService.crearTarea(tarea, trabajo.getId());
        assertFalse(guardada.getIsCompletada());

        // Act
        customTareaDetailsService.toggleCompletada(guardada.getId());
        Tarea actualizada = customTareaDetailsService.findById(guardada.getId());

        // Assert
        assertTrue(actualizada.getIsCompletada());
    }

    // ========== CP09: Calcular progreso del trabajo ==========
    @Test
    @DisplayName("CP09: Calcular progreso de trabajo con tareas completadas")
    void CP09_calcularProgreso_ConTareasCompletadas_CalculaProgresoCorrectamente() {
        // Arrange - Crear 2 tareas: una SIMPLE (peso 1) completada, una MEDIA (peso 2) sin completar
        Tarea tarea1 = new Tarea();
        tarea1.setNombre("Tarea Simple");
        tarea1.setFechaInicio(LocalDateTime.now());
        tarea1.setFechaFinal(LocalDateTime.now().plusDays(1));
        tarea1.setDificultad(Tarea.Dificultad.SIMPLE);
        Tarea guardada1 = customTareaDetailsService.crearTarea(tarea1, trabajo.getId());
        customTareaDetailsService.toggleCompletada(guardada1.getId());

        Tarea tarea2 = new Tarea();
        tarea2.setNombre("Tarea Media");
        tarea2.setFechaInicio(LocalDateTime.now());
        tarea2.setFechaFinal(LocalDateTime.now().plusDays(2));
        tarea2.setDificultad(Tarea.Dificultad.MEDIA);
        customTareaDetailsService.crearTarea(tarea2, trabajo.getId());

        // Act
        // Progreso: 1/(1+2) * 100 = 33.33%
        int progreso = customTareaDetailsService.calcularProgreso(trabajo.getId());

        // Assert
        assertEquals(33, progreso);
    }

    // ========== CP10: Normalizar etiquetas (eliminar duplicados y espacios) ==========
    @Test
    @DisplayName("CP10: Normalizar etiquetas elimina duplicados y espacios")
    void CP10_validarEtiquetas_EliminaDuplicadosYEspacios() {
        // Arrange
        List<String> etiquetas = List.of("backend", "  backend  ", "frontend", "", null);

        // Act
        List<String> normalizadas = customTareaDetailsService.validarEtiquetas(etiquetas);

        // Assert
        assertEquals(2, normalizadas.size());
        assertTrue(normalizadas.contains("backend"));
        assertTrue(normalizadas.contains("frontend"));
    }
}
