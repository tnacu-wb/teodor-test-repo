import { randomUUID } from 'crypto';
import { Constants } from './constants';
import { Bookers, type BookerDetails } from './booker';
import { GuestAddresses } from './guestAddress';
import { Locales, getCurrentLocale } from './locales';
import { StayingGuestDetails, StayingGuests, type StayingGuestData } from './stayingGuest';

export interface GuestDetailsModel {
  reasonForStay: string;
  booker: BookerDetails;
  stayingGuests: StayingGuestData[];
}

/**
 * The guest details used in the app for a reservation.
 */
export class GuestDetailsData {
  private constructor() {}

  private static getCurrentTitle(): 'Mr' | 'Herr' {
    return Locales.isEnglishWebsite(getCurrentLocale().name) ? 'Mr' : 'Herr';
  }

  static readonly DEFAULT_GUEST: GuestDetailsModel = {
    reasonForStay: Constants.REASON_FOR_STAY.leisure,
    booker: Bookers.DEFAULT_BOOKER,
    stayingGuests: [StayingGuests.DEFAULT_STAYING_GUEST],
  };

  static readonly GUEST_DIFFERENT_THAN_BOOKER: GuestDetailsModel = {
    reasonForStay: Constants.REASON_FOR_STAY.leisure,
    booker: Bookers.DEFAULT_BOOKER,
    stayingGuests: [{ sameAsBooker: false, stayingGuestDetails: StayingGuestDetails.DEFAULT_STAYING_GUEST_DETAILS }],
  };

  static readonly MULTIPLE_DEFAULT_GUEST: GuestDetailsModel = {
    reasonForStay: Constants.REASON_FOR_STAY.leisure,
    booker: Bookers.DEFAULT_BOOKER,
    stayingGuests: [
      StayingGuests.DEFAULT_STAYING_GUEST,
      {
        sameAsBooker: false,
        stayingGuestDetails: {
          title: null,
          firstName: `Test${randomUUID().replace(/-/g, '').slice(0, 6)}`,
          lastName: 'Guest',
        },
      },
    ],
  };

  static readonly BUSINESS_GUEST: GuestDetailsModel = {
    reasonForStay: Constants.REASON_FOR_STAY.business,
    booker: Bookers.DEFAULT_BUSINESS_BOOKER,
    stayingGuests: [StayingGuests.DEFAULT_STAYING_GUEST],
  };

  static readonly DEFAULT_GUEST_WITH_ADDITIONAL_INFO: GuestDetailsModel = {
    reasonForStay: Constants.REASON_FOR_STAY.leisure,
    booker: Bookers.DEFAULT_BOOKER_WITH_ADDITIONAL_INFO,
    stayingGuests: [StayingGuests.DEFAULT_STAYING_GUEST],
  };

  /**
   * Generate guest details object with staying guests.
   * @param data.reasonForStay reason for stay
   * @param data.booker booker
   * @param data.numberOfStayingGuests number of staying guests
   * @returns guest details object with staying guests
   */
  static generateGuestDetailsWithStayingGuests({
    reasonForStay = Constants.REASON_FOR_STAY.leisure,
    booker = Bookers.DEFAULT_BOOKER,
    numberOfStayingGuests = 0,
  }: {
    reasonForStay?: string;
    booker?: BookerDetails;
    numberOfStayingGuests?: number;
  } = {}): GuestDetailsModel {
    const guestDetails: GuestDetailsModel = {
      reasonForStay,
      booker,
      stayingGuests: [],
    };

    if (numberOfStayingGuests > 0) {
      const title = GuestDetailsData.getCurrentTitle();
      guestDetails.stayingGuests.push({
        ...StayingGuests.DEFAULT_STAYING_GUEST,
        stayingGuestDetails: { ...StayingGuestDetails.DEFAULT_STAYING_GUEST_DETAILS, title },
      });
      guestDetails.booker = { ...guestDetails.booker, title };
    }

    for (let index = 0; index < numberOfStayingGuests - 1; index++) {
      guestDetails.stayingGuests.push({
        sameAsBooker: false,
        stayingGuestDetails: {
          title: GuestDetailsData.getCurrentTitle(),
          firstName: `Test${randomUUID().replace(/-/g, '').slice(0, 6)}`,
          lastName: 'Guest',
        },
      });
    }

    return guestDetails;
  }

  /**
   * Generate GuestDetails object.
   * @returns guestDetails object used in UI
   */
  static generateGuestDetails({
    reasonForStay = Constants.REASON_FOR_STAY.leisure,
    title = null,
    addressType = 'HOME',
    addressPostalCode = null,
    countryCode = 'GB',
    phoneCountry = 'uk',
    numberOfStayingGuests = 0,
    dateOfBirth,
    nationality,
    passport,
  }: {
    reasonForStay?: string;
    title?: string | null;
    addressType?: string;
    addressPostalCode?: string | null;
    countryCode?: string;
    phoneCountry?: string;
    numberOfStayingGuests?: number;
    dateOfBirth?: string;
    nationality?: string;
    passport?: unknown;
  } = {}): GuestDetailsModel {
    const address = {
      ...GuestAddresses.DEFAULT_GUEST_ADDRESS,
      addressType,
      postalCode: addressPostalCode ? addressPostalCode.toUpperCase() : Constants.VALID_UK_POSTCODE,
      countryCode,
      companyName: addressType === 'BUSINESS' ? 'TestCompany' : undefined,
    };
    const isGermanPhone = phoneCountry.toLowerCase().startsWith('de');
    const booker = Bookers.createBooker({
      title,
      address,
      dateOfBirth,
      nationality,
      passport,
      mobile: (isGermanPhone ? Constants.VALID_MOBILE_NUMBER_PAY_APP_DE : Constants.VALID_MOBILE_NUMBER_PAY_APP_GB).replace(/^0+/, ''),
      landline: (isGermanPhone ? Constants.VALID_LAND_LINE_PAY_APP_DE : Constants.VALID_LAND_LINE_PAY_APP_GB).replace(/^0+/, ''),
    });

    return GuestDetailsData.generateGuestDetailsWithStayingGuests({ reasonForStay, booker, numberOfStayingGuests });
  }

  /**
   * Validate Guest details against InfoGuestProfile from Opera.
   * @param data.guestDetails object guestDetails
   * @param data.infoGuestProfile object infoGuestProfile
   */
  static async validateGuestDetailsAgainstProfile({ guestDetails, infoGuestProfile, ...prefixes }: {
    guestDetails: GuestDetailsModel;
    infoGuestProfile: any;
    mobile?: string;
    landline?: string;
  }): Promise<void> {
    const addPrefix = (number: string, prefix = '') => `${prefix}${Number(number.replace(/\s/g, '')).toString()}`;

    global.expect(guestDetails.booker.title, 'The nameTitle is not the same').toBe(infoGuestProfile.profileDetails.customer.personName[0].nameTitle);
    global.expect(guestDetails.booker.firstName, 'The givenName is not the same').toBe(infoGuestProfile.profileDetails.customer.personName[0].givenName);
    global.expect(guestDetails.booker.lastName, 'The surname is not the same').toBe(infoGuestProfile.profileDetails.customer.personName[0].surname);
    global.expect(guestDetails.booker.emailAddress, 'The emailAddress is not the same').toBe(infoGuestProfile.profileDetails.emails.emailInfo[0].email.emailAddress);

    const indexes: Record<string, number> = {};
    for (const [index, element] of infoGuestProfile.profileDetails.telephones.telephoneInfo.entries()) {
      indexes[element.type] = index;
    }

    global.expect(addPrefix(guestDetails.booker.mobile, prefixes.mobile), 'The mobile is not the same').toBe(infoGuestProfile.profileDetails.telephones.telephoneInfo[indexes.MOBILE].telephone.phoneNumber);
    global.expect(addPrefix(guestDetails.booker.landline, prefixes.landline), 'The landline is not the same').toBe(infoGuestProfile.profileDetails.telephones.telephoneInfo[indexes.HOME].telephone.phoneNumber);
    global.expect(guestDetails.booker.address.countryCode, 'The countryCode is not the same').toBe(infoGuestProfile.profileDetails.customer.citizenCountry.code);
  }
}
