import '@testing-library/jest-dom';
import * as utils from '@whitbread-eos/utils';

import { userEvent, render, screen } from '../utils/test-utils';
import { ONE_YEAR_IN_MINUTES } from './CookiePolicies.constants';
import CookiePoliciesModalContainer, {
  type CookiePoliciesModalContainerProps,
} from './CookiePoliciesModalContainer.component';

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  isSuccess: true,
  data: {
    cookieConsent: {
      cookiePolicies: {
        brand: 'pi',
        introView: {
          acceptAllButtonText: 'Accept all cookies',
          description:
            'Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you. If you’re happy with this, you can ‘accept all cookies’, or to find out more, click ‘manage cookies’.',
          manageButtonText: 'Manage cookies',
          title: 'Cookies and how we use them',
        },
        manageView: {
          alwaysActiveText: 'Always Active',
          description:
            '<p>Choose the cookies that work for you. If you need more information, please see our cookies notice.</p>\n',
          saveSettingsButtonText: 'Confirm settings',
          title: 'Manage cookies',
          cookieGroup: [
            {
              cookieName: 'permissionEssential',
              description:
                '<p>Some cookies are essential – our website wouldn’t work without them! We collect them to keep our website secure and ensure that from browsing to booking, your online experience runs smoothly.</p>\n',
              isAlwaysActive: true,
              title: 'Essential',
              toggleLabel: 'Essentials are always active.',
            },
            {
              cookieName: 'permissionPerformance',
              description:
                '<p>These cookies help us understand user experiences. We’ll never use them to identify you personally – just to monitor how well our website is working and if there’s any room for improvement.</p>\n',
              isAlwaysActive: false,
              title: 'Performance (Recommended) ',
              toggleLabel:
                'Button to choose whether to allow or disallow us to collect performance cookies.',
            },
            {
              cookieName: 'permissionExperience',
              description:
                '<p>We use these cookies to personalise your experience – tailoring content throughout your visit. We also use these cookies to test new website features and improve functionality across our website.</p>\n',
              isAlwaysActive: false,
              title: 'Experience',
              toggleLabel:
                'Button to choose whether to allow or disallow us to collect experience cookies.',
            },
            {
              cookieName: 'permissionMarketing',
              description:
                '<p>These cookies allow us to tailor the advertising you receive from Premier Inn. Without these cookies you would still receive adverts – they would just be less relevant to you.</p>\n',
              isAlwaysActive: false,
              title: 'Marketing',
              toggleLabel:
                'choose the cookies that work for you. If you need more information, please see our cookies notice. ',
            },
          ],
        },
        config: {
          cookieOptInExpiryDays: 365,
          cookieOptOutExpiryDays: 30,
        },
      },
    },
  },
};

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    setCookie: jest.fn(),
    syncDynatraceConsent: jest.fn(),
    useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
    useQueryRequest: () => mockResponse,
  };
});

const mockProps: CookiePoliciesModalContainerProps = {
  onClose: jest.fn(),
  isOpen: true,
  brand: 'pi',
  isDynatraceRumCookieConsentEnabled: true,
};

describe('CookiePoliciesModalContainer', () => {
  it('should render CookiePoliciesModalContainer', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByTestId } = render(<CookiePoliciesModalContainer {...mockProps} />);
    expect(getByTestId('CookiePoliciesModal-Container')).toBeInTheDocument();
  });

  it('should render the cookie policy description', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByTestId } = render(<CookiePoliciesModalContainer {...mockProps} />);
    expect(getByTestId('CookiePoliciesModal-Description')).toBeInTheDocument();
  });

  it('should have a Manage button', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByRole } = render(<CookiePoliciesModalContainer {...mockProps} />);
    expect(getByRole('button', { name: 'Manage cookies' })).toBeInTheDocument();
  });

  it('should have an Accept All button', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByRole } = render(<CookiePoliciesModalContainer {...mockProps} />);
    expect(getByRole('button', { name: 'Accept all cookies' })).toBeInTheDocument();
  });

  it('should display Manage Cookies Modal', async () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByText, getByTestId } = render(<CookiePoliciesModalContainer {...mockProps} />);
    const manageCookieBtn = getByText('Manage cookies');
    await userEvent.click(manageCookieBtn);
    expect(getByTestId('ManageCookieModal-Container')).toBeInTheDocument();
  });

  it('should cancel CookiePolicies when pressing Accept cookies btn', async () => {
    const { getByTestId } = render(<CookiePoliciesModalContainer {...mockProps} />);
    const acceptCookiesBtn = getByTestId('CookiePoliciesModal-AcceptAllButton');
    userEvent.click(acceptCookiesBtn);
    expect(mockProps.onClose).toBeCalledTimes(1);
  });
});

describe('CookiePoliciesModalContainer', () => {
  const OLD_ENV = process.env;
  const cookieNames = ['permissionPerformance', 'permissionExperience', 'permissionMarketing'];

  beforeEach(() => {
    jest.clearAllMocks();
    process.env = { ...OLD_ENV, NEXT_PUBLIC_COOKIES_DOMAIN: '.premierinn.com' };
  });
  afterAll(() => {
    process.env = OLD_ENV;
  });

  it('calls setCookie with correct cookieDomain', () => {
    const onClose = jest.fn();
    render(<CookiePoliciesModalContainer {...mockProps} onClose={onClose} />);

    // Accept all cookies
    const acceptButton = screen.getByTestId('CookiePoliciesModal-AcceptAllButton');
    userEvent.click(acceptButton);

    const paths = ['/gb', '/en-gb'];
    cookieNames.forEach((name) => {
      paths.forEach((path) => {
        expect(utils.setCookie).toHaveBeenCalledWith(
          name,
          true,
          ONE_YEAR_IN_MINUTES,
          path,
          undefined,
          '.premierinn.com'
        );
      });
    });
    expect(utils.syncDynatraceConsent).toHaveBeenCalledWith({
      hasConsent: true,
      isEnabled: true,
      expiryMinutes: ONE_YEAR_IN_MINUTES,
      paths: ['/en-gb', '/gb'],
      domain: '.premierinn.com',
    });
  });

  it('calls setCookie with correct cookieDomain for de locale', () => {
    mockUseRouter.mockReturnValue({ locale: 'de' });
    (utils.useCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });

    const onClose = jest.fn();
    render(<CookiePoliciesModalContainer {...mockProps} onClose={onClose} />);

    const acceptButton = screen.getByTestId('CookiePoliciesModal-AcceptAllButton');
    userEvent.click(acceptButton);

    const paths = ['/de', '/de-de'];
    cookieNames.forEach((name) => {
      paths.forEach((path) => {
        expect(utils.setCookie).toHaveBeenCalledWith(
          name,
          true,
          ONE_YEAR_IN_MINUTES,
          path,
          undefined,
          '.premierinn.com'
        );
      });
    });
  });

  it('calls setCookie with correct cookieDomain which includes 30 days cookie duration', async () => {
    mockUseRouter.mockReturnValue({ locale: 'gb' });
    (utils.useCustomLocale as jest.Mock).mockReturnValue({ language: 'en', country: 'gb' });

    const onClose = jest.fn();
    render(<CookiePoliciesModalContainer {...mockProps} onClose={onClose} />);

    const manageButton = screen.getByTestId('CookiePoliciesModal-ManageButton');
    userEvent.click(manageButton);

    const saveButton = screen.findByTestId('ManageCookieModal-Confirm settings');
    userEvent.click(await saveButton);

    const paths = ['/gb', '/en-gb'];
    cookieNames.forEach((name) => {
      paths.forEach((path) => {
        expect(utils.setCookie).toHaveBeenCalledWith(
          name,
          false,
          43200,
          path,
          undefined,
          '.premierinn.com'
        );
      });
    });
    expect(utils.syncDynatraceConsent).toHaveBeenCalledWith({
      hasConsent: false,
      isEnabled: true,
      expiryMinutes: 43200,
      paths: ['/en-gb', '/gb'],
      domain: '.premierinn.com',
    });
  });
});
