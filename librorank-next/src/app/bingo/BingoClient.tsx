'use client'

import { useState } from 'react'
import type { BingoCasilla } from '@/lib/dao/bingoDAO'
import type { Libro } from '@/lib/dao/libroDAO'

interface Props {
  bingo: BingoCasilla[]
  misLibros: Libro[]
}

export default function BingoClient({ bingo: bingoIni, misLibros }: Props) {
  const [bingo, setBingo] = useState(bingoIni)
  const [seleccionada, setSeleccionada] = useState<BingoCasilla | null>(null)
  const [libroSeleccionado, setLibroSeleccionado] = useState('')

  const completadas = bingo.filter(c => c.completado).length
  const totalCasillas = bingo.length
  const pct = totalCasillas > 0 ? Math.round((completadas / totalCasillas) * 100) : 0

  async function marcarCasilla() {
    if (!seleccionada || !libroSeleccionado) return
    const res = await fetch('/api/bingo', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ retoId: seleccionada.id, libroId: libroSeleccionado }),
    })
    if (res.ok) {
      setBingo(prev => prev.map(c =>
        c.id === seleccionada.id ? { ...c, completado: true, libro_id: Number(libroSeleccionado) } : c
      ))
      setSeleccionada(null)
    }
  }

  const grid: (BingoCasilla | undefined)[] = Array(25).fill(undefined)
  bingo.forEach(c => { grid[c.posicion] = c })

  return (
    <div className="container py-5">
      <div className="d-flex justify-content-between align-items-center mb-5">
        <div>
          <h1 className="font-title display-5 mb-1">🎲 Bingo Literario</h1>
          <p className="text-muted">Completá casillas al cumplir retos de lectura.</p>
        </div>
        <div className="text-end">
          <div className="fw-bold text-gold fs-3">{completadas}/{totalCasillas}</div>
          <div className="text-muted small">casillas completadas</div>
          <div className="progress mt-2" style={{ width: 150, height: 8 }}>
            <div className="progress-bar bg-warning" style={{ width: `${pct}%` }}></div>
          </div>
        </div>
      </div>

      {/* Grilla 5x5 */}
      <div className="mb-5">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '0.75rem', maxWidth: 700, margin: '0 auto' }}>
          {grid.map((casilla, i) => {
            if (!casilla) return <div key={i} className="card" style={{ aspectRatio: '1', minHeight: 80 }}></div>
            return (
              <button
                key={casilla.id}
                onClick={() => !casilla.completado && setSeleccionada(casilla)}
                disabled={casilla.completado}
                className={`card p-2 text-center border-0 ${casilla.completado ? 'bingo-completada' : 'bingo-pendiente'}`}
                style={{
                  aspectRatio: '1',
                  minHeight: 80,
                  background: casilla.completado ? 'rgba(212,175,55,0.2)' : '#2c2724',
                  border: casilla.completado ? '2px solid var(--accent-gold)' : '1px solid rgba(212,175,55,0.2)',
                  cursor: casilla.completado ? 'default' : 'pointer',
                  transition: 'all 0.2s',
                }}
              >
                {casilla.completado && <div style={{ fontSize: '1.5rem' }}>✅</div>}
                <div className="small text-white" style={{ fontSize: '0.7rem', lineHeight: 1.2 }}>{casilla.titulo_reto}</div>
              </button>
            )
          })}
        </div>
      </div>

      {/* Modal de completar casilla */}
      {seleccionada && (
        <div className="modal-overlay" onClick={() => setSeleccionada(null)}>
          <div className="modal-content-custom" onClick={e => e.stopPropagation()}>
            <h5 className="font-title mb-3">Completar: {seleccionada.titulo_reto}</h5>
            <p className="text-muted mb-4">Seleccioná el libro con el que cumpliste este reto:</p>
            <select
              className="form-select bg-input border-0 text-white mb-4"
              value={libroSeleccionado}
              onChange={e => setLibroSeleccionado(e.target.value)}
            >
              <option value="">-- Seleccionar libro --</option>
              {misLibros.filter(l => l.estado === 'LEIDO').map(l => (
                <option key={l.id} value={l.id}>{l.titulo}</option>
              ))}
            </select>
            <div className="d-flex gap-2">
              <button onClick={marcarCasilla} disabled={!libroSeleccionado} className="btn-gold flex-fill">Marcar como completada</button>
              <button onClick={() => setSeleccionada(null)} className="btn btn-outline-secondary">Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
