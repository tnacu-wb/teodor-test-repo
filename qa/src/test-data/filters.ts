import { type StringBase } from './stringBase';
import { Strings } from './strings';

/** Filter that can be used to filter hotels from the Search Results page. */
export interface Filter {
  name: StringBase;
  code: string;
}

/** Collection of filters that can be used to filter hotels from the Search Results page. */
export class Filters {
  private constructor() {}

  static readonly FREE_PARKING: Filter = { name: Strings.FILTERS_FREE_PARKING, code: 'CPF' };
  static readonly CHARGEABLE_OFF_SITE_PARKING: Filter = { name: Strings.FILTERS_CHARGEABLE_OFF_SITE, code: 'COP' };
  static readonly DYNAMIC_CHARGEABLE_OFF_SITE_PARKING: Filter = { name: Strings.FILTERS_CHARGEABLE_OFF_SITE_DYNAMIC, code: 'COP,COC' };
  static readonly CHARGEABLE_ON_SITE_PARKING: Filter = { name: Strings.FILTERS_CHARGEABLE_ON_SITE, code: 'CPP' };
  static readonly AIR_CONDITIONING: Filter = { name: Strings.FILTERS_AIR_CONDITIONING, code: 'ACO' };
  static readonly DYNAMIC_AIR_CONDITIONING: Filter = { name: Strings.FILTERS_AIR_CONDITIONING_DYNAMIC, code: 'ACO,HAC' };
  static readonly LIFT_ACCESS: Filter = { name: Strings.FILTERS_LIFT_ACCESS, code: 'LFT' };
  static readonly DYNAMIC_LIFT_ACCESS: Filter = { name: Strings.FILTERS_LIFT_ACCESS_DYNAMIC, code: 'LFT,HUL' };
  static readonly MEETING_ROOMS: Filter = { name: Strings.FILTERS_MEETING_ROOMS, code: 'MEE' };
  static readonly RESTAURANT: Filter = { name: Strings.FILTERS_RESTAURANT, code: 'EAT' };
  static readonly RESTAURANT_DYNAMIC: Filter = { name: Strings.FILTERS_RESTAURANT_DYNAMIC, code: 'RES,DIN,HRS' };
  static readonly DYNAMIC_PREMIER_PLUS_ROOMS: Filter = { name: Strings.FILTERS_PREMIER_PLUS_ROOMS_DYNAMIC, code: 'PRR' };
  static readonly DYNAMIC_EV_CHARGING_POINT: Filter = { name: Strings.FILTERS_EV_CHARGING_POINT_DYNAMIC, code: 'EVC' };
  static readonly DYNAMIC_INTERCONNECTING_ROOMS: Filter = { name: Strings.FILTERS_INTERCONNECTING_ROOMS_DYNAMIC, code: 'ICR' };
  static readonly DYNAMIC_LUGGAGE_STORAGE: Filter = { name: Strings.FILTERS_LUGGAGE_STORAGE_DYNAMIC, code: 'HLG,LUG' };
  static readonly DYNAMIC_ACCESSIBLE_ROOM_LOWERED_BATH: Filter = { name: Strings.FILTERS_ACCESSIBLE_ROOM_LOWERED_BATH_DYNAMIC, code: 'LWB' };
  static readonly DYNAMIC_ACCESSIBLE_ROOM_WET_ROOM: Filter = { name: Strings.FILTERS_ACCESSIBLE_ROOM_WET_ROOM_DYNAMIC, code: 'WET' };

  /**
   * Get hotel facility code(s) corresponding to given hotel facility name(s).
   * @param data.filterNames The facility name(s): Free parking, Air conditioning, etc. Can be a string or an array.
   * @returns The code(s) of the facility: CPF, ACO, etc. Returns a string or an array based on the input.
   */
  static async getFilterCodeByName(data: { filterNames: string | string[] } | string | string[]): Promise<string | string[]> {
    const filterNames = typeof data === 'object' && !Array.isArray(data) ? data.filterNames : data;
    const names = Array.isArray(filterNames) ? filterNames : [filterNames];
    const codes: string[] = [];

    for (const name of names) {
      let found: Filter | null = null;
      for (const filter of Object.values(Filters)) {
        if (typeof filter === 'object' && 'name' in filter && await filter.name.name === name) {
          found = filter;
          break;
        }
      }
      if (!found) {
        throw new Error(`${name} filter name is not found in the list!`);
      }
      codes.push(found.code);
    }

    return Array.isArray(filterNames) ? codes : codes[0];
  }
}
