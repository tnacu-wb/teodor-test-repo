import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { act, fireEvent, render, screen } from '~utils/test-utils';

import {
  DatatransSecureFieldsForm,
  DatatransSecureFieldsFormHandle,
} from './DatatransSecureFieldsForm';

expect.extend(toHaveNoViolations);

// Mock the useDatatransSecureFields hook
const mockSubmitPayment = jest.fn();
const mockHookReturn = {
  isScriptLoaded: true,
  isScriptLoading: false,
  sessionError: null as Error | null,
  isInitialized: true,
  isReady: true,
  isValid: true,
  errors: {} as Record<string, string>,
  transactionId: 'txn-123',
  submitPayment: mockSubmitPayment,
};
const mockUseDatatransSecureFields = jest.fn(() => mockHookReturn);

jest.mock('~hooks/use-datatrans-secure-fields', () => ({
  useDatatransSecureFields: (config: unknown) => mockUseDatatransSecureFields(config),
}));

const defaultProps = {
  onSuccess: jest.fn(),
  onError: jest.fn(),
  basketId: 'basket-123',
  isVisible: true,
};

const renderComponent = (props = {}) => {
  return render(
    <ChakraProvider>
      <DatatransSecureFieldsForm {...defaultProps} {...props} />
    </ChakraProvider>
  );
};

describe('DatatransSecureFieldsForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockHookReturn.errors = {};
    mockHookReturn.sessionError = null;
    mockHookReturn.isReady = true;
    mockHookReturn.isValid = true;
    mockHookReturn.isInitialized = true;
    mockHookReturn.isScriptLoaded = true;
    mockHookReturn.isScriptLoading = false;
    mockHookReturn.transactionId = 'txn-123';
  });

  describe('Visibility toggling', () => {
    it('should have display: none when isVisible is false', () => {
      renderComponent({ isVisible: false });

      const container = screen.getByTestId('DatatransSecureFieldsForm-Container');
      expect(container).toHaveStyle({ display: 'none' });
    });

    it('should be visible when isVisible is true', () => {
      renderComponent({ isVisible: true });

      const container = screen.getByTestId('DatatransSecureFieldsForm-Container');
      expect(container).not.toHaveStyle({ display: 'none' });
    });
  });

  describe('Error display', () => {
    it('should display error messages when hook returns errors for cardNumber and cvv', () => {
      mockHookReturn.errors = {
        cardNumber: 'Card number is invalid',
        cvv: 'CVV is required',
      };

      renderComponent();

      expect(screen.getByText('Card number is invalid')).toBeInTheDocument();
      expect(screen.getByText('CVV is required')).toBeInTheDocument();
    });
  });

  describe('Local validation errors', () => {
    it('should show inline errors when submit is triggered without selecting expiry', () => {
      const ref = React.createRef<DatatransSecureFieldsFormHandle>();

      render(
        <ChakraProvider>
          <DatatransSecureFieldsForm {...defaultProps} ref={ref} />
        </ChakraProvider>
      );

      act(() => {
        ref.current?.submit();
      });

      expect(screen.getByText('Expiry month is required')).toBeInTheDocument();
      expect(screen.getByText('Expiry year is required')).toBeInTheDocument();
      expect(mockSubmitPayment).not.toHaveBeenCalled();
    });
  });

  describe('onSuccess callback', () => {
    it('should pass onSuccess to the Datatrans hook', () => {
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

  describe('onError callback', () => {
    it('should call onError when hook scriptError is set', () => {
      const onError = jest.fn();
      const scriptError = new Error('Script failed to load');
      mockHookReturn.sessionError = scriptError;

      renderComponent({ onError });

      expect(onError).toHaveBeenCalledWith(scriptError);
    });
  });

  describe('Submit method', () => {
    it('should call submitPayment on the hook with expiry month and year when ref submit is called', () => {
      const ref = React.createRef<DatatransSecureFieldsFormHandle>();

      render(
        <ChakraProvider>
          <DatatransSecureFieldsForm {...defaultProps} ref={ref} />
        </ChakraProvider>
      );

      // Select expiry month
      const monthSelect = screen.getByTestId('DatatransSecureFieldsForm-ExpiryMonth');
      fireEvent.change(monthSelect, { target: { value: '03' } });

      // Select expiry year
      const yearSelect = screen.getByTestId('DatatransSecureFieldsForm-ExpiryYear');
      const currentYear = new Date().getFullYear();
      fireEvent.change(yearSelect, { target: { value: String(currentYear + 1) } });

      act(() => {
        ref.current?.submit();
      });

      expect(mockSubmitPayment).toHaveBeenCalledWith('03', String(currentYear + 1), '');
    });
  });

  describe('Accessibility', () => {
    it('should have no accessibility violations', async () => {
      const { container } = renderComponent();

      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });
  });
});
