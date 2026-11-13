/**
 * response example
 * {
            "stayingGuests": [
                {
                    "firstName": 'Test Auto'
                    "lastName": 'VannozzißÜÖÄüöä'
                    "title": 'Mr'
                }
            ]
}
 */
export class StayingGuest {
  [key: string]: unknown;
  firstName?: string;
  lastName?: string;
  title?: string;

  /**
   * StayingGuests constructor
   * @param data object data
   * @param data.stayingGuest stayingGuest info
   */
  constructor(data: { stayingGuest?: Record<string, unknown> } = {}) {
    const stayingGuest = data.stayingGuest ?? {};
    this.firstName = stayingGuest.firstName as string | undefined;
    this.lastName = stayingGuest.lastName as string | undefined;
    this.title = stayingGuest.title as string | undefined;
  }

  static fromResponse(data: { stayingGuest?: Record<string, unknown> }): StayingGuest {
    return new StayingGuest(data);
  }
}
