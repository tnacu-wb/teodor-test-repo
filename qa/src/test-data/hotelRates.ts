/** Hotel rate used in booking and availability flows. */
export interface HotelRate {
  name: string;
  aka: string;
  /** Rate plan code used in API requests. */
  ratePlanCode: string;
  brand: string;
  prefix: string;
  classification: string;
}

/** The hotel rates used in the app. */
export class HotelRates {
  private constructor() {}

  static readonly PI_FLEX: HotelRate = { name: 'Flex', aka: 'Flex', ratePlanCode: 'FLEXRATE', brand: 'PI', prefix: 'RT', classification: 'A' };
  static readonly PI_SEMI_FLEX: HotelRate = { name: 'Semi-Flex', aka: 'Semi-Flex', ratePlanCode: 'SEMIFLEX', brand: 'PI', prefix: 'RT', classification: 'A' };
  static readonly PI_NON_FLEX: HotelRate = { name: 'Non-Flex', aka: 'Non-Flex', ratePlanCode: 'NONFLEX', brand: 'PI', prefix: 'RT', classification: 'A' };
  static readonly PI_ADVANCE: HotelRate = { name: 'Advance', aka: 'Advance', ratePlanCode: 'ADVANCE', brand: 'PI', prefix: 'RT', classification: 'A' };
  static readonly PI_STANDARD: HotelRate = { name: 'Standard', aka: 'Standard', ratePlanCode: 'STANDARD', brand: 'PI', prefix: 'RT', classification: 'S' };
  static readonly BUSINESS_FLEX: HotelRate = { name: 'Business Flex', aka: 'Business Flex', ratePlanCode: 'BUSIFLEX', brand: 'PI', prefix: 'EE', classification: 'A' };

  /**
   * Get hotel rate based on name.
   * @param name Hotel rate name.
   * @returns Matching hotel rate.
   */
  static getHotelRateByName(name: string): HotelRate {
    const found = Object.values(HotelRates).find((rate): rate is HotelRate => typeof rate === 'object' && rate.name === name);
    if (!found) {
      throw new Error(`${name} hotel rate name is not found in the list!`);
    }
    return found;
  }

  /**
   * Get hotel rate based on ratePlanCode.
   * @param ratePlanCode Rate plan code.
   * @returns Matching hotel rate.
   */
  static getHotelRateByRatePlanCode(ratePlanCode: string): HotelRate {
    const found = Object.values(HotelRates).find((rate): rate is HotelRate => typeof rate === 'object' && rate.ratePlanCode === ratePlanCode);
    if (!found) {
      throw new Error(`${ratePlanCode} hotel rate plan code is not found in the list!`);
    }
    return found;
  }
}
