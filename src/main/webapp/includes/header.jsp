<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="site-header">
    <div class="container d-flex justify-content-between align-items-center">

        <a href="biblioteca" class="logo text-decoration-none">
            Libro<span>Rank</span>
        </a>

        <nav>
            <ul class="d-flex list-unstyled gap-3 mb-0 align-items-center">
                <li>
                    <a href="biblioteca" class="${pageContext.request.requestURI.contains('biblioteca') ? 'text-white' : 'text-muted'} fw-500">
                        📚 Biblioteca
                    </a>
                </li>
                <li>
                    <a href="bingo" class="${pageContext.request.requestURI.contains('bingo') ? 'text-white' : 'text-muted'} fw-500">
                        🎲 Bingo
                    </a>
                </li>
                <li>
                    <a href="retos" class="${pageContext.request.requestURI.contains('retos') ? 'text-white' : 'text-muted'} fw-500">
                        ⚔️ Retos
                    </a>
                </li>
                <li>
                    <a href="cuento" class="${pageContext.request.requestURI.contains('cuento') ? 'text-white' : 'text-muted'} fw-500">
                        ✍️ Cuento
                    </a>
                </li>
                <li>
                    <a href="ranking" class="${pageContext.request.requestURI.contains('ranking') ? 'text-white' : 'text-muted'} fw-500">
                        🏆 Ranking
                    </a>
                </li>
                <li>
                    <a href="amigos" class="${pageContext.request.requestURI.contains('amigos') ? 'text-white' : 'text-muted'} fw-500">
                        👥 Comunidad
                    </a>
                </li>

                <li class="ms-2">
                    <div class="badge-cozy" style="background: rgba(255, 69, 0, 0.1); color: #ff4500; border: 1px solid rgba(255, 69, 0, 0.3);">
                        🔥 ${usuarioLogueado.rachaActual}
                    </div>
                </li>

                <li>
                    <a href="perfil" class="btn-gold btn-sm py-1 px-3">
                        👤 Perfil
                    </a>
                </li>
                <li>
                    <a href="logout" class="text-danger fw-bold small ms-2">Salir</a>
                </li>
            </ul>
        </nav>
    </div>
</header>
