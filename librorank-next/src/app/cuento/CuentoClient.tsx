'use client'

import { useState } from 'react'
import type { FragmentoHistoria } from '@/lib/dao/cuentoDAO'

interface Props {
  fragmentos: FragmentoHistoria[]
  yaEscribio: boolean
  usuarioId: number
}

export default function CuentoClient({ fragmentos: fragmentosIni, yaEscribio: yaEscribioIni, usuarioId }: Props) {
  const [fragmentos, setFragmentos] = useState(fragmentosIni)
  const [yaEscribio, setYaEscribio] = useState(yaEscribioIni)
  const [contenido, setContenido] = useState('')
  const [error, setError] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(e: React.FormEvent) {
    e.preventDefault()
    if (!contenido.trim()) return
    setEnviando(true)
    setError('')

    const res = await fetch('/api/cuento', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ contenido }),
    })
    const data = await res.json()
    setEnviando(false)

    if (!res.ok) {
      setError(data.error)
    } else {
      setYaEscribio(true)
      window.location.reload()
    }
  }

  return (
    <div className="container py-5">
      <h1 className="font-title display-5 mb-2">✍️ El Gran Cuento</h1>
      <p className="text-muted mb-5">Una historia escrita entre todos los lectores de LibroRank. Cada uno aporta su hoja.</p>

      <div className="row g-4">
        {/* Historia */}
        <div className="col-lg-8">
          {fragmentos.length === 0 ? (
            <div className="card p-5 text-center text-muted">
              <p>La historia aún no ha comenzado. ¡Sé el primero en escribir!</p>
            </div>
          ) : (
            <div>
              {fragmentos.map(f => (
                <div key={f.id} className="card mb-4 p-4" style={{ borderLeft: '3px solid var(--accent-gold)' }}>
                  <div className="d-flex justify-content-between mb-3">
                    <span className="text-gold small fw-bold">Hoja #{f.numero_hoja}</span>
                    <span className="text-muted small">@{f.username}</span>
                  </div>
                  <p className="text-white" style={{ lineHeight: 1.8, whiteSpace: 'pre-wrap' }}>{f.contenido}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Panel de contribución */}
        <div className="col-lg-4">
          <div className="card p-4 sticky-top" style={{ top: '1rem' }}>
            <h5 className="font-title mb-3">Tu contribución</h5>
            {yaEscribio ? (
              <div className="text-center py-3">
                <div style={{ fontSize: '2rem' }}>✅</div>
                <p className="text-muted mt-2">Ya has contribuido a esta historia.</p>
                <p className="text-muted small">Cada lector solo puede agregar una hoja.</p>
              </div>
            ) : (
              <form onSubmit={enviar}>
                {error && <div className="alert alert-danger mb-3">{error}</div>}
                <div className="mb-3">
                  <textarea
                    value={contenido}
                    onChange={e => setContenido(e.target.value)}
                    rows={8}
                    maxLength={1000}
                    className="form-control bg-input border-0 text-white"
                    placeholder="Continuá la historia..."
                    required
                  />
                  <div className="text-end text-muted small mt-1">{contenido.length} / 1000</div>
                </div>
                <button type="submit" disabled={enviando || !contenido.trim()} className="btn-gold w-100">
                  {enviando ? 'Enviando...' : 'Agregar mi hoja'}
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
