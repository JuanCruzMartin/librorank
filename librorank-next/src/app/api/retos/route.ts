import { NextRequest, NextResponse } from 'next/server'
import { getAuthUserFromRequest } from '@/lib/auth'
import * as retoDAO from '@/lib/dao/retoDAO'

export async function GET(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const retos = await retoDAO.obtenerRetosActivos(user.id)
  return NextResponse.json(retos)
}

export async function POST(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  try {
    const body = await req.json()
    const { accion } = body

    if (accion === 'crear') {
      const { nombre, libroId, fechaFin } = body
      if (!nombre || !fechaFin) return NextResponse.json({ error: 'Nombre y fecha requeridos' }, { status: 400 })
      const ok = await retoDAO.crearReto(user.id, nombre, libroId ? Number(libroId) : null, fechaFin)
      return NextResponse.json({ ok })
    }

    if (accion === 'unirse') {
      const { retoId } = body
      const ok = await retoDAO.unirseAReto(Number(retoId), user.id)
      return NextResponse.json({ ok })
    }

    if (accion === 'actualizar') {
      const { retoId, progreso } = body
      const ok = await retoDAO.actualizarProgreso(Number(retoId), user.id, Number(progreso))
      return NextResponse.json({ ok })
    }

    return NextResponse.json({ error: 'Acción no válida' }, { status: 400 })
  } catch (err) {
    console.error('Error en /api/retos:', err)
    return NextResponse.json({ error: 'Error interno' }, { status: 500 })
  }
}
