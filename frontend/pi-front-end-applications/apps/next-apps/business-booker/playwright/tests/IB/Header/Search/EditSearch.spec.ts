import { config } from '@WB-playwright/config';
import { IB_Travel_Manager, IB_Booker, IB_Self_Booker, IB_HOME } from '@WB-playwright/constants';
import { LoginBB, goToSRP, goToHDP } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Search_Container_IB } from './elements';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];

test.describe('SRP flow', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 404299, 404300`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToSRP(page, user);

      const searchBar = await Search_Container_IB(page);
      const locationInput = searchBar.getByTestId('IB-Location-Input');
      const editSearchContainer = page.getByTestId('Edit-Search-IB');
      const locationResultsList = page.getByTestId('Location-Results-Container');

      if (config.DEVICE === 'mobile') {
        await expect(editSearchContainer, 'Edit is visible for mobile').toBeVisible();

        const editButton = page.getByTestId('Edit-Search-Button');
        await test.step('When I click the Edit button', async () => {
          await editButton.click();
        });

        await test.step('And I delete the location', async () => {
          await expect(locationInput).not.toHaveValue('', { timeout: 20000 });
          const clearLocation = searchBar.getByTestId('IB-Location-Clear');
          await locationInput.click();
          await expect(clearLocation).toBeVisible();
          await clearLocation.click();
        });

        await test.step('Then the search location is empty and the placeholder "Where to?" is displayed instead', async () => {
          const locationValue = await locationInput.inputValue();
          await expect(locationValue).toBe('');
        });

        await test.step('When I type a hotel name, then a list with new relevant suggestions is displayed', async () => {
          await locationInput.click();
          await locationInput.fill('London Euston');
          const locationValue = await locationInput.inputValue();
          await expect(locationValue).toBe('London Euston');
          await expect(locationResultsList).toBeAttached();
        });
      } else {
        await expect(editSearchContainer, 'Edit is hidden for desktop').toBeHidden();
      }
    });
  });
});
test.describe('SRP Edit button functionality', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 404787`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToSRP(page, user);

      const searchBar = await Search_Container_IB(page);
      const editSearchContainer = page.getByTestId('Edit-Search-IB');

      if (config.DEVICE === 'mobile') {
        const editButton = page.getByTestId('Edit-Search-Button');
        await expect(searchBar, 'When I check the search bar, it is collapsed').toBeHidden();
        await expect(editButton, 'And the Edit button is displayed').toBeVisible();

        await test.step('When I click the Edit button', async () => {
          await editButton.click();
        });

        await test.step('Then the search bar expands and all the search fields are displayed', async () => {
          await expect(searchBar).toBeVisible();
        });
      } else {
        await expect(editSearchContainer, 'Edit is hidden for desktop').toBeHidden();
      }
    });
  });
});

test.describe('HDP flow', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 404301, 404302`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToHDP(page, user);

      const searchBar = await Search_Container_IB(page);
      const locationInput = searchBar.getByTestId('IB-Location-Input');
      const editSearchContainer = page.getByTestId('Edit-Search-IB');
      const locationResultsList = page.getByTestId('Location-Results-Container');

      if (config.DEVICE === 'mobile') {
        await expect(editSearchContainer, 'Edit is visible for mobile').toBeVisible();

        const editButton = page.getByTestId('Edit-Search-Button');
        await test.step('When I click the Edit button', async () => {
          await editButton.click();
        });

        await test.step('And I delete the location', async () => {
          await expect(locationInput).not.toHaveValue('', { timeout: 20000 });
          const clearLocation = searchBar.getByTestId('IB-Location-Clear');
          await locationInput.click();
          await expect(clearLocation).toBeVisible();
          await clearLocation.click();
        });

        await test.step('Then the search location is empty and the placeholder "Where to?" is displayed instead', async () => {
          const locationValue = await locationInput.inputValue();
          await expect(locationValue).toBe('');
        });

        await test.step('When I type a hotel name, then a list with new relevant suggestions is displayed', async () => {
          await locationInput.click();
          await locationInput.fill('London, UK');
          const locationValue = await locationInput.inputValue();
          await expect(locationValue).toBe('London, UK');
          await expect(locationResultsList).toBeAttached();
        });
      } else {
        await expect(editSearchContainer, 'Edit is hidden for desktop').toBeHidden();
      }
    });
  });
});
test.describe('HDP Edit button functionality', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 404786, 403353`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToHDP(page, user);

      const searchBar = await Search_Container_IB(page);
      const editSearchContainer = page.getByTestId('Edit-Search-IB');

      if (config.DEVICE === 'mobile') {
        const editButton = page.getByTestId('Edit-Search-Button');
        await expect(searchBar, 'When I check the search bar, it is collapsed').toBeHidden();
        await expect(editButton, 'And the Edit button is displayed').toBeVisible();

        await test.step('When I click the Edit button', async () => {
          await editButton.click();
        });

        await test.step('Then the search bar expands and all the search fields are displayed', async () => {
          await expect(searchBar).toBeVisible();
        });
      } else {
        await expect(editSearchContainer, 'Edit is hidden for desktop').toBeHidden();
      }
    });
  });
});

test.describe('Homepage Edit Visibility', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 403352`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reach InnBusiness website and the searched was NOT performed', async () => {
        await page.goto(IB_HOME);
      });

      const searchBar = await Search_Container_IB(page);
      const editSearchContainer = page.getByTestId('Edit-Search-IB');

      if (config.DEVICE === 'mobile') {
        await test.step('When I fill the search location and check the Search bar section of Global header', async () => {
          const locationInput = searchBar.getByTestId('IB-Location-Input');
          await locationInput.click();
          await locationInput.fill('London, UK');
        });
        await expect(
          editSearchContainer,
          'Then I see that the Search bar DOES NOT display Edit option'
        ).toBeHidden();
      } else {
        await expect(editSearchContainer, 'Edit is hidden for desktop').toBeHidden();
      }
    });
  });
});
