import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// En desarrollo, las llamadas a /api se redirigen al backend (puerto 8080), así no hay problemas de CORS.
// En producción el backend sirve el frontend compilado desde el mismo dominio.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
