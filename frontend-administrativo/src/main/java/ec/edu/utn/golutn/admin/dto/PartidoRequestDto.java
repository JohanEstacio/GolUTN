package ec.edu.utn.golutn.admin.dto;

import ec.edu.utn.golutn.admin.model.Partido;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Cuerpo de POST /api/Partidos y PUT /api/Partidos/{id}. */
public class PartidoRequestDto implements Serializable {

    private Long id;
    private Integer numeroPartidoFifa;
    private LocalDateTime fechaPartido;
    private Partido.Estado estado;
    private Integer golesLocal;
    private Integer golesVisitante;
    private String faseCodigo;
    private String grupoCodigo;
    private Long sedeId;
    private Long localId;
    private Long visitanteId;

    public PartidoRequestDto() {
    }

    public PartidoRequestDto(Integer numeroPartidoFifa, LocalDateTime fechaPartido, Partido.Estado estado,
                              Integer golesLocal, Integer golesVisitante, String faseCodigo, String grupoCodigo,
                              Long sedeId, Long localId, Long visitanteId) {
        this.numeroPartidoFifa = numeroPartidoFifa;
        this.fechaPartido = fechaPartido;
        this.estado = estado;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.faseCodigo = faseCodigo;
        this.grupoCodigo = grupoCodigo;
        this.sedeId = sedeId;
        this.localId = localId;
        this.visitanteId = visitanteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumeroPartidoFifa() { return numeroPartidoFifa; }
    public void setNumeroPartidoFifa(Integer numeroPartidoFifa) { this.numeroPartidoFifa = numeroPartidoFifa; }

    public LocalDateTime getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(LocalDateTime fechaPartido) { this.fechaPartido = fechaPartido; }

    public Partido.Estado getEstado() { return estado; }
    public void setEstado(Partido.Estado estado) { this.estado = estado; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }

    public String getFaseCodigo() { return faseCodigo; }
    public void setFaseCodigo(String faseCodigo) { this.faseCodigo = faseCodigo; }

    public String getGrupoCodigo() { return grupoCodigo; }
    public void setGrupoCodigo(String grupoCodigo) { this.grupoCodigo = grupoCodigo; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public Long getLocalId() { return localId; }
    public void setLocalId(Long localId) { this.localId = localId; }

    public Long getVisitanteId() { return visitanteId; }
    public void setVisitanteId(Long visitanteId) { this.visitanteId = visitanteId; }
}
