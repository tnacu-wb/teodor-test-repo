import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { saveCard } from '@whitbread-eos/utils/server';

import { PaymentCardContainer } from './PaymentCardContainer';

jest.mock('next/navigation', () => ({
  useParams: () => ({ locale: 'en-gb' }),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
  saveCard: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ country: 'gb', language: 'en' }),
  cn: jest.fn(),
}));

jest.mock('../AddCardIframe', () => ({
  AddCardIframe: (props: { onSuccess: (data: any) => void; onError: () => void }) => (
    <div data-testid="add-card-iframe">
      <button onClick={() => props.onSuccess({ cardNumber: '1234' })}>Success</button>
      <button onClick={() => props.onError()}>Error</button>
    </div>
  ),
}));

describe('PaymentCardContainer', () => {
  const mockOnSuccess = jest.fn();
  const mockOnCancel = jest.fn();
  const defaultProps = {
    onSuccess: mockOnSuccess,
    onCancel: mockOnCancel,
    token: 'test-token',
    employeeId: 'test-employee',
    isPiba: false,
    isPreferenceCard: true,
  };

  const mockIframeData = {
    paymentRedirect: 'test-redirect',
    providerUrl: 'test-provider',
    template: 'test-template',
    sessionId: 'test-session',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('initiates payment on mount', async () => {
    (saveCard as jest.Mock).mockImplementationOnce(({ onSuccess }) => {
      onSuccess(mockIframeData);
    });

    render(<PaymentCardContainer {...defaultProps} />);

    await waitFor(() => {
      expect(saveCard).toHaveBeenCalledWith(
        expect.objectContaining({
          token: 'test-token',
          employeeId: 'test-employee',
          isPiba: false,
        })
      );
    });

    expect(screen.getByTestId('add-card-iframe')).toBeInTheDocument();
  });

  it('calls onCancel when saveCard fails', async () => {
    (saveCard as jest.Mock).mockImplementationOnce(({ onError }) => {
      onError();
    });

    render(<PaymentCardContainer {...defaultProps} />);

    await waitFor(() => {
      expect(mockOnCancel).toHaveBeenCalled();
    });
  });

  it('calls onCancel when saveCard returns invalid data', async () => {
    (saveCard as jest.Mock).mockImplementationOnce(({ onSuccess }) => {
      onSuccess(null);
    });

    render(<PaymentCardContainer {...defaultProps} />);

    await waitFor(() => {
      expect(mockOnCancel).toHaveBeenCalled();
    });
  });

  it('handles iframe error', async () => {
    (saveCard as jest.Mock).mockImplementationOnce(({ onSuccess }) => {
      onSuccess(mockIframeData);
    });

    render(<PaymentCardContainer {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByTestId('add-card-iframe')).toBeInTheDocument();
    });

    screen.getByText('Error').click();

    await waitFor(() => {
      expect(mockOnCancel).toHaveBeenCalled();
    });
  });

  it('prevents multiple payment initiations', async () => {
    (saveCard as jest.Mock).mockImplementation(({ onSuccess }) => {
      onSuccess(mockIframeData);
    });

    const { rerender } = render(<PaymentCardContainer {...defaultProps} />);

    await waitFor(() => {
      expect(saveCard).toHaveBeenCalledTimes(1);
    });

    rerender(<PaymentCardContainer {...defaultProps} />);

    await new Promise((resolve) => setTimeout(resolve, 100));

    expect(saveCard).toHaveBeenCalledTimes(1);
  });
});
