import Link from 'next/link'
import { getAuthUser } from '@/lib/auth'
import { obtenerRankingLectores, getTituloLector } from '@/lib/dao/usuarioDAO'
import Header from '@/components/Header'
import Footer from '@/components/Footer'

export default async function LandingPage() {
  const user = await getAuthUser()
  const top3 = await obtenerRankingLectores(3)

  return (
    <>
      <Header user={null} />
      <main>
        {user ? (
          <div className="container py-5">
            <div className="text-center">
              <h1 className="font-title">Hola, {user.nombre}!</h1>
              <p className="text-muted">Continuá donde lo dejaste.</p>
              <Link href="/home" className="btn-gold btn-sm px-4 mt-3 d-inline-block">Ir al feed →</Link>
            </div>
          </div>
        ) : (
          <>
            <section className="hero-section">
              <div className="container">
                <div className="hero-content">
                  <h1>Tu hábito de lectura <span>ahora es un juego.</span></h1>
                  <p>La plataforma definitiva para lectores. Registra tus libros, compite con amigos y alcanza la cima del ranking literario.</p>
                  <div className="hero-btns">
                    <Link href="/signup" className="btn-main text-decoration-none">Crea tu cuenta gratis</Link>
                    <Link href="#mas-info" className="btn-secondary text-decoration-none">Explorar más</Link>
                  </div>
                </div>
              </div>
            </section>

            <div className="container" id="mas-info">
              <div className="features-grid mb-5 pb-5">
                <div className="feature-card">
                  <span className="feature-icon">📜</span>
                  <h3>Crónica Personal</h3>
                  <p>Lleva un registro detallado de cada mundo que visitas a través de las páginas.</p>
                </div>
                <div className="feature-card">
                  <span className="feature-icon">🏆</span>
                  <h3>Logros Legendarios</h3>
                  <p>Desbloquea trofeos únicos a medida que devoras capítulos y completas desafíos.</p>
                </div>
                <div className="feature-card">
                  <span className="feature-icon">🏛️</span>
                  <h3>Comunidad</h3>
                  <p>Mídete con los mejores lectores y escala posiciones en el ranking legendario.</p>
                </div>
              </div>

              <section className="card border-0 bg-transparent mb-5 pb-5">
                <div className="d-flex justify-content-between align-items-end mb-5">
                  <div>
                    <h2 className="display-5 fw-bold text-white m-0 border-0">Salón de la Fama</h2>
                    <p className="text-muted m-0 mt-2">Los lectores más dedicados.</p>
                  </div>
                  <Link href="/ranking" className="text-gold fw-bold text-decoration-none pb-2">Ver todo el ranking →</Link>
                </div>

                <div className="table-responsive">
                  <table className="table-landing w-100">
                    <thead>
                      <tr>
                        <th>Posición</th>
                        <th>Lector</th>
                        <th>Libros</th>
                        <th>Nivel</th>
                      </tr>
                    </thead>
                    <tbody>
                      {top3.length === 0 ? (
                        <tr><td colSpan={4} className="text-center text-muted py-5">Aún no hay datos de ranking.</td></tr>
                      ) : top3.map((u, i) => (
                        <tr key={u.id}>
                          <td><span className={`rank-number ${i === 0 ? 'top-1' : ''}`}>#{i + 1}</span></td>
                          <td className="fw-bold fs-5 text-white">{u.username}</td>
                          <td className="text-white fw-bold fs-5">{u.total_leidos ?? 0} <small className="text-muted fw-normal">libros</small></td>
                          <td><span className="badge-cozy">{getTituloLector(u.total_leidos ?? 0)}</span></td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>
            </div>
          </>
        )}
      </main>
      <Footer />
    </>
  )
}
