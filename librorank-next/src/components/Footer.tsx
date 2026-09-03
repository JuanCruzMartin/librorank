export default function Footer() {
  return (
    <footer className="site-footer py-5 mt-5 border-top border-secondary">
      <div className="container text-center">
        <div className="logo mb-4 fs-3">LIBRO<span>RANK</span></div>
        <div className="d-flex justify-content-center gap-4 mb-4">
          <a href="#" className="text-muted small text-decoration-none">Términos</a>
          <a href="#" className="text-muted small text-decoration-none">Privacidad</a>
          <a href="#" className="text-muted small text-decoration-none">Contacto</a>
        </div>
        <p className="text-muted small">&copy; 2026 LibroRank. La experiencia de lectura definitiva.</p>
      </div>
    </footer>
  )
}
