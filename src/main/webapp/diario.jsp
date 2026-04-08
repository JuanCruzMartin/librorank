<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Diario de: ${libro.titulo} - LibroRank</title>
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .timeline {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
        }
        .entry {
            background: #fff;
            border-left: 4px solid #3498db;
            margin-bottom: 20px;
            padding: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .entry-header {
            display: flex;
            justify-content: space-between;
            color: #7f8c8d;
            font-size: 0.9em;
            margin-bottom: 10px;
        }
        .capitulo {
            font-weight: bold;
            color: #2980b9;
        }
        .form-diario {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container">
        <h1>Diario de Lectura: ${libro.titulo}</h1>
        <p><em>Por: ${libro.autor}</em></p>

        <div class="row">
            <div class="col-lg-7">
                <div class="form-diario">
                    <h3>Nueva Entrada en el Diario</h3>
                    <form action="diario" method="post">
                        <input type="hidden" name="idLibro" value="${libro.id}">
                        <div class="form-group">
                            <label>Capítulo / Progreso:</label>
                            <input type="text" name="capitulo" placeholder="Ej: Capítulo 5 o Página 120" required>
                        </div>
                        <div class="form-group">
                            <label>¿Qué pasó? / ¿Qué sentiste?</label>
                            <textarea name="comentario" rows="4" placeholder="Escribe tus pensamientos..." required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Guardar Progreso</button>
                    </form>
                </div>
            </div>
            <div class="col-lg-5">
                <div class="form-diario" style="border-left: 4px solid var(--accent-gold);">
                    <h3>Guardar Cita Favorita</h3>
                    <form action="diario" method="post">
                        <input type="hidden" name="accion" value="cita">
                        <input type="hidden" name="idLibro" value="${libro.id}">
                        <div class="form-group">
                            <label>Texto de la Cita:</label>
                            <textarea name="texto" rows="3" placeholder="Esa frase que te encantó..." required></textarea>
                        </div>
                        <div class="form-group">
                            <label>Página:</label>
                            <input type="text" name="pagina" placeholder="Pág. 42">
                        </div>
                        <button type="submit" class="btn btn-gold">Guardar Cita</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="row mt-5">
            <div class="col-md-6">
                <div class="timeline">
                    <h3>Tu Timeline de Lectura</h3>
                    <c:if test="${empty entradas}">
                        <p class="text-muted">Aún no hay notas.</p>
                    </c:if>
                    <c:forEach var="e" items="${entradas}">
                        <div class="entry">
                            <div class="entry-header">
                                <span class="capitulo">${e.capitulo}</span>
                                <span><fmt:parseDate value="${e.fechaCreacion}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                <fmt:formatDate value="${parsedDate}" pattern="dd MMM yyyy" /></span>
                            </div>
                            <div class="entry-content">${e.comentario}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-md-6">
                <div class="timeline">
                    <h3>Citas Destacadas</h3>
                    <c:if test="${empty citas}">
                        <p class="text-muted">No has guardado citas aún.</p>
                    </c:if>
                    <c:forEach var="c" items="${citas}">
                        <div class="entry" style="border-left-color: var(--accent-gold);">
                            <div class="entry-content" style="font-style: italic;">"${c.texto}"</div>
                            <div class="text-end small text-gold mt-2">— Pág. ${c.pagina}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
