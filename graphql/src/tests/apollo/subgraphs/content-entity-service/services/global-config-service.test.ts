jest.mock('../../../../../apollo/client/rest-client');
jest.mock('../../../../../apollo/exception/error-handler', () => ({
  handleError: jest.fn()
}));

jest.mock('../../../../../apollo/pipeline/manager/PipelineManager', () => {
  return {
    PipelineManager: jest.fn().mockImplementation(() => ({
      manage: jest.fn()
    }))
  };
});

jest.mock(
  '../../../../../apollo/subgraphs/booking-confirmation-pipeline/actions/BookingInformationByBasketPipelineAction',
  () => ({
    BookingInformationByBasketPipelineAction: jest.fn().mockImplementation(() => ({}))
  })
);

jest.mock(
  '../../../../../apollo/subgraphs/booking-confirmation-pipeline/actions/ActionContextKeys',
  () => ({
    ActionContextKeys: {
      BOOKING_INFORMATION_BY_BASKET: 'BOOKING_INFORMATION_BY_BASKET'
    }
  })
);

jest.mock('../../../../../apollo/utils/base-utils', () => ({
  addFieldIfNotUndefined: jest.fn((value: any, key: string, map: any) => {
    if (typeof value !== 'undefined') map[key] = value;
  }),
  addFieldsToMap: jest.fn((fields: any[], map: any) => {
    fields.forEach((f) => {
      if (f.required || typeof f.value !== 'undefined') map[f.key] = f.value;
    });
  }),
  getTodayIsoDate: jest.fn(() => '2025-11-27T00:00:00.000Z')
}));

jest.mock('../../../../../apollo/subgraphs/content-entity-service/services/base-service', () => ({
  endpoints: {
    GLOBAL_CONFIG: '/mock/global',
    PROMO_CONFIG: '/mock/promo'
  }
}));

import { get } from '../../../../../apollo/client/rest-client';
import { handleError } from '../../../../../apollo/exception/error-handler';
import { PipelineManager } from '../../../../../apollo/pipeline/manager/PipelineManager';
import * as baseUtils from '../../../../../apollo/utils/base-utils';
import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import {
  globalConfig,
  promotionsInformation
} from '../../../../../apollo/subgraphs/content-entity-service/services/global-config-service';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-confirmation-pipeline/actions/ActionContextKeys';

const mockedGet = get as jest.MockedFunction<any>;
const mockedHandleError = handleError as jest.MockedFunction<any>;
const MockedPipelineManager = PipelineManager as unknown as jest.MockedClass<any>;

describe('global-config-service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('globalConfig', () => {
    it('calls get with endpoint and fields added by addFieldIfNotUndefined', async () => {
      mockedGet.mockResolvedValueOnce({ ok: true });

      const args = {
        channel: 'PI',
        brand: 'pi',
        country: 'gb',
        language: 'en'
      };
      const context = { traceId: 't-1' };

      const result = await globalConfig(args, context);

      expect((baseUtils as any).addFieldIfNotUndefined).toHaveBeenCalledTimes(4);

      expect(mockedGet).toHaveBeenCalledWith(
        endpoints.GLOBAL_CONFIG,
        expect.anything(),
        {
          channelId: 'PI',
          brand: 'pi',
          country: 'gb',
          language: 'en'
        },
        context
      );

      expect(result).toEqual({ ok: true });
    });

    it('calls handleError when get rejects', async () => {
      const err = new Error('network');
      mockedGet.mockRejectedValueOnce(err);

      const args = {
        channel: undefined,
        brand: undefined,
        country: undefined,
        language: undefined
      };
      const context = {};

      const result = await globalConfig(args, context);

      expect(mockedHandleError).toHaveBeenCalledWith(err, {});
      expect(result).toBeUndefined();
    });
  });

  describe('promotionsInformation', () => {
    it('fetches promo config when basketReference present and opera promotion code exists', async () => {
      const pipelineInstance = new PipelineManager() as any;
      pipelineInstance.manage = jest.fn().mockResolvedValue({
        get: (key: string) =>
          key === ActionContextKeys.BOOKING_INFORMATION_BY_BASKET
            ? {
                promotionCode: 'OPERA_PROMO',
                promoKind: 'SITE_WIDE',
                reservationByIdList: [
                  {
                    roomStay: {
                      ratePlanCode: 'RATE123',
                      promotionCode: 'OPERA_PROMO'
                    }
                  }
                ]
              }
            : undefined
      });
      (PipelineManager as unknown as jest.Mock).mockImplementation(() => pipelineInstance);
      mockedGet.mockResolvedValueOnce({ promo: 'ok' });

      const args = {
        promotionsInformationCriteria: {
          basketReference: 'BASKETREF',
          country: 'gb',
          language: 'en',
          channel: 'PI',
          brand: 'pi',
          stayStartDate: '2025-12-01',
          stayEndDate: '2025-12-03'
        }
      };
      const context = { traceId: 't-2' };

      const result = await promotionsInformation(args, context);
      expect(mockedGet).toHaveBeenCalledWith(
        endpoints.PROMO_CONFIG,
        'promotionsInformation',
        expect.objectContaining({
          country: 'gb',
          language: 'en',
          channelId: 'PI',
          brand: 'pi',
          promotionCode: 'OPERA_PROMO',
          bookingDate: expect.any(String),
          stayStartDate: '2025-12-01',
          stayEndDate: '2025-12-03',
          promoKind: 'SITE_WIDE'
        }),
        context
      );

      expect(result).toEqual({
        promo: 'ok',
        promoBookingInfo: {
          promotionCode: 'OPERA_PROMO',
          ratePlanCode: 'RATE123'
        }
      });
    });

    it('does NOT fetch promo config when pipeline returns no opera promotion code (shouldFetchPromoConfig=false)', async () => {
      const pipelineInstance = new PipelineManager() as any;
      pipelineInstance.manage = jest.fn().mockResolvedValue({
        get: (key: string) =>
          key === ActionContextKeys.BOOKING_INFORMATION_BY_BASKET
            ? {
                promotionCode: 'BASKET_PROMO',
                promoKind: 'SITE_WIDE',
                reservationByIdList: [
                  {
                    roomStay: {
                      ratePlanCode: 'RATE_CODE',
                      promotionCode: undefined
                    }
                  }
                ]
              }
            : undefined
      });
      (PipelineManager as unknown as jest.Mock).mockImplementation(() => pipelineInstance);

      mockedGet.mockClear();

      const args = {
        promotionsInformationCriteria: {
          basketReference: 'BASKET_REF',
          country: 'gb',
          language: 'en',
          channel: 'PI',
          brand: 'pi',
          stayStartDate: '2025-12-05',
          stayEndDate: '2025-12-06'
        }
      };
      const context = {};

      const result = await promotionsInformation(args, context);

      expect(mockedGet).not.toHaveBeenCalled();
      expect(result).toEqual({
        ...{},
        promoBookingInfo: {
          promotionCode: 'BASKET_PROMO',
          ratePlanCode: 'RATE_CODE'
        }
      });
    });

    it('calls handleError when pipeline.manage rejects', async () => {
      const pipelineInstance = new PipelineManager() as any;
      pipelineInstance.manage = jest.fn().mockRejectedValue(new Error('pipeline fail'));
      (PipelineManager as unknown as jest.Mock).mockImplementation(() => pipelineInstance);

      const args = {
        promotionsInformationCriteria: {
          basketReference: 'BR3'
        }
      };
      const context = {};

      const result = await promotionsInformation(args, context);

      expect(mockedHandleError).toHaveBeenCalled();
      expect(result).toBeUndefined();
    });
  });
});
