import { HIBasketData } from '@whitbread-eos/api';

import { getMaxValueFromHDPRoomTypes, getNightsNumber } from '../getters';
import { formatBasicBasketDetails } from './formatBasicBasketDetails';

jest.mock('../getters', () => ({
  getMaxValueFromHDPRoomTypes: jest.fn(),
  getNightsNumber: jest.fn(),
}));

describe('formatBasicBasketDetails', () => {
  const mockBasket: HIBasketData = {
    hotelId: 'hotel123',
    arrival: '2023-10-01',
    departure: '2023-10-05',
    selectedRate: {
      ratePlanCode: 'rate123',
      rateCategory: 'category123',
      roomTypes: [],
    },
    bookingFlow: {
      bookingFlowItems: [{ rateCategory: 'category123', bookingId: 'booking123' }],
    },
  };

  const mockReservationId = 'reservation123';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should format basket details correctly', () => {
    (getMaxValueFromHDPRoomTypes as jest.Mock).mockReturnValueOnce(2).mockReturnValueOnce(1);
    (getNightsNumber as jest.Mock).mockReturnValue(4);

    const result = formatBasicBasketDetails(mockBasket, mockReservationId);

    expect(result).toEqual({
      hotelId: 'hotel123',
      startDate: '2023-10-01',
      endDate: '2023-10-05',
      nightsNumber: 4,
      rateCode: 'rate123',
      adultsNumber: 2,
      childrenNumber: 1,
      reservationId: 'reservation123',
      bookingFlowId: 'booking123',
    });

    expect(getMaxValueFromHDPRoomTypes).toHaveBeenCalledWith(
      mockBasket.selectedRate.roomTypes,
      'adults'
    );
    expect(getMaxValueFromHDPRoomTypes).toHaveBeenCalledWith(
      mockBasket.selectedRate.roomTypes,
      'children'
    );
    expect(getNightsNumber).toHaveBeenCalledWith('2023-10-01', '2023-10-05');
  });

  it('should override bookingFlowId when isBB is true', () => {
    const result = formatBasicBasketDetails(mockBasket, mockReservationId, true);

    expect(result.bookingFlowId).toBe('booking-business');
  });

  it('should handle missing bookingFlowItems gracefully', () => {
    const basketWithoutBookingFlowItems = {
      ...mockBasket,
      bookingFlow: { bookingFlowItems: [] },
    };

    const result = formatBasicBasketDetails(basketWithoutBookingFlowItems, mockReservationId);

    expect(result.bookingFlowId).toBe('');
  });
});
