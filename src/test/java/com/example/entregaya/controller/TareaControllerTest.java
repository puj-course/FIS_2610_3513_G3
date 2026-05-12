package com.example.entregaya.controller;

import com.example.entregaya.dto.TareaConEtiquetaDTO;
import com.example.entregaya.dto.TareaCrearDTO;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomComentarioDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
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

import java.util.HashSet;
import java.util.List;
import com.example.entregaya.dto.TareaEditarDTO;

/**
 * HU-49 - Task #495
 * Pruebas unitarias JUnit 5 para TareaController.
 * Se verifica que los métodos del controlador agregan los atributos
 * correctos al Model (assertNotNull / assertEquals) en los flujos:
 * crear, editar, cambiar estado y asignar miembro.
 */
class TareaControllerTest {

    private TareaController tareaController;
    private TareaRepository tareaRepository;
    private CustomTareaDetailsService customTareaDetailsService;
    private CustomTrabajoDetailsService customTrabajoDetailsService;
    private CustomComentarioDetailsService customComentarioDetailsService;
    private UserRepository userRepository;
    private Lideroeditorstrategy lideroeditorstrategy;

    /** Tarea de prueba reutilizable. */
    private Tarea tareaBase;
    /** Trabajo de prueba reutilizable. */
    private Trabajo trabajoBase;
    /** UserDetails de prueba. */
    private UserDetails userLider;

    @BeforeEach
    void setUp() {
        tareaRepository                = Mockito.mock(TareaRepository.class);
        customTareaDetailsService      = Mockito.mock(CustomTareaDetailsService.class);
        customTrabajoDetailsService    = Mockito.mock(CustomTrabajoDetailsService.class);
        customComentarioDetailsService = Mockito.mock(CustomComentarioDetailsService.class);
        userRepository                 = Mockito.mock(UserRepository.class);
        lideroeditorstrategy           = Mockito.mock(Lideroeditorstrategy.class);

        tareaController = new TareaController(
                tareaRepository,
                customTareaDetailsService,
                customTrabajoDetailsService,
                customComentarioDetailsService,
                userRepository,
                lideroeditorstrategy
        );

        tareaBase = new Tarea();
        tareaBase.setId(10L);
        tareaBase.setNombre("Tarea Test");

        trabajoBase = new Trabajo();
        trabajoBase.setNombreTrabajo("Trabajo Test");

        userLider = org.springframework.security.core.userdetails.User
                .withUsername("lider")
                .password("pass")
                .roles("USER")
                .build();
    }


    // CP01 – GET /trabajos/{trabajoId}/tareas/nuevo: atributos en modelo
    // CP01 - NORMAL: formulario() agrega "tarea", "trabajoId" y "dificultades" al modelo.
    // Entrada: trabajoId=1
    // Resultado esperado: atributos no nulos en el modelo, vista "trabajos/tareas/CrearTarea"
    @Test
    void CP01_formulario_AgregaAtributosAlModelo() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        // Act
        String vista = tareaController.formulario(1L, model);

        // Assert
        Assertions.assertEquals("trabajos/tareas/CrearTarea", vista,
                "formulario() debe retornar la vista 'trabajos/tareas/CrearTarea'");
        Assertions.assertNotNull(model.asMap().get("tarea"),
                "El modelo debe contener 'tarea'");
        Assertions.assertNotNull(model.asMap().get("trabajoId"),
                "El modelo debe contener 'trabajoId'");
        Assertions.assertNotNull(model.asMap().get("dificultades"),
                "El modelo debe contener 'dificultades'");
        Assertions.assertNotNull(model.asMap().get("colaboradores"),
                "El modelo debe contener 'colaboradores'");
    }


    // CP02 – GET /{tareaId}/editar con permiso: atributos en modelo
    // CP02 - NORMAL: formularioEditar() con permiso retorna la vista de edición y atributos.
    // Entrada: trabajoId=1, tareaId=10, usuario con permiso
    // Resultado esperado: vista "trabajos/tareas/EditarTarea", atributos tarea y colaboradores
    @Test
    void CP02_formularioEditar_ConPermiso_RetornaVistaEditar_ConAtributos() {
        // Arrange
        Model model = new ExtendedModelMap();
        tareaBase.setEtiquetas(List.of("urgente"));
        tareaBase.setResponsables(new HashSet<>());

        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);
        Mockito.when(customTareaDetailsService.findById(10L)).thenReturn(tareaBase);
        Mockito.when(customTrabajoDetailsService.obtenerPorId(1L)).thenReturn(trabajoBase);

        // Act
        String vista = tareaController.formularioEditar(1L, 10L, model, userLider);

        // Assert
        Assertions.assertEquals("trabajos/tareas/EditarTarea", vista,
                "formularioEditar() con permiso debe retornar 'trabajos/tareas/EditarTarea'");
        Assertions.assertNotNull(model.asMap().get("tarea"),
                "El modelo debe contener 'tarea'");
        Assertions.assertNotNull(model.asMap().get("trabajoId"),
                "El modelo debe contener 'trabajoId'");
        Assertions.assertNotNull(model.asMap().get("colaboradores"),
                "El modelo debe contener 'colaboradores'");
        Assertions.assertNotNull(model.asMap().get("responsablesSeleccionados"),
                "El modelo debe contener 'responsablesSeleccionados'");
        Assertions.assertNotNull(model.asMap().get("etiquetasExistentes"),
                "El modelo debe contener 'etiquetasExistentes'");
    }


    // CP03 – GET /{tareaId}/editar sin permiso: redirige con error
    // CP03 - NEGATIVA: formularioEditar() sin permiso redirige con parámetro de error.
    // Entrada: trabajoId=1, tareaId=10, usuario sin permiso
    // Resultado esperado: "redirect:/trabajos/1?error=noPermiso"
    @Test
    void CP03_formularioEditar_SinPermiso_RedireccionaConError() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(false);

        // Act
        String resultado = tareaController.formularioEditar(1L, 10L, model, userLider);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1?error=noPermiso", resultado,
                "Sin permiso debe redirigir con 'error=noPermiso'");
    }


    // CP04 – POST /{tareaId}/eliminar: redirige al trabajo
    // CP04 - NORMAL: eliminar() redirige al trabajo padre.
    // Entrada: trabajoId=1, tareaId=10
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP04_eliminar_RedireccionaAlTrabajo() {
        // Act
        String resultado = tareaController.eliminar(1L, 10L);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "eliminar() debe redirigir a '/trabajos/1'");
        Mockito.verify(customTareaDetailsService).eliminar(10L);
    }


    // CP05 – POST /{tareaId}/completar: redirige al trabajo
    // CP05 - NORMAL: completar() llama toggleCompletada y redirige al trabajo.
    // Entrada: trabajoId=1, tareaId=10, usuario autenticado
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP05_completar_RedireccionaAlTrabajo() {
        // Act
        String resultado = tareaController.completar(1L, 10L, userLider);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "completar() debe redirigir a '/trabajos/1'");
        Mockito.verify(customTareaDetailsService).toggleCompletada(10L, "lider");
    }


    // CP06 – POST /{tareaId}/asignar: redirige al trabajo
    // CP06 - NORMAL: asignarResponsables() actualiza responsables y redirige al trabajo.
    // Entrada: trabajoId=1, tareaId=10, lista de responsableIds
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP06_asignarResponsables_RedireccionaAlTrabajo() {
        // Act
        String resultado = tareaController.asignarResponsables(1L, 10L, List.of(1L, 2L));

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "asignarResponsables() debe redirigir a '/trabajos/1'");
        Mockito.verify(customTareaDetailsService).actualizarResponsables(10L, List.of(1L, 2L));
    }


    // CP07 – POST /nueva exitoso: redirige al trabajo
    // CP07 - NORMAL: guardar() con tarea válida redirige al trabajo.
    // Entrada: trabajoId=1, tarea válida
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP07_guardar_ConTareaValida_RedireccionaAlTrabajo() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Test");

        String resultado = tareaController.guardar(1L, dto, null, null, ra);

        Assertions.assertEquals("redirect:/trabajos/1", resultado);
    }


    // CP08 – POST /nueva con excepción: redirige al formulario con error
    // CP08 - NEGATIVA: guardar() con datos inválidos redirige al formulario de creación.
    // Entrada: tarea que lanza IllegalArgumentException en el servicio
    // Resultado esperado: "redirect:/trabajos/1/tareas/nuevo"
    @Test
    void CP08_guardar_ConExcepcion_RedireccionaAFormulario() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea Test");
        Mockito.doThrow(new IllegalArgumentException("Nombre duplicado"))
                .when(customTareaDetailsService)
                .crearTareaDesdeDTO(Mockito.any(), Mockito.eq(1L), Mockito.any(), Mockito.any());

        String resultado = tareaController.guardar(1L, dto, null, null, ra);

        Assertions.assertEquals("redirect:/trabajos/1/tareas/nuevo", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("error"));
    }


    // CP09 – GET /{tareaId}/detalle: modelo contiene "tarea" y "tareaConEtiqueta"
    // CP09 - NORMAL: verDetalleTarea() agrega tarea y tareaConEtiqueta al modelo.
    // Entrada: trabajoId=1, tareaId=10
    // Resultado esperado: modelo con "tarea", "tareaConEtiqueta" y "trabajoId" no nulos
    @Test
    void CP09_verDetalleTarea_AgregaAtributosAlModelo() {
        // Arrange
        Model model = new ExtendedModelMap();
        TareaConEtiquetaDTO dto = Mockito.mock(TareaConEtiquetaDTO.class);

        Mockito.when(customTareaDetailsService.findById(10L)).thenReturn(tareaBase);
        Mockito.when(customTareaDetailsService.findByIdConEtiqueta(10L)).thenReturn(dto);

        // Act
        String vista = tareaController.verDetalleTarea(1L, 10L, model);

        // Assert
        Assertions.assertEquals("trabajos/tareas/detalle tarea", vista,
                "verDetalleTarea() debe retornar la vista de detalle");
        Assertions.assertNotNull(model.asMap().get("tarea"),
                "El modelo debe contener 'tarea'");
        Assertions.assertNotNull(model.asMap().get("tareaConEtiqueta"),
                "El modelo debe contener 'tareaConEtiqueta'");
        Assertions.assertNotNull(model.asMap().get("trabajoId"),
                "El modelo debe contener 'trabajoId'");
    }


    // CP10 – POST /{tareaId}/editar con permiso: redirige al trabajo
    // CP10 - NORMAL: guardarEdicion() con permiso y datos válidos redirige al trabajo.
    // Entrada: trabajoId=1, tareaId=10, usuario con permiso
    // Resultado esperado: "redirect:/trabajos/1"
    @Test
    void CP10_guardarEdicion_ConPermiso_RedireccionaAlTrabajo() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaEditarDTO tareaEditadaDTO = new TareaEditarDTO();
        tareaEditadaDTO.setNombre("Tarea Editada");
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(true);

        // Act
        String resultado = tareaController.guardarEdicion(
                1L, 10L, tareaEditadaDTO, null, null, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1", resultado,
                "guardarEdicion() exitoso debe redirigir a '/trabajos/1'");
    }


    // CP11 – POST /{tareaId}/editar sin permiso: redirige con error
    // CP11 - NEGATIVA: guardarEdicion() sin permiso redirige con parámetro de error.
    // Entrada: trabajoId=1, tareaId=10, usuario sin permiso
    // Resultado esperado: "redirect:/trabajos/1?error=noPermiso"
    @Test
    void CP11_guardarEdicion_SinPermiso_RedireccionaConError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        TareaEditarDTO tareaEditadaDTO = new TareaEditarDTO();
        tareaEditadaDTO.setNombre("Tarea Editada");
        Mockito.when(customTrabajoDetailsService.verificarPermiso(1L, "lider", lideroeditorstrategy))
                .thenReturn(false);

        // Act
        String resultado = tareaController.guardarEdicion(
                1L, 10L, tareaEditadaDTO, null, null, userLider, ra);

        // Assert
        Assertions.assertEquals("redirect:/trabajos/1?error=noPermiso", resultado,
                "guardarEdicion() sin permiso debe redirigir con 'error=noPermiso'");
    }


    // CP12 – formulario(): trabajoId en el modelo coincide con el path variable
    // CP12 - BORDE: el valor de "trabajoId" en el modelo debe ser el mismo que el recibido.
    // Entrada: trabajoId=5
    // Resultado esperado: model.get("trabajoId") == 5L
    @Test
    void CP12_formulario_TareaIdEnModeloCoincidesConPathVariable() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(customTrabajoDetailsService.obtenerPorId(5L)).thenReturn(trabajoBase);

        // Act
        tareaController.formulario(5L, model);

        // Assert
        Assertions.assertEquals(5L, model.asMap().get("trabajoId"),
                "El 'trabajoId' en el modelo debe coincidir con el recibido como path variable");
    }
}