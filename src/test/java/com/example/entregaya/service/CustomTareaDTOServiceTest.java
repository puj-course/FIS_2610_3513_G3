package com.example.entregaya.service;

import com.example.entregaya.dto.TareaCrearDTO;
import com.example.entregaya.dto.TareaEditarDTO;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomTareaDetailsService - Tests métodos DTO")
class CustomTareaDTOServiceTest {

    @Autowired
    private CustomTareaDetailsService tareaService;

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private TrabajoRepository trabajoRepository;

    @Autowired
    private UserRepository userRepository;

    private Trabajo trabajo;
    private User usuario;
    private String username;

    @BeforeEach
    void setUp() {
        username = "user_tarea_dto_" + System.nanoTime();
        userDetailsService.register(username, "pass123456", username + "@test.com");
        usuario = userRepository.findByUsername(username).orElseThrow();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Trabajo Test " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoService.crearTrabajo(trabajo, username);
    }

    // ── crearTareaDesdeDTO ──

    // CP01 - NORMAL: crearTareaDesdeDTO crea tarea con todos los campos.
    // Entrada: DTO con nombre, descripción, fechas y dificultad
    // Resultado esperado: tarea persistida con datos del DTO
    @Test
    @DisplayName("CP01: crearTareaDesdeDTO crea tarea con todos los campos correctamente")
    void CP01_crearTareaDesdeDTO_ConDatosValidos_CreaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea desde DTO");
        dto.setDescripcion("Descripción DTO");
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFinal(LocalDateTime.now().plusDays(7));
        dto.setDificultad(Tarea.Dificultad.ALTA);

        tareaService.crearTareaDesdeDTO(dto, trabajo.getId(), null, null);

        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajo.getId());
        assertEquals(1, tareas.size());
        assertEquals("Tarea desde DTO", tareas.get(0).getNombre());
        assertEquals("Descripción DTO", tareas.get(0).getDescripcion());
        assertEquals(Tarea.Dificultad.ALTA, tareas.get(0).getDificultad());
    }

    // CP02 - NORMAL: crearTareaDesdeDTO sin dificultad usa MEDIA por defecto.
    // Entrada: DTO con dificultad null
    // Resultado esperado: tarea creada con dificultad MEDIA
    @Test
    @DisplayName("CP02: crearTareaDesdeDTO con dificultad null usa MEDIA por defecto")
    void CP02_crearTareaDesdeDTO_SinDificultad_UsaMediaPorDefecto() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Sin Dificultad " + System.nanoTime());
        dto.setDificultad(null);

        tareaService.crearTareaDesdeDTO(dto, trabajo.getId(), null, null);

        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajo.getId());
        assertEquals(Tarea.Dificultad.MEDIA, tareas.get(0).getDificultad());
    }

    // CP03 - NORMAL: crearTareaDesdeDTO con responsables los asigna correctamente.
    // Entrada: DTO + lista con ID de usuario
    // Resultado esperado: tarea creada con responsable asignado
    @Test
    @DisplayName("CP03: crearTareaDesdeDTO con responsables los asigna correctamente")
    void CP03_crearTareaDesdeDTO_ConResponsables_AsignaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Con Responsable " + System.nanoTime());
        dto.setDificultad(Tarea.Dificultad.SIMPLE);

        tareaService.crearTareaDesdeDTO(dto, trabajo.getId(), List.of(usuario.getId()), null);

        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajo.getId());
        assertEquals(1, tareas.get(0).getResponsables().size());
    }

    // CP04 - NORMAL: crearTareaDesdeDTO con etiquetas válidas las asigna.
    // Entrada: DTO + lista de etiquetas válidas
    // Resultado esperado: tarea con etiquetas asignadas
    @Test
    @DisplayName("CP04: crearTareaDesdeDTO con etiquetas las asigna correctamente")
    void CP04_crearTareaDesdeDTO_ConEtiquetas_AsignaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Con Etiquetas " + System.nanoTime());
        dto.setDificultad(Tarea.Dificultad.MEDIA);

        tareaService.crearTareaDesdeDTO(dto, trabajo.getId(), null, List.of("urgente", "backend"));

        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajo.getId());
        assertEquals(2, tareas.get(0).getEtiquetas().size());
        assertTrue(tareas.get(0).getEtiquetas().contains("urgente"));
    }

    // CP05 - NEGATIVA: crearTareaDesdeDTO con trabajoId inexistente lanza excepción.
    // Entrada: ID de trabajo que no existe
    // Resultado esperado: RuntimeException
    @Test
    @DisplayName("CP05: crearTareaDesdeDTO con trabajoId inexistente lanza excepción")
    void CP05_crearTareaDesdeDTO_TrabajoInexistente_LanzaExcepcion() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Inexistente");
        dto.setDificultad(Tarea.Dificultad.SIMPLE);

        assertThrows(RuntimeException.class, () ->
                tareaService.crearTareaDesdeDTO(dto, 99999L, null, null)
        );
    }

    // ── editarTareaDesdeDTO ──

    // CP06 - NORMAL: editarTareaDesdeDTO actualiza todos los campos.
    // Entrada: tarea existente + DTO con nuevos valores
    // Resultado esperado: tarea actualizada en BD
    @Test
    @DisplayName("CP06: editarTareaDesdeDTO actualiza todos los campos correctamente")
    void CP06_editarTareaDesdeDTO_ConDatosValidos_ActualizaCorrectamente() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Original");
        tarea.setDificultad(Tarea.Dificultad.SIMPLE);
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Actualizada");
        dto.setDescripcion("Nueva descripción");
        dto.setDificultad(Tarea.Dificultad.ALTA);
        dto.setCompletada(true);
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFinal(LocalDateTime.now().plusDays(5));
        dto.setEtiquetas(new ArrayList<>(List.of("frontend")));

        tareaService.editarTareaDesdeDTO(guardada.getId(), dto);

        Tarea actualizada = tareaService.findById(guardada.getId());
        assertEquals("Tarea Actualizada", actualizada.getNombre());
        assertEquals("Nueva descripción", actualizada.getDescripcion());
        assertEquals(Tarea.Dificultad.ALTA, actualizada.getDificultad());
        assertTrue(actualizada.getIsCompletada());
        assertEquals(1, actualizada.getEtiquetas().size());
    }

    // CP07 - NORMAL: editarTareaDesdeDTO con responsables los actualiza.
    // Entrada: DTO con lista de responsablesIds
    // Resultado esperado: responsables actualizados en la tarea
    @Test
    @DisplayName("CP07: editarTareaDesdeDTO con responsables los actualiza correctamente")
    void CP07_editarTareaDesdeDTO_ConResponsables_ActualizaCorrectamente() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Responsables " + System.nanoTime());
        tarea.setDificultad(Tarea.Dificultad.MEDIA);
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Con Responsables");
        dto.setDificultad(Tarea.Dificultad.MEDIA);
        dto.setResponsablesIds(List.of(usuario.getId()));

        tareaService.editarTareaDesdeDTO(guardada.getId(), dto);

        Tarea actualizada = tareaService.findById(guardada.getId());
        assertEquals(1, actualizada.getResponsables().size());
    }

    // CP08 - NORMAL: editarTareaDesdeDTO con responsablesIds vacíos limpia responsables.
    // Entrada: DTO con lista de responsablesIds vacía
    // Resultado esperado: responsables vaciados
    @Test
    @DisplayName("CP08: editarTareaDesdeDTO con responsablesIds vacíos limpia responsables")
    void CP08_editarTareaDesdeDTO_ResponsablesVacios_LimpiaResponsables() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Sin Resp " + System.nanoTime());
        tarea.setDificultad(Tarea.Dificultad.SIMPLE);
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId(), List.of(usuario.getId()));

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Sin Responsables");
        dto.setDificultad(Tarea.Dificultad.SIMPLE);
        dto.setResponsablesIds(List.of()); // lista vacía

        tareaService.editarTareaDesdeDTO(guardada.getId(), dto);

        Tarea actualizada = tareaService.findById(guardada.getId());
        assertTrue(actualizada.getResponsables().isEmpty());
    }

    // CP09 - NORMAL: editarTareaDesdeDTO con etiquetas nulas usa lista vacía.
    // Entrada: DTO con etiquetas = null
    // Resultado esperado: tarea sin etiquetas
    @Test
    @DisplayName("CP09: editarTareaDesdeDTO con etiquetas nulas usa lista vacía")
    void CP09_editarTareaDesdeDTO_EtiquetasNulas_UsaListaVacia() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Etiq Nulas " + System.nanoTime());
        tarea.setDificultad(Tarea.Dificultad.MEDIA);
        Tarea guardada = tareaService.crearTarea(tarea, trabajo.getId());

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Nombre OK");
        dto.setDificultad(Tarea.Dificultad.MEDIA);
        dto.setEtiquetas(null);

        tareaService.editarTareaDesdeDTO(guardada.getId(), dto);

        Tarea actualizada = tareaService.findById(guardada.getId());
        assertTrue(actualizada.getEtiquetas().isEmpty());
    }

    // CP10 - NEGATIVA: editarTareaDesdeDTO con ID inexistente lanza excepción.
    // Entrada: ID que no existe en BD
    // Resultado esperado: IllegalArgumentException
    @Test
    @DisplayName("CP10: editarTareaDesdeDTO con ID inexistente lanza excepción")
    void CP10_editarTareaDesdeDTO_IdInexistente_LanzaExcepcion() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("No importa");
        dto.setDificultad(Tarea.Dificultad.SIMPLE);

        assertThrows(IllegalArgumentException.class, () ->
                tareaService.editarTareaDesdeDTO(99999L, dto)
        );
    }
}
