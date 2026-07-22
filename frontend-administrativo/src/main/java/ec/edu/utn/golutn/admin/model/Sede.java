package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/** Un estadio sede de partidos del torneo. */
public class Sede implements Serializable {

    private Long id;
    private String nombre;
    private String ciudad;
    private String pais;

    public Sede() {
    }

    public Sede(Long id, String nombre, String ciudad, String pais) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
