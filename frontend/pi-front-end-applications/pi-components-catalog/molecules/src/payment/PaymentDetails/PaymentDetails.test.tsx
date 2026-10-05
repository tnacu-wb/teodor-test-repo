import PaymentDetails from '.';
import '@testing-library/jest-dom';
import { HotelBrand, FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
}));

const mockPaymentDetails = {
  selectedPaymentType: {
    name: 'PIBA',
    type: 'SAVED_CARD',
    order: 1,
    card: {
      token: '5667855671183870034',
      expiryMonth: '12',
      expiryYear: '23',
      type: 'AT',
      logoSrc: '',
      cardHolderName: 'Monica W',
      cardType: 'LEISURE_STORED_CARD',
      cnpRequired: false,
    },
    paymentOptions: [
      {
        type: 'PAY_NOW',
        order: 1,
        enabled: true,
      },
      {
        type: 'PAY_ON_ARRIVAL',
        order: 2,
        enabled: false,
      },
      {
        type: 'RESERVE_WITHOUT_CARD',
        order: 4,
        enabled: true,
      },
    ],
    enabled: true,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  },
  selectedPaymentDetail: {
    type: 'PAY_NOW',
    order: 1,
    enabled: true,
  },
  setSelectedPaymentDetail: jest.fn(),
  errorMessagePayment: undefined as string | undefined,
  t: (key: string) => {
    switch (key) {
      case 'paymentOptions.title':
        return 'Please choose when you’d like to pay for your booking. If you’d like to pay on arrival, we’ll need your card details to reserve your booking now, but no payment will be taken. You may be redirected to your bank or card provider to verify your details.';
      case 'booking.payment.saverHeading':
        return 'booking.payment.saverHeading';
      default:
        return key;
    }
  },
  disablePaymentOptions: false,
};

describe('PaymentDetails', () => {
  beforeEach(() => {
    // Default mock for useFeatureToggle
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: false,
    });
  });

  it('should render PaymentDetails corectly', function () {
    const { getByText, getAllByRole, getByTestId } = render(
      <PaymentDetails {...mockPaymentDetails} />
    );

    expect(getByText('booking.payment.saverHeading')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(3);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).toBeDisabled();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();

    expect(getByTestId('CardDetails-payment-option-PAY_NOW-desc')).toBeInTheDocument();
    expect(getByTestId('CardDetails-payment-option-PAY_ON_ARRIVAL-desc')).toBeInTheDocument();
    expect(getByTestId('CardDetails-payment-option-RESERVE_WITHOUT_CARD-desc')).toBeInTheDocument();
  });

  it('should be selected pay on arrival by default when all payment options are enabled', function () {
    mockPaymentDetails.selectedPaymentType.paymentOptions[0].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[1].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[2].enabled = true;

    const { getAllByRole } = render(<PaymentDetails {...mockPaymentDetails} />);

    expect(getAllByRole('radio')).toHaveLength(3);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
  });

  it('should be checked by default pay on arrival when it is enabled and the user has not selected anything else', function () {
    mockPaymentDetails.selectedPaymentDetail = {
      type: 'default',
      order: 1,
      enabled: false,
    };
    mockPaymentDetails.selectedPaymentType.paymentOptions[0].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[1].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[2].enabled = true;
    const { getAllByRole } = render(<PaymentDetails {...mockPaymentDetails} />);

    expect(getAllByRole('radio')).toHaveLength(3);
    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
  });

  it('should be checked by default first enabled option when pay on arrival is diasbled and the user has not selected anything else', function () {
    mockPaymentDetails.selectedPaymentDetail = {
      type: 'default',
      order: 1,
      enabled: false,
    };
    mockPaymentDetails.selectedPaymentType.paymentOptions[0].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[1].enabled = false;
    mockPaymentDetails.selectedPaymentType.paymentOptions[2].enabled = true;
    const { getAllByRole } = render(<PaymentDetails {...mockPaymentDetails} />);

    expect(getAllByRole('radio')).toHaveLength(3);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
  });

  it('should be checked by default first enabled option when pay on arrival is diasbled and the user has not selected anything else', function () {
    mockPaymentDetails.selectedPaymentDetail = {
      type: 'default',
      order: 1,
      enabled: false,
    };
    mockPaymentDetails.selectedPaymentType.paymentOptions[0].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[1].enabled = false;
    mockPaymentDetails.selectedPaymentType.paymentOptions[2].enabled = true;
    const { getAllByRole } = render(
      <PaymentDetails {...{ ...mockPaymentDetails, isPaymentRedesignEnabled: true }} />
    );

    expect(getAllByRole('radio')).toHaveLength(3);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
  });

  it('should be checked by first enabled option when default option doesnt not exist and is diasbled and the user has not selected anything else', function () {
    mockPaymentDetails.selectedPaymentDetail = {
      type: 'error',
      order: 1,
      enabled: false,
    };

    mockPaymentDetails.setSelectedPaymentDetail;
    mockPaymentDetails.selectedPaymentType.paymentOptions[0].enabled = true;
    mockPaymentDetails.selectedPaymentType.paymentOptions[1].enabled = false;
    mockPaymentDetails.selectedPaymentType.paymentOptions[2].enabled = true;
    render(<PaymentDetails {...mockPaymentDetails} />);

    expect(mockPaymentDetails.setSelectedPaymentDetail).toHaveBeenCalledWith({
      type: 'PAY_NOW',
      order: 1,
      enabled: true,
    });
  });

  it('should display error notification when we receive errors from ConfirmationMutation/PaymentMutationData', function () {
    mockPaymentDetails.errorMessagePayment =
      'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101';
    const { getByTestId, getByText } = render(<PaymentDetails {...mockPaymentDetails} />);

    expect(getByTestId('Payment-Error-Alert')).toBeInTheDocument();
    expect(
      getByText(
        'This transaction was declined. If you would like this matter discussed or reviewed, please contact our Central Reservations Team on +44 3330038101'
      )
    ).toBeInTheDocument();
  });

  it('should display notification when we receive disablePaymentOptions during planet outage', function () {
    mockPaymentDetails.disablePaymentOptions = true;
    const { getByText } = render(<PaymentDetails {...mockPaymentDetails} />);

    expect(getByText('paymentOptions.RESERVE_WITHOUT_CARD_NOTIFICATION')).toBeInTheDocument();
  });

  it('should display GB sub text for RWC GB Hotels', function () {
    mockPaymentDetails.disablePaymentOptions = true;
    const { getByText } = render(
      <PaymentDetails {...{ ...mockPaymentDetails, hotelBrand: HotelBrand.PI }} />
    );

    expect(getByText('paymentOptions.RESERVE_WITHOUT_CARD_DESC_GB')).toBeInTheDocument();
  });

  it('should render payment options with correct enabled states for CCUI', () => {
    const { getAllByRole } = render(
      <PaymentDetails {...mockPaymentDetails} isCCUI={true} isA2cPaymentPage={false} />
    );

    const radioButtons = getAllByRole('radio');
    expect(radioButtons).toHaveLength(3);
    // PAY_NOW enabled
    expect(radioButtons[0]).not.toBeDisabled();
    // PAY_ON_ARRIVAL disabled
    expect(radioButtons[1]).toBeDisabled();
    // RESERVE_WITHOUT_CARD enabled
    expect(radioButtons[2]).not.toBeDisabled();
  });

  describe('Container visibility based on isHubPOAEnabled', () => {
    beforeEach(() => {
      mockPaymentDetails.disablePaymentOptions = false;
    });

    it('should display Container when hotelBrand is not HUB', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: false,
      });

      const { getByTestId } = render(
        <PaymentDetails {...mockPaymentDetails} hotelBrand={HotelBrand.PI} />
      );

      const container = getByTestId('CardDetails-Container');
      expect(container).toBeInTheDocument();
      expect(container).toHaveStyle({ display: 'block' });
    });

    it('should display Container when isHubPOAEnabled is true and hotelBrand is HUB', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: true,
      });

      const { getByTestId } = render(
        <PaymentDetails {...mockPaymentDetails} hotelBrand={HotelBrand.HUB} />
      );

      const container = getByTestId('CardDetails-Container');
      expect(container).toBeInTheDocument();
      expect(container).toHaveStyle({ display: 'block' });
    });

    it('should hide Container when isHubPOAEnabled is false and hotelBrand is HUB', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: false,
      });

      const { getByTestId } = render(
        <PaymentDetails {...mockPaymentDetails} hotelBrand={HotelBrand.HUB} />
      );

      const container = getByTestId('CardDetails-Container');
      expect(container).toBeInTheDocument();
      expect(container).toHaveStyle({ display: 'none' });
    });

    it('should display Container when disablePaymentOptions is true even if hotelBrand is HUB and isHubPOAEnabled is false', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: false,
      });
      mockPaymentDetails.disablePaymentOptions = true;

      const { getByTestId } = render(
        <PaymentDetails {...mockPaymentDetails} hotelBrand={HotelBrand.HUB} />
      );

      const container = getByTestId('CardDetails-Container');
      expect(container).toBeInTheDocument();
      expect(container).toHaveStyle({ display: 'block' });
    });
  });
});
