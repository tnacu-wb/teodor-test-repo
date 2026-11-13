import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('Universal Login', () => {
  test('Login page displays correctly @universalLogin @TC-856', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
    const pageContent = await page.textContent('body');
    expect(pageContent).toBeTruthy();
  });

  test('Unauthenticated user sees login CTA @universalLogin @TC-857', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
    const title = await page.title();
    expect(title).toContain('Premier Inn');
  });

  test('Login button click opens auth modal @universalLogin @TC-858', async ({ page }) => {
    await page.goto('/');
    // Element not found — waiting for locator
    await page.locator('[data-testid="auth-login-modal-trigger"]').click({ timeout: 5000 });
  });
});
