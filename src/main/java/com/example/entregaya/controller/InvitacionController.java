package com.example.entregaya.controller;

import com.example.entregaya.service.CustomInvitacionDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InvitacionController {

    private final CustomInvitacionDetailsService customInvitacionDetailsService;

    public InvitacionController(CustomInvitacionDetailsService customInvitacionDetailsService) {
        this.customInvitacionDetailsService = customInvitacionDetailsService;
    }

    @PostMapping("trabajos/{trabajoId}/invitar")
    public String invitar (@PathVariable Long trabajoId, @RequestParam("destinatario") String destinatario, @AuthenticationPrincipal UserDetails user, RedirectAttributes redirectAttributes) {
        try {
            customInvitacionDetailsService.enviarInvitacion(trabajoId, user.getUsername(), destinatario);
            redirectAttributes.addFlashAttribute("succesInv", "Invitacion enviada a "+ destinatario);
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorInv",e.getMessage());
        }
        return "redirect:/trabajos/"+trabajoId;
    }

    @PostMapping("/invitaciones/{id}/aceptar")
    public String aceptar(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails user,
                          RedirectAttributes redirectAttributes) {
        try {
            customInvitacionDetailsService.aceptar(id, user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Te has unido al trabajo");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/invitaciones/{id}/rechazar")
    public String rechazar (@PathVariable Long id, @AuthenticationPrincipal UserDetails user, RedirectAttributes redirectAttributes) {
        try {
            customInvitacionDetailsService.rechazar(id, user.getUsername());
            redirectAttributes.addFlashAttribute("info", "Invitacion rechazada");
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
