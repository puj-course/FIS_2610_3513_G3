package com.example.entregaya.dto;

import com.example.entregaya.model.Tarea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TareaEditarDTO - Tests unitarios")
class TareaEditarDTOTest {

    // CP01 - NORMAL: Constructor vacío crea listas vacías y completada=false.
    // Entrada: new TareaEditarDTO()
    // Resultado esperado: listas no nulas y vacías, completada=false
    @Test
    @DisplayName("CP01: Constructor vacío inicializa listas vacías y completada false")
    void CP01_ConstructorVacio_ListasVaciasYCompletadaFalse() {
        TareaEditarDTO dto = new TareaEditarDTO();

        assertNotNull(dto.getEtiquetas());
        assertTrue(dto.getEtiquetas().isEmpty());
        assertNotNull(dto.getResponsablesIds());
        assertTrue(dto.getResponsablesIds().isEmpty());
        assertFalse(dto.isCompletada());
        assertNull(dto.getNombre());
        assertNull(dto.getDescripcion());
        assertNull(dto.getDificultad());
    }

    // CP02 - NORMAL: Setters y getters funcionan para todos los campos.
    // Entrada: valores válidos para cada campo
    // Resultado esperado: cada getter retorna el valor asignado
    @Test
    @DisplayName("CP02: Setters y getters funcionan correctamente")
    void CP02_SettersGetters_FuncionanCorrectamente() {
        TareaEditarDTO dto = new TareaEditarDTO();
        LocalDateTime inicio = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 4, 30, 17, 0);

        dto.setNombre("Tarea Editada");
        dto.setDescripcion("Nueva descripción");
        dto.setFechaInicio(inicio);
        dto.setFechaFinal(fin);
        dto.setDificultad(Tarea.Dificultad.ALTA);
        dto.setCompletada(true);
        dto.setEtiquetas(List.of("urgente", "backend"));
        dto.setResponsablesIds(List.of(1L, 2L, 3L));

        assertEquals("Tarea Editada", dto.getNombre());
        assertEquals("Nueva descripción", dto.getDescripcion());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFinal());
        assertEquals(Tarea.Dificultad.ALTA, dto.getDificultad());
        assertTrue(dto.isCompletada());
        assertEquals(2, dto.getEtiquetas().size());
        assertEquals(3, dto.getResponsablesIds().size());
    }

    // CP03 - NORMAL: setCompletada(true) y luego setCompletada(false) funciona.
    // Entrada: completada = true, luego false
    // Resultado esperado: isCompletada() retorna false
    @Test
    @DisplayName("CP03: Toggle de completada funciona correctamente")
    void CP03_ToggleCompletada_FuncionaCorrectamente() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setCompletada(true);
        assertTrue(dto.isCompletada());

        dto.setCompletada(false);
        assertFalse(dto.isCompletada());
    }

    // CP04 - NORMAL: Lista de etiquetas es reemplazable.
    // Entrada: lista inicial y luego otra lista
    // Resultado esperado: getEtiquetas() retorna la última lista asignada
    @Test
    @DisplayName("CP04: Lista de etiquetas es reemplazable")
    void CP04_ListaEtiquetas_EsReemplazable() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setEtiquetas(List.of("etiqueta1"));
        dto.setEtiquetas(List.of("nueva1", "nueva2"));

        assertEquals(2, dto.getEtiquetas().size());
        assertEquals("nueva1", dto.getEtiquetas().get(0));
    }

    // CP05 - NORMAL: Lista de responsablesIds acepta múltiples IDs.
    // Entrada: lista con varios IDs
    // Resultado esperado: todos los IDs están presentes
    @Test
    @DisplayName("CP05: Lista de responsablesIds acepta múltiples IDs")
    void CP05_ResponsablesIds_AceptaMultiplesIds() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setResponsablesIds(List.of(10L, 20L, 30L, 40L));

        assertEquals(4, dto.getResponsablesIds().size());
        assertTrue(dto.getResponsablesIds().contains(20L));
    }

    // CP06 - BORDE: Etiquetas nulas son aceptadas por el DTO.
    // Entrada: setEtiquetas(null)
    // Resultado esperado: getEtiquetas() retorna null
    @Test
    @DisplayName("CP06: Etiquetas nulas son aceptadas por el DTO")
    void CP06_EtiquetasNulas_SonAceptadas() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setEtiquetas(null);

        assertNull(dto.getEtiquetas());
    }

    // CP07 - BORDE: ResponsablesIds nulas son aceptadas por el DTO.
    // Entrada: setResponsablesIds(null)
    // Resultado esperado: getResponsablesIds() retorna null
    @Test
    @DisplayName("CP07: ResponsablesIds nulos son aceptados por el DTO")
    void CP07_ResponsablesIdsNulos_SonAceptados() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setResponsablesIds(null);

        assertNull(dto.getResponsablesIds());
    }

    // CP08 - NORMAL: Dificultades SIMPLE, MEDIA y ALTA son asignables.
    // Entrada: cada valor del enum Dificultad
    // Resultado esperado: cada valor es retornado correctamente
    @Test
    @DisplayName("CP08: Todos los valores de Dificultad son asignables")
    void CP08_TodosLosValoresDificultad_SonAsignables() {
        TareaEditarDTO dto = new TareaEditarDTO();

        dto.setDificultad(Tarea.Dificultad.SIMPLE);
        assertEquals(Tarea.Dificultad.SIMPLE, dto.getDificultad());

        dto.setDificultad(Tarea.Dificultad.MEDIA);
        assertEquals(Tarea.Dificultad.MEDIA, dto.getDificultad());

        dto.setDificultad(Tarea.Dificultad.ALTA);
        assertEquals(Tarea.Dificultad.ALTA, dto.getDificultad());
    }

    // CP09 - BORDE: Lista de etiquetas vacía es aceptada.
    // Entrada: setEtiquetas(new ArrayList<>())
    // Resultado esperado: getEtiquetas() retorna lista vacía
    @Test
    @DisplayName("CP09: Lista de etiquetas vacía es aceptada")
    void CP09_ListaEtiquetasVacia_EsAceptada() {
        TareaEditarDTO dto = new TareaEditarDTO();
        dto.setEtiquetas(new ArrayList<>());

        assertNotNull(dto.getEtiquetas());
        assertTrue(dto.getEtiquetas().isEmpty());
    }
}
