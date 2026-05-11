package com.example.entregaya.facade;

import com.example.entregaya.dto.DashboardDTO;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests para DashboardFacade - centraliza los cálculos del panel principal

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardFacade - Tests unitarios con Mockito")
class DashboardFacadeTest {

    @Mock
    private CustomTrabajoDetailsService customTrabajoDetailsService;

    @Mock
    private CustomTareaDetailsService customTareaDetailsService;

    @Mock
    private CustomInvitacionDetailsService customInvitacionDetailsService;

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardFacade dashboardFacade;

    // CP01 - BORDE: getDashboardData sin trabajos para el usuario.
    // Se justifica para verificar que el facade no falla cuando el usuario no tiene trabajos asignados.
    // Entrada: listarPorUsuario retorna lista vacía
    // Resultados Esperados: totalTrabajos == 0, listas vacías, sin excepción.
    @Test
    @DisplayName("CP01: getDashboardData con lista vacía de trabajos retorna DTO con ceros")
    void CP01_GetDashboardData_SinTrabajos_RetornaDTOConCeros() {
        // Arrange
        String username = "usuarioSinTrabajos";
        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of());
        when(customInvitacionDetailsService.pendientesParaUsuario(username)).thenReturn(List.of());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        DashboardDTO dto = dashboardFacade.getDashboardData(username);

        // Assert
        assertEquals(0, dto.getTotalTrabajos());
        assertEquals(0, dto.getCompletados());
        assertTrue(dto.getTrabajos().isEmpty());
        assertTrue(dto.getProximosVencer().isEmpty());
    }

    // CP02 - NORMAL: getDashboardData cuenta correctamente totalTrabajos y completados.
    // Se justifica para comprobar que la distinción entre activos y completados es correcta.
    // Entrada: 2 trabajos, uno con progreso 100% (completado) y otro con 50%
    // Resultados Esperados: totalTrabajos == 2, completados == 1, trabajos activos == 1.
    @Test
    @DisplayName("CP02: getDashboardData distingue correctamente trabajos activos y completados")
    void CP02_GetDashboardData_ConTrabajosActivosYCompletados_CuentaCorrectamente() {
        // Arrange
        String username = "usuario";
        Trabajo trabajoActivo = new Trabajo();
        trabajoActivo.setNombreTrabajo("Activo");

        Trabajo trabajoCompleto = new Trabajo();
        trabajoCompleto.setNombreTrabajo("Completo");

        List<Trabajo> todos = List.of(trabajoActivo, trabajoCompleto);
        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(todos);
        when(customTareaDetailsService.calcularProgreso(isNull())).thenReturn(50, 100);
        when(customTareaDetailsService.tareas(isNull())).thenReturn(List.of());
        when(customInvitacionDetailsService.pendientesParaUsuario(username)).thenReturn(List.of());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        DashboardDTO dto = dashboardFacade.getDashboardData(username);

        // Assert
        assertEquals(2, dto.getTotalTrabajos());
        assertEquals(1, dto.getCompletados());
        assertEquals(1, dto.getTrabajos().size());
    }

    // CP03 - NORMAL: proximosVencer filtra trabajos con fecha de entrega dentro de 7 días.
    // Se justifica para confirmar que el umbral de 7 días se aplica correctamente.
    // Entrada: trabajo con fechaEntrega = ahora + 5 días y progreso < 100
    // Resultados Esperados: El trabajo aparece en proximosVencer.
    @Test
    @DisplayName("CP03: proximosVencer incluye trabajos con fechaEntrega dentro de 7 días")
    void CP03_GetDashboardData_ConTrabajoProximoAVencer_AparaceEnProximosVencer() {
        // Arrange
        String username = "usuario";
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Urgente");
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(5));

        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of(trabajo));
        when(customTareaDetailsService.calcularProgreso(isNull())).thenReturn(40);
        when(customTareaDetailsService.tareas(isNull())).thenReturn(List.of());
        when(customInvitacionDetailsService.pendientesParaUsuario(username)).thenReturn(List.of());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        DashboardDTO dto = dashboardFacade.getDashboardData(username);

        // Assert
        assertEquals(1, dto.getProximosVencer().size());
        assertEquals("Proyecto Urgente", dto.getProximosVencer().get(0).getNombreTrabajo());
    }

    // CP04 - NORMAL: tareasVencidas cuenta solo tareas no completadas con fecha pasada.
    // Se justifica para asegurar que las tareas completadas no se contabilizan como vencidas.
    // Entrada: trabajo con 2 tareas: una vencida no completada y una completada vencida
    // Resultados Esperados: tareasVencidas == 1.
    @Test
    @DisplayName("CP04: tareasVencidas solo cuenta tareas incompletas con fechaFinal pasada")
    void CP04_GetDashboardData_ConTareasVencidas_CuentaSoloLasNoCompletadas() {
        // Arrange
        String username = "usuario";
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto");

        Tarea tareaVencida = new Tarea();
        tareaVencida.setNombre("Vencida");
        tareaVencida.setFechaFinal(LocalDateTime.now().minusDays(3));
        tareaVencida.setCompletada(false);

        Tarea tareaCompletadaVencida = new Tarea();
        tareaCompletadaVencida.setNombre("Completada Vencida");
        tareaCompletadaVencida.setFechaFinal(LocalDateTime.now().minusDays(1));
        tareaCompletadaVencida.setCompletada(true);

        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of(trabajo));
        when(customTareaDetailsService.calcularProgreso(isNull())).thenReturn(50);
        when(customTareaDetailsService.tareas(isNull()))
                .thenReturn(List.of(tareaVencida, tareaCompletadaVencida));
        when(customInvitacionDetailsService.pendientesParaUsuario(username)).thenReturn(List.of());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        DashboardDTO dto = dashboardFacade.getDashboardData(username);

        // Assert
        assertEquals(1L, dto.getTareasVencidas());
    }

    // CP05 - BORDE: getEstadisticasPersonales sin tareas retorna todos los contadores en cero.
    // Se justifica para comprobar que el facade no falla ni divide por cero cuando no hay tareas.
    // Entrada: usuario sin trabajos
    // Resultados Esperados: total == 0, completadas == 0, vencidas == 0, tasa == 0.0.
    @Test
    @DisplayName("CP05: getEstadisticasPersonales sin tareas retorna todos los valores en cero")
    void CP05_GetEstadisticasPersonales_SinTareas_RetornaCeros() {
        // Arrange
        String username = "usuarioVacio";
        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of());

        // Act
        Map<String, Object> stats = dashboardFacade.getEstadisticasPersonales(username);

        // Assert
        assertEquals(0L, stats.get("totalTareas"));
        assertEquals(0L, stats.get("tareasCompletadas"));
        assertEquals(0L, stats.get("tareasVencidas"));
        assertEquals(0.0, stats.get("tasaCompletitud"));
    }

    // CP06 - NORMAL: getEstadisticasPersonales calcula tasa de completitud correctamente.
    // Se justifica para garantizar la precisión del cálculo estadístico del usuario.
    // Entrada: 2 tareas, 1 completada → tasa = 50.0%
    // Resultados Esperados: tasaCompletitud == 50.0.
    @Test
    @DisplayName("CP06: getEstadisticasPersonales calcula tasa de completitud al 50%")
    void CP06_GetEstadisticasPersonales_ConUnaTareaCompletada_RetornaTasa50() {
        // Arrange
        String username = "usuario";
        Trabajo trabajo = new Trabajo();

        Tarea tareaCompletada = new Tarea();
        tareaCompletada.setNombre("Completada");
        tareaCompletada.setCompletada(true);
        tareaCompletada.setFechaFinal(LocalDateTime.now().plusDays(5));

        Tarea tareaPendiente = new Tarea();
        tareaPendiente.setNombre("Pendiente");
        tareaPendiente.setCompletada(false);
        tareaPendiente.setFechaFinal(LocalDateTime.now().plusDays(10));

        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of(trabajo));
        when(customTareaDetailsService.tareas(isNull()))
                .thenReturn(List.of(tareaCompletada, tareaPendiente));

        // Act
        Map<String, Object> stats = dashboardFacade.getEstadisticasPersonales(username);

        // Assert
        assertEquals(2L, stats.get("totalTareas"));
        assertEquals(1L, stats.get("tareasCompletadas"));
        assertEquals(50.0, stats.get("tasaCompletitud"));
    }

    // CP07 - BORDE: notificacionesNoLeidas retorna 0 si el usuario no existe en el repositorio.
    // Se justifica para garantizar que el facade es resiliente ante usuarios no encontrados.
    // Entrada: userRepository.findByUsername retorna Optional.empty()
    // Resultados Esperados: notificacionesNoLeidas == 0 sin excepción.
    @Test
    @DisplayName("CP07: notificacionesNoLeidas es 0 cuando el usuario no existe")
    void CP07_GetDashboardData_UsuarioNoEncontrado_NotificacionesNoLeidasEsCero() {
        // Arrange
        String username = "desconocido";
        when(customTrabajoDetailsService.listarPorUsuario(username)).thenReturn(List.of());
        when(customInvitacionDetailsService.pendientesParaUsuario(username)).thenReturn(List.of());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        DashboardDTO dto = dashboardFacade.getDashboardData(username);

        // Assert
        assertEquals(0L, dto.getNotificacionesNoLeidas());
        verify(notificacionRepository, never())
                .findByDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(any());
    }
}
