import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import PaymentType from './PaymentType.component';

const mockPaymentTypeData = {
  variant: Area.PI,
  onPaymentTypeClick: jest.fn(),
  isCCUI: false,
  selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
  selectedPaymentType: {
    name: 'PIBA',
    type: 'SAVED_CARD',
    order: 1,
    card: {
      token: '5667855671183870034',
      expiryMonth: '12',
      expiryYear: '23',
      type: 'MD',
      logoSrc: '',
      cardHolderName: 'Monica W',
      cardType: 'LEISURE_STORED_CARD',
      cnpRequired: false,
      cardNumber: 'XXXXXXXXXXXX1234',
    },
    paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
    enabled: true,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  },
  isLoading: false,
  isError: false,
  error: { message: 'Error message' },
  data: {
    paymentMethods: [
      {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'CARD',
        type: 'SAVED_CARD',
        order: 2,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: ['EXPIRED'],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
        ],
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Business Account card',
        type: 'NEW_PIBA',
        order: 4,
        acceptedCardTypes: [
          {
            name: 'Business Account',
            type: 'PI',
            logoSrc: '',
          },
        ],
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        paypalClientToken: '',
        paypalClientId: '',
        name: 'PAYPAL',
        type: 'PAYPAL',
        order: 5,
        enabled: true,
        cnpPreSelected: true,
        cnpOptionAvailable: true,
        acceptedCardTypes: [
          {
            type: 'PP',
            name: 'Business Account',
            logoSrc: '',
          },
        ],
        card: undefined,
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        reasons: [],
      },
      {
        name: 'Apple Pay / Google Pay',
        type: 'APGP',
        subType: '',
        order: 6,
        logoSrc: '',
        enabled: true,
        acceptedCardTypes: [
          {
            type: 'GP',
            name: 'Google Pay',
            logoSrc: '',
          },
          {
            type: 'AP',
            name: 'Apple Pay',
            logoSrc: '',
          },
        ],
        card: undefined,
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
    ],
  },
  t: (key: string) => {
    switch (key) {
      case 'cc.title':
        return 'Payment type';
      case 'cc.subTitle':
        return 'Payment will be handled by a secure third party.';
      default:
        return 'default';
    }
  },
};

const mockDataPayNowOnly = {
  variant: Area.PI,
  onPaymentTypeClick: jest.fn(),
  isCCUI: false,
  selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
  selectedPaymentType: {
    name: 'PIBA',
    type: 'SAVED_CARD',
    order: 1,
    card: {
      token: '5667855671183870034',
      expiryMonth: '12',
      expiryYear: '23',
      type: 'MD',
      logoSrc: '',
      cardHolderName: 'Monica W',
      cardType: 'LEISURE_STORED_CARD',
      cnpRequired: false,
      cardNumber: 'XXXXXXXXXXXX1234',
    },
    paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
    enabled: true,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  },
  isLoading: false,
  isError: false,
  error: { message: 'Error message' },
  data: {
    paymentMethods: [
      {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'CARD',
        type: 'SAVED_CARD',
        order: 2,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: ['EXPIRED'],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
        ],
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Business Account card',
        type: 'NEW_PIBA',
        order: 4,
        acceptedCardTypes: [
          {
            name: 'Business Account',
            type: 'PI',
            logoSrc: '',
          },
        ],
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        paypalClientToken: '',
        paypalClientId: '',
        name: 'PAYPAL',
        type: 'PAYPAL',
        order: 5,
        enabled: true,
        cnpPreSelected: true,
        cnpOptionAvailable: true,
        acceptedCardTypes: [
          {
            type: 'PP',
            name: 'Business Account',
            logoSrc: '',
          },
        ],
        card: undefined,
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        reasons: [],
      },
      {
        name: 'APPLE',
        type: 'APGP',
        subType: '',
        order: 6,
        logoSrc: '',
        enabled: true,
        acceptedCardTypes: [
          {
            type: 'AP',
            name: 'Apple Pay',
            logoSrc: '',
          },
        ],
        card: undefined,
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'GOOGLE',
        type: 'APGP',
        subType: '',
        order: 7,
        logoSrc: '',
        enabled: true,
        acceptedCardTypes: [
          {
            type: 'AP',
            name: 'Google Pay',
            logoSrc: '',
          },
        ],
        card: undefined,
        paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
    ],
  },
  t: (key: string) => {
    switch (key) {
      case 'cc.title':
        return 'Payment type';
      case 'cc.subTitle':
        return 'Payment will be handled by a secure third party.';
      default:
        return 'default';
    }
  },
};

const mockedAmendPaymentCard = {
  name: 'MasterCard Credit',
  type: 'AMEND_SAVED_CARD',
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
    cardNumber: 'XXXXXXXXXXXX1234',
  },
  paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
  enabled: true,
  cnpPreSelected: false,
  cnpOptionAvailable: false,
  reasons: [],
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => true,
  useFeatureToggle: jest.fn(() => ({
    'kill_switch_pi_enable-paypal': true,
  })),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('PaymentType', () => {
  it('should render PaymentType correctly when `isPaymentRedesignEnabled` is enabled', function () {
    const { getByText, getAllByRole } = render(
      <PaymentType {...{ ...mockPaymentTypeData, isPaymentRedesignEnabled: true }} />
    );

    expect(getByText('Payment type')).toBeInTheDocument();
    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(6);
  });

  it('should render PaymentType correctly when `isPaymentRedesignEnabled` is enabled', function () {
    const { getByText } = render(
      <PaymentType {...{ ...mockDataPayNowOnly, isPaymentRedesignEnabled: true }} />
    );

    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
  });

  it('should render PaymentType correctly', function () {
    const { getByText, getAllByRole } = render(<PaymentType {...mockPaymentTypeData} />);

    expect(getByText('Payment type')).toBeInTheDocument();
    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(6);
  });

  it('should render PaymentType correctly for pay now only options correctly', function () {
    const { getByText, getAllByRole } = render(<PaymentType {...mockDataPayNowOnly} />);

    expect(getByText('Payment type')).toBeInTheDocument();
    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(6);
  });

  it('should render PaymentType correctly for Tab component only options correctly', function () {
    const { getByText } = render(<PaymentType {...mockDataPayNowOnly} isUsedWithTabs={true} />);

    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
  });

  it('should check selected radio on render', function () {
    mockPaymentTypeData.selectedPaymentType = {
      name: 'CARD',
      type: 'SAVED_CARD',
      order: 2,
      card: {
        token: '5667855671183870034',
        expiryMonth: '12',
        expiryYear: '23',
        type: 'MD',
        logoSrc: '',
        cardHolderName: 'Monica W',
        cardType: 'LEISURE_STORED_CARD',
        cnpRequired: false,
        cardNumber: 'XXXXXXXXXXXX1234',
      },
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
      ],
      enabled: false,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const { getAllByRole } = render(<PaymentType {...mockPaymentTypeData} />);

    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
    expect(getAllByRole('radio').length).toBe(6);
  });

  it('CCUI - should check selected radio on render', function () {
    mockPaymentTypeData.isCCUI = true;
    mockPaymentTypeData.selectedPaymentType = {
      name: 'CARD',
      type: 'SAVED_CARD',
      order: 2,
      card: {
        token: '5667855671183870034',
        expiryMonth: '12',
        expiryYear: '23',
        type: 'MD',
        logoSrc: '',
        cardHolderName: 'Monica W',
        cardType: 'LEISURE_STORED_CARD',
        cnpRequired: false,
        cardNumber: 'XXXXXXXXXXXX1234',
      },
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: false },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: true },
      ],
      enabled: false,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const { getAllByRole } = render(<PaymentType {...mockPaymentTypeData} />);

    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
    expect(getAllByRole('radio').length).toBe(6);
  });

  it('should render loading spinner if isLoading true', function () {
    mockPaymentTypeData.isLoading = true;
    const { queryByTestId } = render(<PaymentType {...mockPaymentTypeData} />);

    expect(queryByTestId('loading')).toBeInTheDocument();
  });
  it('should show an error state', async () => {
    mockPaymentTypeData.isError = true;
    mockPaymentTypeData.error.message = 'Error loading payment methods.';

    const { getByText } = render(<PaymentType {...mockPaymentTypeData} />);
    expect(getByText('Error loading payment methods.')).toBeInTheDocument();
  });
  it('should select first payment type on first load', async () => {
    const onPaymentTypeClickMock = jest.fn();
    // mockPaymentTypeData.selectedPaymentType.name = '';
    render(<PaymentType {...mockPaymentTypeData} onPaymentTypeClick={onPaymentTypeClickMock} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalledTimes(1);
  });
  it('should select first the Amend Saved Card if there is an existing amendSavedCardPaymentType', async () => {
    const onPaymentTypeClickMock = jest.fn();
    // mockPaymentTypeData.selectedPaymentType.name = '';
    render(
      <PaymentType
        {...mockPaymentTypeData}
        amendPaymentCard={mockedAmendPaymentCard}
        onPaymentTypeClick={onPaymentTypeClickMock}
      />
    );
    expect(onPaymentTypeClickMock).toHaveBeenCalledWith(mockedAmendPaymentCard);
  });

  it('CCUI should select NEW_CARD when user selects PAY_ON_ARRIVAL and NEW_CARD supports it', () => {
    const onPaymentTypeClickMock = jest.fn();
    const ccuiBaseProps = {
      variant: Area.CCUI,
      isLoading: false,
      isError: false,
      error: { message: '' },
      t: (key: string) => key,
    };

    const selectedSavedCardMethod: any = {
      name: 'CARD',
      type: 'SAVED_CARD',
      subType: '',
      order: 2,
      card: {
        token: '5667855671183870034',
        expiryMonth: '12',
        expiryYear: '23',
        type: 'MD',
        logoSrc: '',
        cardHolderName: 'Monica W',
        cardType: 'LEISURE_STORED_CARD',
        cnpRequired: false,
        cardNumber: 'XXXXXXXXXXXX1234',
      },
      paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const newCard: any = {
      name: 'New Credit / Debit card',
      type: 'NEW_CARD',
      subType: '',
      order: 3,
      acceptedCardTypes: [],
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: true },
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    // SAVED_CARD is selected but user has chosen PAY_ON_ARRIVAL which SAVED_CARD doesn't support.
    // NEW_CARD supports PAY_ON_ARRIVAL so CCUI should auto-switch to it.
    const { rerender } = render(
      <PaymentType
        {...ccuiBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={selectedSavedCardMethod}
        selectedPaymentDetail={{ type: 'PAY_NOW', order: 1, enabled: true }}
        data={{ paymentMethods: [selectedSavedCardMethod, newCard] }}
      />
    );

    onPaymentTypeClickMock.mockClear();

    // User switches selectedPaymentDetail to PAY_ON_ARRIVAL - CCUI effect detects
    // that SAVED_CARD doesn't support it and switches to NEW_CARD which does.
    rerender(
      <PaymentType
        {...ccuiBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={selectedSavedCardMethod}
        selectedPaymentDetail={{ type: 'PAY_ON_ARRIVAL', order: 2, enabled: true }}
        data={{ paymentMethods: [selectedSavedCardMethod, newCard] }}
      />
    );

    expect(onPaymentTypeClickMock).toHaveBeenCalledWith(newCard);
  });

  it('CCUI should refresh selectedPaymentType when its payment options change after upgrade', () => {
    const onPaymentTypeClickMock = jest.fn();
    const ccuiBaseProps = {
      variant: Area.CCUI,
      isLoading: false,
      isError: false,
      error: { message: '' },
      t: (key: string) => key,
    };

    const newCardBeforeUpgrade: any = {
      name: 'New Credit / Debit card',
      type: 'NEW_CARD',
      subType: '',
      order: 1,
      acceptedCardTypes: [],
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const newCardAfterUpgrade: any = {
      ...newCardBeforeUpgrade,
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: true },
      ],
    };

    // Render with NEW_CARD selected (PAY_ON_ARRIVAL disabled)
    const { rerender } = render(
      <PaymentType
        {...ccuiBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={newCardBeforeUpgrade}
        selectedPaymentDetail={{ type: 'PAY_NOW', order: 1, enabled: true }}
        data={{ paymentMethods: [newCardBeforeUpgrade] }}
      />
    );

    onPaymentTypeClickMock.mockClear();

    // Rerender with upgraded data (PAY_ON_ARRIVAL now enabled)
    rerender(
      <PaymentType
        {...ccuiBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={newCardBeforeUpgrade} // Still has old options
        selectedPaymentDetail={{ type: 'PAY_NOW', order: 1, enabled: true }}
        data={{ paymentMethods: [newCardAfterUpgrade] }} // Fresh data with new options
      />
    );

    // Should refresh selectedPaymentType with the fresh payment options
    expect(onPaymentTypeClickMock).toHaveBeenCalledWith(newCardAfterUpgrade);
  });

  it('BB should not revert to the centrally stored card when the personal stored card is selected', () => {
    const onPaymentTypeClickMock = jest.fn();
    const bbBaseProps = {
      variant: Area.BB,
      isLoading: false,
      isError: false,
      error: { message: '' },
      t: (key: string) => key,
      selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
    };

    const centrallyStoredCard: any = {
      name: 'PIBA',
      type: 'SAVED_CARD',
      subType: '',
      order: 1,
      card: {
        token: 'centrally-stored-token',
        expiryMonth: '12',
        expiryYear: '30',
        type: 'MD',
        logoSrc: '',
        cardHolderName: 'Monica W',
        cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
        cnpRequired: false,
        cardNumber: 'XXXXXXXXXXXX1234',
      },
      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: true },
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const personalStoredCard: any = {
      ...centrallyStoredCard,
      order: 2,
      card: {
        ...centrallyStoredCard.card,
        token: 'personal-stored-token',
        cardType: 'BUSINESS_PERSONAL_STORED_CARD',
        cardNumber: 'XXXXXXXXXXXX5678',
      },

      paymentOptions: [
        { type: 'PAY_NOW', order: 1, enabled: true },
        { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
      ],
    };

    const data = { paymentMethods: [centrallyStoredCard, personalStoredCard] };

    const { rerender } = render(
      <PaymentType
        {...bbBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={centrallyStoredCard}
        data={data}
      />
    );

    onPaymentTypeClickMock.mockClear();

    rerender(
      <PaymentType
        {...bbBaseProps}
        onPaymentTypeClick={onPaymentTypeClickMock}
        selectedPaymentType={personalStoredCard}
        data={data}
      />
    );

    expect(onPaymentTypeClickMock).not.toHaveBeenCalledWith(centrallyStoredCard);
    expect(onPaymentTypeClickMock).not.toHaveBeenCalled();
  });
});
describe('PIBA Euro PaymentType testing', () => {
  it('should check for DE subType and isPibaNotAllowedForEUHotel', async () => {
    const mockPaymentTypeDataDE = {
      variant: Area.BB,
      onPaymentTypeClick: jest.fn(),
      isCCUI: false,
      selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
      selectedPaymentType: {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [
          { type: 'PAY_NOW', order: 1, enabled: true },
          { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      isLoading: false,
      isError: false,
      error: { message: 'Error message' },
      data: {
        paymentMethods: [
          {
            name: 'PIBA',
            type: 'SAVED_CARD',
            order: 1,
            card: {
              token: '5667855671183870034',
              expiryMonth: '12',
              expiryYear: '23',
              type: 'MD',
              logoSrc: '',
              cardHolderName: 'Monica W',
              cardType: 'LEISURE_STORED_CARD',
              cnpRequired: false,
              cardNumber: 'XXXXXXXXXXXX1234',
            },
            paymentOptions: [
              { type: 'PAY_NOW', order: 1, enabled: true },
              { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
            ],
            enabled: true,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            reasons: ['CARD_NOT_ACCEPTED_AT_HOTEL', 'PIBA_EU_ALLOWED_ONLY_IN_EU'],
          },
          {
            name: 'PIBA',
            type: 'NEW_PIBA',
            order: 4,
            subType: 'PIBADE',
            acceptedCardTypes: [
              {
                name: 'Business Account',
                type: 'PI',
                logoSrc: '',
              },
            ],
            paymentOptions: [
              { type: 'PAY_NOW', order: 1, enabled: true },
              { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
            ],
            enabled: true,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            reasons: [],
          },
        ],
      },
      t: (key: string) => {
        switch (key) {
          case 'cc.NEW_PIBA_EURO.info':
            return 'An InnBusiness Pay card will be required to pay for this stay';
          case 'cc.na.CARD_NOT_ACCEPTED_AT_HOTEL':
            return 'Your stored card ending in "{cardEnding}" is not accepted at this hotel';
          case 'cc.na.PIBA_EU_ALLOWED_ONLY_IN_EU':
            return 'Your stored card ending in "{cardEnding}" cannot be used for stays in the UK as it is a Euro Business Card';
          default:
            return 'default';
        }
      },
    };
    const onPaymentTypeClickMock = jest.fn();
    // mockPaymentTypeData.selectedPaymentType.name = '';
    render(<PaymentType {...mockPaymentTypeDataDE} onPaymentTypeClick={onPaymentTypeClickMock} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalledTimes(1);
  });
  it('should check for GB subType and isPibaNotAllowedForUKHotel ', async () => {
    const mockPaymentTypeDataDE = {
      variant: Area.BB,
      onPaymentTypeClick: jest.fn(),
      isCCUI: false,
      selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
      selectedPaymentType: {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
          cardNumber: 'XXXXXXXXXXXX1234',
        },
        paymentOptions: [
          { type: 'PAY_NOW', order: 1, enabled: true },
          { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      isLoading: false,
      isError: false,
      error: { message: 'Error message' },
      data: {
        paymentMethods: [
          {
            name: 'PIBA',
            type: 'SAVED_CARD',
            order: 1,
            card: {
              token: '5667855671183870034',
              expiryMonth: '12',
              expiryYear: '23',
              type: 'MD',
              logoSrc: '',
              cardHolderName: 'Monica W',
              cardType: 'LEISURE_STORED_CARD',
              cnpRequired: false,
              cardNumber: 'XXXXXXXXXXXX1234',
            },
            paymentOptions: [
              { type: 'PAY_NOW', order: 1, enabled: true },
              { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
            ],
            enabled: true,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            reasons: ['CARD_NOT_ACCEPTED_AT_HOTEL', 'PIBA_UK_ALLOWED_ONLY_IN_UK'],
          },
          {
            name: 'PIBA',
            type: 'NEW_PIBA',
            order: 4,
            subType: 'PIBAGB',
            acceptedCardTypes: [
              {
                name: 'Business Account',
                type: 'PI',
                logoSrc: '',
              },
            ],
            paymentOptions: [
              { type: 'PAY_NOW', order: 1, enabled: true },
              { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
            ],
            enabled: true,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            reasons: [],
          },
        ],
      },
      t: (key: string) => {
        switch (key) {
          case 'cc.NEW_PIBA.info':
            return 'A UK Business Account Card will be required to pay for this stay';
          case 'cc.na.CARD_NOT_ACCEPTED_AT_HOTEL':
            return 'Your stored card ending in "{cardEnding}" is not accepted at this hotel';
          case 'cc.na.PIBA_UK_ALLOWED_ONLY_IN_UK':
            return 'Your stored card ending in "{cardEnding}" cannot be used for stays in Germany as it is a UK Business Card';
          default:
            return 'default';
        }
      },
    };
    const onPaymentTypeClickMock = jest.fn();
    // mockPaymentTypeData.selectedPaymentType.name = '';
    render(<PaymentType {...mockPaymentTypeDataDE} onPaymentTypeClick={onPaymentTypeClickMock} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalledTimes(1);
  });
});
describe('Payment Redesign - Coverage Improved', () => {
  it('should call onPaymentTypeClick for first enabled method if selectedPaymentType.name is empty', () => {
    const onPaymentTypeClickMock = jest.fn();
    const customData = {
      ...mockPaymentTypeData,
      selectedPaymentType: { ...mockPaymentTypeData.selectedPaymentType, name: '' },
      onPaymentTypeClick: onPaymentTypeClickMock,
      variant: Area.CCUI,
    };
    render(<PaymentType {...customData} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalled();
  });

  it('should call onPaymentTypeClick for first enabled method if variant is PI', () => {
    const onPaymentTypeClickMock = jest.fn();
    const customData = {
      ...mockPaymentTypeData,
      selectedPaymentType: { ...mockPaymentTypeData.selectedPaymentType, name: '' },
      onPaymentTypeClick: onPaymentTypeClickMock,
      variant: Area.PI,
    };
    render(<PaymentType {...customData} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalled();
  });

  it('should call onPaymentTypeClick for first enabled method if variant is BB', () => {
    const onPaymentTypeClickMock = jest.fn();
    const customData = {
      ...mockPaymentTypeData,
      selectedPaymentType: { ...mockPaymentTypeData.selectedPaymentType, name: '' },
      onPaymentTypeClick: onPaymentTypeClickMock,
      variant: Area.BB,
    };
    render(<PaymentType {...customData} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalled();
  });

  it('should call onPaymentTypeClick for initialPaymentType', () => {
    const onPaymentTypeClickMock = jest.fn();
    const customData = {
      ...mockPaymentTypeData,
      initialPaymentType: 'SAVED_CARD',
      onPaymentTypeClick: onPaymentTypeClickMock,
    };
    render(<PaymentType {...customData} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalled();
  });
});
