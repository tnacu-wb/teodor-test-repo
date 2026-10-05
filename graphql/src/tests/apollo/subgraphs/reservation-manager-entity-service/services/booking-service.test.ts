import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { cancelBooking } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/booking-service';
import { CancelBookingRequest } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/models/cancel-booking-request';

jest.mock('../../../../../apollo/client/rest-client');

describe('cancelBooking', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const cancelBookingRequest: CancelBookingRequest = {
    arrivalDate: '2025-02-26',
    basketReference: 'AJK-c875714e-0c67-4836-bb48-b9201ecf1553',
    bookingChannel: {
      channel: 'BB',
      subchannel: 'WEB',
      language: 'en'
    },
    country: '',
    hotelId: 'GATGAT',
    bookingReference: 'AJK2932035',
    paymentOption: 'PAY_ON_ARRIVAL',
    sourceSystem: 'OPERA',
    token: ''
  };

  it('should call the post function with correct parameters when cancelBooking is called', async () => {
    await cancelBooking({ cancelBookingRequest }, context);
    const endpointWithParams = `${endpoints.CANCEL_BOOKING.endpoint}?channel=BB&subchannel=WEB`;
    const serviceEndpoint = { ...endpoints.CANCEL_BOOKING, endpoint: endpointWithParams };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      cancelBooking,
      cancelBookingRequest,
      context
    );
  });

  it('should handle errors gracefully when cancelBooking is called', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(cancelBooking({ cancelBookingRequest }, context)).rejects.toThrow('Test error');
  });
});
