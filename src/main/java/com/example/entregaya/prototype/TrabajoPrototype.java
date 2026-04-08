package com.example.entregaya.prototype;

import com.example.entregaya.model.Trabajo;


/**
 * Patrón GoF Prototype — Creacional
 *
 * Define el contrato para que cualquier entidad de tipo "Trabajo"
 * pueda producir una copia profunda de sí misma.
 *
 * Permite crear nuevos trabajos basados en uno existente sin acoplar
 * al llamador con los detalles internos de construcción.
 */

public interface TrabajoPrototype {

    /**
     * Retorna una copia profunda del trabajo.
     * Reglas de la copia:
     *   - id          → null   (se asignará al persistir)
     *   - nombre      → "<nombre original> (copia)"
     *   - descripcion → igual
     *   - fechas      → iguales
     *   - tareas      → clonadas, todas con completada=false, sin id
     *   - colaboradores → VACÍO (el creador se agrega después como LIDER)
     */

    Trabajo clonar();
}
