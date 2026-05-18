package com.example.entregaya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Modelo Comentario - Tests unitarios")
class ComentarioModelTest {

    // CP01 - NORMAL: constructor con campos asigna correctamente
    @Test
    @DisplayName("CP01: constructor con contenido, tarea y autor asigna campos correctamente")
    void CP01_constructor_AsignaCampos() {
        User autor = new User(1L, "alice", "pass");
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTrabajo(trabajo);

        Comentario c = new Comentario("Buen trabajo", tarea, autor);

        assertEquals("Buen trabajo", c.getContenido());
        assertEquals(tarea, c.getTarea());
        assertEquals(autor, c.getAutor());
        assertNotNull(c.getFechaCreacion());
    }

    // CP02 - NORMAL: equals con mismo id devuelve true
    @Test
    @DisplayName("CP02: equals con mismo id retorna true")
    void CP02_equals_ConMismoId_RetornaTrue() {
        Comentario c1 = new Comentario();
        c1.setId(1L);
        Comentario c2 = new Comentario();
        c2.setId(1L);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    // CP03 - NORMAL: equals con ids distintos devuelve false
    @Test
    @DisplayName("CP03: equals con ids distintos retorna false")
    void CP03_equals_ConIdsDist_RetornaFalse() {
        Comentario c1 = new Comentario();
        c1.setId(1L);
        Comentario c2 = new Comentario();
        c2.setId(2L);

        assertNotEquals(c1, c2);
    }

    // CP04 - BORDE: equals con id nulo retorna false
    @Test
    @DisplayName("CP04: equals con id nulo retorna false")
    void CP04_equals_ConIdNulo_RetornaFalse() {
        Comentario c1 = new Comentario();
        Comentario c2 = new Comentario();

        assertNotEquals(c1, c2);
    }

    // CP05 - NORMAL: setters y getters funcionan correctamente
    @Test
    @DisplayName("CP05: setters y getters funcionan correctamente")
    void CP05_settersGetters_FuncionanCorrectamente() {
        Comentario c = new Comentario();
        LocalDateTime now = LocalDateTime.now();
        User autor = new User(2L, "bob", "pass");
        Tarea tarea = new Tarea();
        tarea.setId(5L);

        c.setId(10L);
        c.setContenido("Nuevo contenido");
        c.setFechaCreacion(now);
        c.setAutor(autor);
        c.setTarea(tarea);

        assertEquals(10L, c.getId());
        assertEquals("Nuevo contenido", c.getContenido());
        assertEquals(now, c.getFechaCreacion());
        assertEquals(autor, c.getAutor());
        assertEquals(tarea, c.getTarea());
    }
}

@DisplayName("Modelo User - Tests unitarios")
class UserModelTest {

    // CP01 - NORMAL: constructor con id, username y password asigna campos
    @Test
    @DisplayName("CP01: constructor con id, username y password asigna los campos")
    void CP01_constructor_AsignaCampos() {
        User u = new User(1L, "alice", "secret");

        assertEquals(1L, u.getId());
        assertEquals("alice", u.getUsername());
        assertEquals("secret", u.getPassword());
    }

    // CP02 - NORMAL: equals con mismo id devuelve true
    @Test
    @DisplayName("CP02: equals con mismo id retorna true")
    void CP02_equals_ConMismoId_RetornaTrue() {
        User u1 = new User(1L, "alice", "pass");
        User u2 = new User(1L, "alice2", "pass2");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    // CP03 - NORMAL: equals con ids distintos devuelve false
    @Test
    @DisplayName("CP03: equals con ids distintos retorna false")
    void CP03_equals_ConIdsDist_RetornaFalse() {
        User u1 = new User(1L, "alice", "pass");
        User u2 = new User(2L, "alice", "pass");

        assertNotEquals(u1, u2);
    }

    // CP04 - BORDE: equals con id nulo retorna false
    @Test
    @DisplayName("CP04: equals con id nulo retorna false")
    void CP04_equals_ConIdNulo_RetornaFalse() {
        User u1 = new User();
        User u2 = new User();

        assertNotEquals(u1, u2);
    }

    // CP05 - NORMAL: setters de email y telegramChatId funcionan
    @Test
    @DisplayName("CP05: setters de email y telegramChatId asignan correctamente")
    void CP05_settersEmailYTelegram_AsignanCorrectamente() {
        User u = new User();
        u.setEmail("alice@test.com");
        u.setPhoneNumber("987654321");
        u.setUsername("alice");
        u.setPassword("newpass");

        assertEquals("alice@test.com", u.getEmail());
        assertEquals("987654321", u.getPhoneNumber());
        assertEquals("alice", u.getUsername());
        assertEquals("newpass", u.getPassword());
    }

    // CP06 - BORDE: telegramChatId puede ser null
    @Test
    @DisplayName("CP06: getTelegramChatId retorna null si no se ha asignado")
    void CP06_telegramChatId_SinAsignar_RetornaNull() {
        User u = new User(1L, "alice", "pass");

        assertNull(u.getPhoneNumber());
    }
}

@DisplayName("Modelo HistorialEvento - Tests unitarios")
class HistorialEventoModelTest {

    // CP01 - NORMAL: constructor completo asigna todos los campos
    @Test
    @DisplayName("CP01: constructor con todos los campos asigna correctamente")
    void CP01_constructor_AsignaTodosLosCampos() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);
        Tarea tarea = new Tarea();
        tarea.setId(2L);
        LocalDateTime fecha = LocalDateTime.now();

        HistorialEvento evento = new HistorialEvento(trabajo,
                HistorialEvento.TipoEvento.CREACION_TAREA,
                "Tarea creada", "detalles extra", "alice", fecha, tarea);

        assertEquals(trabajo, evento.getTrabajo());
        assertEquals(HistorialEvento.TipoEvento.CREACION_TAREA, evento.getTipoEvento());
        assertEquals("Tarea creada", evento.getDescripcion());
        assertEquals("detalles extra", evento.getDetalles());
        assertEquals("alice", evento.getUsuarioAccion());
        assertEquals(fecha, evento.getFechaEvento());
        assertEquals(tarea, evento.getTarea());
    }

    // CP02 - NORMAL: constructor parcial (sin tarea ni detalles) funciona
    @Test
    @DisplayName("CP02: constructor parcial asigna campos básicos")
    void CP02_constructorParcial_AsignaCamposBasicos() {
        Trabajo trabajo = new Trabajo();
        trabajo.setId(1L);
        LocalDateTime fecha = LocalDateTime.now();

        HistorialEvento evento = new HistorialEvento(trabajo,
                HistorialEvento.TipoEvento.INGRESO_MIEMBRO,
                "Miembro se unió", "bob", fecha);

        assertEquals(trabajo, evento.getTrabajo());
        assertEquals(HistorialEvento.TipoEvento.INGRESO_MIEMBRO, evento.getTipoEvento());
        assertEquals("bob", evento.getUsuarioAccion());
    }

    // CP03 - NORMAL: TipoEvento tiene descripcionDefault correcta
    @Test
    @DisplayName("CP03: cada TipoEvento tiene su descripcionDefault correcta")
    void CP03_tipoEvento_DescripcionDefaultCorrecta() {
        assertEquals("Tarea creada",
                HistorialEvento.TipoEvento.CREACION_TAREA.getDescripcionDefault());
        assertEquals("Estado de tarea cambiado",
                HistorialEvento.TipoEvento.CAMBIO_ESTADO_TAREA.getDescripcionDefault());
        assertEquals("Miembro se unió",
                HistorialEvento.TipoEvento.INGRESO_MIEMBRO.getDescripcionDefault());
        assertEquals("Miembro abandonó",
                HistorialEvento.TipoEvento.SALIDA_MIEMBRO.getDescripcionDefault());
        assertEquals("Rol de miembro cambió",
                HistorialEvento.TipoEvento.CAMBIO_ROL.getDescripcionDefault());
        assertEquals("Comentario editado",
                HistorialEvento.TipoEvento.COMENTARIO_EDITADO.getDescripcionDefault());
        assertEquals("Comentario eliminado",
                HistorialEvento.TipoEvento.COMENTARIO_ELIMINADO.getDescripcionDefault());
    }

    // CP04 - NORMAL: setters y getters del modelo
    @Test
    @DisplayName("CP04: setters y getters del modelo funcionan correctamente")
    void CP04_settersGetters_FuncionanCorrectamente() {
        HistorialEvento evento = new HistorialEvento();
        Trabajo trabajo = new Trabajo();
        trabajo.setId(3L);
        Tarea tarea = new Tarea();
        tarea.setId(4L);
        LocalDateTime fecha = LocalDateTime.of(2026, 5, 1, 10, 0);

        evento.setId(99L);
        evento.setTrabajo(trabajo);
        evento.setTipoEvento(HistorialEvento.TipoEvento.EDICION_TRABAJO);
        evento.setDescripcion("Trabajo editado");
        evento.setDetalles("Cambio de nombre");
        evento.setUsuarioAccion("carlos");
        evento.setFechaEvento(fecha);
        evento.setTarea(tarea);

        assertEquals(99L, evento.getId());
        assertEquals(trabajo, evento.getTrabajo());
        assertEquals(HistorialEvento.TipoEvento.EDICION_TRABAJO, evento.getTipoEvento());
        assertEquals("Trabajo editado", evento.getDescripcion());
        assertEquals("Cambio de nombre", evento.getDetalles());
        assertEquals("carlos", evento.getUsuarioAccion());
        assertEquals(fecha, evento.getFechaEvento());
        assertEquals(tarea, evento.getTarea());
    }

    // CP05 - NORMAL: todos los TipoEvento están presentes en el enum
    @Test
    @DisplayName("CP05: todos los valores de TipoEvento existen y son accesibles")
    void CP05_tipoEvento_TodosLosValoresExisten() {
        HistorialEvento.TipoEvento[] valores = HistorialEvento.TipoEvento.values();
        assertEquals(9, valores.length);
    }
}
