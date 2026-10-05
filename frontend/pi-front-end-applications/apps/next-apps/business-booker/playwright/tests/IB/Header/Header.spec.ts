import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_Booker,
  IB_Self_Booker,
  Hotels,
  Constants,
} from '@WB-playwright/constants';
import { LoginBB, goToGDP, goToHDP, goToAmendPage } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Search_Container_IB } from './Search/elements';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];

test.describe('HDP Visibility - TestCase ID: 399520', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const searchBar = await Search_Container_IB(page);

      await goToHDP(page, user);

      await test.step('When I check the top part of the page, then I see that searchbar is NOT displayed under the header', async () => {
        const oldSearchContainer = page.getByTestId('search-summary-container');
        await expect(oldSearchContainer).toBeHidden();
      });

      await test.step('And I see the header includes:', async () => {
        const LogoIB = page.getByTestId('IB-Logo');
        await expect(LogoIB, 'The IB logo').toBeVisible();
        if (config.DEVICE === 'desktop') {
          await expect(searchBar, 'The search bar').toBeVisible();
        } else {
          await expect(
            searchBar,
            'Mobile search bar is hidden (edit search is visible)'
          ).toBeHidden();
        }

        const companyName = page.getByTestId('Company-Name');
        if (config.DEVICE === 'desktop') {
          await expect(companyName, 'The company name').toBeVisible();
        } else {
          await expect(companyName, 'Mobile company name is hidden').toBeHidden();
        }

        const languageSwitcher = page.getByTestId('Language-Switcher-Button');
        await expect(languageSwitcher, 'The language switcher').toBeVisible();

        const accountInitials = page.getByTestId('Account-Menu-Button');
        await expect(accountInitials, 'The account initials').toBeVisible();
      });
    });
  });
});

test.describe('GDP Header Design - TestCase ID: 403305, 403307', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reached GDP on a booking flow', async () => {
        await goToGDP(page, user);
      });

      const BusinessSteps = page.getByTestId('BusinessSteps');

      await test.step('When I check the top part of the page, Then I see the new header with the following components:', async () => {
        const LogoIB = page.getByTestId('IB-Logo');
        await expect(LogoIB, 'PI logo').toBeVisible();

        if (config.DEVICE === 'desktop') {
          await expect(
            BusinessSteps,
            'Flow steps with names under their corresponding circle'
          ).toBeVisible();
        } else {
          await expect(BusinessSteps, 'Flow steps under the logo').toBeVisible();

          await expect(
            BusinessSteps,
            'Current page step label text: "Guest details"'
          ).toContainText('Guest details');
        }
      });

      if (config.DEVICE === 'desktop') {
        await test.step('When I check the flow steps for GDP, ', async () => {
          const FirstStep = BusinessSteps.getByText('1');
          await expect(FirstStep, 'Then step 1 is selected').toHaveClass(/bg-darkGrey1/);
          await expect(FirstStep, 'And "Guest details" label text').toContainText('Guest details');
          await expect(FirstStep, 'is written in bold characters').toHaveClass(/font-bold/);
        });
      }
    });
  });
});

test.describe('Amend Visibility - TestCase ID: 400137, 399702', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const searchBar = await Search_Container_IB(page);
      const LogoIB = page.getByTestId('IB-Logo');
      const languageSwitcher = page.getByTestId('Language-Switcher-Button');
      const accountInitials = page.getByTestId('Account-Menu-Button');

      await goToAmendPage(page, user, Hotels.LONDON_GATWICK_AIRPORT.name, {
        time: 'now',
        card: Constants.VISA_CARD,
      });

      await test.step('Header is visible on Amend page', async () => {
        await expect(searchBar).toBeVisible();
        await expect(LogoIB).toBeVisible();
        await expect(languageSwitcher).toBeVisible();
        await expect(accountInitials).toBeVisible();
      });

      const amendPage = page.getByTestId('amend-page-title');

      await test.step('Amend page is visible', async () => {
        await expect(amendPage).toBeVisible();
      });
      let yourMealsButton;

      if (config.LANGUAGE === 'en') {
        yourMealsButton = page.getByTestId('Button-Your_meals');
      } else {
        yourMealsButton = page.getByTestId('Button-Ihre_Mahlzeiten');
      }

      await test.step('Click on Your meals', async () => {
        await expect(yourMealsButton).toBeVisible();

        await yourMealsButton.click();
      });

      const addOneMoreMeal = page
        .getByTestId('RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-AddButton')
        .first();

      await test.step('Add one more meal', async () => {
        await expect(addOneMoreMeal).toBeVisible();

        await addOneMoreMeal.click();
      });

      const initialURL = page.url();

      const confirmAddedMeal = page.getByTestId('RoomsMealSelection-notification-AlertDescription');

      await test.step('Should display confirm added meal notification', async () => {
        await expect(confirmAddedMeal).toBeVisible();
      });

      const confirmChangesAmendButton = page.locator('[name="confirm-changes-button"]');
      await test.step('Click on confirm changes', async () => {
        await expect(confirmChangesAmendButton).toBeEnabled({ timeout: 40000 });

        await confirmChangesAmendButton.click();
      });

      await test.step('Should redirect to Amend BIC page', async () => {
        await page.waitForURL((url) => url.toString() !== initialURL);
      });

      await test.step('Header is visible on Amend BIC', async () => {
        await expect(searchBar).toBeVisible();
        await expect(LogoIB).toBeVisible();
        await expect(languageSwitcher).toBeVisible();
        await expect(accountInitials).toBeVisible();
      });

      const amendBICWrapper = page.getByTestId('booking-confirmation-wrapper');

      await test.step('Wait Amend BIC page to be loaded', async () => {
        await expect(amendBICWrapper).toBeVisible();
      });
    });
  });
});
