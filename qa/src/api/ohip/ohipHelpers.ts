import { expect } from '@playwright/test';
import { Constants } from '../../test-data/constants';
import { OhipApiCalls } from './ohipApiCalls';

interface PaymentMethod {
  paymentMethod?: string;
  [key: string]: unknown;
}

interface Address {
  addressLine?: string[];
  cityName?: string;
  postalCode?: string;
  [key: string]: unknown;
}

interface AddressInfo {
  address?: Address;
  [key: string]: unknown;
}

interface Telephone {
  phoneNumber?: string;
  [key: string]: unknown;
}

interface TelephoneInfo {
  telephone?: Telephone;
  [key: string]: unknown;
}

interface Email {
  emailAddress?: string;
  [key: string]: unknown;
}

interface EmailInfo {
  email?: Email;
  [key: string]: unknown;
}

interface PersonName {
  givenName?: string;
  nameType?: string;
  [key: string]: unknown;
}

interface Company {
  companyName?: string;
  [key: string]: unknown;
}

interface Customer {
  personName?: PersonName[];
  [key: string]: unknown;
}

interface Profile {
  company?: Company;
  addresses?: {
    addressInfo?: AddressInfo[];
    [key: string]: unknown;
  };
  telephones?: {
    telephoneInfo?: TelephoneInfo[];
    [key: string]: unknown;
  };
  customer?: Customer;
  emails?: {
    emailInfo?: EmailInfo[];
    [key: string]: unknown;
  };
  [key: string]: unknown;
}

interface ReservationProfile {
  profile?: Profile;
  reservationProfileType?: string;
  [key: string]: unknown;
}

interface ReservationProfiles {
  reservationProfile?: ReservationProfile[];
  [key: string]: unknown;
}

interface ProfileInfo {
  profile?: Profile;
  [key: string]: unknown;
}

interface ReservationGuest {
  profileInfo?: ProfileInfo;
  [key: string]: unknown;
}

interface UserDefinedField {
  name?: string;
  value?: string;
  [key: string]: unknown;
}

interface OhipReservation {
  reservationPaymentMethods?: PaymentMethod[];
  computedReservationStatus?: string;
  userDefinedFields?: {
    characterUDFs?: UserDefinedField[];
    [key: string]: unknown;
  };
  reservationProfiles?: ReservationProfiles;
  reservationGuests?: ReservationGuest[];
  [key: string]: unknown;
}

interface CompanyData {
  name?: string;
  address?: {
    addressLine1?: string;
    addressLine2?: string;
    addressLine3?: string;
    addressLine4?: string;
    cityName?: string;
    postalCode?: string;
    [key: string]: unknown;
  };
  telephoneNumber?: string;
  profileType?: string;
  [key: string]: unknown;
}

interface GuestDetailsData {
  booker?: {
    firstName?: string;
    lastName?: string;
    mobilePrefix?: string;
    mobile?: string;
    emailAddress?: string;
    [key: string]: unknown;
  };
  [key: string]: unknown;
}

interface ReservationItem {
  sourceId?: string;
}

interface GuaranteeCode {
  reservationId?: string;
  guaranteeCode: string;
  paymentMethod: string;
}

interface OhipReservationLookupResponse {
  reservations?: {
    reservation?: OhipReservation[];
  };
}

/** Methods for validating OHIP API responses. */
export class OhipHelpers {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): OhipHelpers {
    return new OhipHelpers(data);
  }

  /**
   * Validate reservation payment method
  * @param {OhipReservation} ohipReservation - actual reservation from OhipApiCalls
   * @param {String} expectedPaymentMethod - expected payment method code
   */
  static async validatePaymentMethod(
    ohipReservation: OhipReservation,
    expectedPaymentMethod: string
  ): Promise<void> {
    console.log('Validate payment method in OHIP');
    const paymentMethods = ohipReservation.reservationPaymentMethods || [];
    expect(paymentMethods.length, `Expected payment methods array length: ${paymentMethods.length} is not 1`).toEqual(1);
    expect(paymentMethods[0]?.paymentMethod, `Expected payment method value: ${expectedPaymentMethod} does not match ${paymentMethods[0]?.paymentMethod}`).toEqual(expectedPaymentMethod);
  }

  /**
   * Validate reservation status
  * @param {OhipReservation} ohipReservation - actual reservation from OhipApiCalls
  * @param {String|Promise<String>} expectedStatus - expected status code
   */
  static async validateReservationStatus(
    ohipReservation: OhipReservation,
    expectedStatus: string | Promise<string>
  ): Promise<void> {
    const resolvedStatus = await expectedStatus;
    console.log('Validate reservation status in OHIP');
    expect(ohipReservation.computedReservationStatus, `Expected Status value: ${resolvedStatus} does not match ${ohipReservation.computedReservationStatus}`).toEqual(resolvedStatus);
  }

  /**
   * Validate user defined fields of reservation in OHIP
  * @param {OhipReservation} ohipReservation - actual reservation from OhipApiCalls
   * @param {String} name - expected name
   * @param {String} value - expected value
   */
  static async validateReservationUserDefinedFields(
    ohipReservation: OhipReservation,
    name: string,
    value: string
  ): Promise<void> {
    console.log('Validate user defined fields of reservation in OHIP');
    const characterUDFs = ohipReservation.userDefinedFields?.characterUDFs || [];
    expect(characterUDFs[1]?.name, `Expected name: ${name} does not match ${characterUDFs[1]?.name}`).toEqual(name);
    expect(characterUDFs[1]?.value, `Expected value: ${value} does not match ${characterUDFs[1]?.value}`).toEqual(value);
  }

  /**
   * Validate associated profiles of reservation in OHIP
  * @param {OhipReservation} ohipReservation - actual reservation from OhipApiCalls
  * @param {CompanyData} company - expected company data
  * @param {GuestDetailsData} guestDetails - expected guest details
   */
  static async validateReservationAssociatedProfiles(
    ohipReservation: OhipReservation,
    company: CompanyData,
    guestDetails: GuestDetailsData
  ): Promise<void> {
    console.log('Validate associated profiles of reservation in OHIP');

    const reservationProfile = ohipReservation.reservationProfiles?.reservationProfile || [];
    const companyProfile = reservationProfile[0];
    const guestProfile = reservationProfile[1];
    const reservationGuest = ohipReservation.reservationGuests?.[0];

    // Company profile validation
    const companyName = companyProfile?.profile?.company?.companyName;
    expect(companyName, `Expected company name: ${company.name} does not match ${companyName}`).toBe(company.name);

    const addressInfo = reservationGuest?.profileInfo?.profile?.addresses?.addressInfo?.[0]?.address;
    const addressLineOneValue = addressInfo?.addressLine?.[0] || '';
    const addressLineTwoValue = addressInfo?.addressLine?.[1] || '';
    const addressLineThreeValue = addressInfo?.addressLine?.[2] || '';
    const addressLineFourValue = addressInfo?.addressLine?.[3] || '';
    const cityNameValue = addressInfo?.cityName || '';
    const postalCodeValue = addressInfo?.postalCode || '';

    expect(addressLineOneValue, `Expected address line 1: ${company.address?.addressLine1} does not match ${addressLineOneValue}`).toBe(company.address?.addressLine1);
    expect(addressLineTwoValue, `Expected address line 2: ${company.address?.addressLine2} does not match ${addressLineTwoValue}`).toBe(company.address?.addressLine2);
    expect(addressLineThreeValue, `Expected address line 3: ${company.address?.addressLine3} does not match ${addressLineThreeValue}`).toBe(company.address?.addressLine3);
    expect(addressLineFourValue, `Expected address line 4: ${company.address?.addressLine4} does not match ${addressLineFourValue}`).toBe(company.address?.addressLine4);

    if (company.address?.cityName) {
      expect(cityNameValue, `Expected city name: ${company.address.cityName} does not match ${cityNameValue}`).toBe(company.address.cityName);
    }

    expect(postalCodeValue, `Expected postal code: ${company.address?.postalCode} does not match ${postalCodeValue}`).toBe(company.address?.postalCode || '');
    
    const companyPhoneNumber = companyProfile?.profile?.telephones?.telephoneInfo?.[0]?.telephone?.phoneNumber;
    expect(companyPhoneNumber, `Expected telephone number: ${company.telephoneNumber} does not match ${companyPhoneNumber}`).toBe(company.telephoneNumber);
    
    const companyProfileType = companyProfile?.reservationProfileType;
    expect(companyProfileType, `Expected profile type: ${company.profileType} does not match ${companyProfileType}`).toBe(company.profileType);

    // Guest profile validation
    const expectedGivenName = `${guestDetails.booker?.lastName}, ${guestDetails.booker?.firstName}`;
    const actualGivenName = guestProfile?.profile?.customer?.personName?.[0]?.givenName;
    expect(actualGivenName, `Expected given name: ${expectedGivenName} does not match ${actualGivenName}`).toBe(expectedGivenName);
    
    const nameType = guestProfile?.profile?.customer?.personName?.[0]?.nameType;
    expect(nameType, `Expected name type: ${Constants.RESERVATION_NAME_TYPE_PRIMARY} does not match ${nameType}`).toBe(Constants.RESERVATION_NAME_TYPE_PRIMARY);
    
    const guestPhoneNumber = guestProfile?.profile?.telephones?.telephoneInfo?.[0]?.telephone?.phoneNumber;
    const expectedPhoneNumber = `${guestDetails.booker?.mobilePrefix}${guestDetails.booker?.mobile}`;
    expect(guestPhoneNumber, `Expected phone number: ${expectedPhoneNumber} does not match ${guestPhoneNumber}`).toBe(expectedPhoneNumber);
    
    const emailAddress = guestProfile?.profile?.emails?.emailInfo?.[0]?.email?.emailAddress;
    expect(emailAddress, `Expected email address: ${guestDetails.booker?.emailAddress} does not match ${emailAddress}`).toBe(guestDetails.booker?.emailAddress);
    
    const guestProfileType = guestProfile?.reservationProfileType;
    expect(guestProfileType, `Expected profile type: ${Constants.RESERVATION_PROFILE_TYPE_RESERVATION_CONTACT} does not match ${guestProfileType}`).toBe(Constants.RESERVATION_PROFILE_TYPE_RESERVATION_CONTACT);
  }

  /**
   * Validate payment reference in Ohip
   * @param {String} reference payment reference
   * @param {String} paymentId paymentId
   */
  static async validatePaymentReferenceOhip(reference: string | null, paymentId: string): Promise<void> {
    console.log('Validate payment reference in Ohip');
    const regex = /\|[0-3]$/;
    let match = true;

    if (reference !== null && reference.split('|').length === 2 && reference.split('|')[0] === paymentId) {
      match = regex.test(reference);
    } else {
      match = false;
    }

    expect(match, 'Reference should end in 0, 1, or 3').toEqual(true);
  }

  /**
   * Get guarantee code for all reservation items
   * @param {Object} data request data
   * @param {Array.<ReservationItem>} data.items reservation items with source IDs
   * @param {String} data.hotelId hotel id
   * @returns {Array.<GuaranteeCode>} guarantee codes for the reservation items
   */
  static async getGuaranteeCodes(data: {
    items?: ReservationItem[];
    hotelId?: string;
  }): Promise<GuaranteeCode[]> {
    const { items = [], hotelId } = data;
    const guaranteeCodes: GuaranteeCode[] = [];

    for (const item of items) {
      const reservationId = item.sourceId;
      if (!hotelId || !reservationId) {
        continue;
      }
      const reservationResponse = await OhipApiCalls.getHotelReservationById({ hotelId, reservationId }) as OhipReservationLookupResponse;
      const paymentMethods = reservationResponse.reservations?.reservation?.[0]?.reservationPaymentMethods ?? [];
      // Get payment method to infer guarantee type
      const primaryPaymentMethod = OhipHelpers.getLatestPaymentMethod({ paymentMethods })?.paymentMethod || '';
      // Infer guarantee code from payment method
      let inferredGuaranteeCode = 'NON'; // Default: Non-guaranteed
      if (primaryPaymentMethod && primaryPaymentMethod !== 'CA' && primaryPaymentMethod !== 'AC') {
        inferredGuaranteeCode = 'CC'; // Card payment = Credit Card Guaranteed
      }

      guaranteeCodes.push({
        reservationId,
        guaranteeCode: inferredGuaranteeCode,
        paymentMethod: primaryPaymentMethod,
      });

      console.log(
        `Reservation ${reservationId} - Payment method: '${primaryPaymentMethod}', Inferred guarantee: '${inferredGuaranteeCode}'`
      );
    }

    return guaranteeCodes;
  }

  /**
   * Get payment method by code with fallback strategies
  * @param {Object} options lookup options
  * @param {Array.<PaymentMethod>} options.paymentMethods array of payment methods
   * @param {String} options.expectedCode expected payment method code
  * @returns {PaymentMethod|null} matching payment method or null
   */
  static getPaymentMethodByCode(options: {
    paymentMethods?: PaymentMethod[];
    expectedCode?: string;
  }): PaymentMethod | null {
    const { paymentMethods, expectedCode } = options;

    if (!expectedCode || !paymentMethods || paymentMethods.length === 0) {
      return null;
    }

    // First: exact match
    let targetPaymentMethod = paymentMethods.find((pm) => pm.paymentMethod === expectedCode);
    if (targetPaymentMethod) {
      console.log(`Found exact match for payment method code: '${expectedCode}'`);
      return targetPaymentMethod;
    }

    // Second: substring match (handles corrupted codes like 'CAVA' containing 'VA')
    targetPaymentMethod = paymentMethods.find(
      (pm) => pm.paymentMethod && (pm.paymentMethod as string).includes(expectedCode)
    );
    if (targetPaymentMethod) {
      console.log(`Found substring match: '${targetPaymentMethod.paymentMethod}' contains '${expectedCode}'`);
      return targetPaymentMethod;
    }

    // Third: use the last payment method (most recent)
    targetPaymentMethod = paymentMethods[paymentMethods.length - 1];
    console.log(`Using last (most recent) payment method: '${targetPaymentMethod?.paymentMethod}'`);
    return targetPaymentMethod;
  }

  /**
   * Get the latest payment method from Opera reservation payment methods
  * @param {Object} data payment method lookup data
  * @param {Array.<PaymentMethod>} data.paymentMethods payment methods array
  * @returns {PaymentMethod|null} latest payment method or null
   */
  static getLatestPaymentMethod(data: { paymentMethods?: PaymentMethod[] }): PaymentMethod | null {
    const { paymentMethods } = data;

    if (!paymentMethods || paymentMethods.length === 0) {
      return null;
    }

    return paymentMethods[paymentMethods.length - 1];
  }
}
