/**
 * User profile details
 * {
 *   sessionId: 'Khlhzcee24ex0Wrh',
 *   contactDetail: {
 *       title: 'Mrs',
 *       firstName: 'Automation',
 *       lastName: 'Tests',
 *       email: 'piqa@mailinator.com',
 *       mobile: '7834543237',
 *       nationality: '',
 *       passport: { number: '', countryOfIssue: '' },
 *       carRegistration: '',
 *       address: {
 *           line1: 'NORTH HIGHLAND',
 *           line2: '120 HOLBORN',
 *           line3: '',
 *           line4: 'LONDON',
 *           line5: '',
 *           postCode: 'EC1N 2TD',
 *           countryCode: 'GB',
 *           type: 'HOME',
 *           companyName: ''
 *       }
 *   },
 *   additionalGuests: [
 *       {
 *           title: 'Mr',
 *           firstName: 'test',
 *           lastName: 'gdfhsdg',
 *           email: 'test909@gmail.com',
 *           mobile: '+448989898989',
 *           telephone: '',
 *           nationality: 'GB',
 *           carRegistration: ''
 *       }
 *   ],
 *   bookingPreference: {
 *       roomRequirements: {
 *           type: 'DB',
 *           adults: 2,
 *           children: 0,
 *           cotRequired: false,
 *           hotelBrand: 'PI'
 *       },
 *       foodPreference: 11,
 *       wantSmsConfirmations: false
 *   },
 *   businessUse: false,
 *   guestHistoryNumber: 'G54076774',
 *   guestHistoryCreation: '2021-01-28',
 *   totalStays: 0
 * }
 */
export class UserProfileDetails {
  [key: string]: unknown;
  additionalGuests: unknown[] = [];
  bookingPreference?: { foodPreference?: unknown; wantSmsConfirmations?: unknown };
  business?: { accessLevel?: unknown; employeeId?: unknown };
  businessUse?: unknown;
  companyId?: unknown;
  contactDetail?: {
    title?: unknown;
    firstName?: unknown;
    lastName?: unknown;
    email?: unknown;
    mobile?: unknown;
    telephone?: unknown;
    nationality?: unknown;
    passport?: unknown;
    carRegistration?: unknown;
    address?: {
      line1?: unknown;
      line2?: unknown;
      line3?: unknown;
      line4?: unknown;
      line5?: unknown;
      postCode?: unknown;
      countryCode?: unknown;
      type?: unknown;
      companyName?: unknown;
    };
  };
  guestHistoryCreation?: unknown;
  guestHistoryNumber?: unknown;
  totalStays?: unknown;

  /**
   * User Profile Details constructor
   * @param data object data
   * @param data.userProfileDetailsApiResponse response from API
   */
  constructor(data: { userProfileDetailsApiResponse?: Record<string, unknown> } = {}) {
    const userProfileDetailsApiResponse = data.userProfileDetailsApiResponse ?? {};
    const contactDetail = (userProfileDetailsApiResponse.contactDetail ?? {}) as Record<string, unknown>;
    const address = (contactDetail.address ?? {}) as Record<string, unknown>;
    const bookingPreference = userProfileDetailsApiResponse.bookingPreference as Record<string, unknown> | undefined;
    const business = userProfileDetailsApiResponse.business as Record<string, unknown> | undefined;

    this.additionalGuests = [];
    this.contactDetail = {
      title: contactDetail.title,
      firstName: contactDetail.firstName,
      lastName: contactDetail.lastName,
      email: contactDetail.email,
      mobile: contactDetail.mobile,
      telephone: contactDetail.telephone,
      nationality: contactDetail.nationality,
      passport: contactDetail.passport,
      carRegistration: contactDetail.carRegistration,
      address: {
        line1: address.line1,
        line2: address.line2,
        line3: address.line3,
        line4: address.line4,
        line5: address.line5,
        postCode: address.postCode,
        countryCode: address.countryCode,
        type: address.type,
        companyName: address.companyName,
      },
    };
    this.bookingPreference = {
      foodPreference: bookingPreference ? bookingPreference.foodPreference : undefined,
      wantSmsConfirmations: bookingPreference ? bookingPreference.wantSmsConfirmations : undefined,
    };
    this.businessUse = userProfileDetailsApiResponse.businessUse;
    this.guestHistoryNumber = userProfileDetailsApiResponse.guestHistoryNumber;
    this.guestHistoryCreation = userProfileDetailsApiResponse.guestHistoryCreation;
    this.totalStays = userProfileDetailsApiResponse.totalStays;
    this.companyId = userProfileDetailsApiResponse.companyId;
    if (business) {
      this.business = {
        accessLevel: business.accessLevel,
        employeeId: business.employeeId,
      };
    }
  }

  static fromResponse(data: { userProfileDetailsApiResponse?: Record<string, unknown> }): UserProfileDetails {
    return new UserProfileDetails(data);
  }

}
