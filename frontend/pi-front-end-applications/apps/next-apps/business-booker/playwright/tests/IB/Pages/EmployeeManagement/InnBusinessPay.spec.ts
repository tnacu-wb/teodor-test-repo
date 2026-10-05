import { config } from '@WB-playwright/config';
import {
  IB_Tethered_Travel_Manager,
  IB_Travel_Manager,
  IB_USER_MANAGEMENT,
  WORLDLINE_URL,
} from '@WB-playwright/constants';
import { LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

test.describe('Layout WL  - TestCase ID: 413456', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    const accountHolderContainer = page.getByTestId('AccountHolder-container');
    const innBusinessPayTab = page.locator("a[href='?tab=innbusiness-pay'] > span");
    const firstAccountHolderName = page.getByTestId('AccountHolder-Account-1-name');
    const firstAccountHolderCode = page.getByTestId('AccountHolder-Account-1-code');
    const firstAccountHolderBadge = page.getByTestId('AccountHolder-Account-1-badge');
    const firstAccountHolderManage = page.getByTestId('AccountHolder-Account-1-manage-employees');
    const firstTitle = page.getByTestId('InnBusinessPayTab-first-title');
    const secondTitleContainer = page.getByTestId('InnBusinessPayTab-second-title');
    const payCardWidget = page.getByTestId('InnBusinessPayTab-widget');
    const widgetTitle = page.getByTestId('InnBusinessPayTab-widget-title');
    const widgetDescription = page.getByTestId('InnBusinessPayTab-widget-description');
    const widgetCTA = page.getByTestId('InnBusinessPayTab-create-innbusiness-pay-card');

    if (config.DEVICE === 'mobile') {
      await page.goto(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);

      expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step(`When I check the first section page, first title should appear`, async () => {
        await expect(firstTitle).toBeVisible();
      });

      await test.step(`When I check the page a list of account holders are displayed`, async () => {
        await expect(accountHolderContainer).toBeVisible();
      });
      await test.step(`When I check first account should have account name, account serial number, account type`, async () => {
        await expect(firstAccountHolderName).toBeVisible();
        await expect(firstAccountHolderCode).toBeVisible();
        await expect(firstAccountHolderBadge).toBeVisible();
        await expect(firstAccountHolderManage).toBeVisible();
      });

      await test.step(`When I check the second section page, second title should appear`, async () => {
        await secondTitleContainer.scrollIntoViewIfNeeded();
        await expect(secondTitleContainer).toBeVisible();
      });

      await test.step(`When I scroll down on page for second section,  a widget should appear with title, text description and CTA for creating a new InnBusiness Pay card`, async () => {
        await payCardWidget.scrollIntoViewIfNeeded();
        await expect(payCardWidget).toBeVisible();
        await expect(widgetTitle).toBeVisible();
        await expect(widgetDescription).toBeVisible();
        await expect(widgetCTA).toBeVisible();
      });
    } else {
      await page.goto(IB_USER_MANAGEMENT);
      expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step('Given I entered the InnBusiness Pay page', async () => {
        await innBusinessPayTab.click();
        await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);
        await expect(innBusinessPayTab).toHaveAttribute('data-state', 'active');
      });

      await test.step(`When I check the first section page, first title should appear`, async () => {
        await expect(firstTitle).toBeVisible();
      });

      await test.step(`When I check the page a list of account holders are displayed`, async () => {
        await expect(accountHolderContainer).toBeVisible();
      });
      await test.step(`When I check first account should have account name, account serial number, account type`, async () => {
        await expect(firstAccountHolderName).toBeVisible();
        await expect(firstAccountHolderCode).toBeVisible();
        await expect(firstAccountHolderBadge).toBeVisible();
        await expect(firstAccountHolderManage).toBeVisible();
      });

      await test.step(`When I check the second section page, second title should appear`, async () => {
        await secondTitleContainer.scrollIntoViewIfNeeded();
        await expect(secondTitleContainer).toBeVisible();
      });

      await test.step(`When I scroll down on page for second section,  a widget should appear with title, text description and CTA for creating a new InnBusiness Pay card`, async () => {
        await payCardWidget.scrollIntoViewIfNeeded();
        await expect(payCardWidget).toBeVisible();
        await expect(widgetTitle).toBeVisible();
        await expect(widgetDescription).toBeVisible();
        await expect(widgetCTA).toBeVisible();
      });
    }
  });

  test('Verify InnBusiness Pay Manage Employees WL redirect - TestCase ID: 418346', async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();

    await test.step('GIVEN I want to navigate to WL employees page, WHEN I navigate to Manage, THEN The default submeniu option displayed is Manage Employee', async () => {
      await page.goto(`${IB_USER_MANAGEMENT}?tab=innbusiness-pay`);
    });

    await test.step(`WHEN I select the InnBusiness Pay tab, THEN I can find the following menu: "Allow employees to manage your credit account" Manage employees (external link to WL employees page for that particular account)`, async () => {
      const firstAccountHolderManage = page.getByTestId('AccountHolder-Account-1-manage-employees');
      const manageEmployeesButton = firstAccountHolderManage.locator('button');

      await test.step('WHEN I click on Manage Employees hyperlinkText', async () => {
        await manageEmployeesButton.click();
      });

      await test.step('THEN I will be redirected to WL page for that particular account NOTE: the user will be redirected to WL Manage Employee website and he is already logged in.', async () => {
        await page.waitForURL((url) => url.toString() === WORLDLINE_URL);
      });
    });
  });
});
