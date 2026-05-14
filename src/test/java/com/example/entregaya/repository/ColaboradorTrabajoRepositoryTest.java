package com.example.entregaya.repository;

import com.example.entregaya.model.ColaboradorTrabajo;
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
@DisplayName("ColaboradorTrabajoRepository - Tests con H2 y JUnit 5")
class ColaboradorTrabajoRepositoryTest {

    @Autowired
    private ColaboradorTrabajoRepository colaboradorRepository;
    @Autowired
    private TrabajoRepository trabajoRepository;
    @Autowired
    private UserRepository userRepository;

    private Trabajo trabajo;
    private User lider;
    private User editor;

    @BeforeEach
    void setUp() {
        colaboradorRepository.deleteAll();
        trabajoRepository.deleteAll();
        userRepository.deleteAll();

        lider = new User();
        lider.setUsername("repo-colab-lider");
        lider.setPassword("password123");
        lider.setEmail("repo-colab-lider@example.com");
        lider = userRepository.save(lider);

        editor = new User();
        editor.setUsername("repo-colab-editor");
        editor.setPassword("password123");
        editor.setEmail("repo-colab-editor@example.com");
        editor = userRepository.save(editor);

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Colaboradores");
        trabajo.setDescripcion("Test de colaboradores");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        // Guardamos primero el trabajo VACÍO (persist), luego agregamos colaboradores
        // y volvemos a guardar (merge). Esto evita "detached entity" en los Users porque
        // ColaboradorTrabajo tiene @ManyToOne a User sin cascade y el segundo save usa merge.
        trabajo = trabajoRepository.save(trabajo);
        trabajo.agregarColaborador(lider, ColaboradorTrabajo.Rol.LIDER);
        trabajo.agregarColaborador(editor, ColaboradorTrabajo.Rol.EDITOR);
        trabajo = trabajoRepository.save(trabajo);
    }

    @Test
    @DisplayName("CP01: findMiembrosConRol retorna todos los miembros del trabajo")
    void CP01_findMiembrosConRol_RetornaTodosLosMiembros() {
        List<ColaboradorTrabajo> resultado =
                colaboradorRepository.findMiembrosConRol(trabajo.getId());

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(c -> c.getUser().getUsername().equals("repo-colab-lider")));
        assertTrue(resultado.stream().anyMatch(c -> c.getUser().getUsername().equals("repo-colab-editor")));
    }

    @Test
    @DisplayName("CP02: findMiembrosConRol ordena por rol ASC (alfabético: EDITOR antes que LIDER)")
    void CP02_findMiembrosConRol_RespetaOrden() {
        List<ColaboradorTrabajo> resultado =
                colaboradorRepository.findMiembrosConRol(trabajo.getId());

        // El enum Rol se guarda como STRING (@Enumerated(EnumType.STRING)), por lo que
        // ORDER BY ct.rol ASC es alfabético: COLABORADOR < EDITOR < LIDER.
        // Con solo EDITOR y LIDER, EDITOR va primero.
        assertEquals(ColaboradorTrabajo.Rol.EDITOR, resultado.get(0).getRol());
        assertEquals(ColaboradorTrabajo.Rol.LIDER, resultado.get(1).getRol());
    }

    @Test
    @DisplayName("CP03: findMiembrosConRol con trabajo inexistente retorna lista vacía")
    void CP03_findMiembrosConRol_TrabajoInexistente_RetornaVacio() {
        List<ColaboradorTrabajo> resultado =
                colaboradorRepository.findMiembrosConRol(999_999L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
