package ec.edu.utn.golutn.admin.dto;

import java.io.Serializable;
import java.time.LocalDate;

/** Cuerpo de POST /api/bonos/otorgar (RF20: bono anti-bancarrota). */
public class BonoDiarioRequestDto implements Serializable {

    private Long usuarioId;
    private LocalDate fechaSimulada;

    public BonoDiarioRequestDto() {
    }

    public BonoDiarioRequestDto(Long usuarioId, LocalDate fechaSimulada) {
        this.usuarioId = usuarioId;
        this.fechaSimulada = fechaSimulada;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getFechaSimulada() { return fechaSimulada; }
    public void setFechaSimulada(LocalDate fechaSimulada) { this.fechaSimulada = fechaSimulada; }
}
