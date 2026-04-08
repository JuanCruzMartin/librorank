package com.librorank.web;

import com.librorank.model.Usuario;
import com.librorank.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet de Registro simplificado usando Capa de Servicio.
 */
@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SignupServlet.class);
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre    = request.getParameter("nombre");
        String username  = request.getParameter("usuario");
        String email     = request.getParameter("email");
        String password  = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String terms     = request.getParameter("terms");

        if (nombre == null || nombre.isBlank()
                || username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || password2 == null || !password.equals(password2)
                || terms == null) {

            request.setAttribute("errorSignup", "Completa todos los campos y acepta los términos.");
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setUsername(username);
        nuevo.setEmail(email);

        boolean ok = usuarioService.registrar(nuevo, password);

        if (ok) {
            logger.info("Registro exitoso para: {}", email);
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            logger.warn("Fallo de registro para: {}. Usuario o email duplicado.", email);
            request.setAttribute("errorSignup", "El usuario o email ya están registrados.");
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
        }
    }
}
