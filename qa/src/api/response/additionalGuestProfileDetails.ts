/**
 * Additional guest profile details
 *       {
 *           title: 'Mr',
 *           firstName: 'test',
 *           lastName: 'gdfhsdg',
 *           email: 'test909@gmail.com',
 *           mobile: '+448989898989',
 *           nationality: 'GB',
 *           carRegistration: ''
 *       }
 */
export class AdditionalGuestProfileDetails {
  [key: string]: unknown;
  carRegistration?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  mobile?: string;
  nationality?: string;
  title?: string;

  /**
   * AdditionalGuestProfileDetails constructor
   * @param data object data
   * @param data.additionalGuestProfileDetails additionalGuestProfileDetails
   */
  constructor(data: { additionalGuestProfileDetails?: Record<string, unknown> } = {}) {
    const additionalGuestProfileDetails = data.additionalGuestProfileDetails ?? {};
    this.title = additionalGuestProfileDetails.title as string | undefined;
    this.firstName = additionalGuestProfileDetails.firstName as string | undefined;
    this.lastName = additionalGuestProfileDetails.lastName as string | undefined;
    this.email = additionalGuestProfileDetails.email as string | undefined;
    this.mobile = additionalGuestProfileDetails.mobile as string | undefined;
    this.nationality = additionalGuestProfileDetails.nationality as string | undefined;
    this.carRegistration = additionalGuestProfileDetails.carRegistration as string | undefined;
  }

  static fromResponse(data: { additionalGuestProfileDetails?: Record<string, unknown> }): AdditionalGuestProfileDetails {
    return new AdditionalGuestProfileDetails(data);
  }
}
