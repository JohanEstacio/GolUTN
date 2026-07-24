package ec.edu.utn.golutn.admin.util;

import ec.edu.utn.golutn.admin.model.Usuario;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Protege /panel/* y permite el acceso solo a administradores activos.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Object usuarioEnSesion = session == null
                ? null
                : session.getAttribute("usuarioAutenticado");
        Usuario usuario = usuarioEnSesion instanceof Usuario
                ? (Usuario) usuarioEnSesion
                : null;

        if (usuario == null
                || !usuario.isActivo()
                || !"ADMINISTRADOR".equalsIgnoreCase(usuario.getRolNombre())) {
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
            return;
        }

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);
        chain.doFilter(request, response);
    }
}
