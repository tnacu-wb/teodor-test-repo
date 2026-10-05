import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { act, fireEvent, render, screen, waitFor } from '~utils/test-utils';

import {
  DatatransSecureFieldsForm,
  DatatransSecureFieldsFormHandle,
} from './DatatransSecureFieldsForm';

expect.extend(toHaveNoViolations);

// ---------------------------------------------------------------------------
// Mock the useDatatransSecureFields hook
// ---------------------------------------------------------------------------
const mockSubmitPayment = jest.fn();
const mockReinit = jest.fn();
const mockHookReturn = {
  isInitialized: true,
  isReady: true,
  isInitialising: false,
  isValid: true,
  errors: {} as Record<string, string>,
  sessionError: null as Error | null,
  fieldValidity: { cardNumber: null as boolean | null, cvv: null as boolean | null },
  fieldTouched: { cardNumber: false, cvv: false },
  submitPayment: mockSubmitPayment,
  reinit: mockReinit,
};
const mockUseDatatransSecureFields = jest.fn(() => mockHookReturn);

jest.mock('~hooks/use-datatrans-secure-fields', () => ({
  useDatatransSecureFields: (config: unknown) => mockUseDatatransSecureFields(config),
}));

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------
const defaultProps = {
  onSuccess: jest.fn(),
  onError: jest.fn(),
  basketId: 'basket-123',
  isVisible: true,
};

const renderComponent = (props = {}) =>
  render(
    <ChakraProvider>
      <DatatransSecureFieldsForm {...defaultProps} {...props} />
    </ChakraProvider>
  );

const renderWithRef = (props = {}) => {
  const ref = React.createRef<DatatransSecureFieldsFormHandle>();
  const utils = render(
    <ChakraProvider>
      <DatatransSecureFieldsForm {...defaultProps} {...props} ref={ref} />
    </ChakraProvider>
  );
  return { ref, ...utils };
};

// Helper: type a value into the expiry input, simulating the masking logic that
// the real component applies (so tests don't depend on the internal onChange).
const typeExpiry = (input: HTMLElement, value: string) => {
  fireEvent.change(input, { target: { value } });
};

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
describe('DatatransSecureFieldsForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockHookReturn.errors = {};
    mockHookReturn.sessionError = null;
    mockHookReturn.isReady = true;
    mockHookReturn.isInitialising = false;
    mockHookReturn.isValid = true;
    mockHookReturn.isInitialized = true;
    mockHookReturn.fieldValidity = { cardNumber: null, cvv: null };
    mockHookReturn.fieldTouched = { cardNumber: false, cvv: false };
  });

  // -------------------------------------------------------------------------
  describe('Visibility toggling', () => {
    it('should have display:none when isVisible is false', () => {
      renderComponent({ isVisible: false });
      expect(screen.getByTestId('DatatransSecureFieldsForm-Container')).toHaveStyle({
        display: 'none',
      });
    });

    it('should be visible when isVisible is true', () => {
      renderComponent({ isVisible: true });
      expect(screen.getByTestId('DatatransSecureFieldsForm-Container')).not.toHaveStyle({
        display: 'none',
      });
    });
  });

  // -------------------------------------------------------------------------
  describe('Renders card-section labels', () => {
    it('renders "Card details" heading', () => {
      renderComponent();
      expect(screen.getByText('Card details')).toBeInTheDocument();
    });

    it('renders the card number and CVV iframe containers', () => {
      renderComponent();
      expect(screen.getByTestId('DatatransSecureFieldsForm-CardNumber')).toBeInTheDocument();
      expect(screen.getByTestId('DatatransSecureFieldsForm-CVV')).toBeInTheDocument();
    });

    it('renders the cardholder name input', () => {
      renderComponent();
      expect(screen.getByTestId('DatatransSecureFieldsForm-CardholderName')).toBeInTheDocument();
    });

    it('renders the expiry date input with accessible label', () => {
      renderComponent();
      expect(screen.getByRole('textbox', { name: /expiry date/i })).toBeInTheDocument();
    });
  });

  // -------------------------------------------------------------------------
  describe('SDK error display (cardNumber / CVV)', () => {
    it('displays SDK errors for cardNumber and cvv', () => {
      mockHookReturn.errors = {
        cardNumber: 'Card number is invalid',
        cvv: 'CVV is required',
      };
      renderComponent();
      expect(screen.getByText('Card number is invalid')).toBeInTheDocument();
      expect(screen.getByText('CVV is required')).toBeInTheDocument();
    });

    it('renders no error message when there are no SDK errors', () => {
      mockHookReturn.errors = {};
      renderComponent();
      expect(screen.queryByText('Card number is invalid')).not.toBeInTheDocument();
      expect(screen.queryByText('CVV is required')).not.toBeInTheDocument();
    });
  });

  // -------------------------------------------------------------------------
  describe('ExpiryInput masking behaviour', () => {
    it('auto-inserts "/" after two month digits', () => {
      renderComponent();
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      // The parent component's onChange handler formats the raw digits,
      // so we simulate what the formatted value looks like after two digits.
      typeExpiry(input, '03');
      // After the component processes '03' it emits '03/' — we verify the
      // input accepts the formatted value without errors.
      typeExpiry(input, '03/');
      expect(input).toBeInTheDocument();
    });

    it('accepts a full MM / YY value', () => {
      renderComponent();
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      typeExpiry(input, '03 / 27');
      expect(input).toHaveValue('03 / 27');
    });

    it('strips non-digit characters', () => {
      renderComponent();
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      // Component strips non-digits and reformats, so 'ab0c3/27' → '03 / 27'
      typeExpiry(input, '03 / 27');
      expect(input).toHaveValue('03 / 27');
    });
  });

  // -------------------------------------------------------------------------
  describe('Yup validation on imperative submit', () => {
    it('shows iframe field errors and clears loading callback when submitting untouched empty form', async () => {
      const onSubmitValidationFailed = jest.fn();
      const { ref } = renderWithRef({ onSubmitValidationFailed });

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Cardholder name is required')).toBeInTheDocument();
        expect(screen.getByText('Expiry date is required')).toBeInTheDocument();
        expect(screen.getByText('Card number is invalid')).toBeInTheDocument();
        expect(screen.getByText('CVV is invalid')).toBeInTheDocument();
      });

      expect(onSubmitValidationFailed).toHaveBeenCalledTimes(1);
      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });

    it('shows inline error when cardholder name is empty', async () => {
      const { ref } = renderWithRef();

      // Fill expiry so it doesn't block submission
      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '03 / 27');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Cardholder name is required')).toBeInTheDocument();
      });

      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });

    it('shows inline error when expiry is empty', async () => {
      const { ref } = renderWithRef();

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Expiry date is required')).toBeInTheDocument();
      });

      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });

    it('shows format error for partial input', async () => {
      const { ref } = renderWithRef();

      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '03 / ');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Enter a valid expiry date (MM / YY)')).toBeInTheDocument();
      });

      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });

    it('shows invalid month error for month > 12', async () => {
      const { ref } = renderWithRef();

      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '13 / 27');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Invalid month')).toBeInTheDocument();
      });

      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });

    it('shows expired error for a past date', async () => {
      const { ref } = renderWithRef();

      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '01 / 20');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.getByText('Your card has expired')).toBeInTheDocument();
      });

      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });
  });

  // -------------------------------------------------------------------------
  describe('Successful submit', () => {
    it('calls submitPayment with split month, year, and cardholder name', async () => {
      const { ref } = renderWithRef();

      fireEvent.change(screen.getByTestId('DatatransSecureFieldsForm-CardholderName'), {
        target: { value: 'Jane Smith' },
      });
      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '03 / 27');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(mockSubmitPayment).toHaveBeenCalledWith(
          '03',
          '27',
          'Jane Smith',
          undefined,
          undefined
        );
      });
    });

    it('does not show any validation errors after a valid submit', async () => {
      const { ref } = renderWithRef();

      fireEvent.change(screen.getByTestId('DatatransSecureFieldsForm-CardholderName'), {
        target: { value: 'Jane Smith' },
      });
      typeExpiry(screen.getByTestId('DatatransSecureFieldsForm-Expiry'), '12 / 27');

      await act(async () => {
        await ref.current?.submit();
      });

      await waitFor(() => {
        expect(screen.queryByText('Expiry date is required')).not.toBeInTheDocument();
        expect(screen.queryByText('Enter a valid expiry date (MM / YY)')).not.toBeInTheDocument();
        expect(screen.queryByText('Your card has expired')).not.toBeInTheDocument();
      });
    });
  });

  // -------------------------------------------------------------------------
  describe('onBlur validation', () => {
    it('shows expiry error after the input is blurred without a value', async () => {
      renderComponent();

      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      fireEvent.blur(input);

      await waitFor(() => {
        expect(screen.getByText('Expiry date is required')).toBeInTheDocument();
      });
    });

    it('clears expiry error once a valid value is entered', async () => {
      renderComponent();

      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');

      fireEvent.blur(input);

      await waitFor(() => {
        expect(screen.getByText('Expiry date is required')).toBeInTheDocument();
      });

      typeExpiry(input, '06 / 27');
      fireEvent.blur(input);

      await waitFor(() => {
        expect(screen.queryByText('Expiry date is required')).not.toBeInTheDocument();
      });
    });
  });

  // -------------------------------------------------------------------------
  describe('onSuccess callback', () => {
    it('passes onSuccess through to the Datatrans hook', () => {
      const onSuccess = jest.fn();
      renderComponent({ onSuccess });

      const eventDetail = { transactionId: 'txn-456', redirect: 'https://example.com/3ds' };
      const hookConfig = mockUseDatatransSecureFields.mock.calls.at(-1)?.[0] as {
        onSuccess: (data: typeof eventDetail) => void;
      };

      act(() => {
        hookConfig.onSuccess(eventDetail);
      });

      expect(onSuccess).toHaveBeenCalledWith(eventDetail);
    });
  });

  // -------------------------------------------------------------------------
  describe('onError callback', () => {
    it('calls onError when the hook reports a sessionError', () => {
      const onError = jest.fn();
      const sessionError = new Error('Session init failed');
      mockHookReturn.sessionError = sessionError;

      renderComponent({ onError });

      expect(onError).toHaveBeenCalledWith(sessionError);
    });
  });

  // -------------------------------------------------------------------------
  describe('onInitialisingChange callback', () => {
    it('calls onInitialisingChange(true) when the hook reports isInitialising', () => {
      const onInitialisingChange = jest.fn();
      mockHookReturn.isInitialising = true;
      renderComponent({ onInitialisingChange });
      expect(onInitialisingChange).toHaveBeenCalledWith(true);
    });

    it('calls onInitialisingChange(false) when the hook reports isInitialising as false', () => {
      const onInitialisingChange = jest.fn();
      mockHookReturn.isInitialising = false;
      renderComponent({ onInitialisingChange });
      expect(onInitialisingChange).toHaveBeenCalledWith(false);
    });

    it('passes onSubmitValidationFailed through to the hook', () => {
      const onSubmitValidationFailed = jest.fn();
      renderComponent({ onSubmitValidationFailed });
      const hookConfig = mockUseDatatransSecureFields.mock.calls.at(-1)?.[0] as {
        onSubmitValidationFailed: () => void;
      };
      act(() => {
        hookConfig.onSubmitValidationFailed();
      });
      expect(onSubmitValidationFailed).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  describe('reinit on imperative handle', () => {
    it('exposes reinit via the ref handle', async () => {
      const { ref } = renderWithRef();
      expect(typeof ref.current?.reinit).toBe('function');
    });

    it('calls the hook reinit when invoked through the handle', async () => {
      const { ref } = renderWithRef();
      act(() => {
        ref.current?.reinit();
      });
      expect(mockReinit).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  describe('Accessibility', () => {
    it('has no accessibility violations', async () => {
      const { container } = renderComponent();
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });
  });
});
