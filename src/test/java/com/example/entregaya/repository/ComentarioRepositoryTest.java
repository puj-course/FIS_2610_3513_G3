package com.example.entregaya.repository;

import com.example.entregaya.model.Comentario;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("ComentarioRepository - Tests con H2 y JUnit 5")
class ComentarioRepositoryTest {

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private TrabajoRepository trabajoRepository;
    @Autowired
    private UserRepository userRepository;

    private Tarea tarea;
    private User autor;

    @BeforeEach
    void setUp() {
        comentarioRepository.deleteAll();
        tareaRepository.deleteAll();
        trabajoRepository.deleteAll();
        userRepository.deleteAll();

        autor = new User();
        autor.setUsername("repo-coment-user");
        autor.setPassword("password123");
        autor.setEmail("repo-coment-user@example.com");
        autor = userRepository.save(autor);

        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Comentarios");
        trabajo.setDescripcion("Proyecto para tests de comentarios");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoRepository.save(trabajo);

        tarea = new Tarea();
        tarea.setNombre("Tarea con comentarios");
        tarea.setFechaInicio(LocalDateTime.now());
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        tarea.setTrabajo(trabajo);
        tarea = tareaRepository.save(tarea);
    }

    private Comentario crear(String contenido, LocalDateTime fecha) {
        Comentario c = new Comentario();
        c.setContenido(contenido);
        c.setFechaCreacion(fecha);
        c.setTarea(tarea);
        c.setAutor(autor);
        return comentarioRepository.save(c);
    }

    @Test
    @DisplayName("CP01: findByTareaIdOrderByFechaCreacionDesc retorna comentarios ordenados desc")
    void CP01_findByTareaId_OrdenaPorFechaDesc() {
        crear("antiguo", LocalDateTime.now().minusDays(3));
        crear("reciente", LocalDateTime.now());

        List<Comentario> resultado =
                comentarioRepository.findByTareaIdOrderByFechaCreacionDesc(tarea.getId());

        assertEquals(2, resultado.size());
        assertEquals("reciente", resultado.get(0).getContenido());
        assertEquals("antiguo", resultado.get(1).getContenido());
    }

    @Test
    @DisplayName("CP02: findByAutorId retorna todos los comentarios del autor")
    void CP02_findByAutorId_RetornaComentariosDelAutor() {
        crear("c1", LocalDateTime.now());
        crear("c2", LocalDateTime.now());

        List<Comentario> resultado = comentarioRepository.findByAutorId(autor.getId());

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getAutor().getId().equals(autor.getId())));
    }

    @Test
    @DisplayName("CP03: findByAutorId con autor sin comentarios retorna lista vacía")
    void CP03_findByAutorId_SinComentarios_RetornaVacio() {
        List<Comentario> resultado = comentarioRepository.findByAutorId(999_999L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
