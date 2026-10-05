import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { AddCardIframe, IframeData } from './AddCardIframe';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('./hooks/useIframe', () => ({
  useIframe: () => jest.fn(),
}));

describe('AddCardIframe Component', () => {
  const mockIframeData: IframeData = {
    paymentRedirect: 'https://example.com/payment',
    providerUrl: 'https://example.com/provider',
    template: null,
    sessionId: 'test-session-id',
  };

  const mockOnSuccess = jest.fn();
  const mockOnError = jest.fn();

  const mockProfileDetails = {
    contactDetail: {
      address: {},
    },
    paymentPreference: {
      paymentCard: {
        cardNumber: '1234',
        cardType: 'visa',
        cardHolderName: 'Test User',
        expiryDate: '12/25',
      },
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders the component with correct structure', () => {
    render(
      <AddCardIframe
        iframeData={mockIframeData}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
        profileDetails={mockProfileDetails}
      />
    );

    expect(screen.getByTestId('add-card-iframe-element')).toBeInTheDocument();
  });

  test('handles message event with payment data and calls onSuccess', () => {
    render(
      <AddCardIframe iframeData={mockIframeData} onSuccess={mockOnSuccess} onError={mockOnError} />
    );

    const messageData = {
      paymentId: 'payment123',
      tokenNo: '456789',
      cardType: 'mastercard',
      cardHolderName: 'John Doe',
      expiryDate: '10/28',
    };

    window.dispatchEvent(
      new MessageEvent('message', {
        data: JSON.stringify(messageData),
      })
    );

    expect(mockOnSuccess).toHaveBeenCalledTimes(1);
    expect(mockOnSuccess).toHaveBeenCalledWith(
      expect.objectContaining({
        paymentId: 'payment123',
        tokenNo: '456789',
        cardType: 'mastercard',
        cardHolderName: 'John Doe',
        expiryDate: '10/28',
      })
    );

    expect(mockOnError).not.toHaveBeenCalled();
  });

  test('handles error message event and calls onError', () => {
    render(
      <AddCardIframe iframeData={mockIframeData} onSuccess={mockOnSuccess} onError={mockOnError} />
    );

    const errorMessage = 'payment failed: invalid card details';

    window.dispatchEvent(
      new MessageEvent('message', {
        data: errorMessage,
      })
    );

    expect(mockOnError).toHaveBeenCalledTimes(1);
    expect(mockOnError).toHaveBeenCalledWith(errorMessage);

    expect(mockOnSuccess).not.toHaveBeenCalled();
  });
});
