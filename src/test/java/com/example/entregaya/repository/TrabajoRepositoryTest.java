package com.example.entregaya.repository;

import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.model.ColaboradorTrabajo;
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
@DisplayName("TrabajoRepository - Tests con H2 y JUnit 5")
class TrabajoRepositoryTest {

    @Autowired
    private TrabajoRepository trabajoRepository;

    @Autowired
    private UserRepository userRepository;

    private User usuario;

    @BeforeEach
    void setUp() {
        trabajoRepository.deleteAll();
        userRepository.deleteAll();

        usuario = new User();
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setEmail("test@example.com");
        userRepository.save(usuario);
    }

    @Test
    @DisplayName("CP01: Obtener trabajos donde el usuario es colaborador")
    void CP01_findByColaboradoresUsername_ConTrabajos_RetornaLista() {
        Trabajo trabajo1 = new Trabajo();
        trabajo1.setNombreTrabajo("Trabajo 1");
        trabajo1.setDescripcion("Descripción 1");
        trabajo1.setFechaInicio(LocalDateTime.now());
        trabajo1.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo1 = trabajoRepository.save(trabajo1);
        trabajo1.agregarColaborador(usuario, ColaboradorTrabajo.Rol.LIDER);
        trabajoRepository.save(trabajo1);

        Trabajo trabajo2 = new Trabajo();
        trabajo2.setNombreTrabajo("Trabajo 2");
        trabajo2.setDescripcion("Descripción 2");
        trabajo2.setFechaInicio(LocalDateTime.now());
        trabajo2.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo2 = trabajoRepository.save(trabajo2);
        trabajo2.agregarColaborador(usuario, ColaboradorTrabajo.Rol.EDITOR);
        trabajoRepository.save(trabajo2);

        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("testuser");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(t -> t.getNombreTrabajo().equals("Trabajo 1")));
        assertTrue(resultado.stream().anyMatch(t -> t.getNombreTrabajo().equals("Trabajo 2")));
    }

    @Test
    @DisplayName("CP02: findByColaboradoresUsername sin trabajos retorna vacío")
    void CP02_findByColaboradoresUsername_SinTrabajos_RetornaListaVacia() {
        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("usuariosintrabajos");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("CP03: findByColaboradoresUsername con usuario en varios roles")
    void CP03_findByColaboradoresUsername_ConVariosRoles_RetornaTodasLasOcurrencias() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Múltiple");
        trabajo.setDescripcion("Proyecto con múltiples roles");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoRepository.save(trabajo);
        trabajo.agregarColaborador(usuario, ColaboradorTrabajo.Rol.LIDER);
        trabajoRepository.save(trabajo);

        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("testuser");

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getColaboradores().stream()
                .anyMatch(c -> c.getUser().getUsername().equals("testuser")));
    }

    @Test
    @DisplayName("CP04: Verificar si existe trabajo por nombre")
    void CP04_existsByNombreTrabajo_ConNombreExistente_RetornaTrue() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Existente");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajoRepository.save(trabajo);

        boolean existe = trabajoRepository.existsByNombreTrabajo("Proyecto Existente");

        assertTrue(existe);
    }

    @Test
    @DisplayName("CP05: existsByNombreTrabajo con nombre no existente retorna false")
    void CP05_existsByNombreTrabajo_ConNombreInexistente_RetornaFalse() {
        boolean existe = trabajoRepository.existsByNombreTrabajo("Proyecto Fantasma");

        assertFalse(existe);
    }

    @Test
    @DisplayName("CP06: existsByNombreTrabajo es case-sensitive")
    void CP06_existsByNombreTrabajo_CaseSensitive() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("ProyectoTest");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajoRepository.save(trabajo);

        boolean existeExacto = trabajoRepository.existsByNombreTrabajo("ProyectoTest");
        boolean existeMinusculas = trabajoRepository.existsByNombreTrabajo("proyectotest");

        assertTrue(existeExacto);
        assertFalse(existeMinusculas);
    }

    @Test
    @DisplayName("CP07: Obtener trabajo por ID")
    void CP07_findById_ConIdExistente_RetornaTrabajo() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Buscar");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoRepository.save(trabajo);

        var resultado = trabajoRepository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Proyecto Buscar", resultado.get().getNombreTrabajo());
    }

    @Test
    @DisplayName("CP08: findById con ID no existente retorna Optional vacío")
    void CP08_findById_ConIdInexistente_RetornaOptionalVacio() {
        var resultado = trabajoRepository.findById(9999L);

        assertTrue(resultado.isEmpty());
    }
}