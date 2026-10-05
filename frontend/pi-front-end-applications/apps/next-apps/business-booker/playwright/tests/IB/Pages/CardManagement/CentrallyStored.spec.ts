import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_CARD_MANAGEMENT,
  IB_CENTRALLY_CARD_MANAGEMENT,
} from '@WB-playwright/constants';
import { getLabel, LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

test.describe('Centrally Stored card buttons', () => {
  test('Add new card button - Travel Manager - TestCase ID: 381609, 384133', async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);

    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

    const centrallyStoredTab = page.locator("a[href='?tab=centrally-stored'] > span");
    const addNewCardButton = page.locator(
      '//button[@data-testid="CentrallyStoredTab-add-card-button"]'
    );

    if (config.DEVICE !== 'mobile') {
      await test.step('Given I entered the Centrally stored page', async () => {
        await centrallyStoredTab.click();
        await expect(page).toHaveURL(`${IB_CENTRALLY_CARD_MANAGEMENT}`);
        await expect(centrallyStoredTab).toHaveAttribute('data-state', 'active');
      });

      await test.step(`When I check the top right side of the Card Management table`, async () => {
        await expect(addNewCardButton, 'Then I see the Add New Card Button').toBeVisible();
        await expect(addNewCardButton).toHaveClass(/hover/);
        await expect(addNewCardButton).toContainText(await getLabel('ADD_NEW_CARD'));
      });
    } else {
      const thirdLevelManageMenu = page.getByTestId('Mobile-Nav-activeLinkThirdLevel');
      await test.step('Given I entered the Centrally stored page', async () => {
        await expect(thirdLevelManageMenu).toHaveText(await getLabel('CENTRALLY_STORED'));
      });

      await test.step(`When I check above the Card Management table`, async () => {
        await expect(addNewCardButton, 'Then I see the Add New Card Button').toBeVisible();
        await expect(addNewCardButton).toContainText(await getLabel('ADD_NEW_CARD'));
      });
    }
  });
});
