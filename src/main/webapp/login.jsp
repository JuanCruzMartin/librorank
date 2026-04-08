<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Bienvenido de nuevo — LibroRank</title>
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body class="auth-page">

    <!-- LADO IZQUIERDO: ATMÓSFERA -->
    <aside class="auth-visual">
        <div class="auth-quote">
            <h2>"Un lector vive mil vidas antes de morir. El que nunca lee solo vive una."</h2>
            <p>— George R.R. Martin</p>
        </div>
    </aside>

    <!-- LADO DERECHO: FORMULARIO -->
    <main class="auth-content">
        <header class="auth-header">
            <a href="index.jsp" class="logo text-decoration-none">
                Libro<span>Rank</span>
            </a>
        </header>

        <div class="auth-form-container">
            <h1>Inicia sesión</h1>
            <p class="text-muted">Qué bueno verte de nuevo. Tu biblioteca te espera.</p>

            <form action="login" method="post" novalidate>
                <div class="field">
                    <label for="identificador">Email o usuario</label>
                    <input
                        type="text"
                        id="identificador"
                        name="identificador"
                        class="auth-input"
                        placeholder="ejemplo@correo.com"
                        required
                    >
                </div>

                <div class="field">
                    <label for="password">Contraseña</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        class="auth-input"
                        placeholder="••••••••"
                        required
                    >
                </div>

                <button class="btn-auth" type="submit">
                    Entrar a mi biblioteca
                </button>

                <p class="text-muted text-center mt-4">
                    ¿Todavía no tenés cuenta? <a href="signup" class="text-gold fw-bold">Creá tu cuenta gratis</a>
                </p>

                <%
                    String errorLogin = (String) request.getAttribute("errorLogin");
                    if (errorLogin != null) {
                %>
                    <div class="error-message">
                        ⚠️ <%= errorLogin %>
                    </div>
                <%
                    }
                %>
            </form>
        </div>
    </main>

</body>
</html>
