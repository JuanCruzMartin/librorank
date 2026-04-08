<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Mi perfil — LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<main id="content" class="page">
    <section class="section">

        <div class="container">

            <c:if test="${not esMiPerfil}">
                <div style="margin-bottom: 1rem;">
                    <a href="perfil" class="btn btn--sm btn--ghost">← Volver a mi perfil</a>
                </div>
            </c:if>

            <c:if test="${not empty sessionScope.mensajeOk}">
                <div class="alert alert--success">
                    ${sessionScope.mensajeOk}
                </div>
                <c:remove var="mensajeOk" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.mensajeError}">
                <div class="alert alert--error">
                    ${sessionScope.mensajeError}
                </div>
                <c:remove var="mensajeError" scope="session"/>
            </c:if>

            <div class="perfil-layout">

                <aside class="perfil-side">
                    <div class="card perfil-user-card">
                        <div class="user-avatar">
                            <c:set var="avatar" value="${not empty usuarioMostrado.avatarUrl ? usuarioMostrado.avatarUrl : 'personaje_1'}" />
                            <c:choose>
                                <c:when test="${fn:startsWith(avatar, 'personaje_')}">
                                    <img src="assets/personajes/${avatar}.png" alt="Avatar" style="width: 80%; height: 80%; object-fit: contain;">
                                </c:when>
                                <c:otherwise>
                                    <img src="img/${avatar}/${avatar}_0.png" alt="Avatar" style="width: 80%; height: 80%; object-fit: contain;">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="user-meta">
                            <h1 class="user-name"><c:out value="${usuarioMostrado.nombre}"/></h1>
                            <p class="muted user-handle">@<c:out value="${usuarioMostrado.username}"/></p>
                            <span class="badge badge--level">${tituloNivel}</span>

                            <p class="coins-line" style="color: #ffd700; font-weight: bold; margin-top: 10px;">
                                🪙 <span>${usuarioMostrado.monedas}</span> monedas
                            </p>
                            <p style="color: #fff; margin-bottom: 5px;">
                                Nivel <strong>${nivel}</strong> · Total leídos:
                                <strong>${leidosTotal}</strong>
                            </p>
                            <c:if test="${not empty usuarioMostrado.bio}">
                                <p class="user-bio small" style="margin-top: 10px; font-style: italic; color: var(--text-muted);">
                                    "<c:out value="${usuarioMostrado.bio}"/>"
                                </p>
                            </c:if>
                        </div>
                    </div>

                    <div class="card perfil-stats-card">
                        <h2>Progreso de lectura</h2>
                        <p class="mb-2" style="color:var(--text-muted);">Objetivo anual: <strong style="color:#fff;">${objetivoAnual} libros</strong></p>

                        <div class="progress" style="background: rgba(255,255,255,0.05); height: 12px; border-radius: 50px; margin: 20px 0; border: 1px solid rgba(255,255,255,0.1);">
                            <div class="progress-bar" style="width: ${porcentaje}%; background: var(--accent-gold); box-shadow: 0 0 15px var(--accent-gold-glow); border-radius: 50px;"></div>
                        </div>

                        <c:choose>
                            <c:when test="${leidosEsteAnio == 0}">
                                <p class="text-muted small">¡Aún no ha empezado su lectura este año! 📚</p>
                            </c:when>
                            <c:otherwise>
                                <p class="small" style="color: #fff;">
                                    Lleva <strong>${leidosEsteAnio}</strong> libros este año · 
                                    <span class="text-muted">Faltan ${restantesObjetivo}</span>
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="card perfil-level-card">
                        <h2>Próximo nivel</h2>
                        <p class="text-muted small">
                            Estás a <strong style="color:#fff;">${librosParaSiguienteTitulo}</strong> libros de ser:
                        </p>
                        <div class="mt-2">
                            <span class="badge--level" style="background: var(--primary); color: #000; border: none; font-size: 0.85rem;">
                                ${proximoTitulo}
                            </span>
                        </div>
                    </div>
                </aside>

                <section class="perfil-main">
                    <%-- NAVEGACIÓN DE PESTAÑAS --%>
                    <div class="inventory-tabs" style="margin-bottom: 2rem;">
                        <button class="tab-btn active" id="btn-resumen" onclick="switchProfileTab('resumen', this)">✨ Mi Resumen</button>
                        <button class="tab-btn" id="btn-amigos" onclick="switchProfileTab('amigos', this)">👥 Amigos</button>
                        <button class="tab-btn" id="btn-config" onclick="switchProfileTab('config', this)">⚙️ Editar Cuenta</button>
                    </div>

                    <%-- CONTENIDO: AMIGOS --%>
                    <div id="tab-amigos" class="profile-tab-content" style="display: none;">
                        <article class="card perfil-section">
                            <h2>Buscar nuevos amigos</h2>
                            <form action="amigos" method="get" style="display: flex; gap: 10px; margin-bottom: 1.5rem;">
                                <input type="text" name="q" placeholder="Buscar por usuario o email..." style="flex-grow: 1;" required>
                                <button type="submit" class="btn btn--brand">Buscar</button>
                            </form>

                            <c:if test="${not empty resultadosBusqueda}">
                                <h3>Resultados de búsqueda</h3>
                                <div class="grid-lecturas">
                                    <c:forEach var="u" items="${resultadosBusqueda}">
                                        <c:set var="user" value="${u}" scope="request" />
                                        <c:set var="context" value="search" scope="request" />
                                        <jsp:include page="/includes/userCard.jsp" />
                                    </c:forEach>
                                </div>
                            </c:if>
                        </article>

                        <c:if test="${not empty sugerencias}">
                            <article class="card perfil-section" style="border: 1px solid #4cd13733; background: linear-gradient(180deg, #4cd13705, transparent);">
                                <h2>Personas que leen como vos</h2>
                                <p class="muted small" style="margin-bottom: 1rem;">Basado en tus lecturas recientes</p>
                                <div class="grid-lecturas">
                                    <c:forEach var="s" items="${sugerencias}">
                                        <c:set var="user" value="${s}" scope="request" />
                                        <c:set var="context" value="sugerencia" scope="request" />
                                        <jsp:include page="/includes/userCard.jsp" />
                                    </c:forEach>
                                </div>
                            </article>
                        </c:if>

                        <article class="card perfil-section">
                            <h2>Mis amigos</h2>
                            <c:choose>
                                <c:when test="${empty amigos}">
                                    <p class="muted text-center">Aún no tienes amigos agregados.</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="grid-lecturas">
                                        <c:forEach var="a" items="${amigos}">
                                            <c:set var="user" value="${a}" scope="request" />
                                            <c:set var="context" value="friends" scope="request" />
                                            <jsp:include page="/includes/userCard.jsp" />
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </article>
                    </div>

                    <%-- CONTENIDO: RESUMEN (Visible por defecto) --%>
                    <div id="tab-resumen" class="profile-tab-content active">
                        <article class="card perfil-section">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                                <h2 style="margin:0; border:none; padding:0;">Últimas conquistas</h2>
                                <a href="biblioteca" class="text-gold fw-bold text-decoration-none small">Ver biblioteca completa →</a>
                            </div>

                            <c:choose>
                                <c:when test="${empty ultimasLecturas}">
                                    <div class="text-center py-5" style="background: rgba(255,255,255,0.02); border-radius: 12px; border: 1px dashed rgba(255,255,255,0.1);">
                                        <p class="text-muted m-0">Aún no has terminado ningún libro.</p>
                                        <p class="small text-muted">¡Marcá uno como LEÍDO para verlo acá!</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="grid-lecturas">
                                        <c:forEach var="l" items="${ultimasLecturas}">
                                            <div class="card-lectura-mini">
                                                <div style="width: 45px; height: 65px; background: linear-gradient(135deg, var(--bg-input-focus), #111); border-radius: 6px; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 10px rgba(0,0,0,0.3); border: 1px solid rgba(212, 175, 55, 0.2); flex-shrink: 0;">
                                                    <span style="font-size: 1.4rem;">📕</span>
                                                </div>
                                                <div style="flex-grow: 1; min-width: 0;">
                                                    <h4 class="text-truncate">${l.titulo}</h4>
                                                    <p class="text-muted small text-truncate">${l.autor}</p>
                                                </div>
                                                <span class="badge-cozy badge--leido" style="white-space: nowrap;">✓ Leído</span>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </article>

                        <article class="card perfil-section">
                            <h2 style="margin-bottom: 0.5rem; border:none; padding:0;">Muro de Trofeos</h2>
                            <p class="text-muted small mb-4">Tu progreso legendario en LibroRank.</p>
                            <div class="trophies-grid">
                                <c:forEach var="logro" items="${listaLogros}">
                                    <div class="trophy-item ${logro.desbloqueado ? 'desbloqueado' : ''}" title="${logro.descripcion}">
                                        <span class="trophy-icon">${logro.icono}</span>
                                        <p class="trophy-title">${logro.desbloqueado ? logro.titulo : '???'}</p>
                                    </div>
                                </c:forEach>
                            </div>
                        </article>
                    </div>

                    <%-- CONTENIDO: CONFIGURACIÓN (Oculto por defecto) --%>
                    <div id="tab-config" class="profile-tab-content" style="display: none;">
                        <form class="perfil-form" action="perfil" method="post">
                            <article class="card perfil-section">
                                <h2 style="border:none; padding:0; margin-bottom: 1.5rem;">Datos de la cuenta</h2>
                                <div class="form-grid">
                                    <div class="field">
                                        <label for="nombre">Nombre completo</label>
                                        <input type="text" id="nombre" name="nombre" value="${usuarioLogueado.nombre}" placeholder="Tu nombre...">
                                    </div>
                                    <div class="field">
                                        <label for="usuario">Nombre de usuario</label>
                                        <input type="text" id="usuario" name="usuario" value="${usuarioLogueado.username}" placeholder="usuario123">
                                    </div>
                                    <div class="field field--full">
                                        <label for="email">Dirección de email</label>
                                        <input type="email" id="email" name="email" value="${usuarioLogueado.email}" placeholder="correo@ejemplo.com">
                                    </div>
                                    <div class="field field--full">
                                        <label for="bio">Biografía / Frase favorita</label>
                                        <textarea id="bio" name="bio" rows="3" placeholder="Escribe algo sobre vos...">${usuarioLogueado.bio}</textarea>
                                        <p class="text-muted" style="font-size: 0.7rem; margin-top: 4px;">Este texto aparecerá en tu perfil público.</p>
                                    </div>
                                </div>
                            </article>

                            <article class="card perfil-section">
                                <h2 style="border:none; padding:0; margin-bottom: 1.5rem;">Metas Literarias</h2>
                                <div class="form-grid">
                                    <div class="field field--full">
                                        <label>Tus géneros preferidos</label>
                                        <p class="text-muted small mb-2">Seleccioná los temas que más te apasionan:</p>
                                        <input type="hidden" id="generosInput" name="generos" value="${usuarioLogueado.generosFavoritos}">
                                        <div class="chips prefs-chips" id="chipsGeneros">
                                            <button class="chip" type="button" data-genre="Filosofía">Filosofía</button>
                                            <button class="chip" type="button" data-genre="Ficción">Ficción</button>
                                            <button class="chip" type="button" data-genre="Tecnología">Tecnología</button>
                                            <button class="chip" type="button" data-genre="Negocios">Negocios</button>
                                            <button class="chip" type="button" data-genre="Ciencia">Ciencia</button>
                                            <button class="chip" type="button" data-genre="Historia">Historia</button>
                                            <button class="chip" type="button" data-genre="Arte">Arte</button>
                                        </div>
                                    </div>
                                    <div class="field field--full">
                                        <label for="objetivo">Objetivo de lectura anual</label>
                                        <select id="objetivo" name="objetivo" style="width: 100%; cursor: pointer;">
                                            <c:forEach var="obj" items="10,20,30,40,50,100">
                                                <option value="${obj}" ${objetivoAnual == obj ? 'selected' : ''}>${obj} libros al año</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </article>
                            
                            <div class="d-flex justify-content-end mt-3">
                                <button class="btn--brand px-5 py-3" type="submit">
                                    💾 Guardar todos los cambios
                                </button>
                            </div>
                        </form>
                    </div>
                </section>

            </div> </div> </section>
</main>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const chips = document.querySelectorAll('.chip');
        const inputHidden = document.getElementById('generosInput');
        let seleccionados = inputHidden.value ? inputHidden.value.split(',') : [];

        function actualizarVisuales() {
            chips.forEach(chip => {
                if (seleccionados.includes(chip.dataset.genre)) {
                    chip.classList.add('active');
                    chip.style.backgroundColor = "#4cd137";
                    chip.style.color = "#000";
                    chip.style.fontWeight = "bold";
                } else {
                    chip.classList.remove('active');
                    chip.style.backgroundColor = "";
                    chip.style.color = "";
                    chip.style.fontWeight = "normal";
                }
            });
        }

        chips.forEach(chip => {
            chip.addEventListener('click', function() {
                const genero = this.dataset.genre;
                if (seleccionados.includes(genero)) {
                    seleccionados = seleccionados.filter(g => g !== genero);
                } else {
                    seleccionados.push(genero);
                }
                inputHidden.value = seleccionados.join(',');
                actualizarVisuales();
            });
        });
        actualizarVisuales();

        // Manejar cambio de pestaña automático por URL o por parámetro de request (forward)
        const urlParams = new URLSearchParams(window.location.search);
        let tab = urlParams.get('tab');
        
        // Si no está en el query string de la URL (porque fue un forward), 
        // revisamos si el servidor nos inyectó el parámetro en la página.
        if (!tab) {
            tab = "${param.tab}";
        }

        if (tab === 'amigos') {
            switchProfileTab('amigos', document.getElementById('btn-amigos'));
        } else if (tab === 'config') {
            switchProfileTab('config', document.getElementById('btn-config'));
        }
    });

    function switchProfileTab(tabId, btn) {
        // Ocultar todos los contenidos de pestañas
        document.querySelectorAll('.profile-tab-content').forEach(c => {
            c.style.display = 'none';
            c.classList.remove('active');
        });
        
        // Quitar clase active de todos los botones
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        
        // Mostrar el contenido seleccionado
        const target = document.getElementById('tab-' + tabId);
        if (target) {
            target.style.display = 'block';
            target.classList.add('active');
        }
        
        // Activar el botón clicado
        if (btn) btn.classList.add('active');
    }

    function toggleAfinidad(id) {
        const div = document.getElementById(id);
        if (div) {
            if (div.style.display === 'none') {
                div.style.display = 'block';
            } else {
                div.style.display = 'none';
            }
        }
    }
</script>
<jsp:include page="/includes/footer.jsp" />
</body>
</html>