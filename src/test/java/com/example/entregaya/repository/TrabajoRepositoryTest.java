package com.example.entregaya.repository;

import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.model.ColaboradorTrabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("TrabajoRepository - Tests con H2")
class TrabajoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrabajoRepository trabajoRepository;


    private User usuario;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuario = new User();
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setEmail("test@example.com");
        entityManager.persistAndFlush(usuario);
    }

    // ========== CP01: findByColaboradoresUsername (Query personalizado) ==========
    @Test
    @DisplayName("CP01: Obtener trabajos donde el usuario es colaborador")
    void CP01_findByColaboradoresUsername_ConTrabajos_RetornaLista() {
        // Arrange
        Trabajo trabajo1 = new Trabajo();
        trabajo1.setNombreTrabajo("Trabajo 1");
        trabajo1.setDescripcion("Descripción 1");
        trabajo1.setFechaInicio(LocalDateTime.now());
        trabajo1.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo1.agregarColaborador(usuario, ColaboradorTrabajo.Rol.LIDER);
        entityManager.persistAndFlush(trabajo1);

        Trabajo trabajo2 = new Trabajo();
        trabajo2.setNombreTrabajo("Trabajo 2");
        trabajo2.setDescripcion("Descripción 2");
        trabajo2.setFechaInicio(LocalDateTime.now());
        trabajo2.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo2.agregarColaborador(usuario, ColaboradorTrabajo.Rol.EDITOR);
        entityManager.persistAndFlush(trabajo2);

        // Act
        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("testuser");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(t -> t.getNombreTrabajo().equals("Trabajo 1")));
        assertTrue(resultado.stream().anyMatch(t -> t.getNombreTrabajo().equals("Trabajo 2")));
    }

    @Test
    @DisplayName("CP02: findByColaboradoresUsername sin trabajos retorna vacío")
    void CP02_findByColaboradoresUsername_SinTrabajos_RetornaListaVacia() {
        // Act
        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("usuariosintrabajos");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP03: findByColaboradoresUsername con usuario en varios roles")
    void CP03_findByColaboradoresUsername_ConVariosRoles_RetornaTodasLasOcurrencias() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Múltiple");
        trabajo.setDescripcion("Proyecto con múltiples roles");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo.agregarColaborador(usuario, ColaboradorTrabajo.Rol.LIDER);
        entityManager.persistAndFlush(trabajo);

        // Act
        List<Trabajo> resultado = trabajoRepository.findByColaboradoresUsername("testuser");

        // Assert
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getColaboradores().stream()
                .anyMatch(c -> c.getUser().getUsername().equals("testuser")));
    }

    // ========== CP04: existsByNombreTrabajo ==========
    @Test
    @DisplayName("CP04: Verificar si existe trabajo por nombre")
    void CP04_existsByNombreTrabajo_ConNombreExistente_RetornaTrue() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Existente");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        entityManager.persistAndFlush(trabajo);

        // Act
        boolean existe = trabajoRepository.existsByNombreTrabajo("Proyecto Existente");

        // Assert
        assertTrue(existe);
    }

    @Test
    @DisplayName("CP05: existsByNombreTrabajo con nombre no existente retorna false")
    void CP05_existsByNombreTrabajo_ConNombreInexistente_RetornaFalse() {
        // Act
        boolean existe = trabajoRepository.existsByNombreTrabajo("Proyecto Fantasma");

        // Assert
        assertFalse(existe);
    }

    @Test
    @DisplayName("CP06: existsByNombreTrabajo es case-sensitive")
    void CP06_existsByNombreTrabajo_CaseSensitive() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("ProyectoTest");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        entityManager.persistAndFlush(trabajo);

        // Act
        boolean existeExacto = trabajoRepository.existsByNombreTrabajo("ProyectoTest");
        boolean existeMinusculas = trabajoRepository.existsByNombreTrabajo("proyectotest");

        // Assert
        assertTrue(existeExacto);
        assertFalse(existeMinusculas);
    }

    @Test
    @DisplayName("CP07: Obtener trabajo por ID")
    void CP07_findById_ConIdExistente_RetornaTrabajo() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Buscar");
        trabajo.setDescripcion("Descripción");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = entityManager.persistAndFlush(trabajo);

        // Act
        var resultado = trabajoRepository.findById(guardado.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Proyecto Buscar", resultado.get().getNombreTrabajo());
    }

    @Test
    @DisplayName("CP08: findById con ID no existente retorna Optional vacío")
    void CP08_findById_ConIdInexistente_RetornaOptionalVacio() {
        // Act
        var resultado = trabajoRepository.findById(9999L);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
