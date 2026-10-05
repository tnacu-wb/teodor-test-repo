import { config } from '@WB-playwright/config';
import {
  IB_HOME,
  IB_BOOKINGS_URL,
  Hotels,
  Locations,
  IB_USER_MANAGEMENT,
  IB_PAY_CARD_MANAGEMENT,
} from '@WB-playwright/constants';
import {
  UserAccount,
  PaymentTime,
  PaymentOptions,
  CardDetails,
  SearchCriteria,
} from '@WB-playwright/types';
import { getCurrentDate, getLocaleAsString, addRooms, selectDateRange } from '@WB-playwright/utils';
import { Page, test, expect } from '@playwright/test';
import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';

import { Search_Container_IB, Search_Button_IB } from '../tests/IB/Header/Search/elements';

export const searchHotels = async (
  page: Page,
  searchCriteria: SearchCriteria,
  useSuggestedHotelName = false
) => {
  await test.step(`Perform search using: \n${JSON.stringify(searchCriteria)}`, async () => {
    await page.goto(IB_HOME);
    const initialURL = page.url();
    const searchBar = await Search_Container_IB(page);
    const searchButton = await Search_Button_IB(page);
    const locationInput = searchBar.getByTestId('IB-Location-Input');
    const roomOccupancyButton = searchBar.getByTestId('Room-Occupancy-Button');
    const doneButton = searchBar.getByTestId('IB-Room-Occupancy-Done-Button');

    await locationInput.click();
    await locationInput.fill(searchCriteria.location);

    if (useSuggestedHotelName) {
      const hotelsList = page.getByTestId('IB-Hotels-List');
      await expect(hotelsList).toBeVisible();

      const firstHotel = hotelsList.locator(' > *').first();
      await firstHotel.click();
    } else {
      const locationsList = page.getByTestId('IB-Places-List');
      await expect(locationsList).toBeVisible();

      const firstLocation = locationsList.locator(' > *').first();
      await firstLocation.click();
    }

    await selectAndConfirmDate(
      page,
      searchCriteria.arrivalDate,
      searchCriteria.departureDate,
      getLocaleAsString(config.LANGUAGE)
    );

    await roomOccupancyButton.click();
    await addRooms(page, searchCriteria.rooms);
    await doneButton.click();

    const oldCookies = await page.context().cookies();

    await searchButton.click();
    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToSRP = async (
  page: Page,
  user: UserAccount,
  search: string = Locations.LONDON.suggestion
) => {
  await test.step(`Go to SRP searching for ${search}`, async () => {
    await page.goto(IB_HOME);
    const initialURL = page.url();
    const searchBar = await Search_Container_IB(page);
    const searchButton = await Search_Button_IB(page);
    const locationInput = searchBar.getByTestId('IB-Location-Input');

    await locationInput.click();
    await locationInput.fill(search);

    const resultsList = page.getByTestId('IB-Places-List');
    await expect(resultsList).toBeVisible();

    const firstLocation = resultsList.locator(' > *').first();
    await firstLocation.click();

    const todayDate = getCurrentDate(2);
    const futureDate = getCurrentDate(3);
    const startDate = new Date(todayDate.year, todayDate.month - 1, todayDate.day);
    const endDate = new Date(futureDate.year, futureDate.month - 1, futureDate.day);

    await test.step('Select start and end day', async () => {
      await selectAndConfirmDate(page, startDate, endDate, getLocaleAsString(config.LANGUAGE));
    });

    const oldCookies = await page.context().cookies();

    await searchButton.click();
    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToHDP = async (
  page: Page,
  user: UserAccount,
  search: string = Hotels.LONDON_GATWICK_AIRPORT.name
) => {
  await test.step(`Go to HDP searching for ${search}`, async () => {
    await page.goto(IB_HOME);
    const initialURL = page.url();
    const searchBar = await Search_Container_IB(page);
    const searchButton = await Search_Button_IB(page);

    const locationInput = searchBar.getByTestId('IB-Location-Input');

    await locationInput.click();
    await locationInput.fill(search);

    const resultsList = page.getByTestId('IB-Hotels-List');
    await expect(resultsList).toBeVisible();

    const firstHotel = resultsList.locator(' > *').first();
    await firstHotel.click();

    const todayDate = getCurrentDate(2);
    const futureDate = getCurrentDate(3);
    const startDate = new Date(todayDate.year, todayDate.month - 1, todayDate.day);
    const endDate = new Date(futureDate.year, futureDate.month - 1, futureDate.day);

    await test.step('Select start and end day', async () => {
      await selectAndConfirmDate(page, startDate, endDate, getLocaleAsString(config.LANGUAGE));
    });

    const oldCookies = await page.context().cookies();

    await searchButton.click();
    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToGDP = async (
  page: Page,
  user: UserAccount,
  search: string = Hotels.LONDON_GATWICK_AIRPORT.name
) => {
  await goToHDP(page, user, search);

  await test.step(`Go to GDP`, async () => {
    const initialURL = page.url();

    let buttonTestId;

    if (config.DEVICE === 'desktop') {
      buttonTestId = 'hdp_basketBookNowButton';
    } else {
      buttonTestId = 'hdp_mobileBasketBookNowButton';

      const rateSelectorSection = page.getByTestId('hdp_rateSelector-Section');
      await expect(rateSelectorSection).toBeVisible();

      const locationSection = page.getByTestId('hdp_location-Section');
      await locationSection.scrollIntoViewIfNeeded();
    }

    const continueButton = page.getByTestId(buttonTestId);
    await continueButton.click();

    const oldCookies = await page.context().cookies();

    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToPaymentPage = async (
  page: Page,
  user: UserAccount,
  search: string = Hotels.LONDON_GATWICK_AIRPORT.name
) => {
  await goToGDP(page, user, search);

  await test.step(`Go to payment page with ${user.title} and location ${search}`, async () => {
    const initialURL = page.url();
    if (
      user.role === BUSINESS_BOOKER_USER_ROLES.BOOKER ||
      user.role === BUSINESS_BOOKER_USER_ROLES.SUPER
    ) {
      const manualInputButton = page.getByTestId('GuestDetailsBBContainer-Form-SwitchToManual');

      await manualInputButton.click();

      const guestTitleButton = page.getByTestId(
        'DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-menuButton'
      );

      await guestTitleButton.click();

      const firstTitle = page.getByTestId(
        'DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-0'
      );

      await expect(firstTitle).toBeVisible();

      await firstTitle.click();

      const firstNameInput = page.getByTestId('input-bbGuestDetails[0][firstName]');

      await firstNameInput.fill('Firstname');

      const lastNameInput = page.getByTestId('input-bbGuestDetails[0][lastName]');
      await lastNameInput.fill('Lastname');

      const emailInput = page.getByTestId('input-bbGuestDetails[0][emailAddress]');
      await emailInput.fill('test@mailinator.com');
    }

    const headingMeals = page.getByTestId('GuestDetailsPageBB-Meals-Heading-Wrapper');

    await expect(headingMeals).toBeVisible();

    const continueButton = page.getByTestId('GuestDetailsPageBB-ContinueButton');
    await expect(continueButton).toBeVisible();

    await continueButton.scrollIntoViewIfNeeded();
    await continueButton.click();

    const oldCookies = await page.context().cookies();

    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToConfirmationPage = async (
  page: Page,
  user: UserAccount,
  search: string = Hotels.LONDON_GATWICK_AIRPORT.name,
  payment: PaymentOptions
) => {
  await goToPaymentPage(page, user, search);
  await payWithNewCard(page, payment.time);
  await fillNewCardDetails(page, payment.card);
  if (config.ENVIRONMENT === 'dit' || config.ENVIRONMENT === 'dev') {
    await confirmPaymentStep(page);
  }
};

export const payWithNewCard = async (page: Page, time: PaymentTime) => {
  const typeId = time === 'now' ? 'radio-box-wrapper_PAY_NOW' : 'radio-box-wrapper_PAY_ON_ARRIVAL';
  await test.step('Pay with new card', async () => {
    const newCardButton = page.getByTestId('radio-box-wrapper_payment-type-radio-1');
    await newCardButton.click();

    const typeButton = page.getByTestId(typeId);
    await typeButton.click();

    const continueButton = page.getByTestId('TotalCost-Container').getByTestId('submitButton');
    await continueButton.click();
  });
};

export const fillNewCardDetails = async (page: Page, cardDetails: CardDetails) => {
  await test.step('Fill card details', async () => {
    const paymentIframe = page.frameLocator('#paymentFrame');
    const cardNumberInput = paymentIframe.locator('#input_card_number input');
    await cardNumberInput.fill(cardDetails.cardNumber);

    const cardHolderName = paymentIframe.locator('#card_holder_first_name');
    await cardHolderName.fill(cardDetails.holderName);

    const expiryMonthSelect = paymentIframe.locator('[name="card_date_expiry_month"]');
    await expiryMonthSelect.selectOption(cardDetails.expiryMonth);

    const expiryYearSelect = paymentIframe.locator('[name="card_date_expiry_year"]');
    await expiryYearSelect.selectOption(cardDetails.expiryYear);

    if (cardDetails.cardType != 'PIBA') {
      const cvvInput = paymentIframe.locator('#card_card_security_cvx_2');
      await cvvInput.fill(cardDetails.cvv);
    }

    const payButton = paymentIframe.locator('#paybutton');
    await payButton.click();
  });
};

export const confirmPaymentStep = async (page: Page) => {
  await test.step('Confirm payment for lower envs', async () => {
    const initialURL = page.url();
    const paymentIframe = page.frameLocator('#paymentFrame');
    const submitButton = paymentIframe.locator('#testacsform #buttonSubmit');
    await submitButton.click();

    const oldCookies = await page.context().cookies();

    await page.waitForURL((url) => url.toString() !== initialURL);

    await page.context().addCookies(oldCookies);
  });
};

export const goToAmendPage = async (
  page: Page,
  user: UserAccount,
  search: string = Hotels.LONDON_GATWICK_AIRPORT.name,
  payment: PaymentOptions
) => {
  await goToConfirmationPage(page, user, search, payment);

  const bookingRefElement = page.getByTestId('BookingReferenceDetails-Id');
  await expect(bookingRefElement).toBeVisible();

  const bookingRef = await bookingRefElement.innerText();

  const oldCookies = await page.context().cookies();

  await page.goto(IB_BOOKINGS_URL);

  await page.context().addCookies(oldCookies);

  const bookingRefInput = page.locator('input[data-testid="input-bookingFilter"]');
  await bookingRefInput.fill(bookingRef);
  expect(await bookingRefInput.inputValue()).toBe(bookingRef);

  const findBookingButton = page.getByTestId('MyDashboard-findButton');
  await findBookingButton.click();

  const amendBookingButton = page.getByTestId('BookingDetailsController-AmendButton');
  await expect(amendBookingButton).toBeVisible({ timeout: 0 });

  await amendBookingButton.click();

  await page.waitForURL((url) => url.toString() !== IB_BOOKINGS_URL);
  await page.context().addCookies(oldCookies);
};

async function selectAndConfirmDate(page: Page, startDate: Date, endDate: Date, language: string) {
  const searchBar = await Search_Container_IB(page);

  const datePickerInput = searchBar.getByTestId('IB-Date-Picker-Input');
  const datePickerDoneButton = searchBar.getByTestId('IB-Date-Picker-Calendar-buttons-Done');

  await test.step('Click on Calendar ', async () => {
    await datePickerInput.click();
  });

  await selectDateRange(page, startDate, endDate, language);

  await test.step('Click on Calendar done button', async () => {
    await datePickerDoneButton.click();
  });

  await expect(datePickerDoneButton, 'Calendar modal should be closed').toBeHidden();
}
export async function manageEmployeesTableSearch(page: Page, searchString: string) {
  const searchInput = page.getByTestId('SearchEmployeeInput-search-employees-Input');
  const userNameAndEmail = page.locator(
    '[data-testid="DataTablePage-row-accountName-0"] [data-testid="UserInitials-name-and-email"]'
  );
  const userName = page.locator(
    '[data-testid="DataTablePage-row-accountName-0"] [data-testid="UserInitials-name"]'
  );
  const userEmail = page.locator(
    '[data-testid="DataTablePage-row-accountName-0"] [data-testid="UserInitials-email"]'
  );
  const userRole = page.locator('[data-testid="DataTablePage-row-accessLevel-0"] span');
  const userStatus = page.locator(
    '[data-testid="DataTablePage-row-employeeStatus-0"] [data-testid="UserStatus"]'
  );

  await searchInput.click();
  await searchInput.fill(searchString);

  await expect(page).toHaveURL(`${IB_USER_MANAGEMENT}?userSearch=${searchString}`);
  if (config.DEVICE === 'mobile') {
    await expect(userNameAndEmail).toBeVisible();
    await expect(userName).toBeVisible();
    await expect(userEmail).toBeVisible();
    await expect(userRole).not.toBeVisible();
    await expect(userStatus).not.toBeVisible();
  } else {
    await expect(userNameAndEmail).toBeVisible();
    await expect(userName).toBeVisible();
    await expect(userEmail).toBeVisible();
    await expect(userRole).toBeVisible();
    await expect(userStatus).toBeVisible();
  }
}

export async function goToInnBusinessPayTab(page: Page) {
  await test.step('Given I entered the InnBusiness Pay page', async () => {
    if (config.DEVICE === 'mobile') {
      const manageChevronButton = page.getByTestId('Mobile-Nav-Third-Level-Container');
      const thirdLevelManageMenu = page.getByTestId('Mobile-Nav-activeLinkThirdLevel');
      const thirdLevelDrawer = page.getByTestId('Third-Level-Drawer');
      const innBusinessPayTab = page.getByTestId('Manage-Cards-Inn-Business-Pay-Sidebar-Link');
      await manageChevronButton.locator('//button/img').click();
      await expect(thirdLevelDrawer).toBeVisible();
      await innBusinessPayTab.click();

      await expect(page, 'Then the URL changes to ?tab=innbusiness-pay').toHaveURL(
        `${IB_PAY_CARD_MANAGEMENT}`
      );
      await expect(thirdLevelManageMenu).toHaveText('InnBusiness Pay');
    } else {
      const innBusinessPayTab = page.locator("a[href='?tab=innbusiness-pay'] > span");
      await innBusinessPayTab.click();
      await expect(page).toHaveURL(`${IB_PAY_CARD_MANAGEMENT}`);
      await expect(innBusinessPayTab).toHaveAttribute('data-state', 'active');
    }
  });
}
