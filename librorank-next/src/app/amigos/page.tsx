'use client'

import { useState, useEffect, useCallback } from 'react'
import { useRouter } from 'next/navigation'

interface UserCard {
  id: number
  nombre: string
  username: string
  avatar_url: string | null
  bio?: string | null
  generos_favoritos?: string | null
  total_leidos?: number
  libros_en_comun?: number
  titulos_en_comun?: string
}

export default function AmigosPage() {
  const router = useRouter()
  const [amigos, setAmigos] = useState<UserCard[]>([])
  const [sugerencias, setSugerencias] = useState<UserCard[]>([])
  const [resultados, setResultados] = useState<UserCard[]>([])
  const [busqueda, setBusqueda] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/amigos').then(r => r.json()).then(data => {
      setAmigos(data.amigos || [])
      setSugerencias(data.sugerencias || [])
      setLoading(false)
    })
  }, [])

  const buscar = useCallback(async (q: string) => {
    if (q.length < 2) { setResultados([]); return }
    const res = await fetch(`/api/amigos?buscar=${encodeURIComponent(q)}`)
    setResultados(await res.json())
  }, [])

  async function agregar(amigoId: number) {
    await fetch('/api/amigos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action: 'agregar', amigoId }),
    })
    router.refresh()
    window.location.reload()
  }

  async function eliminar(amigoId: number) {
    await fetch('/api/amigos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action: 'eliminar', amigoId }),
    })
    setAmigos(prev => prev.filter(a => a.id !== amigoId))
  }

  const amigoIds = new Set(amigos.map(a => a.id))

  return (
    <div className="container py-5">
      <h1 className="font-title display-5 mb-5">👥 Comunidad</h1>

      {/* Buscador */}
      <div className="card p-4 mb-5">
        <h5 className="font-title mb-3">Buscar lectores</h5>
        <input
          type="text"
          className="form-control bg-input border-0 text-white mb-3"
          placeholder="Buscar por usuario o email..."
          value={busqueda}
          onChange={e => { setBusqueda(e.target.value); buscar(e.target.value) }}
        />
        {resultados.length > 0 && (
          <div className="row g-3">
            {resultados.map(u => (
              <UserCardComp key={u.id} user={u} esAmigo={amigoIds.has(u.id)} onAgregar={agregar} onEliminar={eliminar} />
            ))}
          </div>
        )}
      </div>

      {/* Amigos */}
      <div className="mb-5">
        <h4 className="font-title mb-4">Mis Amigos ({amigos.length})</h4>
        {loading ? (
          <p className="text-muted">Cargando...</p>
        ) : amigos.length === 0 ? (
          <div className="text-center text-muted py-4">
            <p>Aún no seguís a nadie. ¡Buscá lectores que compartan tus géneros!</p>
          </div>
        ) : (
          <div className="row g-4">
            {amigos.map(a => (
              <UserCardComp key={a.id} user={a} esAmigo={true} onAgregar={agregar} onEliminar={eliminar} />
            ))}
          </div>
        )}
      </div>

      {/* Sugerencias */}
      {sugerencias.length > 0 && (
        <div>
          <h4 className="font-title mb-4">Sugerencias de lectores afines</h4>
          <div className="row g-4">
            {sugerencias.map(s => (
              <UserCardComp key={s.id} user={s} esAmigo={false} onAgregar={agregar} onEliminar={eliminar} />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

function UserCardComp({ user, esAmigo, onAgregar, onEliminar }: {
  user: UserCard
  esAmigo: boolean
  onAgregar: (id: number) => void
  onEliminar: (id: number) => void
}) {
  return (
    <div className="col-sm-6 col-md-4">
      <div className="card p-3 h-100">
        <div className="d-flex align-items-center gap-3 mb-3">
          <img
            src={user.avatar_url || '/img/personajes/personaje_1.png'}
            alt={user.username}
            className="rounded-circle"
            style={{ width: 50, height: 50, objectFit: 'cover' }}
            onError={e => { (e.target as HTMLImageElement).src = '/img/personajes/personaje_1.png' }}
          />
          <div>
            <div className="fw-bold text-white">@{user.username}</div>
            <div className="text-muted small">{user.nombre}</div>
          </div>
        </div>
        {user.bio && <p className="text-muted small mb-2">{user.bio}</p>}
        {user.libros_en_comun !== undefined && user.libros_en_comun > 0 && (
          <div className="small text-gold mb-2">📚 {user.libros_en_comun} libros en común</div>
        )}
        {user.total_leidos !== undefined && (
          <div className="small text-muted mb-3">{user.total_leidos} libros leídos</div>
        )}
        <div className="mt-auto d-flex gap-2">
          {esAmigo ? (
            <button onClick={() => onEliminar(user.id)} className="btn btn-sm btn-outline-danger flex-fill">Dejar de seguir</button>
          ) : (
            <button onClick={() => onAgregar(user.id)} className="btn-gold btn-sm flex-fill">+ Seguir</button>
          )}
          <a href={`/perfil?id=${user.id}`} className="btn btn-sm btn-outline-secondary">Ver perfil</a>
        </div>
      </div>
    </div>
  )
}
