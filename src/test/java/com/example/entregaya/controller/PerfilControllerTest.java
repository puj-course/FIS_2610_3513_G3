package com.example.entregaya.controller;

import com.example.entregaya.model.User;
import com.example.entregaya.service.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

/**
 * HU-49 – Pruebas unitarias JUnit 5 para PerfilController.
 * El controlador se instancia directamente con su dependencia mockeada.
 * Se verifican vistas retornadas y atributos flash para los flujos:
 * ver perfil, actualizar username, actualizar password y actualizar email.
 */
class PerfilControllerTest {

    private PerfilController perfilController;
    private CustomUserDetailsService userDetailsService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        perfilController   = new PerfilController(userDetailsService);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();
    }

    // CP01 – perfil(): retorna vista "perfil" y agrega emailActual si existe
    // CP01 - NORMAL: perfil() retorna vista "perfil" con el email del usuario en el modelo.
    // Entrada: usuario autenticado con email registrado
    // Resultado esperado: vista "perfil", modelo contiene "emailActual"
    @Test
    void CP01_perfil_RetornaVistaPerfil_ConEmailActual() {
        // Arrange
        Model model = new ExtendedModelMap();
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");

        Mockito.when(userDetailsService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        // Act
        String vista = perfilController.perfil(userDetails, model);

        // Assert
        Assertions.assertEquals("perfil", vista,
                "perfil() debe retornar la vista 'perfil'");
        Assertions.assertNotNull(model.asMap().get("emailActual"),
                "El modelo debe contener 'emailActual'");
        Assertions.assertEquals("test@test.com", model.asMap().get("emailActual"),
                "emailActual debe coincidir con el email del usuario");
    }

    // CP02 – perfil(): cuando usuario no tiene email, no agrega emailActual
    // CP02 - BORDE: perfil() cuando findByUsername retorna empty no agrega emailActual.
    // Entrada: usuario no encontrado en el servicio
    // Resultado esperado: vista "perfil", modelo NO contiene "emailActual"
    @Test
    void CP02_perfil_UsuarioNoEncontrado_NoAgregaEmailActual() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(userDetailsService.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        // Act
        String vista = perfilController.perfil(userDetails, model);

        // Assert
        Assertions.assertEquals("perfil", vista,
                "perfil() debe retornar la vista 'perfil' incluso sin usuario");
        Assertions.assertNull(model.asMap().get("emailActual"),
                "El modelo NO debe contener 'emailActual' si el usuario no existe");
    }

    // CP03 – actualizarUsername() exitoso: redirige a /login con flash success
    // CP03 - NORMAL: actualizarUsername() exitoso redirige a login e invalida sesión.
    // Entrada: nuevoUsername válido, usuario autenticado
    // Resultado esperado: "redirect:/login", flash "successUser" presente
    @Test
    void CP03_actualizarUsername_Exitoso_RedireccionaALogin_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = perfilController.actualizarUsername("nuevonick", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/login", resultado,
                "actualizarUsername() exitoso debe redirigir a '/login'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("successUser"),
                "Debe existir el atributo flash 'successUser'");
        Mockito.verify(userDetailsService).actualizarUsername("testuser", "nuevonick");
    }

    // CP04 – actualizarUsername() con username duplicado: redirige a /perfil con error
    // CP04 - NEGATIVA: actualizarUsername() con username ya existente redirige a /perfil.
    // Entrada: nuevoUsername ya registrado en el sistema
    // Resultado esperado: "redirect:/perfil", flash "errorUser" presente
    @Test
    void CP04_actualizarUsername_Duplicado_RedireccionaAPerfil_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("El usuario ya existe"))
                .when(userDetailsService).actualizarUsername("testuser", "duplicado");

        // Act
        String resultado = perfilController.actualizarUsername("duplicado", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/perfil", resultado,
                "actualizarUsername() con duplicado debe redirigir a '/perfil'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorUser"),
                "Debe existir el atributo flash 'errorUser'");
    }


    // CP05 – actualizarPassword() exitoso: redirige a /perfil con flash success
    // CP05 - NORMAL: actualizarPassword() exitoso redirige a /perfil con flash "successPass".
    // Entrada: passwords válidas y confirmadas
    // Resultado esperado: "redirect:/perfil", flash "successPass" presente
    @Test
    void CP05_actualizarPassword_Exitoso_RedireccionaAPerfil_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = perfilController.actualizarPassword(
                "actual123", "nueva456", "nueva456", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/perfil", resultado,
                "actualizarPassword() exitoso debe redirigir a '/perfil'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("successPass"),
                "Debe existir el atributo flash 'successPass'");
        Mockito.verify(userDetailsService)
                .actualizarPassword("testuser", "actual123", "nueva456", "nueva456");
    }

    // CP06 – actualizarPassword() con passwords no coincidentes: redirige con error
    // CP06 - NEGATIVA: actualizarPassword() con confirmación incorrecta agrega flash "errorPass".
    // Entrada: passwordNueva != passwordConfirm
    // Resultado esperado: "redirect:/perfil", flash "errorPass" presente
    @Test
    void CP06_actualizarPassword_NoCoinciden_RedireccionaAPerfil_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("Las contraseñas no coinciden"))
                .when(userDetailsService)
                .actualizarPassword("testuser", "actual123", "nueva456", "diferente");

        // Act
        String resultado = perfilController.actualizarPassword(
                "actual123", "nueva456", "diferente", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/perfil", resultado,
                "actualizarPassword() con error debe redirigir a '/perfil'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorPass"),
                "Debe existir el atributo flash 'errorPass'");
    }

    // CP07 – actualizarEmail() exitoso: redirige a /perfil con flash success
    // CP07 - NORMAL: actualizarEmail() exitoso redirige a /perfil con flash "successEmail".
    // Entrada: nuevo email válido
    // Resultado esperado: "redirect:/perfil", flash "successEmail" presente
    @Test
    void CP07_actualizarEmail_Exitoso_RedireccionaAPerfil_ConFlashSuccess() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();

        // Act
        String resultado = perfilController.actualizarEmail(
                "nuevo@mail.com", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/perfil", resultado,
                "actualizarEmail() exitoso debe redirigir a '/perfil'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("successEmail"),
                "Debe existir el atributo flash 'successEmail'");
        Mockito.verify(userDetailsService).actualizarEmail("testuser", "nuevo@mail.com");
    }

    // CP08 – actualizarEmail() con formato inválido: redirige con error
    // CP08 - NEGATIVA: actualizarEmail() con email de formato inválido agrega flash "errorEmail".
    // Entrada: email sin formato válido
    // Resultado esperado: "redirect:/perfil", flash "errorEmail" presente
    @Test
    void CP08_actualizarEmail_FormatoInvalido_RedireccionaAPerfil_ConFlashError() {
        // Arrange
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("Formato de correo inválido"))
                .when(userDetailsService).actualizarEmail("testuser", "noesuncorreo");

        // Act
        String resultado = perfilController.actualizarEmail("noesuncorreo", userDetails, ra);

        // Assert
        Assertions.assertEquals("redirect:/perfil", resultado,
                "actualizarEmail() con error debe redirigir a '/perfil'");
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorEmail"),
                "Debe existir el atributo flash 'errorEmail'");
    }

    // CP09 – perfil(): valor de retorno nunca es nulo
    // CP09 - BORDE: el valor retornado por perfil() no es nulo bajo ninguna circunstancia.
    // Entrada: usuario autenticado
    // Resultado esperado: assertNotNull sobre el valor retornado
    @Test
    void CP09_perfil_RetornoNoEsNulo() {
        // Arrange
        Model model = new ExtendedModelMap();
        Mockito.when(userDetailsService.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        // Act
        String vista = perfilController.perfil(userDetails, model);

        // Assert
        Assertions.assertNotNull(vista,
                "La vista retornada por perfil() no debe ser nula");
    }
}