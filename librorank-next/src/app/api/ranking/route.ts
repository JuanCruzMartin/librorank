import { NextRequest, NextResponse } from 'next/server'
import { getAuthUserFromRequest } from '@/lib/auth'
import { obtenerRankingLectores, getTituloLector } from '@/lib/dao/usuarioDAO'
import { obtenerIdsAmigos } from '@/lib/dao/amigoDAO'

export async function GET(req: NextRequest) {
  const user = await getAuthUserFromRequest(req)
  if (!user) return NextResponse.json({ error: 'No autenticado' }, { status: 401 })

  const ranking = await obtenerRankingLectores(50)
  const idsAmigos = await obtenerIdsAmigos(user.id)

  const rankingConTitulo = ranking.map((u, i) => ({
    ...u,
    posicion: i + 1,
    titulo_lector: getTituloLector(u.total_leidos ?? 0),
    es_amigo: idsAmigos.includes(u.id),
    es_yo: u.id === user.id,
  }))

  return NextResponse.json(rankingConTitulo)
}
