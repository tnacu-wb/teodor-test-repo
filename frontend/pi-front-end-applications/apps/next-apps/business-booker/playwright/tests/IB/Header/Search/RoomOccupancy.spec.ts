import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_Booker,
  IB_HOME,
  IB_Self_Booker,
  IB_FLOW_URL,
} from '@WB-playwright/constants';
import { getLabel, LoginBB, getCurrentDate } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

import { Search_Container_IB } from './elements';

const users = [IB_Travel_Manager, IB_Booker];
const limitedUsers = [IB_Self_Booker];
const defaultValueRegex = /1\s+\S+,\s+1\s+\S+/;

test.describe('Add/Remove Room', () => {
  users.forEach((user) => {
    test(`${user.title} - TestCase ID: 384063, 384064, 384080, 384081, 396500, 396501, 396511, 396512`, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Go to Inn Business homepage', async () => {
        await page.goto(IB_HOME);
      });

      const searchBar = await Search_Container_IB(page);

      const roomOccupancyButton = searchBar.getByTestId('Room-Occupancy-Button');

      await test.step('Open room occupancy dropdown', async () => {
        await roomOccupancyButton.click();
      });

      const removeRoomOneButton = page.getByTestId('IB-Remove-Room-1-Button');

      await expect(removeRoomOneButton, 'A single room should not be removable').toBeHidden();
      const addRoomButton = page.getByTestId('IB-Add-Room-Button');
      await expect(addRoomButton, 'Add another room button should be visible').toBeVisible();

      await test.step('When I add a second room', async () => {
        await addRoomButton.click();
      });

      await test.step('Then I shall see:', async () => {
        const roomNumberTwo = page.getByTestId('IB-Room-2');
        await expect(roomNumberTwo, 'Room number 2').toBeVisible();
        const removeRoomTwoButton = page.getByTestId('IB-Remove-Room-2-Button');
        await expect(
          removeRoomTwoButton,
          'Remove Room option is displayed near the room number'
        ).toBeVisible();
        await expect(addRoomButton, 'Add another room option is displayed').toBeVisible();
      });

      await test.step('When I add a third room', async () => {
        await addRoomButton.click();
      });

      await test.step('Then I shall see:', async () => {
        const roomNumberThree = page.getByTestId('IB-Room-3');
        await expect(roomNumberThree, 'Room number 3').toBeVisible();
        const removeRoomThreeButton = page.getByTestId('IB-Remove-Room-3-Button');
        await expect(
          removeRoomThreeButton,
          'Remove Room option is displayed near the room number'
        ).toBeVisible();
        await expect(addRoomButton, 'Add another room option is displayed').toBeVisible();
      });

      await test.step('When I add a fourth room', async () => {
        await addRoomButton.click();
      });

      const roomNumberFour = page.getByTestId('IB-Room-4');
      await test.step('Then I shall see:', async () => {
        await expect(roomNumberFour, 'Room number 4').toBeVisible();
        const removeRoomFourButton = page.getByTestId('IB-Remove-Room-4-Button');
        await expect(
          removeRoomFourButton,
          'Remove Room option is displayed near the room number'
        ).toBeVisible();
        await expect(addRoomButton, 'Add another room option is NOT displayed').toBeHidden();
        const maxRoomsNotification = page.getByText('Unable to add more rooms');
        await expect(maxRoomsNotification, 'A notification is displayed').toBeVisible();
      });

      //When I scroll up and click the first Remove Room
      await test.step('When I scroll up and click the first Remove Room', async () => {
        await expect(removeRoomOneButton, 'Remove first room button is visible').toBeVisible();
        await removeRoomOneButton.click();
      });

      await expect(roomNumberFour, 'Then the number of rooms will decrease').toBeHidden();
    });
  });

  limitedUsers.forEach((user) => {
    test(`${user.title} - TestCase ID: 384073, 396520, 396502, 396503`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Go to Inn Business homepage', async () => {
        await page.goto(IB_HOME);
      });

      const searchBar = await Search_Container_IB(page);

      const roomOccupancyButton = searchBar.getByTestId('Room-Occupancy-Button');

      await test.step('Open room occupancy dropdown', async () => {
        await roomOccupancyButton.click();
      });

      const removeRoomOneButton = page.getByTestId('IB-Remove-Room-1-Button');

      await expect(
        removeRoomOneButton,
        'Then the Remove room link is NOT displayed next to the room'
      ).toBeHidden();
      const addRoomButton = page.getByTestId('IB-Add-Room-Button');
      await expect(addRoomButton, 'Add another room button should NOT be visible').toBeHidden();
    });
  });
});

test.describe('Altered SRP URL', () => {
  test.describe('Invalid adults or children number', () => {
    [...users, ...limitedUsers].forEach((user) => {
      test(`${user.title} - TestCase ID: 399535`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        const date = getCurrentDate(3);
        const invalidValue = 4;

        await test.step('When I alter the URL with an invalid occupancy and press Enter', async () => {
          await page.goto(
            `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${date.day}&ARRmm=${date.month}&ARRyyyy=${date.year}&NIGHTS=1&ROOMS=1&ADULT1=${invalidValue}&CHILD1=${invalidValue}&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
          );
        });

        const searchBar = await Search_Container_IB(page);
        const roomOccupancyInput = searchBar.getByTestId('Room-Occupancy-Button');

        let errorTooltip;

        if (config.DEVICE === 'desktop') {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip');
        } else {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip-Mobile');
        }

        await test.step('Then the field values are reset to default', async () => {
          const roomOccupancyText = await roomOccupancyInput.innerText();
          const isMatchingDefaultValue = defaultValueRegex.test(roomOccupancyText);
          await expect(isMatchingDefaultValue).toBeTruthy();
        });

        await test.step('And an error message will be displayed, explaining the user to enter a valid room composition', async () => {
          await expect(roomOccupancyInput).toHaveClass(/outline-error/);
          await expect(errorTooltip).toBeVisible();
        });
      });
    });
  });

  test.describe('Invalid room type', () => {
    [...users, ...limitedUsers].forEach((user) => {
      test(`${user.title} - TestCase ID: 399535`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        const date = getCurrentDate(3);
        const invalidValue = 'abcdef';

        await test.step('When I alter the URL with an invalid occupancy and press Enter', async () => {
          await page.goto(
            `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${date.day}&ARRmm=${date.month}&ARRyyyy=${date.year}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=${invalidValue}&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
          );
        });

        const searchBar = await Search_Container_IB(page);
        const roomOccupancyInput = searchBar.getByTestId('Room-Occupancy-Button');

        let errorTooltip;
        if (config.DEVICE === 'desktop') {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip');
        } else {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip-Mobile');
        }

        await test.step('Then the field values are reset to default', async () => {
          const roomOccupancyText = await roomOccupancyInput.innerText();
          const isMatchingDefaultValue = defaultValueRegex.test(roomOccupancyText);
          await expect(isMatchingDefaultValue).toBeTruthy();
        });

        await test.step('And an error message will be displayed, explaining the user to enter a valid room composition', async () => {
          await expect(roomOccupancyInput).toHaveClass(/outline-error/);
          await expect(errorTooltip).toBeVisible();
        });
      });
    });
  });

  test.describe('Invalid cot', () => {
    [...users, ...limitedUsers].forEach((user) => {
      test(`${user.title} - TestCase ID: 399535`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        const date = getCurrentDate(3);
        const invalidValue = 'abcdef';

        await test.step('When I alter the URL with an invalid occupancy and press Enter', async () => {
          await page.goto(
            `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${date.day}&ARRmm=${date.month}&ARRyyyy=${date.year}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=1&COT1=${invalidValue}&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
          );
        });

        const searchBar = await Search_Container_IB(page);
        const roomOccupancyInput = searchBar.getByTestId('Room-Occupancy-Button');

        let errorTooltip;

        if (config.DEVICE === 'desktop') {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip');
        } else {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip-Mobile');
        }

        await test.step('Then the field values are reset to default', async () => {
          const roomOccupancyText = await roomOccupancyInput.innerText();
          const isMatchingDefaultValue = defaultValueRegex.test(roomOccupancyText);
          await expect(isMatchingDefaultValue).toBeTruthy();
        });

        await test.step('And an error message will be displayed, explaining the user to enter a valid room composition', async () => {
          await expect(roomOccupancyInput).toHaveClass(/outline-error/);
          await expect(errorTooltip).toBeVisible();
        });
      });
    });
  });

  test.describe('Invalid number of rooms', () => {
    [...users, ...limitedUsers].forEach((user) => {
      test(`${user.title} - TestCase ID: 399535`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        const date = getCurrentDate(3);
        const invalidValue = 5;

        await test.step('When I alter the URL with an invalid occupancy and press Enter', async () => {
          await page.goto(
            `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=${date.day}&ARRmm=${date.month}&ARRyyyy=${date.year}&NIGHTS=1&ROOMS=${invalidValue}&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
          );
        });

        const searchBar = await Search_Container_IB(page);
        const roomOccupancyInput = searchBar.getByTestId('Room-Occupancy-Button');

        let errorTooltip;

        if (config.DEVICE === 'desktop') {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip');
        } else {
          errorTooltip = searchBar.getByTestId('IB-Room-Occupancy-ErrorTooltip-Mobile');
        }

        await test.step('Then the field values are reset to default', async () => {
          const roomOccupancyText = await roomOccupancyInput.innerText();
          const isMatchingDefaultValue = defaultValueRegex.test(roomOccupancyText);
          await expect(isMatchingDefaultValue).toBeTruthy();
        });

        await test.step('And an error message will be displayed, explaining the user to enter a valid room composition', async () => {
          await expect(roomOccupancyInput).toHaveClass(/outline-error/);
          await expect(errorTooltip).toBeVisible();
        });
      });
    });
  });
});

test.describe('Include a cot option', () => {
  [...users, ...limitedUsers].forEach((user) => {
    test(`${user.title} - TestCase ID: 384074`, async ({ browser }) => {
      const context = await LoginBB(browser, user);
      const page = await context.newPage();

      await test.step('Go to Inn Business homepage', async () => {
        await page.goto(IB_HOME);
      });

      const searchBar = await Search_Container_IB(page);
      const roomOccupancyButton = searchBar.getByTestId('Room-Occupancy-Button');
      await roomOccupancyButton.click();
      const cotToggle = page.getByTestId('IB-shouldIncludeCot-Switch-1');
      const includeCotLabel = cotToggle.locator('//following-sibling::div/span[1]');

      await test.step('When I check the search console at room occupancy component', async () => {
        await expect(cotToggle, 'Then I see include cot toggle displayed').toBeVisible();
        expect(includeCotLabel).toHaveText(await getLabel('INCLUDE_COT'));
        await expect(cotToggle, 'Then the toggle is off').not.toBeChecked();
      });

      await test.step('When I click the Include a cot toggle', async () => {
        await cotToggle.click();
        await expect(cotToggle, 'Then the toggle is on').toBeChecked();
      });

      await test.step('When I click the Include a cot toggle again', async () => {
        await cotToggle.click();
        await expect(cotToggle, 'Then the toggle switches to off').not.toBeChecked();
      });
    });
  });
});
