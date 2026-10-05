import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_Booker,
  IB_Self_Booker,
  IB_FLOW_URL,
} from '@WB-playwright/constants';
import { LoginBB, getCurrentDate } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Search_Container_IB } from './elements';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];
const calendarErrorId =
  config.DEVICE === 'desktop'
    ? 'IB-Date-Picker-ErrorTooltip'
    : 'IB-Date-Picker-ErrorTooltip-Mobile';
test.describe('Arrival date in the past', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 380719`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const pastDate = getCurrentDate(-2);

      await test.step('Given I am on the Search results page, when I alter the URL and change arrival date in the past', async () => {
        await page.goto(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${pastDate.day}&ARRmm=${pastDate.month}&ARRyyyy=${pastDate.year}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );
      });

      const search = await Search_Container_IB(page);
      const calendarElement = search.getByTestId('IB-Date-Picker-Input');
      const errorTooltip = search.getByTestId(calendarErrorId);

      await test.step('Then an error message will be displayed and the field is highlighted in red', async () => {
        await expect(calendarElement).toHaveClass(/outline-error/);
        await expect(errorTooltip).toBeVisible();
      });
    });
  });
});

test.describe('Arrival date in the future', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 380720`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const futureDate = getCurrentDate(370);

      await test.step('Given I am on the Search results page, when I alter the URL and change arrival date in the future', async () => {
        await page.goto(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${futureDate.day}&ARRmm=${futureDate.month}&ARRyyyy=${futureDate.year}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );
      });

      const search = await Search_Container_IB(page);
      const calendarElement = search.getByTestId('IB-Date-Picker-Input');
      const errorTooltip = search.getByTestId(calendarErrorId);

      await test.step('Then an error message will be displayed and the field is highlighted in red', async () => {
        await expect(calendarElement).toHaveClass(/outline-error/);
        await expect(errorTooltip).toBeVisible();
      });
    });
  });
});

test.describe('Invalid arrival date', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 381059`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const invalidDate = 'abcdef';

      await test.step('Given I am on the Search results page, when I alter the URL and change arrival date in the future', async () => {
        await page.goto(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${invalidDate}&ARRmm=${invalidDate}&ARRyyyy=${invalidDate}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );
      });

      const search = await Search_Container_IB(page);
      const calendarElement = search.getByTestId('IB-Date-Picker-Input');
      const errorTooltip = search.getByTestId(calendarErrorId);

      await test.step('Then an error message will be displayed and the field is highlighted in red', async () => {
        await expect(calendarElement).toHaveClass(/outline-error/);
        await expect(errorTooltip).toBeVisible();
      });
    });
  });
});

test.describe('Invalid Number of Nights', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 381119`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      const currentDate = getCurrentDate();
      const numberOfNights = 15;

      await test.step('Given I am on the Search results page, when I alter the URL and change arrival date in the future', async () => {
        await page.goto(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${currentDate.day}&ARRmm=${currentDate.month}&ARRyyyy=${currentDate.year}&NIGHTS=${numberOfNights}&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );
      });

      const search = await Search_Container_IB(page);
      const calendarElement = search.getByTestId('IB-Date-Picker-Input');
      const errorTooltip = search.getByTestId(calendarErrorId);

      await test.step('Then an error message will be displayed and the field is highlighted in red', async () => {
        await expect(calendarElement).toHaveClass(/outline-error/);
        await expect(errorTooltip).toBeVisible();
      });
    });
  });
});
