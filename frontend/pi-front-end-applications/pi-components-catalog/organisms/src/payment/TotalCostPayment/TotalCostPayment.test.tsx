import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import type { Props } from './TotalCostPayment.container';
import TotalCost from './TotalCostPayment.container';

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    text: 'smthing',
  },
};

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useQueryRequest: () => mockUseQueryRequest,
}));

const mockedTotalCostData: Props = {
  hotelName: 'London Harbor',
  hotelId: 'DLONEU',
  rateCode: 'A',
  selectedPaymentDetail: {
    order: 1,
    enabled: true,
    type: 'RESERVE_WITHOUT_CARD',
  },
  ratePlan: {
    name: 'Flex',
    totalCost: {
      amount: '345',
      currency: 'EUR',
    },
  },
  selectedPaymentType: {
    type: 'RESERVE_WITHOUT_CARD',
    order: 1,
    enabled: true,
    cnpOptionAvailable: false,
    cnpPreSelected: false,
    name: 'Payment Method',
  },
  paypalOptions: {
    currency: 'EUR',
  },
  isBillingAddressDisplayed: false,
  errorMessagePayment: undefined as string | undefined,
  continueToNextStep: () => null,
};

describe('TotalCostCard ', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
  });

  it('should render a TotalCostCard corectly', function () {
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Container')).toBeInTheDocument();
  });

  it('should render the corect currency symbol', function () {
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('€');
  });
  it('should render the corect currency symbol for GBP', function () {
    mockedTotalCostData.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });
  it('should render the corect currency symbol for GBP with de currency', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedTotalCostData.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });
  it('should render the corect currency symbol for GBP with DE currency', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedTotalCostData.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });
  it('should render the corect currency symbol for default currentLang with DE currency', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedTotalCostData.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });

  it('should receive the desired currency and value  ', () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedTotalCostData.ratePlan.totalCost.currency = 'EUR';
    const { getByTestId } = render(<TotalCost {...mockedTotalCostData} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('345€');
  });

  it('should display error notification when we receive errors from ConfirmationMutation/PaymentMutationData', function () {
    mockedTotalCostData.errorMessagePayment =
      'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101';
    const { getByTestId, getByText } = render(<TotalCost {...mockedTotalCostData} />);

    expect(getByTestId('Payment-Error-Alert')).toBeInTheDocument();
    expect(
      getByText(
        'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101'
      )
    ).toBeInTheDocument();
  });
});
