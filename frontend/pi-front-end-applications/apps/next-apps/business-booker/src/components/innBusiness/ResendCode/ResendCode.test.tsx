import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';
import { Scheme, LOCALES } from '@whitbread-eos/api';

import { ResendCode } from './ResendCode';

const mockProps = {
  scheme: 'GB' as Scheme,
  tetheredGuid: '2242efb5-dd13-4307-94e3-8b83daedaa1b',
  cardId: '12345',
  open: true,
  onOpenChange: jest.fn(),
  icons: {
    'icon.notification.success': '/path/to/success-icon.svg',
  },
};

const mockPush = jest.fn();

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: mockPush,
    refresh: jest.fn(),
  })),
  usePathname: () => {
    return '/';
  },
}));

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    useTranslation: () => ({
      t: (key: string) => {
        const translations: Record<string, string> = {
          'cards.cardMgmt.cardDetails.resend.code.title': 'Send registration code to card holder',
          'cards.cardMgmt.columns.cardHolderName': 'Card holder',
          'cards.cardMgmt.cardDetails.resend.code.invite.copy':
            'Send me a copy of the invite email',
          'cards.cardMgmt.cardDetails.editCard.replaceCard.cancel': 'Cancel',
          'cards.cardMgmt.cardDetails.resend.code.button': 'Resend registration code',
        };
        return translations[key] || key;
      },
    }),
    cn: jest.fn(),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    resendCodeMutation: jest.fn(),
    getPIBACardDetails: jest.fn().mockReturnValue(Promise.resolve({ cardId: '123' })),
    findError: serverUtils.findError,
  };
});

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
}));

describe('ResendCode Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should render ResendCode component and close it', async () => {
    const { getByTestId, getByText } = render(<ResendCode {...mockProps} />);
    await waitFor(async () => {
      expect(getByTestId('Resend-Code-Dialog')).toBeInTheDocument();
      expect(getByText('Send registration code to card holder')).toBeInTheDocument();
      expect(getByText('Card holder')).toBeInTheDocument();
      expect(getByText('Send me a copy of the invite email')).toBeInTheDocument();
      expect(getByText('Cancel')).toBeInTheDocument();
      expect(getByText('Resend registration code')).toBeInTheDocument();
      const closeButton = getByTestId('ResendCodeDialog-Cancel-Button');
      expect(closeButton).toBeInTheDocument();
      fireEvent.click(closeButton);
    });
  });

  it('should render ResendCode component and resend code button', async () => {
    const { getByTestId } = render(<ResendCode {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('Resend-Code-Dialog')).toBeInTheDocument();
      const resendCodeButton = getByTestId('ResendCodeDialog-Resend-Code-Button');
      expect(resendCodeButton).toBeInTheDocument();
      fireEvent.click(resendCodeButton);
    });
  });
});
