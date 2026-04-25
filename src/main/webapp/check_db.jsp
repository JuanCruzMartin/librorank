<%@ page import="java.sql.Connection" %>
<%@ page import="com.librorank.config.DatabaseConfig" %>
<%@ page import="java.sql.DatabaseMetaData" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Check Database Connection</title>
    <style>
        body { background: #1a1a1a; color: #fff; font-family: sans-serif; padding: 20px; }
        .card { background: #25211e; border: 1px solid #d4af37; padding: 20px; border-radius: 10px; }
        .success { color: #4cd137; }
        .info { color: #d4af37; }
    </style>
</head>
<body>
    <div class="card">
        <h2>🔍 Diagnóstico de Base de Datos</h2>
        <%
            try (Connection conn = DatabaseConfig.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
        %>
            <p class="success">✅ ¡Conexión exitosa!</p>
            <p><strong>URL de conexión:</strong> <span class="info"><%= meta.getURL() %></span></p>
            <p><strong>Usuario actual:</strong> <span class="info"><%= meta.getUserName() %></span></p>
            <p><strong>Base de Datos:</strong> <span class="info"><%= meta.getDatabaseProductName() %> <%= meta.getDatabaseProductVersion() %></span></p>
        <%
            } catch (Exception e) {
        %>
            <p style="color: #ff5e57;">❌ Error de conexión: <%= e.getMessage() %></p>
        <%
            }
        %>
    </div>
</body>
</html>
