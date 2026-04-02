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

    public DashboardDTO() {}

    public DashboardDTO(List<Trabajo> trabajos,
                        Map<Long, Integer> progresos,
                        int totalTrabajos,
                        int completados,
                        List<Trabajo> proximosVencer,
                        List<Invitacion> invitaciones,
                        long tareasVencidas) {
        this.trabajos = trabajos;
        this.progresos = progresos;
        this.totalTrabajos = totalTrabajos;
        this.completados = completados;
        this.proximosVencer = proximosVencer;
        this.invitaciones = invitaciones;
        this.tareasVencidas = tareasVencidas;
    }

    public List<Trabajo> getTrabajos() { return trabajos; }
    public void setTrabajos(List<Trabajo> trabajos) { this.trabajos = trabajos; }

    public Map<Long, Integer> getProgresos() { return progresos; }
    public void setProgresos(Map<Long, Integer> progresos) { this.progresos = progresos; }

    public int getTotalTrabajos() { return totalTrabajos; }
    public void setTotalTrabajos(int totalTrabajos) { this.totalTrabajos = totalTrabajos; }

    public int getCompletados() { return completados; }
    public void setCompletados(int completados) { this.completados = completados; }

    public List<Trabajo> getProximosVencer() { return proximosVencer; }
    public void setProximosVencer(List<Trabajo> proximosVencer) { this.proximosVencer = proximosVencer; }

    public List<Invitacion> getInvitaciones() { return invitaciones; }
    public void setInvitaciones(List<Invitacion> invitaciones) { this.invitaciones = invitaciones; }

    public long getTareasVencidas() { return tareasVencidas; }
    public void setTareasVencidas(long tareasVencidas) { this.tareasVencidas = tareasVencidas; }
}
