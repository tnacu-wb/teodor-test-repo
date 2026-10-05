export type HotelAvailabilityApiResponse = {
  hotelId: string;
  startDate: string;
  endDate: string;
  available: boolean;
  roomRates: RoomRate[];
};

export type RoomRate = {
  ratePlanCode: string;
  roomTypes: RoomType[];
};

export type RoomType = {
  roomType: string;
  adults: number;
  children: number;
  rooms: Rooms[];
};

export type Rooms = {
  pmsRoomType: string;
  silentSubstitution: boolean;
  roomPriceBreakdown: RoomPriceBreakdown;
};

export type RoomPriceBreakdown = {
  effectiveRateAmount: number;
  totalCityTaxAmount: number;
  totalNetAmount: number;
  currencyCode: string;
  dailyPrices: DailyPrice[];
};

export type DailyPrice = {
  date: string;
  netPrice: number;
  effectiveRate: number;
};

function createDailyPrice(dailyPrice: any): DailyPrice {
  return {
    date: dailyPrice.date,
    netPrice: dailyPrice.netPrice,
    effectiveRate: dailyPrice.effectiveRate,
  };
}

function createRoomPriceBreakdown(roomPriceBreakdownApiResponse: any): RoomPriceBreakdown {
  return {
    effectiveRateAmount: roomPriceBreakdownApiResponse.effectiveRateAmount,
    totalCityTaxAmount: roomPriceBreakdownApiResponse.totalCityTaxAmount,
    totalNetAmount: roomPriceBreakdownApiResponse.totalNetAmount,
    currencyCode: roomPriceBreakdownApiResponse.currencyCode,
    dailyPrices: roomPriceBreakdownApiResponse.dailyPrices.map((dailyPrices: DailyPrice) =>
      createDailyPrice(dailyPrices)
    ),
  };
}

function createRoomApiResponse(roomApiResponse: any): Rooms {
  return {
    pmsRoomType: roomApiResponse.pmsRoomType,
    silentSubstitution: roomApiResponse.silentSubstitution,
    roomPriceBreakdown: createRoomPriceBreakdown(roomApiResponse.roomPriceBreakdown),
  };
}

function createRoomTypeApiResponse(roomTypeApiResponse: any): RoomType {
  return {
    roomType: roomTypeApiResponse.roomType,
    adults: roomTypeApiResponse.adults,
    children: roomTypeApiResponse.children,
    rooms: roomTypeApiResponse.rooms.map((room: Rooms) => createRoomApiResponse(room)),
  };
}

function createRoomRateApiResponse(roomRateListApiResponse: any): RoomRate {
  return {
    ratePlanCode: roomRateListApiResponse.ratePlanCode,
    roomTypes: roomRateListApiResponse.roomTypes.map((roomType: RoomType) =>
      createRoomTypeApiResponse(roomType)
    ),
  };
}

export function createHotelAvailability(
  hotelAvailabilityResponse: any
): HotelAvailabilityApiResponse {
  return {
    hotelId: hotelAvailabilityResponse.hotelId,
    startDate: hotelAvailabilityResponse.startDate,
    endDate: hotelAvailabilityResponse.endDate,
    available: hotelAvailabilityResponse.available,
    roomRates: hotelAvailabilityResponse.roomRates.map((roomRate: RoomRate) =>
      createRoomRateApiResponse(roomRate)
    ),
  };
}
