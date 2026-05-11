package com.example.entregaya.prototype;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Tests para el patrón Prototype - clonar Tarea y Trabajo

@DisplayName("Prototype - Tests unitarios de clonación")
class PrototypeTest {

    // CP01 - NORMAL: Tarea.clonar retorna un objeto con id null.
    // Se justifica para garantizar que el clon no hereda el identificador del original,
    // evitando colisiones en la base de datos al persistir.
    // Entrada: Tarea con id=null (no persistida) y nombre "Diseño UI"
    // Resultados Esperados: El clon tiene id null.
    @Test
    @DisplayName("CP01: Tarea.clonar retorna objeto con id null")
    void CP01_TareaCloner_ElClonTieneIdNull() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto");

        Tarea original = new Tarea();
        original.setNombre("Diseño UI");
        original.setDescripcion("Diseño de la interfaz");
        original.setDificultad(Tarea.Dificultad.MEDIA);
        original.setTrabajo(trabajo);

        // Act
        Tarea clon = original.clonar(trabajo);

        // Assert
        assertNull(clon.getId());
    }

    // CP02 - NORMAL: Tarea.clonar prefija "[Copia]" al nombre original.
    // Se justifica para que el usuario identifique visualmente que la tarea es una copia.
    // Entrada: Tarea original con nombre "Revisión de código"
    // Resultados Esperados: El nombre del clon es "[Copia] Revisión de código".
    @Test
    @DisplayName("CP02: Tarea.clonar agrega prefijo '[Copia]' al nombre del clon")
    void CP02_TareaCloner_ElClonTienePrefijoCopia() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea original = new Tarea();
        original.setNombre("Revisión de código");
        original.setDificultad(Tarea.Dificultad.ALTA);
        original.setTrabajo(trabajo);

        // Act
        Tarea clon = original.clonar(trabajo);

        // Assert
        assertEquals("[Copia] Revisión de código", clon.getNombre());
    }

    // CP03 - BORDE: Tarea.clonar siempre produce un clon con completada = false.
    // Se justifica para que una tarea clonada comience siempre en estado pendiente,
    // independientemente del estado del original.
    // Entrada: Tarea original con completada = true
    // Resultados Esperados: El clon tiene completada = false.
    @Test
    @DisplayName("CP03: Tarea.clonar produce clon con completada false aunque el original esté completado")
    void CP03_TareaCloner_ClonSiempreTieneCompletadaFalse() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea original = new Tarea();
        original.setNombre("Tarea Terminada");
        original.setCompletada(true);
        original.setDificultad(Tarea.Dificultad.SIMPLE);
        original.setTrabajo(trabajo);

        // Act
        Tarea clon = original.clonar(trabajo);

        // Assert
        assertFalse(clon.getIsCompletada());
    }

    // CP04 - NORMAL: Trabajo.clonar retorna un trabajo con nombre modificado.
    // Se justifica para que el usuario identifique el trabajo clonado entre los existentes.
    // Entrada: Trabajo con nombre "Sprint 1"
    // Resultados Esperados: El clon tiene nombre "Sprint 1 (copia) ".
    @Test
    @DisplayName("CP04: Trabajo.clonar retorna trabajo con sufijo '(copia)' en el nombre")
    void CP04_TrabajoCloner_ElClonTieneSufijoCopia() {
        // Arrange
        Trabajo original = new Trabajo();
        original.setNombreTrabajo("Sprint 1");
        original.setDescripcion("Primer sprint del proyecto");
        original.setFechaInicio(LocalDateTime.of(2026, 5, 1, 9, 0));
        original.setFechaEntrega(LocalDateTime.of(2026, 5, 31, 18, 0));

        // Act
        Trabajo clon = original.clonar();

        // Assert
        assertEquals("Sprint 1 (copia) ", clon.getNombreTrabajo());
        assertEquals("Primer sprint del proyecto", clon.getDescripcion());
    }

    // CP05 - NORMAL: Trabajo.clonar clona las tareas del trabajo con el mismo nombre.
    // Se justifica para verificar que el clon de un trabajo reproduce fielmente sus tareas.
    // Entrada: Trabajo con 1 tarea llamada "Implementar login"
    // Resultados Esperados: El clon tiene 1 tarea con nombre "Implementar login".
    @Test
    @DisplayName("CP05: Trabajo.clonar clona las tareas manteniendo sus nombres originales")
    void CP05_TrabajoCloner_ClonaTareasConNombreOriginal() {
        // Arrange
        Trabajo original = new Trabajo();
        original.setNombreTrabajo("Proyecto Completo");
        original.setFechaInicio(LocalDateTime.of(2026, 4, 1, 9, 0));
        original.setFechaEntrega(LocalDateTime.of(2026, 6, 30, 18, 0));

        Tarea tarea = new Tarea();
        tarea.setNombre("Implementar login");
        tarea.setDificultad(Tarea.Dificultad.ALTA);
        tarea.setFechaInicio(LocalDateTime.of(2026, 4, 1, 9, 0));
        tarea.setFechaFinal(LocalDateTime.of(2026, 4, 15, 18, 0));
        tarea.setTrabajo(original);
        original.getTareas().add(tarea);

        // Act
        Trabajo clon = original.clonar();

        // Assert
        assertEquals(1, clon.getTareas().size());
        assertTrue(clon.getTareas().stream()
                .anyMatch(t -> t.getNombre().equals("Implementar login")));
    }
}
