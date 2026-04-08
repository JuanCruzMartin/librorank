package com.librorank.web;

import com.librorank.dao.BingoDAO;
import com.librorank.dao.LibroDAO;
import com.librorank.model.BingoCasilla;
import com.librorank.model.Libro;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/bingo")
public class BingoServlet extends HttpServlet {
    private final BingoDAO bingoDAO = new BingoDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        List<BingoCasilla> bingo = bingoDAO.obtenerBingo(usuario.getId());
        List<Libro> misLibros = libroDAO.buscarPorUsuario(usuario.getId());

        request.setAttribute("bingo", bingo);
        request.setAttribute("misLibros", misLibros);
        
        request.getRequestDispatcher("/bingo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        try {
            String retoIdStr = request.getParameter("retoId");
            String libroIdStr = request.getParameter("libroId");
            
            if (retoIdStr == null || libroIdStr == null) {
                response.sendRedirect("bingo?error=missing_params");
                return;
            }

            int retoId = Integer.parseInt(retoIdStr);
            int libroId = Integer.parseInt(libroIdStr);

            if (bingoDAO.marcarCasilla(usuario.getId(), retoId, libroId)) {
                response.sendRedirect("bingo?success=true");
            } else {
                response.sendRedirect("bingo?error=true");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("bingo?error=invalid_params");
        }
    }
}
