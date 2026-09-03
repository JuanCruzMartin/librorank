import { NextRequest, NextResponse } from 'next/server'
import { getAuthUserFromRequest } from '@/lib/auth'
import * as bingoDAO from '@/lib/dao/bingoDAO'
import * as libroDAO from '@/lib/dao/libroDAO'

export async function GET(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const [bingo, misLibros] = await Promise.all([
    bingoDAO.obtenerBingo(user.id),
    libroDAO.buscarPorUsuario(user.id),
  ])

  return NextResponse.json({ bingo, misLibros })
}

export async function POST(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const body = await req.json()
  const { retoId, libroId } = body

  if (!retoId || !libroId) return NextResponse.json({ error: 'retoId y libroId requeridos' }, { status: 400 })

  const ok = await bingoDAO.marcarCasilla(user.id, Number(retoId), Number(libroId))
  return NextResponse.json({ ok })
}
