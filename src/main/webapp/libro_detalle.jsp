<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>${libroGlobal.titulo} - Comunidad LibroRank</title>
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container py-5">
        <div class="row g-5">
            <div class="col-md-4">
                <div class="book-cover-large shadow-lg">
                    <img src="${libroGlobal.portadaUrl}" alt="Portada" style="width: 100%; border-radius: 12px;">
                </div>
            </div>
            <div class="col-md-8">
                <h1 class="display-4 font-title text-white">${libroGlobal.titulo}</h1>
                <h2 class="h3 text-gold mb-4">${libroGlobal.autor}</h2>
                
                <div class="d-flex gap-4 mb-5">
                    <div class="stat-box">
                        <div class="h2 text-warning">${libroGlobal.notaMedia > 0 ? libroGlobal.notaMedia : '---'} ⭐</div>
                        <div class="small text-muted text-uppercase">Nota Media</div>
                    </div>
                    <div class="stat-box">
                        <div class="h2 text-white">${libroGlobal.totalLectores}</div>
                        <div class="small text-muted text-uppercase">Lectores en LibroRank</div>
                    </div>
                </div>

                <hr class="opacity-10 my-5">

                <h3 class="font-title h4 mb-4">Opiniones de la Comunidad</h3>
                <c:if test="${empty reviews}">
                    <p class="text-muted">Nadie ha escrito una reseña aún. ¡Sé el primero!</p>
                </c:if>
                <c:forEach var="r" items="${reviews}">
                    <div class="card p-3 mb-3" style="background: rgba(255,255,255,0.02);">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <strong class="text-gold">@${r.autor}</strong> <!-- Usamos autor para el username -->
                            <div class="text-warning">
                                <c:forEach begin="1" end="5" var="i">
                                    <i class="bi bi-star${r.estrellas >= i ? '-fill' : ''}"></i>
                                </c:forEach>
                            </div>
                        </div>
                        <p class="mb-0 text-white opacity-75">"${r.resena}"</p>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
