'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import type { Usuario } from '@/lib/dao/usuarioDAO'
import type { Libro, PerfilStats } from '@/lib/dao/libroDAO'
import type { Logro } from '@/lib/dao/logroDAO'

const AVATARES = [
  '/img/avatar_explorador/explorador_1.png',
  '/img/avatar_explorador/explorador_2.png',
  '/img/avatar_maestro/maestro_1.png',
  '/img/avatar_maestro/maestro_2.png',
]

interface Props {
  usuario: Usuario
  stats: PerfilStats
  ultimasLecturas: Libro[]
  logros: Logro[]
  leidosEsteAnio: number
  totalLeidos: number
  tituloLector: string
  esMiPerfil: boolean
}

export default function PerfilClient({ usuario, stats, ultimasLecturas, logros, leidosEsteAnio, totalLeidos, tituloLector, esMiPerfil }: Props) {
  const router = useRouter()
  const [tab, setTab] = useState<'resumen' | 'config' | 'biblioteca'>('resumen')
  const [mensaje, setMensaje] = useState('')
  const [guardando, setGuardando] = useState(false)
  const [subiendo, setSubiendo] = useState(false)

  async function guardarPerfil(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setGuardando(true)
    const fd = new FormData(e.currentTarget)
    const data: Record<string, unknown> = {}
    fd.forEach((v, k) => { data[k] = v })

    const res = await fetch('/api/perfil', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
    setGuardando(false)
    if (res.ok) { setMensaje('Perfil actualizado'); router.refresh() }
    else setMensaje('Error al guardar')
  }

  async function seleccionarAvatar(url: string) {
    const res = await fetch('/api/perfil', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ avatar_url: url }),
    })
    if (res.ok) router.refresh()
  }

  async function subirFoto(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return
    setSubiendo(true)
    const fd = new FormData()
    fd.append('foto', file)
    const res = await fetch('/api/upload', { method: 'POST', body: fd })
    setSubiendo(false)
    if (res.ok) router.refresh()
  }

  const nivelPct = Math.min((leidosEsteAnio / (usuario.objetivo_anual || 12)) * 100, 100)

  return (
    <div className="container py-5">
      <div className="perfil-layout" style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: '2rem' }}>
        {/* Sidebar */}
        <aside>
          <div className="card p-4 text-center mb-4">
            <div className="user-avatar mb-3" style={{ width: 120, height: 120, margin: '0 auto', borderRadius: '50%', overflow: 'hidden' }}>
              <img
                src={usuario.avatar_url || '/img/personajes/personaje_1.png'}
                alt="Avatar"
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                onError={e => { (e.target as HTMLImageElement).src = '/img/personajes/personaje_1.png' }}
              />
            </div>
            <h2 className="h4 mb-1">{usuario.nombre}</h2>
            <p className="text-muted small">@{usuario.username}</p>
            <div className="badge-cozy mb-3">{tituloLector}</div>

            <div className="d-flex justify-content-center gap-4 mb-3">
              <div><div className="fw-bold text-gold">🔥 {usuario.racha_actual}</div><div className="small text-muted">Racha</div></div>
              <div><div className="fw-bold text-gold">⭐ {usuario.puntos}</div><div className="small text-muted">Puntos</div></div>
              <div><div className="fw-bold text-white">{totalLeidos}</div><div className="small text-muted">Leídos</div></div>
            </div>

            {usuario.bio && <p className="text-muted small mb-3">{usuario.bio}</p>}

            {/* Objetivo anual */}
            {usuario.objetivo_anual && (
              <div className="mt-2">
                <div className="d-flex justify-content-between small text-muted mb-1">
                  <span>Objetivo {new Date().getFullYear()}</span>
                  <span>{leidosEsteAnio} / {usuario.objetivo_anual}</span>
                </div>
                <div className="progress" style={{ height: 6 }}>
                  <div className="progress-bar bg-warning" style={{ width: `${nivelPct}%` }}></div>
                </div>
              </div>
            )}

            {esMiPerfil && (
              <div className="d-flex gap-2 mt-3">
                <button onClick={() => setTab('config')} className="btn btn-sm btn-outline-secondary flex-fill">Editar perfil</button>
              </div>
            )}
          </div>
        </aside>

        {/* Contenido principal */}
        <div>
          {esMiPerfil && (
            <ul className="nav nav-tabs mb-4">
              {(['resumen', 'config', 'biblioteca'] as const).map(t => (
                <li key={t} className="nav-item">
                  <button onClick={() => setTab(t)} className={`nav-link ${tab === t ? 'active' : 'text-muted'}`}>
                    {t === 'resumen' ? '📊 Resumen' : t === 'config' ? '⚙️ Configuración' : '📚 Biblioteca'}
                  </button>
                </li>
              ))}
            </ul>
          )}

          {tab === 'resumen' && (
            <div>
              {/* Stats */}
              <div className="row g-3 mb-4">
                {[
                  { label: 'Leídos', value: stats.leidos, icon: '📖' },
                  { label: 'Leyendo', value: stats.leyendo, icon: '📖' },
                  { label: 'Pendientes', value: stats.pendientes, icon: '⏳' },
                  { label: 'En pausa', value: stats.pausa, icon: '⏸️' },
                ].map(s => (
                  <div key={s.label} className="col-6 col-sm-3">
                    <div className="stat-card text-center">
                      <div className="stat-icon">{s.icon}</div>
                      <div className="stat-value">{s.value}</div>
                      <div className="stat-label">{s.label}</div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Últimas lecturas */}
              {ultimasLecturas.length > 0 && (
                <div className="mb-4">
                  <h5 className="font-title mb-3">Últimas lecturas</h5>
                  <div className="d-flex gap-3 flex-wrap">
                    {ultimasLecturas.map(l => (
                      <div key={l.id} className="card-lectura-mini">
                        {l.portada_url ? (
                          <img src={l.portada_url} alt={l.titulo} style={{ width: 60, height: 90, objectFit: 'cover', borderRadius: 6 }} />
                        ) : (
                          <div style={{ width: 60, height: 90, background: '#2c2724', borderRadius: 6, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>📚</div>
                        )}
                        <div className="small text-muted mt-1 text-center" style={{ maxWidth: 60, wordBreak: 'break-word' }}>{l.titulo}</div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Logros */}
              <div>
                <h5 className="font-title mb-3">Logros</h5>
                <div className="row g-3">
                  {logros.map(l => (
                    <div key={l.id} className="col-6 col-sm-4">
                      <div className={`trophy-item p-3 text-center ${l.desbloqueado ? 'desbloqueado' : 'bloqueado'}`}
                        style={{ opacity: l.desbloqueado ? 1 : 0.4, background: '#2c2724', borderRadius: 12 }}>
                        <div style={{ fontSize: '2rem' }}>{l.icono || '🏆'}</div>
                        <div className="small fw-bold text-white mt-1">{l.nombre}</div>
                        <div className="small text-muted">{l.descripcion}</div>
                      </div>
                    </div>
                  ))}
                  {logros.length === 0 && <p className="text-muted">Aún no hay logros disponibles.</p>}
                </div>
              </div>
            </div>
          )}

          {tab === 'config' && esMiPerfil && (
            <div className="card p-4">
              <h5 className="font-title mb-4">Editar Perfil</h5>
              {mensaje && <div className="alert alert-info">{mensaje}</div>}

              <div className="mb-4">
                <h6 className="text-muted mb-3">Foto de perfil</h6>
                <label className="btn btn-outline-secondary btn-sm mb-2">
                  {subiendo ? 'Subiendo...' : '📷 Subir foto'}
                  <input type="file" accept="image/*" className="d-none" onChange={subirFoto} />
                </label>
                <div className="d-flex gap-2 flex-wrap mt-2">
                  {AVATARES.map(av => (
                    <button key={av} onClick={() => seleccionarAvatar(av)}
                      className="btn p-0" style={{ border: usuario.avatar_url === av ? '2px solid var(--accent-gold)' : '2px solid transparent' }}>
                      <img src={av} alt="avatar" style={{ width: 50, height: 50, objectFit: 'cover', borderRadius: 6 }} />
                    </button>
                  ))}
                </div>
              </div>

              <form onSubmit={guardarPerfil}>
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label text-muted">Nombre</label>
                    <input name="nombre" defaultValue={usuario.nombre} className="form-control bg-input border-0 text-white" />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted">Username</label>
                    <input name="username" defaultValue={usuario.username} className="form-control bg-input border-0 text-white" />
                  </div>
                  <div className="col-12">
                    <label className="form-label text-muted">Email</label>
                    <input name="email" type="email" defaultValue={usuario.email} className="form-control bg-input border-0 text-white" />
                  </div>
                  <div className="col-12">
                    <label className="form-label text-muted">Bio</label>
                    <textarea name="bio" defaultValue={usuario.bio || ''} rows={3} className="form-control bg-input border-0 text-white" />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted">Objetivo anual de libros</label>
                    <input name="objetivo_anual" type="number" defaultValue={usuario.objetivo_anual || ''} className="form-control bg-input border-0 text-white" />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted">Géneros favoritos</label>
                    <input name="generos_favoritos" defaultValue={usuario.generos_favoritos || ''} className="form-control bg-input border-0 text-white" placeholder="Ej: Fantasía, Ciencia Ficción" />
                  </div>
                </div>
                <button type="submit" disabled={guardando} className="btn-gold mt-4">
                  {guardando ? 'Guardando...' : 'Guardar cambios'}
                </button>
              </form>
            </div>
          )}

          {tab === 'biblioteca' && (
            <div>
              <p className="text-muted">Los libros de {usuario.nombre} son privados. Visitá su perfil público para ver más.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
