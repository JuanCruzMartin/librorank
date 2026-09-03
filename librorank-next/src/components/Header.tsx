'use client'

import Link from 'next/link'
import { usePathname, useRouter } from 'next/navigation'

interface HeaderProps {
  user: {
    nombre: string
    username: string
    racha_actual?: number
    puntos?: number
    avatar_url?: string | null
  } | null
}

export default function Header({ user }: HeaderProps) {
  const pathname = usePathname()
  const router = useRouter()

  const isActive = (path: string) => pathname.startsWith(path) ? 'text-white' : 'text-muted'

  async function handleLogout() {
    await fetch('/api/auth/logout', { method: 'POST' })
    router.push('/')
    router.refresh()
  }

  if (!user) {
    return (
      <header className="site-header">
        <div className="container d-flex justify-content-between align-items-center">
          <Link href="/" className="logo text-decoration-none">Libro<span>Rank</span></Link>
          <nav>
            <ul className="d-flex list-unstyled gap-4 mb-0 align-items-center">
              <li><Link href="/login" className="nav-link-custom">Ingresar</Link></li>
              <li><Link href="/signup" className="btn-gold btn-sm px-4">Empezar ahora</Link></li>
            </ul>
          </nav>
        </div>
      </header>
    )
  }

  return (
    <header className="site-header">
      <div className="container d-flex justify-content-between align-items-center">
        <Link href="/home" className="logo text-decoration-none">Libro<span>Rank</span></Link>
        <nav>
          <ul className="d-flex list-unstyled gap-3 mb-0 align-items-center flex-wrap">
            <li><Link href="/biblioteca" className={`${isActive('/biblioteca')} fw-500`}>📚 Biblioteca</Link></li>
            <li><Link href="/bingo" className={`${isActive('/bingo')} fw-500`}>🎲 Bingo</Link></li>
            <li><Link href="/retos" className={`${isActive('/retos')} fw-500`}>⚔️ Retos</Link></li>
            <li><Link href="/cuento" className={`${isActive('/cuento')} fw-500`}>✍️ Cuento</Link></li>
            <li><Link href="/ranking" className={`${isActive('/ranking')} fw-500`}>🏆 Ranking</Link></li>
            <li><Link href="/amigos" className={`${isActive('/amigos')} fw-500`}>👥 Comunidad</Link></li>
            <li className="ms-2">
              <span className="badge-cozy" style={{ background: 'rgba(255,69,0,0.1)', color: '#ff4500', border: '1px solid rgba(255,69,0,0.3)' }}>
                🔥 {user.racha_actual ?? 0}
              </span>
            </li>
            <li className="ms-2">
              <span className="badge-cozy" style={{ background: 'rgba(212,175,55,0.1)', color: '#D4AF37', border: '1px solid rgba(212,175,55,0.3)' }}>
                ⭐ {user.puntos ?? 0}
              </span>
            </li>
            <li>
              <Link href="/perfil" className="btn-gold btn-sm py-1 px-3">👤 Perfil</Link>
            </li>
            <li>
              <button onClick={handleLogout} className="btn btn-link text-danger fw-bold small ms-2 p-0">Salir</button>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  )
}
