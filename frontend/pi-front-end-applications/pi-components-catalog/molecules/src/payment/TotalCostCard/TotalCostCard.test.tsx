import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { formatDataTestId } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import TotalCostCard, { renderPrice } from './TotalCostCard.component';

const mockContinue = jest.fn();
const mockedProps = {
  isAmendPage: false,
  hotelName: 'Manchester Old Trafford',
  hotelId: 'MANOLD',
  rateCode: 'A',
  ratePlan: {
    name: 'FLEXRATE',
    totalCost: {
      amount: '345',
      currency: 'EUR',
    },
  },
  selectedPaymentDetail: {
    type: '',
    order: 0,
    enabled: false,
  },
  isError: false,
  error: {
    message: undefined,
  },
  isLoading: false,
  data: {
    termsAndConditions: {
      rate: '',
      text: '',
    },
  },
  isBillingAddressDisplayed: false,
  errorMessagePayment: undefined as string | undefined,
  continueToNextStep: mockContinue,
  additionalAmountLabel: '',
};
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

const baseDataTestId = 'TotalCost';

describe('Payment - TotalCostCard', () => {
  beforeEach(() => {
    jest.resetAllMocks();

    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
  });

  it('should display the compomnent', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should display the label', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'label'))).toBeInTheDocument();
  });

  it('should display the correct currency symbol', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Currency'))).toBeInTheDocument();
  });

  it('should render the corect currency symbol for GBP', function () {
    mockedProps.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });

  it('should render the corect currency symbol for GBP with de currency', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedProps.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });

  it('should render the corect currency symbol for default currentLang with DE currency', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedProps.ratePlan.totalCost.currency = 'GBP';
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('£');
  });

  it('should receive the desired currency and value  ', () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    mockedProps.ratePlan.totalCost.currency = 'EUR';
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('TotalCost-Currency')).toHaveTextContent('345€');
  });

  it('should display error notification when we receive errors from ConfirmationMutation/PaymentMutationData', function () {
    mockedProps.errorMessagePayment =
      'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101';
    const { getByTestId, getByText } = render(<TotalCostCard {...mockedProps} />);

    expect(getByTestId('Payment-Error-Alert')).toBeInTheDocument();
    expect(
      getByText(
        'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101'
      )
    ).toBeInTheDocument();
  });

  it('should display the hotelName', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'HotelName'))).toBeInTheDocument();
  });

  it('should render the termsandconditions', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('termsAndConditions')).toBeInTheDocument();
  });

  it('should render the submit button and not call continuie', () => {
    const { getByTestId } = render(
      <TotalCostCard {...mockedProps} isBillingAddressDisplayed={true} />
    );
    expect(getByTestId('submitButton')).toBeInTheDocument();
    fireEvent.click(getByTestId('submitButton'));

    expect(mockContinue).toHaveBeenCalledTimes(0);
  });

  it('should render the submit button', () => {
    const { getByTestId } = render(<TotalCostCard {...mockedProps} />);
    expect(getByTestId('submitButton')).toBeInTheDocument();
    fireEvent.click(getByTestId('submitButton'));

    expect(mockContinue).toHaveBeenCalled();
  });

  it('should render amend page', () => {
    mockedProps.isAmendPage = true;
    mockedProps.additionalAmountLabel = 'amend.balance.poaNew';

    const { getByText } = render(<TotalCostCard {...mockedProps} />);
    expect(getByText('amend.balance.poaNew')).toBeInTheDocument();
  });

  it('should render with payment details type reserve RESERVE_WITHOUT_CARD', () => {
    mockedProps.selectedPaymentDetail.type = 'RESERVE_WITHOUT_CARD';

    const { getByText } = render(<TotalCostCard {...mockedProps} />);
    expect(getByText('ccui.payment.confirmBooking.button')).toBeInTheDocument();
  });

  it('should render the error message', () => {
    mockedProps.isError = true;
    mockedProps.error.message = 'error';

    const { getByText } = render(<TotalCostCard {...mockedProps} />);
    expect(getByText('error')).toBeInTheDocument();
  });
});

describe('renderPrice', () => {
  it('should return the price in GBP for English language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: 'en', currency: 'GBP', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in RO for English language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: 'ro', currency: 'GBP', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in RO for English language and ro currency', () => {
    const result = renderPrice({ currentLanguage: 'ro', currency: 'RON', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in EUR for English language and EUR currency', () => {
    const result = renderPrice({ currentLanguage: 'en', currency: 'EUR', price: 100 });
    expect(result).toEqual('€100.00');
  });

  it('should return the price in RO for English language and EUR currency', () => {
    const result = renderPrice({ currentLanguage: 'ro', currency: 'EUR', price: 100 });
    expect(result).toEqual('€100.00');
  });

  it('should return the price in GBP for German language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: 'de', currency: 'GBP', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in RO for German language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: 'ro', currency: 'GBP', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in GBP for English language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: 'en', currency: 'RON', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price is empty DE for English language and empty currency', () => {
    const result = renderPrice({ currentLanguage: 'de', currency: '', price: 100 });
    expect(result).toEqual('100.00');
  });

  it('should return the price in GBP for default language and GBP currency', () => {
    const result = renderPrice({ currentLanguage: undefined, currency: 'GBP', price: 100 });
    expect(result).toEqual('£100.00');
  });

  it('should return the price in EUR for default language and EUR currency', () => {
    const result = renderPrice({ currentLanguage: undefined, currency: 'EUR', price: 100 });
    expect(result).toEqual('€100.00');
  });
});
