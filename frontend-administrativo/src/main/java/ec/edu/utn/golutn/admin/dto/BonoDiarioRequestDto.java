package ec.edu.utn.golutn.admin.dto;

import java.io.Serializable;
import java.time.LocalDate;

/** Cuerpo de POST /api/bonos/otorgar (RF20: bono anti-bancarrota). */
public class BonoDiarioRequestDto implements Serializable {

    private Long usuarioId;
    private LocalDate fecha;

    public BonoDiarioRequestDto() {
    }

    public BonoDiarioRequestDto(Long usuarioId, LocalDate fecha) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
