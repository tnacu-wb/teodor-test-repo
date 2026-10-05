import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { render, screen } from '../../utils/test-utils';
import PaymentTypeLed from './PaymentTypeLed.component';

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
        ],
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
        ],
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
        ],
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
        ],
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
        card: null,
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: true,
          },
        ],
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
        card: null,
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
        ],
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
    },
    paymentOptions: [
      {
        type: 'PAY_NOW',
        order: 1,
        enabled: true,
      },
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
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        card: null,
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        card: null,
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
        card: null,
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
        ],
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
    cardNumber: 'XXXXXXXXXXXX1100',
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
      enabled: true,
    },
  ],
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

describe('PaymentTypeLed', () => {
  beforeAll(() => {
    jest.clearAllMocks();
  });

  it('should render PaymentTypeLed correctly', function () {
    const { queryByTestId, getAllByRole } = render(<PaymentTypeLed {...mockPaymentTypeData} />);

    expect(queryByTestId('payment-type-method_title')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(6);
  });

  it('should render PaymentTypeLed correctly for pay now only options', function () {
    const { queryByTestId, getAllByRole } = render(<PaymentTypeLed {...mockDataPayNowOnly} />);

    expect(queryByTestId('payment-type-method_title')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(6);
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
      ],
      enabled: false,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    const { getAllByRole } = render(<PaymentTypeLed {...mockPaymentTypeData} />);

    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
    expect(getAllByRole('radio').length).toBe(6);
  });

  it('should check APGP is selected radio on render', function () {
    mockPaymentTypeData.selectedPaymentType = {
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
      card: null,
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
      ],
      reasons: [],
    };

    const { getAllByRole } = render(<PaymentTypeLed {...mockPaymentTypeData} />);

    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[5]).toBeChecked();
    expect(getAllByRole('radio')[2]).not.toBeChecked();
    expect(getAllByRole('radio').length).toBe(6);
  });

  it('should render loading spinner if isLoading true', function () {
    mockPaymentTypeData.isLoading = true;
    const { queryByTestId } = render(<PaymentTypeLed {...mockPaymentTypeData} />);

    expect(queryByTestId('loading')).toBeInTheDocument();
  });

  it('should show an error state', async () => {
    mockPaymentTypeData.isError = true;
    mockPaymentTypeData.error.message = 'Error loading payment methods.';

    const { getByText } = render(<PaymentTypeLed {...mockPaymentTypeData} />);
    expect(getByText('Error loading payment methods.')).toBeInTheDocument();
  });

  it('displays error message when isError is true', () => {
    const error = new Error('Test error');
    render(<PaymentTypeLed {...mockPaymentTypeData} isError={true} error={error} />);
    expect(screen.getByText(error.message)).toBeInTheDocument();
  });

  it('should select first payment type on first load', async () => {
    const onPaymentTypeClickMock = jest.fn();
    // mockPaymentTypeData.selectedPaymentType.name = '';
    render(<PaymentTypeLed {...mockPaymentTypeData} onPaymentTypeClick={onPaymentTypeClickMock} />);
    expect(onPaymentTypeClickMock).toHaveBeenCalledTimes(1);
  });
  it('should select first the Amend Saved Card if there is an existing amendSavedCardPaymentType', async () => {
    const onPaymentTypeClickMock = jest.fn();
    render(
      <PaymentTypeLed
        {...mockPaymentTypeData}
        amendPaymentCard={mockedAmendPaymentCard}
        onPaymentTypeClick={onPaymentTypeClickMock}
      />
    );
    expect(onPaymentTypeClickMock).toHaveBeenCalledWith(mockedAmendPaymentCard);
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
    render(
      <PaymentTypeLed {...mockPaymentTypeDataDE} onPaymentTypeClick={onPaymentTypeClickMock} />
    );
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
    render(
      <PaymentTypeLed {...mockPaymentTypeDataDE} onPaymentTypeClick={onPaymentTypeClickMock} />
    );
    expect(onPaymentTypeClickMock).toHaveBeenCalledTimes(1);
  });
});
