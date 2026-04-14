package com.example.entregaya.prototype;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;

/**
 * Contrato Prototype para Tarea.
 * Se usa internamente en Trabajo#clonar() y por HU-2 directamente.
 */

public interface TareaPrototype {

    /**
     * Retorna una copia de la tarea.
     * - id          → null
     * - completada  → false
     * - fechas      → null (se reasignan manualmente si aplica)
     * - nombre      → "[Copia] <nombre original>"   (HU-2 lo usa así)
     *                 o el nombre original           (cuando lo llama Trabajo#clonar)
     * - responsables → copia del Set original
     * - trabajo     → debe reasignarse externamente
     */

    Tarea clonar(Trabajo trabajo);
}
