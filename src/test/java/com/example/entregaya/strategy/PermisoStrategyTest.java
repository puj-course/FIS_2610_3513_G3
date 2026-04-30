package com.example.entregaya.strategy;

import com.example.entregaya.model.ColaboradorTrabajo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests para PermisoStrategy - Patron Strategy HU-23 #287
class PermisoStrategyTest {

    // NORMAL (CP11): Lider en LideroEditorStrategy
    @Test
    void CP11_LideroEditorStrategy_ConRolLider_RetornaTrue() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.LIDER);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertTrue(tienePermiso, "LIDER debe tener permiso en LideroEditorStrategy");
    }

    // NORMAL (CP12): Editor en LideroEditorStrategy
    @Test
    void CP12_LideroEditorStrategy_ConRolEditor_RetornaTrue() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.EDITOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertTrue(tienePermiso, "EDITOR debe tener permiso en LideroEditorStrategy");
    }

    // NEGATIVA (CP13): Colaborador en LideroEditorStrategy
    @Test
    void CP13_LideroEditorStrategy_ConRolColaborador_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.COLABORADOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "COLABORADOR no debe tener permiso en LideroEditorStrategy");
    }

    // NORMAL (CP14): Lider en SoloLiderStrategy
    @Test
    void CP14_SoloLiderStrategy_ConRolLider_RetornaTrue() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.LIDER);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertTrue(tienePermiso, "LIDER debe tener permiso en SoloLiderStrategy");
    }

    // NEGATIVA (CP15): Editor en SoloLiderStrategy (exclusividad de permisos criticos)
    @Test
    void CP15_SoloLiderStrategy_ConRolEditor_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.EDITOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "EDITOR no debe tener permiso en SoloLiderStrategy");
    }

    // NEGATIVA (CP15b): Colaborador en SoloLiderStrategy
    @Test
    void CP15b_SoloLiderStrategy_ConRolColaborador_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.COLABORADOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "COLABORADOR no debe tener permiso en SoloLiderStrategy");
    }

    // BORDE: Rol Null en LideroEditorStrategy (Fail-safe)
    @Test
    void BORDE_LideroEditorStrategy_ConRolNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(null);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "Un rol null no deberia tener permisos (fail-safe)");
    }

    // BORDE: Colaborador Null en LideroEditorStrategy
    @Test
    void BORDE_LideroEditorStrategy_ConColaboradorNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();

        // Act
        boolean tienePermiso = estrategia.tienePermiso(null);

        // Assert
        assertFalse(tienePermiso, "Un colaborador null no deberia tener permisos (fail-safe)");
    }

    // BORDE: Rol Null en SoloLiderStrategy (Fail-safe)
    @Test
    void BORDE_SoloLiderStrategy_ConRolNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(null);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "Un rol null no deberia tener permisos (fail-safe)");
    }

    // BORDE: Colaborador Null en SoloLiderStrategy
    @Test
    void BORDE_SoloLiderStrategy_ConColaboradorNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();

        // Act
        boolean tienePermiso = estrategia.tienePermiso(null);

        // Assert
        assertFalse(tienePermiso, "Un colaborador null no deberia tener permisos (fail-safe)");
    }
}
