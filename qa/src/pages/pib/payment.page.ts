import { PaymentPageBase } from '../shared/payment.page';
import { PaymentTypeSectionComponent } from '@components/pib/payment/paymentTypeSection.component';
import { PaymentDetailsPage } from './paymentDetails.page';
import { PaymentSixCardSol3dSecureHostPage } from './paymentSixCardSol3dSecureHost.page';

/** BB payment page with the BB-specific payment type section. */
export class PaymentPage extends PaymentPageBase {
  readonly paymentTypeSection = new PaymentTypeSectionComponent();

  protected override async selectBusinessAccountCard(): Promise<void> {
    await this.paymentTypeSection.selectNewBusinessAccountCardButton();
  }

  protected override async selectNewCreditDebitCard(): Promise<void> {
    await this.paymentTypeSection.selectNewCreditDebitCardRadioButton();
  }

  /** Confirm a BB booking using the PIB 3DS host and payment-details page. */
  async confirmCurrentBooking(data: { paymentOptionLabel?: string | null; card?: import('../../test-data/cards').CardDetails; hotel?: import('../shared/payment.page').PaymentHotel; basketReferenceId?: string; isPaymentOptionDisplayed?: boolean; isBAC?: boolean }): Promise<void> {
    await super.confirmCurrentBooking({ ...data, paymentDetailsPage: new PaymentDetailsPage(), paymentSixCardSolution3dSecureHostPage: new PaymentSixCardSol3dSecureHostPage() });
  }
}