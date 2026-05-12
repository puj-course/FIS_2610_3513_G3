package com.example.entregaya.controller;

import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.PdfExportService;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.example.entregaya.dto.TrabajoEditarDTO;

/**
 * HU-49 - Task #494
 * Pruebas unitarias JUnit 5 para TrabajoController.
 * El controlador se instancia directamente con todas sus
 * dependencias mockeadas. Se verifican nombres de vista y
 * redirecciones de: listar, crear, editar, eliminar y ver detalle.
 */
class TrabajoControllerTest {

    private TrabajoController trabajoController;
    private CustomTrabajoDetailsService customTrabajoDetailsService;
    private CustomTareaDetailsService   customTareaDetailsService;
    private CustomInvitacionDetailsService customInvitacionDetailsService;
    private PdfExportService pdfExportService;
    private Lideroeditorstrategy lideroeditorstrategy;

    /** Trabajo de prueba reutilizable entre tests. */
    private Trabajo trabajoBase;
    /** Usuario de prueba con rol LIDER. */
    private UserDetails userLider;

    @BeforeEach
    void setUp() {
        // Mocks de dependencias
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

        // Trabajo base con colecciones inicializadas
        trabajoBase = new Trabajo();
        trabajoBase.setNombreTrabajo("Trabajo Test");

        // UserDetails simulado
        userLider = org.springframework.security.core.userdetails.User
                .withUsername("lider")
                .password("pass")
                .roles("USER")
                .build();
    }

    // CP01 – GET /trabajos: retorna vista "trabajos/lista"
    // CP01 - NORMAL: listar trabajos retorna la vista correcta y agrega atributos al modelo.
    // Entrada: usuario autenticado con trabajos asociados
    // Resultado esperado: vista "trabajos/lista", atributos "trabajos" y "progresos" en modelo
    @Test
    void CP01_listar_RetornaVistaLista_ConAtributosModelo() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.listarPorUsuario("lider"))
                .thenReturn(List.of(trabajoBase));
        Mockito.when(customTareaDetailsService.calcularProgreso(Mockito.any()))
                .thenReturn(50);

        // Act
        String vista = trabajoController.trabajo(model, userLider);

        // Assert
        Assertions.assertEquals("trabajos/lista", vista,
                "listar() debe retornar la vista 'trabajos/lista'");
        Assertions.assertNotNull(model.asMap().get("trabajos"),
                "El modelo debe contener 'trabajos'");
        Assertions.assertNotNull(model.asMap().get("progresos"),
                "El modelo debe contener 'progresos'");
    }

    // CP02 – GET /trabajos/nuevo: retorna vista "trabajos/formulario"
    // CP02 - NORMAL: formulario() retorna vista de creación con objeto Trabajo en modelo.
    // Entrada: ninguna
    // Resultado esperado: vista "trabajos/formulario", atributo "trabajo" no nulo
    @Test
    void CP02_formulario_RetornaVistaFormulario_ConAtributoTrabajo() {
        // Arrange
        Model model = new ExtendedModelMap();

        // Act
        String vista = trabajoController.formulario(model);

        // Assert
        Assertions.assertEquals("trabajos/formulario", vista,
                "formulario() debe retornar la vista 'trabajos/formulario'");
        Assertions.assertNotNull(model.asMap().get("trabajo"),
                "El modelo debe contener el atributo 'trabajo'");
    }

    // CP03 – POST /trabajos/nuevo: redirige a /trabajos tras guardar
    // CP03 - NORMAL: guardar() redirige a /trabajos después de crear el trabajo.
    // Entrada: trabajo válido, usuario autenticado
    // Resultado esperado: "redirect:/trabajos"
    @Test
    void CP03_guardar_Exitoso_RedireccionaATrabajos() {
        // Act
        String resultado = trabajoController.guardar(trabajoBase, userLider);

        // Assert
        Assertions.assertEquals("redirect:/trabajos", resultado,
                "guardar() debe redirigir a '/trabajos'");
    }

    // CP04 – GET /{id}/editar con LIDER: retorna vista "trabajos/editar"
    // CP04 - NORMAL: mostrarFormularioEditar() con usuario LIDER retorna vista de edición.
    // Entrada: id=1, usuario con rol LIDER
    // Resultado esperado: vista "trabajos/editar", atributo "trabajo" en modelo
    @Test
    void CP04_mostrarFormularioEditar_SiEsLider_RetornaVistaEditar() {
        // Arrange
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);

        // Act
        String vista = trabajoController.mostrarFormularioEditar(1L, model, userLider, ra);

        // Assert
        Assertions.assertEquals("trabajos/editar", vista,
                "Un LIDER debe ver la vista 'trabajos/editar'");
        Assertions.assertNotNull(model.asMap().get("trabajo"),
                "El modelo debe contener 'trabajo'");
    }

    // CP05 – GET /{id}/editar sin permiso: redirige al detalle
    // CP05 - NEGATIVA: mostrarFormularioEditar() sin ser LIDER redirige al detalle.
    // Entrada: id=1, usuario que no es LIDER
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP05_mostrarFormularioEditar_SiNoEsLider_RedireccionaADetalle() {
        // Arrange
        Model model = new ExtendedModelMap();
        RedirectAttributes ra = new RedirectAttributesModelMap();

        Trabajo trabajoConColaborador = new Trabajo();
        com.example.entregaya.model.User userObj = new com.example.entregaya.model.User();
        userObj.setUsername("lider");
        ColaboradorTrabajo col = new ColaboradorTrabajo();
        col.setUser(userObj);
        col.setRol(ColaboradorTrabajo.Rol.COLABORADOR);
        trabajoConColaborador.getColaboradores().add(col);

        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoConColaborador);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(false);

        // Act
        String resultado = trabajoController.mostrarFormularioEditar(1L, model, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "Un no-LIDER debe ser redirigido al detalle del trabajo");
    }

    // CP06 – POST /{id}/editar con LIDER exitoso: redirige al detalle
    // CP06 - NORMAL: actualizarTrabajo() por LIDER con datos válidos redirige al detalle.
    // Entrada: id=1, trabajoEditado con nombre, usuario LIDER
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP06_actualizarTrabajo_SiEsLider_RedireccionaADetalle() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("Nombre Actualizado");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);

        // Act
        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "Actualizar con éxito debe redirigir al detalle del trabajo");
    }

    // CP07 – POST /{id}/eliminar: redirige a /trabajos
    // CP07 - NORMAL: eliminar() redirige a la lista de trabajos.
    // Entrada: id=1
    // Resultado esperado: "redirect:/trabajos"
    @Test
    void CP07_eliminar_RedireccionaATrabajos() {
        // Act
        String resultado = trabajoController.eliminar(1L);

        // Assert
        Assertions.assertEquals("redirect:/trabajos", resultado,
                "eliminar() debe redirigir a '/trabajos'");
        Mockito.verify(customTrabajoDetailsService).eliminar(1L);
    }

    // CP08 – GET /{id}: retorna vista "trabajos/detalle" con atributos
    // CP08 - NORMAL: detalle() retorna la vista de detalle con todos los atributos del modelo.
    // Entrada: id=1, usuario autenticado
    // Resultado esperado: vista "trabajos/detalle", atributos trabajo/tareas/progreso no nulos
    @Test
    void CP08_detalle_RetornaVistaDetalle_ConAtributosModelo() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);
        Mockito.when(customTrabajoDetailsService.puedeEditarTarea(1L, "lider")).thenReturn(true);
        Mockito.when(customTareaDetailsService.tareas(1L)).thenReturn(new ArrayList<>());
        Mockito.when(customTareaDetailsService.tareasConEtiquetas(1L)).thenReturn(new ArrayList<>());
        Mockito.when(customTareaDetailsService.calcularProgreso(1L)).thenReturn(0);
        Mockito.when(customInvitacionDetailsService.porTrabajo(1L)).thenReturn(new ArrayList<>());
        Mockito.when(customTrabajoDetailsService.consultarMiembros(1L)).thenReturn(new ArrayList<>());

        // Act
        String vista = trabajoController.detalle(1L, model, userLider);

        // Assert
        Assertions.assertEquals("trabajos/detalle", vista,
                "detalle() debe retornar la vista 'trabajos/detalle'");
        Assertions.assertNotNull(model.asMap().get("trabajo"),
                "El modelo debe contener 'trabajo'");
        Assertions.assertNotNull(model.asMap().get("tareas"),
                "El modelo debe contener 'tareas'");
        Assertions.assertNotNull(model.asMap().get("progreso"),
                "El modelo debe contener 'progreso'");
    }

    // CP09 – GET /{id}/miembros: retorna vista "trabajos/miembros"
    // CP09 - NORMAL: mostrarMiembros() retorna vista de miembros con atributos correctos.
    // Entrada: id=1
    // Resultado esperado: vista "trabajos/miembros", atributos "miembros" y "totalMiembros"
    @Test
    void CP09_mostrarMiembros_RetornaVistaMiembros_ConAtributos() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        // Act
        String vista = trabajoController.mostrarMiembros(1L, model);

        // Assert
        Assertions.assertEquals("trabajos/miembros", vista,
                "mostrarMiembros() debe retornar la vista 'trabajos/miembros'");
        Assertions.assertNotNull(model.asMap().get("miembros"),
                "El modelo debe contener 'miembros'");
        Assertions.assertNotNull(model.asMap().get("totalMiembros"),
                "El modelo debe contener 'totalMiembros'");
    }

    // CP10 – GET /{id}/calendario: retorna vista "trabajos/calendario"
    // CP10 - NORMAL: vistaCalendario() retorna la vista del calendario.
    // Entrada: id=1, usuario autenticado
    // Resultado esperado: vista "trabajos/calendario"
    @Test
    void CP10_vistaCalendario_RetornaVistaCalendario() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        // Act
        String vista = trabajoController.vistaCalendario(1L, model, userLider);

        // Assert
        Assertions.assertEquals("trabajos/calendario", vista,
                "vistaCalendario() debe retornar la vista 'trabajos/calendario'");
    }

    // CP11 – POST /{id}/editar nombre vacío: redirige a /editar
    // CP11 - BORDE: actualizarTrabajo() con nombre vacío redirige al formulario de edición.
    // Entrada: nombre vacío
    // Resultado esperado: "redirect:/trabajos/1/editar"
    @Test
    void CP11_actualizarTrabajo_ConNombreVacio_RedireccionaAEditar() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TrabajoEditarDTO editado = new TrabajoEditarDTO();
        editado.setNombreTrabajo("   ");
        Mockito.when(customTrabajoDetailsService.esLider(1L, "lider")).thenReturn(true);

        // Act
        String resultado = trabajoController.actualizarTrabajo(1L, editado, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1/editar", resultado,
                "Nombre vacío debe redirigir al formulario de edición");
    }
}