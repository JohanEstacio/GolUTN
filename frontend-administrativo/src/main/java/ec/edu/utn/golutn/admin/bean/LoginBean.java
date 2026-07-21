package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.Usuario;
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

    private String correo;
    private String password;
    private Usuario usuarioActual;

    public String iniciarSesion() {
        Usuario u;
        try {
            u = apiClient.iniciarSesion(correo, password);
        } finally {
            password = null;
        }

        if (u == null) {
            // CP02: credenciales invalidas -> mensaje de error claro (RNF10)
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Credenciales incorrectas",
                            "El correo o la contraseña no son validos."));
            return null;
        }

        if (!u.isActivo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Acceso restringido",
                            "El usuario se encuentra inactivo."));
            return null;
        }

        if (!"ADMINISTRADOR".equalsIgnoreCase(u.getRol())) {
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

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isAutenticado() {
        return usuarioActual != null;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Usuario getUsuarioActual() { return usuarioActual; }
}
