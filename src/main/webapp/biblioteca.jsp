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
    <style>
        /* Corrección de visibilidad para el buscador */
        .form-control-cozy, .form-select-cozy {
            background-color: rgba(255, 255, 255, 0.05) !important;
            border: 1px solid rgba(212, 175, 55, 0.3) !important;
            color: #ffffff !important;
        }

        .form-control-cozy::placeholder {
            color: rgba(255, 255, 255, 0.5) !important;
        }

        .form-control-cozy:focus, .form-select-cozy:focus {
            background-color: rgba(255, 255, 255, 0.1) !important;
            border-color: #d4af37 !important;
            box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25) !important;
            color: #ffffff !important;
        }

        .form-select-cozy option {
            background-color: #1a1a1a;
            color: white;
        }
    </style>
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<header class="library-header">
    <div class="container">
        <div class="row align-items-center g-4">
            <div class="col-lg-5">
                <h1 class="mb-1">Mi biblioteca</h1>
                <p class="text-muted">Rincón de lectura de <strong>${usuarioLogueado.nombre}</strong></p>
            </div>

            <div class="col-lg-7">
                <div class="search-container-cozy" id="buscadorGoogle">
                   <div class="input-group">
                       <input type="text" id="inputBusqueda" class="form-control form-control-lg form-control-cozy" placeholder="Escribe el título o autor...">

                       <select id="selectEstadoVisual" class="form-select form-select-cozy">
                           <option value="PENDIENTE">Pendiente</option>
                           <option value="LEYENDO">Leyendo</option>
                           <option value="LEIDO">Leído (+10 🪙)</option>
                       </select>

                       <button class="btn btn-gold px-4" type="button" id="btnBuscar">
                           <i class="bi bi-search"></i>
                       </button>
                   </div>
                    <div id="resultadosBusqueda" class="row g-2 mt-2"></div>
                </div>
            </div>
        </div>
    </div>
</header>

<main class="container mb-5">
    <c:if test="${not empty mensajeOK}">
        <div class="alert alert-success bg-dark text-success border-success mb-4">${mensajeOK}</div>
    </c:if>
   <section class="mb-4">
           <div class="row g-3">
               <div class="col-6 col-lg-3">
                   <div class="stat-card h-100 mb-0">
                       <div class="stat-icon"><i class="bi bi-book-half"></i></div>
                       <div class="stat-details">
                          <span class="stat-value">${statLeidos}</span>
                           <span class="stat-label">Libros Leídos</span>
                       </div>
                   </div>
               </div>

               <div class="col-6 col-lg-3">
                   <div class="stat-card h-100 mb-0">
                       <div class="stat-icon"><i class="bi bi-file-earmark-text"></i></div>
                       <div class="stat-details">
                           <span class="stat-value">${statPaginas}</span>
                           <span class="stat-label">Páginas Totales</span>
                       </div>
                   </div>
               </div>

               <div class="col-6 col-lg-3">
                   <div class="stat-card h-100 mb-0">
                       <div class="stat-icon"><i class="bi bi-pen"></i></div>
                       <div class="stat-details">
                          <span class="stat-value text-truncate" title="${statAutor}">${statAutor}</span>
                           <span class="stat-label">Autor Más Leído</span>
                       </div>
                   </div>
               </div>

               <div class="col-6 col-lg-3">
                   <div class="stat-card h-100 mb-0">
                       <div class="stat-icon"><i class="bi bi-award"></i></div>
                       <div class="stat-details">
                          <span class="stat-value text-truncate" title="${statMejorLibro}">${statMejorLibro}</span>
                           <span class="stat-label">Mejor Calificado</span>
                       </div>
                   </div>
               </div>
           </div>
       </section>
    <div class="row row-cols-2 row-cols-md-4 row-cols-lg-5 row-cols-xl-6 g-4">
        <c:choose>
            <c:when test="${empty libros}">
                <div class="col-12 text-center py-5">
                    <i class="bi bi-journal-x display-1 text-muted"></i>
                    <p class="mt-3 text-muted">Tu estantería está vacía.</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="libro" items="${libros}">
                    <div class="col">
                        <article class="book-card card mb-0">
                            <a href="biblioteca?eliminar=${libro.id}" class="btn-delete-icon" onclick="confirmarEliminar(event, 'Eliminar Libro', '¿Seguro que quieres quitar `${libro.titulo}` de tu biblioteca?');">
                                <i class="bi bi-trash"></i>
                            </a>
                            <button type="button" class="btn-edit-icon" data-id="${libro.id}" data-titulo="${libro.titulo}" data-estado="${libro.estado}"
                                    data-estrellas="${not empty libro.estrellas ? libro.estrellas : 0}"
                                    data-genero="${libro.genero}" data-mood="${libro.mood}"
                                    data-resena="${not empty libro.resena ? libro.resena : ''}" onclick="abrirModalEdicion(this)">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <a href="diario?idLibro=${libro.id}" class="btn-journal-icon" title="Ver Diario">
                                <i class="bi bi-journal-text"></i>
                            </a>

                            <div class="book-cover">
                                <c:choose>
                                    <c:when test="${not empty libro.portadaUrl}">
                                        <img src="${libro.portadaUrl}" alt="${libro.titulo}" style="width: 100%; height: 100%; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-book text-muted fs-2"></i>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <span class="badge-cozy badge--${not empty libro.estado ? libro.estado.toLowerCase() : 'pendiente'} mb-2 d-inline-block">${libro.estado}</span>
                            <h3 class="h6 fw-bold mb-0 text-truncate text-white" title="${libro.titulo}">
                                <a href="libro?id=${libro.libroGlobalId}" class="text-decoration-none text-white">${libro.titulo}</a>
                            </h3>
                            <p class="small text-muted mb-2 text-truncate">${libro.autor}</p>

                            <div class="text-warning small" style="font-size: 0.75rem;">
                                <c:forEach begin="1" end="5" var="i">
                                    <i class="bi bi-star${(not empty libro.estrellas and libro.estrellas >= i) ? '-fill' : ''}"></i>
                                </c:forEach>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
<form action="biblioteca" method="post" id="formGuardarLibro" class="d-none">
        <input type="hidden" name="titulo" id="hTitulo">
        <input type="hidden" name="autor" id="hAutor">
        <input type="hidden" name="portadaUrl" id="hPortada">
        <input type="hidden" name="estado" id="hEstado" value="PENDIENTE">
        <input type="hidden" name="paginas" id="hPaginas">
    </form>

    <div class="modal fade" id="modalEditarLibro" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="background-color: var(--bg-cozy); border: 1px solid var(--accent-gold);">
                <div class="modal-header border-0">
                    <h5 class="modal-title" style="color: var(--accent-gold);" id="modalTituloLibro">Editar Libro</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="biblioteca" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="accion" value="editar">
                        <input type="hidden" name="idLibro" id="editIdLibro">
                        <div class="mb-3">
                            <label class="form-label text-muted small">Estado</label>
                            <select name="estado" id="editEstado" class="form-select">
                                <option value="PENDIENTE">Pendiente</option>
                                <option value="LEYENDO">Leyendo</option>
                                <option value="LEIDO">Leído</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Calificación</label>
                            <select name="estrellas" id="editEstrellas" class="form-select">
                                <option value="0">Sin calificar</option>
                                <option value="1">1 Estrella</option>
                                <option value="2">2 Estrellas</option>
                                <option value="3">3 Estrellas</option>
                                <option value="4">4 Estrellas</option>
                                <option value="5">5 Estrellas</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Reseña</label>
                            <textarea name="resena" id="editResena" class="form-control" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer border-0">
                        <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-gold btn-sm px-4">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 1. Agarramos los elementos del HTML
        const inputBusqueda = document.getElementById('inputBusqueda');
        const btnBuscar = document.getElementById('btnBuscar');
        const contenedorResultados = document.getElementById('resultadosBusqueda');

        // Elementos del formulario oculto
        const formGuardar = document.getElementById('formGuardarLibro');
        const hTitulo = document.getElementById('hTitulo');
        const hAutor = document.getElementById('hAutor');
        const hPortada = document.getElementById('hPortada');
        const hPaginas = document.getElementById('hPaginas'); // Atrapamos el nuevo input

        // 2. Evento: Cuando el usuario hace clic en "Buscar"
        btnBuscar.addEventListener('click', realizarBusqueda);

        // También permitir buscar al presionar ENTER
        inputBusqueda.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                realizarBusqueda();
            }
        });

        function realizarBusqueda() {
            let texto = inputBusqueda.value.trim();
            if (texto === "") return;

            contenedorResultados.innerHTML = '<div class="col-12 text-center text-muted"><div class="spinner-border spinner-border-sm text-warning me-2"></div>Buscando en Google Books...</div>';

            let url = 'https://www.googleapis.com/books/v1/volumes?q=' + encodeURIComponent(texto) + '&maxResults=6';

            fetch(url)
                .then(respuesta => {
                    if (!respuesta.ok) throw new Error("Error en la respuesta de la API");
                    return respuesta.json();
                })
                .then(datos => {
                    contenedorResultados.innerHTML = "";

                    if (!datos.items || datos.items.length === 0) {
                        contenedorResultados.innerHTML = '<div class="col-12 text-center text-danger">No se encontraron libros con ese nombre.</div>';
                        return;
                    }

                    datos.items.forEach(libro => {
                        let info = libro.volumeInfo;
                        if (!info) return;

                        let titulo = info.title || "Sin título";
                        let autor = (info.authors && info.authors.length > 0) ? info.authors[0] : "Autor desconocido";
                        
                        // Buscar la mejor imagen disponible
                        let portada = 'https://via.placeholder.com/128x192.png?text=Sin+Portada';
                        if (info.imageLinks) {
                            portada = info.imageLinks.thumbnail || info.imageLinks.smallThumbnail || portada;
                        }
                        portada = portada.replace('http:', 'https:');

                        let paginas = info.pageCount || 0;

                        // Escapar comillas para evitar errores en el HTML
                        let tituloEscapado = titulo.replace(/'/g, "\\'").replace(/"/g, "&quot;");
                        let autorEscapado = autor.replace(/'/g, "\\'").replace(/"/g, "&quot;");

                        let tarjetaHTML = '<div class="col-md-6">' +
                            '<div class="book-card d-flex align-items-center p-2" style="cursor: pointer;" ' +
                                 'onclick="guardarLibro(\'' + tituloEscapado + '\', \'' + autorEscapado + '\', \'' + portada + '\', ' + paginas + ')">' +
                                '<img src="' + portada + '" alt="Portada" style="width: 50px; height: 75px; object-fit: cover; border-radius: 4px; margin-right: 15px;">' +
                                '<div style="overflow: hidden;">' +
                                    '<h6 class="mb-0 text-white text-truncate" style="font-size: 0.9rem;" title="' + titulo + '">' + titulo + '</h6>' +
                                    '<small class="text-muted text-truncate d-block" style="font-size: 0.8rem;">' + autor + '</small>' +
                                '</div>' +
                            '</div>' +
                        '</div>';

                        contenedorResultados.innerHTML += tarjetaHTML;
                    });
                })
                .catch(error => {
                    console.error("Error en la API:", error);
                    contenedorResultados.innerHTML = '<div class="col-12 text-center text-danger">No se pudo conectar con Google Books. Reintenta en unos instantes.</div>';
                });
        }

        // 6. Función para enviar el libro a tu backend Java (Ahora recibe las páginas)
        function guardarLibro(titulo, autor, portadaUrl, paginas) {
            mostrarConfirmacion('Añadir a biblioteca', `¿Quieres añadir "${titulo}" a tu colección?`, () => {
                hTitulo.value = titulo;
                hAutor.value = autor;
                hPortada.value = portadaUrl;
                hPaginas.value = paginas;

                const estadoElegido = document.getElementById('selectEstadoVisual').value;
                document.getElementById('hEstado').value = estadoElegido;

                formGuardar.submit();
            });
        }

        // 7. Función para abrir el modal de edición
        function abrirModalEdicion(boton) {
            const id = boton.getAttribute('data-id');
            const titulo = boton.getAttribute('data-titulo');
            const estado = boton.getAttribute('data-estado');
            const estrellas = boton.getAttribute('data-estrellas');
            const resena = boton.getAttribute('data-resena');

            document.getElementById('editIdLibro').value = id;
            document.getElementById('modalTituloLibro').innerText = 'Editando: ' + titulo;
            document.getElementById('editEstado').value = estado;
            document.getElementById('editEstrellas').value = estrellas;
            document.getElementById('editResena').value = resena;

            const modal = new bootstrap.Modal(document.getElementById('modalEditarLibro'));
            modal.show();
        }
</script>
<jsp:include page="/includes/footer.jsp" />
</body>
</html>
