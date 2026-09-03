'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import type { Libro } from '@/lib/dao/libroDAO'
import type { DiarioLectura } from '@/lib/dao/diarioDAO'
import type { Cita } from '@/lib/dao/citaDAO'

interface Props {
  libro: Libro | null
  entradas: DiarioLectura[]
  citas: Cita[]
  misLibros: Libro[]
  libroIdActual: number | null
}

export default function DiarioClient({ libro, entradas: entradasIni, citas: citasIni, misLibros, libroIdActual }: Props) {
  const router = useRouter()
  const [entradas, setEntradas] = useState(entradasIni)
  const [citas, setCitas] = useState(citasIni)
  const [tab, setTab] = useState<'entradas' | 'citas'>('entradas')

  async function agregarEntrada(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const fd = new FormData(e.currentTarget)
    const res = await fetch('/api/diario', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ libroId: libroIdActual, capitulo: fd.get('capitulo'), comentario: fd.get('comentario') }),
    })
    if (res.ok) { e.currentTarget.reset(); window.location.reload() }
  }

  async function agregarCita(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const fd = new FormData(e.currentTarget)
    const res = await fetch('/api/diario', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accion: 'cita', libroId: libroIdActual, texto: fd.get('texto'), pagina: fd.get('pagina') }),
    })
    if (res.ok) { e.currentTarget.reset(); window.location.reload() }
  }

  if (!libro) {
    return (
      <div className="container py-5">
        <h1 className="font-title display-5 mb-5">📓 Diario de Lectura</h1>
        <div className="card p-5 text-center">
          <p className="text-muted mb-4">Seleccioná un libro para ver su diario de lectura.</p>
          <div className="row g-3 justify-content-center">
            {misLibros.map(l => (
              <div key={l.id} className="col-auto">
                <a href={`/diario?libroId=${l.id}`} className="d-block">
                  <div className="card p-3 text-center" style={{ width: 120 }}>
                    {l.portada_url ? (
                      <img src={l.portada_url} alt={l.titulo} style={{ width: '100%', height: 90, objectFit: 'cover', borderRadius: 6 }} />
                    ) : <div style={{ height: 90, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem' }}>📚</div>}
                    <div className="small text-white mt-2 text-truncate">{l.titulo}</div>
                  </div>
                </a>
              </div>
            ))}
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="container py-5">
      <div className="d-flex align-items-center gap-3 mb-5">
        <a href="/diario" className="text-muted text-decoration-none">← Volver</a>
        <h1 className="font-title h2 mb-0">📓 {libro.titulo}</h1>
        <span className="text-muted">— {libro.autor}</span>
      </div>

      <div className="row g-4">
        <div className="col-lg-8">
          <ul className="nav nav-tabs mb-4">
            <li className="nav-item">
              <button onClick={() => setTab('entradas')} className={`nav-link ${tab === 'entradas' ? 'active' : 'text-muted'}`}>
                📝 Entradas ({entradas.length})
              </button>
            </li>
            <li className="nav-item">
              <button onClick={() => setTab('citas')} className={`nav-link ${tab === 'citas' ? 'active' : 'text-muted'}`}>
                💬 Citas ({citas.length})
              </button>
            </li>
          </ul>

          {tab === 'entradas' && (
            <div>
              {entradas.length === 0 ? (
                <p className="text-muted">Aún no hay entradas. ¡Agregá tu primera nota!</p>
              ) : (
                <div className="timeline-container">
                  {entradas.map(e => (
                    <div key={e.id} className="card mb-3 p-3">
                      {e.capitulo && <div className="text-gold small fw-bold mb-2">Cap. {e.capitulo}</div>}
                      <p className="text-white mb-2">{e.comentario}</p>
                      <div className="text-muted small">
                        {e.fecha_creacion ? new Date(e.fecha_creacion).toLocaleDateString('es-AR', { day: 'numeric', month: 'long' }) : ''}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {tab === 'citas' && (
            <div>
              {citas.length === 0 ? (
                <p className="text-muted">Aún no hay citas guardadas.</p>
              ) : citas.map(c => (
                <div key={c.id} className="card mb-3 p-4" style={{ borderLeft: '3px solid var(--accent-gold)' }}>
                  <p className="text-white mb-2" style={{ fontStyle: 'italic' }}>
                    &ldquo;{c.texto}&rdquo;
                  </p>
                  {c.pagina && <div className="text-muted small">Página {c.pagina}</div>}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="col-lg-4">
          <div className="card p-4 mb-4">
            <h6 className="font-title mb-3">Nueva entrada</h6>
            <form onSubmit={agregarEntrada}>
              <div className="mb-3">
                <label className="form-label text-muted small">Capítulo / Sección</label>
                <input name="capitulo" type="text" className="form-control bg-input border-0 text-white" placeholder="Ej: Cap. 5" />
              </div>
              <div className="mb-3">
                <label className="form-label text-muted small">Comentario *</label>
                <textarea name="comentario" rows={4} className="form-control bg-input border-0 text-white" required placeholder="¿Qué te pareció?..." />
              </div>
              <button type="submit" className="btn-gold w-100">Guardar entrada</button>
            </form>
          </div>

          <div className="card p-4">
            <h6 className="font-title mb-3">Nueva cita</h6>
            <form onSubmit={agregarCita}>
              <div className="mb-3">
                <textarea name="texto" rows={3} className="form-control bg-input border-0 text-white" required placeholder="Escribí la cita..." />
              </div>
              <div className="mb-3">
                <input name="pagina" type="text" className="form-control bg-input border-0 text-white" placeholder="Página (opcional)" />
              </div>
              <button type="submit" className="btn-gold w-100">Guardar cita</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
