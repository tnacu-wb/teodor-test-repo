import { ApiCalls } from './apiCalls';
import { Basket, Donations, BookingAllowances } from '../response';
import { withLocaleDefaults } from '../../test-data/locales';
import { Hotels } from '../../test-data/hotels';
import { HotelRates } from '../../test-data/hotelRates';

interface DonationsParams {
  hotelId?: string;
  language?: string;
  country?: string;
  rateCode?: string;
  bookingChannel?: string;
}

/**
 * Methods for accessing Premier Inn API backend resources.
 * Experience layer - Basket Query and Mutations
 */
export class ApiBasketCalls {
  [key: string]: unknown;

  private static getAppCode(): string {
    return String((globalThis as any).browser?.options?.app ?? 'pi');
  }

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiBasketCalls {
    return new ApiBasketCalls(data);
  }

  /**
   * GraphQL query for GetBasketByBasketReference
   * @param {String} basketReference basket object
   * @returns {Basket} Basket object that keeps the data from the response
   */
  static async graphqlGetBasketByBasketReference(basketReference: string): Promise<Basket> {
    const variables = { basketReference } as Record<string, unknown>;
    const response = await ApiCalls.makeGraphqlCall('getBasketByBasketReference.graphql', variables);
    const basketData = (response as any).body.data.basket;
    return new Basket({ basket: basketData });
  }

  /** 
   * GraphQL query for GetDonations
   * @param {Object} inputData used to set the GraphQL variables for getDonations call.
   * @param {String} inputData.hotelId Hotel Id for which to get the data
   * @param {String} inputData.language Language for which to get the data
   * @param {String} inputData.country Country for which to get the data
   * @param {String} inputData.rateCode The rate code for which to get the data
   * @param {String} inputData.bookingChannel booking channel code
   * @returns {Donations} Donations object that keeps the data from the response
   */
  static async graphqlGetDonations({
    hotelId = Hotels.DEFAULT_HOTEL.id,
    language,
    country,
    rateCode = HotelRates.PI_FLEX.ratePlanCode,
    bookingChannel = ApiBasketCalls.getAppCode().toUpperCase()
  }: DonationsParams = {}): Promise<Donations> {
    const variables = { hotelId, ...withLocaleDefaults({ country, language }), rateCode, bookingChannel } as Record<string, unknown>;
    const response = await ApiCalls.makeGraphqlCall('getDonations.graphql', variables);
    const donations = (response as any).body.data.donations;
    return new Donations({ donations });
  }

  /**
   * Get booking Allowances by Basket Reference
   * @param {String} basketReference basket reference taken from reservation object
   * @returns {BookingAllowances[]} returns a list of booking allowance objects
   */
  static async graphqlGetBookingAllowanceByBasketReference(basketReference: string): Promise<BookingAllowances[]> {
    const variables = { basketReference } as Record<string, unknown>;
    const response = await ApiCalls.makeGraphqlCall('getBookingAllowancesByBasketReference.graphql', variables);
    const bookingAllowancesList = (response as any).body.data.bookingAllowances.bookingAllowances;
    const bookingAllowances: BookingAllowances[] = [];

    for (const element of bookingAllowancesList) {
      bookingAllowances.push(new BookingAllowances({ bookingAllowance: element }));
    }

    return bookingAllowances;
  }

}
