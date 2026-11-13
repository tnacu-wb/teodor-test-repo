import { type Locator, expect } from '@playwright/test';
import { ApiCalls } from '@api/graphql/apiCalls';
import { ApiBasketCalls } from '@api/graphql/apiBasketCalls';
import { ApiContentCalls } from '@api/graphql/apiContentCalls';
import { ApiReservationCalls } from '@api/graphql/apiReservationCalls';
import { AncillariesBookingSummarySectionComponent } from '@components/pi/ancillaries/bookingSummarySection.component';
import { BookingUpgradeSectionComponent } from '@components/pi/ancillaries/bookingUpgradeSection.component';
import { ChildMealContainerComponent } from '@components/pi/ancillaries/childMealContainer.component';
import { EciSectionComponent } from '@components/pi/ancillaries/eciSection.component';
import { ImportantInformationSectionComponent } from '@components/pi/ancillaries/importantInformationSection.component';
import { LcoSectionComponent } from '@components/pi/ancillaries/lcoSection.component';
import { MealSectionComponent } from '@components/pi/ancillaries/mealSection.component';
import { AncillariesPrivacyNoticeSectionComponent } from '@components/pi/ancillaries/privacyNoticeSection.component';
import { VerticalStripSectionComponent } from '@components/pi/ancillaries/verticalStripSection.component';
import { Constants } from '@test-data/constants';
import { HotelRates } from '@test-data/hotelRates';
import { Locales } from '@test-data/locales';
import { Strings } from '@test-data/strings';
import { PriceHelpers } from '@utils/priceHelpers';
import { BasePage } from './base.page';

type MealCostOptions = {
  hotelID: string;
  ratePlanCode?: string;
  adultMealTitle: string | Promise<string>;
  adultMealsToAdd: number;
  numberOfNights: number;
  startDate?: Date;
  endDate?: Date;
  childMealsToAdd?: number;
  countryCode?: string;
  exactMatch?: boolean;
};

type RemoveMealCostOptions = {
  hotelID: string;
  ratePlanCode?: string;
  adultMealTitle: string | Promise<string>;
  adultMealsToRemove: number;
  numberOfNights: number;
  childMealsToRemove?: number;
  countryCode?: string;
};

type ExtraCostOptions = {
  hotelID: string;
  ratePlanCode?: string;
  adultsNumber: number;
  numberOfNights: number;
  startDate?: Date;
  endDate?: Date;
  childNumber?: number;
  countryCode?: string;
};

/**
 * Shared ancillaries page behavior for booking applications.
 *
 * Concrete applications can override `ancillariesProjectIdentifier` when their
 * test IDs differ while retaining the common booking, meals, and extras flow.
 */
export class AncillariesBasePage extends BasePage {
  // ######## UI elements/properties ########

  static readonly PAGE_IDENTIFIER = 'AncillariesPage';
  static readonly MEALS_HEADING_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-MealsHeading`;
  static readonly CONTINUE_BUTTON_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-ContinueButton`;
  static readonly MEALS_HEADING_TITLE_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-Meals-Heading-Title`;
  static readonly ADULT_MEALS_WRAPPER_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-Meals-Adults-MealItem-Wrapper`;
  static readonly ADULT_MEALS_TITLE_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-Meals-Adults-MealItem-Title`;
  static readonly BOOKING_SUMMARY_EXPAND_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-BookingSummary-MobileVariant-ExpandButton`;
  static readonly BOOKING_SUMMARY_PREFIX = `${AncillariesBasePage.PAGE_IDENTIFIER}-BookingSummary`;
  static readonly ADULT_MEAL_ADD_BUTTON_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-Meals-Adults-MealItem-AddSubtractControls-AddButton`;
  static readonly ADULT_MEAL_VALUE_TESTID = `${AncillariesBasePage.PAGE_IDENTIFIER}-Meals-Adults-MealItem-AddSubtractControls-Value`;
  static readonly MOBILE_VIEWPORT_WIDTH_THRESHOLD = 1024;
  static readonly PAGE_LOAD_TIMEOUT_MS = 30000;
  readonly url: string = 'ancillaries';

  /** Override when an application renders ancillaries controls under another test ID prefix. */
  protected get ancillariesProjectIdentifier(): string {
    return AncillariesBasePage.PAGE_IDENTIFIER;
  }

  // UI components

  readonly verticalStripSection: VerticalStripSectionComponent = new VerticalStripSectionComponent();
  readonly mealSection: MealSectionComponent = new MealSectionComponent();
  readonly childMealContainer: ChildMealContainerComponent = new ChildMealContainerComponent();
  readonly eciSection: EciSectionComponent = new EciSectionComponent();
  readonly lcoSection: LcoSectionComponent = new LcoSectionComponent();
  readonly bookingSummarySection: AncillariesBookingSummarySectionComponent = new AncillariesBookingSummarySectionComponent();
  readonly bookingUpgradeSection: BookingUpgradeSectionComponent = new BookingUpgradeSectionComponent();
  readonly importantInformationSection: ImportantInformationSectionComponent = new ImportantInformationSectionComponent();
  readonly privacyNoticeSection: AncillariesPrivacyNoticeSectionComponent = new AncillariesPrivacyNoticeSectionComponent();

  get pageLoadedIndicator(): Locator {
    return this.page.locator(`h1[data-testid="${this.ancillariesProjectIdentifier}-MealsHeading"]`);
  }

  get continueButton(): Locator {
    return this.page.locator(`button[data-testid="${this.ancillariesProjectIdentifier}-ContinueButton"], #PPLUS-cloned-continue-btn`);
  }

  get backToHotelDetailsButton(): Locator {
    return this.page.locator(`[data-testid="${this.ancillariesProjectIdentifier}-BackToHDPButton"] p`);
  }

  get mealHeading(): Locator {
    return this.pageLoadedIndicator;
  }

  get mealsHeading(): Locator {
    return this.page.locator(`[data-testid="${this.ancillariesProjectIdentifier}-Meals-Heading-Title"]`);
  }

  get tabButtonsList(): Locator {
    return this.page.locator('button[data-testid*="TabButton"]');
  }

  get loadingSpinner(): Locator {
    return this.page.locator('g[transform="translate(50 50)"]');
  }

  get loadingText(): Locator {
    return this.page.locator('p.chakra-text.css-1krigk3');
  }

  get adultMealItems(): Locator {
    return this.page.locator(`div[data-testid="${this.ancillariesProjectIdentifier}-Meals-Adults-MealItem-Wrapper"]`);
  }

  get adultMealTitles(): Locator {
    return this.page.locator(`h4[data-testid="${this.ancillariesProjectIdentifier}-Meals-Adults-MealItem-Title"]`);
  }

  /**
   * Get the add button for an adult meal at a rendered index.
   * @param index - Zero-based adult meal index.
   */
  private getAddButtonForMealAtIndex(index: number): Locator {
    return this.adultMealItems.nth(index).locator(`button[data-testid="${this.ancillariesProjectIdentifier}-Meals-Adults-MealItem-AddSubtractControls-AddButton"]`);
  }

  /**
   * Get the remove button for an adult meal at a rendered index.
   * @param index - Zero-based adult meal index.
   */
  private getRemoveButtonForMealAtIndex(index: number): Locator {
    return this.adultMealItems.nth(index).locator(`button[data-testid="${this.ancillariesProjectIdentifier}-Meals-Adults-MealItem-AddSubtractControls-SubtractButton"]`);
  }

  get bookingSummaryExpandButton(): Locator {
    return this.page.locator(`[data-testid="${this.ancillariesProjectIdentifier}-BookingSummary-MobileVariant-ExpandButton"]`);
  }

  get bookingSummaryTotalCostAmount(): Locator {
    return this.page.locator(
      `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="TotalCost-CostAmount"]`
    );
  }

  get bookingSummaryHotelName(): Locator {
    return this.page.locator(
      `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="HotelInformation-HotelName"]`
    );
  }

  /**
   * Extract the basket/reservation reference ID from the current page URL.
   * @returns The basket reference ID extracted from the `reservationId` query parameter.
   * @throws Error if the `reservationId` parameter is absent.
   */
  async getBasketReferenceIdFromUrl(): Promise<string> {
    console.log('Extracting basket reference ID from URL');
    const currentUrl = this.page.url();
    const reservationId = new URL(currentUrl).searchParams.get('reservationId');

    if (!reservationId) {
      throw new Error(`Could not extract basketReferenceId from URL. Expected 'reservationId' query param in: ${currentUrl}`);
    }

    return reservationId;
  }

  /** Alias for getBasketReferenceIdFromUrl matching the legacy page API. */
  async getReservationIdFromUrl(): Promise<string> {
    console.log('Extracting reservation ID from URL');
    return this.getBasketReferenceIdFromUrl();
  }

  /** Get the current booking summary total, expanding the summary on mobile first. */
  async getBookingSummaryTotal(): Promise<string> {
    console.log('Getting booking summary total');
    await this.expandSectionOnMobile();
    await this.bookingSummaryTotalCostAmount.waitFor({ state: 'visible' });
    return this.bookingSummaryTotalCostAmount.innerText();
  }

  // ######## UI actions/navigation ########

  /** Expand the mobile booking summary when its disclosure button is visible. */
  async expandSectionOnMobile(): Promise<void> {
    console.log('Expanding booking summary section on mobile if needed');
    const viewport = this.page.viewportSize();
    if (!viewport || viewport.width >= AncillariesBasePage.MOBILE_VIEWPORT_WIDTH_THRESHOLD) return;

    const isExpandButtonVisible = await this.bookingSummaryExpandButton.isVisible().catch(() => false);
    if (isExpandButtonVisible) {
      await this.bookingSummaryExpandButton.click();
      await this.bookingSummaryHotelName.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    }
  }

  /**
   * Add breakfast for the specified number of adults.
   * @returns The title of the matched meal item.
   */
  async addBreakfastForAdults(
    adultsCount: number,
    options: { exactMatch?: boolean; breakfastTitle?: string } = {}
  ): Promise<string> {
    console.log(`Adding breakfast for ${adultsCount} adults`);
    const { exactMatch = false, breakfastTitle = Strings.PREMIER_INN_BREAKFAST.name } = options;

    await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS });
    try {
      await this.mealsHeading.waitFor({ state: 'visible', timeout: AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS });
    } catch {
      await this.pageLoadedIndicator.scrollIntoViewIfNeeded();
      await this.mealsHeading.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    }
    await this.pageLoadedIndicator.scrollIntoViewIfNeeded();

    const mealContainerIndex = await this.findMealByTitle(await breakfastTitle, exactMatch);
    if (mealContainerIndex === -1) {
      throw new Error(`Could not find a breakfast meal matching "${breakfastTitle}" (exactMatch: ${exactMatch}) on the ancillaries page`);
    }

    const matchedTitle = await this.adultMealTitles.nth(mealContainerIndex).innerText();
    const addButton = this.getAddButtonForMealAtIndex(mealContainerIndex);
    for (let index = 0; index < adultsCount; index++) {
      await addButton.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
      await addButton.click();
      await this.page.waitForTimeout(500);
    }

    return matchedTitle.trim();
  }

  /** Click Continue without selecting meals to proceed to guest details. */
  async clickContinue(): Promise<void> {
    console.log('Clicking continue button');
    await this.scrollToContinueButton();
    await this.continueButton.click();
  }

  /** Scroll the ancillaries Continue button into view. */
  async scrollToContinueButton(): Promise<void> {
    console.log('Scroll to continue button');
    await this.continueButton.waitFor({ state: 'visible', timeout: AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS });
    await this.continueButton.scrollIntoViewIfNeeded();
  }

  /** @deprecated Use clickContinue() instead. */
  async clickContinueButton(): Promise<void> {
    console.log('Click continue button (compatibility alias)');
    await this.clickContinue();
  }

  /** Navigate back to the selected hotel's details page. */
  async clickBackToHotelDetailsButton(): Promise<void> {
    console.log('Clicking back to hotel details button');
    await this.backToHotelDetailsButton.scrollIntoViewIfNeeded();
    await this.backToHotelDetailsButton.click();
  }

  /** Wait until the ancillaries meal heading is displayed. */
  async waitForMealHeading(): Promise<void> {
    console.log('Wait for ancillaries meal heading');
    await this.mealHeading.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  }

  /** Add adult and child meals and validate the resulting booking total. */
  async addMealAndValidateTotalCost({ hotelID, ratePlanCode = HotelRates.PI_FLEX.ratePlanCode, adultMealTitle, adultMealsToAdd, numberOfNights, startDate = new Date(), endDate = new Date(startDate.getTime() + numberOfNights * 86400000), childMealsToAdd = 0, countryCode = Constants.UK_COUNTRY_CODE, exactMatch = true }: MealCostOptions): Promise<string> {
    console.log(`Add meal(s) and validate total cost for ${adultMealTitle}`);
    const title = await adultMealTitle;
    const { meal, index } = await this.getMealAndIndex({ hotelID, ratePlanCode, title, numberOfNights, startDate, endDate, adultsNumber: adultMealsToAdd, childNumber: childMealsToAdd, exactMatch });
    const totalBefore = PriceHelpers.getPriceAmountFromUiLabel(await this.getBookingSummaryTotal());
    await expect(this.adultMealTitles.nth(index), `Adult meal "${title}" at position ${index}`).toContainText(title);
    await this.clickMealButton(index, adultMealsToAdd, true);
    await this.clickChildMealButton(childMealsToAdd, true);
    return this.validateChangedTotal(totalBefore, meal.price * adultMealsToAdd * numberOfNights, countryCode);
  }

  /** Remove adult and child meals and validate the resulting booking total. */
  async removeMealAndValidateTotalCost({ hotelID, ratePlanCode = HotelRates.PI_FLEX.ratePlanCode, adultMealTitle, adultMealsToRemove, numberOfNights, childMealsToRemove = 0, countryCode = Constants.UK_COUNTRY_CODE }: RemoveMealCostOptions): Promise<string> {
    console.log(`Remove meal(s) and validate total cost for ${adultMealTitle}`);
    const title = await adultMealTitle;
    const { meal, index } = await this.getMealAndIndex({ hotelID, ratePlanCode, title, numberOfNights, adultsNumber: adultMealsToRemove, childNumber: childMealsToRemove, exactMatch: true });
    const totalBefore = PriceHelpers.getPriceAmountFromUiLabel(await this.getBookingSummaryTotal());
    await this.clickMealButton(index, adultMealsToRemove, false);
    await this.clickChildMealButton(childMealsToRemove, false);
    return this.validateChangedTotal(totalBefore, -meal.price * adultMealsToRemove * numberOfNights, countryCode);
  }

  /** Add Early check-in and validate the updated booking total. */
  async addEciAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
    console.log('Add ECI and validate total cost');
    return this.changeExtraAndValidateTotalCost(options, 'eci', 1);
  }

  /** Remove Early check-in and validate the updated booking total. */
  async removeEciAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
    console.log('Remove ECI and validate total cost');
    return this.changeExtraAndValidateTotalCost(options, 'eci', -1);
  }

  /** Add Late check-out and validate the updated booking total. */
  async addLcoAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
    console.log('Add LCO and validate total cost');
    return this.changeExtraAndValidateTotalCost(options, 'lco', 1);
  }

  /** Remove Late check-out and validate the updated booking total. */
  async removeLcoAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
    console.log('Remove LCO and validate total cost');
    return this.changeExtraAndValidateTotalCost(options, 'lco', -1);
  }

  /**
   * Find an adult meal by title and return its rendered index.
   * @param title - Meal name to find.
   * @param exactMatch - Whether the title must match exactly.
   * @returns The zero-based meal index, or `-1` when no match is rendered.
   */
  private async findMealByTitle(title: string, exactMatch: boolean): Promise<number> {
    const count = await this.adultMealTitles.count();
    for (let index = 0; index < count; index++) {
      const mealTitle = (await this.adultMealTitles.nth(index).innerText()).trim();
      if (exactMatch ? mealTitle === title : mealTitle.toLowerCase().includes(title.toLowerCase())) return index;
    }
    return -1;
  }

  /**
   * Fetch and order ancillary meals exactly as they are rendered, then resolve the requested meal.
   * @returns The API meal and its rendered zero-based index.
   */
  private async getMealAndIndex({ hotelID, ratePlanCode, title, numberOfNights, startDate = new Date(), endDate = new Date(startDate.getTime() + numberOfNights * 86400000), adultsNumber, childNumber = 0, exactMatch }: {
    hotelID: string; ratePlanCode: string; title: string; numberOfNights: number; startDate?: Date; endDate?: Date; adultsNumber: number; childNumber?: number; exactMatch: boolean;
  }) {
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotelID, ratePlanCode, isForUI: false });
    const meals = await ApiCalls.graphqlGetAncillariesAdultMeals({ hotelId: hotelID, bookingFlowId, nightsNumber: numberOfNights, startDate, endDate, adultsNumber, childrenNumber: childNumber });
    const sortedMeals = meals
      .filter((meal): meal is typeof meal & { name: string; price: number } => typeof meal.name === 'string' && typeof meal.price === 'number')
      .sort((first, second) => first.name.localeCompare(second.name) || (first.order ?? 0) - (second.order ?? 0));
    const index = sortedMeals.findIndex(meal => exactMatch ? meal.name.trim() === title : meal.name.trim().includes(title));
    if (index === -1) throw new Error(`"${title}" meal is not displayed on Ancillaries page`);
    return { meal: sortedMeals[index], index };
  }

  /**
   * Click an adult meal's add or remove control repeatedly.
   * @param index - Zero-based meal index.
   * @param count - Number of selections to change.
   * @param add - Whether to add instead of remove selections.
   */
  private async clickMealButton(index: number, count: number, add: boolean): Promise<void> {
    const button = add ? this.getAddButtonForMealAtIndex(index) : this.getRemoveButtonForMealAtIndex(index);
    for (let click = 0; click < count; click++) await button.click();
  }

  /**
   * Click the child-meal add or remove control repeatedly.
   * @param count - Number of child selections to change.
   * @param add - Whether to add instead of remove selections.
   */
  private async clickChildMealButton(count: number, add: boolean): Promise<void> {
    for (let click = 0; click < count; click++) {
      if (add) await this.childMealContainer.clickAddMealButton();
      else await this.childMealContainer.clickRemoveMealButton();
    }
  }

  /**
   * Change one ECI or LCO item and validate its API price is reflected in the booking total.
   * @param options - Ancillary availability and booking criteria.
   * @param extraType - Early check-in or late check-out.
   * @param direction - `1` to add the extra or `-1` to remove it.
   * @returns The expected formatted booking total.
   */
  private async changeExtraAndValidateTotalCost(options: ExtraCostOptions, extraType: 'eci' | 'lco', direction: 1 | -1): Promise<string> {
    const startDate = options.startDate ?? new Date();
    const endDate = options.endDate ?? new Date(startDate.getTime() + options.numberOfNights * 86400000);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: options.hotelID, ratePlanCode: options.ratePlanCode ?? HotelRates.PI_FLEX.ratePlanCode, isForUI: false });
    const extras = extraType === 'eci'
      ? await ApiCalls.graphqlGetAncillariesEciExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 })
      : await ApiCalls.graphqlGetAncillariesLcoExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 });
    if (!extras[0] || typeof extras[0].price !== 'number') throw new Error(`No ${extraType.toUpperCase()} extra with a price was returned by the ancillaries API`);
    const totalBefore = PriceHelpers.getPriceAmountFromUiLabel(await this.getBookingSummaryTotal());
    if (extraType === 'eci') await this.eciSection.clickAddRemoveEciButton();
    else await this.lcoSection.clickAddRemoveLCOButton();
    return this.validateChangedTotal(totalBefore, extras[0].price * direction, options.countryCode);
  }

  // ######## UI validations ########

  /** Validate page loading, reservation registration, and the expected page indicator. */
  async validatePage(): Promise<void> {
    console.log('Validating ancillaries page loaded');
    try {
      await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS });
    } catch {
      throw new Error(`Ancillaries page did not load: page loaded indicator was not visible within ${AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS}ms`);
    }

    await this.page.waitForFunction(
      () => new URL(window.location.href).searchParams.has('reservationId'),
      undefined,
      { timeout: browser.options.actionTimeout }
    );
    const basketReferenceId = await this.getBasketReferenceIdFromUrl();
    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    ApiReservationCalls.createdReservations.push(basket);
    console.log(`Created reservation with "${basket.reference}" basket reference for "${basket.hotelId}" hotel id.`);
  }

  /** Validate that the booking summary total contains the expected value. */
  async validateBookingSummaryTotal(expectedTotal: string): Promise<void> {
    console.log(`Validating booking summary total contains: ${expectedTotal}`);
    await this.expandSectionOnMobile();
    await expect(this.bookingSummaryTotalCostAmount, `Booking summary total should contain ${expectedTotal}`).toContainText(expectedTotal);
  }

  /** Validate the added adult meals for a room in the booking summary. */
  async validateAdultMealsPerRoom(roomIndex: number, expectedMeals: string, options: { exactMatch?: boolean } = {}): Promise<void> {
    const { exactMatch = false } = options;
    console.log(`Validating adult meals for room ${roomIndex}: ${expectedMeals}`);
    await this.expandSectionOnMobile();

    const adultMealLabel = this.page.locator(
      `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="RoomInformation-AdultMeal"]`
    );
    const roomWrapper = this.page.locator(
      `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="RoomInformation-Wrapper"]`
    ).locator('> div').nth(roomIndex);
    const roomMealLabel = roomWrapper.locator('[data-testid*="RoomInformation-AdultMeal"]');
    const targetLabel = await roomMealLabel.count() > 0 ? roomMealLabel.first() : adultMealLabel.nth(roomIndex);

    if (exactMatch) {
      await expect(targetLabel, `Adult meals label should exactly match: ${expectedMeals}`).toHaveText(expectedMeals, { timeout: 10000 });
    } else {
      await expect(targetLabel, `Adult meals label should contain: ${expectedMeals}`).toContainText(expectedMeals, { timeout: 10000 });
    }
  }

  /** Validate the Continue button's localized label. */
  async validateContinueButtonLabel(label: string): Promise<void> {
    console.log(`Validate continue button label: ${label}`);
    await expect(this.continueButton, 'Continue button label').toContainText(label);
  }

  /** Validate the ancillaries route and reservation identifier for a selected hotel and rate. */
  async validateUrl({ hotel, ratePlanCode }: { hotel: { id: string; threeLetterId: string }; ratePlanCode: string }): Promise<void> {
    console.log(`Validate ancillaries URL for hotel ${hotel.id}`);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotel.id, ratePlanCode });
    const currentUrl = new URL(this.page.url());
    expect(currentUrl.pathname, 'Ancillaries URL should contain the booking flow and route').toContain(`/${bookingFlowId}/${this.url}`);
    expect(currentUrl.searchParams.get('reservationId'), 'Ancillaries URL reservationId should match hotel code').toBe(hotel.threeLetterId);
  }

  /** Validate the ancillaries loading spinner and its message when visible. */
  async validateLoadingSpinner(isDisplayed: boolean): Promise<void> {
    console.log(`Validate loading spinner displayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.loadingSpinner, 'Loading spinner visibility').toBeVisible();
      await expect(this.loadingText, 'Booking loading message').toContainText(await Strings.BOOKING_LOADING.name);
    } else {
      await expect(this.loadingSpinner, 'Loading spinner visibility').not.toBeVisible();
    }
  }

  /** Validate the booking total including API-provided preselected adult meals. */
  async validateTotalCostOfPreselectedMeals({ hotelID, ratePlanCode = HotelRates.PI_FLEX.ratePlanCode, basketReference, adultMealsAdded, numberOfNights, preselectedMeal }: {
    hotelID: string; ratePlanCode?: string; basketReference: string; adultMealsAdded: number; numberOfNights: number; preselectedMeal: string;
  }): Promise<string> {
    console.log(`Validate total cost of preselected meal: ${preselectedMeal}`);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotelID, ratePlanCode });
    const [mealList, bookingInformation] = await Promise.all([
      ApiCalls.graphqlGetAncillariesAdultMeals({ hotelId: hotelID, bookingFlowId }),
      ApiContentCalls.graphqlGetBookingInformation({ basketReference }),
    ]);
    const meal = mealList.find(item => item.name?.trim() === preselectedMeal);
    if (!meal || typeof meal.price !== 'number') throw new Error(`Preselected meal "${preselectedMeal}" with a price was not returned by the ancillaries API`);
    if (typeof bookingInformation.totalCost !== 'number' || !bookingInformation.currencyCode) throw new Error(`Booking information for "${basketReference}" did not include total cost and currency`);
    const expectedTotal = await Locales.formatPriceBasedOnCurrencyCode(
      (bookingInformation.totalCost + meal.price * adultMealsAdded * numberOfNights).toFixed(2), bookingInformation.currencyCode
    );
    await expect(this.bookingSummaryTotalCostAmount, 'Booking overview total cost including preselected meals').toHaveText(expectedTotal);
    return expectedTotal;
  }

  /**
   * Validate a booking-total change using the country-specific currency format.
   * @returns The expected formatted booking total.
   */
  private async validateChangedTotal(totalBefore: number, costDifference: number, countryCode = Constants.UK_COUNTRY_CODE): Promise<string> {
    const currency = countryCode === Constants.UK_COUNTRY_CODE ? Constants.UK_CURRENCY_CODE : Constants.EURO_CURRENCY_CODE;
    const expectedTotal = await Locales.formatPriceBasedOnCurrencyCode((totalBefore + costDifference).toFixed(2), currency);
    await expect(this.bookingSummaryTotalCostAmount, 'Booking overview total cost value').toHaveText(expectedTotal);
    return expectedTotal;
  }
}
