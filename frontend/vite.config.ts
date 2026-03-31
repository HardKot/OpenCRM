import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '#app': path.resolve(__dirname, './src/app'),
      '#entities': path.resolve(__dirname, './src/entities'),
      '#features': path.resolve(__dirname, './src/features'),
      '#pages': path.resolve(__dirname, './src/pages'),
      '#shared': path.resolve(__dirname, './src/shared'),
      '#widgets': path.resolve(__dirname, './src/widgets'),
    },
  },
  server: {
    host: true,
    port: 3000,
    strictPort: true,
    watch: {
      usePolling: true,
      interval: 300,
    },
  },
  build: {
    outDir: 'build',
  },
});
