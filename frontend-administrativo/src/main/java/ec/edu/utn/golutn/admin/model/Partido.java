package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Uno de los 104 partidos del torneo. */
public class Partido implements Serializable {

    public enum Estado { PROGRAMADO, EN_JUEGO, FINALIZADO }

    private Long id;
    private String seleccionLocal;
    private String seleccionVisitante;
    private String sede;
    private String fase;              // Fase de grupos, Octavos, Cuartos, Semifinal, Final...
    private LocalDateTime fechaHora;
    private Estado estado;
    private Integer golesLocal;        // null hasta que se registre el resultado
    private Integer golesVisitante;

    public Partido() {
    }

    public Partido(Long id, String seleccionLocal, String seleccionVisitante, String sede,
                    String fase, LocalDateTime fechaHora, Estado estado,
                    Integer golesLocal, Integer golesVisitante) {
        this.id = id;
        this.seleccionLocal = seleccionLocal;
        this.seleccionVisitante = seleccionVisitante;
        this.sede = sede;
        this.fase = fase;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeleccionLocal() { return seleccionLocal; }
    public void setSeleccionLocal(String seleccionLocal) { this.seleccionLocal = seleccionLocal; }

    public String getSeleccionVisitante() { return seleccionVisitante; }
    public void setSeleccionVisitante(String seleccionVisitante) { this.seleccionVisitante = seleccionVisitante; }

    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }
}
