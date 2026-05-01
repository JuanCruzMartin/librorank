<%@ page import="com.librorank.model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- Agregamos FMT para números --%>

<%
    // 1. OBTENCIÓN DE DATOS
    List<Usuario> ranking = (List<Usuario>) request.getAttribute("ranking");
    if (ranking == null) {
        ranking = java.util.Collections.emptyList();
    }

    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    Integer posicionUsuario = null;

    // 2. LÓGICA DE POSICIONAMIENTO PERSONAL
    if (usuarioLogueado != null) {
        int posTemp = 1;
        for (Usuario u : ranking) {
            if (u.getId() == usuarioLogueado.getId()) {
                posicionUsuario = posTemp;
                break;
            }
            posTemp++;
        }
    }

    // 3. GENERACIÓN DE AVATAR (INICIALES)
    String inicialesUsuario = "?";
    if (usuarioLogueado != null && usuarioLogueado.getNombre() != null) {
        String nombre = usuarioLogueado.getNombre().trim();
        inicialesUsuario = nombre.length() > 0 ? nombre.substring(0, 1).toUpperCase() : "?";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ranking de lectores — LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<main id="content" class="page">
    <section class="section">
        <div class="container ranking-layout">



            <section class="card">
                <div class="table-wrapper">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Usuario</th>
                                <th>Puntos</th>
                                <th>Libros leídos</th>
                                <th>Título</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="u" items="${ranking}" varStatus="status">
                                <%-- Usamos el operador ternario para pintar tu fila --%>
                                <tr class="${usuarioLogueado != null && u.id == usuarioLogueado.id ? 'table-row--me' : ''}">

                                    <td><strong>${status.count}°</strong></td>

                                    <td>
                                        <c:set var="user" value="${u}" scope="request" />
                                        <c:set var="context" value="ranking" scope="request" />
                                        <jsp:include page="/includes/userCard.jsp" />
                                    </td>

                                    <td style="text-align: center;">
                                        <span class="badge" style="background: rgba(212, 175, 55, 0.1); color: var(--accent-gold); border: 1px solid var(--accent-gold);">
                                            ⭐ ${u.puntos} pts
                                        </span>
                                    </td>

                                    <td style="text-align: center;">
                                        <span class="badge badge--leido">
                                            📚 ${u.totalLibrosLeidos}
                                        </span>
                                    </td>

                                    <td>
                                        <span class="badge badge--level" style="color: #4cd137; border: 1px solid #4cd137;">
                                            ${u.tituloLector}
                                        </span>
                                    </td>

                                    <td>
                                        <c:if test="${usuarioLogueado != null && u.id != usuarioLogueado.id}">
                                            <c:choose>
                                                <c:when test="${idsAmigos.contains(u.id)}">
                                                    <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                        <i class="bi bi-person-check-fill"></i> Amigo
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="amigos" method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="agregar">
                                                        <input type="hidden" name="amigoId" value="${u.id}">
                                                        <button type="submit" class="btn btn--sm btn--brand" title="Agregar Amigo">👤+</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>

                            <%-- Mensaje si está vacío --%>
                            <c:if test="${empty ranking}">
                                <tr>
                                    <td colspan="5" style="text-align: center; padding: 2rem;">
                                        No hay datos disponibles.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </section>
</main>
</body>
</html>