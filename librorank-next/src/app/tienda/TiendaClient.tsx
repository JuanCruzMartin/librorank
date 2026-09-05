'use client'

import { useState } from 'react'
import type { ItemTienda } from '@/lib/tienda'
import type { Carta } from '@/lib/cartas'
import { RAREZAS } from '@/lib/cartas'

interface CartaResultado {
  carta: Carta
  esNueva: boolean
}

interface Props {
  items: ItemTienda[]
  puntosIniciales: number
}

export default function TiendaClient({ items, puntosIniciales }: Props) {
  const [puntos, setPuntos] = useState(puntosIniciales)
  const [comprando, setComprando] = useState<string | null>(null)
  const [resultado, setResultado] = useState<CartaResultado[] | null>(null)
  const [itemAbierto, setItemAbierto] = useState<ItemTienda | null>(null)
  const [cartasReveladas, setCartasReveladas] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function comprar(item: ItemTienda) {
    if (puntos < item.precio || comprando) return
    setComprando(item.id)
    setError(null)
    try {
      const res = await fetch('/api/tienda', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ itemId: item.id }),
      })
      const data = await res.json()
      if (!res.ok) { setError(data.error || 'Error al comprar'); return }
      setPuntos(data.monedasRestantes)
      setItemAbierto(item)
      setResultado(data.cartas)
      setCartasReveladas(false)
      // Auto-revelar después de 600ms (animación de sobre)
      setTimeout(() => setCartasReveladas(true), 800)
    } catch {
      setError('Error de conexión')
    } finally {
      setComprando(null)
    }
  }

  function cerrarResultado() {
    setResultado(null)
    setItemAbierto(null)
    setCartasReveladas(false)
  }

  const rareza = (r: string) => RAREZAS[r as keyof typeof RAREZAS] ?? RAREZAS.comun

  return (
    <>
      <style suppressHydrationWarning>{`
        @keyframes sobreEntrada {
          from { opacity: 0; transform: translateY(30px) scale(0.92); }
          to   { opacity: 1; transform: translateY(0) scale(1); }
        }
        @keyframes cartaAparecer {
          0%   { opacity: 0; transform: translateY(40px) scale(0.8) rotateY(90deg); }
          60%  { transform: translateY(-6px) scale(1.04) rotateY(0deg); }
          100% { opacity: 1; transform: translateY(0) scale(1) rotateY(0deg); }
        }
        @keyframes brilloNueva {
          0%, 100% { box-shadow: 0 0 12px 4px rgba(255,215,0,0.5); }
          50%       { box-shadow: 0 0 28px 10px rgba(255,215,0,0.9); }
        }
        @keyframes pulse-glow {
          0%, 100% { opacity: 0.6; }
          50%       { opacity: 1; }
        }
      `}</style>

      <div style={{ maxWidth: 900, margin: '0 auto' }}>

        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h1 className="font-title" style={{ fontSize: 'clamp(1.8rem,5vw,2.8rem)', color: '#fff', margin: 0 }}>
            🏪 Tienda
          </h1>
          <p style={{ color: 'rgba(255,255,255,0.5)', marginTop: '0.4rem', fontSize: '0.9rem' }}>
            Abrí sobres y expandí tu colección
          </p>
          <div style={{
            display: 'inline-flex', alignItems: 'center', gap: 8,
            background: 'rgba(212,175,55,0.12)', border: '1px solid rgba(212,175,55,0.3)',
            borderRadius: 20, padding: '0.4rem 1.2rem', marginTop: '0.75rem',
          }}>
            <span style={{ fontSize: '1rem' }}>🪙</span>
            <span style={{ color: '#d4af37', fontWeight: 700, fontSize: '1.1rem' }}>
              {puntos.toLocaleString()} puntos
            </span>
          </div>
        </div>

        {error && (
          <div style={{ background: 'rgba(231,76,60,0.15)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: 10, padding: '0.75rem 1rem', color: '#e74c3c', fontSize: '0.85rem', marginBottom: '1rem', textAlign: 'center' }}>
            {error}
          </div>
        )}

        {/* Grid de sobres */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))',
          gap: '1.5rem',
        }}>
          {items.map((item, i) => {
            const puedeComprar = puntos >= item.precio
            const estaCargando = comprando === item.id
            return (
              <div
                key={item.id}
                style={{
                  background: `linear-gradient(160deg, rgba(255,255,255,0.06) 0%, rgba(255,255,255,0.02) 100%)`,
                  border: `1px solid ${puedeComprar ? item.color + '60' : 'rgba(255,255,255,0.08)'}`,
                  borderRadius: 18,
                  overflow: 'hidden',
                  animation: `sobreEntrada 0.5s ease both`,
                  animationDelay: `${i * 0.07}s`,
                  transition: 'transform 0.2s, border-color 0.2s',
                }}
              >
                {/* Imagen del sobre */}
                <div style={{ position: 'relative', aspectRatio: '5/7', overflow: 'hidden' }}>
                  <img
                    src={item.imagen}
                    alt={item.nombre}
                    style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
                  />
                  {/* Precio badge */}
                  <div style={{
                    position: 'absolute', top: 10, right: 10,
                    background: 'rgba(0,0,0,0.75)', backdropFilter: 'blur(6px)',
                    borderRadius: 20, padding: '0.3rem 0.75rem',
                    display: 'flex', alignItems: 'center', gap: 5,
                    border: `1px solid ${item.color}50`,
                  }}>
                    <span style={{ fontSize: '0.75rem' }}>🪙</span>
                    <span style={{ color: '#d4af37', fontWeight: 700, fontSize: '0.82rem' }}>
                      {item.precio.toLocaleString()}
                    </span>
                  </div>
                  {/* Overlay si no puede comprar */}
                  {!puedeComprar && (
                    <div style={{
                      position: 'absolute', inset: 0,
                      background: 'rgba(0,0,0,0.55)',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}>
                      <span style={{ fontSize: '2rem', opacity: 0.7 }}>🔒</span>
                    </div>
                  )}
                </div>

                {/* Info + botón */}
                <div style={{ padding: '1rem' }}>
                  <h3 style={{ color: '#fff', fontSize: '0.92rem', fontWeight: 700, margin: '0 0 0.25rem' }}>
                    {item.nombre}
                  </h3>
                  <p style={{ color: 'rgba(255,255,255,0.45)', fontSize: '0.75rem', margin: '0 0 0.85rem', lineHeight: 1.4 }}>
                    {item.descripcion}
                  </p>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: '0.75rem' }}>
                    {[...Array(5)].map((_, k) => (
                      <div key={k} style={{ width: 28, height: 38, borderRadius: 4, background: item.color + '30', border: `1px solid ${item.color}50`, fontSize: '0.9rem', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>🎴</div>
                    ))}
                  </div>
                  <button
                    onClick={() => comprar(item)}
                    disabled={!puedeComprar || !!comprando}
                    style={{
                      width: '100%', padding: '0.6rem', borderRadius: 10, border: 'none',
                      cursor: puedeComprar && !comprando ? 'pointer' : 'not-allowed',
                      background: puedeComprar
                        ? `linear-gradient(135deg, ${item.color}, ${item.colorSecundario})`
                        : 'rgba(255,255,255,0.06)',
                      color: puedeComprar ? '#fff' : 'rgba(255,255,255,0.3)',
                      fontWeight: 700, fontSize: '0.85rem',
                      transition: 'opacity 0.2s',
                      opacity: estaCargando ? 0.7 : 1,
                    }}
                  >
                    {estaCargando ? 'Abriendo...' : puedeComprar ? '✨ Abrir sobre' : 'Puntos insuficientes'}
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      </div>

      {/* Modal de resultado */}
      {resultado && itemAbierto && (
        <div
          onClick={cerrarResultado}
          style={{
            position: 'fixed', inset: 0, zIndex: 9000,
            background: 'rgba(0,0,0,0.88)', backdropFilter: 'blur(8px)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
            padding: '1rem', cursor: 'pointer',
          }}
        >
          <div onClick={e => e.stopPropagation()} style={{ cursor: 'default', width: '100%', maxWidth: 700 }}>
            <h2 className="font-title" style={{ color: '#fff', textAlign: 'center', marginBottom: '0.25rem', fontSize: 'clamp(1.2rem,4vw,1.8rem)' }}>
              ¡{itemAbierto.nombre} abierto!
            </h2>
            <p style={{ color: 'rgba(255,255,255,0.4)', textAlign: 'center', fontSize: '0.8rem', marginBottom: '1.5rem' }}>
              {resultado.filter(r => r.esNueva).length} cartas nuevas · {resultado.filter(r => !r.esNueva).length} duplicadas
            </p>

            {/* 5 cartas */}
            <div style={{
              display: 'flex', gap: '0.75rem', justifyContent: 'center',
              flexWrap: 'wrap',
            }}>
              {resultado.map((r, i) => {
                const rar = rareza(r.carta.rareza)
                return (
                  <div
                    key={i}
                    style={{
                      width: 120, flexShrink: 0,
                      animation: cartasReveladas ? `cartaAparecer 0.6s ease both` : 'none',
                      animationDelay: cartasReveladas ? `${i * 0.12}s` : '0s',
                      opacity: cartasReveladas ? 1 : 0,
                    }}
                  >
                    {/* Carta */}
                    <div style={{
                      borderRadius: 10, overflow: 'hidden',
                      border: `2px solid ${rar.color}`,
                      boxShadow: r.esNueva
                        ? `0 0 16px 4px ${rar.color}80, 0 0 0 2px gold`
                        : `0 4px 16px rgba(0,0,0,0.5)`,
                      animation: r.esNueva && cartasReveladas ? `brilloNueva 1.5s ease infinite` : 'none',
                      animationDelay: `${i * 0.12 + 0.6}s`,
                      position: 'relative',
                    }}>
                      <img
                        src={r.carta.imagen}
                        alt={r.carta.nombre}
                        style={{ width: '100%', aspectRatio: '3/4', objectFit: 'cover', display: 'block' }}
                        onError={e => { (e.target as HTMLImageElement).src = '/img/card-placeholder.png' }}
                      />
                      {/* Badge rareza */}
                      <div style={{
                        position: 'absolute', top: 5, left: 5,
                        background: rar.color, borderRadius: 4,
                        padding: '1px 5px', fontSize: '0.6rem', fontWeight: 700, color: '#fff',
                      }}>
                        {rar.letra}
                      </div>
                      {/* Nueva badge */}
                      {r.esNueva && (
                        <div style={{
                          position: 'absolute', top: 5, right: 5,
                          background: 'gold', borderRadius: 4,
                          padding: '1px 5px', fontSize: '0.55rem', fontWeight: 900, color: '#000',
                        }}>
                          NEW
                        </div>
                      )}
                    </div>
                    <p style={{ color: '#fff', fontSize: '0.65rem', textAlign: 'center', marginTop: 5, fontWeight: 600, lineHeight: 1.3 }}>
                      {r.carta.nombre}
                    </p>
                    {!r.esNueva && (
                      <p style={{ color: 'rgba(255,255,255,0.3)', fontSize: '0.6rem', textAlign: 'center', margin: 0 }}>
                        duplicada
                      </p>
                    )}
                  </div>
                )
              })}
            </div>

            <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
              <button
                onClick={cerrarResultado}
                style={{
                  padding: '0.6rem 2rem', borderRadius: 10, border: 'none', cursor: 'pointer',
                  background: 'rgba(255,255,255,0.1)', color: '#fff',
                  fontWeight: 600, fontSize: '0.85rem',
                }}
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
