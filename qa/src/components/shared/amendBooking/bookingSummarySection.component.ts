import { type Page, type Locator, expect } from '@playwright/test';
import { PriceHelpers } from '../../../utils/priceHelpers';

/**
 * The Booking Summary section of the Amend Booking flow containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/bookingSummarySection.js`.
 */
export class AmendBookingSummarySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly leadGuestLabels: Locator = this.page.locator('h6[data-testid="amend-booking-summary-room-lead-guest"]');
  readonly roomOccupancyLabels: Locator = this.page.locator('h6[data-testid="amend-booking-summary-room-occupancy"]');
  readonly paymentContainer: Locator = this.page.locator('div[data-testid="amend-booking-summary-of-payments"]');
  readonly paymentsLabels: Locator = this.paymentContainer.locator('h6[data-testid*="amend-booking-summary-"]');
  readonly totalCostLabel: Locator = this.paymentsLabels.last();
  readonly confirmChangesButton: Locator = this.page.locator('button[name="confirm-changes-button"]');
  readonly roomNameLabelList: Locator = this.page.locator('p[data-testid="amend-booking-summary-room-name"]');
  readonly roomPriceLabelList: Locator = this.page.locator('p[data-testid="amend-booking-summary-room-price"]');
  readonly stayDatesLabel: Locator = this.page.getByTestId('amend-booking-summary-stay-dates').last();

  // ######## UI actions/navigation ########

  /** Click the confirm-changes button from the booking summary. */
  async clickConfirmChanges(): Promise<void> {
    console.log('Click booking summary confirm changes button');
    await this.confirmChangesButton.click();
  }

  // ######## UI validations ########

  /** Validate the total cost label in the payment summary contains the expected amount. */
  async validateTotalCost(expectedAmount: string): Promise<void> {
    console.log(`Validate amendment total cost equals ${expectedAmount}`);
    await expect(this.totalCostLabel, 'Amend booking summary total cost').toContainText(expectedAmount);
  }

  /** Validate the number of room name labels matches the expected room count. */
  async validateRoomCount(expectedRoomsCount: number): Promise<void> {
    console.log(`Validate amendment room count is ${expectedRoomsCount}`);
    await expect(this.roomNameLabelList, 'Amend booking summary room count').toHaveCount(expectedRoomsCount);
  }

  /** Return the displayed payment amount labels. */
  async getPaymentsAmountValueLabels(): Promise<Locator> {
    console.log('Get amendment payment amount labels');
    return this.paymentsLabels;
  }

  /** Read the displayed room name for a zero-based room index. */
  async getRoomName(roomIndex: number): Promise<string> {
    console.log(`Get amendment room name at index ${roomIndex}`);
    await expect(this.roomNameLabelList.nth(roomIndex), `Room ${roomIndex} name`).toBeVisible();
    return (await this.roomNameLabelList.nth(roomIndex).innerText()).trim();
  }

  /** Read the displayed room price for a zero-based room index. */
  async getRoomPrice(roomIndex: number): Promise<number> {
    console.log(`Get amendment room price at index ${roomIndex}`);
    await expect(this.roomPriceLabelList.nth(roomIndex), `Room ${roomIndex} price`).toBeVisible();
    return PriceHelpers.getPriceAmountFromUiLabel(await this.roomPriceLabelList.nth(roomIndex).innerText());
  }

  /** Read the displayed total cost amount. */
  async getTotalCostAmount(): Promise<number> {
    console.log('Get amendment total cost amount');
    await expect(this.totalCostLabel, 'Amendment total cost').toBeVisible();
    return PriceHelpers.getPriceAmountFromUiLabel(await this.totalCostLabel.innerText());
  }

  /** Read the displayed total cost currency symbol. */
  async getTotalCostCurrency(): Promise<string> {
    console.log('Get amendment total cost currency');
    await expect(this.totalCostLabel, 'Amendment total cost').toBeVisible();
    return PriceHelpers.getPriceCurrencySymbolFromUiLabel(await this.totalCostLabel.innerText());
  }
}
