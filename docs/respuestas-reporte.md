# Street Report — datos para completar el reporte

Datos consultados el 18/11/2026 en Azure (CLI), GitHub (repositorio y Actions) y la URL pública.

## Correcciones importantes

1. **La base de datos es Azure SQL Database, no PostgreSQL.** El servidor se llama `psql-street-report`, pero es Azure SQL. Corregir la introducción, el marco teórico y la tabla de recursos.
2. **El login, los roles y el panel de administración no se implementaron.** Si aparecen como hechos, pasarlos a *Trabajos futuros*.

## Motor de base de datos

- **Motor:** Azure SQL Database (SQL Server). El backend usa el driver `mssql-jdbc`.
- **Nivel:** General Purpose **serverless**, Gen5 (`GP_S_Gen5_1`): hasta 1 vCore, mínimo 0,5 vCore.
- **Pausa automática:** después de 60 minutos sin uso.
- **Tamaño máximo:** 32 GB.
- **Servidor:** `psql-street-report.database.windows.net`
- **Base de datos:** `reportes`

## Tabla de recursos de Azure

| Recurso | Nombre | Detalle |
|---|---|---|
| Suscripción | Azure for Students | |
| Región | Chile Central (`chilecentral`) | Todos los recursos están en esta región |
| Grupo de recursos | `rg-street-report` | |
| App Service | `street-report-app` | Linux, Java 17 y solo HTTPS |
| Plan de App Service | `ASP-rgstreetreport-867d` | **F1 (Free)**, no B1 |
| SQL Server y base de datos | `psql-street-report` / `reportes` | GP_S_Gen5_1, serverless |
| Cuenta de almacenamiento | `ststreetreport` | StorageV2, Standard_LRS, contenedor `fotos` |
| Identidad administrada | `id-github-street-report` | La usa GitHub Actions para iniciar sesión por OIDC |

## Costos (Cost Management, noviembre hasta el 18/11)

| Servicio | Costo |
|---|---|
| Azure SQL (`microsoft.sql/servers`) | 4,27 USD |
| Storage | menos de 0,01 USD |
| App Service (plan F1) | 0,00 USD |
| **Total** | **unos 4,27 USD** |

- **Presupuesto:** no hay ninguno configurado en Cost Management. La suscripción es Azure for Students, que funciona con un crédito.

## GitHub Actions: ejecución fallida

- **Flujo:** `Desplegar en Azure App Service` (`.github/workflows/deploy-azure.yml`).
- **Ejecución fallida:** 16/11/2026, commit *"ci: agregar despliegue a Azure App Service y driver de Azure SQL"*. El job `build` pasó y el job `deploy` falló en el paso **"Iniciar sesión en Azure (OIDC)"**.
- **Error:** `Login failed with Error: Using auth-type: SERVICE_PRINCIPAL. Not all values are present. Ensure 'client-id' and 'tenant-id' are supplied.`
- **Causa:** el repositorio no tenía los secretos `AZURE_CLIENT_ID`, `AZURE_TENANT_ID` y `AZURE_SUBSCRIPTION_ID`.
- **Corrección:** se creó la identidad administrada `id-github-street-report` con una credencial federada para el repositorio, en el environment `production`. Después se agregaron los tres secretos al repo y el flujo se relanzó a mano (`workflow_dispatch`).
- **Resultado:** la ejecución manual salió bien en 39 s. El merge de `feature/fotos` (PR #1) también se desplegó bien, en 1 min 43 s.
- **Pruebas en CI:** 10 en total (5 de `ReporteControllerTest` y 5 de `FotoControllerTest`), sin fallos.

## URL pública

- https://street-report-app-gfftgxgvczdtbrb4.chilecentral-01.azurewebsites.net

| Prueba (18/11) | Resultado |
|---|---|
| `GET /api/health` | 200 `{"reportes":6,"status":"ok"}` |
| `GET /api/reportes` (listado) | 200, 6 reportes, 1 de ellos con foto |
| `GET /` (página principal) | 200 |

- **Ojo:** la primera petición tardó unos **30 s**, porque el plan F1 y la base serverless se pausan cuando no hay uso. Conviene mencionarlo en el reporte.

## CRediT (según el historial de commits)

| Integrante | Aportes |
|---|---|
| Juan (B) | Estructura inicial, módulo de reportes (backend y frontend) y limpieza del repositorio |
| Oscar (A) | Infraestructura en Azure, flujo de despliegue con GitHub Actions y OIDC, cambio a Azure SQL y subida de fotos a Blob Storage |

## Uso de IA

- El guion de la presentación se generó con **Claude Opus 5.5** (Anthropic).
- Falta completar el modelo que se usó para redactar el reporte y si se usó IA en el código.
