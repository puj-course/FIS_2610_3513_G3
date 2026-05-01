package com.example.entregaya.strategy;

import com.example.entregaya.model.ColaboradorTrabajo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests para PermisoStrategy - Patron Strategy HU-23 #287
class PermisoStrategyTest {

    // CP11 - NORMAL: Acceso Autorizado
    // Se justifica para confirmar que el rol Líder tiene permisos completos en la estrategia compartida.
    // Estrategia: LiderOEditor, Rol: LIDER
    // Resultado Esperado: Retorna true.
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

    // CP12 - NEGATIVA: Denegación por Rol
    // Se justifica para garantizar que un Colaborador no pueda realizar acciones de edición.
    // Estrategia: LiderOEditor, Rol: COLABORADOR
    // Resultado Esperado: Retorna false.
    @Test
    void CP12_LideroEditorStrategy_ConRolColaborador_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.COLABORADOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "COLABORADOR no debe tener permiso en LideroEditorStrategy");
    }

    // CP13 - NEGATIVA: Restricción Crítica
    // Se justifica para validar que solo el Líder pueda realizar acciones de alta sensibilidad.
    // Estrategia: SoloLider, Rol: EDITOR
    // Resultado Esperado: Retorna false.
    @Test
    void CP13_SoloLiderStrategy_ConRolEditor_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(ColaboradorTrabajo.Rol.EDITOR);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "EDITOR no debe tener permiso en SoloLiderStrategy");
    }

    // CP14 - BORDE: Fallo Seguro (LiderOEditor)
    // Se justifica para verificar que ante un rol nulo, el sistema deniegue el acceso por defecto.
    // Rol: null
    // Resultado Esperado: Retorna false.
    @Test
    void CP14_LideroEditorStrategy_ConRolNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Lideroeditorstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(null);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "Un rol null no deberia tener permisos (fail-safe)");
    }

    // CP15 - BORDE: Fallo Seguro (SoloLider)
    // Se justifica para asegurar que la seguridad sea estricta incluso ante datos de entrada incompletos.
    // Rol: null
    // Resultado Esperado: Retorna false.
    @Test
    void CP15_SoloLiderStrategy_ConRolNull_RetornaFalse() {
        // Arrange
        Permisostrategy estrategia = new Sololiderstrategy();
        ColaboradorTrabajo colaborador = new ColaboradorTrabajo();
        colaborador.setRol(null);

        // Act
        boolean tienePermiso = estrategia.tienePermiso(colaborador);

        // Assert
        assertFalse(tienePermiso, "Un rol null no deberia tener permisos (fail-safe)");
    }
}
