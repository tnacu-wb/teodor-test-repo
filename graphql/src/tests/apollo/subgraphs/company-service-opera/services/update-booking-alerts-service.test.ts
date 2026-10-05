import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateBookingAlerts } from '../../../../../apollo/subgraphs/company-service-opera/services/update-booking-alerts-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateBookingAlerts', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_1234567';
  const bookingAlerts = {
    dayOfArrival: true,
    weekendArrival: false,
    passThroughWeekend: true,
    rateCaps: {
      uKWide: {
        amount: 100,
        currency: 'GBP'
      },
      greaterLondon: {
        amount: 50,
        currency: 'GBP'
      },
      ireland: {
        amount: 800,
        currency: 'GBP'
      }
    },
    frequency: 2,
    bookingAlertHotels: ['LONARC'],
    recipientEmailAddresses: ['gab.sim@mailinator.com']
  };

  it('should call the put function with correct parameters when updating booking alerts', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateBookingAlerts({ companyId, bookingAlerts }, context);

    const serviceEndpoint = {
      ...endpoints.UPDATE_BOOKING_ALERTS,
      endpoint: '/companies/admin/COMP_1234567/booking-alerts'
    };

    expect(put).toHaveBeenCalledWith(serviceEndpoint, updateBookingAlerts, bookingAlerts, context);
  });

  it('should return the response data when updating booking alerts', async () => {
    const data = 'response data';
    (put as jest.Mock).mockReturnValue({ data });
    const response = await updateBookingAlerts({ companyId, bookingAlerts }, context);

    expect(response).toBe(data);
  });

  it('should handle errors gracefully when updating booking alerts fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(updateBookingAlerts({ companyId, bookingAlerts }, context)).rejects.toThrow(
      new Error(
        '{"message":"Test error","errors":[{"field":"Error","message":"Test error"}],"errorType":404}'
      )
    );
  });
});
