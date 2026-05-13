package com.example.entregaya.service;

import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.strategy.Sololiderstrategy;
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
@DisplayName("CustomTrabajoDetailsService - Tests con @SpringBootTest")
class CustomTrabajoDetailsServiceTest {

    @Autowired
    private CustomTrabajoDetailsService customTrabajoDetailsService;

    @Autowired
    private TrabajoRepository trabajoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sololiderstrategy sololiderstrategy;

    private User usuario1;
    private User usuario2;

    @BeforeEach
    void setUp() {
        // Crear usuarios
        usuario1 = new User();
        usuario1.setUsername("lider");
        usuario1.setPassword("password123");
        usuario1.setEmail("lider@example.com");
        userRepository.save(usuario1);

        usuario2 = new User();
        usuario2.setUsername("colaborador");
        usuario2.setPassword("password123");
        usuario2.setEmail("colaborador@example.com");
        userRepository.save(usuario2);
    }

    // ========== CP01: Crear trabajo y asignar creador como LIDER ==========
    @Test
    @DisplayName("CP01: Crear trabajo asigna creador como LIDER")
    void CP01_crearTrabajo_AsignaCreadorComoLider() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Nuevo");
        trabajo.setDescripcion("Descripción del proyecto");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));

        // Act
        Trabajo resultado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("Proyecto Nuevo", resultado.getNombreTrabajo());
        assertEquals(1, resultado.getColaboradores().size());
        assertTrue(resultado.getColaboradores().stream()
                .anyMatch(c -> c.getUser().getUsername().equals("lider") && c.getRol() == ColaboradorTrabajo.Rol.LIDER)
        );
    }

    // ========== CP02: Obtener trabajo por ID ==========
    @Test
    @DisplayName("CP02: Obtener trabajo existente por ID")
    void CP02_obtenerPorId_ConIdExistente_RetornaTrabajo() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Buscar");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Act
        Trabajo resultado = customTrabajoDetailsService.obtenerPorId(guardado.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals("Proyecto Buscar", resultado.getNombreTrabajo());
    }

    @Test
    @DisplayName("CP02b: Obtener trabajo inexistente lanza excepción")
    void CP02b_obtenerPorId_ConIdInexistente_LanzaExcepcion() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customTrabajoDetailsService.obtenerPorId(9999L);
        });
    }

    // ========== CP03: Listar trabajos del usuario ==========
    @Test
    @DisplayName("CP03: Listar trabajos donde usuario es colaborador")
    void CP03_listarPorUsuario_RetornaTrabajosDondeEsColaborador() {
        // Arrange
        Trabajo trabajo1 = new Trabajo();
        trabajo1.setNombreTrabajo("Trabajo 1");
        trabajo1.setFechaInicio(LocalDateTime.now());
        trabajo1.setFechaEntrega(LocalDateTime.now().plusDays(30));
        customTrabajoDetailsService.crearTrabajo(trabajo1, "lider");

        Trabajo trabajo2 = new Trabajo();
        trabajo2.setNombreTrabajo("Trabajo 2");
        trabajo2.setFechaInicio(LocalDateTime.now());
        trabajo2.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado2 = customTrabajoDetailsService.crearTrabajo(trabajo2, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado2.getId(), "colaborador");

        // Act
        List<Trabajo> trabajosDeLider = customTrabajoDetailsService.listarPorUsuario("lider");
        List<Trabajo> trabajosDelColaborador = customTrabajoDetailsService.listarPorUsuario("colaborador");

        // Assert
        assertEquals(2, trabajosDeLider.size());
        assertEquals(1, trabajosDelColaborador.size());
    }

    // ========== CP04: Agregar colaborador a trabajo ==========
    @Test
    @DisplayName("CP04: Agregar nuevo colaborador al trabajo")
    void CP04_agregarColaborador_AgreganNuevoUsuario() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Colaborativo");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        assertEquals(1, guardado.getColaboradores().size());

        // Act
        Trabajo resultado = customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");

        // Assert
        assertEquals(2, resultado.getColaboradores().size());
        assertTrue(resultado.getColaboradores().stream()
                .anyMatch(c -> c.getUser().getUsername().equals("colaborador"))
        );
    }

    @Test
    @DisplayName("CP04b: No duplicar colaborador si ya existe")
    void CP04b_agregarColaborador_NoDuplicaSiYaExiste() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Único");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Act
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "lider");
        Trabajo resultado = customTrabajoDetailsService.obtenerPorId(guardado.getId());

        // Assert
        assertEquals(1, resultado.getColaboradores().size());
    }

    // ========== CP05: Cambiar rol de colaborador ==========
    @Test
    @DisplayName("CP05: Cambiar rol de colaborador de COLABORADOR a EDITOR")
    void CP05_cambiarRol_ActualizaRolDelColaborador() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Roles");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");

        // Act
        customTrabajoDetailsService.cambiarRol(guardado.getId(), usuario2.getId(), ColaboradorTrabajo.Rol.EDITOR);
        Trabajo actualizado = customTrabajoDetailsService.obtenerPorId(guardado.getId());

        // Assert
        assertTrue(actualizado.getColaboradores().stream()
                .filter(c -> c.getUser().getId().equals(usuario2.getId()))
                .anyMatch(c -> c.getRol() == ColaboradorTrabajo.Rol.EDITOR)
        );
    }

    @Test
    @DisplayName("CP05b: Cambiar rol a null lanza excepción")
    void CP05b_cambiarRol_ConRolNulo_LanzaExcepcion() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Rol Nulo");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customTrabajoDetailsService.cambiarRol(guardado.getId(), usuario2.getId(), null);
        });
    }

    // ========== CP06: Actualizar trabajo (nombre, descripción, fechas) ==========
    @Test
    @DisplayName("CP06: Actualizar nombre y fechas de trabajo")
    void CP06_actualizarTrabajo_ModificaNombreDescripcionYFechas() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Nombre Original");
        trabajo.setDescripcion("Descripción Original");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(60);
        Trabajo trabajoEditado = new Trabajo();
        trabajoEditado.setNombreTrabajo("Nombre Actualizado");
        trabajoEditado.setDescripcion("Descripción Nueva");
        trabajoEditado.setFechaInicio(LocalDateTime.now());
        trabajoEditado.setFechaEntrega(nuevaFecha);

        // Act
        customTrabajoDetailsService.actualizarTrabajo(guardado.getId(), trabajoEditado);
        Trabajo resultado = customTrabajoDetailsService.obtenerPorId(guardado.getId());

        // Assert
        assertEquals("Nombre Actualizado", resultado.getNombreTrabajo());
        assertEquals("Descripción Nueva", resultado.getDescripcion());
        assertEquals(nuevaFecha, resultado.getFechaEntrega());
    }

    @Test
    @DisplayName("CP06b: No permitir nombre duplicado al actualizar")
    void CP06b_actualizarTrabajo_RechazaNombreDuplicado() {
        // Arrange
        Trabajo trabajo1 = new Trabajo();
        trabajo1.setNombreTrabajo("Trabajo Uno");
        trabajo1.setFechaInicio(LocalDateTime.now());
        trabajo1.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado1 = customTrabajoDetailsService.crearTrabajo(trabajo1, "lider");

        Trabajo trabajo2 = new Trabajo();
        trabajo2.setNombreTrabajo("Trabajo Dos");
        trabajo2.setFechaInicio(LocalDateTime.now());
        trabajo2.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado2 = customTrabajoDetailsService.crearTrabajo(trabajo2, "colaborador");

        Trabajo trabajoEditado = new Trabajo();
        trabajoEditado.setNombreTrabajo("Trabajo Uno"); // Intenta usar nombre del otro

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customTrabajoDetailsService.actualizarTrabajo(guardado2.getId(), trabajoEditado);
        });
    }

    // ========== CP07: Clonar trabajo ==========
    @Test
    @DisplayName("CP07: Clonar trabajo solo LIDER puede hacerlo")
    void CP07_clonarTrabajo_SoloLiderPuedeClonar() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Original");
        trabajo.setDescripcion("Descripción original");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Act
        Trabajo clon = customTrabajoDetailsService.clonarTrabajo(guardado.getId(), "lider");

        // Assert
        assertNotNull(clon.getId());
        assertNotEquals(guardado.getId(), clon.getId());
        assertTrue(clon.getNombreTrabajo().contains("copia"));
        assertEquals(1, clon.getColaboradores().size());
        assertTrue(clon.getColaboradores().stream()
                .anyMatch(c -> c.getRol() == ColaboradorTrabajo.Rol.LIDER && c.getUser().getUsername().equals("lider"))
        );
    }

    @Test
    @DisplayName("CP07b: Clonar trabajo no-LIDER lanza excepción")
    void CP07b_clonarTrabajo_NoLiderLanzaExcepcion() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Protegido");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            customTrabajoDetailsService.clonarTrabajo(guardado.getId(), "colaborador");
        });
    }

    // ========== CP08: Verificar permiso ==========
    @Test
    @DisplayName("CP08: Verificar permiso con estrategia SoloLider")
    void CP08_verificarPermiso_ConEstrategiaSoloLider() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Permisos");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");

        // Act
        boolean liderTienePermiso = customTrabajoDetailsService.verificarPermiso(
                guardado.getId(), "lider", sololiderstrategy
        );
        boolean colaboradorTienePermiso = customTrabajoDetailsService.verificarPermiso(
                guardado.getId(), "colaborador", sololiderstrategy
        );

        // Assert
        assertTrue(liderTienePermiso);
        assertFalse(colaboradorTienePermiso);
    }

    // ========== CP09: Eliminar colaborador ==========
    @Test
    @DisplayName("CP09: Eliminar colaborador del trabajo")
    void CP09_eliminarColaborador_EliminaAlUsuarioDelTrabajo() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Eliminación");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");
        customTrabajoDetailsService.agregarColaborador(guardado.getId(), "colaborador");
        assertEquals(2, guardado.getColaboradores().size());

        // Act
        customTrabajoDetailsService.eliminarColaborador(guardado.getId(), usuario2.getId(), "lider");
        Trabajo actualizado = customTrabajoDetailsService.obtenerPorId(guardado.getId());

        // Assert
        assertEquals(1, actualizado.getColaboradores().size());
        assertFalse(actualizado.getColaboradores().stream()
                .anyMatch(c -> c.getUser().getId().equals(usuario2.getId()))
        );
    }

    @Test
    @DisplayName("CP09b: No permitir eliminar único LIDER")
    void CP09b_eliminarColaborador_NoEliminarUnicoLider() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Lider Único");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customTrabajoDetailsService.eliminarColaborador(guardado.getId(), usuario1.getId(), "lider");
        });
    }

    @Test
    @DisplayName("CP09c: El usuario no puede eliminarse a sí mismo")
    void CP09c_eliminarColaborador_NoEliminarASiMismo() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Autoeliminación");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = customTrabajoDetailsService.crearTrabajo(trabajo, "lider");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customTrabajoDetailsService.eliminarColaborador(guardado.getId(), usuario1.getId(), "lider");
        });
    }
}
