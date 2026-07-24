package ec.edu.utn.golutn.admin.dto;

import java.io.Serializable;

/** Cuerpo de POST /api/Selecciones y PUT /api/Selecciones/{id}. */
public class SeleccionRequestDto implements Serializable {

    private Integer id;
    private String codigoFifa;
    private String nombre;
    private String confederacion;
    private boolean esAnfitrion;
    private String clasificacion;
    private boolean eliminada;
    private String grupoCodigo;

    public SeleccionRequestDto() {
    }

    public SeleccionRequestDto(Integer id, String codigoFifa, String nombre, String confederacion,
                                boolean esAnfitrion, String clasificacion, boolean eliminada, String grupoCodigo) {
        this.id = id;
        this.codigoFifa = codigoFifa;
        this.nombre = nombre;
        this.confederacion = confederacion;
        this.esAnfitrion = esAnfitrion;
        this.clasificacion = clasificacion;
        this.eliminada = eliminada;
        this.grupoCodigo = grupoCodigo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoFifa() { return codigoFifa; }
    public void setCodigoFifa(String codigoFifa) { this.codigoFifa = codigoFifa; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getConfederacion() { return confederacion; }
    public void setConfederacion(String confederacion) { this.confederacion = confederacion; }

    public boolean isEsAnfitrion() { return esAnfitrion; }
    public void setEsAnfitrion(boolean esAnfitrion) { this.esAnfitrion = esAnfitrion; }

    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    public boolean isEliminada() { return eliminada; }
    public void setEliminada(boolean eliminada) { this.eliminada = eliminada; }

    public String getGrupoCodigo() { return grupoCodigo; }
    public void setGrupoCodigo(String grupoCodigo) { this.grupoCodigo = grupoCodigo; }
}
