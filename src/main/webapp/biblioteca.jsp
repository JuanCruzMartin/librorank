<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tu Biblioteca — LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<header class="library-header py-5" style="background: linear-gradient(135deg, #0a0a0a 0%, #151515 100%); border-bottom: 2px solid rgba(212, 175, 55, 0.2);">
    <div class="container">
        <div class="row g-4 align-items-center">
            <!-- Columna Izquierda: Título y Buscador -->
            <div class="col-lg-5">
                <div class="d-flex align-items-center mb-3">
                    <div class="bg-gold p-2 rounded-3 me-3" style="width: 45px; height: 45px; display: flex; align-items: center; justify-content: center;">
                        <i class="bi bi-collection-play-fill text-dark fs-4"></i>
                    </div>
                    <div>
                        <h1 class="h2 fw-bold text-white mb-0">Mi Biblioteca</h1>
                        <p class="text-muted small mb-0">Gestiona tu viaje literario</p>
                    </div>
                </div>
                
                <!-- Buscador Mejorado -->
                <div class="search-box p-3 rounded-4" style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                    <label for="inputBusqueda" class="form-label text-gold small fw-bold mb-2 text-uppercase" style="letter-spacing: 1px;">
                        ¿Qué quieres leer hoy?
                    </label>
                    <div class="input-group">
                        <input type="text" id="inputBusqueda" 
                               class="form-control bg-white text-dark border-0 fw-bold" 
                               placeholder="Título del libro..." 
                               oninput="debounceSearch()">
                        <select id="selectEstadoVisual" class="form-select bg-dark text-white border-0 small" style="max-width: 140px; font-size: 0.85rem;">
                            <option value="PENDIENTE">⏳ Pendiente</option>
                            <option value="LEYENDO">📖 Leyendo</option>
                            <option value="LEIDO">✅ Leído</option>
                        </select>
                        <button class="btn btn-gold px-3 border-0" type="button" onclick="realizarBusqueda()" title="Buscar nuevos libros">
                            <i class="bi bi-search fw-bold"></i>
                        </button>
                    </div>
                    <div id="resultadosBusqueda" class="mt-2" style="max-height: 250px; overflow-y: auto;"></div>
                </div>
            </div>

            <!-- Columna Derecha: Estadísticas Mejoradas -->
            <div class="col-lg-7">
                <div class="row g-3">
                    <!-- Libros Leídos -->
                    <div class="col-sm-6">
                        <div class="stat-card p-3 rounded-4 d-flex align-items-center border" style="background: rgba(255,255,255,0.02); border-color: rgba(212, 175, 55, 0.1) !important; min-height: 80px;">
                            <div class="stat-icon me-3 fs-3">📚</div>
                            <div>
                                <div class="text-gold small fw-bold text-uppercase" style="font-size: 0.65rem; letter-spacing: 1px;">Libros Leídos</div>
                                <div class="h3 fw-black text-white mb-0" style="font-weight: 900; line-height: 1;">${statLeidos}</div>
                            </div>
                        </div>
                    </div>
                    <!-- Páginas Totales -->
                    <div class="col-sm-6">
                        <div class="stat-card p-3 rounded-4 d-flex align-items-center border" style="background: rgba(255,255,255,0.02); border-color: rgba(212, 175, 55, 0.1) !important; min-height: 80px;">
                            <div class="stat-icon me-3 fs-3">📄</div>
                            <div>
                                <div class="text-gold small fw-bold text-uppercase" style="font-size: 0.65rem; letter-spacing: 1px;">Páginas Leídas</div>
                                <div class="h3 fw-black text-white mb-0" style="font-weight: 900; line-height: 1;">${statPaginas}</div>
                            </div>
                        </div>
                    </div>
                    <!-- Autor más leído -->
                    <div class="col-12">
                        <div class="stat-card p-3 rounded-4 d-flex align-items-center border" style="background: rgba(255,255,255,0.02); border-color: rgba(212, 175, 55, 0.1) !important; min-height: 80px;">
                            <div class="stat-icon me-3 fs-3">✍️</div>
                            <div class="w-100 overflow-hidden">
                                <div class="text-gold small fw-bold text-uppercase" style="font-size: 0.65rem; letter-spacing: 1px;">Autor más leído</div>
                                <div class="text-white fw-bold text-truncate" style="font-size: 1.1rem;" title="${statAutor}">
                                    ${not empty statAutor ? statAutor : 'Aún no hay datos'}
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Libro mejor puntuado -->
                    <div class="col-12">
                        <div class="stat-card p-3 rounded-4 d-flex align-items-center border" style="background: rgba(255,255,255,0.02); border-color: rgba(212, 175, 55, 0.1) !important; min-height: 80px;">
                            <div class="stat-icon me-3 fs-3">⭐</div>
                            <div class="w-100 overflow-hidden">
                                <div class="text-gold small fw-bold text-uppercase" style="font-size: 0.65rem; letter-spacing: 1px;">Libro mejor puntuado</div>
                                <div class="text-white fw-bold text-truncate" style="font-size: 1.1rem;" title="${statMejorLibro}">
                                    ${not empty statMejorLibro ? statMejorLibro : 'Sin calificar'}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<main class="container my-5">
    <c:if test="${not empty mensajeOK}">
        <div class="alert alert-success border-0 shadow-sm mb-4"><c:out value="${mensajeOK}"/></div>
    </c:if>

    <div class="row row-cols-2 row-cols-md-4 row-cols-lg-6 g-4" id="contenedorLibros">
        <c:forEach var="libro" items="${libros}">
            <div class="col book-item-container">
                <div class="card h-100 bg-dark border-secondary text-white p-2">
                    <div class="position-relative">
                        <img src="${not empty libro.portadaUrl ? libro.portadaUrl : 'https://via.placeholder.com/150x225'}" class="card-img-top rounded shadow-sm" style="height: 200px; object-fit: cover;">
                        <div class="position-absolute top-0 end-0 p-1">
                            <a href="biblioteca?eliminar=${libro.id}" class="btn btn-danger btn-sm p-1" onclick="return confirm('¿Borrar?')"><i class="bi bi-trash"></i></a>
                        </div>
                    </div>
                    <div class="card-body p-2 text-center">
                        <h6 class="card-title text-truncate mb-1"><c:out value="${libro.titulo}"/></h6>
                        <p class="card-text small text-muted text-truncate mb-3"><c:out value="${libro.autor}"/></p>
                        
                        <div class="d-grid gap-2">
                            <a href="diario?idLibro=${libro.id}" class="btn btn-gold btn-sm">
                                <i class="bi bi-journal-bookmark"></i> Diario / Citas
                            </a>
                            <button class="btn btn-outline-warning btn-sm" 
                                    onclick="abrirModalEdicion('${libro.id}', '${libro.titulo}', '${libro.estado}', '${libro.estrellas}', '${libro.resena}')">
                                <i class="bi bi-pencil"></i> Editar
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Formulario oculto para añadir libros -->
    <form action="biblioteca" method="post" id="formGuardarLibro" style="display:none;">
        <input type="hidden" name="accion" value="nuevo">
        <input type="hidden" name="titulo" id="hTitulo">
        <input type="hidden" name="autor" id="hAutor">
        <input type="hidden" name="portadaUrl" id="hPortada">
        <input type="hidden" name="estado" id="hEstado">
        <input type="hidden" name="paginas" id="hPaginas">
    </form>

    <!-- Modal Editar -->
    <div class="modal fade" id="modalEditarLibro" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark border-secondary text-white">
                <form action="biblioteca" method="post">
                    <input type="hidden" name="accion" value="editar">
                    <input type="hidden" name="idLibro" id="editIdLibro">
                    <div class="modal-header border-secondary">
                        <h5 class="modal-title" id="modalTituloLibro">Editar Libro</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Estado</label>
                            <select name="estado" id="editEstado" class="form-select bg-dark text-white border-secondary">
                                <option value="PENDIENTE">⏳ Pendiente</option>
                                <option value="LEYENDO">📖 Leyendo</option>
                                <option value="LEIDO">✅ Leído</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Calificación</label>
                            <select name="estrellas" id="editEstrellas" class="form-select bg-dark text-white border-secondary">
                                <option value="0">Sin calificar</option>
                                <option value="5">⭐⭐⭐⭐⭐</option>
                                <option value="4">⭐⭐⭐⭐</option>
                                <option value="3">⭐⭐⭐</option>
                                <option value="2">⭐⭐</option>
                                <option value="1">⭐</option>
                            </select>
                        </div>
                        <div class="mb-0">
                            <label class="form-label">Reseña</label>
                            <textarea name="resena" id="editResena" class="form-control bg-dark text-white border-secondary" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="submit" class="btn btn-gold w-100">Guardar Cambios</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var timeoutBusqueda = null;

    function debounceSearch() {
        clearTimeout(timeoutBusqueda);
        var texto = document.getElementById('inputBusqueda').value.toLowerCase();
        
        // Filtrado local
        var cards = document.querySelectorAll('.book-item-container');
        cards.forEach(function(c) {
            var t = c.querySelector('.card-title').innerText.toLowerCase();
            c.style.display = t.indexOf(texto) > -1 ? 'block' : 'none';
        });

        if (texto.length > 2) {
            timeoutBusqueda = setTimeout(realizarBusqueda, 600);
        }
    }

    function realizarBusqueda() {
        var texto = document.getElementById('inputBusqueda').value;
        if (texto.length < 3) return;

        var res = document.getElementById('resultadosBusqueda');
        res.innerHTML = '<div class="col-12 text-center text-muted small">Buscando nuevos...</div>';

        fetch('https://www.googleapis.com/books/v1/volumes?q=' + encodeURIComponent(texto) + '&maxResults=6')
            .then(function(r) { return r.json(); })
            .then(function(data) {
                res.innerHTML = "";
                if (!data.items) return;
                data.items.forEach(function(item) {
                    var info = item.volumeInfo;
                    var title = info.title || "Sin título";
                    var author = info.authors ? info.authors[0] : "Anónimo";
                    var img = info.imageLinks ? info.imageLinks.thumbnail : "";
                    var pages = info.pageCount || 0;

                    var col = document.createElement('div');
                    col.className = 'col-md-4';
                    col.innerHTML = '<div class="card bg-secondary text-white p-2" style="cursor:pointer; font-size:0.7rem;">' +
                        '<strong>' + title + '</strong><br>' + author + '</div>';
                    col.onclick = function() {
                        if (confirm('¿Añadir libro?')) {
                            document.getElementById('hTitulo').value = title;
                            document.getElementById('hAutor').value = author;
                            document.getElementById('hPortada').value = img;
                            document.getElementById('hPaginas').value = pages;
                            document.getElementById('hEstado').value = document.getElementById('selectEstadoVisual').value;
                            document.getElementById('formGuardarLibro').submit();
                        }
                    };
                    res.appendChild(col);
                });
            });
    }

    function abrirModalEdicion(id, titulo, estado, estrellas, resena) {
        document.getElementById('editIdLibro').value = id;
        document.getElementById('modalTituloLibro').innerText = 'Editar: ' + titulo;
        document.getElementById('editEstado').value = estado;
        document.getElementById('editEstrellas').value = estrellas;
        document.getElementById('editResena').value = resena;
        new bootstrap.Modal(document.getElementById('modalEditarLibro')).show();
    }
</script>

<jsp:include page="/includes/footer.jsp" />
</body>
</html>
