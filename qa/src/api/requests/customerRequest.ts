/**
 *input CustomerRequest {
    password: String
    newPassword: String
    contactDetail: ContactDetailInput!
    paymentPreference: PaymentPreferenceInput!
    additionalGuests: [BaseContactInput]
    bookingPreference: BookingPreferenceInput!
    marketingPreference: MarketingPreferenceInput
    guestId: Int
    companyId: String
    guestHistoryNumber: String
}
 */
export interface CustomerRequestData {
  password?: string;
  newPassword?: string;
  contactDetail?: unknown;
  paymentPreference?: unknown;
  additionalGuests?: unknown[];
  bookingPreference?: unknown;
  marketingPreference?: unknown;
  guestId?: number;
  companyId?: string;
  guestHistoryNumber?: string;
}

export class CustomerRequest {
  [key: string]: unknown;
  password?: string;
  newPassword?: string;
  contactDetail?: unknown;
  paymentPreference?: unknown;
  additionalGuests?: unknown[];
  bookingPreference?: unknown;
  marketingPreference?: unknown;
  guestId?: number;
  companyId?: string;
  guestHistoryNumber?: string;

  constructor(data: CustomerRequestData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: CustomerRequestData): CustomerRequest {
    return new CustomerRequest(data);
  }
}
