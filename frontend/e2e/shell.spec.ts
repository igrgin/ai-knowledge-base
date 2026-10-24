import { test, expect } from '@playwright/test';
test('packaged shell reports the real database readiness', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'AI knowledge base' })).toBeVisible();
  await expect(page.getByRole('status')).toHaveText(process.env.EXPECT_DATABASE_DOWN ? 'Database unavailable. Start the local database and check again.' : 'Database connected. Workspace runtime is ready.', { timeout: 15000 });
  if (!process.env.EXPECT_DATABASE_DOWN) await page.screenshot({ path: '../.local/t1-shell.png', fullPage: true });
  await page.getByRole('button', { name: 'Check again' }).click();
  await expect(page.getByRole('status')).toHaveText(process.env.EXPECT_DATABASE_DOWN ? 'Database unavailable. Start the local database and check again.' : 'Database connected. Workspace runtime is ready.', { timeout: 15000 });
});
