<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Estadísticas Literarias - LibroRank</title>
    <link rel="stylesheet" href="Css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container py-5">
        <h1 class="font-title mb-5">Tus Insights Literarios</h1>

        <div class="row g-4">
            <!-- Gráfico de Géneros -->
            <div class="col-md-6">
                <div class="card p-4 h-100">
                    <h3 class="h5 mb-4 text-center">Tus Géneros más leídos</h3>
                    <canvas id="chartGeneros"></canvas>
                </div>
            </div>

            <!-- Gráfico de Moods -->
            <div class="col-md-6">
                <div class="card p-4 h-100">
                    <h3 class="h5 mb-4 text-center">Tus Moods (Estados de Ánimo)</h3>
                    <canvas id="chartMoods"></canvas>
                </div>
            </div>
        </div>

        <div class="text-center mt-5">
            <a href="biblioteca" class="btn btn-gold">Volver a mi Biblioteca</a>
        </div>
    </div>

    <script>
        const generosData = ${generosJson};
        const moodsData = ${moodsJson};

        function createChart(ctxId, dataMap, title, type = 'doughnut') {
            const ctx = document.getElementById(ctxId).getContext('2d');
            const labels = Object.keys(dataMap);
            const values = Object.values(dataMap);

            new Chart(ctx, {
                type: type,
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: [
                            '#d4af37', '#3498db', '#e67e22', '#2ecc71', '#9b59b6', '#f1c40f'
                        ],
                        borderColor: '#25211e',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: { color: '#e0d9d5', font: { family: 'Plus Jakarta Sans' } }
                        }
                    }
                }
            });
        }

        if (Object.keys(generosData).length > 0) {
            createChart('chartGeneros', generosData, 'Géneros');
        }
        if (Object.keys(moodsData).length > 0) {
            createChart('chartMoods', moodsData, 'Moods', 'pie');
        }
    </script>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
