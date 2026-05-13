package com.example.entregaya.controller;

import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.PdfExportService;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDateTime;
import java.util.*;
import com.example.entregaya.dto.TrabajoEditarDTO;

/**
 * Tests adicionales para aumentar cobertura de TrabajoController.
 * Cubre ramas de error y casos especiales no cubiertos por TrabajoControllerTest.
 */
class TrabajoControllerExtraTest {

    private TrabajoController trabajoController;
    private CustomTrabajoDetailsService customTrabajoDetailsService;
    private CustomTareaDetailsService customTareaDetailsService;
    private CustomInvitacionDetailsService customInvitacionDetailsService;
    private PdfExportService pdfExportService;
    private Lideroeditorstrategy lideroeditorstrategy;

    private Trabajo trabajoBase;
    private UserDetails userLider;
    private UserDetails userColaborador;

    @BeforeEach
    void setUp() {
        customTrabajoDetailsService    = Mockito.mock(CustomTrabajoDetailsService.class);
        customTareaDetailsService      = Mockito.mock(CustomTareaDetailsService.class);
        customInvitacionDetailsService = Mockito.mock(CustomInvitacionDetailsService.class);
        pdfExportService               = Mockito.mock(PdfExportService.class);
        lideroeditorstrategy           = Mockito.mock(Lideroeditorstrategy.class);

        trabajoController = new TrabajoController(
                customTrabajoDetailsService,
                customTareaDetailsService,
                customInvitacionDetailsService,
                pdfExportService,
                lideroeditorstrategy
        );

        trabajoBase = new Trabajo();
        trabajoBase.setId(1L);
        trabajoBase.setNombreTrabajo("Trabajo Test");

        userLider = org.springframework.security.core.userdetails.User
                .withUsername("lider").password("pass").roles("USER").build();
        userColaborador = org.springframework.security.core.userdetails.User
                .withUsername("colaborador").password("pass").roles("USER").build();
    }

    // ---- mostrarFormularioEditar: ramas de error ----

    @Test
    void mostrarFormularioEditar_EsLider_RetornaVistaEditar() {
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);

        String resultado = trabajoController.mostrarFormularioEditar(1L, model, userLider, ra);

        Assertions.assertEquals("trabajos/editar", resultado);
    }

    @Test
    void mostrarFormularioEditar_NoEsLider_EsColaborador_RedireccionaConError() {
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Crear un colaborador en el trabajo que coincida con el usuario
        User u = new User();
        u.setId(2L);
        u.setUsername("colaborador");
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setUser(u);
        col.setRol(ColaboradorTrabajo.Rol.COLABORADOR);
        Set<ColaboradorTrabajo> cols = new HashSet<>();
        cols.add(col);
        trabajoBase.setColaboradores(cols);

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "colaborador")).thenReturn(false);

        String resultado = trabajoController.mostrarFormularioEditar(1L, model, userColaborador, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    @Test
    void mostrarFormularioEditar_NoEsLider_NoEsColaborador_RedireccionaConError() {
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Trabajo sin colaboradores → usuario no pertenece
        trabajoBase.setColaboradores(new HashSet<>());

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(false);

        String resultado = trabajoController.mostrarFormularioEditar(1L, model, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    @Test
    void mostrarFormularioEditar_RuntimeException_RedireccionaATrabajos() {
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("no existe"));

        String resultado = trabajoController.mostrarFormularioEditar(1L, model, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    // ---- actualizarTrabajo: ramas ----

    @Test
    void actualizarTrabajo_NoEsLider_RedireccionaConError() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("Nuevo nombre");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "colaborador")).thenReturn(false);

        String resultado = trabajoController.actualizarTrabajo(1L, editado, userColaborador, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    @Test
    void actualizarTrabajo_NombreNulo_RedireccionaAEditar() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo(null);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);

        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1/editar", resultado);
    }

    @Test
    void actualizarTrabajo_Exitoso_RedireccionaADetalle() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("Nombre Valido");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doNothing().when(customTrabajoDetailsService).actualizarTrabajoDesdeDTO(1L, editado);

        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("success"));
    }

    void actualizarTrabajo_IllegalArgumentException_RedireccionaAEditar() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("Nombre duplicado");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doThrow(new IllegalArgumentException("nombre duplicado"))
                .when(customTrabajoDetailsService).actualizarTrabajoDesdeDTO(1L, editado);

        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1/editar", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    @Test
    void actualizarTrabajo_GenericException_RedireccionaAEditar() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("Nombre OK");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doThrow(new RuntimeException("db error"))
                .when(customTrabajoDetailsService).actualizarTrabajoDesdeDTO(1L, editado);

        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1/editar", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    // ---- eliminarColaborador ----

    @Test
    void eliminarColaborador_Exitoso_RedireccionaConSuccess() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doNothing().when(customTrabajoDetailsService)
                .eliminarColaborador(1L, 2L, "lider");

        String resultado = trabajoController.eliminarColaborador(1L, 2L, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("success"));
    }

    @Test
    void eliminarColaborador_IllegalArgument_RedireccionaConError() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("no existe"))
                .when(customTrabajoDetailsService).eliminarColaborador(1L, 2L, "lider");

        String resultado = trabajoController.eliminarColaborador(1L, 2L, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    @Test
    void eliminarColaborador_GenericException_RedireccionaConError() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new RuntimeException("db error"))
                .when(customTrabajoDetailsService).eliminarColaborador(1L, 2L, "lider");

        String resultado = trabajoController.eliminarColaborador(1L, 2L, userLider, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    // ---- obtenerMiembrosConRoles ----

    @Test
    void obtenerMiembrosConRoles_TrabajoNull_Retorna404() {
        Mockito.when(customTrabajoDetailsService.obtenerPorId(99L)).thenReturn(null);

        ResponseEntity<Map<String, Object>> resp = trabajoController.obtenerMiembrosConRoles(99L);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void obtenerMiembrosConRoles_Exitoso_Retorna200ConDatos() {
        User u = new User();
        u.setId(1L);
        u.setUsername("lider");
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setUser(u);
        col.setRol(ColaboradorTrabajo.Rol.LIDER);
        Set<ColaboradorTrabajo> cols = new HashSet<>();
        cols.add(col);
        trabajoBase.setColaboradores(cols);

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        ResponseEntity<Map<String, Object>> resp = trabajoController.obtenerMiembrosConRoles(1L);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertTrue(resp.getBody().containsKey("miembros"));
    }

    @Test
    void obtenerMiembrosConRoles_Exception_Retorna500() {
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("db error"));

        ResponseEntity<Map<String, Object>> resp = trabajoController.obtenerMiembrosConRoles(1L);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // ---- actualizarRolMiembro ----

    @Test
    void actualizarRolMiembro_NoEsLider_Retorna403() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "colaborador")).thenReturn(false);
        Map<String, String> request = Map.of("rol", "EDITOR");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userColaborador);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void actualizarRolMiembro_RolNulo_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = new HashMap<>();
        request.put("rol", null);

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void actualizarRolMiembro_RolInvalido_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Map<String, String> request = Map.of("rol", "INVALIDO");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void actualizarRolMiembro_Exitoso_Retorna200() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doNothing().when(customTrabajoDetailsService)
                .cambiarRol(1L, 2L, ColaboradorTrabajo.Rol.EDITOR);
        Map<String, String> request = Map.of("rol", "EDITOR");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void actualizarRolMiembro_IllegalArgument_Retorna400() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doThrow(new IllegalArgumentException("usuario no pertenece"))
                .when(customTrabajoDetailsService).cambiarRol(1L, 2L, ColaboradorTrabajo.Rol.EDITOR);
        Map<String, String> request = Map.of("rol", "EDITOR");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void actualizarRolMiembro_GenericException_Retorna500() {
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.doThrow(new RuntimeException("db error"))
                .when(customTrabajoDetailsService).cambiarRol(1L, 2L, ColaboradorTrabajo.Rol.LIDER);
        Map<String, String> request = Map.of("rol", "LIDER");

        ResponseEntity<Map<String, Object>> resp =
                trabajoController.actualizarRolMiembro(1L, 2L, request, userLider);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // ---- calendarioTareas ----

    @Test
    void calendarioTareas_SinFechas_ExcluyeTareas() {
        Tarea tareasSinFecha = new Tarea();
        tareasSinFecha.setNombre("Sin fecha");
        tareasSinFecha.setFechaInicio(null);
        tareasSinFecha.setFechaFinal(null);
        tareasSinFecha.setCompletada(false);
        tareasSinFecha.setDificultad(Tarea.Dificultad.SIMPLE);

        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(List.of(tareasSinFecha));

        ResponseEntity<List<Map<String, Object>>> resp = trabajoController.calendarioTareas(1L);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertEquals(0, resp.getBody().size());
    }

    @Test
    void calendarioTareas_ConFechas_IncluyeTareas() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setNombre("Con fecha");
        tarea.setFechaInicio(LocalDateTime.now().minusDays(1));
        tarea.setFechaFinal(LocalDateTime.now().plusDays(1));
        tarea.setCompletada(false);
        tarea.setDificultad(Tarea.Dificultad.ALTA);

        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(List.of(tarea));

        ResponseEntity<List<Map<String, Object>>> resp = trabajoController.calendarioTareas(1L);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals(1, resp.getBody().size());
        Assertions.assertEquals(Boolean.FALSE, resp.getBody().get(0).get("vencida"));
    }

    @Test
    void calendarioTareas_TareaVencida_MarcaVencida() {
        Tarea tarea = new Tarea();
        tarea.setId(2L);
        tarea.setNombre("Vencida");
        tarea.setFechaInicio(LocalDateTime.now().minusDays(5));
        tarea.setFechaFinal(LocalDateTime.now().minusDays(1));
        tarea.setCompletada(false);
        tarea.setDificultad(Tarea.Dificultad.MEDIA);

        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(List.of(tarea));

        ResponseEntity<List<Map<String, Object>>> resp = trabajoController.calendarioTareas(1L);

        Assertions.assertEquals(Boolean.TRUE, resp.getBody().get(0).get("vencida"));
    }

    @Test
    void calendarioTareas_Exception_Retorna500() {
        Mockito.when(customTareaDetailsService.tareas(1L))
                .thenThrow(new RuntimeException("db error"));

        ResponseEntity<List<Map<String, Object>>> resp = trabajoController.calendarioTareas(1L);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    void calendarioTareas_SoloFechaFinal_IncluyeTarea() {
        Tarea tarea = new Tarea();
        tarea.setId(3L);
        tarea.setNombre("Solo final");
        tarea.setFechaInicio(null);
        tarea.setFechaFinal(LocalDateTime.now().plusDays(2));
        tarea.setCompletada(true);
        tarea.setDificultad(Tarea.Dificultad.SIMPLE);

        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(List.of(tarea));

        ResponseEntity<List<Map<String, Object>>> resp = trabajoController.calendarioTareas(1L);

        Assertions.assertEquals(1, resp.getBody().size());
        Assertions.assertEquals(Boolean.TRUE, resp.getBody().get(0).get("completada"));
    }

    // ---- clonar ----

    @Test
    void clonar_Exitoso_Retorna201() {
        Trabajo nuevo = new Trabajo();
        nuevo.setId(99L);
        nuevo.setNombreTrabajo("Trabajo Test (copia) ");
        Mockito.when(customTrabajoDetailsService.clonarTrabajo(1L, "lider")).thenReturn(nuevo);

        ResponseEntity<?> resp = trabajoController.clonar(1L, userLider);

        Assertions.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void clonar_SecurityException_Retorna403() {
        Mockito.when(customTrabajoDetailsService.clonarTrabajo(1L, "lider"))
                .thenThrow(new SecurityException("no es lider"));

        ResponseEntity<?> resp = trabajoController.clonar(1L, userLider);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void clonar_IllegalArgumentException_Retorna404() {
        Mockito.when(customTrabajoDetailsService.clonarTrabajo(1L, "lider"))
                .thenThrow(new IllegalArgumentException("no existe"));

        ResponseEntity<?> resp = trabajoController.clonar(1L, userLider);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    // ---- exportarPdf ----

    @Test
    void exportarPdf_SinPermisos_Retorna403() throws Exception {
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(false);

        ResponseEntity<byte[]> resp = trabajoController.exportarPdf(1L, userLider);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void exportarPdf_Exitoso_Retorna200() throws Exception {
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(new ArrayList<>());
        Mockito.when(pdfExportService.generarPdfTareas(Mockito.any(), Mockito.any()))
                .thenReturn(new byte[]{1, 2, 3});

        ResponseEntity<byte[]> resp = trabajoController.exportarPdf(1L, userLider);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
    }

    @Test
    void exportarPdf_Exception_Retorna500() throws Exception {
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("db error"));

        ResponseEntity<byte[]> resp = trabajoController.exportarPdf(1L, userLider);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // ---- verHistorial ----

    @Test
    void verHistorial_EsMiembro_RetornaVistaHistorial() {
        Model model = new ExtendedModelMap();
        User u = new User();
        u.setId(1L);
        u.setUsername("lider");
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setUser(u);
        col.setRol(ColaboradorTrabajo.Rol.LIDER);
        Set<ColaboradorTrabajo> cols = new HashSet<>();
        cols.add(col);
        trabajoBase.setColaboradores(cols);

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.obtenerHistorial(1L)).thenReturn(new ArrayList<>());

        String resultado = trabajoController.verHistorial(1L, model, userLider);

        Assertions.assertEquals("trabajos/historial", resultado);
    }

    @Test
    void verHistorial_NoEsMiembro_RedireccionaATrabajos() {
        Model model = new ExtendedModelMap();
        trabajoBase.setColaboradores(new HashSet<>());

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        String resultado = trabajoController.verHistorial(1L, model, userLider);

        Assertions.assertEquals("redirect:/trabajos", resultado);
    }

    @Test
    void verHistorial_GenericException_RedireccionaATrabajos() {
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("db error"));

        String resultado = trabajoController.verHistorial(1L, model, userLider);

        Assertions.assertEquals("redirect:/trabajos", resultado);
    }

    // ---- obtenerHistorialJson ----

    @Test
    void obtenerHistorialJson_EsMiembro_Retorna200() {
        User u = new User();
        u.setId(1L);
        u.setUsername("lider");
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setUser(u);
        col.setRol(ColaboradorTrabajo.Rol.LIDER);
        Set<ColaboradorTrabajo> cols = new HashSet<>();
        cols.add(col);
        trabajoBase.setColaboradores(cols);

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.obtenerHistorial(1L)).thenReturn(new ArrayList<>());

        ResponseEntity<?> resp = trabajoController.obtenerHistorialJson(1L, userLider);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void obtenerHistorialJson_NoEsMiembro_Retorna403() {
        trabajoBase.setColaboradores(new HashSet<>());
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        ResponseEntity<?> resp = trabajoController.obtenerHistorialJson(1L, userLider);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void obtenerHistorialJson_Exception_Retorna500() {
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("db error"));

        ResponseEntity<?> resp = trabajoController.obtenerHistorialJson(1L, userLider);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // ---- endpoints menores ----

    @Test
    void trabajosEspecificos_RetornaVistaEspecificos() {
        Model model = new ExtendedModelMap();

        String resultado = trabajoController.TrabajosEspecificos(model);

        Assertions.assertEquals("trabajos-especificos", resultado);
        Assertions.assertNotNull(model.asMap().get("trabajo"));
    }

    @Test
    void detallesPorId_RetornaVistaDetalle() {
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        String resultado = trabajoController.DetallesxId(1L, model);

        Assertions.assertEquals("trabajos/detalle", resultado);
    }
}
