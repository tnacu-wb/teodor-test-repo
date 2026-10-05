import { RoomTypesData } from '../../test-data/roomTypes';

/** API model for the current TypeScript test framework. */
export class Customer {
  [key: string]: unknown;
  additionalGuests?: unknown;
  bookingPreference?: {
    roomRequirements?: { type?: string; adults?: number; children?: number; cotRequired?: boolean; [key: string]: unknown };
    foodPreference?: number;
    preselectWifi?: boolean;
    [key: string]: unknown;
  };
  business?: unknown;
  businessUse?: unknown;
  companyId?: string;
  contactDetail?: unknown;
  customerAccountId?: unknown;
  guestHistoryCreation?: unknown;
  newPassword?: string;
  password?: string;
  paymentPreference?: {
    electronicInvoiceRequired?: boolean;
    paymentCard?: { cardType?: string; cardNumber?: string; expiryDate?: string; cardHolderName?: string } | null;
    [key: string]: unknown;
  };
  totalStays?: unknown;

  /**
   * Customer constructor
   * @param data object data
   * @param data.customer customer data
   */
  constructor(data: { customer?: Record<string, unknown> } = {}) {
    const customer = data.customer ?? {};
    this.customerAccountId = customer.customerAccountId;
    this.contactDetail = customer.contactDetail;
    this.paymentPreference = customer.paymentPreference as Customer['paymentPreference'];
    this.additionalGuests = customer.additionalGuests;
    this.bookingPreference = customer.bookingPreference as Customer['bookingPreference'];
    this.companyId = customer.companyId as string | undefined;
    this.businessUse = customer.businessUse;
    this.business = customer.business;
    this.guestHistoryCreation = customer.guestHistoryCreation;
    this.totalStays = customer.totalStays;
    this.password = customer.password as string | undefined;
    this.newPassword = customer.newPassword as string | undefined;
  }

  static fromResponse(data: { customer?: Record<string, unknown> }): Customer {
    return new Customer(data);
  }

  /**
   * Set room preferences to default values
   */
  setRoomPreferencesToDefaultValues(): void {
    delete this.customerAccountId;
    delete this.additionalGuests;
    delete this.businessUse;
    delete this.business;
    delete this.guestHistoryCreation;
    delete this.totalStays;
    if (this.bookingPreference?.roomRequirements) {
      this.bookingPreference.roomRequirements.type = RoomTypesData.DOUBLE.id;
      this.bookingPreference.roomRequirements.adults = 1;
      this.bookingPreference.roomRequirements.children = 0;
      this.bookingPreference.roomRequirements.cotRequired = false;
    }
  }

  /**
   * Set paymentCard
   */
  setPaymentCard(): void {
    delete this.customerAccountId;
    delete this.additionalGuests;
    delete this.businessUse;
    delete this.business;
    delete this.guestHistoryCreation;
    delete this.totalStays;
    if (this.paymentPreference) {
      this.paymentPreference.paymentCard = {
        cardType: 'AT',
        cardNumber: '***************0017',
        expiryDate: '03/34',
        cardHolderName: 'Travel Piba Testersons',
      };
    }
  }

  /**
   * Delete paymentCard
   */
  deletePaymentCard(): void {
    delete this.customerAccountId;
    delete this.additionalGuests;
    delete this.businessUse;
    delete this.business;
    delete this.guestHistoryCreation;
    delete this.totalStays;
    if (this.paymentPreference) {
      delete this.paymentPreference.paymentCard;
    }
  }

  /**
   * Set user password
   * @param password current password
   * @param newPassword new and confirm password
   */
  setUserPassword(password: string, newPassword: string): void {
    delete this.customerAccountId;
    delete this.additionalGuests;
    delete this.businessUse;
    delete this.business;
    delete this.guestHistoryCreation;
    delete this.totalStays;
    this.password = password;
    this.newPassword = newPassword;
  }

  /**
   * Set meals and extras preferences to default values
   */
  setMealsAndExtrasPreferencesToDefaultValues(): void {
    delete this.customerAccountId;
    delete this.additionalGuests;
    delete this.businessUse;
    delete this.business;
    delete this.guestHistoryCreation;
    delete this.totalStays;
    if (this.paymentPreference) {
      this.paymentPreference.electronicInvoiceRequired = true;
    }
    if (this.bookingPreference) {
      this.bookingPreference.foodPreference = 0;
      this.bookingPreference.preselectWifi = false;
    }
  }

}
