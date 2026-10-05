import '@testing-library/jest-dom';
import { render, fireEvent, act } from '@testing-library/react';
import { LOCALES, CustomerAccountDetails, Scheme } from '@whitbread-eos/api';
import * as navigation from 'next/navigation';

import { userEvent } from '~utils/test-utils';

import { MemorableWordForm } from './memorable-word-component';

const mockAccount: CustomerAccountDetails = {
  tetheredGuid: 'test-guid',
  accountNumber: '123456',
  scheme: 'GB' as Scheme,
};

const mockProps = {
  baseDataTestId: 'testId',
  icons: { icon: 'test' },
  locale: LOCALES.EN,
  account: mockAccount,
};

const mockResponse = {
  status: 'success',
};

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  Notification: function Notification({ message }: { message: string }) {
    return <div>{message}</div>;
  },
  SanitizedContent: ({
    children,
  }: {
    children: React.ReactNode;
    replacements?: Record<string, string>;
  }) => <div>{children}</div>,
  useToast: () => ({
    toast: jest.fn(),
  }),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
  usePathname: jest.fn(() => '/en-gb/spending?tab=innbusiness-pay&account=123456'),
}));

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: jest.fn().mockReturnValue('test-cookie'),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
  findError: () => undefined,
  useTranslation: () => ({
    t: (str: string) => str,
  }),
  formatIBAssetsUrl: (url: string) => url || '/',
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (str: string) => str,
  }),
  resetMemorableWord: () => mockResponse,
  getPathForLocale: (locale: string, path: string) => `/${locale}/${path}`,
}));

describe('MemorableWord Component', () => {
  const mockRouter = {
    push: jest.fn(),
    replace: jest.fn(),
    refresh: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    prefetch: jest.fn(),
  };

  beforeEach(() => {
    jest.mocked(navigation.useRouter).mockReturnValue(mockRouter);
    jest.clearAllMocks();
  });

  it('should render MemorableWordForm component', async () => {
    const { getByTestId } = render(<MemorableWordForm {...mockProps} />);

    expect(getByTestId('testId-Submit-Button')).toBeInTheDocument();
  });

  it('should submit the form successfully and redirect', async () => {
    const { getByTestId } = render(<MemorableWordForm {...mockProps} />);

    const input = getByTestId('memorableWord-Form-Input');
    await act(async () => {
      input.focus();
      fireEvent.change(input, { target: { value: 'Whitbread10' } });
      await userEvent.tab();
    });

    const submitButton = getByTestId('testId-Submit-Button');
    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(mockRouter.push).toHaveBeenCalledWith(
      '/en-gb/spending?tab=innbusiness-pay&account=test-guid'
    );
  });
});
