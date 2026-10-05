import {
  IB_Travel_Manager,
  IB_Booker,
  IB_HOME,
  IB_Self_Booker,
  IB_Guest,
  Hotels,
} from '@WB-playwright/constants';
import { LoginBB, goToHDP } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];
const limitedUsers = [IB_Guest];

test.describe('IB Homepage Visibility - TestCase ID: 399696, 399697', () => {
  [...users, ...limitedUsers].forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Go to Inn Business homepage', async () => {
        await page.goto(IB_HOME);
      });

      const footerComponent = page.getByTestId('IB-Footer');
      await footerComponent.scrollIntoViewIfNeeded();
      await expect(
        footerComponent,
        'When I check the bottom of the page and I inspect the footer component'
      ).toBeVisible();

      await test.step('Then the footer contains the following:', async () => {
        const bottomLinks = page.getByTestId('IB-Footer-Quick-Links');
        await expect(bottomLinks, 'One row of links').toBeVisible();

        const socialMedia = page.getByTestId('IB-Social-Media-Links');
        await expect(socialMedia, 'Social media links').toBeVisible();
      });
    });
  });
});

test.describe('HDP Visibility - TestCase ID: 399695', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToHDP(page, user, Hotels.LONDON_GATWICK_AIRPORT.name);

      const footerComponent = page.getByTestId('IB-Footer');
      await footerComponent.scrollIntoViewIfNeeded();
      await expect(
        footerComponent,
        'When I check the bottom of the page, then I see the footer is displayed'
      ).toBeVisible();
    });
  });
});
