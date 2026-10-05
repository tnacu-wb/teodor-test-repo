import { config } from '@WB-playwright/config';
import {
  IB_Tethered_Travel_Manager,
  IB_CARD_MANAGEMENT,
  IB_PAY_CARD_MANAGEMENT,
  IB_CENTRALLY_CARD_MANAGEMENT,
  IB_HOME,
  IB_USER_MANAGEMENT,
} from '@WB-playwright/constants';
import { getLabel, LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Side_Bar_IB } from '../../Header/Search/elements';

test('Card Management Tabs - Travel Manager - TestCase ID: 385096', async ({ browser }) => {
  const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
  const page = await context.newPage();
  await page.goto(IB_CARD_MANAGEMENT);

  expect(page.getByTestId('ManageCardsPage-container')).toBeVisible();

  if (config.DEVICE === 'mobile') {
    const manageChevronButton = page.getByTestId('Mobile-Nav-Third-Level-Container');
    const thirdLevelManageMenu = page.getByTestId('Mobile-Nav-activeLinkThirdLevel');
    const thirdLevelDrawer = page.getByTestId('Third-Level-Drawer');
    const innBusinessPayTab = page.getByTestId('Manage-Cards-Inn-Business-Pay-Sidebar-Link');
    const centrallyStoredTab = page.getByTestId('Manage-Cards-Inn-Business-Sidebar-Link');

    await test.step('When I click on InnBusiness Pay', async () => {
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await innBusinessPayTab.click();

      await expect(page, 'Then the URL changes to ?tab=innbusiness-pay').toHaveURL(
        `${IB_PAY_CARD_MANAGEMENT}`
      );
      await expect(thirdLevelManageMenu).toHaveText('InnBusiness Pay');
    });

    await test.step('When I click on Centrally Stored', async () => {
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await centrallyStoredTab.click();

      await expect(page, 'Then the URL changes to ?tab=centrally-stored').toHaveURL(
        `${IB_CARD_MANAGEMENT}`
      );
      await expect(thirdLevelManageMenu).toHaveText(await getLabel('CENTRALLY_STORED'));
    });
  } else {
    await test.step('When I click on InnBusiness Pay', async () => {
      const innBusinessPayTab = page.locator("a[href='?tab=innbusiness-pay'] > span");
      await innBusinessPayTab.click();

      await expect(page).toHaveURL(`${IB_PAY_CARD_MANAGEMENT}`);
      await expect(innBusinessPayTab).toHaveAttribute('data-state', 'active');
    });

    await test.step('When I click on Centrally Stored', async () => {
      const centrallyStoredTab = page.locator("a[href='?tab=centrally-stored'] > span");
      await centrallyStoredTab.click();

      await expect(page).toHaveURL(`${IB_CENTRALLY_CARD_MANAGEMENT}`);
      await expect(centrallyStoredTab).toHaveAttribute('data-state', 'active');
    });
  }
});

test.describe('[Mobile] Card Management navigation - TestCase ID: 389722, 389721', () => {
  // 'https://whitbread.yourzephyr.com/flex/html5/tcr/467;testcaseId=1298584;viewType=list;pageSize=50;pageView=search;offset=0;searchText=testcaseId%20in%20%28389722,389721%29;searchType=zql;inRelease=true;currentIndex=1;usageHistoryGridSize=50'
  if (config.DEVICE !== 'mobile') {
    return;
  }

  test('Travel Manager', async ({ browser }) => {
    const context = await LoginBB(browser, IB_Tethered_Travel_Manager);
    const page = await context.newPage();
    await page.goto(IB_HOME);

    const sidebar = await Side_Bar_IB(page);
    const manageMenuButton = sidebar.getByTestId('Manage-Sidebar-Link');
    const secondLevelCollapseButton = page.getByTestId('Second-Level-Collapse-Icon');
    const secondLevelManageMenu = secondLevelCollapseButton.locator(
      'xpath=/following-sibling::div'
    );
    const cardManagementButton = secondLevelManageMenu.getByTestId('CardManagement-Sidebar-Link');
    const centrallyStoredButton = page.getByTestId('Manage-Cards-Inn-Business-Sidebar-Link');
    const innBusinessPayButton = page.getByTestId('Manage-Cards-Inn-Business-Pay-Sidebar-Link');

    const thirdLevelExpandButton = page.getByTestId('Third-Level-Expand-Button').locator('img');
    const thirdLevelManageMenu = page.getByTestId('Mobile-Nav-activeLinkThirdLevel');
    const manageChevronButton = page.getByTestId('Mobile-Nav-Third-Level-Container');
    const thirdLevelDrawer = page.getByTestId('Third-Level-Drawer');
    const backToManageArrowButton = thirdLevelDrawer.locator('xpath=/button[2]/img');
    const backToManageLabel = thirdLevelDrawer.locator('xpath=/button[2]/span');
    const manageEmployeesButton = secondLevelManageMenu.getByTestId('ManageEmployees-Sidebar-Link');

    await test.step('Given that I click on the Manage icon of the mobile sidebar bar at the bottom', async () => {
      await manageMenuButton.click();
    });

    await test.step('When I select Card Management entry from the list', async () => {
      await cardManagementButton.click();
      await expect(
        manageMenuButton.locator('//span'),
        '- Manage entry is displayed as selected'
      ).toHaveCSS('font-bold', '');
      await expect(
        thirdLevelManageMenu,
        '- The overlay is collapsed showing the Centrally stored entry'
      ).toHaveText(await getLabel('CENTRALLY_STORED'));
      await expect(
        thirdLevelExpandButton,
        '- There is an expand arrow so that I can navigate between the level 3 items or go back to level 2'
      ).toBeVisible();
    });

    await test.step('When I click on the expand arrow', async () => {
      await thirdLevelExpandButton.click();
      await expect(thirdLevelDrawer).toBeVisible();
      await expect(backToManageArrowButton, '- back to manage arrow').toBeVisible();
      await expect(backToManageLabel, '- back to manage label').toHaveText('Back to Manage');
      await expect(centrallyStoredButton, '- Centrally stored entry').toBeVisible();
      await expect(innBusinessPayButton, '- InnBusiness Pay entry').toBeVisible();
    });

    await test.step('When I click on InnBusiness Pay level 3 entry', async () => {
      await innBusinessPayButton.click();
      await expect(
        manageMenuButton.locator('//span'),
        '- Manage entry is still displayed as selected'
      ).toHaveCSS('font-bold', '');
      await expect(page, '- Innbusiness Pay page is displayed and the URL is updated').toHaveURL(
        `${IB_PAY_CARD_MANAGEMENT}`
      );
      await expect(
        thirdLevelManageMenu,
        '- The overlay is collapsed showing InnBusiness Pay entry'
      ).toHaveText('InnBusiness Pay');
      await expect(
        thirdLevelExpandButton,
        '- There is an expand arrow so that I can navigate between the level 3 items or go back to level 2'
      ).toBeVisible();
    });

    await test.step('When I click on Centrally Stored', async () => {
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await centrallyStoredButton.click();
      await expect(
        manageMenuButton.locator('//span'),
        '- Manage entry is still displayed as selected'
      ).toHaveCSS('font-bold', '');
      await expect(page, 'Centrally stored page is displayed and the URL is updated').toHaveURL(
        `${IB_CARD_MANAGEMENT}`
      );
      await expect(
        thirdLevelManageMenu,
        '- The overlay is collapsed showing Centrally stored entry'
      ).toHaveText(await getLabel('CENTRALLY_STORED'));
      await expect(
        thirdLevelExpandButton,
        '- There is an expand arrow so that I can navigate between the level 3 items or go back to level 2'
      ).toBeVisible();
    });

    await test.step('When I click on Back to Manage', async () => {
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await backToManageArrowButton.click();
      await expect(
        secondLevelManageMenu,
        'Then I see The list of level 2 entries for Manage'
      ).toBeVisible();
      await expect(
        cardManagementButton.locator('//span'),
        'Card management entry is displayed as selected'
      ).toHaveCSS('font-bold', '');
      await expect(secondLevelCollapseButton, 'Collapse arrow').toBeVisible();
    });

    await test.step('When I click on the collapse arrow', async () => {
      await secondLevelCollapseButton.click();
      await expect(
        manageMenuButton.locator('//span'),
        '- Manage entry is still displayed as selected'
      ).toHaveCSS('font-bold', '');
      await expect(
        thirdLevelManageMenu,
        '- The overlay is collapsed showing the page I was (e.g Centrally stored if that was my selection)'
      ).toHaveText(await getLabel('CENTRALLY_STORED'));
      await expect(
        thirdLevelExpandButton,
        '- There is an expand arrow so that I can navigate between the level 3 items or go back to level 2'
      ).toBeVisible();
    });

    await test.step('When I click on the expand arrow, go Back to Manage and select another level 2 entry from the list (e.g Company details)', async () => {
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await backToManageArrowButton.click();
      await manageEmployeesButton.click();
      await expect(page, 'Then the page is changed to my selection').toHaveURL(
        `${IB_USER_MANAGEMENT}`
      );
    });
  });
});
