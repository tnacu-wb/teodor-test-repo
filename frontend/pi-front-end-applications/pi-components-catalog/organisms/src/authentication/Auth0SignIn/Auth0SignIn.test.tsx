import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { useAuth0Navigation, useFeatureToggle, useUserData } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import Auth0SignIn from './Auth0SignIn.component';

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(() => ({
    release_pi_auth0_login: true,
  })),
  useUserData: jest.fn(() => ({ isLoggedIn: false })),
  useAuth0Navigation: jest.fn(() => ({
    navigateToLogin: jest.fn(),
  })),
  formatDataTestId: (base: string, suffix: string) => `${base}-${suffix}`,
}));

describe('Auth0SignIn', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: true });
    (useUserData as jest.Mock).mockReturnValue({ isLoggedIn: false });
    (useAuth0Navigation as jest.Mock).mockReturnValue({ navigateToLogin: jest.fn() });
  });

  it('should render the container when Auth0 is enabled and user is not logged in', () => {
    const { getByTestId } = render(<Auth0SignIn />);
    expect(getByTestId('GuestDetails-Auth0SignIn-Container')).toBeInTheDocument();
  });

  it('should render the heading', () => {
    const { getByTestId } = render(<Auth0SignIn />);
    expect(getByTestId('GuestDetails-Auth0SignIn-Heading')).toBeInTheDocument();
  });

  it('should render the description', () => {
    const { getByTestId } = render(<Auth0SignIn />);
    expect(getByTestId('GuestDetails-Auth0SignIn-Description')).toBeInTheDocument();
  });

  it('should render the sign in button', () => {
    const { getByTestId } = render(<Auth0SignIn />);
    expect(getByTestId('GuestDetails-Auth0SignIn-Button')).toBeInTheDocument();
  });

  it('should return null when Auth0 is disabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: false });
    const { queryByTestId } = render(<Auth0SignIn />);
    expect(queryByTestId('GuestDetails-Auth0SignIn-Container')).not.toBeInTheDocument();
  });

  it('should return null when user is already logged in', () => {
    (useUserData as jest.Mock).mockReturnValue({ isLoggedIn: true });
    const { queryByTestId } = render(<Auth0SignIn />);
    expect(queryByTestId('GuestDetails-Auth0SignIn-Container')).not.toBeInTheDocument();
  });

  it('should call navigateToLogin when the sign in button is clicked', () => {
    const mockNavigateToLogin = jest.fn();
    (useAuth0Navigation as jest.Mock).mockReturnValue({ navigateToLogin: mockNavigateToLogin });

    const { getByTestId } = render(<Auth0SignIn />);
    fireEvent.click(getByTestId('GuestDetails-Auth0SignIn-Button'));

    expect(mockNavigateToLogin).toHaveBeenCalledTimes(1);
  });
});
