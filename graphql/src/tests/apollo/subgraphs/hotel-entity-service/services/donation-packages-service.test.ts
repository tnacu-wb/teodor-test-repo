import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { fetchDonationPackagesOpera } from '../../../../../apollo/subgraphs/hotel-entity-service/services/donation-packages-service';
import { get } from '../../../../../apollo/client/rest-client';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/donations-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('fetchDonationPackagesOpera', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();
  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.BOOKING_INFORMATION) {
      return {
        donation: {
          charityCodes: ['C1', 'C2', 'C3']
        }
      };
    } else if (key === ActionContextKeys.HOTEL_INFORMATION) {
      return {
        hotelId: 'HOTEL_1'
      };
    }
  });

  it('should call the get function with correct parameters when fetching donation packages', async () => {
    await fetchDonationPackagesOpera({}, context, pipelineContext);

    const serviceEndpoint = {
      ...endpoints.DONATION_PACKAGES_OPERA,
      endpoint: '/v1/hotels/HOTEL_1/packages/donations'
    };
    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      fetchDonationPackagesOpera,
      {
        packageCodes: 'C1,C2,C3'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching donation packages', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchDonationPackagesOpera({}, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });

  it('should return nothing when donation charity codes are not provided', async () => {
    jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
      if (key === ActionContextKeys.BOOKING_INFORMATION) {
        return {
          donation: {
            charityCodes: []
          }
        };
      } else if (key === ActionContextKeys.HOTEL_INFORMATION) {
        return {
          hotelId: 'HOTEL_1'
        };
      }
    });

    await expect(fetchDonationPackagesOpera({}, context, pipelineContext)).resolves.toBeUndefined();
  });
});
