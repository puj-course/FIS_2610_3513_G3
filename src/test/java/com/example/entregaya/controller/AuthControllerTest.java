package com.example.entregaya.controller;

import com.example.entregaya.dto.DashboardDTO;
import com.example.entregaya.facade.DashboardFacade;
import com.example.entregaya.service.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

/**
 * HU-49 - #493
 * Pruebas unitarias JUnit 5 para AuthController.
 * Se instancia directamente el controlador con sus dependencias
 * inyectadas mediante mocks. Se verifican las vistas retornadas
 * (login, register, dashboard, estadisticas) y las redirecciones
 * del flujo de registro.
 */
class AuthControllerTest {

    private AuthController authController;
    private CustomUserDetailsService userDetailsService;
    private DashboardFacade dashboardFacade;

    @BeforeEach
    void setUp() {
        // Instanciar mocks de dependencias
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        dashboardFacade    = Mockito.mock(DashboardFacade.class);

        // Instanciar controller con inyeccion directa
        authController = new AuthController(userDetailsService, dashboardFacade);
    }

    // CP01 – login(): debe retornar la vista "login"
    // CP01 - NORMAL: GET /login retorna la vista de login.
    // Entrada: ninguna
    // Resultado esperado: String "login"
    @Test
    void CP01_login_RetornaVistaLogin() {
        // Act
        String vista = authController.login();

        // Assert
        Assertions.assertEquals("login", vista,
                "El método login() debe retornar la vista 'login'");
    }

    // CP02 – registerForm(): debe retornar la vista "register"
    // CP02 - NORMAL: GET /register retorna la vista de registro.
    // Entrada: ninguna
    // Resultado esperado: String "register"
    @Test
    void CP02_registerForm_RetornaVistaRegister() {
        // Act
        String vista = authController.registerForm();

        // Assert
        Assertions.assertEquals("register", vista,
                "El método registerForm() debe retornar la vista 'register'");
    }

    // CP03 – register() exitoso: redirige a /login
    // CP03 - NORMAL: POST /register con datos válidos redirige a login.
    // Entrada: username="user1", password="pass123", email="user@test.com"
    // Resultado esperado: "redirect:/login"
    @Test
    void CP03_register_ConDatosValidos_RedireccionaALogin() {
        // Arrange
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Mockito.when(userDetailsService.register("user1", "pass123", "user@test.com"))
                .thenReturn(null);

        // Act
        String resultado = authController.register(
                "user1", "pass123", "user@test.com", redirectAttributes);

        // Assert
        Assertions.assertEquals("redirect:/login", resultado,
                "Un registro exitoso debe redirigir a '/login'");
    }

    // CP04 – register() fallido: redirige a /register con error
    // CP04 - NEGATIVA: POST /register con usuario duplicado redirige a /register.
    // Entrada: username ya existente
    // Resultado esperado: "redirect:/register" y atributo "error" en flash
    @Test
    void CP04_register_ConUsuarioDuplicado_RedireccionaARegister() {
        // Arrange
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Mockito.when(userDetailsService.register("duplicado", "pass123", "x@x.com"))
                .thenThrow(new IllegalArgumentException("El usuario ya existe"));

        // Act
        String resultado = authController.register(
                "duplicado", "pass123", "x@x.com", redirectAttributes);

        // Assert
        Assertions.assertEquals("redirect:/register", resultado,
                "Un registro fallido debe redirigir a '/register'");
        Assertions.assertNotNull(redirectAttributes.getFlashAttributes().get("error"),
                "Debe existir el atributo flash 'error'");
    }

    // CP05 – dashboard(): retorna la vista "dashboard" y agrega atributo "data"
    // CP05 - NORMAL: GET /dashboard retorna vista "dashboard" con atributo "data".
    // Entrada: usuario autenticado
    // Resultado esperado: vista "dashboard", model contiene "data"
    @Test
    void CP05_dashboard_RetornaVistaDashboard_ConAtributoData() {
        // Arrange
        Model model = new ExtendedModelMap();
        UserDetails userDetails = User.withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();
        DashboardDTO dto = new DashboardDTO();
        Mockito.when(dashboardFacade.getDashboardData("testuser")).thenReturn(dto);

        // Act
        String vista = authController.dashboard(model, userDetails);

        // Assert
        Assertions.assertEquals("dashboard", vista,
                "El método dashboard() debe retornar la vista 'dashboard'");
        Assertions.assertNotNull(model.asMap().get("data"),
                "El modelo debe contener el atributo 'data'");
    }

    // CP06 – estadisticas(): retorna la vista "estadisticas" con atributo "stats"
    // CP06 - NORMAL: GET /estadisticas retorna vista "estadisticas" con atributo "stats".
    // Entrada: usuario autenticado
    // Resultado esperado: vista "estadisticas", model contiene "stats"
    @Test
    void CP06_estadisticas_RetornaVistaEstadisticas_ConAtributoStats() {
        // Arrange
        Model model = new ExtendedModelMap();
        UserDetails userDetails = User.withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();
        Mockito.when(dashboardFacade.getEstadisticasPersonales("testuser"))
                .thenReturn(new java.util.HashMap<>());

        // Act
        String vista = authController.estadisticas(model, userDetails);

        // Assert
        Assertions.assertEquals("estadisticas", vista,
                "El método estadisticas() debe retornar la vista 'estadisticas'");
        Assertions.assertNotNull(model.asMap().get("stats"),
                "El modelo debe contener el atributo 'stats'");
    }

    // CP07 – login() retorna valor no nulo
    // CP07 - BORDE: El valor retornado por login() no debe ser nulo.
    // Entrada: ninguna
    // Resultado esperado: assertNotNull sobre el valor de retorno
    @Test
    void CP07_login_RetornoNoEsNulo() {
        // Act
        String vista = authController.login();

        // Assert
        Assertions.assertNotNull(vista,
                "La vista retornada por login() no debe ser nula");
    }

    // CP08 – register() exitoso agrega atributo flash "success"
    // CP08 - NORMAL: POST /register exitoso debe agregar flash attribute "success".
    // Entrada: datos válidos de registro
    // Resultado esperado: redirectAttributes contiene "success"
    @Test
    void CP08_register_Exitoso_AgregaFlashSuccess() {
        // Arrange
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Mockito.when(userDetailsService.register("nuevo", "pass123", "nuevo@test.com"))
                .thenReturn(null);

        // Act
        authController.register("nuevo", "pass123", "nuevo@test.com", redirectAttributes);

        // Assert
        Assertions.assertNotNull(redirectAttributes.getFlashAttributes().get("success"),
                "El registro exitoso debe agregar el atributo flash 'success'");
    }
}
