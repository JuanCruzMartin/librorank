<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Comunidad — LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<header class="community-header py-5" style="background: linear-gradient(135deg, #1a1a1a 0%, #0a0a0a 100%); border-bottom: 1px solid rgba(212, 175, 55, 0.2);">
    <div class="container text-center">
        <h1 class="display-5 fw-bold text-white mb-3">Comunidad de Lectores</h1>
        <p class="lead text-muted mx-auto" style="max-width: 600px;">Conecta con otros lectores, descubre afinidades y expande tu círculo literario.</p>
        
        <div class="search-container mt-4 mx-auto" style="max-width: 500px;">
            <form action="amigos" method="get" class="input-group input-group-lg shadow-lg">
                <input type="text" name="q" class="form-control bg-dark text-white border-gold" 
                       placeholder="Buscar lectores por nombre o @usuario..." 
                       value="${param.q}" required
                       style="border-right: none;">
                <button class="btn btn-gold px-4" type="submit">
                    <i class="bi bi-search"></i>
                </button>
            </form>
        </div>
    </div>
</header>

<main class="container py-5">
    
    <c:if test="${not empty param.q}">
        <section class="mb-5 animate-fadeIn">
            <div class="d-flex align-items-center mb-4">
                <div class="bg-gold p-2 rounded-3 me-3"><i class="bi bi-person-search text-dark fs-4"></i></div>
                <h2 class="h3 mb-0 text-white">Resultados de búsqueda</h2>
            </div>
            
            <c:choose>
                <c:when test="${empty resultadosBusqueda}">
                    <div class="alert bg-dark border-secondary text-center py-5 rounded-4">
                        <i class="bi bi-emoji-frown fs-1 text-muted d-block mb-3"></i>
                        <p class="text-muted mb-0">No encontramos ningún lector con ese nombre o usuario.</p>
                        <a href="amigos" class="btn btn-link text-gold text-decoration-none mt-2">Ver mis amigos</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach var="u" items="${resultadosBusqueda}">
                            <div class="col">
                                <c:set var="user" value="${u}" scope="request" />
                                <c:set var="context" value="search" scope="request" />
                                <jsp:include page="/includes/userCard.jsp" />
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </c:if>

    <div class="row g-5">
        <!-- Columna Izquierda: Mis Amigos -->
        <div class="col-lg-8">
            <div class="d-flex align-items-center justify-content-between mb-4">
                <div class="d-flex align-items-center">
                    <div class="bg-gold p-2 rounded-3 me-3"><i class="bi bi-people-fill text-dark fs-4"></i></div>
                    <h2 class="h3 mb-0 text-white">Mis Amigos <span class="badge bg-gold text-dark fs-6 ms-2">${fn:length(amigos)}</span></h2>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty amigos}">
                    <div class="text-center py-5 rounded-4" style="background: rgba(255,255,255,0.02); border: 1px dashed rgba(255,255,255,0.1);">
                        <i class="bi bi-person-plus fs-1 text-muted d-block mb-3"></i>
                        <p class="text-muted">Aún no tienes amigos en tu red literaria.</p>
                        <p class="small text-muted mb-0">¡Usa el buscador para conectar con la gente!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-md-2 g-4">
                        <c:forEach var="a" items="${amigos}">
                            <div class="col">
                                <c:set var="user" value="${a}" scope="request" />
                                <c:set var="context" value="friends" scope="request" />
                                <jsp:include page="/includes/userCard.jsp" />
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Columna Derecha: Sugerencias / Actividad -->
        <div class="col-lg-4">
            <div class="sticky-top" style="top: 100px;">
                <div class="card bg-dark border-secondary rounded-4 overflow-hidden mb-4">
                    <div class="card-header border-secondary bg-transparent py-3">
                        <h3 class="h5 mb-0 text-gold"><i class="bi bi-stars me-2"></i>Lectores Afines</h3>
                    </div>
                    <div class="card-body p-3">
                        <c:choose>
                            <c:when test="${empty sugerencias}">
                                <p class="small text-muted text-center py-3">Sigue leyendo para que podamos recomendarte lectores afines.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="s" items="${sugerencias}">
                                    <c:set var="user" value="${s}" scope="request" />
                                    <c:set var="context" value="sugerencia" scope="request" />
                                    <jsp:include page="/includes/userCard.jsp" />
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-footer border-secondary bg-transparent text-center py-3">
                        <a href="ranking" class="btn btn-sm btn-link text-gold text-decoration-none">Ver ranking global →</a>
                    </div>
                </div>
                
                <div class="card bg-gold border-0 rounded-4 p-4 text-dark shadow-gold">
                    <h4 class="fw-bold h5 mb-3">¿Sabías que...?</h4>
                    <p class="small mb-0 opacity-75">Tener amigos en LibroRank te permite ver qué están leyendo otros y descubrir nuevos libros a través de sus diarios y citas.</p>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/includes/footer.jsp" />

<style>
    .community-header { background-size: cover; background-position: center; }
    .border-gold { border-color: rgba(212, 175, 55, 0.4) !important; }
    .border-gold:focus { border-color: var(--accent-gold) !important; box-shadow: 0 0 15px rgba(212, 175, 55, 0.2); }
    .shadow-gold { box-shadow: 0 10px 30px rgba(212, 175, 55, 0.15); }
    .animate-fadeIn { animation: fadeIn 0.5s ease; }
</style>

</body>
</html>