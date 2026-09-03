'use client'

import { useState } from 'react'
import type { RetoAmigo } from '@/lib/dao/retoDAO'
import type { Libro } from '@/lib/dao/libroDAO'

interface Props {
  retos: RetoAmigo[]
  misLibros: Libro[]
  usuarioId: number
}

export default function RetosClient({ retos: retosIni, misLibros, usuarioId }: Props) {
  const [retos, setRetos] = useState(retosIni)
  const [showModal, setShowModal] = useState(false)

  async function crearReto(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const fd = new FormData(e.currentTarget)
    const res = await fetch('/api/retos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        accion: 'crear',
        nombre: fd.get('nombre'),
        libroId: fd.get('libroId') || null,
        fechaFin: fd.get('fechaFin'),
      }),
    })
    if (res.ok) { setShowModal(false); window.location.reload() }
  }

  async function unirse(retoId: number) {
    await fetch('/api/retos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accion: 'unirse', retoId }),
    })
    window.location.reload()
  }

  async function actualizarProgreso(retoId: number, progreso: number) {
    await fetch('/api/retos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accion: 'actualizar', retoId, progreso }),
    })
    window.location.reload()
  }

  return (
    <div className="container py-5">
      <div className="d-flex justify-content-between align-items-center mb-5">
        <div>
          <h1 className="font-title display-5 mb-1">⚔️ Retos</h1>
          <p className="text-muted">Competí con amigos en desafíos de lectura.</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn-gold">+ Nuevo Reto</button>
      </div>

      {retos.length === 0 ? (
        <div className="text-center py-5 text-muted">
          <p>Aún no hay retos activos. ¡Creá el primero!</p>
          <button onClick={() => setShowModal(true)} className="btn-gold btn-sm mt-2">Crear reto</button>
        </div>
      ) : (
        <div className="row g-4">
          {retos.map(r => {
            const miParticipacion = r.participantes.find(p => p.usuario_id === usuarioId)
            const yaParticipa = Boolean(miParticipacion)

            return (
              <div key={r.id} className="col-md-6">
                <div className="card p-4">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 className="font-title text-white mb-1">{r.nombre_reto}</h5>
                      <div className="text-muted small">
                        Por @{r.creador_username}
                        {r.fecha_fin && ` · Hasta ${new Date(r.fecha_fin).toLocaleDateString('es-AR')}`}
                      </div>
                    </div>
                    {!yaParticipa && (
                      <button onClick={() => unirse(r.id)} className="btn-gold btn-sm">Unirse</button>
                    )}
                  </div>

                  {r.titulo_libro && (
                    <div className="badge-cozy mb-3">📚 {r.titulo_libro}</div>
                  )}

                  <div className="participantes">
                    <div className="small text-muted mb-2">Participantes ({r.participantes.length})</div>
                    {r.participantes.map(p => (
                      <div key={p.usuario_id} className="d-flex align-items-center gap-2 mb-2">
                        <img
                          src={p.avatar_url || '/img/personajes/personaje_1.png'}
                          alt={p.username}
                          className="rounded-circle"
                          style={{ width: 30, height: 30, objectFit: 'cover' }}
                          onError={e => { (e.target as HTMLImageElement).src = '/img/personajes/personaje_1.png' }}
                        />
                        <span className="text-white small flex-fill">@{p.username}</span>
                        <div className="d-flex align-items-center gap-1" style={{ minWidth: 100 }}>
                          <div className="progress flex-fill" style={{ height: 6 }}>
                            <div className="progress-bar bg-warning" style={{ width: `${Math.min(p.progreso, 100)}%` }}></div>
                          </div>
                          <span className="text-muted small">{p.progreso}%</span>
                        </div>
                        {p.usuario_id === usuarioId && (
                          <div className="d-flex gap-1">
                            {[25, 50, 75, 100].map(pct => (
                              <button key={pct} onClick={() => actualizarProgreso(r.id, pct)}
                                className="btn btn-sm btn-outline-secondary" style={{ fontSize: '0.65rem', padding: '2px 4px' }}>
                                {pct}%
                              </button>
                            ))}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content-custom" onClick={e => e.stopPropagation()}>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h4 className="font-title">Crear Reto</h4>
              <button onClick={() => setShowModal(false)} className="btn-close btn-close-white"></button>
            </div>
            <form onSubmit={crearReto}>
              <div className="mb-3">
                <label className="form-label text-muted">Nombre del reto *</label>
                <input name="nombre" type="text" className="form-control bg-input border-0 text-white" required placeholder="Ej: Leer 5 libros en enero" />
              </div>
              <div className="mb-3">
                <label className="form-label text-muted">Libro (opcional)</label>
                <select name="libroId" className="form-select bg-input border-0 text-white">
                  <option value="">Sin libro específico</option>
                  {misLibros.map(l => <option key={l.id} value={l.id}>{l.titulo}</option>)}
                </select>
              </div>
              <div className="mb-4">
                <label className="form-label text-muted">Fecha límite *</label>
                <input name="fechaFin" type="date" className="form-control bg-input border-0 text-white" required />
              </div>
              <button type="submit" className="btn-gold w-100">Crear reto</button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
