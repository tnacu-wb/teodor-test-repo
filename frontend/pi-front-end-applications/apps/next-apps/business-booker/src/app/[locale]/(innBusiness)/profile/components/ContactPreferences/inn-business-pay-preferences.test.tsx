import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { InnBusinessPayPreferences } from './inn-business-pay-preferences';

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url || '',
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  WorldlineLink: jest.fn(
    ({
      children,
      'aria-label': ariaLabel,
      'data-testid': testId,
    }: {
      children: React.ReactNode;
      'aria-label': string;
      'data-testid': string;
    }) => (
      <button data-testid={testId} aria-label={ariaLabel}>
        {children}
      </button>
    )
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({
    src,
    alt,
    width,
    height,
    'aria-hidden': ariaHidden,
  }: {
    src: string;
    alt: string;
    width: number;
    height: number;
    'aria-hidden': boolean;
  }) => (
    <img
      src={src}
      alt={alt}
      width={width}
      height={height}
      aria-hidden={ariaHidden}
      data-testid="next-image"
    />
  ),
}));

describe('InnBusinessPayPreferences', () => {
  const baseProps = {
    tetheredGuid: 'test-guid',
    worldlinePostUrl: 'https://test-post-url.com',
    worldlineReturnUrl: 'https://test-return-url.com',
    icons: {
      'icon.manageEmployees-icon': '/icons/manage.svg',
    },
    accountLabels: [],
  };

  test('renders basic account information without preferences', () => {
    const props = {
      ...baseProps,
      accountName: 'Test Account',
      accountNumber: '1234-5678',
      accountLabels: ['account holder', 'finance user'],
    };

    render(<InnBusinessPayPreferences {...props} />);

    expect(screen.getByText('Test Account')).toBeInTheDocument();
    expect(screen.getByText('1234-5678')).toBeInTheDocument();

    const labels = screen.getByTestId('InnBusinessPayPreferences-AccountLabels');
    expect(labels).toBeInTheDocument();
    expect(labels.children).toHaveLength(2);
    expect(screen.getByText('account holder')).toBeInTheDocument();
    expect(screen.getByText('finance user')).toBeInTheDocument();

    const editLink = screen.getByTestId('InnBusinessPayPreferences-EditLink');
    expect(editLink).toBeInTheDocument();
    expect(editLink).toHaveAttribute(
      'aria-label',
      'innBusinessPay.preferences.button.edit Test Account'
    );

    const editIcon = screen.getByTestId('next-image');
    expect(editIcon).toHaveAttribute('src', '/icons/manage.svg');
    expect(editIcon).toHaveAttribute('aria-hidden', 'true');
  });

  test('renders full account information with preferences', () => {
    const preferences = [
      {
        id: 'sms-alerts',
        translationKey: 'innBusinessPay.preferences.text.alerts.title',
        value: 'Send',
      },
      {
        id: 'card-delivery',
        translationKey: 'innBusinessPay.preferences.replacement.cards.title',
        value: 'Do not send',
      },
    ];

    const props = {
      ...baseProps,
      accountName: 'Test Account',
      accountNumber: '1234-5678',
      accountLabels: ['account holder'],
      preferences,
    };

    render(<InnBusinessPayPreferences {...props} />);

    preferences.forEach((pref) => {
      const prefElement = screen.getByTestId(`InnBusinessPayPreferences-Pref-${pref.id}`);
      expect(prefElement).toBeInTheDocument();
      expect(screen.getByText(pref.translationKey)).toBeInTheDocument();
      expect(screen.getByText(pref.value)).toBeInTheDocument();
    });

    const container = screen.getByTestId('InnBusinessPayPreferences');
    expect(container).toHaveClass(
      'w-full',
      'flex',
      'flex-col',
      'items-start',
      'max-w-[620px]',
      'mb-10'
    );

    const card = container.children[0];
    expect(card).toHaveClass(
      'w-full',
      'bg-white',
      'border',
      'border-lightGrey4',
      'rounded-lg',
      'p-6'
    );
  });
});
