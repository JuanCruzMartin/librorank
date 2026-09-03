'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

export default function LoginPage() {
  const router = useRouter()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setLoading(true)
    setError('')

    const fd = new FormData(e.currentTarget)
    const data = { identificador: fd.get('identificador'), password: fd.get('password') }

    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })

    const json = await res.json()
    setLoading(false)

    if (!res.ok) {
      setError(json.error || 'Error al iniciar sesión')
    } else {
      router.push(json.redirect || '/home')
      router.refresh()
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <Link href="/" className="logo text-decoration-none">Libro<span>Rank</span></Link>
          <h1 className="auth-title">Bienvenido de vuelta</h1>
          <p className="auth-subtitle">Ingresá a tu cuenta para continuar tu aventura lectora.</p>
        </div>

        {error && (
          <div className="alert alert-danger" role="alert">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="mb-3">
            <label className="form-label text-muted">Email o Usuario</label>
            <input name="identificador" type="text" className="form-control bg-input border-0 text-white"
              placeholder="Tu email o username" required />
          </div>
          <div className="mb-4">
            <label className="form-label text-muted">Contraseña</label>
            <input name="password" type="password" className="form-control bg-input border-0 text-white"
              placeholder="Tu contraseña" required />
          </div>
          <button type="submit" className="btn-auth w-100" disabled={loading}>
            {loading ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>

        <p className="text-center text-muted mt-4 small">
          ¿No tenés cuenta?{' '}
          <Link href="/signup" className="text-gold text-decoration-none fw-bold">Registrate gratis</Link>
        </p>
      </div>
    </div>
  )
}
