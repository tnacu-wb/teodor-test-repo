import { HandlePromoApiErrorParams } from './types';

export const todayLabel = 'Today';
export const tomorrowLabel = 'Tomorrow';

export const getCreatePromotionFormFields = (t: Record<string, string>) => {
  return [
    {
      type: 'text',
      name: 'campaignName',
      label: `${t.campaignNameLabel}*`,
      testId: 'campaignName',
    },
    {
      type: 'text',
      name: 'operaPromoCode',
      label: `${t.promoCodeFormLabel}*`,
      testId: 'operaPromoCode',
    },
    {
      type: 'autoComplete',
      name: 'hotelId',
      label: `${t.hotelIdLabel}*`,
      testId: 'hotelId',
    },
    {
      type: 'check',
      name: 'isGeneric',
      label: `${t.isGenericLabel}`,
      testId: 'isGeneric',
    },
    {
      type: 'text',
      name: 'prefix',
      label: `${t.voucherPrefixTitle}*`,
      testId: 'prefix',
    },
    {
      type: 'text',
      name: 'genericPromoCode',
      label: `${t.genericPromoCodeLabel}`,
      testId: 'genericPromoCode',
    },
    {
      type: 'datePicker',
      name: 'expiryDate',
      label: `${t.endDateLabel}*`,
      testId: 'expiryDate',
    },
    {
      type: 'check',
      name: 'isLimitRedemptions',
      label: `${t.limitRedemptionsLabel}`,
      testId: 'isLimitRedemptions',
    },
    {
      type: 'number',
      name: 'maxRedemptionLimit',
      label: `${t.maximumRedemptionsLabel}`,
      testId: 'maxRedemptionLimit',
      min: 1,
      max: 1000000,
    },
    {
      type: 'number',
      name: 'batchCount',
      label: `${t.vouchersNeededLabel}*`,
      testId: 'batchCount',
    },
    {
      type: 'platformConfig',
      name: 'platformConfig',
      label: `${t.platformConfigurationTitle}*`,
      testId: 'platformConfig',
    },
    {
      type: 'text',
      name: 'requestedBy',
      label: t.requestedByTitle,
      testId: 'requestedBy',
      isDisabled: true,
    },
  ];
};

export const fieldProps = (field: any) => {
  return {
    name: field.name,
    onBlur: field.onBlur,
    value: field.value,
    onChange: (value: any) => {
      field.onChange(value);
    },
  };
};

export const notesWrapperStyles = {
  width: '100%',
  padding: '1.375rem',
  borderRadius: '0 0 8px 8px',
  background: '#EAF2F3',
  fontSize: '0.875rem',
  color: 'var(--Greys-Greys---Base-Black, #000)',
  fontStyle: 'normal',
  lineHeight: '1.3125rem',
};

export const notesTextStyles = {
  fontWeight: '600',
  marginBottom: '0.5rem',
};

export const notesListWrapper = {
  styleType: 'disc',
  paddingLeft: 3,
  fontWeight: '400',
  lineHeight: '1.3125rem',
};

export const getPromoErrorMessage = (error: any): string | null => {
  const messageString = error?.response?.errors?.[0]?.message;
  if (!messageString) {
    return null;
  }
  try {
    const parsed = JSON.parse(messageString);
    return parsed?.errCode ?? null;
  } catch {
    return null;
  }
};

export const getPromoCodeErrorMessage = (error: any): string | null => {
  const messageString = error?.response?.errors?.[0]?.message;

  if (!messageString) {
    return null;
  }

  try {
    const parsed = JSON.parse(messageString);

    switch (parsed?.errCode) {
      case 974:
        return parsed?.globalErrTextTemplate || null;

      case 1011:
        return parsed?.debugMessage || null;

      default:
        return null;
    }
  } catch {
    return null;
  }
};

export const handlePromoApiError = ({
  createPromoIsError,
  createPromoError,
  duplicatePrefixError,
  expiryDateInvalidError,
  promotionsInvalidError,
  handleSetError,
}: HandlePromoApiErrorParams) => {
  if (!createPromoIsError) return;

  const errorCode = Number(getPromoErrorMessage(createPromoError));
  const errorMessage = getPromoCodeErrorMessage(createPromoError);

  switch (errorCode) {
    case 1010:
      handleSetError('prefix', {
        type: 'custom',
        message: duplicatePrefixError,
      });
      break;

    case 1011:
      handleSetError('operaPromoCode', {
        type: 'custom',
        message: errorMessage || promotionsInvalidError,
      });
      break;

    case 974:
      handleSetError('hotelId', {
        type: 'custom',
        message: errorMessage || promotionsInvalidError,
      });
      break;

    case 1012:
      handleSetError('expiryDate', {
        type: 'custom',
        message: expiryDateInvalidError,
      });
      break;
    default:
      break;
  }
};
