import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { getPreselectedCard, PaymentCardForm } from './PaymentCardForm';

const mockProps = {
  onSubmit: jest.fn(),
  cards: [
    {
      cardId: 'a',
      cardLabel: 'a',
      cardType: 'a',
      cardNumber: 'a',
    },
  ],
  icons: {},
  locale: LOCALES.EN,
  formRef: { current: document.createElement('form') },
} as any;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    findError: serverUtils.findError,
  };
});

describe('PaymentCardForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render PaymentCardForm component and not find card', async () => {
    const { getByTestId } = render(<PaymentCardForm {...mockProps} />);

    expect(getByTestId('PaymentCardForm')).toBeInTheDocument();
  });

  it('should render PaymentCardForm component with option none', async () => {
    mockProps.selectedCardId = '1';
    const { getByTestId } = render(<PaymentCardForm {...mockProps} />);

    expect(getByTestId('PaymentCardForm')).toBeInTheDocument();
  });

  it('should render PaymentCardForm component and find card selected', async () => {
    mockProps.selectedCardId = 'a';
    const { getByTestId } = render(<PaymentCardForm {...mockProps} />);

    expect(getByTestId('PaymentCardForm')).toBeInTheDocument();
  });

  it('should render PaymentCardForm component and not find card selected', async () => {
    mockProps.selectedCardId = 'b';
    const { getByTestId } = render(<PaymentCardForm {...mockProps} />);

    expect(getByTestId('PaymentCardForm')).toBeInTheDocument();
  });

  it('should not render payment type components', async () => {
    const { queryByTestId } = render(<PaymentCardForm {...mockProps} />);

    expect(queryByTestId('Payment-Type-Heading')).not.toBeInTheDocument();
    expect(queryByTestId('Payment-Type-Form-Input')).not.toBeInTheDocument();
  });
});

describe('getPreselectedCard', () => {
  const cards = [
    { cardId: 'a', cardLabel: 'Card A', cardType: 'visa', cardNumber: '12345678' },
    { cardId: 'b', cardLabel: 'Card B', cardType: 'mastercard', cardNumber: '87654321' },
  ] as any;

  it('returns NONE_CARD_ID if paymentTypeOnly is true', () => {
    expect(getPreselectedCard(cards, 'a', true)).toBe('1');
  });

  it('returns selectedCardId if it exists in cards', () => {
    expect(getPreselectedCard(cards, 'a', false)).toBe('a');
  });

  it('returns NONE_CARD_ID if selectedCardId does not exist in cards', () => {
    expect(getPreselectedCard(cards, 'c', false)).toBe('1');
  });

  it('returns NONE_CARD_ID if selectedCardId is undefined', () => {
    expect(getPreselectedCard(cards, undefined, false)).toBe('1');
  });

  it('returns NONE_CARD_ID if cards is empty', () => {
    expect(getPreselectedCard([], 'a', false)).toBe('1');
  });
});
