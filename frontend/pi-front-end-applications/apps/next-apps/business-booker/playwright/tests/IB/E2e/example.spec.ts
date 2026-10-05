import {
  HotelRate,
  Hotels,
  IB_Travel_Manager,
  Locations,
  RoomComposition,
  getSearchCriteriaAndHotelAvailability,
} from '@WB-playwright/constants';
import { searchHotels, LoginBB } from '@WB-playwright/utils';
import { test, expect } from '@playwright/test';

const users = [IB_Travel_Manager];

test.describe.skip('E2e search flow', () => {
  users.forEach((user) => {
    test(`E2E search flow and booking flow - ${user.title} - TestCase ID: 11111`, async ({
      browser,
    }) => {
      const context = await LoginBB(browser, user);
      const page = await context.newPage();

      await test.step('Given that I reached SRP on a booking flow', async () => {
        const response = await getSearchCriteriaAndHotelAvailability({
          hotelLocation: Locations.FREIBURG.name,
          hotelId: Hotels.FREIBURG_CITY_SUD.id,
          daysNumberForArrivalDate: 6,
          rooms: [
            RoomComposition.DOUBLE_1_ADULT_0_CHILDREN,
            RoomComposition.FAMILY_1_ADULT_2_CHILDREN,
          ],
          ratePlanCode: HotelRate.STANDARD.ratePlanCode,
        });
        const searchCriteria = response.searchCriteria;
        await searchHotels(page, searchCriteria, true);
      });

      const HDPWrapper = page.getByTestId('HotelDetailsBBPage-Wrapper');

      await test.step('Should display HDP', async () => {
        await expect(HDPWrapper).toBeVisible();
      });
    });
  });
});
