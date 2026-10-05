import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';
import { BritishFlagRounded, GermanFlagRounded, Icon, Tick24 } from '@whitbread-eos/atoms';
import {
  useFeatureToggle,
  useAuth0Navigation,
  useQueryRequest,
  useCustomLocale,
  getLoggedInUserInfo,
} from '@whitbread-eos/utils';
import React from 'react';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { render, renderUserContext, userEvent, waitFor } from '../../../../utils/test-utils';
import NavigationMenuMobile from './NavigationMenuMobile.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: jest.fn(() => mockUseQueryRequest),
  useRestQueryRequest: () => mockUseQueryRequest,
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useFeatureSwitch: () => true,
  useFeatureToggle: jest.fn(() => ({
    release_pi_auth0_login: false,
  })),
  useAuth0Navigation: jest.fn(() => ({
    navigateToLogin: jest.fn(),
    navigateToLogout: jest.fn(),
  })),
  useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
  getAuthCookie: jest.fn(() => ''),
  getLoggedInUserInfo: jest.fn(() => ({ isBusiness: false })),
}));

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
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
    content: {
      global: {
        addRoom: 'Add another room',
        adult: 'adult',
        adults: 'adults',
        adultsLabel: 'Adults',
        child: 'child',
        children: 'children',
        childrenLabel: 'Children',
        done: 'Done',
        night: 'night',
        room: 'room',
        rooms: 'rooms',
        today: 'Today',
        tomorrow: 'Tomorrow',
        single: 'Single',
        double: 'Double',
        accessible: 'Accessible',
        twin: 'Twin',
        family: 'Family',
      },
    },
    datePicker: {
      reset: 'Reset',
      invalidDate: 'Invalid Date',
      checkOut: 'Check out',
    },
    form: {
      where: 'Enter place, postcode or hotel',
      adultsHelperText: 'Max 2 per room',
      checkout: 'Check out:',
      childrenHelperText: '2-15 years',
      cotLimit: '0-2 years',
      includeCot: 'Include a cot?',
      removeRoom: 'Remove room',
      roomType: 'Room type',
      invalidFutureDate:
        'Unable to book over a year in advance, rates below are for availability today',
      invalidDate: 'Please enter a valid date',
      invalidPastDate: 'Your selected date is in the past, rates below are for availability today',
      invalidNights: 'Please enter a valid number of nights',
      invalidRooms: 'Please enter a valid room composition',
      invalidLocation: 'Please enter a location or a hotel',
    },
    results: {
      notifications: {
        ccuiGroupBookingMessage:
          'If caller wishes to add more than 9 rooms then please ask them to contact the Groups Team at group.enquiries@whitbread.com',
        groupBookingHeader: 'Unable to add more rooms',
        groupBookingMessage:
          'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
        noResults:
          'Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.',
        errorTitle: 'Oh dear..',
        availabilitiesErrorMessage: 'Something went wrong when we tried to load the results',
        groupBookingFormPageMessage:
          'To make a group booking of 5 to 9 rooms, contact us via Live Chat for guidance. To book 10 rooms or more, please complete the <a href="/gb/en/why/groups/form{groupBookingLink}">group booking form</a> and we will be in contact to discuss your enquiry.',
      },
    },
    config: {
      api: {
        bookingChannel: {
          business: 'CBT',
          leisure: 'WEB',
        },
      },
      roomCodes: {
        accessible: 'DIS',
        double: 'DB',
        family: 'FAM',
        single: 'SB',
        twin: 'TWIN',
      },
    },
    contactDetail: {
      firstName: 'John',
      lastName: 'Doe',
    },
  },
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'gb',
  }),
}));

const setIsLoggedIn = jest.fn();

const props = {
  area: Area.PI,
  currentLanguage: 'en',
  router: {},
  t: jest.fn(),
  prefixDataTestId: '',
  languagesList: [
    {
      locale: 'en' as 'en' | 'de',
      languageName: 'britishLanguage',
      icon: <Icon svg={<BritishFlagRounded cursor="pointer" data-testid="britishFlag" />} />,
    },
    {
      locale: 'de' as 'en' | 'de',
      languageName: 'germanLanguage',
      icon: <Icon svg={<GermanFlagRounded cursor="pointer" data-testid="britishFlag" />} />,
    },
  ],
  tickIcon: <Icon svg={<Tick24 />} />,
  labels: {
    language: 'Language',
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
    subNav: [
      {
        title: 'Short breaks ',
        navOptions: [
          { title: 'Short breaks ', url: undefined },
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

describe('NavigationMenuMobile', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete (window as any).location;
    (window as any).location = {
      href: '',
      hostname: 'localhost',
      pathname: '/',
      protocol: 'http:',
      assign: jest.fn(),
    };
  });

  it('should render a <NavigationMenuMobile> with default props ', async () => {
    const { queryAllByTestId, queryByTestId, getByTestId } = render(
      <NavigationMenuMobile {...props} />
    );
    expect(queryByTestId('modalSideBar')).not.toBeInTheDocument();

    const burgerBtn = getByTestId('burgerMenu');
    await userEvent.click(burgerBtn);

    await waitFor(() => {
      expect(getByTestId('modalSideBar')).toBeInTheDocument();
      expect(getByTestId('defaultSideNav')).toBeInTheDocument();
      expect(queryAllByTestId('navigationItem').length).toBe(3);
    });
    window.postMessage(
      {
        action: 'userLoggedOut',
        data: 'userLoggedOut',
      },
      '*'
    );

    fireEvent(
      window,
      new MessageEvent('message', {
        data: { action: 'userLoggedOut', data: 'userLoggedOut' },
        origin: 'localhost',
      })
    );
  });

  it('should have a login option', async () => {
    const { getByTestId } = render(<NavigationMenuMobile {...props} />);

    await userEvent.click(getByTestId('burgerMenu'));

    expect(getByTestId('defaultSideNav')).toBeInTheDocument();

    await userEvent.click(getByTestId('Global-Login-Mobile'));

    expect(getByTestId('Global-Login-Mobile')).toBeInTheDocument();
  });

  it('should render the language selection when clicking on language option', async function () {
    const { getByTestId } = render(<NavigationMenuMobile {...props} />);

    await userEvent.click(getByTestId('burgerMenu'));

    expect(getByTestId('defaultSideNav')).toBeInTheDocument();

    const triggerBtn = getByTestId('Global-TriggerLanguageSideNav');

    await userEvent.click(triggerBtn);

    expect(getByTestId('languageSelectorSideNav')).toBeInTheDocument();
  });

  it('should render the business options when clicking on Business option', async function () {
    const { getByTestId, getByText } = render(<NavigationMenuMobile {...props} />);

    await userEvent.click(getByTestId('burgerMenu'));

    expect(getByTestId('defaultSideNav')).toBeInTheDocument();

    const triggerBtn = getByTestId('Global-TriggerBusinessSideNav');

    await userEvent.click(triggerBtn);

    expect(getByTestId('businessSideNav')).toBeInTheDocument();
    expect(getByText('Business home')).toBeInTheDocument();
    expect(getByText('Business Booker')).toBeInTheDocument();
    expect(getByText('Business Account')).toBeInTheDocument();

    expect(getByText('Business home').closest('a')).toHaveAttribute(
      'href',
      '/gb/en/business.html?INTCMP=topNav'
    );
    expect(getByText('Business blog').closest('a')).toHaveAttribute(
      'href',
      '/gb/en/business-blog.html?INTCMP=topNav'
    );
  });

  it('should render the discovery options when clicking on Discover Premier Inn option', async function () {
    const { getByTestId, getByText } = render(<NavigationMenuMobile {...props} />);

    await userEvent.click(getByTestId('burgerMenu'));

    expect(getByTestId('defaultSideNav')).toBeInTheDocument();

    const triggerBtn = getByTestId('Global-TriggerDiscoverSideNav');

    await userEvent.click(triggerBtn);

    expect(getByTestId('discoverSideNav')).toBeInTheDocument();
    expect(getByText('Family breaks')).toBeInTheDocument();
    expect(getByText('Hotel directory')).toBeInTheDocument();
    expect(getByText('Our rates')).toBeInTheDocument();
  });

  it('should render Bookings link', async () => {
    const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    await userEvent.click(getByTestId('burgerMenu'));
    await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));

    const bookingsLink = getByTestId('BookingsButton-Mobile');
    expect(bookingsLink).toBeInTheDocument();
    expect(bookingsLink.tagName).toBe('A');
    expect(bookingsLink).toHaveAttribute('href', '/account/dashboard');
  });

  it('should click Logout link', async () => {
    const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    const mockAuth = {
      contentWindow: {
        postMessage: jest.fn(),
      },
    };
    document.getElementById = jest.fn().mockReturnValue(mockAuth);

    await userEvent.click(getByTestId('burgerMenu'));
    await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));
    await userEvent.click(getByTestId('Global-Logout-Mobile'));

    expect(mockAuth.contentWindow.postMessage).toBeCalledWith(
      '{"action":"logout"}',
      'http://localhost'
    );
  });

  it('redirects to home once SecureTwo confirms logout after the logout button was clicked', async () => {
    const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    const mockAuth = {
      contentWindow: {
        postMessage: jest.fn(),
      },
    };
    document.getElementById = jest.fn().mockReturnValue(mockAuth);

    await userEvent.click(getByTestId('burgerMenu'));
    await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));
    await userEvent.click(getByTestId('Global-Logout-Mobile'));

    fireEvent(
      window,
      new MessageEvent('message', {
        data: 'userLoggedOut',
        origin: 'http://localhost',
      })
    );

    expect(window.location.assign).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('does not redirect when userLoggedOut arrives without the logout button having been clicked', async () => {
    const { getByTestId } = render(<NavigationMenuMobile {...props} />);

    await userEvent.click(getByTestId('burgerMenu'));

    fireEvent(
      window,
      new MessageEvent('message', {
        data: 'userLoggedOut',
        origin: 'http://localhost',
      })
    );

    expect(window.location.assign).not.toHaveBeenCalled();
  });

  it('should render Bookings link for de', async () => {
    props.currentLanguage = 'de';
    const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    await userEvent.click(getByTestId('burgerMenu'));
    await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));

    const bookingsLink = getByTestId('BookingsButton-Mobile');
    expect(bookingsLink).toBeInTheDocument();
    expect(bookingsLink.tagName).toBe('A');
    expect(bookingsLink).toHaveAttribute('href', '/account/dashboard');
  });

  it('should render ManageModal link', async () => {
    const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
      isLoggedIn: true,
      setIsLoggedIn: setIsLoggedIn,
    });

    await userEvent.click(getByTestId('burgerMenu'));
    await userEvent.click(getByTestId('Global-TriggerManageModal'));

    expect(getByTestId('Global-TriggerManageModal')).toBeInTheDocument();
  });

  describe('Auth0 Integration', () => {
    it('should call navigateToLogin when Auth0 is enabled and login is clicked', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
      });

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

      expect(mockNavigateToLogin).toHaveBeenCalled();
    });

    it('should show legacy login modal when Auth0 is disabled and login is clicked', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: false,
      });

      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

      expect(getByTestId('Global-Login-Mobile')).toBeInTheDocument();
    });

    it('should show legacy login modal when Auth0 is enabled but user clicks login from a business page and PIB login redirect is enabled but businessDomain is empty', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

      expect(getByTestId('Global-Login-Mobile')).toBeInTheDocument();
    });

    it('should not call navigateToLogin when Auth0 is enabled and user clicks login from a business page and PIB login redirect is enabled and businessDomain is not empty', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const mockUseQueryRequestWithBusinessDomain = {
        ...mockUseQueryRequest,
        data: {
          ...mockUseQueryRequest.data,
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

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

      expect(mockNavigateToLogin).not.toHaveBeenCalled();
    });

    it('should use correct login url based on feature toggles and businessDomain value, language and country', async () => {
      const mockNavigateToLogin = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
        release_pib_login_redirect: true,
      });

      const mockUseQueryRequestWithBusinessDomain = {
        ...mockUseQueryRequest,
        data: {
          ...mockUseQueryRequest.data,
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

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

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
        ...mockUseQueryRequest,
        data: {
          ...mockUseQueryRequest.data,
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

      delete (window as any).location;
      (window as any).location = {
        href: '',
      };

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: mockNavigateToLogin,
      });

      delete (window as any).location;
      (window as any).location = {
        href: '',
        pathname: '/business.html',
      };

      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });
      const { getByTestId } = render(<NavigationMenuMobile {...props} />);

      await userEvent.click(getByTestId('burgerMenu'));
      fireEvent.click(getByTestId('Global-Login-Mobile'));

      expect(window.location.href).toBe(
        'https://example.com/business-login/de-de/account/login?intcmp=piLogInModalLink'
      );
    });

    it('should call navigateToLogout when Auth0 is enabled and logout is clicked', async () => {
      const mockNavigateToLogout = jest.fn();

      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: true,
      });

      (useAuth0Navigation as jest.Mock).mockReturnValue({
        navigateToLogin: jest.fn(),
        navigateToLogout: mockNavigateToLogout,
      });

      const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      await userEvent.click(getByTestId('burgerMenu'));
      await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));
      fireEvent.click(getByTestId('Global-Logout-Mobile'));

      expect(mockNavigateToLogout).toHaveBeenCalledTimes(1);
    });

    it('should use iframe postMessage for logout when Auth0 is disabled', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_auth0_login: false,
      });

      const mockAuth = { contentWindow: { postMessage: jest.fn() } };
      document.getElementById = jest.fn().mockReturnValue(mockAuth);

      const { getByTestId } = renderUserContext(<NavigationMenuMobile {...props} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      await userEvent.click(getByTestId('burgerMenu'));
      await userEvent.click(getByTestId('Global-SideNav-UserName-NavItem-Mobile'));
      fireEvent.click(getByTestId('Global-Logout-Mobile'));

      expect(mockAuth.contentWindow.postMessage).toHaveBeenCalledWith(
        '{"action":"logout"}',
        expect.any(String)
      );
    });
  });

  describe('BB user redirect', () => {
    beforeEach(() => {
      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'en', country: 'gb' });
    });

    const mockResponseWithBusinessDomain = {
      ...mockUseQueryRequest,
      data: {
        ...mockUseQueryRequest.data,
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

      renderUserContext(<NavigationMenuMobile {...props} />, {
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

      renderUserContext(<NavigationMenuMobile {...props} />, {
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

      renderUserContext(<NavigationMenuMobile {...props} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe('');
    });

    it('should not redirect when the user is a BB user but businessDomain is not available', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockUseQueryRequest);

      renderUserContext(<NavigationMenuMobile {...props} />, {
        isLoggedIn: true,
        setIsLoggedIn: jest.fn(),
      });

      expect(window.location.href).toBe('');
    });

    it('should not redirect when the user is not logged in', () => {
      (getLoggedInUserInfo as jest.Mock).mockReturnValue({ isBusiness: true });
      (useQueryRequest as jest.Mock).mockReturnValue(mockResponseWithBusinessDomain);

      render(<NavigationMenuMobile {...props} />);

      expect(window.location.href).toBe('');
    });
  });
});
