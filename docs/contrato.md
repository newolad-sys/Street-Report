# Contrato técnico de Street Report (A y B)

Acuerdo entre los dos integrantes para que los módulos encajen al unirlos. Si algo cambia, se actualiza este archivo y se avisa al otro.

**Stack:** Spring Boot 3 (Java 17) + React (Vite) + PostgreSQL. Un solo App Service sirve la API y el frontend compilado, así que no hay CORS en producción.

## Responsabilidades

| Módulo | Responsable | Estado |
|---|---|---|
| Reportes: crear, listar con filtros, detalle, cambio de estado | B | Hecho en la Fase 1 |
| Pantallas de reportes (formulario, listado, filtros) | B | Hecho en la Fase 1 |
| Usuarios, registro/login, roles | A | Pendiente |
| Panel de administración | A | Pendiente |
| Subida de fotos a Blob Storage (`/api/fotos`) | A | Hecho |
| Infraestructura Azure y despliegue automático | A | Pendiente |

## Modelo de datos

### `reportes` (módulo B, ya implementado)

| Campo | Tipo | Notas |
|---|---|---|
| id | bigint, PK, autoincremental | |
| titulo | varchar(150) | obligatorio |
| descripcion | varchar(2000) | obligatorio |
| categoria | varchar(20) | `BACHE`, `ALUMBRADO`, `BASURA`, `SEGURIDAD`, `OTRO` |
| estado | varchar(20) | `PENDIENTE` (por defecto), `EN_PROCESO`, `RESUELTO` |
| direccion | varchar(250) | obligatorio |
| latitud, longitud | double | opcionales |
| foto_url | varchar(500) | opcional; URL de la foto en Blob Storage |
| usuario_id | bigint | opcional hasta integrar el login; luego lo llena el backend con el usuario autenticado |
| creado_en, actualizado_en | timestamp | automáticos |

### `usuarios` (módulo A, propuesta)

| Campo | Tipo | Notas |
|---|---|---|
| id | bigint, PK | |
| nombre | varchar | |
| email | varchar, único | |
| password_hash | varchar | nunca guardar contraseñas en texto plano |
| rol | varchar | `CIUDADANO` o `ADMIN` |
| creado_en | timestamp | |

Cada integrante crea solo las tablas de su módulo. `ddl-auto=update` las genera a partir de las entidades.

## Endpoints

Todas las rutas empiezan por `/api`. Los errores devuelven JSON con la forma `{ "error": "mensaje" }`; los errores de validación agregan `"campos": { "titulo": "mensaje" }`.

### Reportes (B, implementados)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/health` | Verifica que la app y la BD estén arriba | 200 `{ "status": "ok", "reportes": 4 }` |
| GET | `/api/reportes` | Lista, del más reciente al más antiguo. Filtros opcionales: `estado`, `categoria`, `q` (busca en título, descripción y dirección) | 200, arreglo de reportes |
| GET | `/api/reportes/{id}` | Detalle | 200 o 404 |
| POST | `/api/reportes` | Crea un reporte (estado inicial `PENDIENTE`) | 201 con el reporte, 400 si hay datos inválidos |
| PATCH | `/api/reportes/{id}/estado` | Cambia el estado. Lo usa el panel admin; **A le agrega la restricción de rol `ADMIN`** | 200 o 404 |

Ejemplo de `POST /api/reportes`:

```json
{
  "titulo": "Hueco grande en la vía",
  "descripcion": "Ocupa casi todo el carril.",
  "categoria": "BACHE",
  "direccion": "Carrera 15 # 80-20",
  "latitud": null,
  "longitud": null,
  "fotoUrl": null
}
```

Ejemplo de `PATCH /api/reportes/{id}/estado`:

```json
{ "estado": "EN_PROCESO" }
```

### Autenticación (A, propuesta)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/registro` | Crea un usuario con rol `CIUDADANO` |
| POST | `/api/auth/login` | Devuelve `{ "token": "...", "usuario": { "id", "nombre", "rol" } }` |

El frontend envía `Authorization: Bearer <token>` en las rutas protegidas. Al integrar, B ajusta `frontend/src/api.js` para incluir el token y el backend toma `usuario_id` del token en `POST /api/reportes`.

### Fotos (A, implementado)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/fotos` | Recibe `multipart/form-data` con el campo `archivo` (imagen), la sube a Blob Storage y devuelve `{ "url": "https://..." }` | 201 con la URL; 400 si falta el archivo o no es JPG, PNG, WEBP o GIF; 413 si pesa más de 5 MB; 503 si Blob Storage no está configurado o falla |

El formato se reconoce por el contenido del archivo (no por su nombre) y la foto se guarda con un nombre único (`<uuid>.<ext>`) en el contenedor `AZURE_STORAGE_CONTAINER`, que tiene lectura pública a blobs.

Flujo en el formulario de B: sube la foto a `/api/fotos`, recibe la `url` y la envía como `fotoUrl` al crear el reporte.

## Variables de entorno

| Variable | Uso | Quién la configura |
|---|---|---|
| `DB_URL` | Cadena JDBC. En Azure: `jdbc:postgresql://<servidor>.postgres.database.azure.com:5432/<bd>?sslmode=require` | A, en App Service |
| `DB_USER`, `DB_PASSWORD` | Credenciales de la BD | A, en App Service |
| `SEED_DATA` | `true` carga 4 reportes de ejemplo si la tabla está vacía (útil para la demo) | A, en App Service |
| `AZURE_STORAGE_CONNECTION_STRING`, `AZURE_STORAGE_CONTAINER` | Blob Storage | A. Se comparte con B por mensaje privado, nunca en el repo |

## Reglas de trabajo

- Ramas: `main` siempre desplegable. B trabaja en `feature/reportes`, A en `feature/auth-admin`. Se integra con Pull Request y el otro lo revisa.
- Commits pequeños y frecuentes, con mensaje claro (`feat: filtros de reportes`, `fix: validación de título`).
- Cada uno toca solo sus carpetas/paquetes. Si hay que tocar un archivo compartido (`App.jsx`, `api.js`, `pom.xml`), se avisa antes.
- Ningún secreto en el repositorio. Solo `.env.example` sin valores reales.
