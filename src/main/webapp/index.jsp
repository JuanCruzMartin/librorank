<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>LibroRank — Gamifica tu hábito de lectura</title>
    <link rel="stylesheet" href="Css/styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="landing-page">

<header class="site-header">
    <div class="container d-flex justify-content-between align-items-center">
        <a href="${pageContext.request.contextPath}/" class="logo text-decoration-none">LIBRO<span>RANK</span></a>
        <nav>
            <ul class="d-flex list-unstyled gap-4 mb-0 align-items-center">
                <li><a href="ranking" class="nav-link-custom">Ranking</a></li>
                <c:choose>
                    <c:when test="${not empty sessionScope.usuarioLogueado}">
                        <li><a href="biblioteca" class="nav-link-custom">Mi Biblioteca</a></li>
                        <li><a href="perfil" class="btn-gold btn-sm px-3 ms-2">@<c:out value="${sessionScope.usuarioLogueado.username}"/></a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="login" class="nav-link-custom">Ingresar</a></li>
                        <li><a href="signup" class="btn-gold btn-sm px-4 ms-2">Empezar ahora</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>

<main>
    <c:choose>
        <c:when test="${not empty sessionScope.usuarioLogueado}">
            <!-- DASHBOARD DE USUARIO CON FEED SOCIAL -->
            <div class="container py-5">
                <div class="row g-4">
                    <!-- Columna Izquierda: Perfil Rápido -->
                    <div class="col-lg-4">
                        <div class="card p-4 text-center">
                            <div class="user-avatar mb-3" style="width: 100px; height: 100px; margin: 0 auto;">
                                <img src="img/personajes/personaje_1.png" alt="Avatar" style="width: 100%; height: 100%; object-fit: cover;">
                            </div>
                            <h2 class="h4 mb-1">Hola, ${usuarioLogueado.nombre}!</h2>
                            <p class="text-muted small mb-3">@${usuarioLogueado.username}</p>
                            
                            <div class="d-flex justify-content-center gap-3 mt-2">
                                <div class="text-center">
                                    <div class="fw-bold text-gold">🔥 ${usuarioLogueado.rachaActual}</div>
                                    <div class="small text-muted">Racha</div>
                                </div>
                            </div>

                            <hr class="my-4 opacity-10">
                            
                            <c:if test="${not empty citaDelDia}">
                                <div class="quote-of-the-day p-3 rounded text-start" style="background: rgba(212, 175, 55, 0.05); border: 1px dashed var(--accent-gold);">
                                    <i class="bi bi-quote fs-4 text-gold opacity-50"></i>
                                    <p class="mb-2 text-white italic" style="font-style: italic; font-size: 0.9rem;">"${citaDelDia.texto}"</p>
                                    <div class="text-gold" style="font-size: 0.75rem; font-weight: bold;">— ${citaDelDia.tituloLibro}</div>
                                </div>
                                <hr class="my-4 opacity-10">
                            </c:if>

                            <a href="biblioteca" class="btn btn-gold w-100 mb-2">Mi Biblioteca</a>
                        </div>
                    </div>

                    <!-- Columna Derecha: El Feed Social -->
                    <div class="col-lg-8">
                        <h2 class="font-title h3 mb-4">Muro de Actividad</h2>
                        <div class="feed-container">
                            <c:if test="${empty feed}">
                                <div class="card p-5 text-center text-muted">
                                    <i class="bi bi-people display-4 mb-3"></i>
                                    <p>Aún no hay actividad de tus amigos. ¡Sigue a más personas para llenar tu muro!</p>
                                    <a href="amigos" class="btn btn-gold btn-sm mt-2">Buscar Amigos</a>
                                </div>
                            </c:if>
                            <c:forEach var="act" items="${feed}">
                                <div class="feed-item card mb-3 p-3">
                                    <div class="d-flex gap-3">
                                        <div class="feed-user-img">
                                            <img src="img/personajes/personaje_2.png" alt="User" class="rounded-circle" style="width: 45px; height: 45px;">
                                        </div>
                                        <div class="flex-grow-1">
                                            <div class="feed-header d-flex justify-content-between">
                                                <span class="fw-bold text-white">@${act.username}</span>
                                                <span class="text-muted small">Activo</span>
                                            </div>
                                            <div class="feed-content my-2 text-white opacity-75">
                                                <c:choose>
                                                    <c:when test="${act.tipoActividad == 'NUEVO_LIBRO'}">empezó a leer <strong class="text-gold">"${act.tituloLibro}"</strong></c:when>
                                                    <c:when test="${act.tipoActividad == 'NUEVO_RETO'}">ha lanzado un <strong class="text-gold">Nuevo Reto Colectivo</strong>: "${act.detalle}"</c:when>
                                                    <c:when test="${act.tipoActividad == 'CAMBIO_ESTADO'}">marcó <strong class="text-gold">"${act.tituloLibro}"</strong> como <span class="badge-cozy">${act.detalle}</span></c:when>
                                                    <c:when test="${act.tipoActividad == 'NUEVA_CALIFICACION'}">puntuó <strong class="text-gold">"${act.tituloLibro}"</strong> con <span class="text-warning">${act.detalle} <i class="bi bi-star-fill"></i></span></c:when>
                                                    <c:when test="${act.tipoActividad == 'DIARIO_LOG'}">actualizó su progreso: <em>${act.detalle}</em></c:when>
                                                </c:choose>
                                            </div>
                                            <div class="feed-actions mt-2 pt-2 border-top border-secondary opacity-50 d-flex gap-4">
                                                <a href="home?like=${act.id}" class="text-decoration-none ${act.leGustaAlUsuario ? 'text-primary' : 'text-muted'} small">
                                                    <i class="bi bi-hand-thumbs-up${act.leGustaAlUsuario ? '-fill' : ''}"></i> ${act.totalLikes} Me gusta
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <!-- HERO SECTION: ATMOSPHERIC SPLIT -->
    <section class="hero-section">
        <div class="container">
            <div class="hero-content">
                <h1>Tu hábito de lectura <span>ahora es un juego.</span></h1>
                <p>La plataforma definitiva para lectores. Registra tus libros, compite con amigos y alcanza la cima del ranking literario.</p>
                <div class="hero-btns">
                    <c:choose>
                        <c:when test="${empty sessionScope.usuarioLogueado}">
                            <a href="signup" class="btn-main text-decoration-none">Crea tu cuenta gratis</a>
                            <a href="#mas-info" class="btn-secondary text-decoration-none">Explorar más</a>
                        </c:when>
                        <c:otherwise>
                            <a href="biblioteca" class="btn-main text-decoration-none">Ir a mi Biblioteca</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </section>

    <div class="container" id="mas-info">
        <!-- FEATURES: PREMIUM CARDS -->
        <div class="features-grid mb-5 pb-5">
            <div class="feature-card">
                <span class="feature-icon">📜</span>
                <h3>Crónica Personal</h3>
                <p>Lleva un registro detallado de cada mundo que visitas a través de las páginas.</p>
            </div>
            <div class="feature-card">
                <span class="feature-icon">🏆</span>
                <h3>Logros Legendarios</h3>
                <p>Desbloquea trofeos únicos a medida que devoras capítulos y completas desafíos.</p>
            </div>
            <div class="feature-card">
                <span class="feature-icon">🏛️</span>
                <h3>Comunidad</h3>
                <p>Mídete con los mejores lectores y escala posiciones en el ranking legendario.</p>
            </div>
        </div>

        <!-- RANKING PREVIEW -->
        <section class="card border-0 bg-transparent mb-5 pb-5">
            <div class="d-flex justify-content-between align-items-end mb-5">
                <div>
                    <h2 class="display-5 fw-bold text-white m-0 border-0">Salón de la Fama</h2>
                    <p class="text-muted m-0 mt-2">Los lectores más dedicados de la última semana.</p>
                </div>
                <a href="ranking" class="text-gold fw-bold text-decoration-none pb-2">Ver todo el ranking →</a>
            </div>

            <div class="table-responsive">
                <table class="table-landing w-100">
                    <thead>
                        <tr>
                            <th>Posición</th>
                            <th>Lector</th>
                            <th>Libros</th>
                            <th>Nivel</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty top3}">
                                <tr><td colspan="4" class="text-center text-muted py-5">Aún no hay datos de ranking.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${top3}" varStatus="status">
                                    <tr class="${not empty sessionScope.usuarioLogueado and sessionScope.usuarioLogueado.id == u.id ? 'highlight' : ''}">
                                        <td>
                                            <span class="rank-number ${status.index == 0 ? 'top-1' : ''}">
                                                #${status.count}
                                            </span>
                                        </td>
                                        <td class="fw-bold fs-5 text-white">
                                            <c:out value="${u.username}"/>
                                            <c:if test="${not empty sessionScope.usuarioLogueado and sessionScope.usuarioLogueado.id == u.id}">
                                                <span class='text-gold'>(Vos)</span>
                                            </c:if>
                                        </td>
                                        <td class="text-white fw-bold fs-5"><c:out value="${u.totalLibrosLeidos}"/> <small class="text-muted fw-normal" style="font-size: 0.9rem;">libros</small></td>
                                        <td><span class="badge-cozy" style="letter-spacing:1px; background: rgba(0,0,0,0.3); border: 1px solid rgba(212, 175, 55, 0.2);"><c:out value="${u.tituloLector}"/></span></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
        </c:otherwise>
    </c:choose>
</main>

<footer class="py-5 mt-5 border-top border-secondary">
    <div class="container text-center">
        <div class="logo mb-4 fs-3">LIBRO<span>RANK</span></div>
        <div class="d-flex justify-content-center gap-4 mb-4">
            <a href="#" class="text-muted small text-decoration-none">Términos</a>
            <a href="#" class="text-muted small text-decoration-none">Privacidad</a>
            <a href="#" class="text-muted small text-decoration-none">Contacto</a>
        </div>
        <p class="text-muted small">&copy; 2026 LibroRank. La experiencia de lectura definitiva.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
