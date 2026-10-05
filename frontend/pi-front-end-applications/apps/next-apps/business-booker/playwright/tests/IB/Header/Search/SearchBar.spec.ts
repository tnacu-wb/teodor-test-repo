import { config } from '@WB-playwright/config';
import {
  IB_Travel_Manager,
  IB_Booker,
  IB_Self_Booker,
  IB_HOME,
  IB_FLOW_URL,
  Locations,
  Hotels,
  Constants,
} from '@WB-playwright/constants';
import {
  LoginBB,
  selectDateRange,
  getLocale,
  getLocaleAsString,
  getCurrentDate,
  goToHDP,
  goToSRP,
  Labels,
  getLabel,
} from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';
import { format, differenceInDays, addDays } from 'date-fns';

import { Search_Button_IB, Search_Container_IB } from './elements';
import { searchElementsNotInViewport } from './utils';

const users = [IB_Travel_Manager, IB_Booker, IB_Self_Booker];

test.describe('Submit search location', () => {
  test.describe('Submit Search with 1 adult and 1 children + cot', () => {
    users.forEach((user) => {
      test(`${user.title} - TestCase ID: 399693`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        await test.step('Go to Inn Business homepage', async () => {
          await page.goto(IB_HOME);
        });

        const desktopSearch = await Search_Container_IB(page);

        const locationInput = desktopSearch.getByTestId('IB-Location-Input');
        const datePickerInput = desktopSearch.getByTestId('IB-Date-Picker-Input');
        const datePickerDoneButton = desktopSearch.getByTestId(
          'IB-Date-Picker-Calendar-buttons-Done'
        );
        const roomOccupancyDoneButton = desktopSearch.getByTestId('IB-Room-Occupancy-Done-Button');
        const roomOccupancyButton = desktopSearch.getByTestId('Room-Occupancy-Button');

        const searchButton = await Search_Button_IB(page);

        const addChildrenMenuButton = page.getByTestId(
          'IB-RoomOccupancy-Children-Dropdown-1-IB-Select-Trigger'
        );

        await test.step('Click in location Input', async () => {
          await locationInput.click();
        });
        await test.step('Fill location', async () => {
          await locationInput.fill(Locations.LONDON.suggestion);
        });
        await test.step('Click on Calendar ', async () => {
          await datePickerInput.click();
        });

        const todayDate = getCurrentDate(2);
        const futureDate = getCurrentDate(5);
        const startDate = new Date(todayDate.year, todayDate.month - 1, todayDate.day);
        const endDate = new Date(futureDate.year, futureDate.month - 1, futureDate.day);

        await test.step('Select start and end day', async () => {
          await selectDateRange(page, startDate, endDate, getLocaleAsString(config.LANGUAGE));
        });

        await test.step('Click on Calendar done button', async () => {
          await datePickerDoneButton.click();
        });

        await expect(datePickerDoneButton, 'Calendar modal should be closed').toBeHidden();

        await test.step('Click on Room occupancy', async () => {
          await roomOccupancyButton.click();
        });
        await test.step('Click on children field', async () => {
          await addChildrenMenuButton.click();
        });
        const selector1Children = page.getByTestId('IB-RoomOccupancy-Children-Dropdown-1-1-Option');

        await test.step('Select 1 children', async () => {
          await selector1Children.click();
        });
        const selectCotForFirstRoom = page.getByTestId('IB-shouldIncludeCot-Switch-1');

        await test.step('Select COT for first room', async () => {
          await selectCotForFirstRoom.click();
        });

        await test.step('Click on Room occupancy done button', async () => {
          await roomOccupancyDoneButton.click();
        });

        await expect(roomOccupancyDoneButton, 'Room occupancy modal should be closed').toBeHidden();

        await test.step('Then Click on Search ', async () => {
          await searchButton.click();
        });

        const startDay = startDate.getDate();
        const startMonth = startDate.getMonth() + 1;
        const startYear = startDate.getFullYear();

        const nights = differenceInDays(endDate, startDate);

        await expect(page).toHaveURL(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=${Locations.LONDON.id}&ARRdd=${startDay}&ARRmm=${startMonth}&ARRyyyy=${startYear}&NIGHTS=${nights}&ROOMS=1&ADULT1=1&CHILD1=1&COT1=1&INTTYP1=FAM&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );

        await expect(locationInput).toHaveValue(Locations.LONDON.suggestion);
        const datePickerButtonText = await datePickerInput.textContent();

        const startDateFormattedFromButton = format(startDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });
        const endDateFormattedFromButton = format(endDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });

        expect(datePickerButtonText).toBe(
          `${startDateFormattedFromButton}|${endDateFormattedFromButton}`
        );

        const roomOccupancyButtonText = await roomOccupancyButton.textContent();
        expect(roomOccupancyButtonText).toBe(
          `1 ${await Labels.ADULT_LOWER_CASE}, 1 ${await Labels.CHILD_LOWER_CASE}, 1 ${await Labels.ROOM_LOWER_CASE}`
        );
      });
    });
  });
  test.describe('Submit Search with 2 adults and no children, no cot', () => {
    users.forEach((user) => {
      test(`${user.title} - TestCase ID: 399693`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        await test.step('Go to Inn Business homepage', async () => {
          await page.goto(IB_HOME);
        });

        const desktopSearch = await Search_Container_IB(page);

        const locationInput = desktopSearch.getByTestId('IB-Location-Input');
        const datePickerInput = desktopSearch.getByTestId('IB-Date-Picker-Input');
        const datePickerDoneButton = desktopSearch.getByTestId(
          'IB-Date-Picker-Calendar-buttons-Done'
        );
        const roomOccupancyDoneButton = desktopSearch.getByTestId('IB-Room-Occupancy-Done-Button');
        const roomOccupancyButton = desktopSearch.getByTestId('Room-Occupancy-Button');

        const searchButton = await Search_Button_IB(page);

        await test.step('Click in location Input', async () => {
          await locationInput.click();
        });
        await test.step('Fill location', async () => {
          await locationInput.fill(Locations.LONDON.suggestion);
        });
        await test.step('Click on Calendar ', async () => {
          await datePickerInput.click();
        });

        const todayDate = getCurrentDate(2);
        const futureDate = getCurrentDate(5);
        const startDate = new Date(todayDate.year, todayDate.month - 1, todayDate.day);
        const endDate = new Date(futureDate.year, futureDate.month - 1, futureDate.day);

        await test.step('Select start and end day', async () => {
          await selectDateRange(page, startDate, endDate, getLocaleAsString(config.LANGUAGE));
        });

        await test.step('Click on Calendar done button', async () => {
          await datePickerDoneButton.click();
        });

        await expect(datePickerDoneButton, 'Calendar modal should be closed').toBeHidden();

        await test.step('Click on Room occupancy', async () => {
          await roomOccupancyButton.click();
        });

        const addAdultsMenuButton = page.getByTestId(
          'IB-RoomOccupancy-Adults-Dropdown-1-IB-Select-Trigger'
        );

        await test.step('Click on adults field', async () => {
          await addAdultsMenuButton.click();
        });

        const selector2Adults = page.getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-1-Option');

        await test.step('Select 2 adults', async () => {
          await selector2Adults.click();
        });

        await test.step('Click on Room occupancy done button', async () => {
          await roomOccupancyDoneButton.click();
        });

        await expect(roomOccupancyDoneButton, 'Room occupancy modal should be closed').toBeHidden();

        await test.step('Then Click on Search ', async () => {
          await searchButton.click();
        });

        const startDay = startDate.getDate();
        const startMonth = startDate.getMonth() + 1;
        const startYear = startDate.getFullYear();

        const nights = differenceInDays(endDate, startDate);

        await expect(page).toHaveURL(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London,%20UK&PLACEID=${Locations.LONDON.id}&ARRdd=${startDay}&ARRmm=${startMonth}&ARRyyyy=${startYear}&NIGHTS=${nights}&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );

        await expect(locationInput).toHaveValue(Locations.LONDON.suggestion);
        const datePickerButtonText = await datePickerInput.textContent();

        const startDateFormattedFromButton = format(startDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });
        const endDateFormattedFromButton = format(endDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });

        expect(datePickerButtonText).toBe(
          `${startDateFormattedFromButton}|${endDateFormattedFromButton}`
        );

        const roomOccupancyButtonText = await roomOccupancyButton.textContent();
        expect(roomOccupancyButtonText).toBe(
          `2 ${await Labels.ADULTS_LOWER_CASE}, 1 ${await Labels.ROOM_LOWER_CASE}`
        );
      });
    });
  });
});
test.describe('Submit search hotel', () => {
  test.describe('Submit Search with Accessible room with 1 adult', () => {
    users.forEach((user) => {
      test(`${user.title} - TestCase ID: 397973, 397977, 397974`, async ({ browser }) => {
        const context = await LoginBB(browser, user);

        const page = await context.newPage();

        await test.step('Go to Inn Business homepage', async () => {
          await page.goto(IB_HOME);
        });

        const desktopSearch = await Search_Container_IB(page);

        const locationInput = desktopSearch.getByTestId('IB-Location-Input');
        const datePickerInput = desktopSearch.getByTestId('IB-Date-Picker-Input');
        const datePickerDoneButton = desktopSearch.getByTestId(
          'IB-Date-Picker-Calendar-buttons-Done'
        );
        const roomOccupancyDoneButton = desktopSearch.getByTestId('IB-Room-Occupancy-Done-Button');
        const roomOccupancyButton = desktopSearch.getByTestId('Room-Occupancy-Button');

        const searchButton = await Search_Button_IB(page);

        await test.step('Click in location Input', async () => {
          await locationInput.click();
        });
        await test.step('Fill location', async () => {
          await locationInput.fill(Hotels.LONDON_EUSTON.name);
        });
        await test.step('Click on Calendar ', async () => {
          await datePickerInput.click();
        });

        const todayDate = getCurrentDate(2);
        const futureDate = getCurrentDate(5);
        const startDate = new Date(todayDate.year, todayDate.month - 1, todayDate.day);
        const endDate = new Date(futureDate.year, futureDate.month - 1, futureDate.day);

        await test.step('Select start and end day', async () => {
          await selectDateRange(page, startDate, endDate, getLocaleAsString(config.LANGUAGE));
        });

        await test.step('Click on Calendar done button', async () => {
          await datePickerDoneButton.click();
        });

        await expect(datePickerDoneButton, 'Calendar modal should be closed').toBeHidden();

        await test.step('Click on Room occupancy', async () => {
          await roomOccupancyButton.click();
        });

        const changeRoomTypeButton = page.getByTestId(
          'IB-RoomOccupancy-RoomType-1-IB-Select-Trigger'
        );

        await test.step('Click on room type field', async () => {
          await changeRoomTypeButton.click();
        });

        const selectorForRoomTypeAccessible = page.getByTestId(
          'IB-RoomOccupancy-RoomType-1-2-Option'
        );

        await test.step('Select accessible room', async () => {
          await selectorForRoomTypeAccessible.click();
        });

        await test.step('Click on Room occupancy done button', async () => {
          await roomOccupancyDoneButton.click();
        });

        await expect(roomOccupancyDoneButton, 'Room occupancy modal should be closed').toBeHidden();

        await test.step('Then Click on Search ', async () => {
          await searchButton.click();
        });

        const startDay = startDate.getDate();
        const startMonth = startDate.getMonth() + 1;
        const startYear = startDate.getFullYear();

        const nights = differenceInDays(endDate, startDate);

        await expect(page).toHaveURL(
          `${IB_FLOW_URL}/search.html?searchModel.searchTerm=London%20Euston&LOCATION=${Hotels.LONDON_EUSTON.location}&ARRdd=${startDay}&ARRmm=${startMonth}&ARRyyyy=${startYear}&NIGHTS=${nights}&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DIS&BOOKINGCHANNEL=CBT&SORT=1&VIEW=2`
        );

        await expect(locationInput).toHaveValue(Hotels.LONDON_EUSTON.name);
        const datePickerButtonText = await datePickerInput.textContent();

        const startDateFormattedFromButton = format(startDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });
        const endDateFormattedFromButton = format(endDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
          locale: getLocale(config.LANGUAGE),
        });

        expect(datePickerButtonText).toBe(
          `${startDateFormattedFromButton}|${endDateFormattedFromButton}`
        );

        const roomOccupancyButtonText = await roomOccupancyButton.textContent();
        expect(roomOccupancyButtonText).toBe(
          `1 ${await Labels.ADULT_LOWER_CASE}, 1 ${await Labels.ROOM_LOWER_CASE}`
        );
      });
    });
  });
});

test.describe('[Mobile] Search bar - Design components - TestCase ID: 384535, 384537, 384539', () => {
  if (config.DEVICE !== 'mobile') {
    return;
  }

  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reach InnBusiness website ', async () => {
        await page.goto(IB_HOME);
      });

      await test.step('When I check the Search bar section of Global header, Then I see the following elements for the Search bar below the Global Header:', async () => {
        const search = await Search_Container_IB(page);

        const locationField = search.getByTestId('IB-Location-Container');
        await expect(locationField, 'Location fields with:').toBeVisible();

        const icon = locationField.locator('img[alt="Location Image"]');
        await expect(icon, 'location icon').toBeVisible();

        const input = locationField.getByTestId('IB-Location-Input');
        await expect(input, '"Where to?" placeholder text').toHaveAttribute(
          'placeholder',
          await getLabel('WHERE_TO')
        );
      });
    });
  });
});

test.describe('[Mobile][HDP] Search bar is not sticky - TestCase ID: 404303', () => {
  if (config.DEVICE !== 'mobile') {
    return;
  }

  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reach InnBusiness website on HDP page', async () => {
        await goToHDP(page, user, Hotels.FRANKFURT_MESSE.name);
      });

      await test.step('When I scroll down', async () => {
        await page.evaluate(() => window.scrollBy(0, 1000));

        await test.step('Then the search bar, header or the search location Edit button are not sticky', async () => {
          await searchElementsNotInViewport(page);
        });
      });
    });
  });
});

test.describe('[Mobile][SRP] Search bar is not sticky - TestCase ID: 404304', () => {
  if (config.DEVICE !== 'mobile') {
    return;
  }

  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reach InnBusiness website on SRP page', async () => {
        await goToSRP(page, user, Locations.LONDON.suggestion);
      });

      await test.step('When I scroll down', async () => {
        const srpMapView = page.getByTestId('SRP-mapView');
        await expect(srpMapView).toBeVisible();

        await page.evaluate(() => window.scrollBy(0, 1000));

        await test.step('Then the search bar, header or the search location Edit button are not sticky', async () => {
          await searchElementsNotInViewport(page);
        });
      });
    });
  });
});

test.describe('[Mobile][SRP] Edit button - TestCase ID: 403353', () => {
  if (config.DEVICE !== 'mobile') {
    return;
  }

  users.forEach((user) => {
    test(`${user.title}`, async ({ browser }) => {
      const context = await LoginBB(browser, user);

      const page = await context.newPage();

      await test.step('Given that I reach InnBusiness website on SRP after a search was performed', async () => {
        await goToSRP(page, user, Locations.LONDON.suggestion);
      });

      const search = await Search_Container_IB(page);
      const editButton = page.getByTestId('Edit-Search-Button');

      await test.step('When I check the Search bar section of Global header, Then I see that the Search bar displays Edit option', async () => {
        await expect(editButton).toBeVisible();
        await expect(search).toBeHidden();

        await test.step('When I click on the Edit option', async () => {
          await editButton.click();
        });

        await test.step('Then the search is enabled and can be edited, having the location, date and rooms prefilled with the search criteria from before', async () => {
          await expect(search).toBeVisible();

          const locationInput = search.getByTestId('IB-Location-Input');
          const datePickerInput = search.getByTestId('IB-Date-Picker-Input');
          const roomOccupancyButton = search.getByTestId('Room-Occupancy-Button');

          await expect(locationInput).toHaveValue(Locations.LONDON.suggestion);
          const datePickerButtonText = await datePickerInput.textContent();
          const startDate = addDays(new Date(), 2);
          const endDate = addDays(new Date(), 3);
          const startDateFormattedFromButton = format(
            startDate,
            Constants.DAY_MONTH_NAME_YEAR_DATE,
            {
              locale: getLocale(config.LANGUAGE),
            }
          );
          const endDateFormattedFromButton = format(endDate, Constants.DAY_MONTH_NAME_YEAR_DATE, {
            locale: getLocale(config.LANGUAGE),
          });

          expect(datePickerButtonText).toBe(
            `${startDateFormattedFromButton}|${endDateFormattedFromButton}`
          );

          const roomOccupancyButtonText = await roomOccupancyButton.textContent();
          expect(roomOccupancyButtonText).toBe(
            `1 ${await Labels.ADULT_LOWER_CASE}, 1 ${await Labels.ROOM_LOWER_CASE}`
          );
        });
      });
    });
  });
});
