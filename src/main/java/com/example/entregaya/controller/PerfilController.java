package com.example.entregaya.controller;

import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private static final String REDIRECT_PERFIL = "redirect:/perfil";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    private final CustomUserDetailsService userDetailsService;

    public PerfilController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        userDetailsService.findByUsername(userDetails.getUsername())
                .ifPresent(user -> model.addAttribute("emailActual", user.getEmail()));
        return "perfil";
    }

    @PostMapping("/perfil/actualizar-username")
    public String actualizarUsername(@RequestParam("nuevoUsername") String nuevoUsername,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.actualizarUsername(userDetails.getUsername(), nuevoUsername);
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("successUser", "Usuario actualizado. Por favor inicia sesión de nuevo.");
            return REDIRECT_LOGIN;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorUser", e.getMessage());
            return REDIRECT_PERFIL;
        }
    }

    @PostMapping("/perfil/actualizar-password")
    public String actualizarPassword(@RequestParam("passwordActual") String passwordActual,
                                     @RequestParam("passwordNueva") String passwordNueva,
                                     @RequestParam("passwordConfirm") String passwordConfirm,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.actualizarPassword(userDetails.getUsername(), passwordActual, passwordNueva, passwordConfirm);
            redirectAttributes.addFlashAttribute("successPass", "Contraseña actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPass", e.getMessage());
        }
        return REDIRECT_PERFIL;
    }

    @PostMapping("/perfil/actualizar-email")
    public String actualizarEmail(@RequestParam("nuevoEmail") String nuevoEmail,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.actualizarEmail(userDetails.getUsername(), nuevoEmail);
            redirectAttributes.addFlashAttribute("successEmail", "Correo actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorEmail", e.getMessage());
        }
        return REDIRECT_PERFIL;
    }
}
