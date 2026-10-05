import NavigationMenu from '.';
import '@testing-library/jest-dom';
import {
  analytics,
  useFeatureToggle,
  useAuth0Navigation,
  useQueryRequest,
  useCustomLocale,
  getLoggedInUserInfo,
} from '@whitbread-eos/utils';

import { fireEvent, render, renderUserContext, userEvent } from '../../../../utils/test-utils';

const mockLanguage = {
  router: {
    locale: 'en',
  },
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockLanguage,
}));

const mockSearchParamsGet = jest.fn(() => null);

jest.mock('next/navigation', () => ({
  useSearchParams: () => ({
    get: mockSearchParamsGet,
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: jest.fn(() => mockResponse),
  useQuery: () => mockResponse,
  useRestQueryRequest: () => mockResponse,
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useFeatureSwitch: () => true,
  useFeatureToggle: jest.fn(() => ({
    release_pi_signup_in_header: true,
  })),
  analytics: {
    update: jest.fn(),
    remove: jest.fn(),
    track: jest.fn(),
  },
  useAuth0Navigation: jest.fn(() => ({
    navigateToLogin: jest.fn(),
    navigateToSignup: jest.fn(),
    navigateToLogout: jest.fn(),
  })),
  useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
  getAuthCookie: jest.fn(() => ''),
  getLoggedInUserInfo: jest.fn(() => ({ isBusiness: false })),
}));

const setIsLoggedIn = jest.fn();

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    contactDetail: {
      firstName: 'John',
      lastName: 'Doe',
    },
    headerInformation: {
      content: {
        authentication: {
          login: {
            leisure: {
              loginButton: '',
              formLabel: '',
              emailPlaceholder: '',
            },
            business: {
              businessDomain: '',
            },
          },
        },
      },
    },
  },
};

const mockProps = {
  currentLanguage: mockLanguage.router.locale,
  labels: {
    discoverPI: 'Discover Premier Inn',
    business: 'Business',
    businessSubNav: {
      title: 'Business',
      navOptions: [
        {
          title: 'Business home',
          url: '/gb/en/business.html?INTCMP=topNav',
        },
        {
          title: 'Business Booker ',
          url: '/gb/en/business/business-booker.html?INTCMP=topNav',
        },
        {
          title: 'Business Account',
          url: '/gb/en/business/business-account.html?INTCMP=topNav',
        },
        { title: 'Business blog', url: '/gb/en/business-blog.html?INTCMP=topNav' },
      ],
    },
    findBooking: 'Manage Booking',
    logIn: 'Log in',
    logOut: 'Log out',
    signUpButton: 'Sign Up',
    subNav: [
      {
        title: 'Short breaks ',
        navOptions: [
          { title: 'Short breaks ', url: '/gb/en/short-breaks.html?INTCMP=topNav' },
          { title: 'City breaks', url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav' },
          {
            title: 'Beach breaks',
            url: '/gb/en/short-breaks/coastal-breaks.html?INTCMP=topNav',
          },
          { title: 'National parks', url: '/gb/en/hotels/national-parks.html?INTCMP=topNav' },
          {
            title: 'Family breaks',
            url: '/gb/en/short-breaks/family-breaks.html?INTCMP=topNav',
          },
        ],
      },
      {
        title: 'Locations ',
        navOptions: [
          { title: 'Book a hotel', url: '/gb/en/book-a-hotel.html?INTCMP=topNav' },
          { title: 'Hotel directory', url: '/gb/en/hotels.html?INTCMP=topNav' },
          { title: 'Best hotels', url: '/gb/en/hotels/best-hotels.html?INTCMP=topNav' },
          { title: 'Germany hotels', url: '/gb/en/hotels/germany.html?INTCMP=topNav' },
          { title: 'Local guides', url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav' },
        ],
      },
      {
        title: 'About us',
        navOptions: [
          { title: 'About us', url: '/gb/en/why.html?INTCMP=topNav' },
          { title: 'Our rates', url: '/gb/en/why/rates.html?INTCMP=topNav' },
          { title: 'Food and drink', url: '/gb/en/why/food.html?INTCMP=topNav' },
          { title: 'Our rooms', url: '/gb/en/sleep/our-rooms.html?INTCMP=topNav' },
          { title: 'Families', url: '/gb/en/why/family.html?INTCMP=topNav' },
        ],
      },
    ],
    accountLinks: [
      {
        icon: 'bookings',
        url: '/account/dashboard',
        title: 'Bookings',
      },
      {
        icon: 'settings',
        url: '/account/settings',
        title: 'Settings',
      },
    ],
  },
};

describe('NavigationMenu', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockSearchParamsGet.mockReturnValue(null);
    delete (window as any).location;
    (window as any).location = {
      href: '',
      hostname: 'localhost',
      pathname: '/',
      protocol: 'http:',
      assign: jest.fn(),
    };
  });

  it('should open the manage booking modal when manage-booking is true', () => {
    mockSearchParamsGet.mockImplementation((parameter) =>
      parameter === 'manage-booking' ? 'true' : null
    );

    const { getByTestId } = render(<NavigationMenu {...mockProps} />);

    expect(mockSearchParamsGet).toHaveBeenCalledWith('manage-booking');
    expect(getByTestId('ManageBookingModal-ModalContent')).toBeInTheDocument();
  });

  it('should render a NavigationMenu with login item', function () {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_signup_in_header: false,
      release_pi_auth0_login: false,
    });

    const { getByTestId } = render(<NavigationMenu {...mockProps} />);
    getByTestId('Global-Login-Desktop');
  });

  it('should have 4 items inside the header if language is german', function () {
    mockProps.currentLanguage = 'de';
    const { queryAllByTestId } = render(<NavigationMenu {...mockProps} />);
    const navItems = queryAllByTestId('listId');
    expect(navItems.length).toBe(4);
  });

  it('should render all the links correctly if language is english', function () {
    mockProps.currentLanguage = 'en';
    const { queryAllByTestId } = render(<NavigationMenu {...mockProps} />);
    const navLinks = queryAllByTestId('listItem');
    expect(navLinks.length).toBe(19);
  });

  it('should render all the links correctly if language is german', function () {
    mockProps.currentLanguage = 'de';
    const { queryAllByTestId } = render(<NavigationMenu {...mockProps} />);
    const navLinks = queryAllByTestId('listItem');
    expect(navLinks.length).toBe(19);
  });

  it('should render Bookings link for en', () => {
    mockProps.currentLanguage = 'en';
    const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });
    const bookingsLink = getByTestId('BookingsButton');
    expect(bookingsLink).toBeInTheDocument();
    expect(bookingsLink.tagName).toBe('A');
    expect(bookingsLink).toHaveAttribute('href', '/account/dashboard');
  });

  it('should render Log out link for en', () => {
    mockProps.currentLanguage = 'en';
    const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });
    const logoutButton = getByTestId('Global-Logout-Desktop');

    const mockAuth = {
      contentWindow: {
        postMessage: jest.fn(),
      },
    };
    document.getElementById = jest.fn().mockReturnValue(mockAuth);

    userEvent.click(logoutButton);

    expect(mockAuth.contentWindow.postMessage).toBeCalledWith(
      '{"action":"logout"}',
      'http://localhost'
    );
  });

  it('redirects to home once SecureTwo confirms logout after the logout button was clicked', () => {
    mockProps.currentLanguage = 'en';
    const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });
    const logoutButton = getByTestId('Global-Logout-Desktop');

    const mockAuth = {
      contentWindow: {
        postMessage: jest.fn(),
      },
    };
    document.getElementById = jest.fn().mockReturnValue(mockAuth);

    fireEvent.click(logoutButton);

    fireEvent(
      window,
      new MessageEvent('message', {
        data: 'userLoggedOut',
        origin: 'http://localhost',
      })
    );

    expect(window.location.assign).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('does not redirect when userLoggedOut arrives without the logout button having been clicked', () => {
    mockProps.currentLanguage = 'en';
    renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    fireEvent(
      window,
      new MessageEvent('message', {
        data: 'userLoggedOut',
        origin: 'http://localhost',
      })
    );

    expect(window.location.assign).not.toHaveBeenCalled();
  });

  it('should render Bookings link for de', () => {
    mockProps.currentLanguage = 'de';
    const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });
    const bookingsLink = getByTestId('BookingsButton');
    expect(bookingsLink).toBeInTheDocument();
    expect(bookingsLink.tagName).toBe('A');
    expect(bookingsLink).toHaveAttribute('href', '/account/dashboard');
  });

  it('should render Log out link for de', () => {
    mockProps.currentLanguage = 'de';
    const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });
    getByTestId('Global-Logout-Desktop');
  });

  it('should render a NavigationMenu with login item', function () {
    const { getByTestId } = render(<NavigationMenu {...mockProps} />);
    const signUpButton = getByTestId('ManageBookingButton');
    expect(signUpButton).toBeInTheDocument();
    userEvent.click(signUpButton);
  });

  it('should close login modal', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_signup_in_header: false,
      release_pi_auth0_login: false,
    });

    const { getByTestId } = render(<NavigationMenu {...mockProps} />);
    const loginButton = getByTestId('Global-Login-Desktop');
    fireEvent.click(loginButton);
    const closeButton = getByTestId('Header-Auth-ModalCloseButton');
    userEvent.click(closeButton);
    expect(analytics.update).toHaveBeenCalledWith({
      loginClicked: true,
    });
  });

  it('should render Sign up button', function () {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_signup_in_header: true,
      release_pi_auth0_login: false,
    });

    const { getByTestId } = render(<NavigationMenu {...mockProps} />);
    const signUpButton = getByTestId('Global-SignUp-Desktop');
    expect(signUpButton).toBeInTheDocument();
    userEvent.click(signUpButton);
  });

  describe('Auth0 Integration', () => {
    it('should call navigateToLogin when Auth0 is enabled and login is clicked', () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pi_signup_in_header: false,
      });

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);
      const loginButton = getByTestId('Global-Login-Desktop');

      fireEvent.click(loginButton);

      expect(mockNavigateToLogin).toHaveBeenCalled();
    });

    it('should display both login and signup buttons when signup feature is enabled', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_signup_in_header: true,
        release_pi_auth0_login: false,
      });

      const { getByText, getByTestId } = render(<NavigationMenu {...mockProps} />);

      expect(getByText('Log in')).toBeInTheDocument();
      expect(getByTestId('Global-SignUp-Desktop')).toBeInTheDocument();
    });

    it('should navigate to register page when signup is clicked', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_signup_in_header: true,
        release_pi_auth0_login: false,
      });

      delete (window as any).location;
      window.location = { href: '', hostname: 'localhost' } as unknown as Location;

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);
      const signUpButton = getByTestId('Global-SignUp-Desktop');

      fireEvent.click(signUpButton);

      expect(window.location.href).toContain('/account/register.html');
    });

    it('should show legacy login modal when Auth0 is disabled', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: false,
        release_pi_signup_in_header: false,
      });

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);
      const loginButton = getByTestId('Global-Login-Desktop');

      fireEvent.click(loginButton);

      expect(analytics.update).toHaveBeenCalledWith({
        loginClicked: true,
      });
    });

    it('should show call Auth0 navigate to login when Auth0 is enabled but user clicks login from a business page and PIB login redirect is enabled but businessDomain is empty', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const mockNavigateToLogin = jest.fn();
      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      const loginButton = getByTestId('Global-Login-Desktop');
      fireEvent.click(loginButton);

      expect(getByTestId('Global-Login-Desktop')).toBeInTheDocument();
      expect(mockNavigateToLogin).toHaveBeenCalled();
    });

    it('should not call navigateToLogin when Auth0 is enabled and user clicks login from a business page and PIB login redirect is enabled and businessDomain is not empty', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      const mockUseQueryRequestWithBusinessDomain = {
        ...mockResponse,
        data: {
          ...mockResponse.data,
          headerInformation: {
            content: {
              authentication: {
                login: {
                  business: {
                    businessDomain: 'https://example.com/business-login',
                  },
                },
              },
            },
          },
        },
      };

      (useQueryRequest as jest.Mock).mockReturnValue(mockUseQueryRequestWithBusinessDomain);

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);

      const loginButton = getByTestId('Global-Login-Desktop');
      fireEvent.click(loginButton);

      expect(mockNavigateToLogin).not.toHaveBeenCalled();
    });

    it('should use correct login url based on feature toggles and businessDomain value, language and country', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const mockUseQueryRequestWithBusinessDomain = {
        ...mockResponse,
        data: {
          ...mockResponse.data,
          headerInformation: {
            content: {
              authentication: {
                login: {
                  business: {
                    businessDomain: 'https://example.com/business-login',
                  },
                },
              },
            },
          },
        },
      };

      (useQueryRequest as jest.Mock).mockReturnValue(mockUseQueryRequestWithBusinessDomain);

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);

      const loginButton = getByTestId('Global-Login-Desktop');
      fireEvent.click(loginButton);

      expect(window.location.href).toBe(
        'https://example.com/business-login/en-gb/account/login?intcmp=piLogInModalLink'
      );
    });

    it('should use correct login url based on feature toggles and businessDomain value and language = de and country = de', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const mockUseQueryRequestWithBusinessDomain = {
        ...mockResponse,
        data: {
          ...mockResponse.data,
          headerInformation: {
            content: {
              authentication: {
                login: {
                  business: {
                    businessDomain: 'https://example.com/business-login',
                  },
                },
              },
            },
          },
        },
      };

      (useQueryRequest as jest.Mock).mockReturnValue(mockUseQueryRequestWithBusinessDomain);

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });
      const { getByTestId } = render(<NavigationMenu {...mockProps} />);

      const loginButton = getByTestId('Global-Login-Desktop');
      fireEvent.click(loginButton);

      expect(window.location.href).toBe(
        'https://example.com/business-login/de-de/account/login?intcmp=piLogInModalLink'
      );
    });

    it('should call navigateToLogout when Auth0 is enabled and logout is clicked', () => {
      const mockNavigateToLogout = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pi_signup_in_header: false,
      });

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: jest.fn(),
        navigateToLogout: mockNavigateToLogout,
        navigateToSignup: jest.fn(),
      });

      const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      fireEvent.click(getByTestId('Global-Logout-Desktop'));

      expect(mockNavigateToLogout).toHaveBeenCalledTimes(1);
    });

    it('should use iframe postMessage for logout when Auth0 is disabled', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: false,
        release_pi_signup_in_header: false,
      });

      const mockAuth = { contentWindow: { postMessage: jest.fn() } };
      document.getElementById = jest.fn().mockReturnValue(mockAuth);

      const { getByTestId } = renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      fireEvent.click(getByTestId('Global-Logout-Desktop'));

      expect(mockAuth.contentWindow.postMessage).toHaveBeenCalledWith(
        '{"action":"logout"}',
        expect.any(String)
      );
    });

    it('should call navigateToSignup when Auth0 is enabled and sign up is clicked', () => {
      const mockNavigateToSignup = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pi_signup_in_header: true,
      });

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: jest.fn(),
        navigateToLogout: jest.fn(),
        navigateToSignup: mockNavigateToSignup,
      });

      const { getByTestId } = render(<NavigationMenu {...mockProps} />);

      fireEvent.click(getByTestId('Global-SignUp-Desktop'));

      expect(mockNavigateToSignup).toHaveBeenCalledTimes(1);
    });
  });

  describe('BB user redirect', () => {
    beforeEach(() => {
      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'en', country: 'gb' });
    });

    const mockResponseWithBusinessDomain = {
      ...mockResponse,
      data: {
        ...mockResponse.data,
        headerInformation: {
          content: {
            authentication: {
              login: {
                business: {
                  businessDomain: 'https://example.com/business-login',
                },
              },
            },
          },
        },
      },
    };

    it('should redirect a logged-in BB user to BB login when businessDomain is available', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponseWithBusinessDomain);

      renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe(
        'https://example.com/business-login/en-gb/account/login?intcmp=piLogInModalLink'
      );
    });

    it('should use the correct locale in the BB login redirect URL for de-de', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponseWithBusinessDomain);
      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });

      renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe(
        'https://example.com/business-login/de-de/account/login?intcmp=piLogInModalLink'
      );
    });

    it('should not redirect when the logged-in user is not a BB user', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: false });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponseWithBusinessDomain);

      renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe('');
    });

    it('should not redirect when the user is a BB user but businessDomain is not available', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponse);

      renderUserContext(<NavigationMenu {...mockProps} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe('');
    });

    it('should not redirect when the user is not logged in', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponseWithBusinessDomain);

      render(<NavigationMenu {...mockProps} />);

      expect(window.location.href).toBe('');
    });
  });
});
