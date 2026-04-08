package com.librorank.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Set;

/**
 * Filtro de Autenticación Global.
 */
@WebFilter("/*")
public class AutenticacionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionFilter.class);

    private static final Set<String> RUTAS_PUBLICAS = Set.of(
        "/login.jsp",
        "/login",
        "/signup.jsp",
        "/signup",
        "/index.jsp",
        "/",
        "/logout"
    );

    private static final Set<String> CARPETAS_ESTATICAS = Set.of(
        "/css/",
        "/js/",
        "/img/",
        "/assets/",
        "/Css/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();
        if (path == null || path.isEmpty()) path = "/";

        HttpSession session = httpRequest.getSession(false);
        boolean estaLogueado = (session != null && session.getAttribute("usuarioLogueado") != null);

        // Verificar si es un recurso estático de forma más eficiente
        boolean esRecursoEstatico = false;
        for (String carpeta : CARPETAS_ESTATICAS) {
            if (path.startsWith(carpeta)) {
                esRecursoEstatico = true;
                break;
            }
        }

        boolean esRutaPublica = esRecursoEstatico || RUTAS_PUBLICAS.contains(path);

        if (estaLogueado || esRutaPublica) {
            chain.doFilter(request, response);
        } else {
            logger.warn("Acceso denegado a ruta protegida: {}", path);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    @Override public void init(FilterConfig filterConfig) throws ServletException {}
    @Override public void destroy() {}
}
