package com.example.entregaya.model;

import com.example.entregaya.builder.TareaBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Modelo Trabajo - Tests unitarios")
class TrabajoModelTest {

    // CP01 - NORMAL: agregarColaborador agrega un usuario con rol específico
    @Test
    @DisplayName("CP01: agregarColaborador agrega usuario con rol LIDER")
    void CP01_agregarColaborador_ConRolLider_AgregaColaborador() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);
        trabajo.setNombreTrabajo("Proyecto Test");

        User user = new User(1L, "alice", "pass");

        trabajo.agregarColaborador(user, ColaboradorTrabajo.Rol.LIDER);

        assertEquals(1, trabajo.getColaboradores().size());
        assertTrue(trabajo.getColaboradores().stream()
            .anyMatch(c -> c.getRol() == ColaboradorTrabajo.Rol.LIDER));
    }

    // CP02 - NORMAL: agregarColaborador sin rol usa COLABORADOR por defecto
    @Test
    @DisplayName("CP02: agregarColaborador sin rol asigna COLABORADOR por defecto")
    void CP02_agregarColaborador_SinRol_AsignaColaboradorPorDefecto() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(2L);
        trabajo.setNombreTrabajo("Proyecto Default");

        User user = new User(2L, "bob", "pass");

        trabajo.agregarColaborador(user);

        assertEquals(1, trabajo.getColaboradores().size());
        assertTrue(trabajo.getColaboradores().stream()
            .anyMatch(c -> c.getRol() == ColaboradorTrabajo.Rol.COLABORADOR));
    }

    // CP03 - BORDE: agregarColaborador no duplica si ya existe
    @Test
    @DisplayName("CP03: agregarColaborador no duplica si el usuario ya es miembro")
    void CP03_agregarColaborador_YaExiste_NoDuplica() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(3L);
        trabajo.setNombreTrabajo("Proyecto Sin Dups");

        User user = new User(3L, "carol", "pass");

        trabajo.agregarColaborador(user, ColaboradorTrabajo.Rol.LIDER);
        trabajo.agregarColaborador(user, ColaboradorTrabajo.Rol.EDITOR);

        assertEquals(1, trabajo.getColaboradores().size());
    }

    // CP04 - NORMAL: clonar trabajo crea una copia sin colaboradores
    @Test
    @DisplayName("CP04: clonar trabajo crea copia con nombre modificado y sin colaboradores")
    void CP04_clonar_CreaCopiaSinColaboradores() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(4L);
        trabajo.setNombreTrabajo("Original");
        trabajo.setDescripcion("Desc");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));

        Trabajo copia = trabajo.clonar();

        assertNull(copia.getId());
        assertTrue(copia.getNombreTrabajo().contains("copia"));
        assertEquals(0, copia.getColaboradores().size());
    }

    // CP05 - NORMAL: clonar trabajo copia las tareas
    @Test
    @DisplayName("CP05: clonar trabajo con tareas copia todas las tareas")
    void CP05_clonar_ConTareas_CopiaTareas() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(5L);
        trabajo.setNombreTrabajo("Con Tareas");

        Tarea tarea = new TareaBuilder()
            .nombre("Tarea Original")
            .dificultad(Tarea.Dificultad.MEDIA)
            .trabajo(trabajo)
            .responsables(new HashSet<>())
            .build();
        trabajo.getTareas().add(tarea);

        Trabajo copia = trabajo.clonar();

        assertEquals(1, copia.getTareas().size());
        assertTrue(copia.getTareas().stream()
            .allMatch(t -> t.getTrabajo() == copia));
    }
}

@DisplayName("Modelo Tarea - Tests unitarios")
class TareaModelTest {

    // CP01 - NORMAL: getDificultad.getPeso retorna valores correctos
    @Test
    @DisplayName("CP01: getPeso retorna 1/2/3 para SIMPLE/MEDIA/ALTA")
    void CP01_getPeso_RetornaValoresCorrectos() {
        assertEquals(1, Tarea.Dificultad.SIMPLE.getPeso());
        assertEquals(2, Tarea.Dificultad.MEDIA.getPeso());
        assertEquals(3, Tarea.Dificultad.ALTA.getPeso());
    }

    // CP02 - NORMAL: clonar tarea crea una copia con [Copia] en el nombre
    @Test
    @DisplayName("CP02: clonar tarea agrega prefijo [Copia] al nombre")
    void CP02_clonar_AgregaPrefijoAlNombre() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);

        Tarea original = new TareaBuilder()
            .nombre("Mi Tarea")
            .dificultad(Tarea.Dificultad.ALTA)
            .trabajo(trabajo)
            .responsables(new HashSet<>())
            .build();

        Tarea clon = original.clonar(trabajo);

        assertTrue(clon.getNombre().startsWith("[Copia]"));
        assertNull(clon.getId());
        assertFalse(clon.getIsCompletada());
    }

    // CP03 - NORMAL: equals y hashCode basados en id
    @Test
    @DisplayName("CP03: equals retorna true para dos tareas con el mismo id")
    void CP03_equals_ConMismoId_RetornaTrue() {
        Tarea t1 = new Tarea();
        t1.setId(1L);
        Tarea t2 = new Tarea();
        t2.setId(1L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    // CP04 - NORMAL: equals retorna false para tareas con ids distintos
    @Test
    @DisplayName("CP04: equals retorna false para tareas con ids distintos")
    void CP04_equals_ConIdsDistintos_RetornaFalse() {
        Tarea t1 = new Tarea();
        t1.setId(1L);
        Tarea t2 = new Tarea();
        t2.setId(2L);

        assertNotEquals(t1, t2);
    }

    // CP05 - BORDE: equals con id nulo retorna false
    @Test
    @DisplayName("CP05: equals con id nulo retorna false")
    void CP05_equals_ConIdNulo_RetornaFalse() {
        Tarea t1 = new Tarea();
        Tarea t2 = new Tarea();

        assertNotEquals(t1, t2);
    }
}

@DisplayName("Modelo Notificacion - Tests unitarios")
class NotificacionModelTest {

    // CP01 - NORMAL: Constructor con destinatario y mensaje
    @Test
    @DisplayName("CP01: Constructor con destinatario y mensaje asigna tipo TAREA por defecto")
    void CP01_Constructor_ConDestinatarioYMensaje_AsignaTipoPorDefecto() {
        User user = new User();
        user.setUsername("test");

        Notificacion n = new Notificacion(user, "Mensaje de prueba");

        assertEquals(Notificacion.TipoNotificacion.TAREA, n.getTipo());
        assertFalse(n.isLeida());
        assertNotNull(n.getFechaCreacion());
        assertEquals("Mensaje de prueba", n.getMensaje());
    }

    // CP02 - NORMAL: Constructor con tipo específico
    @Test
    @DisplayName("CP02: Constructor con tipo MIEMBRO asigna el tipo correctamente")
    void CP02_Constructor_ConTipoMiembro_AsignaTipoMiembro() {
        User user = new User();
        user.setUsername("test");

        Notificacion n = new Notificacion(user, "Nuevo miembro", Notificacion.TipoNotificacion.MIEMBRO);

        assertEquals(Notificacion.TipoNotificacion.MIEMBRO, n.getTipo());
    }

    // CP03 - NORMAL: Marcar como leída
    @Test
    @DisplayName("CP03: setLeida(true) cambia el estado a leída")
    void CP03_setLeida_CambiaEstado() {
        User user = new User();
        Notificacion n = new Notificacion(user, "Test");

        assertFalse(n.isLeida());
        n.setLeida(true);
        assertTrue(n.isLeida());
    }
}

@DisplayName("Modelo ColaboradorTrabajo - Tests unitarios")
class ColaboradorTrabajoModelTest {

    // CP01 - NORMAL: Constructor con trabajo, user y rol asigna todos los campos
    @Test
    @DisplayName("CP01: Constructor asigna trabajo, user y rol correctamente")
    void CP01_Constructor_AsignaCampos() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);
        User user = new User(2L, "test", "pass");

        ColaboradorTrabajo colaborador = new ColaboradorTrabajo(trabajo, user, ColaboradorTrabajo.Rol.EDITOR);

        assertEquals(ColaboradorTrabajo.Rol.EDITOR, colaborador.getRol());
        assertEquals(user, colaborador.getUser());
        assertEquals(trabajo, colaborador.getTrabajo());
    }

    // CP02 - NORMAL: ColaboradorTrabajoId equals y hashCode
    @Test
    @DisplayName("CP02: ColaboradorTrabajoId equals funciona con mismos ids")
    void CP02_ColaboradorTrabajoId_EqualsConMismosIds() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        ColaboradorTrabajoId id2 = new ColaboradorTrabajoId(1L, 2L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    // CP03 - NORMAL: ColaboradorTrabajoId no equals con ids distintos
    @Test
    @DisplayName("CP03: ColaboradorTrabajoId not equals con ids distintos")
    void CP03_ColaboradorTrabajoId_NotEqualsConIdsDist() {
        ColaboradorTrabajoId id1 = new ColaboradorTrabajoId(1L, 2L);
        ColaboradorTrabajoId id2 = new ColaboradorTrabajoId(1L, 3L);

        assertNotEquals(id1, id2);
    }
}
