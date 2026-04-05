package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.CustomComentarioDetailsService;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HU-22 — Subissue #272
 * Prueba del flujo completo desde el formulario real:
 *   - Escenario A: nombre vacío → error en formulario, sin redirigir a lista
 *   - Escenario B: fechas inconsistentes (final < inicio) → error en formulario
 *   - Escenario C: tarea válida → guardada correctamente, redirige al detalle del trabajo
 *
 * Capa probada: TareaController (MockMvc, sin base de datos).
 * El TareaBuilder lanza IllegalStateException que el controlador captura
 * y convierte en flash attribute "error" para mostrar en el formulario.
 */
@WebMvcTest(TareaController.class)
@DisplayName("HU-22 #272 — Flujo completo desde formulario real")
class TareaControllerFlujoCompletoTest {

    @Autowired
    private MockMvc mockMvc;

    // ── Dependencias del controlador ────────────────────────────────────────
    @MockitoBean
    private CustomTareaDetailsService customTareaDetailsService;

    @MockitoBean
    private CustomTrabajoDetailsService customTrabajoDetailsService;

    @MockitoBean
    private CustomComentarioDetailsService customComentarioDetailsService;

    @MockitoBean
    private TareaRepository tareaRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private Lideroeditorstrategy lideroeditorstrategy;

    // ── Datos de prueba ─────────────────────────────────────────────────────
    private static final long TRABAJO_ID = 1L;
    private static final long TAREA_ID   = 10L;

    private Trabajo trabajoMock;

    @BeforeEach
    void setUp() {
        trabajoMock = new Trabajo();
        trabajoMock.setId(TRABAJO_ID);
        trabajoMock.setNombre("Trabajo de prueba");

        when(customTrabajoDetailsService.obtenerPorId(TRABAJO_ID)).thenReturn(trabajoMock);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ESCENARIO A — Nombre vacío
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Al enviar el formulario con nombre vacío, el Builder lanza
     * IllegalStateException. El controlador debe:
     *   1. Capturar la excepción.
     *   2. Añadir el mensaje como flash attribute "error".
     *   3. Redirigir al formulario de creación (NO a /trabajos/{id}).
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("A — nombre vacío: redirige al formulario con mensaje de error")
    void crearTarea_nombreVacio_redirigeAlFormularioConError() throws Exception {

        // El Builder lanza excepción cuando nombre es vacío
        when(customTareaDetailsService.crearTarea(any(Tarea.class), eq(TRABAJO_ID), any()))
                .thenThrow(new IllegalStateException(
                        "El nombre de la tarea es obligatorio y no puede estar en blanco."));

        mockMvc.perform(post("/trabajos/{id}/tareas/nueva", TRABAJO_ID)
                        .with(csrf())
                        .param("nombre", "")                     // ← nombre vacío
                        .param("descripcion", "Descripción test")
                        .param("dificultad", "MEDIA"))
                // Debe redirigir al formulario, NO a /trabajos/1
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trabajos/" + TRABAJO_ID + "/tareas/CrearTarea"))
                // El flash attribute "error" debe estar presente con el mensaje del Builder
                .andExpect(flash().attributeExists("error"));

        // Verificar que el servicio fue llamado (intentó crear)
        verify(customTareaDetailsService).crearTarea(any(Tarea.class), eq(TRABAJO_ID), any());
    }

    /**
     * Tras el redirect, el formulario GET debe mostrar el mensaje de error
     * que vino como flash attribute.
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("A2 — formulario CrearTarea muestra el error recibido por flash")
    void formularioCrearTarea_muestraFlashError() throws Exception {

        Trabajo trabajoConColabs = new Trabajo();
        trabajoConColabs.setId(TRABAJO_ID);
        trabajoConColabs.setColaboradores(Set.of());
        when(customTrabajoDetailsService.obtenerPorId(TRABAJO_ID)).thenReturn(trabajoConColabs);

        mockMvc.perform(get("/trabajos/{id}/tareas/CrearTarea", TRABAJO_ID)
                        .flashAttr("error",
                                "El nombre de la tarea es obligatorio y no puede estar en blanco."))
                .andExpect(status().isOk())
                .andExpect(view().name("trabajos/tareas/CrearTarea"))
                // El modelo debe tener el atributo "error" para que Thymeleaf lo renderice
                .andExpect(model().attributeExists("error"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ESCENARIO B — Fechas inconsistentes (fechaFinal < fechaInicio)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Al enviar fechaFinal anterior a fechaInicio, el Builder lanza
     * IllegalStateException con mensaje sobre fechas. El controlador debe:
     *   1. Capturar la excepción.
     *   2. Añadir el mensaje como flash attribute "error".
     *   3. Redirigir al formulario de creación.
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("B — fechas inconsistentes: redirige al formulario con error de fechas")
    void crearTarea_fechasInconsistentes_redirigeAlFormularioConError() throws Exception {

        when(customTareaDetailsService.crearTarea(any(Tarea.class), eq(TRABAJO_ID), any()))
                .thenThrow(new IllegalStateException(
                        "La fecha final no puede ser anterior a la fecha de inicio."));

        mockMvc.perform(post("/trabajos/{id}/tareas/nueva", TRABAJO_ID)
                        .with(csrf())
                        .param("nombre", "Tarea con fechas cruzadas")
                        .param("fechaInicio", "2025-06-15T09:00")  // inicio posterior
                        .param("fechaFinal",  "2025-06-01T09:00")  // final anterior ← inválido
                        .param("dificultad", "ALTA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trabajos/" + TRABAJO_ID + "/tareas/CrearTarea"))
                .andExpect(flash().attributeExists("error"));

        verify(customTareaDetailsService).crearTarea(any(Tarea.class), eq(TRABAJO_ID), any());
    }

    /**
     * Mismo escenario B pero al EDITAR una tarea existente:
     * fechas inconsistentes → redirige al formulario de edición, no a la lista.
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("B2 — editar con fechas inconsistentes: redirige al formulario de edición con error")
    void editarTarea_fechasInconsistentes_redirigeAlFormularioConError() throws Exception {

        when(customTrabajoDetailsService.verificarPermiso(eq(TRABAJO_ID), eq("lider"), any()))
                .thenReturn(true);

        when(customTareaDetailsService.editarTarea(eq(TAREA_ID), any(Tarea.class), any()))
                .thenThrow(new IllegalStateException(
                        "La fecha final no puede ser anterior a la fecha de inicio."));

        mockMvc.perform(post("/trabajos/{id}/tareas/{tareaId}/editar", TRABAJO_ID, TAREA_ID)
                        .with(csrf())
                        .param("nombre", "Tarea editada")
                        .param("fechaInicio", "2025-08-20T10:00")
                        .param("fechaFinal",  "2025-08-01T10:00")  // ← inválido
                        .param("dificultad", "MEDIA"))
                .andExpect(status().is3xxRedirection())
                // Debe volver al formulario de edición, NO a /trabajos/1
                .andExpect(redirectedUrl(
                        "/trabajos/" + TRABAJO_ID + "/tareas/" + TAREA_ID + "/editar"))
                .andExpect(flash().attributeExists("error"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ESCENARIO C — Tarea válida
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Al enviar datos válidos (nombre presente, fechas consistentes),
     * el servicio guarda la tarea correctamente. El controlador debe:
     *   1. No lanzar ninguna excepción.
     *   2. Añadir flash attribute "success".
     *   3. Redirigir al detalle del trabajo (/trabajos/{id}).
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("C — tarea válida: se guarda y redirige al detalle del trabajo")
    void crearTarea_datosValidos_guardaYRedirigeAlTrabajo() throws Exception {

        // El servicio retorna la tarea guardada sin lanzar excepción
        Tarea tareaGuardada = new Tarea();
        tareaGuardada.setId(99L);
        tareaGuardada.setNombre("Diseño de interfaz");

        when(customTareaDetailsService.crearTarea(any(Tarea.class), eq(TRABAJO_ID), any()))
                .thenReturn(tareaGuardada);

        mockMvc.perform(post("/trabajos/{id}/tareas/nueva", TRABAJO_ID)
                        .with(csrf())
                        .param("nombre", "Diseño de interfaz")          // ← nombre válido
                        .param("descripcion", "Mockups en Figma")
                        .param("fechaInicio", "2025-06-01T09:00")
                        .param("fechaFinal",  "2025-06-15T18:00")       // ← fechas coherentes
                        .param("dificultad", "ALTA"))
                // Debe redirigir al detalle del trabajo, NO al formulario
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trabajos/" + TRABAJO_ID))
                .andExpect(flash().attributeExists("success"));

        verify(customTareaDetailsService).crearTarea(any(Tarea.class), eq(TRABAJO_ID), any());
    }

    /**
     * Tarea válida al EDITAR: datos correctos → redirige al trabajo con éxito.
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("C2 — editar tarea válida: se actualiza y redirige al detalle del trabajo")
    void editarTarea_datosValidos_actualizaYRedirigeAlTrabajo() throws Exception {

        when(customTrabajoDetailsService.verificarPermiso(eq(TRABAJO_ID), eq("lider"), any()))
                .thenReturn(true);

        Tarea tareaActualizada = new Tarea();
        tareaActualizada.setId(TAREA_ID);
        tareaActualizada.setNombre("Tarea actualizada");

        when(customTareaDetailsService.editarTarea(eq(TAREA_ID), any(Tarea.class), any()))
                .thenReturn(tareaActualizada);

        mockMvc.perform(post("/trabajos/{id}/tareas/{tareaId}/editar", TRABAJO_ID, TAREA_ID)
                        .with(csrf())
                        .param("nombre", "Tarea actualizada")            // ← válido
                        .param("descripcion", "Descripción actualizada")
                        .param("fechaInicio", "2025-07-01T08:00")
                        .param("fechaFinal",  "2025-07-10T17:00")        // ← fechas coherentes
                        .param("dificultad", "SIMPLE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trabajos/" + TRABAJO_ID))
                .andExpect(flash().attributeExists("success"));

        verify(customTareaDetailsService).editarTarea(eq(TAREA_ID), any(Tarea.class), any());
    }

    // ════════════════════════════════════════════════════════════════════════
    // CASO BORDE — nombre nulo (sin enviar el parámetro)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Si el parámetro nombre no se envía en absoluto (nulo),
     * el Builder también lanza IllegalStateException.
     * El controlador debe comportarse igual que en el Escenario A.
     */
    @Test
    @WithMockUser(username = "lider", roles = "USER")
    @DisplayName("A3 — nombre nulo (parámetro ausente): redirige al formulario con error")
    void crearTarea_nombreNulo_redirigeAlFormularioConError() throws Exception {

        when(customTareaDetailsService.crearTarea(any(Tarea.class), eq(TRABAJO_ID), any()))
                .thenThrow(new IllegalStateException(
                        "El nombre de la tarea es obligatorio y no puede estar en blanco."));

        mockMvc.perform(post("/trabajos/{id}/tareas/nueva", TRABAJO_ID)
                        .with(csrf())
                        // nombre no se incluye → llegará null al controlador
                        .param("dificultad", "MEDIA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trabajos/" + TRABAJO_ID + "/tareas/CrearTarea"))
                .andExpect(flash().attributeExists("error"));
    }
}
