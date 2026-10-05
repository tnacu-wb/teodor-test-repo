import { randomUUID } from 'crypto';
import { Constants } from './constants';
import { GuestAddresses, type GuestAddressDetails } from './guestAddress';

export interface BookerDetails {
  title: string | null;
  firstName: string;
  lastName: string;
  emailAddress: string;
  mobile: string;
  mobilePrefix: string;
  landline: string;
  landlinePrefix: string;
  acceptFutureMailing: boolean;
  address: GuestAddressDetails;
  dateOfBirth?: string;
  nationality?: string;
  passport?: unknown;
  carRegistration?: string;
}

/**
 * Booker Object used for Guest Reservation
 */
export class Bookers {
  private constructor() {}

  static get DEFAULT_BOOKER(): BookerDetails {
    return Bookers.createBooker({ firstName: 'Test Auto' });
  }

  static get CONTACT_BANK_BOOKER(): BookerDetails {
    return Bookers.createBooker({ firstName: 'Testt' });
  }

  static get INCORRECT_CARD_DETAILS_BOOKER(): BookerDetails {
    return Bookers.createBooker({ firstName: 'TesttAutoJr' });
  }

  static get TRY_AGAIN_BOOKER(): BookerDetails {
    return Bookers.createBooker({ firstName: 'Test' });
  }

  static get DEFAULT_BUSINESS_BOOKER(): BookerDetails {
    return Bookers.createBooker({ firstName: 'Test Auto', address: GuestAddresses.DEFAULT_BUSINESS_ADDRESS });
  }

  static get DEFAULT_BOOKER_WITH_ADDITIONAL_INFO(): BookerDetails {
    return Bookers.createBooker({
      firstName: 'Test Auto',
      dateOfBirth: '01-01-2000',
      nationality: 'Afghan',
      passport: 'ABC12345',
    });
  }

  /**
   * Create a booker object from logged user details.
   * @param userProfileDetails logged user profile details
   * @returns a booker object based on user profile details
   */
  static getUserProfileDetailsBooker(userProfileDetails: any): BookerDetails {
    return {
      title: userProfileDetails.contactDetail.title,
      firstName: userProfileDetails.contactDetail.firstName,
      lastName: userProfileDetails.contactDetail.lastName,
      emailAddress: userProfileDetails.contactDetail.email,
      mobile: userProfileDetails.contactDetail.mobile,
      mobilePrefix: '',
      landline: userProfileDetails.contactDetail.telephone,
      landlinePrefix: '',
      carRegistration: userProfileDetails.contactDetail.carRegistration,
      nationality: userProfileDetails.contactDetail.nationality,
      passport: {
        number: userProfileDetails.contactDetail.passport ? userProfileDetails.contactDetail.passport.number : undefined,
        countryOfIssue: userProfileDetails.contactDetail.passport ? userProfileDetails.contactDetail.passport.countryOfIssue : undefined,
      },
      acceptFutureMailing: false,
      address: {
        addressType: 'HOME',
        postalCode: userProfileDetails.contactDetail.address.postCode,
        addressLine1: userProfileDetails.contactDetail.address.line1,
        addressLine2: userProfileDetails.contactDetail.address.line2,
        addressLine3: userProfileDetails.contactDetail.address.line3,
        addressLine4: userProfileDetails.contactDetail.address.line4,
        countryCode: userProfileDetails.contactDetail.address.countryCode,
        cityName: userProfileDetails.contactDetail.address.line5,
      },
    };
  }

  static createBooker(overrides: Partial<BookerDetails> = {}): BookerDetails {
    return {
      title: null,
      firstName: 'Test Auto',
      lastName: 'Testersons',
      emailAddress: `automationwb${randomUUID().replace(/-/g, '').slice(0, 10)}@mailinator.com`,
      mobile: '36343483842',
      mobilePrefix: '+3',
      landline: '36343483842',
      landlinePrefix: '+3',
      acceptFutureMailing: false,
      address: GuestAddresses.DEFAULT_GUEST_ADDRESS,
      ...overrides,
    };
  }
}

export interface NewBookerDetails {
  title: string | null;
  firstName: string;
  lastName: string;
  emailAddress: string;
  password: string;
  mobilePhone: string;
  alternatePhone: string;
  address: GuestAddressDetails;
}

/**
 * Booker Object used for creating account
 */
export class NewBookers {
  private constructor() {}

  static get NEW_BOOKER(): NewBookerDetails {
    return NewBookers.createNewBooker({ address: GuestAddresses.DEFAULT_GUEST_ADDRESS });
  }

  static get NEW_BUSINESS_BOOKER(): NewBookerDetails {
    return NewBookers.createNewBooker({ address: GuestAddresses.DEFAULT_BUSINESS_ADDRESS });
  }

  static createNewBooker(overrides: Partial<NewBookerDetails> = {}): NewBookerDetails {
    return {
      title: null,
      firstName: `Test${randomUUID().replace(/-/g, '').slice(0, 6)}`,
      lastName: 'Testersons',
      emailAddress: Constants.RANDOM_GENERATED_EMAIL,
      password: Constants.INN_BUSINESS_USER_GUEST_PASSWORD,
      mobilePhone: '36343483842',
      alternatePhone: '7655518056',
      address: GuestAddresses.DEFAULT_GUEST_ADDRESS,
      ...overrides,
    };
  }
}
