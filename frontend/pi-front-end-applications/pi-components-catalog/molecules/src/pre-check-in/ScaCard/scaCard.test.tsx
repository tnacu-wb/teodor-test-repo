import { render, screen } from '@testing-library/react';
import { BASKET_STATUS } from '@whitbread-eos/api';
import React from 'react';

import ScaCard from './index';

const mockUseQueryRequest = {
  isLoading: true,
  isError: false,
  error: { message: '' },
  data: {},
};

const mockUsePollBasketStatus = {
  pollingInProgress: true,
  basketStatus: BASKET_STATUS.AMEND_FAILED,
  dynamicSpinnerLabel: [],
  retryPayment: true,
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn().mockReturnValue({ t: (key) => key }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockUseQueryRequest,
  usePollBasketStatus: () => mockUsePollBasketStatus,
}));

describe('ScaCard', () => {
  const mockProps = {
    scaErrorVisible: false,
    copyBookingReference: 'ABC123',
    copyBookingLoading: false,
    paymentLoading: false,
    paymentData: {
      initiatePayment: {
        paymentRequiredDetails: {
          paymentRedirect: '<iframe src="https://example.com"></iframe>',
          providerUrl: 'https://example.com',
        },
      },
    },
    isSuccess: true,
    isPaymentComplete: true,
    handleRetryPayment: jest.fn(),
    handlePaymentComplete: jest.fn(),
    setPaymentFailedError: jest.fn(),
  };

  it('should render the description text', () => {
    render(<ScaCard {...mockProps} />);
    const descriptionText = screen.getByTestId('pre-checkin-sca-description');
    expect(descriptionText).toBeInTheDocument();
    expect(descriptionText.textContent).toBe('precheckin.SCA.description');
  });

  it('should render the error notification when scaErrorVisible is true', () => {
    render(<ScaCard {...mockProps} scaErrorVisible={true} />);
    const errorNotification = screen.getByTestId('reg-card-sca-error-AlertDescription');
    expect(errorNotification).toBeInTheDocument();
    expect(errorNotification).toHaveTextContent('precheckin.SCA.error');
  });

  it('should render the error notification when paymentLoading is true', () => {
    render(<ScaCard {...mockProps} paymentLoading={true} />);
    const descriptionText = screen.getByTestId('pre-checkin-sca-description');
    expect(descriptionText).toBeInTheDocument();
  });
});
