package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/** Uno de los 12 grupos de la fase de grupos (A, B, C ... L). */
public class Grupo implements Serializable {

    private String codigo;
    private String nombre;

    public Grupo() {
    }

    public Grupo(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
