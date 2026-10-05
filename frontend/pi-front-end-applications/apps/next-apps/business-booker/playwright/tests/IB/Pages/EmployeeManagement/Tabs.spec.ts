import { config } from '@WB-playwright/config';
import { IB_Travel_Manager, IB_USER_MANAGEMENT } from '@WB-playwright/constants';
import { LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

test.describe('InnBusiness Tabs - TestCase ID: 413455', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();
    if (config.DEVICE === 'mobile') {
      expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await page.goto(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);
      await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);

      await page.goto(`${IB_USER_MANAGEMENT}?tab=innbusiness`);
      await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness`);
    } else {
      await page.goto(IB_USER_MANAGEMENT);

      expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      const innBusinessPayTab = page.locator("a[href='?tab=innbusiness-pay'] > span");
      await innBusinessPayTab.click();

      await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);
      await expect(innBusinessPayTab).toHaveAttribute('data-state', 'active');

      const innbusinessTab = page.locator("a[href='?tab=innbusiness'] > span");
      await innbusinessTab.click();

      await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness`);
      await expect(innbusinessTab).toHaveAttribute('data-state', 'active');
    }
  });
});
