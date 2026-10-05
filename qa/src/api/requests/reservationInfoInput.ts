/**
 * Defines the reservations input for a room
 */
export class ReservationInfoInput {
  [key: string]: unknown;
  adultsNumber?: number;
  arrival?: string;
  childrenNumber?: number;
  cotRequired?: boolean;
  departure?: string;
  hotelId?: string;
  roomRates?: { pmsRoomType?: string; startDate?: string; endDate?: string; ratePlanCode?: string };

  /**
   * Reservation Info Input constructor
   * @param hotelId - hotelId
   * @param arrival - booking start date
   * @param departure - booking end date
   * @param adultsNumber - number of adults
   * @param childrenNumber - number of children
   * @param cotRequired  - cot required
   * @param rooms - booking room rates
   */
  constructor(
    hotelId?: string,
    arrival?: string,
    departure?: string,
    adultsNumber?: number,
    childrenNumber?: number,
    cotRequired?: boolean,
    rooms?: ReservationInfoInput['roomRates'],
  ) {
    this.hotelId = hotelId;
    this.arrival = arrival;
    this.departure = departure;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.cotRequired = cotRequired;
    this.roomRates = rooms;
  }

  static fromRequest(data: {
    hotelId?: string;
    arrival?: string;
    departure?: string;
    adultsNumber?: number;
    childrenNumber?: number;
    cotRequired?: boolean;
    roomRates?: ReservationInfoInput['roomRates'];
  }): ReservationInfoInput {
    return new ReservationInfoInput(
      data.hotelId,
      data.arrival,
      data.departure,
      data.adultsNumber,
      data.childrenNumber,
      data.cotRequired,
      data.roomRates,
    );
  }

  /**
   * Create a custom reservation info input with parameters
   * @param data object data
   * @param data.hotel - hotelId
   * @param data.startDate - booking start date
   * @param data.endDate - booking end date
   * @param data.roomType - booking room type
   * @param data.rate - rate plan
   * @param data.adultsNumber - number of adults
   * @param data.childrenNumber - number of children
   * @param data.cotRequired  - cot required
   */
  static async createCustomReservationInfoInput(data: {
    hotel?: string;
    startDate?: string;
    endDate?: string;
    roomType?: string;
    rate?: string;
    adultsNumber?: number;
    childrenNumber?: number;
    cotRequired?: boolean;
  }): Promise<ReservationInfoInput[]> {
    const { hotel, startDate, endDate, roomType, rate, adultsNumber = 2, childrenNumber = 0, cotRequired = false } = data;
    const rooms = {
      pmsRoomType: roomType,
      startDate,
      endDate,
      ratePlanCode: rate,
    };
    return [new ReservationInfoInput(hotel, startDate, endDate, adultsNumber, childrenNumber, cotRequired, rooms)];
  }

}
