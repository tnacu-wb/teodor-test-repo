import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Scheme, LOCALES } from '@whitbread-eos/api';

import { CardHolderRegistered } from './card-holder-registered';

const mockProps = {
  registered: true,
  scheme: 'GB' as Scheme,
  tetheredGuid: '2242efb5-dd13-4307-94e3-8b83daedaa1b',
  cardId: '12345',
  open: true,
  onResendingCode: jest.fn(),
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
  usePathname: () => {
    return '/';
  },
}));

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
    cn: jest.fn(),
    useTranslation: () => ({
      t: (key: string) => {
        const translations: Record<string, string> = {
          'cardMgmt.cardHolder.options.registered': 'Registered',
          'cardMgmt.columns.resendCode': 'Resend code',
        };
        return translations[key] || key;
      },
    }),
  };
});

describe('CardHolderRegistered Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CardHolderRegistered component', () => {
    const { getByTestId, getByText } = render(<CardHolderRegistered {...mockProps} />);

    expect(getByTestId('CardHolderRegistered')).toBeInTheDocument();
    expect(getByText('Registered')).toBeInTheDocument();
  });

  it('should render CardHolderRegistered component with registered false', () => {
    mockProps.registered = false;
    const { getByTestId, getByText } = render(<CardHolderRegistered {...mockProps} />);

    expect(getByTestId('CardHolderRegistered')).toBeInTheDocument();
    expect(getByText('Resend code')).toBeInTheDocument();
  });
});
