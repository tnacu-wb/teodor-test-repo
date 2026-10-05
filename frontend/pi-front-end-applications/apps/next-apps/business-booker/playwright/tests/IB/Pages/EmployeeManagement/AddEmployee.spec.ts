import { IB_Travel_Manager, IB_USER_MANAGEMENT } from '@WB-playwright/constants';
import { LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import {
  SelectPaymentOption,
  userRoleOnlyOneRoleCanBeSelected,
  userRoleVisibleItems,
} from './add-edit-common';

test.describe('Verify that only one role can be selected, Verify the layout of Account Role - TestCase ID: 424671, 424672', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    await expect(
      page.getByTestId('InnBusiness-DataTable'),
      `Given that I'm logged in as a travel manager, When I Navigate to Manage - Manage Employees, Then i can see the list of employees`
    ).toBeVisible();

    await test.step('When I Add employee', async () => {
      const addButton = page.getByTestId('InnBusinessTab-add-employee-button');
      await addButton.click();

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_USER_MANAGEMENT}/add`
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

    await test.step('When I Add employee', async () => {
      const addButton = page.getByTestId('InnBusinessTab-add-employee-button');
      await addButton.click();

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_USER_MANAGEMENT}/add`
      );
    });

    await test.step('When I scroll down to Payment Card form', async () => {
      await SelectPaymentOption(page);
    });
  });
});

test.describe('EN Form - TestCase ID: 429048, 429053, 429110, 429112, 429113, 429164', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    await expect(
      page.getByTestId('InnBusiness-DataTable'),
      `Given I want to add/edit a company address for an employee, When I Navigate to Manage - Manage Employees, Then i can see the list of employees`
    ).toBeVisible();

    await test.step('When I click add/edit employee page', async () => {
      const addButton = page.getByTestId('InnBusinessTab-add-employee-button');
      await addButton.click();

      await expect(page, 'Then I am redirected to employee details page').toHaveURL(
        `${IB_USER_MANAGEMENT}/add`
      );
    });

    await test.step('And check Company address section, ', async () => {
      const searchAddressButton = page.getByTestId(
        'CompanyAddressForm-search-for-new-address-text'
      );
      await expect(
        searchAddressButton,
        'Then the company address is pre-populated, and a Search for a new address URL CTA is displayed under the address'
      ).toBeVisible();
      await test.step('Then I click the CTA', async () => {
        await searchAddressButton.click();
      });
    });

    const postcodeInput = page.getByTestId('postCode-Form-Input');
    const postcodeFindButton = page.getByTestId('CompanyAddressForm-findAddressButton');
    const manualAddressButton = page.getByTestId('IB-ManualAddress-Button');
    const addressLine1 = page.getByTestId('Address-Line-1-Form-Input');
    const addressLine2 = page.getByTestId('Address-Line-2-Form-Input');
    const addressLine3 = page.getByTestId('Address-Line-3-Form-Input');
    const addressLine4 = page.getByTestId('Address-Line-4-Form-Input');
    const countryButton = page.getByTestId('Manual-Countries-IB-Form-Select-Button');
    await test.step('Then the following fields are displayed:', async () => {
      await expect(postcodeInput, 'Postcode field').toBeVisible();
      await expect(postcodeFindButton, 'Find address button').toBeVisible();
      await expect(manualAddressButton, 'or enter address manually button').toBeVisible();
    });

    await test.step('When I click on or enter address manually URL:', async () => {
      await manualAddressButton.click();
      await expect(manualAddressButton, 'Then the Button is not displayed').toBeHidden();
      await expect(addressLine1, 'Address line 1 field is displayed').toBeVisible();
      await expect(addressLine2, 'Address line 2 optional field is displayed').toBeVisible();
      await expect(addressLine3, 'Address line 3 optional field is displayed').toBeVisible();
      await expect(addressLine4, 'Address line 4 optional field is displayed').toBeVisible();
      await expect(
        page.getByTestId('Manual-Countries-IB-Form-Select-Button'),
        'Country dropdown button is displayed'
      ).toBeVisible();
    });

    await test.step('When I input an invalid postcode, and click on the Find address button', async () => {
      await postcodeInput.fill('TR49A');
      await postcodeFindButton.click();
      await expect(
        page.getByTestId('postCode-Error-Tooltip'),
        'Then an error message is displayed under postcode field'
      ).toBeVisible();
    });

    await test.step('When I input a valid postcode, and click on the Find address button', async () => {
      await postcodeInput.fill('blo 0le');
      await postcodeFindButton.click();
      await expect(
        page.getByTestId('selectAddress-IB-Form-Select-Button'),
        'Then a dropdown field is displayed'
      ).toBeVisible();
      await expect(addressLine1, 'The fields are populated with the selected address').toHaveValue(
        '1 The Drive'
      );
    });

    await test.step('When I leave empty Address Line 1, And click outside the field, an error is displayed', async () => {
      await addressLine1.fill('');
      await addressLine1.blur();
      await expect(page.getByTestId('Address-Line-1-Error-Tooltip')).toBeVisible();
    });

    await test.step('When I leave empty Address Line 2/3/4, And click outside the field no error is displayed', async () => {
      await addressLine2.fill('');
      await addressLine2.blur();
      await expect(page.getByTestId('Address-Line-2-Error-Tooltip')).toBeHidden();
      await addressLine3.fill('');
      await addressLine3.blur();
      await expect(page.getByTestId('Address-Line-3-Error-Tooltip')).toBeHidden();
      await addressLine4.fill('');
      await addressLine4.blur();
      await expect(page.getByTestId('Address-Line-4-Error-Tooltip')).toBeHidden();
    });

    await test.step('When I click on the country dropdown button', async () => {
      await countryButton.click();
      const countryOption = page.getByTestId('Manual-Countries-AD-Option');
      await expect(countryOption, 'When I click on a country from the list').toBeVisible();
      await countryOption.click();
      await expect(
        countryButton,
        'The country field is filled with the selected country'
      ).toHaveText('Andorra');
    });
  });
});
