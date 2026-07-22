package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.Usuario;
import ec.edu.utn.golutn.admin.service.AutenticacionException;
import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

/**
 * Maneja el inicio de sesion del administrador (RF02, CP01, CP02).
 * Vive en sesion mientras el admin navega por el panel.
 */
@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @Inject
    private EstadisticasApiClient apiClient;

    private Usuario usuarioActual;

    public String iniciarSesion(String username, String password) {
        Usuario u;
        try {
            u = apiClient.iniciarSesion(username, password);
        } catch (AutenticacionException e) {
            mostrarErrorAutenticacion(e);
            return null;
        }

        if (!u.isActivo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Acceso restringido",
                            "El usuario se encuentra inactivo."));
            return null;
        }

        if (!"ADMINISTRADOR".equalsIgnoreCase(u.getRolNombre())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Acceso restringido",
                            "Este panel es exclusivo para administradores."));
            return null;
        }

        this.usuarioActual = u;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute("usuarioAutenticado", u);
        return "/panel/dashboard.xhtml?faces-redirect=true";
    }

    private void mostrarErrorAutenticacion(AutenticacionException e) {
        String resumen;
        String detalle;

        switch (e.getTipo()) {
            case CREDENCIALES_INVALIDAS:
                resumen = "Credenciales incorrectas";
                detalle = "El username o la contraseña no son válidos.";
                break;
            case ACCESO_DENEGADO:
                resumen = "Acceso denegado";
                detalle = "El backend no autoriza el acceso de este usuario.";
                break;
            case CONEXION:
                resumen = "Servicio no disponible";
                detalle = "No se pudo conectar con el servicio de autenticación. Intenta nuevamente.";
                break;
            default:
                resumen = "Error de autenticación";
                detalle = "El servicio de autenticación devolvió una respuesta inesperada.";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle));
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isAutenticado() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
}
