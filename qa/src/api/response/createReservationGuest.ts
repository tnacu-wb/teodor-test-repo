/**
The create reservation guest from API response example: 
{
    "data": {
        "createReservationGuest": {
            "basketReference": "EDIPAR1584454"
        }
    }
}
 */
export class CreateReservationGuest {
  [key: string]: unknown;
  basketReference?: string;

  /**
   * CreateReservationGuest constructor
   * @param data object data
   * @param data.createReservationGuest createReservationGuest
   */
  constructor(data: { createReservationGuest?: Record<string, unknown> } = {}) {
    const createReservationGuest = data.createReservationGuest ?? {};
    this.basketReference = createReservationGuest.basketReference as string | undefined;
  }

  static fromResponse(data: { createReservationGuest?: Record<string, unknown> }): CreateReservationGuest {
    return new CreateReservationGuest(data);
  }

  /**
   * Validate actual basket reference against an expected one
   * @param basketReference basket reference
   */
  async validateBasketReference(basketReference?: string): Promise<void> {
    if (this.basketReference !== basketReference) {
      throw new Error('Basket reference not expected.');
    }
  }

}
