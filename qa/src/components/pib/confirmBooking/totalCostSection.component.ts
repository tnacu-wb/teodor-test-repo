import { expect } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { TotalCostSectionBaseComponent } from '../../../components/shared/confirmBooking/totalCostSectionBase.component';

/** Business Booker total-cost section on the confirmation page. */
export class TotalCostSectionComponent extends TotalCostSectionBaseComponent {
  // ######## UI elements/properties ########

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the BB Pay Now confirmation message. */
  async validateTotalCostPayNowPaymentMessage(): Promise<void> {
    console.log('Validate BB total cost payment message for pay now');
    await expect(this.totalCostPaymentMessageLabel, 'Total cost Pay Now message').toHaveText(await Strings.THANK_YOU_YOUR_PREPAYMENT_HAS_BEEN_TAKEN_CCUI.name);
  }

  /** Validate the BB Pay on Arrival confirmation message. */
  async validateTotalCostPayOnArrivalPaymentMessage(): Promise<void> {
    console.log('Validate BB total cost payment message for pay on arrival');
    await expect(this.totalCostPaymentMessageLabel, 'Total cost Pay on Arrival message').toHaveText(await Strings.THANK_YOU_YOUR_PAYMENT_WILL_BE_TAKEN_CCUI.name);
  }

  /** Validate the BB non-guaranteed confirmation message. */
  async validateTotalCostNonGuaranteedPaymentMessage(): Promise<void> {
    console.log('Validate BB total cost payment message for non-guaranteed payment');
    await expect(this.totalCostPaymentMessageLabel, 'Total cost non-guaranteed message').toHaveText(await Strings.THANK_YOU_FOR_YOUR_BOOKING_CCUI.name);
  }
}