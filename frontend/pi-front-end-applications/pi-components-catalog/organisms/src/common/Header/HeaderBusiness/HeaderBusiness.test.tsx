import { ChakraProvider } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, render } from '@testing-library/react';

import HeaderBusiness from './HeaderBusiness.container';

const mockUseRouter = jest.fn(() => {
  return { push: jest.fn() };
});
const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: () => mockUseQueryRequest,
  useCustomLocale: () => mockCustomLocale(),
  useCompanyDetails: () => ({ requestedCompany: { companyDetails: { companyName: 'test' } } }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(),
}));

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    headerInformation: {
      config: {
        authentication: {
          business: {
            businessAccountLinks: [
              {
                subMenuLinks: [
                  {
                    title: 'Manage employees',
                    url: '/gb/en/business-booker/account/company-management.html#/manage-employees',
                  },
                  {
                    title: 'Booking Allowances',
                    url: '/gb/en/business-booker/account/company-management.html#/booking-allowances',
                  },
                  {
                    title: 'Booking Alerts',
                    url: '/gb/en/business-booker/account/company-management.html#/booking-alerts',
                  },
                  {
                    title: 'Payment options',
                    url: '/gb/en/business-booker/account/company-management.html#/payment-options',
                  },
                  {
                    title: 'Employee Questions',
                    url: '/gb/en/business-booker/account/company-management.html#/employee-questions',
                  },
                  {
                    title: 'Company details',
                    url: '/gb/en/business-booker/account/company-management.html#/company-details',
                  },
                ],
                title: 'Company management',
              },
              {
                subMenuLinks: [
                  {
                    title: 'Management Information Report',
                    url: '/gb/en/business-booker/account/reporting.html#/management-information-report',
                  },
                  {
                    title: 'Emergency report',
                    url: '/gb/en/business-booker/account/reporting.html#/emergency-report',
                  },
                  {
                    title: 'Out of policy report',
                    url: '/gb/en/business-booker/account/reporting.html#/out-of-policy-report',
                  },
                ],
                title: 'Reporting',
              },
            ],
          },
          accountLinks: [
            {
              title: 'Bookings',
              url: '/gb/en/business-booker/account/dashboard.html',
            },
            {
              title: 'Account settings',
              url: '/gb/en/business-booker/account/profile.html',
            },
          ],
        },
      },
      content: {
        menu: { business: 'test123' },
        subNav: [
          {
            navOptions: [
              {
                title: 'Food & drink',
                url: '/gb/en/business-booker/why/food.html?INTCMP=BBtopNav',
              },
              {
                title: 'Our rooms',
                url: '/gb/en/business-booker/sleep/our-rooms.html?INTCMP=BBtopNav',
              },
              {
                title: 'Our rates',
                url: '/gb/en/business-booker/why/rates.html?INTCMP=BBtopNav',
              },
            ],
            title: 'Business Customers',
          },
        ],
        global: {
          brand: {
            hub: 'Hub by Premier Inn',
            hubBadge: null,
            hubLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg',
            pi: 'Premier Inn',
            piLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg',
            pid: null,
            pidLogo: null,
            zip: 'ZIP by Premier Inn',
            zipBadge: null,
            zipLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg',
          },
        },
        countries: [
          {
            language: 'English',
            flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
          },
          {
            language: 'German',
            flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
          },
        ],
        header: {
          image: '/etc/clientlibs/pi-header/resources/images/pi-refresh-logo.svg',
        },
      },
    },
  },
};

const Header = (variant: 'business-default' | 'business-step' | undefined) => {
  return (
    <QueryClientProvider client={new QueryClient()}>
      <HeaderBusiness variant={variant} />
    </QueryClientProvider>
  );
};

describe('HeaderBusiness', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render a <HeaderBusiness> with default props ', function () {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    const { getByTestId, queryByTestId } = render(
      <ChakraProvider>
        <Header />
      </ChakraProvider>
    );
    expect(getByTestId('HeaderBusiness')).toBeTruthy();
    expect(getByTestId('HeaderBusiness-NavigationLinks')).toBeTruthy();
    expect(queryByTestId('logo-container-pi-icon')).toBeTruthy();
    expect(queryByTestId('languageSelectorContainer')).toBeTruthy();

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

  it('should render a <HeaderBusiness> for DE site ', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });
    mockUseRouter.mockReturnValue({
      locale: 'de',
    });
    const { queryByTestId, getByTestId } = render(
      <ChakraProvider>
        <Header />
      </ChakraProvider>
    );
    expect(getByTestId('HeaderBusiness')).toBeTruthy();
    expect(getByTestId('HeaderBusiness-NavigationLinks')).toBeTruthy();
    expect(queryByTestId('HeaderBusiness-Business Account')).toBeFalsy();
    expect(queryByTestId('logo-container-pi-icon')).toBeTruthy();
    expect(queryByTestId('languageSelectorContainer')).toBeTruthy();
  });

  it('should render a <HeaderBusiness> with default variant ', function () {
    const { queryByTestId } = render(
      <ChakraProvider>
        <Header variant="business-default" />
      </ChakraProvider>
    );
    expect(queryByTestId('HeaderBusiness')).toBeTruthy();
    expect(queryByTestId('logo-container-pi-icon')).toBeTruthy();
    expect(queryByTestId('languageSelectorContainer')).toBeTruthy();
  });

  it('should render a <HeaderBusiness> with variant ', function () {
    const { queryByTestId, queryAllByTestId } = render(
      <ChakraProvider>
        <HeaderBusiness variant={'business-step'} />
      </ChakraProvider>
    );

    expect(queryAllByTestId('logo-container').length).toBe(2);
    expect(queryByTestId('languageSelectorContainer')).toBeFalsy();
    expect(queryByTestId('menuWrapperId')).toBeFalsy();
    expect(queryByTestId('progress-indicator-wrapper')).toBeTruthy();
  });

  it('should render the loading text', function () {
    mockUseQueryRequest.isLoading = true;
    const { getByText } = render(
      <ChakraProvider>
        <HeaderBusiness variant={'business-step'} />
      </ChakraProvider>
    );

    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });
});
