import { describe, it, expect } from '@jest/globals';

import validateFormCreatePromotions from './formValidationCreatePromotion';

const t = (key: string) => key;
const { formValidationSchemaCreatePromotions } = validateFormCreatePromotions({ t } as any);

describe('formValidationSchemaCreatePromotions', () => {
  it('should return endDateInvalidError when expiryDate is null', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('expiryDate', {
        expiryDate: null,
      })
    ).rejects.toThrow('endDateInvalidError');
  });

  it('should reject empty expiryDate', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('expiryDate', {
        expiryDate: '',
      })
    ).rejects.toThrow('endDateInvalidError');
  });

  it('should reject past expiryDate', async () => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);

    await expect(
      formValidationSchemaCreatePromotions.validateAt('expiryDate', {
        expiryDate: yesterday,
      })
    ).rejects.toThrow('endDateInvalidError');
  });

  it('should allow empty prefix when isGeneric is true', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('prefix', {
        isGeneric: true,
        prefix: '',
      })
    ).resolves.toBe('');
  });

  it('should require prefix when isGeneric is false', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('prefix', {
        isGeneric: false,
        prefix: '',
      })
    ).rejects.toThrow('voucherPrefixRequiredError');
  });

  it('should reject invalid prefix', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('prefix', {
        isGeneric: false,
        prefix: '@@@',
      })
    ).rejects.toThrow('voucherPrefixValidationError');
  });

  it('should reject prefix longer than 20 characters', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('prefix', {
        isGeneric: false,
        prefix: 'ABCDEFGHIJKLMNOPQRSTU',
      })
    ).rejects.toThrow('voucherPrefixValidationError');
  });

  it('should require genericPromoCode', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('genericPromoCode', {
        isGeneric: true,
        genericPromoCode: '',
      })
    ).rejects.toThrow('promoCodeRequiredError');
  });

  it('should require hotelId', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('hotelId', {
        hotelId: '',
      })
    ).rejects.toThrow('hotelIdRequiredError');
  });

  it('should not require genericPromoCode when isGeneric is false', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('genericPromoCode', {
        isGeneric: false,
      })
    ).resolves.toBeUndefined();
  });

  it('should transform empty batchCount to undefined', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('batchCount', {
        isLimitRedemptions: false,
        batchCount: '',
      })
    ).rejects.toThrow('vouchersCountRequiredError');
  });

  it('should not require batchCount when isGeneric is true', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('batchCount', {
        isGeneric: true,
      })
    ).resolves.toBeUndefined();
  });

  it('should reject negative batchCount', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('batchCount', {
        isLimitRedemptions: false,
        batchCount: -1,
      })
    ).rejects.toThrow('vouchersCountInvalidError');
  });

  it('should reject decimal batchCount', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('batchCount', {
        isLimitRedemptions: false,
        batchCount: 2.5,
      })
    ).rejects.toThrow('vouchersCountInvalidError');
  });

  it('should reject batchCount greater than max', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('batchCount', {
        isLimitRedemptions: false,
        batchCount: 1000001,
      })
    ).rejects.toThrow('vouchersCountInvalidError');
  });

  it('should allow maxRedemptionLimit when limitRedemptions is false', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('maxRedemptionLimit', {
        isLimitRedemptions: false,
      })
    ).resolves.toBeUndefined();
  });

  it('should reject maxRedemptionLimit below minimum', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('maxRedemptionLimit', {
        isGeneric: true,
        isLimitRedemptions: true,
        maxRedemptionLimit: 0,
      })
    ).rejects.toThrow('genericPromoInvalidError');
  });

  it('should reject maxRedemptionLimit above max', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('maxRedemptionLimit', {
        isGeneric: true,
        isLimitRedemptions: true,
        maxRedemptionLimit: 1000001,
      })
    ).rejects.toThrow('genericPromoInvalidError');
  });

  it('should reject undefined platformConfig', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('platformConfig', {
        platformConfig: undefined,
      })
    ).rejects.toThrow();
  });

  it('should reject when no country is enabled', async () => {
    await expect(
      formValidationSchemaCreatePromotions.validateAt('platformConfig', {
        platformConfig: {
          GB: { enabled: false },
          DE: { enabled: false },
        },
      })
    ).rejects.toThrow();
  });
});
