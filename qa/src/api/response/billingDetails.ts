import type { GuestDetailsModel } from '../../test-data/guestDetails';
import { Locales } from '../../test-data/locales';

/**
 * response example:
 *{
    "billing": {
        "address": {
            "country": "GB",
            "addressLine1": "31 Poplar Grove",
            "addressLine2": "Ramsbottom",
            "addressLine3": "BURY",
            "addressLine4": "",
            "postalCode": "BL0 0BE"
        },
        "email": "",
        "firstName": "Test1",
        "lastName": "Test2",
        "telephone": "+4401208 266881",
        "landline": "+4401208 266881",
        "title": "Mr"
        },
 *}
 */
export class BillingDetails {
  [key: string]: unknown;
  address1?: string | null;
  address2?: string | null;
  address3?: string | null;
  address4?: string | null;
  country?: string | null;
  email?: string | null;
  firstName?: string | null;
  landline?: string | null;
  lastName?: string | null;
  postalCode?: string | null;
  telephone?: string | null;
  title?: string | null;

  /**
   * Billing Details Constructor
   * @param data object data
   * @param data.billingDetails billing details data
   */
  constructor(data: { billingDetails?: unknown } = {}) {
    const billingDetails = (data.billingDetails ?? null) as Record<string, unknown> | null;
    const address = (billingDetails?.address ?? {}) as Record<string, unknown>;
    this.country = billingDetails ? (address.country as string) : null;
    this.address1 = billingDetails ? (address.addressLine1 as string) : null;
    this.address2 = billingDetails ? (address.addressLine2 as string) : null;
    this.address3 = billingDetails ? (address.addressLine3 as string) : null;
    this.address4 = billingDetails ? (address.addressLine4 as string) : null;
    this.postalCode = billingDetails ? (address.postalCode as string) : null;
    this.email = billingDetails ? (billingDetails.email as string) : null;
    this.firstName = billingDetails ? (billingDetails.firstName as string) : null;
    this.lastName = billingDetails ? (billingDetails.lastName as string) : null;
    this.telephone = billingDetails ? (billingDetails.telephone as string) : null;
    this.landline = billingDetails ? (billingDetails.landline as string) : null;
    this.title = billingDetails ? (billingDetails.title as string) : null;
  }

  static fromResponse(data: { billingDetails?: unknown }): BillingDetails {
    return new BillingDetails(data);
  }

  /**
   * Validate Billing Details against Guest Details
   * @param data object data
   * @param data.guestDetails object data
   */
  async validateBillingDetails(data: { guestDetails: GuestDetailsModel }): Promise<void> {
    console.log('Validate Billing Details against Guest Details');
    const { guestDetails } = data;
    const { booker } = guestDetails;

    if (this.country !== booker.address.countryCode) {
      throw new Error(`Country code=${this.country} should be=${booker.address.countryCode}`);
    }
    await this.validateBillingAddress({ guestDetails });
    if (this.postalCode !== booker.address.postalCode) {
      throw new Error(`Postcode=${this.postalCode} should be=${booker.address.postalCode}`);
    }
    if (this.email !== booker.emailAddress) {
      throw new Error(`Email=${this.email} should be=${booker.emailAddress}`);
    }
    if (this.firstName !== booker.firstName) {
      throw new Error(`First name=${this.firstName} should be=${booker.firstName}`);
    }
    if (this.lastName !== booker.lastName) {
      throw new Error(`Last name=${this.lastName} should be=${booker.lastName}`);
    }
    if (this.title !== booker.title) {
      throw new Error(`Title=${this.title} should be=${booker.title}`);
    }
  }

  /**
   * Validate Billing Details Only for Address
   * @param data object data
   * @param data.guestDetails object data
   */
  async validateBillingAddress(data: { guestDetails: GuestDetailsModel }): Promise<void> {
    const { booker } = data.guestDetails;

    if (this.address1 !== booker.address.addressLine1) {
      throw new Error(`Address Line 1=${this.address1} should be=${booker.address.addressLine1}`);
    }
    if (this.address2 !== booker.address.addressLine2) {
      throw new Error(`Address Line 2=${this.address2} should be=${booker.address.addressLine2}`);
    }
    if (this.address3 !== booker.address.addressLine3) {
      throw new Error(`Address Line 3=${this.address3} should be=${booker.address.addressLine3}`);
    }
    const localeString = String((global.browser?.options as Record<string, unknown> | undefined)?.locale ?? 'gb-en');
    if (Locales.isEnglishWebsite(localeString) && this.address4 !== booker.address.addressLine4) {
      throw new Error(`Address Line 4=${this.address4} should be=${booker.address.addressLine4}`);
    }
  }

}
