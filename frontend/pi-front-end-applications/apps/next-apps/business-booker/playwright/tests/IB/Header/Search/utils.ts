import { expect, Page } from '@playwright/test';

import { Search_Container_IB } from './elements';

export async function searchElementsNotInViewport(page: Page) {
  const search = await Search_Container_IB(page);
  await expect(search, 'search bar').not.toBeInViewport();

  const header = page.locator('header');
  await expect(header, 'header').not.toBeInViewport();

  const editSearch = page.getByTestId('Edit-Search-IB');
  await expect(editSearch, 'search location Edit button').not.toBeInViewport();
}
