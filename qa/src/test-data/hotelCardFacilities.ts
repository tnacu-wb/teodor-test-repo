/** Map of hotel-card facility label to available facility codes. */
export type HotelCardFacilityMap = Record<string, string[]>;

/**
 * Facilities displayed on the search-results hotel card when available.
 */
export class HotelCardFacilities {
  private constructor() {}

  static readonly FACILITY_CODES: HotelCardFacilityMap = {
    Accessible: ['HAR'],
    'Air conditioning': ['ACO', 'HAC'],
    Parking: ['COC', 'COP', 'CPF', 'CPP', 'HEX', 'PAF'],
    Restaurant: ['DIN', 'HRS', 'RES', 'ZFB'],
  };

  static get hotelCardFacilitiesMap(): Map<string, string[]> {
    return new Map(Object.entries(HotelCardFacilities.FACILITY_CODES));
  }

  /**
   * Get facility codes displayed on a search-results hotel card.
   * The Accessible facility is included only when the searched room is accessible.
   * @param isAccessibleRoom Whether the searched room type is accessible.
   * @returns Facility labels mapped to their corresponding facility codes.
   */
  static getHotelCardFacilityCodes(isAccessibleRoom: boolean): HotelCardFacilityMap {
    if (isAccessibleRoom) {
      return HotelCardFacilities.FACILITY_CODES;
    }

    const { Accessible: _ignored, ...withoutAccessible } = HotelCardFacilities.FACILITY_CODES;
    return withoutAccessible;
  }

  /**
   * Get dictionary containing hotel facilities codes and corresponding description that should be displayed on a Hotel Card from Search Results page.
   * @param data.isAccessibleRoom true if the searched room has type Accessible, false if not
   * @returns Map object containing facility types and their corresponding codes
   */
  static async getHotelCardFacilityCodesDictionary({ isAccessibleRoom }: { isAccessibleRoom: boolean }): Promise<Map<string, string[]>> {
    const map = HotelCardFacilities.hotelCardFacilitiesMap;
    if (!isAccessibleRoom) {
      map.delete('Accessible');
    }
    return map;
  }
}
