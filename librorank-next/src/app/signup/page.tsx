'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

export default function SignupPage() {
  const router = useRouter()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setLoading(true)
    setError('')

    const fd = new FormData(e.currentTarget)
    const data = {
      nombre: fd.get('nombre'),
      usuario: fd.get('usuario'),
      email: fd.get('email'),
      password: fd.get('password'),
      password2: fd.get('password2'),
    }

    const res = await fetch('/api/auth/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })

    const json = await res.json()
    setLoading(false)

    if (!res.ok) {
      setError(json.error || 'Error al registrarse')
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
          <h1 className="auth-title">Crea tu cuenta</h1>
          <p className="auth-subtitle">Únete a la comunidad de lectores más épica.</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="row g-3 mb-3">
            <div className="col-sm-6">
              <label className="form-label text-muted">Nombre completo</label>
              <input name="nombre" type="text" className="form-control bg-input border-0 text-white" placeholder="Tu nombre" required />
            </div>
            <div className="col-sm-6">
              <label className="form-label text-muted">Nombre de usuario</label>
              <input name="usuario" type="text" className="form-control bg-input border-0 text-white" placeholder="@username" required />
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label text-muted">Email</label>
            <input name="email" type="email" className="form-control bg-input border-0 text-white" placeholder="tu@email.com" required />
          </div>
          <div className="row g-3 mb-4">
            <div className="col-sm-6">
              <label className="form-label text-muted">Contraseña</label>
              <input name="password" type="password" className="form-control bg-input border-0 text-white" placeholder="Mínimo 6 caracteres" required minLength={6} />
            </div>
            <div className="col-sm-6">
              <label className="form-label text-muted">Confirmar contraseña</label>
              <input name="password2" type="password" className="form-control bg-input border-0 text-white" placeholder="Repetí la contraseña" required />
            </div>
          </div>
          <div className="mb-4 form-check">
            <input type="checkbox" className="form-check-input" id="terms" name="terms" required />
            <label className="form-check-label text-muted small" htmlFor="terms">
              Acepto los <a href="#" className="text-gold">Términos y Condiciones</a>
            </label>
          </div>
          <button type="submit" className="btn-auth w-100" disabled={loading}>
            {loading ? 'Creando cuenta...' : 'Crear cuenta gratis'}
          </button>
        </form>

        <p className="text-center text-muted mt-4 small">
          ¿Ya tenés cuenta?{' '}
          <Link href="/login" className="text-gold text-decoration-none fw-bold">Ingresá acá</Link>
        </p>
      </div>
    </div>
  )
}
