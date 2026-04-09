package com.example.entregaya.dto;

import com.example.entregaya.model.Invitacion;
import com.example.entregaya.model.Trabajo;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private List<Trabajo> trabajos;
    private Map<Long, Integer> progresos;
    private int totalTrabajos;
    private int completados;
    private List<Trabajo> proximosVencer;
    private List<Invitacion> invitaciones;
    private long tareasVencidas;
    private long notificacionesNoLeidas;   // DoD D6

    public DashboardDTO() {}

    public DashboardDTO(List<Trabajo> trabajos,
                        Map<Long, Integer> progresos,
                        int totalTrabajos,
                        int completados,
                        List<Trabajo> proximosVencer,
                        List<Invitacion> invitaciones,
                        long tareasVencidas,
                        long notificacionesNoLeidas) {
        this.trabajos = trabajos;
        this.progresos = progresos;
        this.totalTrabajos = totalTrabajos;
        this.completados = completados;
        this.proximosVencer = proximosVencer;
        this.invitaciones = invitaciones;
        this.tareasVencidas = tareasVencidas;
        this.notificacionesNoLeidas = notificacionesNoLeidas;
    }

    public List<Trabajo>      getTrabajos()               { return trabajos; }
    public void               setTrabajos(List<Trabajo> t){ this.trabajos = t; }

    public Map<Long, Integer> getProgresos()              { return progresos; }
    public void               setProgresos(Map<Long, Integer> p) { this.progresos = p; }

    public int                getTotalTrabajos()           { return totalTrabajos; }
    public void               setTotalTrabajos(int t)     { this.totalTrabajos = t; }

    public int                getCompletados()             { return completados; }
    public void               setCompletados(int c)       { this.completados = c; }

    public List<Trabajo>      getProximosVencer()          { return proximosVencer; }
    public void               setProximosVencer(List<Trabajo> p) { this.proximosVencer = p; }

    public List<Invitacion>   getInvitaciones()            { return invitaciones; }
    public void               setInvitaciones(List<Invitacion> i) { this.invitaciones = i; }

    public long               getTareasVencidas()          { return tareasVencidas; }
    public void               setTareasVencidas(long t)   { this.tareasVencidas = t; }

    public long               getNotificacionesNoLeidas()  { return notificacionesNoLeidas; }
    public void               setNotificacionesNoLeidas(long n) { this.notificacionesNoLeidas = n; }
}