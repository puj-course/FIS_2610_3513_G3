package com.example.entregaya.service;

import com.example.entregaya.model.*;
import com.example.entregaya.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomComentarioDetailsService - Tests de integración")
class CustomComentarioDetailsServiceTest {

    @Autowired
    private CustomComentarioDetailsService comentarioService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private CustomTareaDetailsService tareaService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TareaRepository tareaRepository;

    private User usuario;
    private Tarea tarea;
    private Trabajo trabajo;

    @BeforeEach
    void setUp() {
        userDetailsService.register("comentarista", "pass123456", "comentarista@test.com");
        usuario = userRepository.findByUsername("comentarista").orElseThrow();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Trabajo Comentarios " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoService.crearTrabajo(trabajo, "comentarista");

        Tarea t = new Tarea();
        t.setNombre("Tarea para comentar");
        t.setFechaInicio(LocalDateTime.now());
        t.setFechaFinal(LocalDateTime.now().plusDays(5));
        t.setDificultad(Tarea.Dificultad.MEDIA);
        tarea = tareaService.crearTarea(t, trabajo.getId());
    }

    // CP01 - NORMAL: Crear comentario con datos válidos
    @Test
    @DisplayName("CP01: crearComentario con datos válidos persiste el comentario")
    void CP01_crearComentario_ConDatosValidos_PersistComentario() {
        Comentario resultado = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Buen trabajo");

        assertNotNull(resultado.getId());
        assertEquals("Buen trabajo", resultado.getContenido());
        assertEquals(usuario.getId(), resultado.getAutor().getId());
        assertEquals(tarea.getId(), resultado.getTarea().getId());
    }

    // CP02 - NORMAL: Obtener comentarios por tarea
    @Test
    @DisplayName("CP02: obtenerComentariosPorTarea retorna los comentarios ordenados")
    void CP02_obtenerComentariosPorTarea_RetornaComentariosOrdenados() {
        comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Primer comentario");
        comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Segundo comentario");

        List<Comentario> comentarios = comentarioService.obtenerComentariosPorTarea(tarea.getId());

        assertEquals(2, comentarios.size());
    }

    // CP03 - NORMAL: Editar comentario propio funciona
    @Test
    @DisplayName("CP03: editarComentario con autor correcto actualiza el contenido")
    void CP03_editarComentario_ConAutorCorrecto_ActualizaContenido() {
        Comentario original = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Texto original");

        Comentario editado = comentarioService.editarComentario(original.getId(), "Texto editado", "comentarista");

        assertEquals("Texto editado", editado.getContenido());
    }

    // CP04 - NEGATIVA: Editar comentario de otro usuario lanza excepción
    @Test
    @DisplayName("CP04: editarComentario con usuario incorrecto lanza IllegalArgumentException")
    void CP04_editarComentario_ConUsuarioIncorrecto_LanzaExcepcion() {
        Comentario comentario = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Mi comentario");

        assertThrows(IllegalArgumentException.class, () ->
            comentarioService.editarComentario(comentario.getId(), "Intentar editar", "otro_usuario")
        );
    }

    // CP05 - NORMAL: Eliminar comentario propio funciona
    @Test
    @DisplayName("CP05: eliminarComentario con autor correcto elimina el comentario")
    void CP05_eliminarComentario_ConAutorCorrecto_EliminaComentario() {
        Comentario comentario = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "A eliminar");

        comentarioService.eliminarComentario(comentario.getId(), "comentarista");

        assertThrows(RuntimeException.class, () ->
            comentarioService.obtenerComentarioPorId(comentario.getId())
        );
    }

    // CP06 - NEGATIVA: Eliminar comentario de otro usuario lanza excepción
    @Test
    @DisplayName("CP06: eliminarComentario con usuario incorrecto lanza IllegalArgumentException")
    void CP06_eliminarComentario_ConUsuarioIncorrecto_LanzaExcepcion() {
        Comentario comentario = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Protegido");

        assertThrows(IllegalArgumentException.class, () ->
            comentarioService.eliminarComentario(comentario.getId(), "intruso")
        );
    }

    // CP07 - NORMAL: obtenerComentarioPorId retorna el comentario existente
    @Test
    @DisplayName("CP07: obtenerComentarioPorId con id válido retorna el comentario")
    void CP07_obtenerComentarioPorId_ConIdValido_RetornaComentario() {
        Comentario creado = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Buscame");

        Comentario encontrado = comentarioService.obtenerComentarioPorId(creado.getId());

        assertNotNull(encontrado);
        assertEquals("Buscame", encontrado.getContenido());
    }

    // CP08 - NEGATIVA: obtenerComentarioPorId con id inexistente lanza excepción
    @Test
    @DisplayName("CP08: obtenerComentarioPorId con id inexistente lanza excepción")
    void CP08_obtenerComentarioPorId_ConIdInexistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            comentarioService.obtenerComentarioPorId(99999L)
        );
    }

    // CP09 - NORMAL: eliminarComentario sin verificar usuario (uso interno)
    @Test
    @DisplayName("CP09: eliminarComentario sin username elimina directamente")
    void CP09_eliminarComentario_SinUsername_EliminaDirectamente() {
        Comentario comentario = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Uso interno");

        comentarioService.eliminarComentario(comentario.getId());

        assertThrows(RuntimeException.class, () ->
            comentarioService.obtenerComentarioPorId(comentario.getId())
        );
    }

    // CP10 - NORMAL: editarComentario sin verificar usuario (uso interno)
    @Test
    @DisplayName("CP10: editarComentario sin username edita directamente")
    void CP10_editarComentario_SinUsername_EditaDirectamente() {
        Comentario comentario = comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Original interno");

        Comentario editado = comentarioService.editarComentario(comentario.getId(), "Editado interno");

        assertEquals("Editado interno", editado.getContenido());
    }
}
