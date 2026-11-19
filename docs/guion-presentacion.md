# Guion de presentación — Street Report

**Duración objetivo:** 14 minutos (límite: 15).
**Presentadores:**
- **Oscar** (integrante A): infraestructura en Azure, base de datos Azure SQL, despliegue automático con GitHub Actions y fotos en Blob Storage.
- **Juan** (integrante B): módulo de reportes, que incluye la API REST y las pantallas en React.

Cada uno presenta lo que construyó. Así la división del trabajo se ve en el video y coincide con la tabla CRediT del reporte.

> **Datos verificados el 18/11/2026 en Azure y GitHub.** La base de datos es **Azure SQL Database serverless**, no PostgreSQL, aunque el servidor se llame `psql-street-report`. El login, los roles y el panel de administración **no se implementaron**: en este guion aparecen como trabajo futuro.

---

## Tiempos

| # | Sección | Quién | Inicio | Duración |
|---|---|---|---|---|
| 1 | Apertura | Oscar + Juan | 0:00 | 0:45 |
| 2 | Problema y justificación | Juan | 0:45 | 1:30 |
| 3 | Objetivos | Oscar | 2:15 | 0:45 |
| 4 | Marco teórico | Oscar → Juan | 3:00 | 1:30 |
| 5 | Arquitectura y componentes | Oscar → Juan | 4:30 | 2:00 |
| 6 | Demostración | Juan → Oscar | 6:30 | 4:15 |
| 7 | Resultados | Juan → Oscar | 10:45 | 1:00 |
| 8 | Conclusiones y trabajos futuros | Oscar | 11:45 | 1:15 |
| 9 | Aportes (CRediT) y uso de IA | Juan → Oscar | 13:00 | 0:45 |
| 10 | Cierre | Oscar + Juan | 13:45 | 0:15 |

Quedan alrededor de 60 s de margen para pausas o para que la página tarde en cargar.

---

## 1. Apertura (0:00 – 0:45)

**Diapositiva:** portada con el nombre del proyecto, la materia, la universidad, los nombres (Oscar Andrade y Juan [apellido]) y la fecha.

**Oscar:** Hola, somos Oscar y Juan, y les vamos a presentar **Street Report**, nuestro proyecto de Desarrollo de Soluciones Cloud.

**Juan:** Street Report es una aplicación web para que cualquier ciudadano reporte problemas de su barrio, como huecos, luminarias dañadas o basuras, con una foto, y pueda consultar en qué estado va cada reporte.

**Oscar:** La aplicación está desplegada en Microsoft Azure. En los próximos minutos les contamos el problema, cómo la construimos y la vamos a ver funcionando.

---

## 2. Planteamiento del problema y justificación (0:45 – 2:15)

**Diapositiva:** fotos de un hueco, una luminaria apagada y basura acumulada, con tres viñetas: *canales dispersos*, *sin seguimiento*, *sin datos*.

**Juan:** En nuestros barrios vemos todos los días problemas como estos. Cuando alguien quiere reportarlos, el proceso suele ser poco claro: hay que llamar, escribir en redes sociales o ir a una oficina. Después de reportar, el ciudadano no sabe si alguien recibió el reporte ni si se va a solucionar.

**Juan:** Identificamos tres problemas:
1. **Los canales están dispersos.** No existe un único lugar para reportar.
2. **No hay seguimiento.** El ciudadano no ve el estado de su reporte.
3. **No hay datos consolidados.** Sin un registro centralizado es difícil saber qué zonas o qué tipos de problema son más frecuentes.

**Juan:** *(Justificación)* Una aplicación web resuelve esto sin instalar nada, porque funciona desde el navegador del celular o del computador. Elegimos la nube porque nos da una URL pública, base de datos y almacenamiento de fotos gestionados, y despliegue automático, sin administrar servidores propios y con un costo muy bajo.

---

## 3. Objetivos (2:15 – 3:00)

**Diapositiva:** el objetivo general y la lista de objetivos específicos.

**Oscar:** **Objetivo general:** desarrollar y desplegar en Azure una aplicación web que permita a los ciudadanos registrar problemas de su barrio y hacer seguimiento a su estado.

**Oscar:** **Objetivos específicos:**
- Construir una API REST para crear, listar, filtrar y actualizar reportes.
- Diseñar una interfaz web sencilla para registrar y consultar reportes.
- Guardar la información en una base de datos gestionada en la nube, Azure SQL Database.
- Permitir adjuntar una foto a cada reporte y almacenarla en Azure Blob Storage.
- Desplegar la solución en Azure App Service con despliegue continuo desde GitHub.

---

## 4. Marco teórico (3:00 – 4:30)

**Diapositiva:** un icono por concepto con una línea de definición cada uno.

**Oscar:** Unos conceptos que sostienen el proyecto:
- **Computación en la nube:** acceso bajo demanda a recursos de cómputo por internet, pagando solo por lo que se usa.
- **PaaS (plataforma como servicio):** el proveedor administra el sistema operativo y el servidor, y nosotros solo subimos la aplicación. **Azure App Service** es el PaaS que usamos.
- **Base de datos serverless:** Azure SQL Database en modo serverless escala la capacidad según la carga y **se pausa sola** cuando no hay uso, así que solo pagamos cuando trabaja.
- **Almacenamiento de objetos:** Azure Blob Storage guarda archivos como las fotos de los reportes.
- **CI/CD y OIDC:** con GitHub Actions, cada cambio en `main` se prueba, se compila y se despliega solo. GitHub inicia sesión en Azure con **OIDC**, mediante una identidad federada, así que no guardamos contraseñas de Azure en el repositorio.

**Juan:** Y en el lado del desarrollo:
- **API REST:** el frontend y el backend se comunican por HTTP con JSON, usando los métodos GET, POST y PATCH.
- **Arquitectura en capas:** separamos controlador, servicio y repositorio, de modo que cada capa tiene una sola responsabilidad.
- **ORM (JPA/Hibernate):** traduce las clases Java a tablas de la base de datos sin escribir SQL a mano. Por eso pudimos pasar de PostgreSQL en local a Azure SQL en la nube cambiando solo el driver y la cadena de conexión.
- **SPA con React:** la página carga una vez y se actualiza sin recargar el navegador.

---

## 5. Arquitectura y componentes (4:30 – 6:30)

**Diapositiva:** diagrama de arquitectura.

```
 Navegador (React)
        │  HTTPS
        ▼
 Azure App Service (street-report-app)  ──►  Azure SQL Database (reportes)
 Plan F1 · Linux · Java 17                    serverless GP_S_Gen5_1
 Spring Boot: API + frontend compilado
        │
        └──►  Azure Blob Storage (ststreetreport / contenedor fotos)

 GitHub (main) ──► GitHub Actions ──(OIDC: id-github-street-report)──► App Service

 Todo en el grupo de recursos rg-street-report · región Chile Central
```

**Oscar:** Esta es la arquitectura. Todos los recursos están en el grupo `rg-street-report`, en la región **Chile Central**. Usamos **un solo App Service**, en el plan gratuito **F1** con Linux y Java 17, que sirve tanto la API como el frontend ya compilado. Como todo sale del mismo dominio, evitamos problemas de CORS y pagamos un solo recurso de cómputo.

**Oscar:** La base de datos es **Azure SQL Database en modo serverless**: hasta un vCore, con pausa automática después de 60 minutos sin uso. Las credenciales no están en el código; se configuran como **variables de entorno** del App Service. En el repositorio solo hay un `.env.example` sin valores reales.

**Oscar:** Las fotos se suben a **Blob Storage**, al contenedor `fotos`, y en la base de datos solo guardamos su URL. Cada vez que hacemos merge a `main`, **GitHub Actions** ejecuta las pruebas, compila React, lo empaqueta dentro del backend y despliega el `.jar` en el App Service.

**Diapositiva:** el stack (Spring Boot 3 / Java 17, React + Vite, Azure SQL) y las capas del backend.

**Juan:** Por dentro, el backend está hecho en **Spring Boot 3 con Java 17** y organizado por capas:
- **controller:** expone los endpoints y convierte los errores en JSON claros.
- **service:** contiene la lógica de negocio, por ejemplo que todo reporte nuevo empieza en estado *Pendiente*.
- **repository:** acceso a datos con Spring Data JPA.
- **model y dto:** la entidad `Reporte`, con cinco categorías (bache, alumbrado, basura, seguridad y otro) y tres estados (pendiente, en proceso y resuelto).

**Juan:** Los endpoints son estos:
- `GET /api/reportes`, con filtros por estado, categoría y texto.
- `POST /api/reportes` para crear un reporte.
- `GET /api/reportes/{id}` para ver el detalle.
- `PATCH /api/reportes/{id}/estado` para cambiar el estado.
- `POST /api/fotos` para subir la foto.
- `/api/health`, que verifica que la app y la base de datos estén arriba.

**Juan:** El frontend en **React** tiene el formulario de nuevo reporte, la barra de filtros, la tarjeta de cada reporte y la ventana de detalle. Todas las llamadas al backend pasan por un único archivo, `api.js`.

**Juan:** Para trabajar en paralelo definimos un **contrato técnico** con los endpoints, el modelo de datos y las reglas de ramas. Así cada uno avanzó en su módulo sin pisar el código del otro.

---

## 6. Demostración (6:30 – 10:45)

> **Importante:** el plan F1 y la base serverless "se duermen" cuando no hay uso, y la primera petición puede tardar unos **30 segundos**. Abran la URL **un par de minutos antes de grabar**. Tengan también como respaldo un clip con la misma demo ya grabada.
>
> URL: https://street-report-app-gfftgxgvczdtbrb4.chilecentral-01.azurewebsites.net

### Parte de Juan: reportes (6:30 – 8:30)

**Juan:** Esta es la aplicación corriendo en Azure. *(Mostrar la barra de direcciones con la URL `*.azurewebsites.net`.)*

**Juan:** En la página principal vemos los reportes del más reciente al más antiguo, cada uno con su categoría, estado y dirección.

**Juan:** Probemos los filtros. *(Elegir la categoría **Alumbrado público**.)* La lista solo muestra los reportes de alumbrado. *(Elegir el estado **Resuelto**.)* Los filtros se combinan. *(Clic en **Limpiar**.)*

**Juan:** También hay un buscador que busca en el título, la descripción y la dirección. *(Escribir una dirección de un reporte existente.)* La búsqueda se hace en el servidor, y esperamos un cuarto de segundo antes de consultar para no hacer una petición por cada tecla.

**Juan:** Si hago clic en un reporte, se abre el **detalle**: la foto en grande, la descripción completa, la dirección y la fecha. *(Abrir el reporte que tiene foto y cerrarlo con Esc.)*

**Juan:** Y aquí `/api/health` confirma que la aplicación está conectada a la base de datos en Azure y cuántos reportes hay. *(Abrir `<url>/api/health`.)*

### Parte de Oscar: foto, nube y despliegue (8:30 – 10:45)

**Oscar:** Ahora creo un reporte con foto. *(Clic en **Nuevo reporte**.)* Título: "Hueco grande en la vía". Categoría: *Hueco en la vía*. Dirección: "Carrera 15 # 80-20". Descripción: "Ocupa casi todo el carril". *(Elegir una foto; se ve la vista previa.)* El formulario solo acepta JPG, PNG, WEBP o GIF de hasta 5 MB. *(Clic en **Enviar reporte**.)*

**Oscar:** Primero la foto se sube a `/api/fotos`. El backend la guarda en Blob Storage y devuelve la URL; después se crea el reporte con esa URL. El reporte aparece de primero en la lista, con su foto y en estado **Pendiente**.

**Oscar:** Ahora el portal de Azure. *(Mostrar el grupo de recursos `rg-street-report`.)* Aquí están el **App Service**, su plan F1, el servidor y la base de datos **Azure SQL**, la cuenta de **Storage** y la **identidad administrada** que usa GitHub. *(Entrar a `ststreetreport` → contenedor `fotos`.)* Aquí está la foto que acabamos de subir.

**Oscar:** *(Abrir App Service → Variables de entorno.)* La conexión a la base de datos y a Storage se configura aquí. Los valores están ocultos y no están en el código.

**Oscar:** Por último, el despliegue automático. *(Abrir la pestaña **Actions** de GitHub, sin traducción automática del navegador.)* Cada push a `main` ejecuta este flujo: compila React, lo empaqueta dentro de Spring Boot, corre las 10 pruebas y publica en App Service. *(Mostrar la primera ejecución, la que falló.)* La primera vez falló al iniciar sesión en Azure, porque faltaban los secretos del cliente y el tenant. Lo corregimos creando una identidad administrada con credencial federada (OIDC) y agregando los secretos al repositorio. *(Mostrar las ejecuciones en verde.)* Desde entonces cada despliegue sale bien.

---

## 7. Resultados (10:45 – 11:45)

**Diapositiva:** lista de chequeo con lo logrado y una cifra de costo.

**Juan:** Como resultado tenemos:
- Una aplicación funcional y **pública en Azure**, accesible desde cualquier navegador.
- Una API REST con seis endpoints, documentados en el contrato técnico.
- **10 pruebas automatizadas** del backend: 5 del módulo de reportes y 5 del de fotos. Todas pasan en cada despliegue.

**Oscar:**
- Fotos almacenadas en Blob Storage, con validación de tipo y tamaño.
- Despliegue continuo con GitHub Actions y OIDC, sin contraseñas de Azure en el repositorio.
- **Costo bajo:** en noviembre llevamos unos **4,27 USD**, casi todo de la base de datos. El App Service no cuesta nada porque usa el plan F1, y el almacenamiento cuesta menos de un centavo.

---

## 8. Conclusiones y trabajos futuros (11:45 – 13:00)

**Diapositiva:** dos columnas, *Conclusiones* y *Trabajos futuros*.

**Oscar:** **Conclusiones:**
- Un PaaS como App Service nos permitió concentrarnos en la aplicación y no en administrar servidores.
- Usar JPA nos dio independencia de la base de datos: pasamos de PostgreSQL en local a Azure SQL en la nube sin reescribir la lógica.
- Los niveles gratuitos y serverless reducen mucho el costo. A cambio, la app tarda en "despertar" después de un tiempo sin uso; para producción real conviene un plan pagado.
- Definir el contrato antes de programar fue clave para trabajar en paralelo entre dos personas.
- Automatizar el despliegue reduce errores manuales: lo que está en `main` es lo que está en producción.

**Oscar:** **Trabajos futuros:**
- **Usuarios y roles** (ciudadano y administrador), con registro e inicio de sesión. El frontend ya tiene preparado el manejo del token.
- Un **panel de administración** para cambiar el estado de los reportes desde la interfaz. Hoy el endpoint existe, pero no tiene pantalla ni restricción por rol.
- Un **mapa** con la ubicación de cada reporte, aprovechando los campos de latitud y longitud que ya existen en el modelo.
- **Notificaciones** cuando cambie el estado de un reporte.
- Monitoreo con **Application Insights**, una alerta de presupuesto en Cost Management y un plan de App Service sin pausas.

---

## 9. Aportes (CRediT) y uso de IA (13:00 – 13:45)

**Diapositiva:** la tabla CRediT del reporte, la misma que en el PDF.

**Juan:** En el reporte está el detalle de los aportes con la taxonomía **CRediT**. En resumen: yo desarrollé la estructura inicial del proyecto y el módulo de reportes, tanto el backend como las pantallas. Oscar se encargó de la infraestructura en Azure, el despliegue automático, el cambio a Azure SQL y la subida de fotos a Blob Storage. La conceptualización, la redacción y la revisión fueron compartidas.

**Oscar:** Sobre el uso de **IA generativa**: usamos [modelo(s) que usaron] como apoyo para [ajustar a lo que hicieron, por ejemplo: "redactar borradores del documento, preparar este guion y revisar código"]. El guion de esta presentación se preparó con **Claude Opus 5.5**. Todo el contenido lo revisamos, probamos y ajustamos nosotros. Los detalles están en la sección de aclaraciones del reporte.

---

## 10. Cierre (13:45 – 14:00)

**Oscar:** Eso es Street Report: una forma sencilla de que los ciudadanos reporten problemas de su barrio y sepan qué pasa con ellos, construida y desplegada en la nube.

**Juan:** Muchas gracias por su atención.

---

## Lista de chequeo antes de grabar

- [ ] Completar el apellido de Juan y el texto de uso de IA según lo que realmente hicieron.
- [ ] Abrir la URL de Azure 2 minutos antes y comprobar que `/api/health` responde.
- [ ] Tener a mano una foto de prueba (JPG o PNG de menos de 5 MB) para el reporte nuevo.
- [ ] Abrir en pestañas aparte el portal de Azure (`rg-street-report`) y GitHub Actions, sin traducción automática.
- [ ] Cronometrar un ensayo completo: debe quedar por debajo de 14:30.
- [ ] Exportar el video y subirlo a OneDrive con **enlace de solo lectura** para el formulario (entrega: **19/11/2026, 20:00**).

**Herramientas para grabar:** Microsoft Teams (reunión grabada con pantalla compartida), OBS Studio o PowerPoint → *Grabar*. Si graban por separado, cada uno graba sus secciones y luego se unen en Clipchamp, que viene incluido en Windows.
