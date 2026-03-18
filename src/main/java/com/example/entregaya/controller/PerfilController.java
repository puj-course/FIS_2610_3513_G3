package com.example.entregaya.controller;

import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final CustomUserDetailsService userDetailsService;

    public PerfilController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @PostMapping("/perfil/actualizar-username")
    public String actualizarUsername(@RequestParam("nuevoUsername") String nuevoUsername,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.actualizarUsername(userDetails.getUsername(), nuevoUsername);
            // Invalidar sesión para que Spring Security refresque el principal
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("successUser", "Usuario actualizado. Por favor inicia sesión de nuevo.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorUser", e.getMessage());
            return "redirect:/perfil";
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
        return "redirect:/perfil";
    }
}