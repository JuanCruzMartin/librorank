<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Unite a la comunidad — LibroRank</title>
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body class="auth-page">

    <!-- LADO IZQUIERDO: ATMÓSFERA -->
    <aside class="auth-visual" style="background-image: url('https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=2000&auto=format&fit=crop');">
        <div class="auth-quote">
            <h2>"No hay amigo tan leal como un libro."</h2>
            <p>— Ernest Hemingway</p>
        </div>
    </aside>

    <!-- LADO DERECHO: FORMULARIO -->
    <main class="auth-content" style="width: 600px;"> <!-- Un poco más ancho para el grid -->
        <header class="auth-header">
            <a href="index.jsp" class="logo text-decoration-none">
                Libro<span>Rank</span>
            </a>
        </header>

        <div class="auth-form-container">
            <h1>Crea tu cuenta</h1>
            <p class="text-muted">Unite a miles de lectores y empezá a construir tu habitación.</p>

            <form action="signup" method="post" novalidate>
                <div class="row">
                    <div class="col-md-6">
                        <div class="field">
                            <label for="nombre">Nombre completo</label>
                            <input type="text" id="nombre" name="nombre" class="auth-input" placeholder="Juan Pérez" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="field">
                            <label for="usuario">Usuario</label>
                            <input type="text" id="usuario" name="usuario" class="auth-input" placeholder="@lector123" required>
                        </div>
                    </div>
                </div>

                <div class="field">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" class="auth-input" placeholder="tu@email.com" required>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="field">
                            <label for="password">Contraseña</label>
                            <input type="password" id="password" name="password" class="auth-input" placeholder="••••••••" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="field">
                            <label for="password2">Repetir</label>
                            <input type="password" id="password2" name="password2" class="auth-input" placeholder="••••••••" required>
                        </div>
                    </div>
                </div>

                <button class="btn-auth" type="submit">
                    Comenzar mi aventura
                </button>

                <p class="text-muted text-center mt-4">
                    ¿Ya sos parte? <a href="login" class="text-gold fw-bold">Iniciá sesión acá</a>
                </p>
            </form>
        </div>
    </main>

</body>
</html>
