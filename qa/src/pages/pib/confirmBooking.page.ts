import { expect, type Locator } from '@playwright/test';
import { ConfirmBookingPageBase } from '../shared/confirmBookingBase.page';
import { RoomDetailsSectionComponent } from '../../components/pib/confirmBooking/roomDetailsSection.component';
import { TotalCostSectionComponent } from '../../components/pib/confirmBooking/totalCostSection.component';

/**
 * Business Booker confirmation page migrated from qa/reference pages/bb/confirmBooking.page.js.
 */
export class ConfirmBookingPage extends ConfirmBookingPageBase {
	// ######## UI elements/properties ########
	override readonly roomDetailsSection = new RoomDetailsSectionComponent();
	override readonly totalCostSection = new TotalCostSectionComponent();
	readonly totalCostAmountLabel: Locator = this.totalCostSection.totalCostLabel;
	readonly goToHomePageButton: Locator = this.page.locator('button[data-testid="continueBtn"]');

	// ######## UI actions/navigation ########
	/** Click the BB confirmation page button that returns to the homepage. */
	override async clickGoToHomePageButton(): Promise<void> {
		console.log('Click on Continue to homepage button');
		await this.goToHomePageButton.scrollIntoViewIfNeeded();
		await this.goToHomePageButton.click();
	}

	// ######## UI validations ########
	/** Validate the booking reference displayed on the BB confirmation page. */
	async validateBookingReference(basketReference: string): Promise<void> {
		console.log(`Validate booking reference=${basketReference}`);
		await expect(this.bookingDetailsIntroSection.bookingReferenceDetailsId, 'Booking reference ID').toBeVisible();
		await expect(this.bookingDetailsIntroSection.bookingReferenceDetailsId, 'Booking reference ID should match').toContainText(basketReference);
	}
}