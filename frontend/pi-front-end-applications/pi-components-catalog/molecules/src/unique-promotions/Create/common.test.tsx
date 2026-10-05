import { describe, it, expect, beforeEach, jest } from '@jest/globals';

import {
  todayLabel,
  tomorrowLabel,
  getCreatePromotionFormFields,
  fieldProps,
  getPromoErrorMessage,
  handlePromoApiError,
  getPromoCodeErrorMessage,
} from './common';

describe('common utils', () => {
  it('exports todayLabel and tomorrowLabel correctly', () => {
    expect(todayLabel).toBe('Today');
    expect(tomorrowLabel).toBe('Tomorrow');
  });

  it('returns correct form field configuration', () => {
    const t = {
      campaignNameLabel: 'Campaign Name',
      promoCodeFormLabel: 'Promo Code',
      hotelIdLabel: 'Hotel ID',
      isGenericLabel: 'Is Generic',
      voucherPrefixTitle: 'Prefix',
      genericPromoCodeLabel: 'Generic PromoCode*',
      endDateLabel: 'End Date',
      limitRedemptionsLabel: 'Limit Redemptions',
      maximumRedemptionsLabel: 'Maximum Redemptions',
      vouchersNeededLabel: 'Vouchers Needed',
      platformConfigurationTitle: 'Platform Configuration',
      requestedByTitle: 'Requested By',
    };

    const fields = getCreatePromotionFormFields(t);

    expect(fields).toHaveLength(12);

    expect(fields[0]).toEqual({
      type: 'text',
      name: 'campaignName',
      label: 'Campaign Name*',
      testId: 'campaignName',
    });

    expect(fields[1]).toEqual({
      type: 'text',
      name: 'operaPromoCode',
      label: 'Promo Code*',
      testId: 'operaPromoCode',
    });

    expect(fields[2]).toEqual({
      type: 'autoComplete',
      name: 'hotelId',
      label: 'Hotel ID*',
      testId: 'hotelId',
    });

    expect(fields[3]).toEqual({
      type: 'check',
      name: 'isGeneric',
      label: 'Is Generic',
      testId: 'isGeneric',
    });

    expect(fields[4]).toEqual({
      type: 'text',
      name: 'prefix',
      label: 'Prefix*',
      testId: 'prefix',
    });

    expect(fields[5]).toEqual({
      type: 'text',
      name: 'genericPromoCode',
      label: 'Generic PromoCode*',
      testId: 'genericPromoCode',
    });

    expect(fields[6]).toEqual({
      type: 'datePicker',
      name: 'expiryDate',
      label: 'End Date*',
      testId: 'expiryDate',
    });

    expect(fields[7]).toEqual({
      type: 'check',
      name: 'isLimitRedemptions',
      label: 'Limit Redemptions',
      testId: 'isLimitRedemptions',
    });

    expect(fields[8]).toEqual({
      type: 'number',
      name: 'maxRedemptionLimit',
      label: 'Maximum Redemptions',
      testId: 'maxRedemptionLimit',
      min: 1,
      max: 1000000,
    });

    expect(fields[9]).toEqual({
      type: 'number',
      name: 'batchCount',
      label: 'Vouchers Needed*',
      testId: 'batchCount',
    });

    expect(fields[10]).toEqual({
      type: 'platformConfig',
      name: 'platformConfig',
      label: 'Platform Configuration*',
      testId: 'platformConfig',
    });

    expect(fields[11]).toEqual({
      type: 'text',
      name: 'requestedBy',
      label: 'Requested By',
      testId: 'requestedBy',
      isDisabled: true,
    });
  });

  it('maps react-hook-form field props correctly', () => {
    const onChange = jest.fn();
    const onBlur = jest.fn();

    const field = {
      name: 'testField',
      value: 'abc',
      onChange,
      onBlur,
    };

    const props = fieldProps(field);

    expect(props).toEqual({
      name: 'testField',
      value: 'abc',
      onBlur,
      onChange: expect.any(Function),
    });

    props.onChange('new-value');
    expect(onChange).toHaveBeenCalledWith('new-value');
  });
});

describe('getPromoErrorMessage', () => {
  it('returns errCode when message is valid JSON with errCode', () => {
    const error = {
      response: {
        errors: [
          {
            message: JSON.stringify({ errCode: 1010 }),
          },
        ],
      },
    };

    expect(getPromoErrorMessage(error)).toBe(1010);
  });

  it('returns null when message is valid JSON but no errCode', () => {
    const error = {
      response: {
        errors: [
          {
            message: JSON.stringify({ someOtherProp: 'value' }),
          },
        ],
      },
    };

    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when message is missing', () => {
    const error = {
      response: {
        errors: [{}],
      },
    };

    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when response is missing', () => {
    expect(getPromoErrorMessage(null)).toBeNull();
    expect(getPromoErrorMessage(undefined)).toBeNull();
    expect(getPromoErrorMessage({})).toBeNull();
  });

  it('returns null when JSON parsing fails (invalid JSON)', () => {
    const error = {
      response: {
        errors: [
          {
            message: 'this-is-not-json',
          },
        ],
      },
    };

    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when errors array is empty', () => {
    const error = { response: { errors: [] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when messageString is null or undefined', () => {
    const error1 = { response: { errors: [{ message: null }] } };
    const error2 = { response: { errors: [{ message: undefined }] } };

    expect(getPromoErrorMessage(error1)).toBeNull();
    expect(getPromoErrorMessage(error2)).toBeNull();
  });

  it('returns null when errors array is empty', () => {
    const error = { response: { errors: [] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when response is null', () => {
    const error = { response: null };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when response is undefined', () => {
    const error = { response: undefined };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when error is null', () => {
    expect(getPromoErrorMessage(null)).toBeNull();
  });

  it('returns null when error is undefined', () => {
    expect(getPromoErrorMessage(undefined)).toBeNull();
  });

  it('returns null when errors array is undefined', () => {
    const error = { response: { errors: undefined } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when first error is null', () => {
    const error = { response: { errors: [null] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when first error message is null', () => {
    const error = { response: { errors: [{ message: null }] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when first error message is undefined', () => {
    const error = { response: { errors: [{ message: undefined }] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });

  it('returns null when JSON parsing fails (invalid JSON string)', () => {
    const error = { response: { errors: [{ message: 'not-a-json' }] } };
    expect(getPromoErrorMessage(error)).toBeNull();
  });
});

describe('handlePromoApiError', () => {
  const handleSetError = jest.fn();

  const baseArgs = {
    createPromoIsError: true,
    createPromoError: {},
    duplicatePrefixError: 'Prefix already used',
    expiryDateInvalidError: 'The selected end date is not valid in Opera',
    promotionsInvalidError: 'Invalid Code',
    hotelIdInvalidError: 'Invalid Hotel ID',
    handleSetError,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('does nothing when createPromoIsError is false', () => {
    handlePromoApiError({ ...baseArgs, createPromoIsError: false });
    expect(handleSetError).not.toHaveBeenCalled();
  });

  it('handles error code 974 (hotelId)', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: {
        response: {
          errors: [{ message: JSON.stringify({ errCode: 974 }) }],
        },
      },
    });

    expect(handleSetError).toHaveBeenCalledWith('hotelId', {
      type: 'custom',
      message: 'Invalid Code',
    });
  });

  it('handles error code 1010 (prefix)', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: {
        response: { errors: [{ message: JSON.stringify({ errCode: 1010 }) }] },
      },
    });

    expect(handleSetError).toHaveBeenCalledWith('prefix', {
      type: 'custom',
      message: 'Prefix already used',
    });
  });

  it('handles error code 1011 (operaPromoCode)', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: {
        response: {
          errors: [{ message: JSON.stringify({ errCode: 1011 }) }],
        },
      },
    });

    expect(handleSetError).toHaveBeenCalledWith('operaPromoCode', {
      type: 'custom',
      message: 'Invalid Code',
    });
  });

  it('handles error code 1012 (expiryDate)', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: {
        response: {
          errors: [{ message: JSON.stringify({ errCode: 1012 }) }],
        },
      },
    });

    expect(handleSetError).toHaveBeenCalledWith('expiryDate', {
      type: 'custom',
      message: 'The selected end date is not valid in Opera',
    });
  });

  it('does nothing for unknown error codes', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: {
        response: {
          errors: [{ message: JSON.stringify({ errCode: 9999 }) }],
        },
      },
    });

    expect(handleSetError).not.toHaveBeenCalled();
  });

  it('does nothing when getPromoErrorMessage returns null', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: { response: { errors: [{}] } },
    });

    expect(handleSetError).not.toHaveBeenCalled();
  });

  it('works when createPromoError is completely undefined', () => {
    handlePromoApiError({
      ...baseArgs,
      createPromoError: undefined,
    });
    expect(handleSetError).not.toHaveBeenCalled();
  });

  it('does nothing when getPromoErrorMessage returns null', () => {
    const handleSetError = jest.fn();

    handlePromoApiError({
      createPromoIsError: true,
      createPromoError: { response: { errors: [{}] } },
      duplicatePrefixError: 'Duplicate prefix',
      expiryDateInvalidError: 'Invalid expiry date',
      promotionsInvalidError: 'Invalid Code',
      handleSetError,
    });

    expect(handleSetError).not.toHaveBeenCalled();
  });
});

describe('getPromoCodeErrorMessage', () => {
  it('returns globalErrTextTemplate for errCode 974', () => {
    const error = {
      response: {
        errors: [
          {
            message: JSON.stringify({
              errCode: 974,
              globalErrTextTemplate: 'Hotel ID is invalid globally',
            }),
          },
        ],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBe('Hotel ID is invalid globally');
  });

  it('returns null for errCode 974 when globalErrTextTemplate is missing', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ errCode: 974 }) }],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null for errCode 974 when globalErrTextTemplate is empty string', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ errCode: 974, globalErrTextTemplate: '' }) }],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns debugMessage for errCode 1011', () => {
    const error = {
      response: {
        errors: [
          {
            message: JSON.stringify({
              errCode: 1011,
              debugMessage: 'Opera promo code failed validation',
            }),
          },
        ],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBe('Opera promo code failed validation');
  });

  it('returns null for errCode 1011 when debugMessage is missing', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ errCode: 1011 }) }],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null for errCode 1011 when debugMessage is empty string', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ errCode: 1011, debugMessage: '' }) }],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null for an unhandled errCode (default case)', () => {
    const error = {
      response: {
        errors: [
          {
            message: JSON.stringify({
              errCode: 1010,
              globalErrTextTemplate: 'Should not be returned',
              debugMessage: 'Should not be returned either',
            }),
          },
        ],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when errCode is missing entirely', () => {
    const error = {
      response: {
        errors: [{ message: JSON.stringify({ globalErrTextTemplate: 'foo' }) }],
      },
    };

    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when message is missing', () => {
    const error = { response: { errors: [{}] } };
    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when messageString is null or undefined', () => {
    const error1 = { response: { errors: [{ message: null }] } };
    const error2 = { response: { errors: [{ message: undefined }] } };

    expect(getPromoCodeErrorMessage(error1)).toBeNull();
    expect(getPromoCodeErrorMessage(error2)).toBeNull();
  });

  it('returns null when errors array is empty', () => {
    const error = { response: { errors: [] } };
    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when errors array is undefined', () => {
    const error = { response: { errors: undefined } };
    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when response is missing, null, or undefined', () => {
    expect(getPromoCodeErrorMessage({})).toBeNull();
    expect(getPromoCodeErrorMessage({ response: null })).toBeNull();
    expect(getPromoCodeErrorMessage({ response: undefined })).toBeNull();
  });

  it('returns null when error itself is null or undefined', () => {
    expect(getPromoCodeErrorMessage(null)).toBeNull();
    expect(getPromoCodeErrorMessage(undefined)).toBeNull();
  });

  it('returns null when JSON parsing fails (invalid JSON string)', () => {
    const error = { response: { errors: [{ message: 'not-a-json' }] } };
    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });

  it('returns null when first error entry is null', () => {
    const error = { response: { errors: [null] } };
    expect(getPromoCodeErrorMessage(error)).toBeNull();
  });
});
