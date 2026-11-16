// Guarda el token de sesión que devuelve el login de A (POST /api/auth/login).
// Mientras A no tenga el login, no hay token y las llamadas funcionan igual (sin cabecera Authorization).
const CLAVE = 'street-report-token';

export function getToken() {
  try {
    return localStorage.getItem(CLAVE);
  } catch {
    return null;
  }
}

export function setToken(token) {
  try {
    localStorage.setItem(CLAVE, token);
  } catch {
    /* almacenamiento no disponible */
  }
}

export function clearToken() {
  try {
    localStorage.removeItem(CLAVE);
  } catch {
    /* almacenamiento no disponible */
  }
}
