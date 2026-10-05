import { endpoints } from '../../../../../apollo/subgraphs/hotel-dashboard-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getHotelDashboard } from '../../../../../apollo/subgraphs/hotel-dashboard-service-opera/services/get-hotel-dashboard';

jest.mock('../../../../../apollo/client/rest-client');

describe('globalConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should call the get function with correct parameters', async () => {
    const mockArgs = {
      customerId: 'customer123',
      dashboardRequest: {
        business: 'business1',
        surname: 'Doe',
        confirmationNumber: 'ABC123',
        language: 'en',
        arrivalDate: '2025-03-01',
        employeeId: 'emp456'
      },
      hasRecentSearches: true
    };

    const finalMap = {
      business: 'business1',
      customerId: 'customer123',
      hasRecentSearches: true,
      surname: 'Doe',
      confirmationNumber: 'ABC123',
      language: 'en',
      arrivalDate: '2025-03-01',
      employeeId: 'emp456'
    };

    const serviceEndpoint = {
      ...endpoints.GET_HOTEL_DASHBOARD,
      endpoint: expect.stringContaining('/dashboard')
    };

    await getHotelDashboard(mockArgs, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getHotelDashboard, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const dashboardRequest = {
      business: 'business',
      surname: 'surname',
      confirmationNumber: 123,
      language: 'en',
      arrivalDate: '2025-10-10',
      employeeId: 'id'
    };

    const args = {
      customerId: 'id',
      hasRecentSearches: false,
      dashboardRequest
    };

    await expect(getHotelDashboard(args, context)).rejects.toThrow('Test error');
  });
});
