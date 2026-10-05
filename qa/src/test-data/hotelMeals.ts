import { Hotels } from './hotels';
import { Strings } from './strings';
import { type StringBase } from './stringBase';
import { ApiHelpers } from '../api/apiHelpers';
import { type ReservationInfo } from '../api/response';

/** Meal label used when validating adult meal package availability. */
export type HotelMealName = StringBase;

/** Map of hotel ID to configured meal labels. */
export type HotelMealsMap = Record<string, HotelMealName[]>;

/**
 * Hotel-to-meals mapping used when validating meal availability by hotel.
 */
export class HotelMeals {
  private constructor() {}

  static readonly MEALS_BY_HOTEL_ID: HotelMealsMap = {
    [Hotels.BANGOR_GWYNEDD_NORTH_WALES.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.BERLIN_ALEXANDERPLATZ.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.CARDIFF.id]: [Strings.PREMIER_INN_ZIP],
    [Hotels.CHRISTCHURCH_HIGHCLIFFE.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.DONCASTER_CENTRAL_HIGH_FISHERGATE.id]: [],
    [Hotels.DOUGLAS_ISLE_OF_MAN.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.DRESDEN_CITY_CENTER.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.DUBLIN_CITY_CENTER_TEMPLE_BAR.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.DUNFERMLINE.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.DUSSELDORF_CITY_CENTRE.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.EDINBURGH_PARK_AIRPORT.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.FALKIRK_NORTH.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.FRANKFURT_CITY_CENTRE.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.FRANKFURT_MESSE.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.FRANKFURT_WESTEND.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.GLASGOW_CITY_CENTER_GEORGE_SQUARE.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.GLASGOW_PACIFIC_QUAY_SECC.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.HAYDOCK_PARK_M6_J23.id]: [],
    [Hotels.LONDON_COVENT_GARDEN.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST],
    [Hotels.LONDON_WEST_BROMPTON.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.LEEDS_CITY_CENTRE_LEEDS_ARENA.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.LONDON_FINSBURY.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.LONEUS.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.MAIDSTONE_A26_WATERINGBURY.id]: [Strings.CONTINENTAL_BREAKFAST],
    [Hotels.MANCHESTER_OLD_TRAFFORD.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.MANCHESTER_CITY_CENTRE_PORTLAND.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.NEWCASTLE_CITY_CENTER_THE_GATE.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.NEWHAVEN.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.STUTTGART_AIRPORT_MESSE.id]: [Strings.BREAKFAST_ANCILLARIES],
    [Hotels.PRESTON_EAST.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.WORKSOP.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
    [Hotels.YORK_SOUTH_WEST.id]: [Strings.PREMIER_INN_BREAKFAST, Strings.CONTINENTAL_BREAKFAST, Strings.MEAL_DEAL],
  };

  static get hotelMealsMap(): Map<string, HotelMealName[]> {
    return new Map(Object.entries(HotelMeals.MEALS_BY_HOTEL_ID));
  }

  /**
   * Get configured meal names for a hotel and validate them against the packages API when reservationInfo is supplied.
   * @param reservationInfo Reservation info used by the packages API, or the hotel ID when only names are needed.
   * @param hotelId Hotel ID.
   * @returns Configured meal labels.
   */
  static async getHotelMealsById(reservationInfo: ReservationInfo | string | undefined, hotelId?: string): Promise<string[]> {
    const resolvedHotelId = hotelId ?? String(reservationInfo);
    const foundHotelsMeals = HotelMeals.MEALS_BY_HOTEL_ID[resolvedHotelId] ?? [];
    const configuredMeals: string[] = [];
    for (const element of foundHotelsMeals) {
      configuredMeals.push(await element.name);
    }

    if (hotelId) {
      global.expect(configuredMeals, `Unexpected meals for hotel with id ${hotelId}`).toEqual(await ApiHelpers.getAdultMealsNamesFromPackagesCall(reservationInfo as ReservationInfo));
    }

    return configuredMeals;
  }
}
