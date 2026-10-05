import { type RoomConfig } from '../components/pi/searchConsole/searchConsole.component';
import { futureDate } from '../utils/dateHelpers';

/**
 * Booking search criteria — dates + room configuration.
 */
export interface BookingCriteria {
  /** Check-in date in ISO format (YYYY-MM-DD). */
  checkInDate: string;
  /** Check-out date in ISO format (YYYY-MM-DD). */
  checkOutDate: string;
  /** Room configurations. */
  rooms: RoomConfig[];
}

export class BookingCriteriaData {
  private constructor() {}

  // ─── Helpers ────────────────────────────────────────────────────────────────

  /**
   * Create a standard 1-night booking criteria for a single room.
   *
   * @param daysFromNow - How many days from today check-in should be (default: 14)
   * @param adults - Number of adults (default: 2)
   * @param children - Number of children (default: 0)
   */
  static createSingleNightCriteria(
    daysFromNow = 14,
    adults = 2,
    children = 0,
  ): BookingCriteria {
    return {
      checkInDate: futureDate(daysFromNow),
      checkOutDate: futureDate(daysFromNow + 1),
      rooms: [{ adults, children }],
    };
  }

  /**
   * Create a multi-night booking criteria.
   *
   * @param daysFromNow - How many days from today check-in should be
   * @param nights - Number of nights
   * @param rooms - Room configurations
   */
  static createMultiNightCriteria(
    daysFromNow: number,
    nights: number,
    rooms: RoomConfig[] = [{ adults: 2, children: 0 }],
  ): BookingCriteria {
    return {
      checkInDate: futureDate(daysFromNow),
      checkOutDate: futureDate(daysFromNow + nights),
      rooms,
    };
  }
}
