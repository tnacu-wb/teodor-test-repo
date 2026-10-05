import { config } from '@WB-playwright/config';
import { IB_Travel_Manager, IB_Booker, IB_Self_Booker } from '@WB-playwright/constants';
import { LoginBB, goToPaymentPage } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

const users = [IB_Booker, IB_Self_Booker];
const managerUser = [IB_Travel_Manager];

test.describe('Payment page rebranding', () => {
  users.forEach((user) => {
    test(`Payment page header rebranding - ${user.title} - TestCase ID: 403061, 403306`, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reached Payment page on a booking flow', async () => {
        await goToPaymentPage(page, user);
      });

      const paymentPage = page.getByTestId('paymentPageSection');

      await test.step('Wait Payment page to be loaded', async () => {
        await expect(paymentPage).toBeVisible();
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

          await expect(BusinessSteps, 'Current page step label text: Payments').toContainText(
            'Guest details2PaymentPayment'
          );
        }
      });

      if (config.DEVICE === 'desktop') {
        await test.step('When I check the flow steps for Payments Page, ', async () => {
          const FirstStep = BusinessSteps.getByText('2');
          await expect(FirstStep, 'Then step 2 is selected').toHaveClass(/bg-darkGrey1/);
          await expect(FirstStep, 'And "Payments" label text').toContainText('Payment');
          await expect(FirstStep, 'is written in bold characters').toHaveClass(/font-bold/);
        });
      }
    });
  });

  managerUser.forEach((user) => {
    test(`Payment page header rebranding - ${user.title} - TestCase ID: 403061, 403306`, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reached Payment page on a booking flow', async () => {
        await goToPaymentPage(page, user);
      });

      const paymentPage = page.getByTestId('paymentPageSection');

      await test.step('Wait Payment page to be loaded', async () => {
        await expect(paymentPage).toBeVisible();
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

          await expect(BusinessSteps, 'Current page step label text: Payments').toContainText(
            'Guest details2PaymentPayment'
          );
        }
      });

      if (config.DEVICE === 'desktop') {
        await test.step('When I check the flow steps for Payments Page, ', async () => {
          const FirstStep = BusinessSteps.getByText('2');
          await expect(FirstStep, 'Then step 2 is selected').toHaveClass(/bg-darkGrey1/);
          await expect(FirstStep, 'And "Payments" label text').toContainText('Payment');
          await expect(FirstStep, 'is written in bold characters').toHaveClass(/font-bold/);
        });
      }
    });
  });
});
