import { describe, it, expect, jest, beforeEach } from '@jest/globals';

import {
  buildBatchEligibilities,
  defaultPlatformConfig,
  getPromoErrorMessage,
  onSubmitCreatePromotionForm,
  PlatformConfig,
  PromoFormDetails,
} from './common';

jest.mock('@whitbread-eos/utils', () => ({
  formatDate: jest.fn((date: string) => date),
}));

describe('onSubmitCreatePromotionForm', () => {
  const mockMutate = jest.fn();

  const defaultData: PromoFormDetails = {
    campaignName: 'TestCampaign',
    operaPromoCode: 'PROMO1234',
    isGeneric: false,
    prefix: 'PRE',
    batchCount: 10,
    isLimitRedemptions: false,
    maxRedemptionLimit: undefined,
    expiryDate: '2026-01-01',
    codeLength: 0,
    notes: '',
    hotelId: 'HEAPTI',
    requestedBy: '',
    platformConfig: defaultPlatformConfig,
  };

  const mockEvent = { preventDefault: jest.fn() };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls mutate with formatted expiryDate and prefix calculation when isGeneric is false', async () => {
    await onSubmitCreatePromotionForm({
      data: defaultData,
      event: mockEvent as any,
      userEmail: 'test@example.com',
      createPromoMutation: { mutate: mockMutate },
    });

    expect(mockEvent.preventDefault).toHaveBeenCalled();
    expect(mockMutate).toHaveBeenCalledWith({
      campaignName: 'TestCampaign',
      operaPromoCode: 'PROMO1234',
      expiryDate: '2026-01-01',
      codeLength: 13,
      notes: 'notes',
      hotelId: 'HEAPTI',
      requestedBy: 'test@example.com',
      isMultiple: false,
      prefix: 'PRE',
      batchCount: 10,
      maxRedemptionLimit: null,
      batchEligibilities: [],
    });
  });

  it('calculates generic promo code length and defaults batchCount to 1 when isGeneric and isLimitRedemptions are true but batchCount is falsy', async () => {
    const genericData: PromoFormDetails = {
      ...defaultData,
      isGeneric: true,
      genericPromoCode: 'GENERIC123',
      isLimitRedemptions: true,
      maxRedemptionLimit: 50,
      batchCount: undefined as any,
    };

    await onSubmitCreatePromotionForm({
      data: genericData,
      event: mockEvent as any,
      userEmail: 'admin@example.com',
      createPromoMutation: { mutate: mockMutate },
    });

    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({
        isMultiple: true,
        prefix: 'GENERIC123',
        codeLength: 20,
        batchCount: 1,
        maxRedemptionLimit: 50,
      })
    );
  });

  it('handles empty identifier and sets prefixAndCodeLength to 10 when neither prefix nor genericPromoCode are present', async () => {
    const dataNoIdentifier: PromoFormDetails = {
      ...defaultData,
      prefix: '',
      isGeneric: false,
    };

    await onSubmitCreatePromotionForm({
      data: dataNoIdentifier,
      event: mockEvent as any,
      createPromoMutation: { mutate: mockMutate },
    });

    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({
        codeLength: 10,
      })
    );
  });

  it('calls mutate with empty string if expiryDate is missing', async () => {
    const dataNoExpiry: PromoFormDetails = { ...defaultData, expiryDate: undefined };

    await onSubmitCreatePromotionForm({
      data: dataNoExpiry,
      event: mockEvent as any,
      userEmail: 'test@example.com',
      createPromoMutation: { mutate: mockMutate },
    });

    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({
        expiryDate: '',
      })
    );
  });

  it('defaults requestedBy to empty string if no userEmail provided', async () => {
    await onSubmitCreatePromotionForm({
      data: defaultData,
      event: mockEvent as any,
      createPromoMutation: { mutate: mockMutate },
    });

    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({
        requestedBy: '',
      })
    );
  });
});

describe('buildBatchEligibilities', () => {
  it('returns an empty array when platformConfig is undefined', () => {
    expect(buildBatchEligibilities(undefined)).toEqual([]);
  });

  it('skips countries that are disabled', () => {
    const config: PlatformConfig = {
      ...defaultPlatformConfig,
      GB: { ...defaultPlatformConfig.GB, enabled: false },
    };

    expect(buildBatchEligibilities(config)).toEqual([]);
  });

  it('skips platforms that are disabled or have no options selected', () => {
    const config: PlatformConfig = {
      ...defaultPlatformConfig,
      GB: {
        enabled: true,
        platforms: {
          ...defaultPlatformConfig.GB.platforms,
          PI: { enabled: false, selected: { web: true, app: true } },
          CCUI: { enabled: true, selected: { web: false, app: false } },
        },
      },
    };

    expect(buildBatchEligibilities(config)).toEqual([]);
  });

  it('correctly maps platforms and channels for enabled selections', () => {
    const config: PlatformConfig = {
      ...defaultPlatformConfig,
      GB: {
        enabled: true,
        platforms: {
          PI: { enabled: true, selected: { web: true, app: true } },
          CCUI: { enabled: true, selected: { web: true, app: false } },
          PREMIER_INN_BUSINESS: { enabled: true, selected: { web: false, app: true } },
        },
      },
      DE: {
        enabled: true,
        platforms: {
          ...defaultPlatformConfig.DE.platforms,
          PI: { enabled: true, selected: { web: true, app: false } },
        },
      },
    };

    const result = buildBatchEligibilities(config);

    expect(result).toEqual([
      { region: 'GB', channel: 'PI', platforms: ['WEB', 'MOBILE'] },
      { region: 'GB', channel: 'CCUI', platforms: ['WEB'] },
      { region: 'GB', channel: 'BB', platforms: ['MOBILE'] },
      { region: 'DE', channel: 'PI', platforms: ['WEB'] },
    ]);
  });
});

describe('getPromoErrorMessage', () => {
  it('returns errCode when message is valid JSON with errCode', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ errCode: 1010 }) }],
      },
    };
    expect(getPromoErrorMessage(error)).toEqual(1010);
  });

  it('returns null when message is valid JSON but has no errCode', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ message: 'Error without errCode' }) }],
      },
    };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when JSON parsing fails (non-JSON error string)', () => {
    const error = {
      response: {
        errors: [{ message: 'Server Internal Error' }],
      },
    };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when error structure is missing or malformed', () => {
    expect(getPromoErrorMessage(null)).toBeNull();
    expect(getPromoErrorMessage(undefined)).toBeNull();
    expect(getPromoErrorMessage({})).toBeNull();
    expect(getPromoErrorMessage({ response: null })).toBeNull();
    expect(getPromoErrorMessage({ response: { errors: [] } })).toBeNull();
    expect(getPromoErrorMessage({ response: { errors: [{ message: null }] } })).toBeNull();
  });
});
