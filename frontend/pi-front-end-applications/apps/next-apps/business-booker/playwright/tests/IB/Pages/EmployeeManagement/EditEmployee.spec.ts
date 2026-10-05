import { IB_BASE_URL, IB_Travel_Manager, IB_USER_MANAGEMENT } from '@WB-playwright/constants';
import { LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import {
  SelectPaymentOption,
  userRoleOnlyOneRoleCanBeSelected,
  userRoleVisibleItems,
} from './add-edit-common';

test.describe('Verify that only one role can be selected, Verify the layout of Account Role - TestCase ID: 424590, 424672', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    await expect(
      page.getByTestId('InnBusiness-DataTable'),
      `Given that I'm logged in as a travel manager, When I Navigate to Manage - Manage Employees, Then i can see the list of employees`
    ).toBeVisible();

    await test.step('When I Edit an existing employee', async () => {
      const firstRow = page.getByTestId('DataTablePage-row-0');
      const editButton = firstRow.getByTestId('UserActions');
      await editButton.click();

      const href = await editButton.getAttribute('href');

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_BASE_URL}${href}`
      );
    });

    await userRoleOnlyOneRoleCanBeSelected(page);
    await userRoleVisibleItems(page);
  });
});
test.describe('Payment card form - TestCase ID: 424453, 424455, 424459,424460', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    await expect(
      page.getByTestId('InnBusiness-DataTable'),
      `Given that I'm logged in as a travel manager, When I Navigate to Manage - Manage Employees, Then i can see the list of employees`
    ).toBeVisible();

    await test.step('When I Edit an existing employee', async () => {
      const firstRow = page.getByTestId('DataTablePage-row-0');
      const editButton = firstRow.getByTestId('UserActions');
      await editButton.click();

      const href = await editButton.getAttribute('href');

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_BASE_URL}${href}`
      );
    });

    await test.step('When I scroll down to Payment Card form', async () => {
      await SelectPaymentOption(page);
    });
  });
});

test.describe('Registration Questions - TestCase ID: 427189, 427190, 427191, 427192, 427194', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    await expect(
      page.getByTestId('InnBusiness-DataTable'),
      `Given that I'm logged in as a travel manager, When I Navigate to Manage - Manage Employees, Then i can see the list of employees`
    ).toBeVisible();

    await test.step('When I Edit an existing employee', async () => {
      const firstRow = page.getByTestId('DataTablePage-row-0');
      const editButton = firstRow.getByTestId('UserActions');
      await editButton.click();

      const href = await editButton.getAttribute('href');

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_BASE_URL}${href}`
      );
    });

    await test.step('Then I can see the following items:', async () => {
      await expect(page.getByTestId('Registration-Questions-Heading'), 'Title').toBeVisible();
      await expect(
        page.getByTestId('Registration-Question-Label-0'),
        'Non mandatory Question'
      ).toBeVisible();
      await expect(
        page.getByTestId('Registration-Question-Label-1'),
        'Mandatory Question'
      ).toBeVisible();
    });
  });
});
