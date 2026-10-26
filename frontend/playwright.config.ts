import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  workers: 1,
  reporter: 'list',
  use: { baseURL: 'http://127.0.0.1:15175' },
  webServer: {
    command: 'vite preview --host 127.0.0.1 --port 15175 --strictPort',
    url: 'http://127.0.0.1:15175',
    reuseExistingServer: false,
  },
});
