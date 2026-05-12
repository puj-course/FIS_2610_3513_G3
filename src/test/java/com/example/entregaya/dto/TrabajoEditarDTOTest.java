package com.example.entregaya.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrabajoEditarDTO - Tests unitarios")
class TrabajoEditarDTOTest {

    // CP01 - NORMAL: Constructor vacío crea objeto con campos nulos.
    // Entrada: new TrabajoEditarDTO()
    // Resultado esperado: todos los getters retornan null
    @Test
    @DisplayName("CP01: Constructor vacío crea objeto con campos nulos")
    void CP01_ConstructorVacio_CamposNulos() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();

        assertNull(dto.getNombreTrabajo());
        assertNull(dto.getDescripcion());
        assertNull(dto.getFechaInicio());
        assertNull(dto.getFechaEntrega());
    }

    // CP02 - NORMAL: Setters asignan y getters retornan los valores correctos.
    // Entrada: valores válidos para todos los campos
    // Resultado esperado: cada getter retorna el valor asignado
    @Test
    @DisplayName("CP02: Setters y getters funcionan correctamente")
    void CP02_SettersGetters_FuncionanCorrectamente() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        LocalDateTime inicio = LocalDateTime.of(2026, 2, 1, 9, 0);
        LocalDateTime entrega = LocalDateTime.of(2026, 8, 31, 18, 0);

        dto.setNombreTrabajo("Trabajo Editado");
        dto.setDescripcion("Nueva descripción");
        dto.setFechaInicio(inicio);
        dto.setFechaEntrega(entrega);

        assertEquals("Trabajo Editado", dto.getNombreTrabajo());
        assertEquals("Nueva descripción", dto.getDescripcion());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(entrega, dto.getFechaEntrega());
    }

    // CP03 - BORDE: Nombre vacío es aceptado sin modificación.
    // Entrada: nombreTrabajo = ""
    // Resultado esperado: getNombreTrabajo() retorna "" (validación en capa superior)
    @Test
    @DisplayName("CP03: Nombre vacío es aceptado por el DTO")
    void CP03_NombreVacio_EsAceptado() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo("");

        assertEquals("", dto.getNombreTrabajo());
    }

    // CP04 - BORDE: Solo se actualiza el nombre, resto permanece nulo.
    // Entrada: solo setNombreTrabajo con valor válido
    // Resultado esperado: nombre asignado, resto null
    @Test
    @DisplayName("CP04: Solo nombre asignado, resto permanece nulo")
    void CP04_SoloNombreAsignado_RestoNulo() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo("Solo nombre");

        assertEquals("Solo nombre", dto.getNombreTrabajo());
        assertNull(dto.getDescripcion());
        assertNull(dto.getFechaInicio());
        assertNull(dto.getFechaEntrega());
    }

    // CP05 - NORMAL: Sobreescritura de valores funciona correctamente.
    // Entrada: asignar valor y luego sobreescribirlo
    // Resultado esperado: el getter retorna el último valor
    @Test
    @DisplayName("CP05: Sobreescritura de valores funciona correctamente")
    void CP05_SobreescrituraDeValores_FuncionaCorrectamente() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setDescripcion("Primera descripción");
        dto.setDescripcion("Segunda descripción");

        assertEquals("Segunda descripción", dto.getDescripcion());
    }

    // CP06 - BORDE: Fechas en el pasado son aceptadas.
    // Entrada: fecha en el pasado
    // Resultado esperado: el DTO acepta la fecha sin excepción
    @Test
    @DisplayName("CP06: Fechas en el pasado son aceptadas por el DTO")
    void CP06_FechasEnElPasado_SonAceptadas() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        LocalDateTime pasado = LocalDateTime.of(2020, 1, 1, 0, 0);

        dto.setFechaInicio(pasado);
        dto.setFechaEntrega(pasado.plusDays(30));

        assertEquals(pasado, dto.getFechaInicio());
        assertNotNull(dto.getFechaEntrega());
    }
}
