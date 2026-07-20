package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/** Una de las 48 selecciones participantes. */
public class Seleccion implements Serializable {

    private Long id;
    private String nombre;
    private String grupo;      // A, B, C ... L (12 grupos)
    private String bandera;    // URL o codigo de bandera

    public Seleccion() {
    }

    public Seleccion(Long id, String nombre, String grupo, String bandera) {
        this.id = id;
        this.nombre = nombre;
        this.grupo = grupo;
        this.bandera = bandera;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getBandera() { return bandera; }
    public void setBandera(String bandera) { this.bandera = bandera; }
}
