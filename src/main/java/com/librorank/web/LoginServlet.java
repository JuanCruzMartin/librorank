package com.librorank.web;

import com.librorank.model.Usuario;
import com.librorank.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet de Login simplificado usando Capa de Servicio.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String identificador = request.getParameter("identificador");
        String password      = request.getParameter("password");

        if (identificador == null || identificador.isBlank()
                || password == null || password.isBlank()) {

            request.setAttribute("errorLogin", "Completa usuario/email y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioService.login(identificador, password);

        if (usuario == null) {
            logger.warn("Fallo de login para: {}", identificador);
            request.setAttribute("errorLogin", "Usuario o contraseña incorrecta");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        logger.info("Login exitoso para: {}", identificador);
        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogueado", usuario);

        response.sendRedirect(request.getContextPath() + "/perfil");
    }
}
