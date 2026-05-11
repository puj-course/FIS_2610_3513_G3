package com.example.entregaya.dto;

import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Tests para DashboardDTO - contenedor de datos del panel principal

@DisplayName("DashboardDTO - Tests unitarios")
class DashboardDTOTest {

    // CP01 - NORMAL: Constructor completo.
    // Se justifica para verificar que el constructor parametrizado asigna todos los campos correctamente.
    // Entrada: listas y valores numéricos validos
    // Resultados Esperados: Todos los getters retornan los valores pasados al constructor.
    @Test
    @DisplayName("CP01: Constructor con todos los parámetros asigna campos correctamente")
    void CP01_Constructor_ConTodosLosParametros_AsignaCamposCorrectamente() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto A");
        List<Trabajo> trabajos = List.of(trabajo);
        Map<Long, Integer> progresos = Map.of(1L, 50);
        long tareasVencidas = 3L;
        long notificacionesNoLeidas = 2L;

        // Act
        DashboardDTO dto = new DashboardDTO(
                trabajos, progresos, 1, 0,
                List.of(), List.of(),
                tareasVencidas, notificacionesNoLeidas
        );

        // Assert
        assertEquals(1, dto.getTrabajos().size());
        assertEquals(50, dto.getProgresos().get(1L));
        assertEquals(1, dto.getTotalTrabajos());
        assertEquals(0, dto.getCompletados());
        assertEquals(3L, dto.getTareasVencidas());
        assertEquals(2L, dto.getNotificacionesNoLeidas());
    }

    // CP02 - NORMAL: Constructor vacío y setters independientes.
    // Se justifica para garantizar que el DTO puede construirse progresivamente con setters.
    // Entrada: Constructor vacío + setters individuales
    // Resultados Esperados: Cada getter retorna el valor asignado por su setter.
    @Test
    @DisplayName("CP02: Constructor vacío y setters asignan valores correctamente")
    void CP02_ConstructorVacio_SettersIndependientes_AsignanValores() {
        // Arrange
        DashboardDTO dto = new DashboardDTO();

        // Act
        dto.setTotalTrabajos(5);
        dto.setCompletados(2);
        dto.setTareasVencidas(1L);
        dto.setNotificacionesNoLeidas(4L);

        // Assert
        assertEquals(5, dto.getTotalTrabajos());
        assertEquals(2, dto.getCompletados());
        assertEquals(1L, dto.getTareasVencidas());
        assertEquals(4L, dto.getNotificacionesNoLeidas());
    }

    // CP03 - NORMAL: getProximosVencer retorna la lista asignada.
    // Se justifica para verificar que la lista de trabajos próximos a vencer se almacena sin modificaciones.
    // Entrada: lista con un Trabajo con fecha de entrega
    // Resultados Esperados: Lista de tamaño 1 con el trabajo esperado.
    @Test
    @DisplayName("CP03: getProximosVencer retorna la lista asignada correctamente")
    void CP03_GetProximosVencer_ConListaAsignada_RetornaListaCorrecta() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Próximo");
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(3));
        DashboardDTO dto = new DashboardDTO();

        // Act
        dto.setProximosVencer(List.of(trabajo));

        // Assert
        assertEquals(1, dto.getProximosVencer().size());
        assertEquals("Proyecto Próximo", dto.getProximosVencer().get(0).getNombreTrabajo());
    }

    // CP04 - BORDE: Listas nulas asignadas con setters.
    // Se justifica para comprobar que el DTO no impone restricciones internas sobre valores nulos.
    // Entrada: setTrabajos(null), setProgresos(null)
    // Resultados Esperados: Los getters retornan null sin excepción.
    @Test
    @DisplayName("CP04: Listas nulas asignadas con setters no lanzan excepción")
    void CP04_SetterConValoresNulos_NoLanzaExcepcion() {
        // Arrange
        DashboardDTO dto = new DashboardDTO();

        // Act
        dto.setTrabajos(null);
        dto.setProgresos(null);
        dto.setInvitaciones(null);

        // Assert
        assertNull(dto.getTrabajos());
        assertNull(dto.getProgresos());
        assertNull(dto.getInvitaciones());
    }

    // CP05 - BORDE: Valores extremos en contadores long.
    // Se justifica para asegurar que los campos long soportan valores grandes sin overflow.
    // Entrada: Long.MAX_VALUE para tareasVencidas y notificacionesNoLeidas
    // Resultados Esperados: Los getters retornan el valor exacto.
    @Test
    @DisplayName("CP05: Contadores long soportan valores extremos sin overflow")
    void CP05_ContadoresLong_ConValoresExtremos_RetornanValorExacto() {
        // Arrange
        DashboardDTO dto = new DashboardDTO();

        // Act
        dto.setTareasVencidas(Long.MAX_VALUE);
        dto.setNotificacionesNoLeidas(Long.MAX_VALUE);

        // Assert
        assertEquals(Long.MAX_VALUE, dto.getTareasVencidas());
        assertEquals(Long.MAX_VALUE, dto.getNotificacionesNoLeidas());
    }
}
