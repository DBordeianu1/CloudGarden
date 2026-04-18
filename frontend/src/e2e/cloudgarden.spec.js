const { test, expect, request } = require('@playwright/test');

const API_BASE = 'http://localhost:8080/api/plants';

test.describe('Plant lifecycle', () => {
  let createdPlantId = null;

  test.afterEach(async () => {
    if (createdPlantId !== null) {
      const apiContext = await request.newContext();
      await apiContext.delete(`${API_BASE}/${createdPlantId}`);
      await apiContext.dispose();
      createdPlantId = null;
    }
  });

  test('should show empty garden on load', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText('Total Plants: 0')).toBeVisible();
    await expect(page.getByText('Healthy: 0')).toBeVisible();
  });

  test('should plant a new succulent and show HEALTHY status', async ({ page }) => {
    await page.goto('/');

    await page.getByRole('button', { name: 'Plant New Succulent' }).click();
    await expect(page.getByRole('heading', { name: 'Plant a New Succulent' })).toBeVisible();

    await page.getByPlaceholder('e.g., Spike, Green Bean, Mr. Pickles').fill('Spike');
    await page.getByPlaceholder('e.g., Aloe, Agave, Echeveria').fill('Aloe');
    await page.getByRole('button', { name: 'Plant It!' }).click();

    // h3 is 3 levels deep: card-root > div.flex > div.flex-1 > h3
    const card = page.locator('h3', { hasText: 'Spike' }).locator('../../..');
    await expect(card).toBeVisible();

    // capture id before further assertions so afterEach can clean up even if they fail
    const idText = await card.getByText(/Id: \d+/).textContent();
    createdPlantId = idText.replace('Id: ', '').trim();

    await expect(card.getByText('HEALTHY')).toBeVisible();
  });

  test('should update header stats after planting', async ({ page }) => {
    const apiContext = await request.newContext();
    const response = await apiContext.post(API_BASE, {
      data: { name: 'Beanie', type: 'Echeveria' },
    });
    const plant = await response.json();
    createdPlantId = plant.id;
    await apiContext.dispose();

    await page.goto('/');
    await expect(page.getByText('Total Plants: 1')).toBeVisible();
    await expect(page.getByText('Healthy: 1')).toBeVisible();
  });

  test('should water a plant and keep it HEALTHY', async ({ page }) => {
    const apiContext = await request.newContext();
    const response = await apiContext.post(API_BASE, {
      data: { name: 'Droplet', type: 'Agave' },
    });
    const plant = await response.json();
    createdPlantId = plant.id;
    await apiContext.dispose();

    await page.goto('/');

    // h3 is 3 levels deep: card-root > div.flex > div.flex-1 > h3
    const card = page.locator('h3', { hasText: 'Droplet' }).locator('../../..');
    await expect(card).toBeVisible();
    await card.getByRole('button', { name: 'Water' }).click();

    await expect(card.getByText('HEALTHY')).toBeVisible();
    await expect(card.getByText('100%')).toBeVisible();
  });

  test('should delete a plant and reset stats to zero', async ({ page }) => {
    const apiContext = await request.newContext();
    const response = await apiContext.post(API_BASE, {
      data: { name: 'Doomed', type: 'Aloe' },
    });
    const plant = await response.json();
    createdPlantId = plant.id;
    await apiContext.dispose();

    await page.goto('/');

    // h3 is 3 levels deep: card-root > div.flex > div.flex-1 > h3
    const card = page.locator('h3', { hasText: 'Doomed' }).locator('../../..');
    await expect(card).toBeVisible();
    await card.getByRole('button', { name: 'Delete' }).click();

    await expect(page.getByRole('heading', { name: 'Remove Plant?' })).toBeVisible();
    await page.getByRole('button', { name: 'Remove' }).click();

    await expect(page.locator('h3', { hasText: 'Doomed' })).not.toBeVisible();
    await expect(page.getByText('Total Plants: 0')).toBeVisible();

    createdPlantId = null;
  });
});
