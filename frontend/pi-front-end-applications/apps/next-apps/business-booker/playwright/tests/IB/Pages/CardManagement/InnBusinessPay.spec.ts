import { config } from '@WB-playwright/config';
import {
  IB_Tethered_Travel_Manager,
  IB_Travel_Manager,
  IB_CARD_MANAGEMENT,
  IB_PAY_CARD_MANAGEMENT,
} from '@WB-playwright/constants';
import { getLabel, goToInnBusinessPayTab, Labels, LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';
import { useTranslationServer } from '@whitbread-eos/utils/server';

test.describe('InnBusiness Pay Add/Download buttons', () => {
  test('Add new card button - Travel Manager - TestCase ID: 388067, 385098', async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);
    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

    const addNewCardButton = page.getByTestId('InnBusinessPayTab-add-card-button');
    const downloadButton = page.getByTestId('DownloadButton');

    await goToInnBusinessPayTab(page);

    await test.step(`When I check the top right side of the Card Management table`, async () => {
      await expect(addNewCardButton, 'Then I see the Add New Card Button').toBeVisible();
      await expect(addNewCardButton).toHaveClass(/hover/);
      await expect(addNewCardButton).toContainText(await getLabel('ADD_NEW_CARD'));
      await expect(downloadButton, 'Then I see the Download Button').toBeVisible();
      await expect(downloadButton).toHaveClass(/hover/);
      await expect(downloadButton).toContainText(await getLabel('DOWNLOAD'));
    });
  });
});

test.describe('InnBusiness Pay cards table', () => {
  test('Table header - TestCase ID: 385103, 388059', async ({ browser }) => {
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);
    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

    const innBusinessPayTableHeader = page.getByTestId('InnBusiness-DataTable-row').locator('//th');

    await goToInnBusinessPayTab(page);

    await test.step(`When I check the top side of the InnBusiness Pay table (the table header)
      Then I see the name of each column`, async () => {
      const innBusinessPayTableColumnNames = [
        await getLabel('YOUR_CARD'),
        await getLabel('CARD_HOLDER'),
        await getLabel('CARD_HOLDER_REGISTRATION'),
        await getLabel('CARD_NO'),
        await getLabel('CARD_STATUS'),
        '',
      ];
      expect(await innBusinessPayTableHeader.allInnerTexts()).toEqual(
        innBusinessPayTableColumnNames
      );
    });
  });
});

test.describe('InnBusiness Pay table filters', () => {
  test('Cancelled cards / Only my cards - Travel Manager - TestCase ID: 388070, 388072, 388073, 388503', async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);
    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

    const innBusinessPayFilters = page.getByTestId('InnBusinessPayFilters');
    const cancelledCardsCheckbox = page
      .getByTestId('InnBusinessPayFilters-cancelledCards')
      .locator('//button');
    const cancelledCardsStatus = page.locator('//span[@data-testid="CardStatus"]');
    const cancelledStatusCount = await cancelledCardsStatus.count();
    const onlyMyCardsCheckbox = page
      .getByTestId('InnBusinessPayFilters-onlyMyCards')
      .locator('//button');
    const checkIcon = page.locator('//td[contains(@data-testid,"DataTable-row-yourCard-")]');
    const checkIconCount = await checkIcon.count();

    await goToInnBusinessPayTab(page);

    await test.step(`When I check the top side of the Card Management table`, async () => {
      expect(innBusinessPayFilters, 'Then I see').toHaveText(
        `${await Labels.SHOW}${await Labels.CANCELLED_CARDS}${await Labels.ONLY_MY_CARDS}`
      );

      await test.step(`When I click on the "Cancelled" cards filter`, async () => {
        await cancelledCardsCheckbox.click();
        await expect(cancelledCardsCheckbox, 'Then "Cancelled cards" is checked').toBeChecked();
        for (let i = 0; i < cancelledStatusCount; i++) {
          const statusText = cancelledCardsStatus.nth(i);
          await expect(
            statusText,
            'Then cards with status "Cancelled" are displayed'
          ).toContainText(await getLabel('CANCELLED'));
        }
        await expect(page, 'Then the URL contains cancelledCards filter').toHaveURL(
          `${IB_PAY_CARD_MANAGEMENT}&cancelledCards=1`
        );
      });

      await test.step(`When I click on the "Cancelled" cards checkbox again`, async () => {
        await cancelledCardsCheckbox.click();
        await expect(
          cancelledCardsCheckbox,
          'Then "Cancelled cards" is un-checked'
        ).not.toBeChecked();
        for (let i = 0; i < cancelledStatusCount; i++) {
          const statusText = cancelledCardsStatus.nth(i);
          const allowedStatuses = [
            await getLabel('ACTIVE'),
            await getLabel('DISPATCHING'),
            await getLabel('ACTIVATE'),
            await getLabel('CANCELLED'),
          ];
          expect(
            allowedStatuses,
            'Then cards with containing all the statuses are displayed'
          ).toContain(await statusText.innerText());
        }
        await expect(page, 'Then the URL no longer contains cancelledCards filter').toHaveURL(
          `${IB_PAY_CARD_MANAGEMENT}`
        );
      });

      await test.step(`When I click the box for "Only my cards" filter`, async () => {
        await onlyMyCardsCheckbox.click();
        await expect(onlyMyCardsCheckbox, 'Then "Only my cards" is checked').toBeChecked();
        for (let i = 0; i < checkIconCount; i++) {
          const hasCheckMark = checkIcon.nth(i).locator('svg').isVisible();
          await expect(
            hasCheckMark,
            'Then all the cards for the account holder are displayed'
          ).toBeTruthy();
        }
        await expect(page, 'Then the URL contains onlyMyCards filter').toHaveURL(
          `${IB_PAY_CARD_MANAGEMENT}&onlyMyCards=1`
        );
      });

      await test.step(`When I click on the "Only my cards" checkbox again`, async () => {
        await onlyMyCardsCheckbox.click();
        await expect(onlyMyCardsCheckbox, 'Then "Only my cards" is un-checked').not.toBeChecked();
        for (let i = 0; i < checkIconCount; i++) {
          const hasCheckMark = await checkIcon
            .nth(i)
            .locator('svg')
            .isVisible()
            .catch(() => false);
          if (!hasCheckMark) {
            const svgExists = await checkIcon.nth(i).locator('svg').count();
            expect(svgExists, 'Then all the cards are displayed').toBe(0);
          }
        }
        await expect(page, 'Then the URL no longer contains onlyMyCards filter').toHaveURL(
          `${IB_PAY_CARD_MANAGEMENT}`
        );
      });

      await test.step(`When I click on the "Only my cards" and the "Cancelled cards" checkbox`, async () => {
        await cancelledCardsCheckbox.click();
        await onlyMyCardsCheckbox.click();
        await expect(cancelledCardsCheckbox, 'Then "Cancelled cards" is selected').toBeChecked();
        await expect(onlyMyCardsCheckbox, 'Then "Only my cards" is selected').toBeChecked();
        await expect(page, 'Then the URL contains cancelledCards and onlyMyCards filter').toHaveURL(
          `${IB_PAY_CARD_MANAGEMENT}&cancelledCards=1&onlyMyCards=1`
        );
      });
    });
  });
});

test.describe('InnBusiness Pay - No tethered account created', () => {
  test('Travel Manager with no tethered account created - TestCase ID: 388069, 388071', async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);
    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();
    await goToInnBusinessPayTab(page);

    const innbusinessApplySection = page.getByTestId('Inn-Business-Pay-Apply-Container');

    await test.step(`When I reach InnBusiness Pay from Card Management menu with no tethered account`, async () => {
      const { t } = await useTranslationServer(config.LANGUAGE, 'cards');

      await expect(
        innbusinessApplySection.locator('xpath=/div[1]'),
        'Then I see first section containing'
      ).toBeVisible();
      await expect(innbusinessApplySection.locator('xpath=/div[1]//span[1]'), '- title').toHaveText(
        t('applyBanner.title')
      );
      await expect(
        innbusinessApplySection.locator('xpath=/div[1]//span[2]'),
        '- subtitle'
      ).toHaveText(t('applyBanner.subtitle'));
      await expect(
        innbusinessApplySection.locator('xpath=/div[1]//button[1]'),
        '- Apply now button'
      ).toHaveText(t('applyBanner.applyNowButton'));
      await expect(
        innbusinessApplySection.locator('xpath=/div[1]//button[2]'),
        '- Link an existing account button'
      ).toHaveText(t('applyBanner.linkAccountButton'));

      await expect(
        innbusinessApplySection.locator('xpath=/div[2]'),
        'Then I see second section'
      ).toBeVisible();
      await expect(
        innbusinessApplySection.locator('xpath=/div[2]/div[1]/span[1]'),
        '- Interest-free credit'
      ).toHaveText(t('creditBox.title'));
      await expect(
        innbusinessApplySection.locator('xpath=//div[2]/span[1]'),
        '- Expense management'
      ).toHaveText(t('expenseBox.title'));
      await expect(
        innbusinessApplySection.locator('xpath=//div[3]/span[1]'),
        '- Consolidated invoices'
      ).toHaveText(t('invoicesBox.title'));
      await expect(
        innbusinessApplySection.locator('xpath=/div[3]'),
        'Then I see third section'
      ).toBeVisible();
      await expect(
        innbusinessApplySection.locator('xpath=/div[3]//span[1]'),
        '- Already have an account title'
      ).toHaveText(t('linkAccountBanner.title'));
      await expect(
        innbusinessApplySection.locator('xpath=/div[3]//span[2]'),
        '- Already have an account - description'
      ).toHaveText(t('linkAccountBanner.subtitle'));
      await expect(
        innbusinessApplySection.locator('xpath=/div[3]//button'),
        '- Link an existing account button'
      ).toBeVisible();
    });
  });
});

test.describe('InnBusiness Pay Account holder section', () => {
  test('Travel Manager - TestCase ID: 385112', async ({ browser }) => {
    const { t } = await useTranslationServer(config.LANGUAGE, 'cards');
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_CARD_MANAGEMENT);
    expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

    const accountHolderSection =
      config.DEVICE === 'desktop'
        ? page.getByTestId('AccountHolder').nth(0)
        : page.getByTestId('AccountHolder').nth(1);

    const accountHolderDropdownButton = accountHolderSection.locator('xpath=//img');
    const accountHolderName = accountHolderSection.locator('xpath=div[1]');
    const accountHolderNumber = accountHolderSection.locator('xpath=span[1]');
    const accountHolderTypeLabel = accountHolderSection.locator('xpath=span[2]');
    const dropdownElements =
      config.DEVICE === 'desktop' ? page.locator('//body/div[3]') : page.locator('//body/div[4]');
    const firstAccountElement =
      config.DEVICE === 'desktop'
        ? dropdownElements.locator('xpath=/div/a[1]/div')
        : dropdownElements.locator('xpath=/div/a[1]/button');

    await goToInnBusinessPayTab(page);

    await test.step('When I reach InnBusiness Pay from Card management menu', async () => {
      await expect(accountHolderSection, 'Then I see the Account Holder section').toBeVisible();
      await expect(accountHolderDropdownButton, 'followed by a dropdown arrow').toBeVisible();
      await expect(accountHolderName, 'Then I see the Account Holder name').toBeVisible();
      await expect(accountHolderName).toHaveText('whibtread Digital');
      await expect(accountHolderNumber, 'Then I see the Account Holder number').toBeVisible();
      await expect(accountHolderNumber).toHaveText('3089503200100352');
      await expect(
        accountHolderTypeLabel,
        'Then I see the Account Holder type label'
      ).toBeVisible();
      await expect(accountHolderTypeLabel).toHaveText(t('badge.accountHolder'));
    });

    await test.step('When I click the dropdown arrow', async () => {
      await accountHolderDropdownButton.click();
      await expect(dropdownElements, 'Then a list of accounts is displayed').toBeVisible();
      await expect(firstAccountElement).toHaveText('(*0352) whibtread Digital');
    });

    await test.step('When I click on another account from the list', async () => {
      await firstAccountElement.click();
      await expect(page, 'Then the URL is updated with the selected account ID').toHaveURL(
        `${IB_PAY_CARD_MANAGEMENT}&account=3089503200100352`
      );
      await expect(dropdownElements, 'Then the overlay is closed').not.toBeVisible();
    });

    if (config.DEVICE === 'mobile') {
      await test.step('When I click the dropdown arrow and click close button', async () => {
        await accountHolderDropdownButton.click();
        // click on other element outside the dropdown
        await dropdownElements.locator('xpath=/button').click();
        await expect(dropdownElements, 'Then the overlay is closed').not.toBeVisible();
      });
    }
  });
});
