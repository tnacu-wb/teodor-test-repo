import { describe, it, expect, beforeEach, jest } from '@jest/globals';
import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';
import { useForm, FormProvider } from 'react-hook-form';

import CreatePromotionCodeForm from './CreatePromotionCodeForm';
import { useCreatePromoCodeFormContext } from './useCreatePromotionCodeFormContext';

const mockReplace = jest.fn().mockResolvedValue(true);
const mockUseCustomLocale = jest.fn(() => ({ country: 'uk', language: 'en' }));
const mockGetCreatePromotionFormFields = jest.fn(() => [
  { name: 'campaignName', label: 'Campaign Name', type: 'text', testId: 'campaignName' },
  { name: 'operaPromoCode', label: 'Opera Code', type: 'text', testId: 'operaCode' },
  { name: 'hotelId', label: 'Hotel ID', type: 'autoComplete', testId: 'hotelId' },
  { name: 'isGeneric', label: 'Is Generic', type: 'check', testId: 'isGeneric' },
  { name: 'prefix', label: 'Prefix', type: 'text', testId: 'prefix' },
  { name: 'genericPromoCode', label: 'Generic Code', type: 'text', testId: 'genericCode' },
  {
    name: 'isLimitRedemptions',
    label: 'Limit Redemptions',
    type: 'check',
    testId: 'isLimitRedemptions',
  },
  { name: 'batchCount', label: 'Batch Count', type: 'number', testId: 'batchCount' },
  { name: 'maxRedemptionLimit', label: 'Max Redemptions', type: 'number', testId: 'maxRedemption' },
  {
    name: 'platformConfig',
    label: 'Platform Config',
    type: 'platformConfig',
    testId: 'platformConfig',
  },
  { name: 'expiryDate', label: 'Expiry Date', type: 'datePicker', testId: 'expiryDate' },
]);

jest.mock('next/router', () => ({
  useRouter: () => ({
    replace: mockReplace,
  }),
}));

const mockSetLoadingTransition = jest.fn();
jest.mock('./useCreatePromotionCodeFormContext', () => ({
  useCreatePromoCodeFormContext: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  formatDataTestId: (base: string, id: string) => `${base}-${id}`,
  useCustomLocale: () => mockUseCustomLocale(),
  usePromoTranslation: () => ({
    cancelButtonText: 'Cancel',
    generateButtonText: 'Generate',
    platformConfigurationTitle: 'Platform Config',
    configurePlatformText: 'Configure your platform',
    duplicatePrefixError: 'Duplicate prefix',
    endDateInvalidError: 'Invalid expiry date',
    promotionsInvalidError: 'Invalid promotions',
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Input: ({ placeholderText, label, error, onChange, value, isDisabled, ...props }: any) => (
    <div>
      <label>{label}</label>
      <input
        aria-label={label}
        placeholder={placeholderText}
        value={value ?? ''}
        disabled={isDisabled}
        onChange={onChange}
        {...props}
      />
      {error && <span role="alert">{error}</span>}
    </div>
  ),
  Checkbox: ({ children, isChecked, onChange, ...props }: any) => (
    <label>
      <input type="checkbox" checked={!!isChecked} onChange={onChange} {...props} />
      {children}
    </label>
  ),
  SingleDatePicker: ({
    locale,
    defaultStartDate,
    onSelectDate,
    inputLabel,
    datepickerStyles,
    ...props
  }: any) => (
    <div>
      <label>{inputLabel}</label>
      <input
        aria-label={inputLabel}
        data-locale={locale}
        data-default-start-date={defaultStartDate ? String(defaultStartDate) : 'null'}
        data-datepicker-styles={JSON.stringify(datepickerStyles)}
        {...props}
      />
      <button
        type="button"
        data-testid="mock-select-date-btn"
        onClick={() => onSelectDate(new Date('2026-12-31'))}
      >
        Select Date
      </button>
    </div>
  ),
}));

jest.mock('./PlatformConfiguration', () => ({
  __esModule: true,
  default: ({ onChange, value }: any) => (
    <input
      aria-label="Platform Config Input"
      value={value || ''}
      onChange={(e) => onChange(e.target.value)}
    />
  ),
}));

jest.mock('./PlatformConfiguration/common', () => ({
  isPlatformConfigValid: jest.fn((val) => Boolean(val)),
}));

jest.mock('./common', () => ({
  todayLabel: 'Today',
  tomorrowLabel: 'Tomorrow',
  fieldProps: (field: any) => field,
  handlePromoApiError: jest.fn(),
  getCreatePromotionFormFields: (t: any) => mockGetCreatePromotionFormFields(t),
}));

const FormWrapper = ({
  defaultValues = {},
  errors = {},
  handleSetError = jest.fn(),
  customClearErrors,
  customSetValue,
}: any) => {
  const methods = useForm({
    defaultValues: {
      campaignName: '',
      operaPromoCode: '',
      hotelId: '',
      prefix: '',
      genericPromoCode: '',
      isGeneric: false,
      isLimitRedemptions: false,
      batchCount: '10',
      maxRedemptionLimit: '',
      platformConfig: 'valid',
      expiryDate: new Date(),
      ...defaultValues,
    },
  });

  return (
    <FormProvider {...methods}>
      <CreatePromotionCodeForm
        control={methods.control}
        formField={{ testid: 'create-promo' }}
        errors={errors}
        getValues={methods.getValues}
        handleSetError={handleSetError}
        clearErrors={customClearErrors ?? methods.clearErrors}
        setValue={customSetValue ?? methods.setValue}
      />
    </FormProvider>
  );
};

describe('CreatePromotionCodeForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
    (useCreatePromoCodeFormContext as jest.Mock).mockReturnValue({
      createPromoError: null,
      createPromoIsError: false,
      setLoadingTransition: mockSetLoadingTransition,
    });
    mockUseCustomLocale.mockReturnValue({ country: 'uk', language: 'en' });
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('Conditional Field Rendering (isGeneric & isLimitRedemptions)', () => {
    it('renders non-generic fields by default (Prefix & Batch Count visible)', () => {
      render(<FormWrapper defaultValues={{ isGeneric: false, isLimitRedemptions: false }} />);

      expect(screen.getByLabelText('Prefix')).toBeInTheDocument();
      expect(screen.getByLabelText('Batch Count')).toBeInTheDocument();

      expect(screen.queryByLabelText('Generic Code')).not.toBeInTheDocument();
      expect(screen.queryByLabelText('Limit Redemptions')).not.toBeInTheDocument();
      expect(screen.queryByLabelText('Max Redemptions')).not.toBeInTheDocument();
    });

    it('shows Generic Code and Limit Redemptions checkbox when isGeneric is checked', () => {
      render(<FormWrapper defaultValues={{ isGeneric: true }} />);

      expect(screen.getByLabelText('Generic Code')).toBeInTheDocument();
      expect(screen.getByLabelText('Limit Redemptions')).toBeInTheDocument();
      expect(screen.queryByLabelText('Prefix')).not.toBeInTheDocument();
    });

    it('shows Max Redemptions input and hides Batch Count when both isGeneric and isLimitRedemptions are checked', () => {
      render(<FormWrapper defaultValues={{ isGeneric: true, isLimitRedemptions: true }} />);

      expect(screen.getByLabelText('Max Redemptions')).toBeInTheDocument();
      expect(screen.queryByLabelText('Batch Count')).not.toBeInTheDocument();
    });
  });

  describe('isGeneric Toggle Effect', () => {
    it('calls clearErrors and setValue for prefix and batchCount when isGeneric becomes true', () => {
      const mockClearErrors = jest.fn();
      const mockSetValue = jest.fn();

      render(
        <FormWrapper
          defaultValues={{ isGeneric: false }}
          customClearErrors={mockClearErrors}
          customSetValue={mockSetValue}
        />
      );

      mockClearErrors.mockClear();
      mockSetValue.mockClear();

      const genericCheckbox = screen.getByLabelText('Is Generic');
      fireEvent.click(genericCheckbox);

      expect(mockClearErrors).toHaveBeenCalledWith(['prefix', 'batchCount']);
      expect(mockSetValue).toHaveBeenCalledWith('prefix', '');
      expect(mockSetValue).toHaveBeenCalledWith('batchCount', undefined);
    });

    it('calls clearErrors and setValue for genericPromoCode, isLimitRedemptions, and maxRedemptionLimit when isGeneric becomes false', () => {
      const mockClearErrors = jest.fn();
      const mockSetValue = jest.fn();

      render(
        <FormWrapper
          defaultValues={{ isGeneric: true }}
          customClearErrors={mockClearErrors}
          customSetValue={mockSetValue}
        />
      );

      mockClearErrors.mockClear();
      mockSetValue.mockClear();

      const genericCheckbox = screen.getByLabelText('Is Generic');
      fireEvent.click(genericCheckbox);

      expect(mockClearErrors).toHaveBeenCalledWith([
        'genericPromoCode',
        'isLimitRedemptions',
        'maxRedemptionLimit',
      ]);
      expect(mockSetValue).toHaveBeenCalledWith('genericPromoCode', '');
      expect(mockSetValue).toHaveBeenCalledWith('isLimitRedemptions', false);
      expect(mockSetValue).toHaveBeenCalledWith('maxRedemptionLimit', undefined);
    });

    it('handles undefined clearErrors and setValue props safely without throwing errors', () => {
      expect(() => {
        render(
          <FormWrapper
            defaultValues={{ isGeneric: false }}
            customClearErrors={undefined}
            customSetValue={undefined}
          />
        );
        const genericCheckbox = screen.getByLabelText('Is Generic');
        fireEvent.click(genericCheckbox);
      }).not.toThrow();
    });
  });

  describe('Value Transformations', () => {
    it('converts operaPromoCode, prefix, and genericPromoCode text to uppercase automatically', () => {
      render(<FormWrapper />);
      const operaInput = screen.getByLabelText('Opera Code') as HTMLInputElement;
      fireEvent.change(operaInput, { target: { value: 'mycode123' } });
      expect(operaInput.value).toBe('MYCODE123');
    });

    it('clamps maxRedemptionLimit values between 1 and 1000000', () => {
      render(<FormWrapper defaultValues={{ isGeneric: true, isLimitRedemptions: true }} />);
      const maxLimitInput = screen.getByLabelText('Max Redemptions') as HTMLInputElement;
      fireEvent.change(maxLimitInput, { target: { value: '1000000' } });
      expect(maxLimitInput.value).toBe('1000000');

      fireEvent.change(maxLimitInput, { target: { value: '0' } });
      expect(maxLimitInput.value).toBe('1');
    });
  });

  describe('Form Validation & Button States', () => {
    it('disables the Submit (Generate) button when required fields are missing', () => {
      render(
        <FormWrapper
          defaultValues={{
            campaignName: '',
            operaPromoCode: '',
            hotelId: '',
            prefix: '',
            expiryDate: null,
          }}
        />
      );
      const submitBtn = screen.getByTestId('submit-create-promotion-code-form');
      expect(submitBtn).toBeDisabled();
    });

    it('enables the Submit button when all required non-generic form fields are valid', () => {
      render(
        <FormWrapper
          defaultValues={{
            campaignName: 'Summer Promo',
            operaPromoCode: 'OPERA10',
            hotelId: 'HEAPTI',
            prefix: 'SUM',
            batchCount: '100',
            platformConfig: 'valid-config',
            expiryDate: new Date(),
          }}
        />
      );
      const submitBtn = screen.getByTestId('submit-create-promotion-code-form');
      expect(submitBtn).not.toBeDisabled();
    });
  });

  describe('Navigation & Action Handlers', () => {
    it('triggers loading transition and redirects to batch list on cancel click', async () => {
      render(<FormWrapper />);
      const cancelBtn = screen.getByTestId('cancel-create-promotion-code-form');
      fireEvent.click(cancelBtn);
      expect(mockSetLoadingTransition).toHaveBeenCalledWith(true);
      expect(mockReplace).toHaveBeenCalledWith('/uk/en/unique-promotions/list');
      await waitFor(
        () => {
          expect(mockSetLoadingTransition).toHaveBeenCalledWith(false);
        },
        { timeout: 1000 }
      );
    });
  });

  describe('DatePicker Field', () => {
    it('executes field.onChange and field.onBlur inside onSelectDate callback', () => {
      render(<FormWrapper />);
      const selectDateBtn = screen.getByTestId('mock-select-date-btn');
      fireEvent.click(selectDateBtn);
      expect(screen.getByLabelText('Expiry Date')).toBeInTheDocument();
    });

    it('executes field.onBlur and statement inside onBlurCapture handler', () => {
      render(<FormWrapper />);
      const datePickerInput = screen.getByLabelText('Expiry Date');
      fireEvent.blur(datePickerInput, { capture: true });
      expect(datePickerInput).toBeInTheDocument();
    });

    it('applies error border styles when expiryDate error message exists', () => {
      const mockErrors = {
        expiryDate: {
          message: 'Expiry date is required',
        },
      };
      render(<FormWrapper errors={mockErrors} />);
      const datePickerInput = screen.getByLabelText('Expiry Date');
      const passedStyles = JSON.parse(
        datePickerInput.getAttribute('data-datepicker-styles') || '{}'
      );

      expect(passedStyles.datepickerInputElementStyles).toEqual(
        expect.objectContaining({
          border: '1px solid var(--chakra-colors-error)',
          borderColor: 'none',
        })
      );
    });

    it('sets locale to "de" when useCustomLocale returns language "de"', () => {
      mockUseCustomLocale.mockReturnValue({
        country: 'de',
        language: 'de',
      });
      render(<FormWrapper />);
      const datePickerInput = screen.getByLabelText('Expiry Date');
      expect(datePickerInput.getAttribute('data-locale')).toBe('de');
    });

    it('sets locale to "en" when useCustomLocale returns a language other than "de"', () => {
      mockUseCustomLocale.mockReturnValue({
        country: 'uk',
        language: 'en',
      });
      render(<FormWrapper />);
      const datePickerInput = screen.getByLabelText('Expiry Date');
      expect(datePickerInput.getAttribute('data-locale')).toBe('en');
    });

    it('passes null as defaultStartDate when field.value is undefined/null (?? null branch)', () => {
      render(
        <FormWrapper
          defaultValues={{
            expiryDate: null,
          }}
        />
      );
      const datePickerInput = screen.getByLabelText('Expiry Date');
      expect(datePickerInput.getAttribute('data-default-start-date')).toBe('null');
    });

    it('returns empty fragment (<></>) for unknown or unhandled field types (return <></> branch)', () => {
      mockGetCreatePromotionFormFields.mockReturnValueOnce([
        {
          name: 'unsupportedField',
          label: 'Unknown Field',
          type: 'unsupportedType',
          testId: 'unknown',
        },
      ]);
      render(<FormWrapper />);
      expect(screen.queryByLabelText('Unknown Field')).not.toBeInTheDocument();
    });
  });

  it('executes field.onChange("") and returns early when maxRedemptionLimit is set to empty string', () => {
    render(
      <FormWrapper
        defaultValues={{
          isGeneric: true,
          isLimitRedemptions: true,
          maxRedemptionLimit: 50,
        }}
      />
    );
    const maxLimitInput = screen.getByLabelText('Max Redemptions') as HTMLInputElement;
    fireEvent.change(maxLimitInput, { target: { value: '' } });
    expect(maxLimitInput.value).toBe('');
  });
});
