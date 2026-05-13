package com.example.entregaya.dto;

import com.example.entregaya.model.Tarea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TareaCrearDTO - Tests unitarios")
class TareaCrearDTOTest {

    // CP01 - NORMAL: Constructor vacío asigna dificultad MEDIA por defecto.
    // Entrada: new TareaCrearDTO()
    // Resultado esperado: dificultad == MEDIA, resto nulos
    @Test
    @DisplayName("CP01: Constructor vacío asigna dificultad MEDIA por defecto")
    void CP01_ConstructorVacio_DificultadMediaPorDefecto() {
        TareaCrearDTO dto = new TareaCrearDTO();

        assertEquals(Tarea.Dificultad.MEDIA, dto.getDificultad());
        assertNull(dto.getNombre());
        assertNull(dto.getDescripcion());
        assertNull(dto.getFechaInicio());
        assertNull(dto.getFechaFinal());
    }

    // CP02 - NORMAL: Setters y getters funcionan correctamente para todos los campos.
    // Entrada: valores válidos para cada campo
    // Resultado esperado: cada getter retorna el valor asignado
    @Test
    @DisplayName("CP02: Setters y getters funcionan correctamente")
    void CP02_SettersGetters_FuncionanCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 1, 8, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 3, 31, 18, 0);

        dto.setNombre("Tarea Nueva");
        dto.setDescripcion("Descripción de la tarea");
        dto.setFechaInicio(inicio);
        dto.setFechaFinal(fin);
        dto.setDificultad(Tarea.Dificultad.ALTA);

        assertEquals("Tarea Nueva", dto.getNombre());
        assertEquals("Descripción de la tarea", dto.getDescripcion());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFinal());
        assertEquals(Tarea.Dificultad.ALTA, dto.getDificultad());
    }

    // CP03 - NORMAL: Cambio de dificultad a SIMPLE funciona correctamente.
    // Entrada: setDificultad(SIMPLE)
    // Resultado esperado: getDificultad() retorna SIMPLE
    @Test
    @DisplayName("CP03: Cambio de dificultad a SIMPLE funciona")
    void CP03_CambioDificultadSimple_FuncionaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setDificultad(Tarea.Dificultad.SIMPLE);

        assertEquals(Tarea.Dificultad.SIMPLE, dto.getDificultad());
    }

    // CP04 - NORMAL: Cambio de dificultad a MEDIA funciona correctamente.
    // Entrada: setDificultad(MEDIA) explícito
    // Resultado esperado: getDificultad() retorna MEDIA
    @Test
    @DisplayName("CP04: Cambio de dificultad a MEDIA funciona")
    void CP04_CambioDificultadMedia_FuncionaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setDificultad(Tarea.Dificultad.ALTA);
        dto.setDificultad(Tarea.Dificultad.MEDIA);

        assertEquals(Tarea.Dificultad.MEDIA, dto.getDificultad());
    }

    // CP05 - BORDE: Fechas nulas son aceptadas.
    // Entrada: no asignar fechas
    // Resultado esperado: ambas fechas retornan null
    @Test
    @DisplayName("CP05: Fechas nulas son aceptadas por el DTO")
    void CP05_FechasNulas_SonAceptadas() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre("Tarea sin fechas");

        assertNull(dto.getFechaInicio());
        assertNull(dto.getFechaFinal());
    }

    // CP06 - BORDE: Nombre nulo es aceptado por el DTO.
    // Entrada: setNombre(null)
    // Resultado esperado: getNombre() retorna null
    @Test
    @DisplayName("CP06: Nombre nulo es aceptado por el DTO")
    void CP06_NombreNulo_EsAceptado() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setNombre(null);

        assertNull(dto.getNombre());
    }

    // CP07 - BORDE: Sobreescritura de dificultad funciona correctamente.
    // Entrada: asignar ALTA y luego SIMPLE
    // Resultado esperado: getDificultad() retorna SIMPLE
    @Test
    @DisplayName("CP07: Sobreescritura de dificultad funciona correctamente")
    void CP07_SobreescrituraDificultad_FuncionaCorrectamente() {
        TareaCrearDTO dto = new TareaCrearDTO();
        dto.setDificultad(Tarea.Dificultad.ALTA);
        dto.setDificultad(Tarea.Dificultad.SIMPLE);

        assertEquals(Tarea.Dificultad.SIMPLE, dto.getDificultad());
    }
}
