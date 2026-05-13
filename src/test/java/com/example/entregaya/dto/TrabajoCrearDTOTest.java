package com.example.entregaya.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrabajoCrearDTO - Tests unitarios")
class TrabajoCrearDTOTest {

    // CP01 - NORMAL: Constructor vacío crea objeto con campos nulos.
    // Entrada: new TrabajoCrearDTO()
    // Resultado esperado: todos los getters retornan null
    @Test
    @DisplayName("CP01: Constructor vacío crea objeto con campos nulos")
    void CP01_ConstructorVacio_CamposNulos() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();

        assertNull(dto.getNombreTrabajo());
        assertNull(dto.getDescripcion());
        assertNull(dto.getFechaInicio());
        assertNull(dto.getFechaEntrega());
    }

    // CP02 - NORMAL: Setters asignan y getters retornan los valores correctos.
    // Entrada: valores válidos para cada campo
    // Resultado esperado: cada getter retorna el valor asignado
    @Test
    @DisplayName("CP02: Setters y getters funcionan correctamente")
    void CP02_SettersGetters_FuncionanCorrectamente() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime entrega = LocalDateTime.of(2026, 6, 1, 23, 59);

        dto.setNombreTrabajo("Proyecto Alpha");
        dto.setDescripcion("Descripción del proyecto");
        dto.setFechaInicio(inicio);
        dto.setFechaEntrega(entrega);

        assertEquals("Proyecto Alpha", dto.getNombreTrabajo());
        assertEquals("Descripción del proyecto", dto.getDescripcion());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(entrega, dto.getFechaEntrega());
    }

    // CP03 - BORDE: Nombre con espacios en blanco es aceptado sin modificación.
    // Entrada: nombreTrabajo = "   "
    // Resultado esperado: getNombreTrabajo() retorna "   " (validación en capa superior)
    @Test
    @DisplayName("CP03: Nombre con espacios en blanco es aceptado por el DTO")
    void CP03_NombreConEspacios_EsAceptado() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("   ");

        assertEquals("   ", dto.getNombreTrabajo());
    }

    // CP04 - BORDE: Fechas iguales (inicio == entrega) son aceptadas.
    // Entrada: misma LocalDateTime para inicio y entrega
    // Resultado esperado: ambas fechas son iguales
    @Test
    @DisplayName("CP04: Fechas iguales en inicio y entrega son aceptadas")
    void CP04_FechasIguales_SonAceptadas() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 15, 12, 0);

        dto.setFechaInicio(fecha);
        dto.setFechaEntrega(fecha);

        assertEquals(dto.getFechaInicio(), dto.getFechaEntrega());
    }

    // CP05 - BORDE: Descripción nula es aceptada.
    // Entrada: setDescripcion(null)
    // Resultado esperado: getDescripcion() retorna null
    @Test
    @DisplayName("CP05: Descripción nula es aceptada por el DTO")
    void CP05_DescripcionNula_EsAceptada() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("Solo nombre");
        dto.setDescripcion(null);

        assertNull(dto.getDescripcion());
    }

    // CP06 - NORMAL: Sobreescritura de valores con setters.
    // Entrada: asignar un valor y luego sobreescribirlo
    // Resultado esperado: el getter retorna el último valor asignado
    @Test
    @DisplayName("CP06: Sobreescritura de valores funciona correctamente")
    void CP06_SobreescrituraDeValores_FuncionaCorrectamente() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("Nombre inicial");
        dto.setNombreTrabajo("Nombre final");

        assertEquals("Nombre final", dto.getNombreTrabajo());
    }
}
