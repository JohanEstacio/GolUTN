package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/** Una fase del torneo (fase de grupos, octavos, cuartos, semifinal, final...). */
public class Fase implements Serializable {

    private String codigo;
    private String nombre;
    private Integer orden;

    public Fase() {
    }

    public Fase(String codigo, String nombre, Integer orden) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.orden = orden;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
