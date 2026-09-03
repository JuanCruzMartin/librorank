import { query, execute } from '@/lib/db'

export interface Logro {
  id: number
  nombre_key: string
  nombre: string
  descripcion: string
  icono: string
  desbloqueado: boolean
}

export async function obtenerLogrosUsuario(usuarioId: number): Promise<Logro[]> {
  return query<Logro>(
    `SELECT l.*, (ul.usuario_id IS NOT NULL) AS desbloqueado
     FROM logros l
     LEFT JOIN usuario_logros ul ON l.id=ul.logro_id AND ul.usuario_id=?
     ORDER BY desbloqueado DESC, l.id ASC`,
    [usuarioId]
  )
}

export async function obtenerLogrosKeys(usuarioId: number): Promise<Set<string>> {
  const rows = await query<{ nombre_key: string }>(
    `SELECT l.nombre_key FROM logros l
     JOIN usuario_logros ul ON l.id=ul.logro_id WHERE ul.usuario_id=?`,
    [usuarioId]
  )
  return new Set(rows.map(r => r.nombre_key))
}

export async function desbloquearLogro(usuarioId: number, logroKey: string): Promise<boolean> {
  const res = await execute(
    'INSERT IGNORE INTO usuario_logros (usuario_id, logro_id) SELECT ?, id FROM logros WHERE nombre_key=?',
    [usuarioId, logroKey]
  )
  return res.affectedRows > 0
}
