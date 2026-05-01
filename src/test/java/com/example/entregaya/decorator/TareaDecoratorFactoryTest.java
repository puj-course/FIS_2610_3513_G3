package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Tests para TareaDecoratorFactory - Patron Decorator HU-29 #316
class TareaDecoratorFactoryTest {

    // CP06 - NORMAL: Prioridad de Estado. Se justifica para validar que una tarea terminada siempre use el decorador de "Completada".
    // Entrada: completada = true
    // Resultados Esperados: Retorna CompletadaDecorator.
    @Test
    void CP06_Resolver_TareaCompletada_RetornaCompletadaDecorator() {
        // Arrange
        Tarea tareaCompletada = new Tarea();
        tareaCompletada.setCompletada(true);
        tareaCompletada.setFechaFinal(LocalDateTime.now().minusDays(5));

        // Act
        TareaInfo decorator = TareaDecoratorFactory.resolver(tareaCompletada);

        // Assert
        assertInstanceOf(CompletadaDecorator.class, decorator);
        assertEquals("Completada", decorator.getEtiquetaUrgencia());
    }

    // CP07 - NEGATIVA: Deteccion de Vencimiento. Se justifica para asegurar que el sistema identifique correctamente tareas fuera de plazo.
    // Entrada: fechaFinal = ayer
    // Resultados Esperados: Retorna VencidaDecorator.
    @Test
    void CP07_Resolver_TareaVencida_RetornaVencidaDecorator() {
        // Arrange
        Tarea tareaVencida = new Tarea();
        tareaVencida.setCompletada(false);
        tareaVencida.setFechaFinal(LocalDateTime.now().minusDays(1));

        // Act
        TareaInfo decorator = TareaDecoratorFactory.resolver(tareaVencida);

        // Assert
        assertInstanceOf(VencidaDecorator.class, decorator);
        assertEquals("Vencida", decorator.getEtiquetaUrgencia());
        assertEquals("#ef4444", decorator.getColorEtiqueta());
    }

    // CP08 - BORDE: Limite de Urgencia. Se justifica para validar el umbral exacto donde una tarea pasa de ser normal a urgente.
    // Entrada: fechaFinal = +2 dias
    // Resultados Esperados: Retorna UrgenteDecorator.
    @Test
    void CP08_Resolver_LimiteUrgencia2Dias_RetornaUrgenteDecorator() {
        // Arrange
        Tarea tareaUrgente = new Tarea();
        tareaUrgente.setCompletada(false);
        tareaUrgente.setFechaFinal(LocalDateTime.now().plusDays(2));

        // Act
        TareaInfo decorator = TareaDecoratorFactory.resolver(tareaUrgente);

        // Assert
        assertInstanceOf(UrgenteDecorator.class, decorator);
        assertEquals("Urgente", decorator.getEtiquetaUrgencia());
        assertEquals("#ef4444", decorator.getColorEtiqueta());
    }

    // CP09 - BORDE: Limite de Proximidad. Se justifica para verificar la precision del factory en el limite superior de tareas cercanas.
    // Entrada: fechaFinal = +7 dias
    // Resultados Esperados: Retorna ProximaDecorator.
    @Test
    void CP09_Resolver_LimiteProximidad7Dias_RetornaProximaDecorator() {
        // Arrange
        Tarea tareaProxima = new Tarea();
        tareaProxima.setCompletada(false);
        tareaProxima.setFechaFinal(LocalDateTime.now().plusDays(7));

        // Act
        TareaInfo decorator = TareaDecoratorFactory.resolver(tareaProxima);

        // Assert
        assertInstanceOf(ProximaDecorator.class, decorator);
        assertEquals("Próxima", decorator.getEtiquetaUrgencia());
        assertEquals("#f59e0b", decorator.getColorEtiqueta());
    }

    // CP10 - NEGATIVA: Objeto Nulo. Se justifica como medida de robustez para evitar caidas del sistema al intentar procesar tareas inexistentes.
    // Entrada: tarea = null
    // Resultados Esperados: Lanza IllegalArgumentException.
    @Test
    void CP10_Constructor_ConTareaNull_LanzaExcepcion() {
        // Arrange
        Tarea tareaNula = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new TareaInfoBase(tareaNula));
    }
}
