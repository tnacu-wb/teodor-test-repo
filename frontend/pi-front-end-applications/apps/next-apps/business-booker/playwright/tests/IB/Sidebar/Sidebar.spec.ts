import { config } from '@WB-playwright/config';
import { IB_Travel_Manager, IB_Booker, IB_Self_Booker, IB_HOME } from '@WB-playwright/constants';
import { LoginBB, goToHDP } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];

const sidebarId =
  config.DEVICE === 'desktop' ? 'SidebarDesktop-container' : 'SidebarMobile-container';

test.describe('HDP Visibility - TestCase ID: 399517, 399518', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToHDP(page, user, 'London Euston');

      const sidebarComponent = page.getByTestId(sidebarId);
      await sidebarComponent.scrollIntoViewIfNeeded();
      await expect(
        sidebarComponent,
        'When I check the left part of the page (or mobile bottom), then I see the new navigation bar'
      ).toBeVisible();

      await test.step('And contains the same elements as on the Home page: Home, Spending, Manage, Bookings', async () => {
        await expect(await sidebarComponent.getByTestId('Home-Sidebar-Link')).toBeVisible();
        await expect(await sidebarComponent.getByTestId('Spending-Sidebar-Link')).toBeVisible();
        const manageSidebarLink = await sidebarComponent.getByTestId('Manage-Sidebar-Link');
        user != IB_Travel_Manager
          ? await expect(manageSidebarLink).toBeHidden()
          : await expect(manageSidebarLink).toBeVisible();
        await expect(await sidebarComponent.getByTestId('Bookings-Sidebar-Link')).toBeVisible();
      });

      await test.step('When I click on the Home menu, then I am redirected to homepage', async () => {
        const homeMenu = await sidebarComponent.getByTestId('Home-Sidebar-Link');
        await homeMenu.click();
        await expect(page).toHaveURL(IB_HOME);
      });
    });
  });
});
