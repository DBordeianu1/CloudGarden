const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './src/e2e',
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:3000',
    viewport: { width: 1280, height: 720 },
    headless: false,
    slowMo: 1000,
    screenshot: 'off',
    video: 'off',
    trace: 'off',
  },
  projects: [
    {
      name: 'chromium',
      use: { browserName: 'chromium' },
    },
  ],
});
