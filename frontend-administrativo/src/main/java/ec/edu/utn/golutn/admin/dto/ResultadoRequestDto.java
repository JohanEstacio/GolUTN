package ec.edu.utn.golutn.admin.dto;

import java.io.Serializable;

/** Cuerpo de PUT /api/Partidos/{id}/resultado. */
public class ResultadoRequestDto implements Serializable {

    private Integer golesLocal;
    private Integer golesVisitante;
    private Long usuarioId;

    public ResultadoRequestDto() {
    }

    public ResultadoRequestDto(Integer golesLocal, Integer golesVisitante, Long usuarioId) {
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.usuarioId = usuarioId;
    }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
