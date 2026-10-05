import { ChakraProvider } from '@chakra-ui/react';
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { FT_PI_ADD_TO_WALLET } from '@whitbread-eos/api';
import { isIOSDevice, decryptCryptString, useFeatureToggle } from '@whitbread-eos/utils';
import i18n from 'i18next';
import { I18nextProvider } from 'react-i18next';

import { AddToWalletPagePi } from './index';
import { isValidArrivalDate } from './page.pi';

const mockT = jest.fn((key: string) => {
  const translations = {
    'error.wallet.add.link.expired': 'Sorry, this link has expired.',
    'error.wallet.add.device.notSupported': 'Sorry, your device is not supported.',
  };
  return translations[key] || key;
});

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: mockT }),
}));

const mockPush = jest.fn();

const mockRouter = {
  push: mockPush,
  query: {},
  isReady: false,
};

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    isIOSDevice: jest.fn(),
    decryptCryptString: jest.fn(),
    useFeatureToggle: jest.fn(),
  };
});

const queryClient = new ReactQuery.QueryClient();

const renderPage = () =>
  render(
    <ChakraProvider>
      <I18nextProvider i18n={i18n}>
        <AddToWalletPagePi queryClient={queryClient} router={mockRouter} />
      </I18nextProvider>
    </ChakraProvider>
  );

const formatDate = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

describe('AddToWalletPagePi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRouter.query = {};
    mockRouter.isReady = false;
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_PI_ADD_TO_WALLET]: true });
  });

  it('redirects to Apple Wallet on iOS with valid arrival date', async () => {
    const today = formatDate(new Date());
    const referrerString = `arrivalDate=${today}`;
    mockRouter.isReady = true;
    mockRouter.query.referrer = encodeURIComponent(referrerString);
    (isIOSDevice as jest.Mock).mockReturnValue(true);
    (decryptCryptString as jest.Mock).mockReturnValue(`arrivalDate=${today}`);

    renderPage();

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith(
        '/api/addtowallet/apple?referrer=' + encodeURIComponent(referrerString)
      );
    });
  });

  it('shows expired link message on iOS with invalid arrival date', async () => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const referrerString = `arrivalDate=${formatDate(yesterday)}`;
    mockRouter.isReady = true;
    mockRouter.query.referrer = encodeURIComponent(referrerString);
    (isIOSDevice as jest.Mock).mockReturnValue(true);
    (decryptCryptString as jest.Mock).mockReturnValue(`arrivalDate=${formatDate(yesterday)}`);

    renderPage();

    await waitFor(() => {
      expect(mockPush).not.toHaveBeenCalled();
      expect(mockT).toHaveBeenCalledWith('error.wallet.add.link.expired');
    });
  });

  it('shows unsupported device message on non-iOS devices', async () => {
    const today = formatDate(new Date());
    const referrerString = `arrivalDate=${today}`;
    mockRouter.isReady = true;
    mockRouter.query.referrer = encodeURIComponent(referrerString);
    (isIOSDevice as jest.Mock).mockReturnValue(false);
    (decryptCryptString as jest.Mock).mockReturnValue(`arrivalDate=${today}`);

    renderPage();

    await waitFor(() => {
      expect(mockT).toHaveBeenCalledWith('error.wallet.add.device.notSupported');
      expect(mockPush).not.toHaveBeenCalled();
    });
  });

  it('does nothing if router is not ready', async () => {
    mockRouter.isReady = false;
    mockRouter.query.referrer = 'some-referrer';
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_PI_ADD_TO_WALLET]: true });

    renderPage();
  });

  it('does nothing if referrer is missing', async () => {
    mockRouter.isReady = true;
    mockRouter.query.referrer = undefined;
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_PI_ADD_TO_WALLET]: true });

    renderPage();
  });

  it('does nothing if feature toggle is disabled', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_PI_ADD_TO_WALLET]: false });
    mockRouter.isReady = true;
    mockRouter.query.referrer = 'arrivalDate=2026-02-11';

    renderPage();

    await waitFor(() => {
      expect(mockPush).not.toHaveBeenCalled();
      expect(screen.queryByText(/Sorry,/)).not.toBeInTheDocument();
    });
  });
});

describe('isValidArrivalDate', () => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  it('returns true for today', () => {
    expect(isValidArrivalDate(formatDate(today))).toBe(true);
  });

  it('returns true for tomorrow', () => {
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    expect(isValidArrivalDate(formatDate(tomorrow))).toBe(true);
  });

  it('returns false for yesterday', () => {
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    expect(isValidArrivalDate(formatDate(yesterday))).toBe(false);
  });

  it('returns false for more than one day in the future', () => {
    const future = new Date(today);
    future.setDate(today.getDate() + 2);
    expect(isValidArrivalDate(formatDate(future))).toBe(false);
  });

  it('returns false for invalid date string', () => {
    expect(isValidArrivalDate('invalid-date')).toBe(false);
  });
});
