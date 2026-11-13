import { expect, type Locator } from '@playwright/test';
import { ApiCalls } from '@api/graphql/apiCalls';
import { EciAllRoomsSectionComponent } from '@components/ccui/ancillaries/eciAllRoomsSection.component';
import { EntireStayMealsSectionComponent } from '@components/ccui/ancillaries/entireStayMealsSection.component';
import { IndividualMealsSectionComponent } from '@components/ccui/ancillaries/individualMealsSection.component';
import { LcoAllRoomsSectionComponent } from '@components/ccui/ancillaries/lcoAllRoomsSection.component';
import { Constants } from '@test-data/constants';
import { HotelRates } from '@test-data/hotelRates';
import { Locales } from '@test-data/locales';
import { Strings } from '@test-data/strings';
import { PriceHelpers } from '@utils/priceHelpers';
import { UiUtils } from '../../utils/uiUtils';
import { AncillariesBasePage } from '../shared/ancillariesBase.page';

type AllRoomsExtraCostOptions = {
  hotelID: string;
  ratePlanCode?: string;
  adultsNumber: number;
  numberOfNights: number;
  startDate?: Date;
  endDate?: Date;
  childNumber?: number;
  countryCode?: string;
  roomsCount?: number;
};

/**
 * Ancillaries page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class AncillariesCcuiPage extends AncillariesBasePage {
  // ######## properties ########

  readonly title = Strings.CHOOSE_YOUR_MEALS.data.default;

  // ######## UI elements/properties ########

  readonly individualMealsSection: IndividualMealsSectionComponent = new IndividualMealsSectionComponent();
  readonly entireStayMealsSection: EntireStayMealsSectionComponent = new EntireStayMealsSectionComponent();
  readonly eciSectionAllRooms: EciAllRoomsSectionComponent = new EciAllRoomsSectionComponent();
  readonly lcoSectionAllRooms: LcoAllRoomsSectionComponent = new LcoAllRoomsSectionComponent();

  /** Return the CCUI meals heading used as the page-load indicator. */
  override get mealHeading(): Locator {
    return this.page.locator('h1[data-testid="AncillariesPage-MealsHeading"]');
  }

  // ######## UI actions/navigation ########

  /** Wait for meal heading to be loaded. */
  async waitForMealHeading(): Promise<void> {
    console.log('Wait for meal heading to be loaded');
    await this.mealHeading.waitFor({ state: 'visible' });
  }

  /**
   * Add early check-in for all rooms and validate the updated booking total.
   * @param options Booking and availability data used to calculate the expected total.
   * @returns The expected formatted booking total.
   */
  async addEciAllRoomsAndValidateTotalCost(options: AllRoomsExtraCostOptions): Promise<string> {
    console.log('Add early check-in items to all rooms then validate total cost');
    return this.changeAllRoomsExtraAndValidateTotalCost(options, 'eci', 1);
  }

  /**
   * Remove early check-in from all rooms and validate the updated booking total.
   * @param options Booking and availability data used to calculate the expected total.
   * @returns The expected formatted booking total.
   */
  async removeEciAllRoomsAndValidateTotalCost(options: AllRoomsExtraCostOptions): Promise<string> {
    console.log('Remove early check-in items then validate total cost');
    return this.changeAllRoomsExtraAndValidateTotalCost(options, 'eci', -1);
  }

  /**
   * Add late check-out for all rooms and validate the updated booking total.
   * @param options Booking and availability data used to calculate the expected total.
   * @returns The expected formatted booking total.
   */
  async addLcoAllRoomsAndValidateTotalCost(options: AllRoomsExtraCostOptions): Promise<string> {
    console.log('Add late check-out items to all rooms then validate total cost');
    return this.changeAllRoomsExtraAndValidateTotalCost(options, 'lco', 1);
  }

  /**
   * Remove late check-out from all rooms and validate the updated booking total.
   * @param options Booking and availability data used to calculate the expected total.
   * @returns The expected formatted booking total.
   */
  async removeLcoAllRoomsAndValidateTotalCost(options: AllRoomsExtraCostOptions): Promise<string> {
    console.log('Remove late check-out items then validate total cost');
    return this.changeAllRoomsExtraAndValidateTotalCost(options, 'lco', -1);
  }

  /**
   * Loads the page handling cookies and authorization.
   * @param reservationInfo Reservation info used to build the page URL.
   */
  async open(reservationInfo: unknown): Promise<void> {
    console.log('Open CCUI ancillaries page');
    const reservationId = this.getReservationId(reservationInfo);
    await this.openLocalizedPath(`${this.url}?reservationId=${reservationId}`);
  }

  // ######## UI validations ########

  /** Validate the Free breakfast block location. */
  async validateFreeBreakFastBlockLocation(): Promise<void> {
    console.log('Validate Free breakfast block location');
    await expect(this.mealHeading, 'Meal heading').toBeVisible();
    await UiUtils.validateIsBelow({
      aboveElement: this.mealHeading,
      belowElement: this.mealSection.childMealsNotificationContainer.locator('[data-testid="AlertTitle"]'),
      elementDescription: 'Free breakfast notification below meal heading',
    });
    await UiUtils.validateIsBelow({
      aboveElement: this.mealSection.childMealsNotificationContainer.locator('[data-testid="AlertTitle"]'),
      belowElement: this.entireStayMealsSection.addMealsLabel,
      maxDistanceBetween: 300,
      elementDescription: 'Entire-stay meals heading below free breakfast notification',
    });
  }

  /**
   * Extract the reservation identifier used by the CCUI ancillaries route.
   * @param reservationInfo Reservation data containing a basket reference.
   * @returns The nested or top-level basket reference, or an empty string when absent.
   */
  private getReservationId(reservationInfo: unknown): string {
    const candidate = reservationInfo as { reservationDetails?: { basketReference?: string }; basketReference?: string };
    return candidate.reservationDetails?.basketReference ?? candidate.basketReference ?? '';
  }

  /**
   * Change an all-rooms ECI or LCO extra and validate the resulting booking total.
   * @param options Booking and availability data used to retrieve the extra price.
   * @param extraType The all-rooms extra to change.
   * @param direction `1` when adding the extra or `-1` when removing it.
   * @returns The expected formatted booking total.
   */
  private async changeAllRoomsExtraAndValidateTotalCost(
    options: AllRoomsExtraCostOptions,
    extraType: 'eci' | 'lco',
    direction: 1 | -1,
  ): Promise<string> {
    const startDate = options.startDate ?? new Date();
    const endDate = options.endDate ?? new Date(startDate.getTime() + options.numberOfNights * 86400000);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({
      hotelId: options.hotelID,
      ratePlanCode: options.ratePlanCode ?? HotelRates.PI_FLEX.ratePlanCode,
      isForUI: false,
    });
    const extras = extraType === 'eci'
      ? await ApiCalls.graphqlGetAncillariesEciExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 })
      : await ApiCalls.graphqlGetAncillariesLcoExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 });
    const extraPrice = extras[0]?.price;
    if (typeof extraPrice !== 'number') throw new Error(`No ${extraType.toUpperCase()} extra with a price was returned by the ancillaries API`);

    const totalBefore = PriceHelpers.getPriceAmountFromUiLabel(await this.getBookingSummaryTotal());
    if (extraType === 'eci') await this.eciSectionAllRooms.clickAddRemoveAllRoomsEciButton();
    else await this.lcoSectionAllRooms.clickAddRemoveAllRoomsLCOButton();

    const currency = options.countryCode === Constants.UK_COUNTRY_CODE || !options.countryCode
      ? Constants.UK_CURRENCY_CODE
      : Constants.EURO_CURRENCY_CODE;
    const expectedTotal = await Locales.formatPriceBasedOnCurrencyCode(
      (totalBefore + extraPrice * (options.roomsCount ?? 1) * direction).toFixed(2),
      currency,
    );
    await expect(this.bookingSummaryTotalCostAmount, 'Booking overview total cost value').toHaveText(expectedTotal);
    return expectedTotal;
  }
}