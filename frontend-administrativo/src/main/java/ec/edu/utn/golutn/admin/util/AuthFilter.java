package ec.edu.utn.golutn.admin.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Filtro base para proteger /panel/*.
 *
 * NOTA PARA EL EQUIPO: por ahora deja pasar todas las peticiones porque el
 * mecanismo de autenticacion definitivo (sesion + rol validado contra el
 * Servicio de Estadisticas) todavia se esta afinando junto con el backend.
 * Cuando el login contra la API este 100% estable, aqui se debe:
 *   1. Leer el usuario autenticado desde la sesion HTTP.
 *   2. Si no existe o su rol no es ADMINISTRADOR, redirigir a /login.xhtml.
 * Dejamos el filtro ya registrado en web.xml para no olvidarlo.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        chain.doFilter(request, response);
    }
}
