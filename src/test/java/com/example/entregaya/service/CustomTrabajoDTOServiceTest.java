package com.example.entregaya.service;

import com.example.entregaya.dto.TrabajoCrearDTO;
import com.example.entregaya.dto.TrabajoEditarDTO;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TrabajoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomTrabajoDetailsService - Tests métodos DTO")
class CustomTrabajoDTOServiceTest {

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TrabajoRepository trabajoRepository;

    private String username;

    @BeforeEach
    void setUp() {
        username = "user_dto_" + System.nanoTime();
        userDetailsService.register(username, "pass123456", username + "@test.com");
    }

    // ── crearTrabajoDesdeDTO ──

    // CP01 - NORMAL: crearTrabajoDesdeDTO crea trabajo con todos los campos.
    // Entrada: DTO con nombre, descripción y fechas válidos
    // Resultado esperado: trabajo persistido con los datos del DTO y usuario como LIDER
    @Test
    @DisplayName("CP01: crearTrabajoDesdeDTO crea trabajo con datos del DTO")
    void CP01_crearTrabajoDesdeDTO_ConDatosValidos_CreaTrabajoCorrectamente() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("Trabajo DTO " + System.nanoTime());
        dto.setDescripcion("Descripción desde DTO");
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaEntrega(LocalDateTime.now().plusDays(30));

        trabajoService.crearTrabajoDesdeDTO(dto, username);

        Trabajo guardado = trabajoRepository
                .findByColaboradoresUsername(username)
                .stream()
                .filter(t -> t.getNombreTrabajo().equals(dto.getNombreTrabajo()))
                .findFirst()
                .orElseThrow();

        assertEquals(dto.getNombreTrabajo(), guardado.getNombreTrabajo());
        assertEquals("Descripción desde DTO", guardado.getDescripcion());
        assertNotNull(guardado.getFechaInicio());
        assertNotNull(guardado.getFechaEntrega());
        assertEquals(1, guardado.getColaboradores().size());
    }

    // CP02 - NORMAL: crearTrabajoDesdeDTO sin fechas crea trabajo correctamente.
    // Entrada: DTO solo con nombre
    // Resultado esperado: trabajo persistido, fechas nulas
    @Test
    @DisplayName("CP02: crearTrabajoDesdeDTO sin fechas crea trabajo correctamente")
    void CP02_crearTrabajoDesdeDTO_SinFechas_CreaTrabajoCorrectamente() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("Trabajo Sin Fechas " + System.nanoTime());

        trabajoService.crearTrabajoDesdeDTO(dto, username);

        Trabajo guardado = trabajoRepository
                .findByColaboradoresUsername(username)
                .stream()
                .filter(t -> t.getNombreTrabajo().equals(dto.getNombreTrabajo()))
                .findFirst()
                .orElseThrow();

        assertNull(guardado.getFechaInicio());
        assertNull(guardado.getFechaEntrega());
    }

    // CP03 - NEGATIVA: crearTrabajoDesdeDTO con usuario inexistente lanza excepción.
    // Entrada: username que no existe en BD
    // Resultado esperado: RuntimeException
    @Test
    @DisplayName("CP03: crearTrabajoDesdeDTO con usuario inexistente lanza excepción")
    void CP03_crearTrabajoDesdeDTO_UsuarioInexistente_LanzaExcepcion() {
        TrabajoCrearDTO dto = new TrabajoCrearDTO();
        dto.setNombreTrabajo("Trabajo Inexistente " + System.nanoTime());

        assertThrows(RuntimeException.class, () ->
                trabajoService.crearTrabajoDesdeDTO(dto, "usuario_que_no_existe_xyz")
        );
    }

    // ── actualizarTrabajoDesdeDTO ──

    // CP04 - NORMAL: actualizarTrabajoDesdeDTO actualiza todos los campos.
    // Entrada: trabajo existente + DTO con nuevos valores
    // Resultado esperado: trabajo actualizado en BD
    @Test
    @DisplayName("CP04: actualizarTrabajoDesdeDTO actualiza todos los campos correctamente")
    void CP04_actualizarTrabajoDesdeDTO_ConDatosValidos_ActualizaCorrectamente() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Original " + System.nanoTime());
        trabajo.setDescripcion("Descripción original");
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, username);

        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo("Actualizado " + System.nanoTime());
        dto.setDescripcion("Nueva descripción");
        dto.setFechaInicio(LocalDateTime.of(2026, 1, 1, 8, 0));
        dto.setFechaEntrega(LocalDateTime.of(2026, 12, 31, 18, 0));

        trabajoService.actualizarTrabajoDesdeDTO(guardado.getId(), dto);

        Trabajo actualizado = trabajoService.obtenerPorId(guardado.getId());
        assertEquals(dto.getNombreTrabajo(), actualizado.getNombreTrabajo());
        assertEquals("Nueva descripción", actualizado.getDescripcion());
        assertNotNull(actualizado.getFechaInicio());
        assertNotNull(actualizado.getFechaEntrega());
    }

    // CP05 - NORMAL: actualizarTrabajoDesdeDTO con mismo nombre no lanza excepción.
    // Entrada: DTO con el mismo nombre que ya tiene el trabajo
    // Resultado esperado: actualización exitosa sin error de duplicado
    @Test
    @DisplayName("CP05: actualizarTrabajoDesdeDTO con mismo nombre no lanza excepción")
    void CP05_actualizarTrabajoDesdeDTO_MismoNombre_NoLanzaExcepcion() {
        String nombre = "Mismo Nombre " + System.nanoTime();
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo(nombre);
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, username);

        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo(nombre); // mismo nombre
        dto.setDescripcion("Descripción actualizada");

        assertDoesNotThrow(() ->
                trabajoService.actualizarTrabajoDesdeDTO(guardado.getId(), dto)
        );

        Trabajo actualizado = trabajoService.obtenerPorId(guardado.getId());
        assertEquals("Descripción actualizada", actualizado.getDescripcion());
    }

    // CP06 - NEGATIVA: actualizarTrabajoDesdeDTO con nombre duplicado lanza excepción.
    // Entrada: nombre que ya existe en otro trabajo
    // Resultado esperado: IllegalArgumentException
    @Test
    @DisplayName("CP06: actualizarTrabajoDesdeDTO con nombre duplicado lanza excepción")
    void CP06_actualizarTrabajoDesdeDTO_NombreDuplicado_LanzaExcepcion() {
        String nombreExistente = "Nombre Existente " + System.nanoTime();
        Trabajo otroTrabajo = new Trabajo();
        otroTrabajo.setNombreTrabajo(nombreExistente);
        trabajoService.crearTrabajo(otroTrabajo, username);

        String nombreOriginal = "Trabajo Original " + System.nanoTime();
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo(nombreOriginal);
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, username);

        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo(nombreExistente); // nombre de otro trabajo

        assertThrows(IllegalArgumentException.class, () ->
                trabajoService.actualizarTrabajoDesdeDTO(guardado.getId(), dto)
        );
    }

    // CP07 - NEGATIVA: actualizarTrabajoDesdeDTO con ID inexistente lanza excepción.
    // Entrada: ID que no existe en BD
    // Resultado esperado: IllegalArgumentException
    @Test
    @DisplayName("CP07: actualizarTrabajoDesdeDTO con ID inexistente lanza excepción")
    void CP07_actualizarTrabajoDesdeDTO_IdInexistente_LanzaExcepcion() {
        TrabajoEditarDTO dto = new TrabajoEditarDTO();
        dto.setNombreTrabajo("No Importa");

        assertThrows(IllegalArgumentException.class, () ->
                trabajoService.actualizarTrabajoDesdeDTO(99999L, dto)
        );
    }
}
