// Todas las llamadas al backend pasan por aquí. Las rutas están definidas en docs/contrato.md.
import { getToken } from './auth';

const BASE = '/api';

function cabeceras(extra = {}) {
  const cab = { ...extra };
  const token = getToken();
  if (token) cab.Authorization = `Bearer ${token}`;
  return cab;
}

async function manejarRespuesta(res) {
  if (!res.ok) {
    let data = null;
    try {
      data = await res.json();
    } catch {
      /* la respuesta no era JSON */
    }
    const error = new Error(data?.error || `Error ${res.status}`);
    error.campos = data?.campos;
    error.status = res.status;
    throw error;
  }
  return res.status === 204 ? null : res.json();
}

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    headers: cabeceras({ 'Content-Type': 'application/json' }),
  });
  return manejarRespuesta(res);
}

export function listarReportes({ estado, categoria, q } = {}) {
  const params = new URLSearchParams();
  if (estado) params.set('estado', estado);
  if (categoria) params.set('categoria', categoria);
  if (q && q.trim()) params.set('q', q.trim());
  const query = params.toString();
  return request(`/reportes${query ? `?${query}` : ''}`);
}

export function obtenerReporte(id) {
  return request(`/reportes/${id}`);
}

export function crearReporte(datos) {
  return request('/reportes', { method: 'POST', body: JSON.stringify(datos) });
}

// Lo usará el panel de administración (módulo de A).
export function cambiarEstado(id, estado) {
  return request(`/reportes/${id}/estado`, { method: 'PATCH', body: JSON.stringify({ estado }) });
}

// Módulo de A: POST /api/fotos recibe multipart (campo "archivo") y devuelve { url }.
export async function subirFoto(archivo) {
  const formulario = new FormData();
  formulario.append('archivo', archivo);
  // Sin Content-Type: el navegador lo define con el boundary del multipart
  const res = await fetch(`${BASE}/fotos`, { method: 'POST', headers: cabeceras(), body: formulario });
  if (res.status === 404) {
    throw new Error('el servicio de fotos todavía no está disponible');
  }
  return manejarRespuesta(res);
}
