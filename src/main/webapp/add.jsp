<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Añadir libro — LibroRank</title>
    <meta name="description" content="Agregá un nuevo libro a tu biblioteca de LibroRank con estado, fechas y nota personal." />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>
<a class="skip-link" href="#content">Saltar al contenido</a>
<header class="site-header" role="banner">
    <div class="container nav">
        <a class="brand" href="index.jsp" aria-label="Inicio LibroRank">
            <span class="logo" aria-hidden="true"></span>
            LibroRank
        </a>
        <nav aria-label="Principal">
            <div class="menu" role="menubar">
                <a href="index.jsp#features" role="menuitem">Cómo funciona</a>
                <a href="index.jsp#categorias" role="menuitem">Categorías</a>

                <!-- Ranking: va al servlet /ranking -->
                <a href="ranking" role="menuitem">Ranking</a>

                <a href="index.jsp" role="menuitem">Novedades</a>
            </div>
        </nav>
        <div class="auth">
            <!-- Si tenés servlet /biblioteca, apuntá ahí -->
            <a class="btn btn--ghost" href="biblioteca">Mi biblioteca</a>

            <!-- Crear cuenta: servlet /signup -->
            <a class="btn btn--brand" href="signup.jsp">Crear cuenta</a>
        </div>
    </div>
</header>

<main id="content" class="page" role="main">
    <section class="section">
        <div class="container add-layout">

            <header class="add-header">
                <div>
                    <h1>Añadir libro</h1>
                    <p class="muted">
                        Cargá un nuevo libro a tu biblioteca. Más adelante estos datos se van a guardar en la base y se van a usar para tu ranking.
                    </p>
                </div>
                <a class="btn btn--ghost" href="biblioteca.jsp">Volver a mi biblioteca</a>
            </header>

            <section class="card add-card" aria-label="Formulario para añadir libro">
                <form class="add-form" action="#" method="post" novalidate>

                    <!-- Bloque 1: Datos principales -->
                    <fieldset class="add-fieldset">
                        <legend>Datos del libro</legend>
                        <div class="form-grid">
                            <div class="field">
                                <label for="titulo">Título</label>
                                <input
                                        type="text"
                                        id="titulo"
                                        name="titulo"
                                        placeholder="Ej: El hombre en busca de sentido"
                                        required
                                >
                            </div>

                            <div class="field">
                                <label for="autor">Autor</label>
                                <input
                                        type="text"
                                        id="autor"
                                        name="autor"
                                        placeholder="Ej: Viktor E. Frankl"
                                        required
                                >
                            </div>

                            <div class="field">
                                <label for="genero">Género principal</label>
                                <select id="genero" name="genero">
                                    <option value="">Elegí un género</option>
                                    <option value="ficcion">Ficción</option>
                                    <option value="no-ficcion">No ficción</option>
                                    <option value="filosofia">Filosofía</option>
                                    <option value="autoayuda">Autoayuda</option>
                                    <option value="negocios">Negocios</option>
                                    <option value="tecnologia">Tecnología</option>
                                    <option value="historia">Historia</option>
                                    <option value="biografia">Biografía</option>
                                </select>
                            </div>

                            <div class="field">
                                <label for="paginas">Páginas (opcional)</label>
                                <input
                                        type="number"
                                        id="paginas"
                                        name="paginas"
                                        min="1"
                                        placeholder="Ej: 240"
                                >
                            </div>

                            <div class="field field--full">
                                <label for="portada">URL de portada (opcional)</label>
                                <input
                                        type="url"
                                        id="portada"
                                        name="portada"
                                        placeholder="https://ejemplo.com/portada.jpg"
                                >
                            </div>
                        </div>
                    </fieldset>

                    <!-- Bloque 2: Estado y fechas -->
                    <fieldset class="add-fieldset">
                        <legend>Estado y fechas</legend>
                        <div class="form-grid">
                            <div class="field">
                                <label for="estado">Estado</label>
                                <select id="estado" name="estado">
                                    <option value="leyendo">Leyendo</option>
                                    <option value="leido">Leído</option>
                                    <option value="pausa">En pausa</option>
                                    <option value="pendiente">Pendiente</option>
                                </select>
                            </div>

                            <div class="field">
                                <label for="fecha-inicio">Fecha de inicio</label>
                                <input
                                        type="date"
                                        id="fecha-inicio"
                                        name="fecha_inicio"
                                >
                            </div>

                            <div class="field">
                                <label for="fecha-fin">Fecha de finalización</label>
                                <input
                                        type="date"
                                        id="fecha-fin"
                                        name="fecha_fin"
                                >
                            </div>

                            <div class="field">
                                <label for="puntaje">Puntaje (1 a 5)</label>
                                <select id="puntaje" name="puntaje">
                                    <option value="">Sin puntaje</option>
                                    <option value="1">★☆☆☆☆</option>
                                    <option value="2">★★☆☆☆</option>
                                    <option value="3">★★★☆☆</option>
                                    <option value="4">★★★★☆</option>
                                    <option value="5">★★★★★</option>
                                </select>
                            </div>
                        </div>
                    </fieldset>

                    <!-- Bloque 3: Notas -->
                    <fieldset class="add-fieldset">
                        <legend>Notas personales</legend>
                        <div class="field">
                            <label for="notas">Qué te dejó este libro</label>
                            <textarea
                                    id="notas"
                                    name="notas"
                                    rows="4"
                                    placeholder="Escribí ideas clave, frases que te gustaron o por qué lo recomendarías."
                            ></textarea>
                        </div>
                    </fieldset>

                    <!-- Acciones -->
                    <div class="add-actions">
                        <button class="btn btn--brand" type="submit">Guardar libro</button>
                        <a class="btn btn--ghost" href="biblioteca.jsp">Cancelar</a>
                    </div>

                </form>
            </section>

        </div>
    </section>
</main>

<footer class="site-footer">
    <div class="container footer">
        <p>© <span id="y"></span> LibroRank. Hecho con ❤ desde Buenos Aires.</p>
        <nav aria-label="Legal" class="menu">
            <a href="#">Privacidad</a>
            <a href="#">Términos</a>
            <a href="#">Contacto</a>
        </nav>
    </div>
</footer>

<script>
    document.getElementById('y').textContent = new Date().getFullYear();
</script>
</body>
</html>
