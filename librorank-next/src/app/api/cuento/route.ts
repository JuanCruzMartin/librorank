import { NextRequest, NextResponse } from 'next/server'
import { getAuthUserFromRequest } from '@/lib/auth'
import * as cuentoDAO from '@/lib/dao/cuentoDAO'

export async function GET(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const historiaId = await cuentoDAO.obtenerOIdUnicaHistoria()
  const [fragmentos, yaEscribio] = await Promise.all([
    cuentoDAO.obtenerHistoriaCompleta(historiaId),
    cuentoDAO.haEscritoYa(historiaId, user.id),
  ])

  return NextResponse.json({ fragmentos, yaEscribio, historiaId })
}

export async function POST(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const body = await req.json()
  const { contenido } = body

  if (!contenido || contenido.trim().length === 0) {
    return NextResponse.json({ error: 'El contenido no puede estar vacío' }, { status: 400 })
  }

  if (contenido.length > 1000) {
    return NextResponse.json({ error: 'Máximo 1000 caracteres' }, { status: 400 })
  }

  const historiaId = await cuentoDAO.obtenerOIdUnicaHistoria()
  const yaEscribio = await cuentoDAO.haEscritoYa(historiaId, user.id)

  if (yaEscribio) {
    return NextResponse.json({ error: 'Ya has contribuido a esta historia' }, { status: 409 })
  }

  const ok = await cuentoDAO.guardarHoja(historiaId, user.id, contenido)
  return NextResponse.json({ ok })
}
