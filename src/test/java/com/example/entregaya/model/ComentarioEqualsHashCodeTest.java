package com.example.entregaya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para equals() y hashCode() de Comentario.
 * Cubre las 2 líneas + 2 condiciones que Sonar marca sin cubrir en código nuevo.
 */
@DisplayName("Comentario - Tests de equals y hashCode")
class ComentarioEqualsHashCodeTest {

    @Test
    @DisplayName("CP01: equals con la misma instancia retorna true (rama this == o)")
    void CP01_equals_MismaInstancia_RetornaTrue() {
        Comentario c = new Comentario("hola", new Tarea(), new User());
        c.setId(1L);

        assertEquals(c, c);
    }

    @Test
    @DisplayName("CP02: equals con objeto de otra clase retorna false (rama !instanceof)")
    void CP02_equals_OtraClase_RetornaFalse() {
        Comentario c = new Comentario("hola", new Tarea(), new User());
        c.setId(1L);

        assertNotEquals(c, "no soy un comentario");
        assertNotEquals(c, null);
    }

    @Test
    @DisplayName("CP03: equals con dos comentarios con el mismo id retorna true")
    void CP03_equals_MismoId_RetornaTrue() {
        Comentario c1 = new Comentario("a", new Tarea(), new User());
        c1.setId(5L);
        Comentario c2 = new Comentario("b", new Tarea(), new User());
        c2.setId(5L);

        assertEquals(c1, c2);
    }

    @Test
    @DisplayName("CP04: equals con dos comentarios con id distinto retorna false")
    void CP04_equals_IdsDistintos_RetornaFalse() {
        Comentario c1 = new Comentario("a", new Tarea(), new User());
        c1.setId(5L);
        Comentario c2 = new Comentario("a", new Tarea(), new User());
        c2.setId(7L);

        assertNotEquals(c1, c2);
    }

    @Test
    @DisplayName("CP05: equals con id null en this retorna false (cubre la rama id != null)")
    void CP05_equals_IdNull_RetornaFalse() {
        Comentario c1 = new Comentario("a", new Tarea(), new User());
        // id queda null (no se llama setId)
        Comentario c2 = new Comentario("a", new Tarea(), new User());
        c2.setId(1L);

        assertNotEquals(c1, c2);
    }

    @Test
    @DisplayName("CP06: hashCode con id null retorna 0 y con id no-null retorna hash del id")
    void CP06_hashCode_CubreAmbasRamas() {
        Comentario sinId = new Comentario("a", new Tarea(), new User());
        Comentario conId = new Comentario("a", new Tarea(), new User());
        conId.setId(42L);

        assertEquals(0, sinId.hashCode());
        assertEquals(Long.hashCode(42L), conId.hashCode());
    }
}
