package com.librorank.web;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin-usuarios")
public class AdminUsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String deleteId = req.getParameter("borrar");
        if (deleteId != null) {
            borrarUsuario(Integer.parseInt(deleteId));
            resp.sendRedirect("admin-usuarios");
            return;
        }

        List<Usuario> usuarios = listarUsuarios();

        out.println("<html><head><title>Admin Usuarios</title>");
        out.println("<style>body{background:#1a1a1a;color:white;font-family:sans-serif;padding:20px;}");
        out.println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
        out.println("th,td{padding:12px;border:1px solid #333;text-align:left;}");
        out.println("th{background:#d4af37;color:black;}");
        out.println(".btn-del{background:#ff5e57;color:white;padding:5px 10px;text-decoration:none;border-radius:4px;}</style>");
        out.println("</head><body>");
        out.println("<h1>Administración de Usuarios</h1>");
        out.println("<p>Esta página muestra los usuarios de la base de datos actual.</p>");
        out.println("<table><tr><th>ID</th><th>Nombre</th><th>Username</th><th>Email</th><th>Acción</th></tr>");

        for (Usuario u : usuarios) {
            out.println("<tr>");
            out.println("<td>" + u.getId() + "</td>");
            out.println("<td>" + u.getNombre() + "</td>");
            out.println("<td>" + u.getUsername() + "</td>");
            out.println("<td>" + u.getEmail() + "</td>");
            out.println("<td><a href='admin-usuarios?borrar=" + u.getId() + "' class='btn-del' onclick='return confirm(\"¿Borrar usuario y sus libros?\")'>Borrar</a></td>");
            out.println("</tr>");
        }

        out.println("</table></body></html>");
    }

    private List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nombre, username, email FROM usuarios")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                lista.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private void borrarUsuario(int id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Borrar libros primero por la FK
                try (PreparedStatement st1 = conn.prepareStatement("DELETE FROM libros_usuario WHERE usuario_id = ?")) {
                    st1.setInt(1, id);
                    st1.executeUpdate();
                }
                // Borrar usuario
                try (PreparedStatement st2 = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
                    st2.setInt(1, id);
                    st2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
