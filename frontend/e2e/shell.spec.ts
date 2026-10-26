import { test, expect } from '@playwright/test';

// Isolated frontend behavior: mocks exist only in Playwright, never in the app.
test('shows readiness and checks again', async ({ page }) => {
  let requests = 0;
  await page.route('**/actuator/health/readiness', route => {
    requests++;
    return route.fulfill({ status: 200, json: { status: 'UP' } });
  });
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'AI knowledge base' })).toBeVisible();
  await expect(page.getByRole('status')).toHaveText('Database connected. Workspace runtime is ready.');
  const beforeRetry = requests;
  await page.getByRole('button', { name: 'Check again' }).click();
  await expect(page.getByRole('status')).toHaveText('Database connected. Workspace runtime is ready.');
  expect(requests).toBeGreaterThan(beforeRetry);
});

test('shows database failure and recovers when retry succeeds', async ({ page }) => {
  let available = false;
  await page.route('**/actuator/health/readiness', route => route.fulfill({
    status: available ? 200 : 503, json: { status: available ? 'UP' : 'DOWN' },
  }));
  await page.goto('/');
  await expect(page.getByRole('status')).toHaveText('Database unavailable. Start the local database and check again.');
  available = true;
  await page.getByRole('button', { name: 'Check again' }).click();
  await expect(page.getByRole('status')).toHaveText('Database connected. Workspace runtime is ready.');
});

test('reports a network failure without claiming readiness', async ({ page }) => {
  await page.route('**/actuator/health/readiness', route => route.abort('connectionrefused'));
  await page.goto('/');
  await expect(page.getByRole('status')).toHaveText('Database unavailable. Start the local database and check again.');
});
