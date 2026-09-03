'use client'

import { useState, useCallback } from 'react'
import Link from 'next/link'
import type { Libro, PerfilStats } from '@/lib/dao/libroDAO'
import type { Usuario } from '@/lib/dao/usuarioDAO'

const ESTADOS = ['PENDIENTE', 'LEYENDO', 'LEIDO', 'PAUSA']
const GENEROS = ['Fantasía', 'Ciencia Ficción', 'Romance', 'Terror', 'Misterio', 'Historia', 'Biografía', 'Autoayuda', 'Poesía', 'Otro']
const MOODS = ['Relajado', 'Aventurero', 'Emotivo', 'Intelectual', 'Nostálgico', 'Inspirador', 'Oscuro', 'Divertido']

interface Props {
  librosIniciales: Libro[]
  stats: PerfilStats
  autorMasLeido: string
  mejorCalificado: string
  paginas: number
  usuario: Usuario
}

export default function BibliotecaClient({ librosIniciales, stats, autorMasLeido, mejorCalificado, paginas }: Props) {
  const [libros, setLibros] = useState(librosIniciales)
  const [filtro, setFiltro] = useState('TODOS')
  const [busqueda, setBusqueda] = useState('')
  const [sugerencias, setSugerencias] = useState<{ titulo: string; autor: string; anio: string; paginas: string; portada: string }[]>([])
  const [editando, setEditando] = useState<Libro | null>(null)
  const [mensaje, setMensaje] = useState('')
  const [showModal, setShowModal] = useState(false)

  const buscarLibros = useCallback(async (q: string) => {
    if (q.length < 3) { setSugerencias([]); return }
    const res = await fetch(`/api/libros/buscar?q=${encodeURIComponent(q)}`)
    const data = await res.json()
    setSugerencias(data)
  }, [])

  const seleccionarSugerencia = (s: { titulo: string; autor: string; anio: string; paginas: string; portada: string }) => {
    const form = document.getElementById('formNuevo') as HTMLFormElement
    if (!form) return
    ;(form.elements.namedItem('titulo') as HTMLInputElement).value = s.titulo
    ;(form.elements.namedItem('autor') as HTMLInputElement).value = s.autor
    ;(form.elements.namedItem('anio') as HTMLInputElement).value = s.anio
    ;(form.elements.namedItem('paginas') as HTMLInputElement).value = s.paginas
    ;(form.elements.namedItem('portada_url') as HTMLInputElement).value = s.portada
    setSugerencias([])
    setBusqueda(s.titulo)
  }

  async function agregarLibro(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const fd = new FormData(e.currentTarget)
    const data = {
      accion: 'nuevo',
      titulo: fd.get('titulo'), autor: fd.get('autor'),
      anio: fd.get('anio'), paginas: fd.get('paginas'),
      estado: fd.get('estado'), portada_url: fd.get('portada_url'),
      genero: fd.get('genero'), mood: fd.get('mood'),
    }
    const res = await fetch('/api/libros', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) })
    const json = await res.json()
    if (!res.ok) { setMensaje(json.error); return }

    setShowModal(false)
    window.location.reload()
  }

  async function editarLibro(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!editando) return
    const fd = new FormData(e.currentTarget)
    const data = {
      accion: 'editar', id: editando.id,
      estado: fd.get('estado'), estrellas: fd.get('estrellas'),
      resena: fd.get('resena'), genero: fd.get('genero'), mood: fd.get('mood'),
    }
    const res = await fetch('/api/libros', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) })
    if (res.ok) { setEditando(null); window.location.reload() }
  }

  async function eliminarLibro(id: number) {
    if (!confirm('¿Eliminar este libro?')) return
    await fetch('/api/libros', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ accion: 'eliminar', id }) })
    setLibros(prev => prev.filter(l => l.id !== id))
  }

  const librosFiltrados = libros.filter(l => {
    if (filtro !== 'TODOS' && l.estado !== filtro) return false
    if (busqueda && !l.titulo.toLowerCase().includes(busqueda.toLowerCase()) && !l.autor.toLowerCase().includes(busqueda.toLowerCase())) return false
    return true
  })

  return (
    <div className="container py-5">
      {mensaje && <div className="alert alert-danger">{mensaje}</div>}

      {/* Stats */}
      <div className="row g-3 mb-5">
        {[
          { label: 'Leídos', value: stats.leidos, icon: '📖' },
          { label: 'Páginas', value: paginas, icon: '📄' },
          { label: 'Autor favorito', value: autorMasLeido, icon: '✍️' },
          { label: 'Mejor libro', value: mejorCalificado, icon: '⭐' },
        ].map(s => (
          <div key={s.label} className="col-sm-6 col-lg-3">
            <div className="stat-card">
              <div className="stat-icon">{s.icon}</div>
              <div className="stat-value">{s.value}</div>
              <div className="stat-label">{s.label}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Toolbar */}
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <h1 className="font-title h2 mb-0">Mi Biblioteca</h1>
        <div className="d-flex gap-2 flex-wrap">
          <input
            type="text"
            className="form-control bg-input border-0 text-white"
            placeholder="Buscar..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
            style={{ maxWidth: 200 }}
          />
          {ESTADOS.map(e => (
            <button key={e} onClick={() => setFiltro(e)}
              className={`btn btn-sm ${filtro === e ? 'btn-gold' : 'btn-outline-secondary'}`}>
              {e}
            </button>
          ))}
          <button onClick={() => setFiltro('TODOS')} className={`btn btn-sm ${filtro === 'TODOS' ? 'btn-gold' : 'btn-outline-secondary'}`}>TODOS</button>
          <button onClick={() => setShowModal(true)} className="btn-gold btn-sm px-3">+ Agregar</button>
        </div>
      </div>

      {/* Grid de libros */}
      {librosFiltrados.length === 0 ? (
        <div className="text-center text-muted py-5">
          <p>No hay libros en esta categoría.</p>
          <button onClick={() => setShowModal(true)} className="btn-gold btn-sm mt-2">Agregar mi primer libro</button>
        </div>
      ) : (
        <div className="row g-4">
          {librosFiltrados.map(libro => (
            <div key={libro.id} className="col-sm-6 col-md-4 col-lg-3">
              <div className="book-card">
                <div className="book-cover">
                  {libro.portada_url ? (
                    <img src={libro.portada_url} alt={libro.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    <div className="d-flex align-items-center justify-content-center h-100 text-muted" style={{ fontSize: '3rem' }}>📚</div>
                  )}
                  <span className={`badge position-absolute top-0 end-0 m-2 badge-estado-${libro.estado.toLowerCase()}`}>{libro.estado}</span>
                </div>
                <div className="book-info p-3">
                  <h6 className="text-white mb-1 text-truncate">{libro.titulo}</h6>
                  <p className="text-muted small mb-2">{libro.autor}</p>
                  {libro.estrellas > 0 && (
                    <div className="text-warning small mb-2">{'⭐'.repeat(libro.estrellas)}</div>
                  )}
                  <div className="d-flex gap-2 mt-2">
                    <button onClick={() => setEditando(libro)} className="btn btn-sm btn-outline-secondary flex-fill">Editar</button>
                    <Link href={`/diario?libroId=${libro.id}`} className="btn btn-sm btn-outline-secondary">📓</Link>
                    <button onClick={() => eliminarLibro(libro.id)} className="btn btn-sm btn-outline-danger">🗑</button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal: Agregar libro */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content-custom" onClick={e => e.stopPropagation()}>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h4 className="font-title">Agregar Libro</h4>
              <button onClick={() => setShowModal(false)} className="btn-close btn-close-white"></button>
            </div>
            <div className="position-relative mb-3">
              <label className="form-label text-muted">Buscar en Google Books</label>
              <input
                type="text"
                className="form-control bg-input border-0 text-white"
                placeholder="Buscar por título..."
                value={busqueda}
                onChange={e => { setBusqueda(e.target.value); buscarLibros(e.target.value) }}
              />
              {sugerencias.length > 0 && (
                <div className="position-absolute w-100 z-50" style={{ background: '#2c2724', border: '1px solid rgba(212,175,55,0.3)', borderRadius: 8, top: '100%', zIndex: 999 }}>
                  {sugerencias.map((s, i) => (
                    <button key={i} onClick={() => seleccionarSugerencia(s)}
                      className="d-flex gap-2 align-items-center w-100 text-start p-2 btn btn-link text-white border-0">
                      {s.portada && <img src={s.portada} alt={s.titulo} style={{ height: 50 }} />}
                      <div>
                        <div className="fw-bold">{s.titulo}</div>
                        <div className="text-muted small">{s.autor}</div>
                      </div>
                    </button>
                  ))}
                </div>
              )}
            </div>
            <form id="formNuevo" onSubmit={agregarLibro}>
              <div className="row g-3">
                <div className="col-12">
                  <label className="form-label text-muted">Título *</label>
                  <input name="titulo" type="text" className="form-control bg-input border-0 text-white" required />
                </div>
                <div className="col-12">
                  <label className="form-label text-muted">Autor *</label>
                  <input name="autor" type="text" className="form-control bg-input border-0 text-white" required />
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Año</label>
                  <input name="anio" type="number" className="form-control bg-input border-0 text-white" />
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Páginas</label>
                  <input name="paginas" type="number" className="form-control bg-input border-0 text-white" />
                </div>
                <div className="col-12">
                  <label className="form-label text-muted">URL de portada</label>
                  <input name="portada_url" type="text" className="form-control bg-input border-0 text-white" />
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Estado</label>
                  <select name="estado" className="form-select bg-input border-0 text-white">
                    {ESTADOS.map(e => <option key={e} value={e}>{e}</option>)}
                  </select>
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Género</label>
                  <select name="genero" className="form-select bg-input border-0 text-white">
                    <option value="">Sin género</option>
                    {GENEROS.map(g => <option key={g} value={g}>{g}</option>)}
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label text-muted">Mood</label>
                  <select name="mood" className="form-select bg-input border-0 text-white">
                    <option value="">Sin mood</option>
                    {MOODS.map(m => <option key={m} value={m}>{m}</option>)}
                  </select>
                </div>
              </div>
              <button type="submit" className="btn-gold w-100 mt-4">Agregar libro</button>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Editar libro */}
      {editando && (
        <div className="modal-overlay" onClick={() => setEditando(null)}>
          <div className="modal-content-custom" onClick={e => e.stopPropagation()}>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h4 className="font-title">Editar: {editando.titulo}</h4>
              <button onClick={() => setEditando(null)} className="btn-close btn-close-white"></button>
            </div>
            <form onSubmit={editarLibro}>
              <div className="row g-3">
                <div className="col-6">
                  <label className="form-label text-muted">Estado</label>
                  <select name="estado" defaultValue={editando.estado} className="form-select bg-input border-0 text-white">
                    {ESTADOS.map(e => <option key={e} value={e}>{e}</option>)}
                  </select>
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Estrellas</label>
                  <select name="estrellas" defaultValue={editando.estrellas} className="form-select bg-input border-0 text-white">
                    {[0,1,2,3,4,5].map(n => <option key={n} value={n}>{n > 0 ? '⭐'.repeat(n) : 'Sin calificar'}</option>)}
                  </select>
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Género</label>
                  <select name="genero" defaultValue={editando.genero || ''} className="form-select bg-input border-0 text-white">
                    <option value="">Sin género</option>
                    {GENEROS.map(g => <option key={g} value={g}>{g}</option>)}
                  </select>
                </div>
                <div className="col-6">
                  <label className="form-label text-muted">Mood</label>
                  <select name="mood" defaultValue={editando.mood || ''} className="form-select bg-input border-0 text-white">
                    <option value="">Sin mood</option>
                    {MOODS.map(m => <option key={m} value={m}>{m}</option>)}
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label text-muted">Reseña</label>
                  <textarea name="resena" defaultValue={editando.resena || ''} rows={4} className="form-control bg-input border-0 text-white" />
                </div>
              </div>
              <button type="submit" className="btn-gold w-100 mt-4">Guardar cambios</button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
