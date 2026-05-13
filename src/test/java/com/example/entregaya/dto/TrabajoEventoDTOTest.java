package com.example.entregaya.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Tests para TrabajoEventoDTO - DTO de eventos de cambios en la composición de un trabajo

@DisplayName("TrabajoEventoDTO - Tests unitarios")
class TrabajoEventoDTOTest {

    // CP01 - NORMAL: Constructor con tipoEvento INGRESO asigna el campo correctamente.
    // Se justifica para verificar que el evento de ingreso de un miembro se almacena correctamente.
    // Entrada: tipoEvento = INGRESO, afectadoUsername = "nuevoMiembro"
    // Resultados Esperados: dto.tipoEvento() == INGRESO, dto.afectadoUsername() == "nuevoMiembro".
    @Test
    @DisplayName("CP01: Constructor con INGRESO asigna tipoEvento y afectadoUsername correctamente")
    void CP01_Constructor_ConTipoEventoIngreso_AsignaCamposCorrectamente() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 5, 1, 10, 0);

        // Act
        TrabajoEventoDTO dto = new TrabajoEventoDTO(
                1L, "Proyecto Alpha",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevoMiembro", "admin", fecha
        );

        // Assert
        assertEquals(TrabajoEventoDTO.TipoEvento.INGRESO, dto.tipoEvento());
        assertEquals("nuevoMiembro", dto.afectadoUsername());
    }

    // CP02 - NORMAL: Constructor con tipoEvento SALIDA asigna el campo correctamente.
    // Se justifica para confirmar que la baja de un miembro del trabajo se registra con el tipo correcto.
    // Entrada: tipoEvento = SALIDA
    // Resultados Esperados: dto.tipoEvento() == SALIDA.
    @Test
    @DisplayName("CP02: Constructor con SALIDA asigna tipoEvento correctamente")
    void CP02_Constructor_ConTipoEventoSalida_AsignaTipoSalida() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.now();

        // Act
        TrabajoEventoDTO dto = new TrabajoEventoDTO(
                2L, "Proyecto Beta",
                TrabajoEventoDTO.TipoEvento.SALIDA,
                "exMiembro", "lider", fecha
        );

        // Assert
        assertEquals(TrabajoEventoDTO.TipoEvento.SALIDA, dto.tipoEvento());
    }

    // CP03 - NORMAL: afectadoUsername y realizadoPorUsername se almacenan por separado.
    // Se justifica para verificar que el DTO distingue al afectado del ejecutor de la acción.
    // Entrada: afectadoUsername = "juan", realizadoPorUsername = "maria"
    // Resultados Esperados: Los valores son distintos y accesibles individualmente.
    @Test
    @DisplayName("CP03: afectadoUsername y realizadoPorUsername son campos independientes")
    void CP03_Constructor_AfectadoYRealizadoPor_SonCamposIndependientes() {
        // Arrange & Act
        TrabajoEventoDTO dto = new TrabajoEventoDTO(
                3L, "Proyecto Gamma",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "juan", "maria", LocalDateTime.now()
        );

        // Assert
        assertEquals("juan", dto.afectadoUsername());
        assertEquals("maria", dto.realizadoPorUsername());
        assertNotEquals(dto.afectadoUsername(), dto.realizadoPorUsername());
    }

    // CP04 - NORMAL: El enum TipoEvento contiene exactamente los valores INGRESO y SALIDA.
    // Se justifica para asegurar que no existen valores de evento inesperados en el enum.
    // Entrada: TrabajoEventoDTO.TipoEvento.values()
    // Resultados Esperados: Exactamente 2 valores: INGRESO y SALIDA.
    @Test
    @DisplayName("CP04: TipoEvento tiene exactamente los valores INGRESO y SALIDA")
    void CP04_TipoEvento_ContieneExactamenteDosValores() {
        // Arrange & Act
        TrabajoEventoDTO.TipoEvento[] valores = TrabajoEventoDTO.TipoEvento.values();

        // Assert
        assertEquals(2, valores.length);
        assertNotNull(TrabajoEventoDTO.TipoEvento.INGRESO);
        assertNotNull(TrabajoEventoDTO.TipoEvento.SALIDA);
    }

    // CP05 - NORMAL: fechaEvento y trabajoId se almacenan y recuperan correctamente.
    // Se justifica para garantizar que los identificadores y la marca temporal del evento son fiables.
    // Entrada: trabajoId = 10L, fecha específica
    // Resultados Esperados: dto.trabajoId() == 10L, dto.fechaEvento() igual a la fecha dada.
    @Test
    @DisplayName("CP05: trabajoId y fechaEvento se almacenan y recuperan correctamente")
    void CP05_TrabajoidYFechaEvento_SeAlmacenanYRecuperanCorrectos() {
        // Arrange
        LocalDateTime fechaEspecifica = LocalDateTime.of(2026, 6, 15, 12, 0);

        // Act
        TrabajoEventoDTO dto = new TrabajoEventoDTO(
                10L, "Proyecto Delta",
                TrabajoEventoDTO.TipoEvento.SALIDA,
                "saliente", "lider", fechaEspecifica
        );

        // Assert
        assertEquals(10L, dto.trabajoId());
        assertEquals(fechaEspecifica, dto.fechaEvento());
        assertEquals("Proyecto Delta", dto.nombreTrabajo());
    }
}
