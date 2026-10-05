import { config } from '@WB-playwright/config';
import { extractCardNumber } from '@WB-playwright/utils';
import { test, expect, Page } from '@playwright/test';

export async function userRoleOnlyOneRoleCanBeSelected(page: Page) {
  await test.step('And only one role can be selected:', async () => {
    const travelManagerRadioButton = page.locator('#SUPER');
    const bookerRadioButton = page.locator('#BOOKER');

    await expect(travelManagerRadioButton).toHaveAttribute('data-state', 'checked');
    await expect(bookerRadioButton).toHaveAttribute('data-state', 'unchecked');

    await bookerRadioButton.click();

    await expect(travelManagerRadioButton).toHaveAttribute('data-state', 'unchecked');
    await expect(bookerRadioButton).toHaveAttribute('data-state', 'checked');
  });
}

export async function userRoleVisibleItems(page: Page) {
  await test.step('Then i can see the following items:', async () => {
    await expect(page.getByTestId('User-Role-Sub-Heading'), 'Title: Account role').toBeVisible();

    await test.step('Options with Radio Buttons: Travel Manager, Booker, Self-Booker, Guest', async () => {
      await expect(page.locator('#SUPER')).toBeVisible();
      await expect(page.locator('#BOOKER')).toBeVisible();
      await expect(page.locator('#SELF')).toBeVisible();
      await expect(page.locator('#STAYER')).toBeVisible();
    });

    await test.step('Small info icon: Tooltip that can be clicked and a pop-up message will be displayed.', async () => {
      const tooltipIcon = page.getByTestId('User-Role-Info-Tooltip-Icon');
      const tooltip = page.getByTestId('User-Role-Info-Tooltip');

      await expect(tooltipIcon).toBeVisible();

      if (config.DEVICE === 'mobile') {
        await tooltipIcon.click();
      } else {
        await tooltipIcon.hover();
      }

      await expect(tooltip).toBeVisible();
    });
  });
}
export async function SelectPaymentOption(page: Page) {
  const paymentCardFormContainer = page.getByTestId('PaymentCardForm');
  const cardNameHeading = page.getByTestId('Card-Name-Heading');
  const paymentTypeHeading = page.getByTestId('Payment-Type-Heading');
  const cardListButton = page.getByTestId('cardId-IB-Form-Select-Button');
  const cardList = page.getByTestId('cardId-IB-Form-Select-Dropdown');
  const cardListNoneOption = page.getByTestId('cardId-none-Option');
  const cardIcon = page.getByTestId('CardIcon').first();
  const paymentTypeFormInput = page.getByTestId('Payment-Type-Form-Input');
  const allOptions = cardList.locator('button');
  const secondListOption = allOptions.nth(1);
  await expect(paymentCardFormContainer).toBeVisible();

  await expect(cardNameHeading, `Then I expect to see the Card Name title`).toBeVisible();
  await expect(paymentTypeHeading, `Then I expect to see the Payment type title`).toBeVisible();

  await cardListButton.click();

  await expect(cardList, `Then I see the list with company cards`).toBeVisible();
  await expect(cardListNoneOption, `I can see the "none" option always`).toBeVisible();
  await expect(cardIcon, 'I can see the card type icon').toBeVisible();
  await expect(paymentTypeFormInput, `Payment type input should be disabled`).toBeDisabled();

  await cardListNoneOption.click();
  await expect(paymentTypeFormInput, `Then input has "none" as value`).toHaveValue('None');

  await cardListButton.click();
  await expect(secondListOption, `Then I want to select second option`).toBeVisible();

  await secondListOption.click();

  const secondOptionValue = (await secondListOption.textContent()) ?? '';

  const extractedCardNumber = extractCardNumber(secondOptionValue);

  await expect(paymentTypeFormInput, `Then input has value of selected option`).toHaveValue(
    extractedCardNumber ?? ''
  );
}
