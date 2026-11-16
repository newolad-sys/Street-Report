# Street Report

**Street Report** es una aplicación web para que los ciudadanos reporten problemas del barrio (huecos, luminarias, basuras) y consulten su estado. Proyecto de Desarrollo de Soluciones Cloud, desplegado en Microsoft Azure.

- **Backend:** Spring Boot 3, Java 17, JPA, PostgreSQL
- **Frontend:** React + Vite
- **Nube:** Azure App Service, base de datos PostgreSQL gestionada, Blob Storage (fotos), GitHub Actions

El acuerdo entre módulos está en [`docs/contrato.md`](docs/contrato.md).

## Estructura

```
Street-Report/
├── backend/            API REST (Spring Boot, Maven)
│   └── src/main/java/com/reportes/
│       ├── controller/   endpoints y manejo de errores
│       ├── service/      lógica de negocio
│       ├── repository/   acceso a datos
│       ├── model/        entidades y enums
│       ├── dto/          objetos de entrada y salida
│       └── config/       datos de ejemplo
├── frontend/           interfaz (React + Vite)
│   └── src/
│       ├── components/   formulario, filtros, tarjeta
│       ├── api.js        llamadas al backend
│       └── App.jsx
├── docs/contrato.md    endpoints, modelo de datos y reglas de trabajo
├── docker-compose.yml  PostgreSQL local
└── .env.example        variables de entorno (sin valores reales)
```

## Requisitos

- JDK 17 o superior y Maven 3.9+ (IntelliJ ya incluye Maven)
- Node.js 18 o superior
- Docker (opcional, solo para usar PostgreSQL local)

## Cómo correrlo en local

**1. Backend** (desde `backend/`). Elige una opción:

```bash
# Opción A: sin Docker, base de datos en memoria con datos de ejemplo
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# Opción B: con PostgreSQL local (igual que en Azure)
docker compose up -d          # desde la raíz del repo
mvn spring-boot:run
```

Comprueba que responde: <http://localhost:8080/api/health>

**2. Frontend** (desde `frontend/`):

```bash
npm install
npm run dev
```

Abre <http://localhost:5173>. Las llamadas a `/api` se redirigen al backend automáticamente.

**3. Pruebas del backend** (desde `backend/`):

```bash
mvn test
```

## Variables de entorno

Spring Boot no lee el archivo `.env` por sí solo. Define las variables en la configuración de ejecución de tu IDE o expórtalas en la terminal. Los valores por defecto ya apuntan al PostgreSQL de `docker-compose.yml`, así que en local normalmente no hace falta definir nada. La lista completa está en `docs/contrato.md`.

## Compilar para desplegar en Azure

Un solo App Service sirve la API y el frontend compilado:

```bash
cd frontend && npm ci && npm run build
mkdir -p ../backend/src/main/resources/static
cp -r dist/* ../backend/src/main/resources/static/
cd ../backend && mvn -B package -DskipTests      # genera target/app.jar
```

El despliegue automático desde GitHub lo configura el integrante A (Azure). Estos mismos pasos son los que debe ejecutar el flujo de GitHub Actions antes de publicar `target/app.jar`.

## Subir el proyecto a GitHub

El repositorio lo crea A (dueño de la suscripción de Azure) y agrega a B como colaborador. Luego, desde la carpeta raíz de este proyecto:

```bash
git init -b main
git add .
git commit -m "feat: estructura inicial y módulo de reportes"
git remote add origin https://github.com/<usuario-de-A>/<repositorio>.git
git push -u origin main
git checkout -b feature/reportes
```

A partir de ahí, cada uno trabaja en su rama y se integra con Pull Request (ver reglas en `docs/contrato.md`).
