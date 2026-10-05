import { expect } from '@playwright/test';
import { Constants } from '../test-data/constants';
import { Strings } from '../test-data/strings';

/** Basic helper methods migrated from the reference Helpers utility. */
export class Helpers {
  private constructor() {}

  /**
   * Return a telephone number without its leading zero or whitespace.
   * @param telephoneNumber telephone number
   * @returns normalized telephone number
   */
  static getTelephoneNumberWithoutZero(telephoneNumber: string): string {
    return (telephoneNumber.startsWith('0') ? telephoneNumber.slice(1) : telephoneNumber).replace(/\s/g, '');
  }

  /**
   * Return the country code used by the prepayments list.
   * @param hotel hotel with a country code
   * @returns prepayment country code
   */
  static getPrepaymentCountryCode(hotel: { countryCode: string }): string {
    if (hotel.countryCode === Constants.UK_COUNTRY_CODE) return 'UK';
    if (hotel.countryCode === Constants.GERMANY_COUNTRY_CODE) return 'GERMANY';
    return hotel.countryCode.toUpperCase();
  }

  /**
   * Validate that a date's day of week matches the localized AEM day.
   * @param date API date value
   * @param day expected UI day value
   */
  static async validateDayOfWeek(date: string, day: string): Promise<void> {
    const days = (await Strings.FULL_WEEK.name).split('|');
    expect(days[new Date(`${date}T12:00:00`).getDay()], 'Day of the week matches value from AEM').toBe(day);
  }

  /**
   * Remove markup, entities, and non-readable characters from a string.
   * @param inputString string to simplify
   * @returns simplified string
   */
  static removeNonReadableCharsFromString(inputString: string): string {
    return inputString.replace(/&nbsp;/g, ' ').replace(/\>[ ]+\</g, '><').replace(/<([^>]+)>/g, '').replace(/\u00a0/g, ' ').replace(/&quot;/g, '"').replace(/[\r\n\t\f\v]+/g, '').replace('  ', ' ').trim();
  }
}