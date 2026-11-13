export interface StayingGuestDetailsData {
  title: string | null;
  firstName: string;
  lastName: string;
}

/** Staying Guest Details object used for Create Reservation Guest. */
export class StayingGuestDetails {
  private constructor() {}

  static readonly DEFAULT_STAYING_GUEST_DETAILS: StayingGuestDetailsData = {
    title: null,
    firstName: 'Test Auto',
    lastName: 'Staying Guest',
  };
}

export interface StayingGuestData {
  sameAsBooker: boolean;
  stayingGuestDetails: StayingGuestDetailsData;
}

/** Staying Guest object used for Create Reservation Guest. */
export class StayingGuests {
  private constructor() {}

  static readonly DEFAULT_STAYING_GUEST: StayingGuestData = {
    sameAsBooker: true,
    stayingGuestDetails: StayingGuestDetails.DEFAULT_STAYING_GUEST_DETAILS,
  };
}
