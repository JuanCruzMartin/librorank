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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.13/cropper.min.css">
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .cropper-view-box, .cropper-face { border-radius: 50%; }
        .modal-cropper-content { background: #1a1a1a; border: 1px solid var(--accent-gold); color: white; }
    </style>
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

            <div class="perfil-layout">

                <aside class="perfil-side">
                    <div class="card perfil-user-card">
                        <div class="user-avatar" style="position: relative; overflow: visible;">
                            <c:set var="avatar" value="${not empty usuarioMostrado.avatarUrl ? usuarioMostrado.avatarUrl : 'personaje_1'}" />
                            <c:choose>
                                <c:when test="${fn:contains(avatar, '/')}">
                                    <img src="${avatar}" alt="Foto de Perfil" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                                </c:when>
                                <c:when test="${fn:startsWith(avatar, 'personaje_')}">
                                    <img src="assets/personajes/${avatar}.png" alt="Avatar" style="width: 80%; height: 80%; object-fit: contain;">
                                </c:when>
                                <c:otherwise>
                                    <img src="img/${avatar}/${avatar}_0.png" alt="Avatar" style="width: 80%; height: 80%; object-fit: contain;">
                                </c:otherwise>
                            </c:choose>
                            
                            <c:if test="${usuarioLogueado.id == usuarioMostrado.id}">
                                <div id="loadingFoto" style="display: none; position: absolute; inset: 0; background: rgba(0,0,0,0.6); border-radius: 50%; z-index: 20; align-items: center; justify-content: center;">
                                    <div class="spinner-border text-gold" role="status" style="width: 2rem; height: 2rem;"></div>
                                </div>
                                <form action="subirFoto" method="post" enctype="multipart/form-data" id="formFoto" style="position: absolute; bottom: 5px; right: 5px; z-index: 21;">
                                    <label for="inputFoto" class="btn-gold" style="width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; padding: 0; box-shadow: 0 4px 15px rgba(0,0,0,0.6); background: #1a1a1a; border: 2px solid var(--accent-gold); color: var(--accent-gold); transition: all 0.3s ease;">
                                        <i class="bi bi-camera-fill" style="font-size: 1.2rem;"></i>
                                    </label>
                                    <input type="file" name="fotoPerfil" id="inputFoto" accept="image/*" style="display: none;" onchange="enviarFormularioFoto()">
                                </form>
                                <style>
                                    label[for="inputFoto"]:hover {
                                        background: var(--accent-gold) !important;
                                        color: #000 !important;
                                        transform: scale(1.1);
                                    }
                                </style>
                            </c:if>
                        </div>
                        <div class="user-meta">
                            <h1 class="user-name"><c:out value="${usuarioMostrado.nombre}"/></h1>
                            <p class="muted user-handle">@<c:out value="${usuarioMostrado.username}"/></p>
                            <span class="badge badge--level">${tituloNivel}</span>

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

                        <p class="small" style="color: #fff;">
                            Lleva <strong>${leidosEsteAnio}</strong> libros este año · 
                            <span class="text-muted">Faltan ${restantesObjetivo}</span>
                        </p>
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
                    <div class="inventory-tabs" style="margin-bottom: 2rem;">
                        <button class="tab-btn active" id="btn-resumen" onclick="switchProfileTab('resumen', this)">✨ Mi Resumen</button>
                        <button class="tab-btn" id="btn-amigos" onclick="switchProfileTab('amigos', this)">👥 Amigos</button>
                        <button class="tab-btn" id="btn-config" onclick="switchProfileTab('config', this)">⚙️ Editar Cuenta</button>
                    </div>

                    <div id="tab-amigos" class="profile-tab-content" style="display: none;">
                        <article class="card perfil-section">
                            <h2>Buscar nuevos amigos</h2>
                            <form action="amigos" method="get" style="display: flex; gap: 10px; margin-bottom: 1.5rem;">
                                <input type="text" name="q" placeholder="Buscar por usuario o email..." style="flex-grow: 1;" required>
                                <button type="submit" class="btn btn--brand">Buscar</button>
                            </form>
                            <c:if test="${not empty resultadosBusqueda}">
                                <div class="grid-lecturas">
                                    <c:forEach var="u" items="${resultadosBusqueda}">
                                        <c:set var="user" value="${u}" scope="request" />
                                        <jsp:include page="/includes/userCard.jsp" />
                                    </c:forEach>
                                </div>
                            </c:if>
                        </article>

                        <article class="card perfil-section">
                            <h2>Mis amigos</h2>
                            <div class="grid-lecturas">
                                <c:forEach var="a" items="${amigos}">
                                    <c:set var="user" value="${a}" scope="request" />
                                    <jsp:include page="/includes/userCard.jsp" />
                                </c:forEach>
                            </div>
                        </article>
                    </div>

                    <div id="tab-resumen" class="profile-tab-content active">
                        <article class="card perfil-section">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                                <h2 style="margin:0; border:none; padding:0;">Últimas conquistas</h2>
                                <a href="biblioteca" class="text-gold fw-bold text-decoration-none small">Ver biblioteca completa →</a>
                            </div>
                            <div class="grid-lecturas">
                                <c:forEach var="l" items="${ultimasLecturas}">
                                    <div class="card-lectura-mini">
                                        <div style="width: 45px; height: 65px; background: #222; border-radius: 6px; display: flex; align-items: center; justify-content: center; border: 1px solid rgba(212, 175, 55, 0.2);">
                                            <span>📕</span>
                                        </div>
                                        <div style="flex-grow: 1; min-width: 0;">
                                            <h4 class="text-truncate">${l.titulo}</h4>
                                            <p class="text-muted small text-truncate">${l.autor}</p>
                                        </div>
                                        <span class="badge-cozy badge--leido">✓ Leído</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </article>

                        <article class="card perfil-section">
                            <h2>Muro de Trofeos</h2>
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

                    <div id="tab-config" class="profile-tab-content" style="display: none;">
                        <form class="perfil-form" action="perfil" method="post">
                            <article class="card perfil-section">
                                <h2>Datos de la cuenta</h2>
                                <div class="form-grid">
                                    <div class="field">
                                        <label for="nombre">Nombre completo</label>
                                        <input type="text" id="nombre" name="nombre" value="${usuarioLogueado.nombre}">
                                    </div>
                                    <div class="field">
                                        <label for="usuario">Nombre de usuario</label>
                                        <input type="text" id="usuario" name="usuario" value="${usuarioLogueado.username}">
                                    </div>
                                    <div class="field field--full">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" name="email" value="${usuarioLogueado.email}">
                                    </div>
                                    <div class="field field--full">
                                        <label for="bio">Biografía</label>
                                        <textarea id="bio" name="bio" rows="3">${usuarioLogueado.bio}</textarea>
                                    </div>
                                </div>
                                <div class="mt-3">
                                    <button class="btn--brand px-5 py-3" type="submit">Guardar cambios</button>
                                </div>
                            </article>
                        </form>
                    </div>
                </section>
            </div>
        </div>
    </section>
</main>

<!-- Modal de Recorte -->
<div class="modal fade" id="modalCropper" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content modal-cropper-content">
            <div class="modal-header border-0">
                <h5 class="modal-title font-title text-gold">Ajustar Foto</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" onclick="cancelarRecorte()"></button>
            </div>
            <div class="modal-body p-0" style="background: #000; min-height: 400px; display: flex; align-items: center; justify-content: center;">
                <img id="imageToCrop" src="" style="max-width: 100%; display: block;">
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal" onclick="cancelarRecorte()">Cancelar</button>
                <button type="button" class="btn btn-gold" onclick="guardarRecorte()">Guardar</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.13/cropper.min.js"></script>
<script>
    let cropper;
    let bSModalCropper;

    function enviarFormularioFoto() {
        const input = document.getElementById('inputFoto');
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const image = document.getElementById('imageToCrop');
                image.src = e.target.result;
                if (!bSModalCropper) bSModalCropper = new bootstrap.Modal(document.getElementById('modalCropper'));
                bSModalCropper.show();
                
                document.getElementById('modalCropper').addEventListener('shown.bs.modal', function init() {
                    if (cropper) cropper.destroy();
                    cropper = new Cropper(image, { aspectRatio: 1, viewMode: 1, dragMode: 'move', autoCropArea: 1 });
                    document.getElementById('modalCropper').removeEventListener('shown.bs.modal', init);
                });
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function guardarRecorte() {
        if (!cropper) return;
        document.getElementById('loadingFoto').style.display = 'flex';
        bSModalCropper.hide();
        cropper.getCroppedCanvas({ width: 400, height: 400 }).toBlob((blob) => {
            const formData = new FormData();
            formData.append('fotoPerfil', blob, 'perfil.jpg');
            fetch('subirFoto', { method: 'POST', body: formData })
            .then(r => r.redirected ? window.location.href = r.url : window.location.reload())
            .catch(() => alert('Error al subir'));
        }, 'image/jpeg');
    }

    function cancelarRecorte() {
        if (cropper) cropper.destroy();
        document.getElementById('inputFoto').value = '';
    }

    function switchProfileTab(tabId, btn) {
        document.querySelectorAll('.profile-tab-content').forEach(c => c.style.display = 'none');
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('tab-' + tabId).style.display = 'block';
        if (btn) btn.classList.add('active');
    }
</script>

<jsp:include page="/includes/footer.jsp" />
</body>
</html>
