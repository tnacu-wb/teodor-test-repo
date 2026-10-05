import { expect, type Locator } from '@playwright/test';
import { Constants } from '../../../test-data/constants';
import { CcuiComponent } from '../baseCcui.component';
import { PriceHelpers } from '../../../utils';
import { PaymentBookingSummaryHotelInformationSectionComponent } from '../../shared/payment/bookingSummaryHotelInformationSection.component';
import { PaymentBookingSummaryRateInformationSectionComponent } from '../../shared/payment/bookingSummaryRateInformationSection.component';
import { PaymentBookingSummaryTotalCostSectionComponent } from '../../shared/payment/bookingSummaryTotalCostSection.component';
import { PaymentBookingSummaryRoomInformationSectionComponent } from '../../shared/payment/bookingSummaryRoomInformationSection.component';
import { PaymentBookingSummaryStayDatesSectionComponent } from '../../shared/payment/bookingSummaryStayDatesInformationSection.component';
import { type BookingInformation } from '../../../api/response/bookingInformation';
import { type HotelInformation } from '../../../api/response/hotelInformation';

/** Booking summary section from the CCUI payment page. Mirrors the CCUI reference component. */
export class BookingSummarySectionComponent extends CcuiComponent {
  // ######## properties ########

  // ######## UI elements/properties ########

  readonly totalPriceContainer: Locator = this.page.locator(`div[data-testid="BookingSummary-${Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE}-TotalCost-Wrapper"]`);

  readonly totalCostDiscountLabel: Locator = this.page.locator('p[data-testid="BookingSummary-DesktopVariant-TotalCost-DiscountName"]');
  readonly totalCostPreviousTotalCostLabel: Locator = this.page.locator('s[data-testid="BookingSummary-DesktopVariant-TotalCost-PreviousTotalCostName"]');
  readonly totalCostPreviousTotalCostPriceLabel: Locator = this.page.locator('s[data-testid="BookingSummary-DesktopVariant-TotalCost-PreviousTotalCostPrice"]');
  readonly totalCostNewTotalCostLabel: Locator = this.page.locator('h6[data-testid="BookingSummary-DesktopVariant-TotalCost-NewTotalCostName"]');
  readonly newTotalCostLabel: Locator = this.totalPriceContainer.locator('h2[data-testid="BookingSummary-DesktopVariant-TotalCost-TotalCostValue"]');
  readonly notificationInfoIconList: Locator = this.page.locator('div[data-testid="Alert"]');
  readonly notificationInfoTitleLabelList: Locator = this.page.locator('div[data-testid="AlertTitle"]');
  readonly notificationInfoDescriptionLabelList: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly totalCostCityTaxMessage: Locator = this.page.locator(`p[data-testid="BookingSummary-${Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE}-TotalCost-TaxesMessage"]`);
  readonly totalPriceValueLabel: Locator = this.totalPriceContainer.locator(`h2[data-testid="BookingSummary-${Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE}-TotalCost-CostAmount"]`);
  readonly bookingSummaryHotelInformationSection = new PaymentBookingSummaryHotelInformationSectionComponent();
  readonly bookingSummaryRateInformationSection = new PaymentBookingSummaryRateInformationSectionComponent();
  readonly bookingSummaryTotalCostSection = new PaymentBookingSummaryTotalCostSectionComponent();
  readonly bookingSummaryRoomInformationSection = new PaymentBookingSummaryRoomInformationSectionComponent();
  readonly bookingSummaryStayDatesSection = new PaymentBookingSummaryStayDatesSectionComponent();

  /** Read the displayed total cost from the booking summary. */
  async getTotalCostValue(): Promise<string> {
    console.log('Get total cost value');
    await this.totalPriceValueLabel.waitFor({ state: 'visible' });
    const rawValue = (await this.totalPriceValueLabel.innerText()).trim();
    const decimalPart = this.totalPriceValueLabel.locator('[data-testid="pence-price-decimal"]');
    if (await decimalPart.count()) {
      const decimals = (await decimalPart.first().innerText()).trim();
      const withoutDecimals = rawValue.slice(0, rawValue.lastIndexOf(decimals)).trim();
      return `${withoutDecimals}.${decimals}`;
    }
    return rawValue;
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate that the booking summary displays a discount. */
  async validateTotalCostWithDiscount({ discountAmount, totalCostAmount, currency }: { discountAmount?: string | number; totalCostAmount?: string | number; currency?: string } = {}): Promise<void> { console.log('Validate total cost with discount'); await expect(this.totalCostDiscountLabel, 'Total cost discount label').toBeVisible(); if (discountAmount !== undefined && totalCostAmount !== undefined) await expect(await PriceHelpers.getPriceAmountFromUiLabel(await this.newTotalCostLabel.innerText()), 'Discounted total amount').toBe(Number(totalCostAmount) - Number(discountAmount)); if (currency) await expect(await this.newTotalCostLabel.innerText(), 'Discounted total currency').toContain(currency); }
  /** Validate that the booking overview displays its total price. */
  async validateBookingOverviewSectionElements({ hotelInformationApi, bookingInformationApi }: { hotelInformationApi?: HotelInformation; bookingInformationApi?: BookingInformation } = {}): Promise<void> { console.log('Validate booking overview section elements'); if (hotelInformationApi) await this.bookingSummaryHotelInformationSection.validateData(hotelInformationApi); if (bookingInformationApi) { await this.bookingSummaryRateInformationSection.validateData(bookingInformationApi); await this.bookingSummaryTotalCostSection.validateData(bookingInformationApi); } await expect(this.totalPriceValueLabel, 'Booking overview total price').toBeVisible(); }
  /** Validate that the booking summary displays its total price. */
  async validateBookingSummarySectionElements({ hotelInformationApi, bookingInformationApi, basketReferenceId }: { hotelInformationApi?: HotelInformation; bookingInformationApi?: BookingInformation; basketReferenceId?: string } = {}): Promise<void> { console.log('Validate booking summary section elements'); await this.validateBookingOverviewSectionElements({ hotelInformationApi, bookingInformationApi }); if (bookingInformationApi && basketReferenceId) { await this.bookingSummaryRoomInformationSection.validateData(bookingInformationApi, basketReferenceId); await this.bookingSummaryStayDatesSection.validateData(bookingInformationApi); } }
  /** Validate that booking-summary notifications are hidden. */
  async validateBookingSummaryNotificationsAreNotDisplayed(): Promise<void> { console.log('Validate booking summary notifications are not displayed'); await expect(this.notificationInfoDescriptionLabelList.first(), 'Booking summary notification').toBeHidden(); }
  /**
   * Validate whether the city-tax message is displayed.
   * @param isCityTaxDisplayed Whether the city-tax message should be displayed.
   */
  async validateTotalCostCityTaxMessage(isCityTaxDisplayed = true): Promise<void> { console.log('Validate total cost city tax message'); await this.validateDisplayState(this.totalCostCityTaxMessage, 'Total cost city tax message', isCityTaxDisplayed); }
}