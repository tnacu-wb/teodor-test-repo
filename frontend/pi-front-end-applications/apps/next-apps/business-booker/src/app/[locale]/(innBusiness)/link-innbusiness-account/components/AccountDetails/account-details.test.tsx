import '@testing-library/jest-dom';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { businessTether } from '@whitbread-eos/utils/server';
import { useSearchParams, useRouter } from 'next/navigation';

import AccountDetails from './account-details';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
  useSearchParams: jest.fn(() => ({
    get: jest.fn().mockImplementation((key: string) => {
      if (key === 'linkCode') {
        return 'rqv-cyz-9d8';
      }
      return null;
    }),
  })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: jest.fn(() => 'mock-token'),
  formatAccountNumber: jest.fn((num) => num),
  formatIBAssetsUrl: () => {
    return '/';
  },
  getPathForLocale: (locale: LOCALES, path: string): string => `/${locale}/${path}`,
  renderSanitizedHtml: (html: string) => (
    <div data-testid="sanitized-content" dangerouslySetInnerHTML={{ __html: html }} />
  ),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
  findError: () => undefined,
  useTranslation: () => ({
    t: (str: string) => str,
  }),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getLocaleByPathname: () => {
    return LOCALES.EN;
  },
  businessTether: jest.fn().mockResolvedValue({
    data: {
      businessTether: {
        guid: 'user-guid',
      },
    },
  }),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  useToast: () => ({
    toast: jest.fn((text: string) => <span>{text}</span>),
  }),
}));

const mockFormData = {
  linkId: '3089503200100393',
  memorableWord: 'Hello12egt',
  saveInCdh: true,
};

describe('AccountDetails Component', () => {
  const mockRouter = {
    push: jest.fn(),
    replace: jest.fn(),
    refresh: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    prefetch: jest.fn(),
  };

  const mockIcons = {
    'icon.arrow.left.purple': 'mockArrowLeftIcon',
    'icon.notification.error': 'mockErrorIcon',
  };

  const defaultProps = {
    baseDataTestId: 'LinkAccount',
    icons: mockIcons,
    locale: LOCALES.EN,
  };

  beforeEach(() => {
    jest.mocked(useRouter).mockReturnValue(mockRouter);
    jest.clearAllMocks();
  });

  it('should render the form with default values', () => {
    render(<AccountDetails {...defaultProps} />);
    expect(screen.getByPlaceholderText('auth.linkIbPayAccount.linkCode.label')).toHaveValue(
      'rqv-cyz-9d8'
    );
    expect(screen.getByPlaceholderText('auth.linkIbPayAccount.accountNumber.label')).toHaveValue(
      ''
    );
    expect(screen.getByText('auth.linkIbPayAccount.accountNumber.hint')).toBeInTheDocument();
    expect(screen.getByText('auth.linkIbPayAccount.description')).toBeInTheDocument();
  });

  it('should render the Continue button', () => {
    render(<AccountDetails {...defaultProps} />);

    expect(screen.getByTestId('LinkAccount-Button')).toBeInTheDocument();
  });

  it('should display error message for invalid card number', async () => {
    render(<AccountDetails {...defaultProps} />);

    // Submit form without filling the required field
    const submitButton = screen.getByTestId('LinkAccount-Button');
    fireEvent.click(submitButton);

    // Validation should prevent submission (not calling businessTether)
    expect(businessTether).not.toHaveBeenCalled();
  });

  it('should render memorable word input', () => {
    render(<AccountDetails {...defaultProps} />);

    expect(screen.getByText('auth.linkIbPayAccount.memorableWord.title')).toBeInTheDocument();
    expect(screen.getByText('auth.linkIbPayAccount.memorableWord.description')).toBeInTheDocument();
  });

  it('should display error message for invalid link code', async () => {
    (useSearchParams as jest.Mock).mockImplementation(
      jest.fn(() => ({
        get: (key: string) => (key === 'linkCode' ? 'invalid-link-code' : null),
      }))
    );
    render(<AccountDetails {...defaultProps} />);

    expect(
      await screen.findByText('auth.linkIbPayAccount.error.invalidLinkCode')
    ).toBeInTheDocument();
  });

  it('should navigate to spending on success', async () => {
    render(<AccountDetails {...defaultProps} />);

    const accountNumberInput = screen.getByPlaceholderText(
      'auth.linkIbPayAccount.accountNumber.label'
    );
    const memorableWordInput = screen.getByPlaceholderText('spending.memorable.word.placeholder');
    const continueButton = screen.getByTestId('LinkAccount-Button');

    fireEvent.change(accountNumberInput, { target: { value: mockFormData.linkId } });
    fireEvent.change(memorableWordInput, { target: { value: mockFormData.memorableWord } });

    await act(async () => {
      fireEvent.click(continueButton);
    });
    expect(businessTether).toHaveBeenCalled();
    expect(mockRouter.push).toHaveBeenCalledWith('/en-gb/spending?tab=innbusiness-pay');
  });

  it('should navigate to homepage on error', async () => {
    (businessTether as jest.Mock).mockResolvedValue(null);
    render(<AccountDetails {...defaultProps} />);

    const accountNumberInput = screen.getByPlaceholderText(
      'auth.linkIbPayAccount.accountNumber.label'
    );
    const memorableWordInput = screen.getByPlaceholderText('spending.memorable.word.placeholder');
    const continueButton = screen.getByTestId('LinkAccount-Button');

    fireEvent.change(accountNumberInput, { target: { value: mockFormData.linkId } });
    fireEvent.change(memorableWordInput, { target: { value: mockFormData.memorableWord } });

    await act(async () => {
      fireEvent.click(continueButton);
    });
    expect(mockRouter.push).toHaveBeenCalledWith('/en-gb/homepage');
  });
});
