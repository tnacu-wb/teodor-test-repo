import '@testing-library/jest-dom';
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { requestStatus } from '@whitbread-eos/api';
import * as serverUtils from '@whitbread-eos/utils/server';
import * as navigation from 'next/navigation';

import { ContactPreferencesForm } from './contact-preferences-page';

jest.mock('@whitbread-eos/utils/server');
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
  usePathname: jest.fn(() => '/en-gb/profile/contact-preferences'),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: {
    src: string;
    alt: string;
    width: number;
    height: number;
    className?: string;
  }) => {
    return <img {...props} alt={props.alt} />;
  },
}));

const mockToast = jest.fn();
jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  useToast: () => ({ toast: mockToast }),
  Notification: ({ message }: { message: string }) => (
    <div data-testid="ContactPreferences-Error">{message}</div>
  ),
}));

describe('ContactPreferencesForm', () => {
  const mockRouter = {
    push: jest.fn(),
    replace: jest.fn(),
    refresh: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    prefetch: jest.fn(),
  };

  const defaultProps = {
    icons: {
      'icon.brand.pi.round': '/pi-icon',
      'icon.brand.zip.round': '/zip-icon',
      'icon.brand.hub.round': '/hub-icon',
      'icon.brand.beefeater.round': '/beefeater-icon',
      'icon.brand.cookhouseandpub.round': '/cookhouse-icon',
      'icon.brand.tabletable.round': '/tabletable-icon',
      'icon.brand.brewersfayre.round': '/brewersfayre-icon',
      'icon.brand.barandblock.round': '/barandblock-icon',
      'icon.brand.whitbreadinns.round': '/whitbreadinns-icon',
      'icon.chevron.left.purple': '/back-icon',
      'icon.notification.error': '/error-icon',
    },
    email: 'test@example.com',
    token: 'test-token',
    initialPreferences: {
      optIn: false,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
    },
  };

  const mockTranslations = {
    t: jest.fn((key) => key),
    translations: {},
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(navigation.useRouter).mockReturnValue(mockRouter);
    jest.mocked(serverUtils.useTranslation).mockReturnValue(mockTranslations);
    jest.mocked(serverUtils.formatIBAssetsUrl).mockImplementation((url: string) => url);
  });

  it('renders the form with initial preferences', () => {
    render(<ContactPreferencesForm {...defaultProps} />);

    expect(screen.getByTestId('ContactPreferences')).toBeInTheDocument();
    expect(screen.getByTestId('ContactPreferences-PremierInnCheckbox')).toHaveAttribute(
      'aria-checked',
      'false'
    );
    expect(screen.getByTestId('ContactPreferences-RestaurantsCheckbox')).toHaveAttribute(
      'aria-checked',
      'false'
    );
    expect(screen.getByTestId('ContactPreferences-ThirdPartyCheckbox')).toHaveAttribute(
      'aria-checked',
      'false'
    );
  });

  it('updates preferences when checkboxes are clicked', async () => {
    render(<ContactPreferencesForm {...defaultProps} />);

    const premierInnCheckbox = screen.getByTestId('ContactPreferences-PremierInnCheckbox');
    const restaurantsCheckbox = screen.getByTestId('ContactPreferences-RestaurantsCheckbox');

    await act(async () => {
      fireEvent.click(premierInnCheckbox);
    });

    expect(premierInnCheckbox).toHaveAttribute('aria-checked', 'true');
    expect(restaurantsCheckbox).toHaveAttribute('aria-checked', 'false');

    await act(async () => {
      fireEvent.click(restaurantsCheckbox);
    });

    expect(restaurantsCheckbox).toHaveAttribute('aria-checked', 'true');
  });

  it('handles successful form submission', async () => {
    jest.mocked(serverUtils.updateContactPreferences).mockResolvedValueOnce({
      status: requestStatus.success,
    });

    render(<ContactPreferencesForm {...defaultProps} />);

    const premierInnCheckbox = screen.getByTestId('ContactPreferences-PremierInnCheckbox');
    const submitButton = screen.getByTestId('ContactPreferences-SubmitButton');

    expect(premierInnCheckbox).toHaveAttribute('aria-checked', 'false');

    await act(async () => {
      fireEvent.click(premierInnCheckbox);
    });

    expect(premierInnCheckbox).toHaveAttribute('aria-checked', 'true');

    await act(async () => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(serverUtils.updateContactPreferences).toHaveBeenCalledWith(
        defaultProps.token,
        'en-gb',
        {
          optIn: true,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
        }
      );
      expect(mockToast).toHaveBeenCalledWith({
        content: 'profile.notification.success',
      });
      expect(mockRouter.push).toHaveBeenCalledWith('/en-gb/profile');
    });
  });

  it('handles form submission failure', async () => {
    jest.mocked(serverUtils.updateContactPreferences).mockResolvedValueOnce({
      status: requestStatus.fail,
    });

    render(<ContactPreferencesForm {...defaultProps} />);

    await act(async () => {
      fireEvent.click(screen.getByTestId('ContactPreferences-SubmitButton'));
    });

    await waitFor(() => {
      expect(screen.getByTestId('ContactPreferences-Error')).toBeInTheDocument();
    });
  });

  it('handles network error during form submission', async () => {
    jest
      .mocked(serverUtils.updateContactPreferences)
      .mockRejectedValueOnce(new Error('Network error'));

    render(<ContactPreferencesForm {...defaultProps} />);

    await act(async () => {
      fireEvent.click(screen.getByTestId('ContactPreferences-SubmitButton'));
    });

    await waitFor(() => {
      expect(screen.getByTestId('ContactPreferences-Error')).toBeInTheDocument();
    });
  });

  it('navigates back when back button is clicked', () => {
    render(<ContactPreferencesForm {...defaultProps} />);

    fireEvent.click(screen.getByTestId('ContactPreferences-BackButton'));

    expect(mockRouter.push).toHaveBeenCalledWith('/en-gb/profile');
  });

  it('navigates back when cancel button is clicked', () => {
    render(<ContactPreferencesForm {...defaultProps} />);

    fireEvent.click(screen.getByTestId('ContactPreferences-BackButton'));

    expect(mockRouter.push).toHaveBeenCalledWith('/en-gb/profile');
  });
});
