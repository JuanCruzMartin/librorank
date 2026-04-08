package com.librorank.web;

import com.librorank.dao.LibroGlobalDAO;
import com.librorank.model.LibroGlobal;
import com.librorank.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/libro")
public class LibroDetalleServlet extends HttpServlet {
    private final LibroGlobalDAO libroGlobalDAO = new LibroGlobalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("biblioteca");
            return;
        }

        int idGlobal = Integer.parseInt(idStr);
        LibroGlobal libro = libroGlobalDAO.buscarPorId(idGlobal);
        List<Libro> reviews = libroGlobalDAO.obtenerReviewsGlobales(idGlobal);

        request.setAttribute("libroGlobal", libro);
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/libro_detalle.jsp").forward(request, response);
    }
}
