import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
export default defineConfig({ plugins: [react(), tailwindcss()], server: { host: '127.0.0.1', port: Number(process.env.FRONTEND_PORT ?? 5173), strictPort: true, proxy: { '/actuator': `http://127.0.0.1:${process.env.APP_PORT ?? 8080}` } } });
