import { config } from '@WB-playwright/config';
import { IB_Travel_Manager, IB_Booker, IB_Self_Booker } from '@WB-playwright/constants';
import { goToSRP, LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Side_Bar_IB } from '../../Header/Search/elements';

const users = [IB_Booker, IB_Self_Booker];
const managerUser = [IB_Travel_Manager];

test.describe('Search results page rebranding', () => {
  users.forEach((user) => {
    test(`Sidebar, searchBar and booking flow - ${user.title} - TestCase ID: 396563, 401291, 401292`, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reached SRP on a booking flow', async () => {
        await goToSRP(page, user);
      });

      const sideBar = await Side_Bar_IB(page);

      const firstLevelNav = sideBar.getByTestId('FirstLevelNav-container');
      const homeLink = sideBar.getByTestId('Home-Sidebar-Link');
      const spendingLink = sideBar.getByTestId('Spending-Sidebar-Link');
      const manageLink = sideBar.getByTestId('Manage-Sidebar-Link');
      const bookingsLink = sideBar.getByTestId('Bookings-Sidebar-Link');

      await expect(firstLevelNav).toBeVisible();
      await expect(homeLink).toBeVisible();
      await expect(spendingLink).toBeVisible();
      await expect(manageLink).toBeHidden();
      await expect(bookingsLink).toBeVisible();

      if (config.DEVICE === 'desktop') {
        const searchBar = page.getByTestId('IB-Search-Container-Desktop');

        await expect(searchBar).toBeVisible();
      } else {
        const editSearchContainer = page.getByTestId('Edit-Search-IB');
        await expect(editSearchContainer).toBeVisible();
      }

      const infiniteScroller = page.locator('.infinite-scroll-component__outerdiv');

      await test.step('Should display list of hotels', async () => {
        await expect(infiniteScroller).toBeVisible();
      });

      const firstHotelCard = page.getByTestId('SRP-hotel-card').first();

      await expect(firstHotelCard).toBeVisible();

      const initialURL = page.url();

      await test.step('Click on hotel card', async () => {
        await firstHotelCard.click();
      });

      await test.step('Should redirect to HDP', async () => {
        await page.waitForURL((url) => url.toString() !== initialURL);
      });

      const HDPWrapper = page.getByTestId('HotelDetailsBBPage-Wrapper');

      await test.step('Should display HDP', async () => {
        await expect(HDPWrapper).toBeVisible();
      });
    });
  });

  managerUser.forEach((user) => {
    test(`Sidebar, searchBar and booking flow - ${user.title} - TestCase ID: 396560, 396563, 401291 `, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reached SRP on a booking flow', async () => {
        await goToSRP(page, user);
      });

      const sideBar = await Side_Bar_IB(page);

      const firstLevelNav = sideBar.getByTestId('FirstLevelNav-container');
      const homeLink = sideBar.getByTestId('Home-Sidebar-Link');
      const spendingLink = sideBar.getByTestId('Spending-Sidebar-Link');
      const manageLink = sideBar.getByTestId('Manage-Sidebar-Link');
      const bookingsLink = sideBar.getByTestId('Bookings-Sidebar-Link');

      await expect(firstLevelNav).toBeVisible();
      await expect(homeLink).toBeVisible();
      await expect(spendingLink).toBeVisible();
      await expect(manageLink).toBeVisible();
      await expect(bookingsLink).toBeVisible();

      if (config.DEVICE === 'desktop') {
        const searchBar = page.getByTestId('IB-Search-Container-Desktop');

        await expect(searchBar).toBeVisible();
      } else {
        const editSearchContainer = page.getByTestId('Edit-Search-IB');
        await expect(editSearchContainer).toBeVisible();
      }

      const infiniteScroller = page.locator('.infinite-scroll-component__outerdiv');

      await test.step('Should display list of hotels', async () => {
        await expect(infiniteScroller).toBeVisible();
      });

      const firstHotelCard = page.getByTestId('SRP-hotel-card').first();

      await expect(firstHotelCard).toBeVisible();

      const initialURL = page.url();

      await test.step('Click on hotel card', async () => {
        await firstHotelCard.click();
      });

      await test.step('Should redirect to HDP', async () => {
        await page.waitForURL((url) => url.toString() !== initialURL);
      });

      const HDPWrapper = page.getByTestId('HotelDetailsBBPage-Wrapper');

      await test.step('Should display HDP', async () => {
        await expect(HDPWrapper).toBeVisible();
      });
    });
  });
});
