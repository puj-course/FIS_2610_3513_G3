package com.example.entregaya.controller;


import com.example.entregaya.dto.MiembroRolDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/trabajos")
public class TrabajoController {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomInvitacionDetailsService customInvitacionDetailsService;

    public TrabajoController(CustomTrabajoDetailsService customTrabajoDetailsService,
                             CustomTareaDetailsService customTareaDetailsService, CustomInvitacionDetailsService customInvitacionDetailsService) {
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customInvitacionDetailsService = customInvitacionDetailsService;
    }

    @GetMapping
    public String trabajo(Model model, @AuthenticationPrincipal UserDetails user) {
        List<Trabajo>trabajos= customTrabajoDetailsService.listarPorUsuario(user.getUsername());
        Map<Long,Integer> progresos = new HashMap<>();
        for(Trabajo t : trabajos){
            progresos.put(t.getId(), customTareaDetailsService.calcularProgreso(t.getId()));
        }

        model.addAttribute("trabajos", trabajos);
        model.addAttribute("progresos", progresos);
        return "trabajos/lista";
    }

    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("trabajo", new Trabajo());
        return "trabajos/formulario";
    }
    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Trabajo trabajo, @AuthenticationPrincipal UserDetails user) {
        customTrabajoDetailsService.crearTrabajo(trabajo,user.getUsername());
        return "redirect:/trabajos";
    }

    /**
     * HU-15 (#226): Mostrar formulario de edición (solo LIDER)
     * Valida que el usuario sea LIDER antes de permitir acceso al formulario.
     * Si no es LIDER, redirige con mensaje de error.
     * 
     * @param id ID del trabajo a editar
     * @param model Modelo para pasar datos a la vista
     * @param user Usuario autenticado
     * @param redirectAttributes Para mensajes flash
     * @return Vista editar.html si es LIDER, redirect si no
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable long id, 
                                          Model model, 
                                          @AuthenticationPrincipal UserDetails user,
                                          RedirectAttributes redirectAttributes) {
        try {
            // Verificar que el trabajo existe
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
            
            // VALIDACIÓN CRÍTICA: Solo LIDER puede editar
            if (!customTrabajoDetailsService.esLider(id, user.getUsername())) {
                // Determinar rol del usuario para mensaje específico
                boolean esColaborador = trabajo.getColaboradores().stream()
                        .anyMatch(col -> col.getUser().getUsername().equals(user.getUsername()));
                
                if (esColaborador) {
                    redirectAttributes.addFlashAttribute("error", 
                        "No tienes permisos para editar este trabajo. Solo el líder puede hacerlo.");
                } else {
                    redirectAttributes.addFlashAttribute("error", 
                        "No perteneces a este trabajo.");
                }
                return "redirect:/trabajos/" + id;
            }
            
            // Usuario es LIDER - permitir acceso al formulario
            model.addAttribute("trabajo", trabajo);
            return "trabajos/editar";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "El trabajo no existe o no tienes acceso a él.");
            return "redirect:/trabajos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cargar el formulario de edición.");
            return "redirect:/trabajos";
        }
    }

    /**
     * HU-15 (#226): Procesar edición del trabajo (solo LIDER)
     * Valida permisos LIDER antes de guardar cambios.
     * Valida datos del formulario (nombre único, no vacío).
     * 
     * @param id ID del trabajo a actualizar
     * @param trabajoEditado Datos del formulario
     * @param user Usuario autenticado
     * @param redirectAttributes Para mensajes flash
     * @return Redirect a detalle si éxito, a editar si error
     */
    @PostMapping("/{id}/editar")
    public String actualizarTrabajo(@PathVariable long id,
                                   @ModelAttribute Trabajo trabajoEditado,
                                   @AuthenticationPrincipal UserDetails user,
                                   RedirectAttributes redirectAttributes) {
        try {
            // VALIDACIÓN CRÍTICA: Solo LIDER puede editar
            if (!customTrabajoDetailsService.esLider(id, user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", 
                    "No tienes permisos para editar este trabajo. Solo el líder puede hacerlo.");
                return "redirect:/trabajos/" + id;
            }
            
            // Validar que el nombre no esté vacío
            if (trabajoEditado.getNombreTrabajo() == null || trabajoEditado.getNombreTrabajo().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre del trabajo no puede estar vacío.");
                return "redirect:/trabajos/" + id + "/editar";
            }
            
            // Actualizar el trabajo (incluye validación de nombre único)
            customTrabajoDetailsService.actualizarTrabajo(id, trabajoEditado);
            
            redirectAttributes.addFlashAttribute("success", "Trabajo actualizado correctamente.");
            return "redirect:/trabajos/" + id;
            
        } catch (IllegalArgumentException e) {
            // Error de validación (nombre duplicado, etc.)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/" + id + "/editar";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el trabajo.");
            return "redirect:/trabajos/" + id + "/editar";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model,  @AuthenticationPrincipal UserDetails user) {
        // Verificar si el usuario actual es LIDER
        boolean esLider = customTrabajoDetailsService.esLider(id, user.getUsername());
        boolean puedeGestionar = customTrabajoDetailsService.puedeEditarTarea(id, user.getUsername());
        model.addAttribute("esLider", esLider);
        model.addAttribute("puedeGestionar", puedeGestionar);
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
        List<Tarea> tareas = customTareaDetailsService.tareas(id);

        trabajo.getColaboradores().size();
        tareas.forEach(t -> t.getResponsables().size());

        // Calcular estadísticas
        long completadas = tareas.stream().filter(Tarea::getIsCompletada).count();
        long pendientes = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        (t.getFechaInicio() == null || t.getFechaInicio().isAfter(java.time.LocalDateTime.now())))
                .count();
        long enProgreso = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        t.getFechaInicio() != null &&
                        !t.getFechaInicio().isAfter(java.time.LocalDateTime.now()))
                .count();

        // Obtener próximas entregas (tareas no completadas ordenadas por fecha)
        List<Tarea> proximasEntregas = tareas.stream()
                .filter(t -> !t.getIsCompletada() && t.getFechaFinal() != null)
                .sorted((t1, t2) -> t1.getFechaFinal().compareTo(t2.getFechaFinal()))
                .limit(5)
                .toList();

        // HU-11: Lógica de comparación de fechas para alertas visuales
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime en24Horas = ahora.plusHours(24);

        // Identificar tareas vencidas (fechaFinal < ahora && !completada)
        List<Long> tareasVencidas = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        t.getFechaFinal() != null &&
                        t.getFechaFinal().isBefore(ahora))
                .map(Tarea::getId)
                .toList();

        // Identificar tareas que vencen pronto (fechaFinal entre ahora y ahora+24h && !completada)
        List<Long> tareasVencenPronto = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        t.getFechaFinal() != null &&
                        t.getFechaFinal().isAfter(ahora) &&
                        t.getFechaFinal().isBefore(en24Horas))
                .map(Tarea::getId)
                .toList();

        // Miembros con roles para mostrar en la vista de detalle
        List<MiembroRolDTO> miembros = customTrabajoDetailsService.consultarMiembros(id);

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("tareas", tareas);
        model.addAttribute("progreso", customTareaDetailsService.calcularProgreso(id));
        model.addAttribute("completadas", completadas);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProgreso", enProgreso);
        model.addAttribute("proximasEntregas", proximasEntregas);
        model.addAttribute("invitaciones", customInvitacionDetailsService.porTrabajo(id));
        model.addAttribute("miembros", miembros);
        // HU-11: Agregar listas de alertas al modelo
        model.addAttribute("tareasVencidas", tareasVencidas);
        model.addAttribute("tareasVencenPronto", tareasVencenPronto);
        return "trabajos/detalle";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar (@PathVariable long id) {
        customTrabajoDetailsService.eliminar(id);
        return "redirect:/trabajos";
    }

    /**
     * Eliminar un colaborador de un trabajo.
     * Solo accesible desde la vista para líderes (la restricción visual está en detalle.html).
     * Se puede agregar validación de rol en el servicio para mayor seguridad.
     */
    @PostMapping("/{id}/eliminarColaborador")
    public String eliminarColaborador(@PathVariable long id,
                                      @RequestParam Long userId,
                                      @AuthenticationPrincipal UserDetails currentUser,
                                      RedirectAttributes redirectAttributes) {
        try {
            customTrabajoDetailsService.eliminarColaborador(id, userId, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Colaborador eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar al colaborador.");
        }
        return "redirect:/trabajos/" + id;
    }

    @GetMapping("/CrearTarea")
    public String CrearTarea(Model model) {
        model.addAttribute("tarea", new Tarea());
        return "trabajos/CrearTarea";
    }

    @GetMapping("/trabajos-especificos")
    public String TrabajosEspecificos(Model model) {
        model.addAttribute("trabajo", new Trabajo());
        return "trabajos-especificos";
    }

    @GetMapping("/{id}/detalle")
    public String DetallesxId(@PathVariable long id, Model model) {
        model.addAttribute("trabajo", customTrabajoDetailsService.obtenerPorId(id));
        return "trabajos/detalle";
    }

    @GetMapping("/{id}/miembros")
    public String mostrarMiembros(@PathVariable long id, Model model) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);

        // Convertir Set a List y ordenar: LIDER primero, luego por username
        List<ColaboradorTrabajo> miembrosOrdenados = new ArrayList<>(trabajo.getColaboradores());
        miembrosOrdenados.sort((m1, m2) -> {
            // LIDER siempre primero
            if (m1.getRol() == ColaboradorTrabajo.Rol.LIDER) return -1;
            if (m2.getRol() == ColaboradorTrabajo.Rol.LIDER) return 1;
            // Si ambos son COLABORADOR, ordenar por username
            return m1.getUser().getUsername().compareTo(m2.getUser().getUsername());
        });

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("miembros", miembrosOrdenados);
        model.addAttribute("totalMiembros", miembrosOrdenados.size());
        return "trabajos/miembros";
    }

    // Endpoint REST para obtener miembros con roles (JSON)
    @GetMapping("/{id}/miembros/roles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerMiembrosConRoles(@PathVariable long id) {
        try {
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);

            if (trabajo == null) {
                return ResponseEntity.notFound().build();
            }

            List<ColaboradorTrabajo> colaboradores = new ArrayList<>(trabajo.getColaboradores());
            List<Map<String, Object>> miembros = new ArrayList<>();

            for (int i = 0; i < colaboradores.size(); i++) {
                ColaboradorTrabajo colaborador = colaboradores.get(i);
                User user = colaborador.getUser();

                // Obtener el rol real desde la entidad ColaboradorTrabajo
                String rol = colaborador.getRol().name(); // LIDER, EDITOR, o COLABORADOR

                Map<String, Object> miembro = new HashMap<>();
                miembro.put("id", user.getId());
                miembro.put("username", user.getUsername());
                miembro.put("rol", rol);
                miembro.put("trabajosCount", user.getTrabajos().size());
                miembro.put("posicion", i + 1);

                miembros.add(miembro);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("trabajoId", trabajo.getId());
            response.put("nombreTrabajo", trabajo.getNombreTrabajo());
            response.put("totalMiembros", miembros.size());
            response.put("totalTareas", trabajo.getTareas().size());
            response.put("miembros", miembros);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint REST para actualizar el rol de un miembro
    @PutMapping("/{trabajoId}/miembros/{userId}/rol")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarRolMiembro(
            @PathVariable Long trabajoId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails user) {
        try {
            // VALIDACIÓN: Verificar que el usuario autenticado sea LIDER
            if (!customTrabajoDetailsService.esLider(trabajoId, user.getUsername())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Solo los LIDER pueden cambiar los roles de los miembros");
                return ResponseEntity.status(403).body(errorResponse);
            }

            // Obtener el rol del request
            String rolStr = request.get("rol");
            if (rolStr == null || rolStr.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El rol es requerido");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validar que el rol sea válido
            ColaboradorTrabajo.Rol nuevoRol;
            try {
                nuevoRol = ColaboradorTrabajo.Rol.valueOf(rolStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Rol inválido. Los roles válidos son: LIDER, EDITOR, COLABORADOR");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Actualizar el rol (incluye validaciones de existencia y pertenencia)
            customTrabajoDetailsService.cambiarRol(trabajoId, userId, nuevoRol);

            // Retornar respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol actualizado correctamente");
            response.put("trabajoId", trabajoId);
            response.put("userId", userId);
            response.put("nuevoRol", nuevoRol.name());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Errores de validación (trabajo no existe, usuario no pertenece, etc.)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            // Otros errores
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al actualizar el rol");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    @GetMapping("/{id}/tareas/calendario")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> calendarioTareas(@PathVariable long id) {
        try {
            List<Tarea> tareas = customTareaDetailsService.tareas(id);
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            List<Map<String, Object>> eventos = new ArrayList<>();

            for (Tarea tarea : tareas) {
                // Solo incluir tareas que tengan al menos una fecha
                if (tarea.getFechaInicio() == null && tarea.getFechaFinal() == null) continue;

                boolean vencida = !tarea.getIsCompletada()
                        && tarea.getFechaFinal() != null
                        && tarea.getFechaFinal().isBefore(ahora);

                Map<String, Object> evento = new HashMap<>();
                evento.put("id",          tarea.getId().toString());
                evento.put("title",       tarea.getNombre());
                evento.put("start",       tarea.getFechaInicio() != null ? tarea.getFechaInicio().format(fmt) : tarea.getFechaFinal().format(fmt));
                evento.put("end",         tarea.getFechaFinal() != null ? tarea.getFechaFinal().format(fmt) : null);
                evento.put("completada",  tarea.getIsCompletada());
                evento.put("vencida",     vencida);
                evento.put("dificultad",  tarea.getDificultad().name());

                eventos.add(evento);
            }

            return ResponseEntity.ok(eventos);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{id}/calendario")
    public String vistaCalendario(@PathVariable long id, Model model,
                                  @AuthenticationPrincipal UserDetails user) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
        model.addAttribute("trabajo", trabajo);
        model.addAttribute("trabajoId", id);
        return "trabajos/calendario";
    }


    /**
     *
     *  @return 201 Created con el nuevo trabajo, o 403 Forbidden si no es LIDER,
     *          o 404 si el trabajo no existe.
     *
     *
     *
     */

    @PostMapping("/{id}/clonar")
    @ResponseBody
    public ResponseEntity<?> clonar(@PathVariable long id,
                                    @AuthenticationPrincipal UserDetails user) {
        try{
            Trabajo nuevo = customTrabajoDetailsService.clonarTrabajo(id, user.getUsername());
            return ResponseEntity.status(201).body(Map.of(
                    "id", nuevo.getId(),
                    "nombre", nuevo.getNombreTrabajo(),
                    "mensaje", "Trabajo clonado con exito"
            ));
        }catch (SecurityException e){
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

}