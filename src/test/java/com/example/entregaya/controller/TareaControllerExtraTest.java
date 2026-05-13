package com.example.entregaya.controller;

import com.example.entregaya.decorator.SinFechaDecorator;
import com.example.entregaya.dto.TareaCrearDTO;
import com.example.entregaya.model.*;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomComentarioDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.example.entregaya.dto.TareaEditarDTO;

/**
 * Tests adicionales para cobertura de TareaController (ramas no cubiertas)
 * y clases de modelos/decoradores con baja cobertura.
 */
class TareaControllerExtraTest {

    private TareaController tareaController;
    private TareaRepository tareaRepository;
    private CustomTareaDetailsService customTareaDetailsService;
    private CustomTrabajoDetailsService customTrabajoDetailsService;
    private CustomComentarioDetailsService customComentarioDetailsService;
    private UserRepository userRepository;
    private Lideroeditorstrategy lideroeditorstrategy;

    private UserDetails userDetails;
    private Tarea tareaBase;
    private Trabajo trabajoBase;

    @BeforeEach
    void setUp() {
        tareaRepository = Mockito.mock(TareaRepository.class);
        customTareaDetailsService = Mockito.mock(CustomTareaDetailsService.class);
        customTrabajoDetailsService = Mockito.mock(CustomTrabajoDetailsService.class);
        customComentarioDetailsService = Mockito.mock(CustomComentarioDetailsService.class);
        userRepository = Mockito.mock(UserRepository.class);
        lideroeditorstrategy = Mockito.mock(Lideroeditorstrategy.class);

        tareaController = new TareaController(
                tareaRepository, customTareaDetailsService, customTrabajoDetailsService,
                customComentarioDetailsService, userRepository, lideroeditorstrategy
        );

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("lider").password("pass").roles("USER").build();

        tareaBase = new Tarea();
        tareaBase.setId(1L);
        tareaBase.setNombre("Tarea Test");
        tareaBase.setCompletada(false);
        tareaBase.setDificultad(Tarea.Dificultad.SIMPLE);
        tareaBase.setEtiquetas(new ArrayList<>());
        tareaBase.setResponsables(new HashSet<>());

        trabajoBase = new Trabajo();
        trabajoBase.setId(1L);
        trabajoBase.setNombreTrabajo("Trabajo Test");
        trabajoBase.setColaboradores(new HashSet<>());
    }

    // ---- guardar (POST /nueva) - rama de excepción ----

    @Test
    void guardar_IllegalArgumentException_RedireccionaAFormularioNuevo() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Test");
        Mockito.doThrow(new IllegalArgumentException("nombre duplicado"))
                .when(customTareaDetailsService)
                .crearTareaDesdeDTO(Mockito.any(), Mockito.eq(1L), Mockito.any(), Mockito.any());

        String resultado = tareaController.guardar(1L, dto, null, null, ra);

        Assertions.assertEquals("redirect:/trabajos/1/tareas/nuevo", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }

    // ---- guardarEdicion (POST /{tareaId}/editar) - rama sin permiso y excepción ----

    @Test
    void guardarEdicion_SinPermiso_RedireccionaConError() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Test");
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(false);

        String resultado = tareaController.guardarEdicion(1L, 1L, dto, null, null, userDetails, ra);

        Assertions.assertEquals("redirect:/trabajos/1?error=noPermiso", resultado);
    }

    @Test
    void guardarEdicion_IllegalArgumentException_RedireccionaAEditar() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Test");
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);
        Mockito.doThrow(new IllegalArgumentException("datos inválidos"))
                .when(customTareaDetailsService).editarTareaDesdeDTO(Mockito.eq(1L), Mockito.any());

        String resultado = tareaController.guardarEdicion(1L, 1L, dto, null, null, userDetails, ra);

        Assertions.assertEquals("redirect:/trabajos/1/tareas/1/editar", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }


    @Test
    void guardarEdicion_Exitoso_RedireccionaAlTrabajo() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea Test");
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);

        String resultado = tareaController.guardarEdicion(1L, 1L, dto, null, null, userDetails, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
    }

    // ---- editarComentario ----

    @Test
    void editarComentario_ContenidoVacio_Retorna400() {
        Map<String, String> body = Map.of("contenido", "");

        ResponseEntity<Map<String, Object>> resp =
                tareaController.editarComentario(1L, 1L, 1L, body, userDetails);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void editarComentario_Exitoso_Retorna200() {
        Map<String, String> body = Map.of("contenido", "Contenido editado");
        Mockito.when(customComentarioDetailsService
                .editarComentario(1L, "Contenido editado", "lider"))
                .thenReturn(new Comentario());

        ResponseEntity<Map<String, Object>> resp =
                tareaController.editarComentario(1L, 1L, 1L, body, userDetails);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("Contenido editado", resp.getBody().get("contenido"));
    }

    @Test
    void editarComentario_IllegalArgument_Retorna403() {
        Map<String, String> body = Map.of("contenido", "Texto");
        Mockito.doThrow(new IllegalArgumentException("no es el autor"))
                .when(customComentarioDetailsService).editarComentario(1L, "Texto", "lider");

        ResponseEntity<Map<String, Object>> resp =
                tareaController.editarComentario(1L, 1L, 1L, body, userDetails);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    // ---- eliminarComentario ----

    @Test
    void eliminarComentario_Exitoso_Retorna200() {
        Mockito.doNothing().when(customComentarioDetailsService)
                .eliminarComentario(1L, "lider");

        ResponseEntity<Map<String, Object>> resp =
                tareaController.eliminarComentario(1L, 1L, 1L, userDetails);

        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void eliminarComentario_IllegalArgument_Retorna403() {
        Mockito.doThrow(new IllegalArgumentException("no es el autor"))
                .when(customComentarioDetailsService).eliminarComentario(1L, "lider");

        ResponseEntity<Map<String, Object>> resp =
                tareaController.eliminarComentario(1L, 1L, 1L, userDetails);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    // ---- clonarTarea ----

    @Test
    void clonarTarea_Exitoso_Retorna201() {
        Tarea clon = new Tarea();
        clon.setId(99L);
        clon.setNombre("Tarea Test (copia)");
        clon.setDescripcion("desc");
        clon.setDificultad(Tarea.Dificultad.SIMPLE);

        Mockito.when(customTareaDetailsService.clonarTarea(1L, 1L, "lider")).thenReturn(clon);

        ResponseEntity<Map<String, Object>> resp =
                tareaController.clonarTarea(1L, 1L, userDetails);

        Assertions.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody().get("id"));
    }

    @Test
    void clonarTarea_SecurityException_Retorna403() {
        Mockito.when(customTareaDetailsService.clonarTarea(1L, 1L, "lider"))
                .thenThrow(new SecurityException("sin permisos"));

        ResponseEntity<Map<String, Object>> resp =
                tareaController.clonarTarea(1L, 1L, userDetails);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    // ====== MODELOS CON BAJA COBERTURA ======

    // ---- SinFechaDecorator ----

    @Test
    void sinFechaDecorator_GetEtiquetaUrgencia_RetornaSinFecha() {
        SinFechaDecorator decorator = new SinFechaDecorator(tareaBase);
        Assertions.assertEquals("Sin fecha", decorator.getEtiquetaUrgencia());
    }

    @Test
    void sinFechaDecorator_GetColorEtiqueta_RetornaGris() {
        SinFechaDecorator decorator = new SinFechaDecorator(tareaBase);
        Assertions.assertEquals("#6b7280", decorator.getColorEtiqueta());
    }

    @Test
    void sinFechaDecorator_GetNombre_PropagaAlBase() {
        SinFechaDecorator decorator = new SinFechaDecorator(tareaBase);
        Assertions.assertEquals("Tarea Test", decorator.getNombre());
    }

    // ---- ColaboradorTrabajoId ----

    @Test
    void colaboradorTrabajoId_Equals_MismosIds_EsIgual() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        ColaboradorTrabajoId id2 = new ColaboradorTrabajoId(1L, 2L);
        Assertions.assertEquals(id1, id2);
    }

    @Test
    void colaboradorTrabajoId_Equals_DiferentesIds_NoEsIgual() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        ColaboradorTrabajoId id2 = new ColaboradorTrabajoId(1L, 3L);
        Assertions.assertNotEquals(id1, id2);
    }

    @Test
    void colaboradorTrabajoId_Equals_MismaInstancia_EsIgual() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        Assertions.assertEquals(id1, id1);
    }

    @Test
    void colaboradorTrabajoId_Equals_TipoDistinto_NoEsIgual() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        Assertions.assertNotEquals(id1, "otro tipo");
    }

    @Test
    void colaboradorTrabajoId_HashCode_MismosIds_MismoHash() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        ColaboradorTrabajoId id2 = new ColaboradorTrabajoId(1L, 2L);
        Assertions.assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void colaboradorTrabajoId_GettersSetters_FuncionanCorrectamente() {
        ColaboradorTrabajoId id = new ColaboradorTrabajoId();
        id.setTrabajoId(5L);
        id.setUserId(10L);
        Assertions.assertEquals(5L, id.getTrabajoId());
        Assertions.assertEquals(10L, id.getUserId());
    }

    // ---- Notificacion ----

    @Test
    void notificacion_ConstructorConTipo_CreaCorrectamente() {
        User u = new User();
        u.setId(1L);
        u.setUsername("user");
        Notificacion n = new Notificacion(u, "Mensaje test", Notificacion.TipoNotificacion.MIEMBRO);
        Assertions.assertEquals("Mensaje test", n.getMensaje());
        Assertions.assertEquals(Notificacion.TipoNotificacion.MIEMBRO, n.getTipo());
        Assertions.assertFalse(n.isLeida());
        Assertions.assertNotNull(n.getFechaCreacion());
    }

    @Test
    void notificacion_ConstructorDefault_TipoTarea() {
        User u = new User();
        Notificacion n = new Notificacion(u, "msg");
        Assertions.assertEquals(Notificacion.TipoNotificacion.TAREA, n.getTipo());
    }

    @Test
    void notificacion_SetLeida_CambiaEstado() {
        Notificacion n = new Notificacion();
        n.setLeida(true);
        Assertions.assertTrue(n.isLeida());
    }

    @Test
    void notificacion_TipoRecordatorioVencimiento_ExisteEnEnum() {
        Notificacion.TipoNotificacion tipo = Notificacion.TipoNotificacion.RECORDATORIO_VENCIMIENTO;
        Assertions.assertNotNull(tipo);
    }

    @Test
    void notificacion_SetFechaCreacion_CambiaFecha() {
        Notificacion n = new Notificacion();
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 1, 0, 0);
        n.setFechaCreacion(fecha);
        Assertions.assertEquals(fecha, n.getFechaCreacion());
    }

    // ---- User model ----

    @Test
    void user_Equals_MismoId_EsIgual() {
        User u1 = new User(1L, "user1", "pass");
        User u2 = new User(1L, "user2", "pass2");
        Assertions.assertEquals(u1, u2);
    }

    @Test
    void user_Equals_IdNulo_NoEsIgual() {
        User u1 = new User();
        User u2 = new User();
        Assertions.assertNotEquals(u1, u2);
    }

    @Test
    void user_Equals_OtroTipo_NoEsIgual() {
        User u1 = new User(1L, "user", "pass");
        Assertions.assertNotEquals(u1, "string");
    }

    @Test
    void user_HashCode_ConId_Funciona() {
        User u = new User(1L, "user", "pass");
        Assertions.assertNotEquals(0, u.hashCode());
    }

    @Test
    void user_TelegramChatId_GetterSetter() {
        User u = new User();
        u.setTelegramChatId("12345");
        Assertions.assertEquals("12345", u.getTelegramChatId());
    }

    @Test
    void user_Email_GetterSetter() {
        User u = new User();
        u.setEmail("test@example.com");
        Assertions.assertEquals("test@example.com", u.getEmail());
    }

    // ---- ColaboradorTrabajo ----

    @Test
    void colaboradorTrabajo_RolColaborador_PorDefecto() {
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        Assertions.assertEquals(ColaboradorTrabajo.Rol.COLABORADOR, col.getRol());
    }

    @Test
    void colaboradorTrabajo_SetRol_CambiaRol() {
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setRol(ColaboradorTrabajo.Rol.EDITOR);
        Assertions.assertEquals(ColaboradorTrabajo.Rol.EDITOR, col.getRol());
    }

    // ---- Trabajo.agregarColaborador ----

    @Test
    void trabajo_AgregarColaborador_NoDuplica() {
        User u = new User(1L, "user", "pass");
        u.setId(1L);
        trabajoBase.setId(1L);
        trabajoBase.agregarColaborador(u, ColaboradorTrabajo.Rol.COLABORADOR);
        int antes = trabajoBase.getColaboradores().size();
        trabajoBase.agregarColaborador(u, ColaboradorTrabajo.Rol.EDITOR); // no debe duplicar
        Assertions.assertEquals(antes, trabajoBase.getColaboradores().size());
    }

    @Test
    void trabajo_AgregarColaborador_RolPorDefecto() {
        User u = new User(1L, "user", "pass");
        u.setId(1L);
        trabajoBase.setId(1L);
        trabajoBase.agregarColaborador(u); // sobrecarga sin rol
        Assertions.assertEquals(1, trabajoBase.getColaboradores().size());
    }
    @Test
    @DisplayName("CP_NEW_01: guardarEdicion() con responsableIds y etiquetas no nulos redirige al trabajo")
    void guardarEdicion_ConResponsablesYEtiquetasNoNulos_RedireccionaAlTrabajo() {
        // Arrange
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea con datos reales");
        dto.setDificultad(com.example.entregaya.model.Tarea.Dificultad.ALTA);

        List<Long>   responsableIds = List.of(10L, 20L);   // <-- no nulo
        List<String> etiquetas      = List.of("urgente");  // <-- no nulo

        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = tareaController.guardarEdicion(
                1L, 10L, dto, responsableIds, etiquetas, userDetails, ra);

        // Assert: la rama (!=null ? lista : List.of()) se ejecuta con la lista real.
        // El servicio está mockeado, por lo tanto solo verificamos la redirección.
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "Debe redirigir al trabajo tras guardar correctamente");

        // Verificar que el DTO recibió los valores reales (no List.of())
        Assertions.assertEquals(responsableIds, dto.getResponsablesIds());
        Assertions.assertEquals(etiquetas,      dto.getEtiquetas());
    }
    @Test
    @DisplayName("CP_NEW_02: guardarEdicion() responsableIds nulo y etiquetas no nulas")
    void guardarEdicion_ResponsablesNulosYEtiquetasNoNulas_RedireccionaAlTrabajo() {
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);

        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setNombre("Tarea mezcla");
        dto.setDificultad(com.example.entregaya.model.Tarea.Dificultad.MEDIA);

        List<String> etiquetas = List.of("backend", "api");  // no nulo

        RedirectAttributes ra = new RedirectAttributesModelMap();

        String resultado = tareaController.guardarEdicion(
                1L, 10L, dto, null, etiquetas, userDetails, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
        // responsableIds era null → DTO debe tener List.of()
        Assertions.assertTrue(dto.getResponsablesIds().isEmpty());
        // etiquetas era no nulo → DTO debe tener la lista real
        Assertions.assertEquals(etiquetas, dto.getEtiquetas());
    }
}
