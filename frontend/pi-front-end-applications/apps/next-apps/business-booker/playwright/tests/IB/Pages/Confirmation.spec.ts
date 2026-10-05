import {
  IB_Travel_Manager,
  IB_Booker,
  IB_Self_Booker,
  Hotels,
  Constants,
} from '@WB-playwright/constants';
import { LoginBB, goToConfirmationPage } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];

test.describe('Header Visibility - TestCase ID: 403308, 403309', () => {
  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await goToConfirmationPage(page, user, Hotels.LONDON_GATWICK_AIRPORT.name, {
        time: 'now',
        card: Constants.VISA_CARD,
      });

      await test.step('When I check the top part of the page I see:', async () => {
        const IBHeaderLogo = page.getByTestId('IB-Logo');
        await expect(IBHeaderLogo, 'PI logo').toBeVisible();

        const businessSteps = page.getByTestId('BusinessSteps');
        await expect(
          businessSteps,
          'Flow steps with names under their corresponding circles'
        ).toBeVisible();

        const thanksForBooking = page.getByTestId('ThanksForBooking-Title-Name');
        await expect(thanksForBooking, 'Confirmed reservation').toBeVisible();
      });
    });
  });
});
