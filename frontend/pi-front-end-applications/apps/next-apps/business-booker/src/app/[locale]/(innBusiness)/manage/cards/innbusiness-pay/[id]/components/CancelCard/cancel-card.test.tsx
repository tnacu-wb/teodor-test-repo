import '@testing-library/jest-dom';
import { render, act, fireEvent } from '@testing-library/react';
import { Scheme, LOCALES } from '@whitbread-eos/api';

import { CancelCard } from './cancel-card';

const mockProps = {
  locale: LOCALES.EN,
  scheme: 'GB' as Scheme,
  tetheredUserId: '2242efb5-dd13-4307-94e3-8b83daedaa1b',
  cardId: '12345',
  open: true,
  onOpenChange: jest.fn(),
  icons: {
    'icon.notification.alert': '/path/to/error-icon.svg',
  },
};

const mockPush = jest.fn();

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: mockPush,
    refresh: jest.fn(),
  })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'cards.cardMgmt.cardDetails.cancel.title': 'Cancel card',
        'cards.cardMgmt.cardDetails.cancelCard.title':
          'This action cannot be undone.If your card has been lost or stolen, or your employee will no longer need it, this card will be blocked.',
        'cards.cardMgmt.cardDetails.cancelCard.info':
          'Bookings made on their original card will need to be updated with their new card details. Otherwise, upcoming bookings will not be pre-authorised, and payment will be required at check-in.',
        'cards.cardMgmt.cardDetails.editCard.replaceCard.cancel': 'Cancel',
        'cards.cardMgmt.cardDetails.cancelCard.button': 'Cancel card',
      };
      return translations[key] || key;
    },
  }),
  formatIBAssetsUrl: () => {
    return '/';
  },
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  getAuthCookie: jest.fn(),
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
  renderSanitizedHtml: jest.fn((html: string) => html),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cancelAndReplacePIBACardMutation: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
}));

describe('CancelCard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should render CancelCard component and close it', async () => {
    const { getByTestId, getByText, getAllByText } = render(<CancelCard {...mockProps} />);
    expect(getAllByText('Cancel card')).toHaveLength(2);
    expect(
      getByText(
        'This action cannot be undone.If your card has been lost or stolen, or your employee will no longer need it, this card will be blocked.'
      )
    ).toBeInTheDocument();
    expect(
      getByText(
        'Bookings made on their original card will need to be updated with their new card details. Otherwise, upcoming bookings will not be pre-authorised, and payment will be required at check-in.'
      )
    ).toBeInTheDocument();
    expect(getByTestId('Cancel-Card-Dialog')).toBeInTheDocument();

    const closeButton = getByTestId('CancelCardDialog-Cancel-Button');
    expect(closeButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(closeButton);
    });
  });

  it('should render CancelCard component and cancel card button', async () => {
    const { getByTestId } = render(<CancelCard {...mockProps} />);

    expect(getByTestId('Cancel-Card-Dialog')).toBeInTheDocument();

    const cancelCardButton = getByTestId('CancelCardDialog-Cancel-Card-Button');
    expect(cancelCardButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(cancelCardButton);
    });
  });
});
