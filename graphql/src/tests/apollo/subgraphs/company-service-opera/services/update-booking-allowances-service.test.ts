import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateBookingAllowances } from '../../../../../apollo/subgraphs/company-service-opera/services/update-booking-allowances-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateBookingAllowances', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_1234567';
  const bookingAllowances = {
    maxDinnerBudgets: {
      uKWide: {
        amount: 20,
        currency: 'GBP'
      },
      greaterLondon: {
        amount: 0,
        currency: 'GBP'
      },
      ireland: {
        amount: 15,
        currency: 'GBP'
      }
    },
    extrasCodes: ['1', '2', '3', '4'],
    upsellItemsAllowed: ['11', '15', '12', '17', '5', '18'],
    allowAlcohol: true,
    allowCarParking: true,
    allowAdditionalCosts: true,
    allowPremierSaverRates: false,
    allowIndividualCards: true,
    maxNumberOfNights: 10
  };

  it('should call the put function with correct parameters when updating booking allowances', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateBookingAllowances({ companyId, bookingAllowances }, context);
    const updateBookingAllowancesEndpoint = endpoints.UPDATE_BOOKING_ALLOWANCES.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.UPDATE_BOOKING_ALLOWANCES,
      endpoint: updateBookingAllowancesEndpoint
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateBookingAllowances,
      bookingAllowances,
      context
    );
  });

  it('should handle errors gracefully when updating booking allowances fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateBookingAllowances({ companyId, bookingAllowances }, context)
    ).rejects.toThrow('Test error');
  });
});
