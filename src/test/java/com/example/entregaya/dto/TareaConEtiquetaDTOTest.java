package com.example.entregaya.dto;

import com.example.entregaya.decorator.TareaDecoratorFactory;
import com.example.entregaya.decorator.TareaInfo;
import com.example.entregaya.decorator.TareaInfoBase;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Tests para TareaConEtiquetaDTO - DTO con etiqueta de urgencia calculada por el decorador

@DisplayName("TareaConEtiquetaDTO - Tests unitarios")
class TareaConEtiquetaDTOTest {

    // CP01 - NORMAL: fromTareaInfo con TareaInfoBase (tarea normal, más de 7 días).
    // Se justifica para verificar que el DTO extrae correctamente los datos del decorador base.
    // Entrada: TareaInfoBase que envuelve una tarea con nombre y trabajo
    // Resultados Esperados: nombre, etiquetaUrgencia y colorEtiqueta del DTO corresponden al base.
    @Test
    @DisplayName("CP01: fromTareaInfo con TareaInfoBase mapea nombre y etiqueta correctamente")
    void CP01_FromTareaInfo_ConTareaInfoBase_MapeaNombreYEtiqueta() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Normal");
        tarea.setTrabajo(trabajo);
        tarea.setFechaFinal(LocalDateTime.now().plusDays(30));
        TareaInfo tareaInfo = new TareaInfoBase(tarea);

        // Act
        TareaConEtiquetaDTO dto = TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);

        // Assert
        assertEquals("Tarea Normal", dto.nombre());
        assertEquals("Normal", dto.etiquetaUrgencia());
        assertEquals("#10b981", dto.colorEtiqueta());
    }

    // CP02 - NORMAL: etiquetaUrgencia y colorEtiqueta se copian del decorador.
    // Se justifica para confirmar que el DTO refleja la etiqueta calculada por el decorador (no la hardcodea).
    // Entrada: TareaDecoratorFactory.resolver() con tarea vencida
    // Resultados Esperados: etiquetaUrgencia == "Vencida", colorEtiqueta == "#ef4444".
    @Test
    @DisplayName("CP02: etiquetaUrgencia y colorEtiqueta reflejan el decorador aplicado")
    void CP02_FromTareaInfo_ConDecoradorVencida_RefleleEtiquetaVencida() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Vencida");
        tarea.setFechaFinal(LocalDateTime.now().minusDays(2));
        tarea.setCompletada(false);
        TareaInfo tareaInfo = TareaDecoratorFactory.resolver(tarea);

        // Act
        TareaConEtiquetaDTO dto = TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);

        // Assert
        assertEquals("Vencida", dto.etiquetaUrgencia());
        assertEquals("#ef4444", dto.colorEtiqueta());
    }

    // CP03 - NORMAL: isCompletada se copia correctamente desde el decorador.
    // Se justifica para asegurar que el estado de completado de la tarea llega al DTO sin alteración.
    // Entrada: tarea con completada = true
    // Resultados Esperados: dto.isCompletada() == true.
    @Test
    @DisplayName("CP03: isCompletada se copia correctamente al DTO")
    void CP03_FromTareaInfo_TareaCompletada_IsCompletadaEsTrue() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Completada");
        tarea.setCompletada(true);
        TareaInfo tareaInfo = TareaDecoratorFactory.resolver(tarea);

        // Act
        TareaConEtiquetaDTO dto = TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);

        // Assert
        assertTrue(dto.isCompletada());
        assertEquals("Completada", dto.etiquetaUrgencia());
    }

    // CP04 - NORMAL: responsables del DTO son los mismos que los de la tarea.
    // Se justifica para verificar que el conjunto de responsables no se transforma ni se copia.
    // Entrada: tarea con un responsable asignado
    // Resultados Esperados: dto.responsables() tiene tamaño 1 con el usuario esperado.
    @Test
    @DisplayName("CP04: responsables del DTO corresponden a los responsables de la tarea")
    void CP04_FromTareaInfo_ConResponsables_RefleleResponsables() {
        // Arrange
        User usuario = new User();
        usuario.setUsername("responsable1");
        Set<User> responsables = new HashSet<>();
        responsables.add(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea con Responsable");
        tarea.setResponsables(responsables);
        TareaInfo tareaInfo = new TareaInfoBase(tarea);

        // Act
        TareaConEtiquetaDTO dto = TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);

        // Assert
        assertEquals(1, dto.responsables().size());
        assertTrue(dto.responsables().stream()
                .anyMatch(u -> u.getUsername().equals("responsable1")));
    }

    // CP05 - NORMAL: fechas del DTO son las de la tarea original.
    // Se justifica para garantizar que las fechas de inicio y final llegan sin alteraciones al DTO.
    // Entrada: tarea con fechaInicio y fechaFinal específicas
    // Resultados Esperados: dto.fechaInicio() y dto.fechaFinal() son iguales a las de la tarea.
    @Test
    @DisplayName("CP05: fechas de inicio y final se preservan intactas en el DTO")
    void CP05_FromTareaInfo_FechasEspecificas_SePreservanEnDTO() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 1, 8, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 5, 31, 18, 0);

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea con Fechas");
        tarea.setFechaInicio(inicio);
        tarea.setFechaFinal(fin);
        TareaInfo tareaInfo = new TareaInfoBase(tarea);

        // Act
        TareaConEtiquetaDTO dto = TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);

        // Assert
        assertEquals(inicio, dto.fechaInicio());
        assertEquals(fin, dto.fechaFinal());
    }
}
