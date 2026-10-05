import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_USER_MANAGEMENT,
  IB_LoadMore_TravelManager,
} from '@WB-playwright/constants';
import { getLabel, LoginBB, manageEmployeesTableSearch } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

test.describe('Layout - TestCase ID: 413455, 418169, 424438, 424427', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    const innBusinessTitle = page.getByTestId('InnBusinessTab-title');
    const innBusinessTab = page.locator("a[href='?tab=innbusiness'] > span");
    const companyEmployeesText = page.getByTestId('CompanyEmployees-text');
    const searchEmployeeInput = page.getByTestId('SearchEmployeeInput-search-employees-Input');
    const downloadButton = page.getByTestId('InnBusinessTab-DownloadButton');
    const addEmployeesButton = page.getByTestId('InnBusinessTab-add-employee-button');
    const toolTipIcon = page.getByTestId('InnBusinessTab-title-icon');
    const toolTip = page.getByTestId('InnBusinessTab-InfoTooltip');

    if (config.DEVICE === 'mobile') {
      await expect(
        page.getByTestId('Mobile-Nav-activeLinkThirdLevel'),
        'Given I navigate to Manage and click on Manage Employees, Then second level menu is displayed'
      ).toBeVisible();
      const expandButton = page.getByTestId('Third-Level-Expand-Button');
      await expect(expandButton).toBeVisible();
      await expandButton.click();

      await test.step('THEN the second level expands having the following sub-menus:', async () => {
        const innBusinessButton = page.getByTestId('Manage-Employees-Inn-Business-Sidebar-Link');
        await expect(innBusinessButton, 'InnBusiness').toBeVisible();
        await expect(
          page.getByTestId('Manage-Employees-Inn-Business-Pay-Sidebar-Link'),
          'InnBusiness Pay'
        ).toBeVisible();
        await innBusinessButton.click();
      });

      await test.step('THEN I can find the following items one below another', async () => {
        const pageTitle = page.getByTestId('ManageEmployeesPage-container').locator('h1');
        await expect(pageTitle, 'Manage employee page title').toBeVisible();
        await expect(innBusinessTitle, 'Title').toBeVisible();
        await expect(toolTipIcon, 'Tooltip').toBeVisible();
        await toolTipIcon.click();
        await expect(toolTip, 'The tooltip info is displayed').toBeVisible();
        await expect(downloadButton, 'Download button').toBeVisible();
        await expect(addEmployeesButton, 'Add empployee button').toBeVisible();
        await expect(searchEmployeeInput, 'Searchbar').toBeVisible();
        await expect(page.getByTestId('InnBusiness-DataTable'), 'Table').toBeVisible();
      });
    } else {
      await expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step('Given I entered the InnBusiness Pay page', async () => {
        await innBusinessTab.click();
        await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness`);
        await expect(innBusinessTab).toHaveAttribute('data-state', 'active');
      });

      await test.step(`When I check the first section page, first title should appear with company employees`, async () => {
        await expect(innBusinessTitle).toBeVisible();
        await expect(companyEmployeesText).toBeVisible();
      });
      await test.step(`Tooltip should display information about users`, async () => {
        await toolTipIcon.hover();

        await expect(toolTip).toBeVisible();
        await expect(toolTip).toContainText(await getLabel('TOOLTIP_MESSAGE'));
      });

      await test.step(`Search Input should be displayed on page, along side with CTAs for download, add user and compare user`, async () => {
        await expect(searchEmployeeInput).toBeVisible();
        await expect(downloadButton).toBeVisible();
        await expect(addEmployeesButton).toBeVisible();
      });
    }
  });
});

test.describe('Search & Table - TestCase ID: 413455, 418187, 418222, 418260, 418261, 418262, 418263, 418196', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    const innBusinessTitle = page.getByTestId('InnBusinessTab-title');
    const innBusinessTab = page.locator("a[href='?tab=innbusiness'] > span");
    const dataTable = page.getByTestId('InnBusiness-DataTable');
    const dataTableAccountName = page.getByTestId('InnBusiness-DataTable-head-accountName');
    const dataTableAccountRole = page.getByTestId('InnBusiness-DataTable-head-accessLevel');
    const dataTableAccountStatus = page.getByTestId('InnBusiness-DataTable-head-employeeStatus');
    const dataTableAccountAction = page.getByTestId('InnBusiness-DataTable-head-actions');
    const clearFieldIcon = page.getByTestId('SearchEmployeeInput-search-input-icon');

    if (config.DEVICE === 'mobile') {
      await expect(dataTable).toBeVisible();
      await expect(dataTableAccountName, 'Name column is visible').toBeVisible();
      await expect(dataTableAccountAction, 'Actions column is visible').toBeVisible();
    } else {
      await expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step('Given I entered the InnBusiness page', async () => {
        await innBusinessTab.click();
        await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?tab=innbusiness`);
        await expect(innBusinessTab).toHaveAttribute('data-state', 'active');
        await expect(innBusinessTitle).toBeVisible();
      });

      await test.step(`Data Table should be displayed and header of table should be  Name, Account Role, Account status columns`, async () => {
        await expect(dataTable).toBeVisible();
        await expect(dataTableAccountName).toBeVisible();
        await expect(dataTableAccountRole).toBeVisible();
        await expect(dataTableAccountStatus).toBeVisible();
      });
      await test.step(`Should search by logged in user with email`, async () => {
        await manageEmployeesTableSearch(page, IB_Travel_Manager.email);
      });
      await test.step(`Should clear searchInput when click on X icon`, async () => {
        await expect(clearFieldIcon).toBeVisible();
        await clearFieldIcon.click();

        const searchInput = page.getByTestId('SearchEmployeeInput-search-employees-Input');

        await expect(searchInput).toHaveValue('');

        await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}`);
      });

      await test.step(`Should search by logged in user with name`, async () => {
        await manageEmployeesTableSearch(page, IB_Travel_Manager.name);
      });
    }
  });
});

test.describe('Add/Edit Personal Details - TestCase ID: 418266, 418267, 418268, 418269, 418270', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    const dataTable = page.getByTestId('InnBusiness-DataTable');

    if (config.DEVICE === 'mobile') {
      await expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();
      //no mobile testcase yet, will be added in further sprints
    } else {
      await expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step(`Given I reached manage employee page and I want to edit an employee`, async () => {
        await expect(dataTable).toBeVisible();
      });

      const initialURL = page.url();

      await test.step(`When I click on the Edit employee button`, async () => {
        const editButton = page.getByTestId('DataTablePage-row-actions-0').locator('a');
        await expect(editButton).toBeVisible();
        await editButton.click();
      });

      await test.step('Then I reach edit employee page and check the Personal details section, I see:', async () => {
        await page.waitForURL((url) => url.toString() !== initialURL);
      });

      const heading = page.getByTestId('Personal-Details-Heading');

      await expect(heading, 'Personal details sub-title').toBeVisible();

      await test.step('Dropdown for choosing employee title', async () => {
        const titleButton = page.getByTestId('Title-IB-Form-Select-Button');
        await expect(titleButton, 'Is visible').toBeVisible();
        await titleButton.click();
        const firstTitle = page.locator('[data-testid^="Title-"][data-testid$="-Option"]').first();
        await firstTitle.click();
        await titleButton.click();
      });

      await test.step('First Name Error Handling', async () => {
        const error = page.getByTestId('First-Name-Error-Tooltip');
        const firstNameInput = page.getByTestId('First-Name-Form-Input');
        await expect(firstNameInput, 'Is visible').toBeVisible();
        await firstNameInput.click();
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await firstNameInput.fill('1');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await firstNameInput.fill('John!');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await firstNameInput.fill('TheseAreThirtyAndMoreCharacters');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await firstNameInput.fill('John');
        await heading.click();
        await expect(error, 'Error is NOT visible').toBeHidden();
      });

      await test.step('Last Name Error Handling', async () => {
        const error = page.getByTestId('Last-Name-Error-Tooltip');
        const lastNameInput = page.getByTestId('Last-Name-Form-Input');
        await expect(lastNameInput, 'Is visible').toBeVisible();
        await lastNameInput.click();
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await lastNameInput.fill('1');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await lastNameInput.fill('Jonathan!');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await lastNameInput.fill('TheseAreThirtyAndMoreCharacters');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await lastNameInput.fill('Jonathan');
        await heading.click();
        await expect(error, 'Error is NOT visible').toBeHidden();
      });

      await test.step('Email Error Handling', async () => {
        const error = page.getByTestId('Email-Error-Tooltip');
        const emailInput = page.getByTestId('Email-Form-Input');
        await expect(emailInput, 'Is visible').toBeVisible();
        await emailInput.click();
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await emailInput.fill('notEmail@.c');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await emailInput.fill('test@mailinator.com');
        await heading.click();
        await expect(error, 'Error is NOT visible').toBeHidden();
      });

      await test.step('Email Error Handling', async () => {
        const error = page.getByTestId('Phone-Number-Error-Tooltip');
        const phoneNumberInput = page.getByTestId('Phone-Number-Form-Input');
        await expect(phoneNumberInput, 'Is visible').toBeVisible();
        await phoneNumberInput.click();
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await phoneNumberInput.fill('7777777a');
        await heading.click();
        await expect(error, 'Error is visible').toBeVisible();
        await phoneNumberInput.fill('77777777');
        await heading.click();
        await expect(error, 'Error is NOT visible').toBeHidden();
      });
    }
  });
});

test.describe('Resend Activation Modal - TestCase ID: 424407, 424410, 424428', () => {
  test(`${IB_Travel_Manager.title}`, async ({ browser }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);

    if (config.DEVICE === 'mobile') {
      //not yet available
    } else {
      const resendRowButton = page.getByTestId('Resend-Activation-Row-Button');
      const XButton = page.getByTestId('Dialog-X-Close-Button');
      const modalTitle = page.getByTestId('Resend-Activation-Box-Title');
      const employeeFullName = page.getByTestId('Resend-Activation-Full-Name');
      const employeeEmail = page.getByTestId('Resend-Activation-Email');
      const cancelButton = page.getByTestId('Resend-Activation-Cancel-Button');
      const resendButton = page.getByTestId('Resend-Activation-Submit-Button');
      const toast = page.getByTestId('Toast');
      await expect(page.getByTestId('ManageEmployeesPage-container')).toBeVisible();

      await test.step('Check table for inactive user and click on Resend activation button', async () => {
        await manageEmployeesTableSearch(page, 'resend');
        await expect(resendRowButton).toBeVisible();
        await resendRowButton.click();
      });

      await expect(modalTitle, 'Then Resend activation email popup is displayed').toBeVisible();

      await test.step('When I inspect the popup', async () => {
        await expect(modalTitle, 'Then I see title').toBeVisible();
        await expect(XButton, 'I see X button').toBeVisible();
        await expect(employeeFullName, 'I see employee full name').toBeVisible();
        await expect(employeeEmail, 'I see employee email').toBeVisible();
        await expect(cancelButton, 'I see Cancel button').toBeVisible();
        await expect(resendButton, 'I see resend button').toBeVisible();
      });

      await test.step('When I click X button', async () => {
        await XButton.click();
        await expect(modalTitle, 'Then the popup is closed').toBeHidden();
      });

      await resendRowButton.click();

      await test.step('When I click the Cancel button', async () => {
        await cancelButton.click();
        await expect(modalTitle, 'Then the popup is closed').toBeHidden();
      });

      await resendRowButton.click();

      await test.step('When I click Resend activation code button', async () => {
        await resendButton.click();
        await expect(modalTitle, 'Then the popup is closed').toBeHidden();
        await expect(toast, 'Toast message is visible').toBeVisible();
      });
    }
  });
});

test.describe('Load More Pagination - TestCase ID: 418214, 424403', () => {
  test(`Number of employees < 15, button is hidden for : ${IB_Travel_Manager.email}`, async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_Travel_Manager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);
    const dataTable = page.getByTestId('InnBusiness-DataTable');
    const manageEmployeesContainer = page.getByTestId('ManageEmployeesPage-container');
    const loadMoreButton = page.getByTestId('DataTablePage-LoadMoreButton');

    await expect(manageEmployeesContainer).toBeVisible();

    await test.step('Expect table to be visible', async () => {
      await expect(dataTable).toBeVisible();
    });

    await test.step('When I scroll down load more button should not be visible', async () => {
      await expect(loadMoreButton).not.toBeVisible();
    });

    await test.step(`Should search by name ${IB_Travel_Manager.name} and load more button is still not visible`, async () => {
      await manageEmployeesTableSearch(page, IB_Travel_Manager.name);
    });
  });
  test(`Number of employees > 15 , button is displayed for : ${IB_LoadMore_TravelManager.email}`, async ({
    browser,
  }) => {
    const context = await LoginBB(browser, IB_LoadMore_TravelManager);

    const page = await context.newPage();

    await page.goto(IB_USER_MANAGEMENT);
    const dataTable = page.getByTestId('InnBusiness-DataTable');
    const manageEmployeesContainer = page.getByTestId('ManageEmployeesPage-container');
    const loadMoreButton = page.getByTestId('DataTablePage-LoadMoreButton');
    const firstRowAfterLoadMore = page.getByTestId('InnBusinessTab-row-loadMore-0-0');
    const firstRowAfterSecondLoadMore = page.getByTestId('InnBusinessTab-row-loadMore-1-0');

    await expect(manageEmployeesContainer).toBeVisible();

    await test.step('Expect table to be visible', async () => {
      await expect(dataTable).toBeVisible();
    });

    await test.step('When I scroll down load more button should be visible', async () => {
      await expect(loadMoreButton).toBeVisible();
    });
    await test.step('When I click on Load more, 15 more results should be displayed', async () => {
      await loadMoreButton.click();
      await expect(firstRowAfterLoadMore).toBeVisible();
    });

    await test.step('When I click again on Load more, 15 more results should be displayed', async () => {
      await loadMoreButton.click();
      await expect(firstRowAfterSecondLoadMore).toBeVisible();
    });

    await test.step(`Should search by name`, async () => {
      await manageEmployeesTableSearch(page, 'Vlad');
    });

    await test.step(`Click again on load More`, async () => {
      await loadMoreButton.click();
      await expect(firstRowAfterLoadMore).toBeVisible();
    });
    await test.step(`Load more button is no longer displayed, all results has been displayed in table`, async () => {
      await expect(loadMoreButton).not.toBeVisible();
    });
  });
});
