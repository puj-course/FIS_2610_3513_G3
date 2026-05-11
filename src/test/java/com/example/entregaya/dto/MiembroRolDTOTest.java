package com.example.entregaya.dto;

import com.example.entregaya.model.ColaboradorTrabajo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests para MiembroRolDTO - representación de un miembro con su rol en un trabajo

@DisplayName("MiembroRolDTO - Tests unitarios")
class MiembroRolDTOTest {

    // CP01 - NORMAL: Constructor con todos los parámetros.
    // Se justifica para verificar que el constructor asigna userId, username y rol correctamente.
    // Entrada: userId=1L, username="alice", rol=LIDER
    // Resultados Esperados: Cada getter retorna el valor correspondiente.
    @Test
    @DisplayName("CP01: Constructor con todos los parámetros asigna campos correctamente")
    void CP01_Constructor_ConTodosLosParametros_AsignaCampos() {
        // Arrange & Act
        MiembroRolDTO dto = new MiembroRolDTO(1L, "alice", ColaboradorTrabajo.Rol.LIDER);

        // Assert
        assertEquals(1L, dto.getUserId());
        assertEquals("alice", dto.getUsername());
        assertEquals(ColaboradorTrabajo.Rol.LIDER, dto.getRol());
    }

    // CP02 - NORMAL: Constructor vacío y uso de setters.
    // Se justifica para garantizar que el DTO puede construirse progresivamente sin el constructor parametrizado.
    // Entrada: Constructor vacío + setUserId, setUsername, setRol
    // Resultados Esperados: Cada getter retorna el valor asignado por su setter.
    @Test
    @DisplayName("CP02: Constructor vacío y setters asignan valores correctamente")
    void CP02_ConstructorVacio_Setters_AsignanValores() {
        // Arrange
        MiembroRolDTO dto = new MiembroRolDTO();

        // Act
        dto.setUserId(2L);
        dto.setUsername("bob");
        dto.setRol(ColaboradorTrabajo.Rol.EDITOR);

        // Assert
        assertEquals(2L, dto.getUserId());
        assertEquals("bob", dto.getUsername());
        assertEquals(ColaboradorTrabajo.Rol.EDITOR, dto.getRol());
    }

    // CP03 - NORMAL: Rol LIDER asignado correctamente.
    // Se justifica para confirmar que el rol de máximos permisos se representa correctamente en el DTO.
    // Entrada: rol = ColaboradorTrabajo.Rol.LIDER
    // Resultados Esperados: getRol() == LIDER.
    @Test
    @DisplayName("CP03: Rol LIDER se asigna y recupera correctamente")
    void CP03_SetRol_ConRolLider_RetornaLider() {
        // Arrange
        MiembroRolDTO dto = new MiembroRolDTO();

        // Act
        dto.setRol(ColaboradorTrabajo.Rol.LIDER);

        // Assert
        assertEquals(ColaboradorTrabajo.Rol.LIDER, dto.getRol());
    }

    // CP04 - NORMAL: Rol EDITOR asignado correctamente.
    // Se justifica para verificar que el rol de edición se almacena y recupera sin alteraciones.
    // Entrada: rol = ColaboradorTrabajo.Rol.EDITOR
    // Resultados Esperados: getRol() == EDITOR.
    @Test
    @DisplayName("CP04: Rol EDITOR se asigna y recupera correctamente")
    void CP04_SetRol_ConRolEditor_RetornaEditor() {
        // Arrange
        MiembroRolDTO dto = new MiembroRolDTO();

        // Act
        dto.setRol(ColaboradorTrabajo.Rol.EDITOR);

        // Assert
        assertEquals(ColaboradorTrabajo.Rol.EDITOR, dto.getRol());
    }

    // CP05 - NORMAL: Rol COLABORADOR asignado correctamente.
    // Se justifica para asegurar que el rol de menor jerarquía se maneja igual que los demás.
    // Entrada: rol = ColaboradorTrabajo.Rol.COLABORADOR
    // Resultados Esperados: getRol() == COLABORADOR.
    @Test
    @DisplayName("CP05: Rol COLABORADOR se asigna y recupera correctamente")
    void CP05_SetRol_ConRolColaborador_RetornaColaborador() {
        // Arrange
        MiembroRolDTO dto = new MiembroRolDTO();

        // Act
        dto.setRol(ColaboradorTrabajo.Rol.COLABORADOR);

        // Assert
        assertEquals(ColaboradorTrabajo.Rol.COLABORADOR, dto.getRol());
    }
}
