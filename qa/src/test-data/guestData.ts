import { randomUUID } from 'crypto';
import { futureDate } from '../utils/dateHelpers';

/**
 * Guest personal information for form filling.
 */
export interface GuestInfo {
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  mobile: string;
  landline: string;
}

/**
 * Guest address for manual address entry.
 */
export interface GuestAddress {
  postalCode: string;
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  cityName: string;
  countryCode: string;
  /** Address classification (HOME/BUSINESS). Optional. */
  addressType?: 'HOME' | 'BUSINESS';
}

/**
 * Reason for stay selection.
 */
export type ReasonForStay = 'leisure' | 'business';

export interface RoomOccupancy {
  adultsNumber: number;
  childrenNumber: number;
}

export interface StayingGuestsAndRoomDetails {
  rooms: RoomOccupancy[];
}

/** Guest and address data used by migrated PI booking tests. */
export class GuestData {
  private constructor() {}

  // Default Test Guest

  static readonly DEFAULT_GUEST: GuestInfo = {
    title: 'Mr',
    firstName: 'Test',
    lastName: 'Automation',
    emailAddress: 'test.automation@premierinn.example.com',
    mobile: '07700900000',
    landline: '02012345678',
  };

  static readonly DEFAULT_ADDRESS: GuestAddress = {
    postalCode: 'RH6 0PH',
    addressLine1: '1 Test Street',
    addressLine2: 'Gatwick',
    addressLine3: 'West Sussex',
    cityName: 'Crawley',
    countryCode: 'GB',
  };

  // Business Guest

  static readonly BUSINESS_GUEST: GuestInfo = {
    title: 'Mrs',
    firstName: 'Business',
    lastName: 'Tester',
    emailAddress: 'business.tester@premierinn.example.com',
    mobile: '07700900001',
    landline: '02012345679',
  };

  static readonly BUSINESS_ADDRESS: GuestAddress = {
    postalCode: 'EC1A 1BB',
    addressLine1: '100 Business Park',
    addressLine2: 'City Centre',
    addressLine3: 'London',
    cityName: 'London',
    countryCode: 'GB',
  };

  // Baseline test data

  /**
   * Static guest information for test scenarios.
   * Email is unique per test run to avoid conflicts.
   */
  static readonly GUEST_INFO: GuestInfo = {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Smith',
    emailAddress: GuestData.generateUniqueEmail(),
    mobile: '07700900123',
    landline: '02071234567',
  };

  /**
   * Static home address for guest booking tests. Uses a valid UK postcode format.
   */
  static readonly GUEST_ADDRESS: GuestAddress = {
    addressType: 'HOME',
    postalCode: 'RH6 0PH',
    addressLine1: '10 Gatwick Road',
    addressLine2: 'Crawley',
    addressLine3: 'West Sussex',
    countryCode: 'GB',
    cityName: 'Crawley',
  };

  /** Check-in date: 3 days from today (ISO YYYY-MM-DD). */
  static readonly CHECK_IN_DATE = futureDate(3);
  /** Check-out date: 4 days from today (ISO YYYY-MM-DD). */
  static readonly CHECK_OUT_DATE = futureDate(4);

  // Room Occupancy

  /** Double room: 2 adults, 0 children (initial booking). */
  static readonly DOUBLE_ROOM_OCCUPANCY: RoomOccupancy = {
    adultsNumber: 2,
    childrenNumber: 0,
  };

  /** Family room: 2 adults, 1 child (after amendment). */
  static readonly FAMILY_ROOM_OCCUPANCY: RoomOccupancy = {
    adultsNumber: 2,
    childrenNumber: 1,
  };

  // Staying Guests & Room Details

  /** Initial booking: 1 double room with 2 adults. */
  static readonly INITIAL_BOOKING_GUESTS: StayingGuestsAndRoomDetails = {
    rooms: [GuestData.DOUBLE_ROOM_OCCUPANCY],
  };

  /** After amendment: 1 family room with 2 adults + 1 child. */
  static readonly AMENDED_BOOKING_GUESTS: StayingGuestsAndRoomDetails = {
    rooms: [GuestData.FAMILY_ROOM_OCCUPANCY],
  };

  // Reason for Stay

  static readonly DEFAULT_REASON_FOR_STAY: ReasonForStay = 'leisure';

  /**
   * Create guest info with optional overrides.
   */
  static createGuestInfo(overrides?: Partial<GuestInfo>): GuestInfo {
    return { ...GuestData.DEFAULT_GUEST, ...overrides };
  }

  /**
   * Create guest address with optional overrides.
   */
  static createGuestAddress(overrides?: Partial<GuestAddress>): GuestAddress {
    return { ...GuestData.DEFAULT_ADDRESS, ...overrides };
  }

  /**
   * Creates a fresh GuestInfo with a unique email per call.
   * Use for tests that need multiple distinct guests.
   */
  static generateGuestInfo(overrides?: Partial<GuestInfo>): GuestInfo {
    return {
      ...GuestData.GUEST_INFO,
      emailAddress: GuestData.generateUniqueEmail(),
      ...overrides,
    };
  }

  /** Generates a unique email address per test run using crypto.randomUUID(). */
  private static generateUniqueEmail(): string {
    return `test-${randomUUID()}@mailinator.com`;
  }

}
