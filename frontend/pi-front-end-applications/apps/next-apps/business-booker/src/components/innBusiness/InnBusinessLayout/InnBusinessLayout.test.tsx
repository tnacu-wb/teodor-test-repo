import InnBusinessLayout from '.';
import '@testing-library/jest-dom';
import { act, render } from '@testing-library/react';
import { FT_DYNATRACE_RUM_COOKIE_CONSENT, FT_ONE_TRUST_COOKIE_CONSENT } from '@whitbread-eos/api';
import { syncDynatraceConsentFromCookie } from '@whitbread-eos/utils';

import { ExtendedServerSideProps } from './InnBusinessLayout';

jest.mock('@whitbread-eos/layout', () => ({
  Header: ({ onSearchButtonClick, hideEditSearch, children }: any) => (
    <div
      data-testid="Header"
      onClick={() => {
        onSearchButtonClick();
        hideEditSearch();
      }}
    >
      {children}
    </div>
  ),
  Sidebar: ({ children }: any) => <div data-testid="Sidebar">{children}</div>,
  SidebarMobile: ({ children }: any) => <div data-testid="SidebarMobile">{children}</div>,
  Main: ({ children, id, className }: any) => (
    <div data-testid="Main" data-main-id={id} className={className}>
      {children}
    </div>
  ),
  Footer: () => <div data-testid="Footer-Id"></div>,
  EditSearch: () => <div data-testid="Edit-Search-Id"></div>,
  CookieConsentDialog: ({ children }: any) => (
    <div data-testid="CookieConsentDialog">{children}</div>
  ),
  CookieConsentProvider: ({ children }: any) => <>{children}</>,
}));

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return '/';
  },
  useSearchParams: () => {
    return {
      get: (param: string) => {
        if (param === 'account') {
          return 'test-account-guid';
        }
        return null;
      },
      entries: () => {
        return [];
      },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => {
  const originalModule = jest.requireActual('@whitbread-eos/utils');
  return {
    ...originalModule,
    getCookie: (cookieName: string) => {
      if (cookieName === 'consent_cookie') {
        return '1';
      }
    },
    syncDynatraceConsentFromCookie: jest.fn(),
  };
});

process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL = 'https://onetrust.test/otSDKStub.js';
process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB = 'test-domain-gb';

const mockServerSideProps = {
  labels: {
    content: {
      header: {
        image: '/',
      },
      form: {
        searchIcon: '/',
      },
    },
    layout: {},
  },
  icons: {},
  companyDetails: {
    requestedCompany: {
      companyDetails: {
        companyName: '',
      },
    },
  },
  userDetails: {
    business: {},
    contactDetail: {},
  },
  isTethered: true,
  isCardHolder: true,
  isAccountHolder: true,
} as unknown as ExtendedServerSideProps;

const mockProps = {
  isBusinessBookerPage: false,
  businessBookerPageOptions: {},
  showFooter: false,
  showEditSearch: false,
};

describe('InnBusinessLayout', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.showFooter = false;
    mockProps.showEditSearch = false;
    mockProps.isBusinessBookerPage = false;
  });

  it('should render a <InnBusinessLayout> with children', () => {
    const { getByText, findByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    getByText('test');
    findByTestId('Header');
    findByTestId('Sidebar');
    findByTestId('SidebarMobile');
    findByTestId('Main');
  });

  it('should render a <InnBusinessLayout> with children and footer', () => {
    const { getByText, findByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    getByText('test');
    findByTestId('Header');
    findByTestId('Sidebar');
    findByTestId('SidebarMobile');
    findByTestId('Main');
    findByTestId('IB-Footer');
  });

  it('should render extra elements on HDP', () => {
    mockProps.isBusinessBookerPage = true;
    mockProps.businessBookerPageOptions = {
      isHotelDetailsPage: true,
    };

    const { findByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    findByTestId('Main');
  });

  it('should render footer', () => {
    mockProps.showFooter = true;

    const { findByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    findByTestId('Footer-Id');
  });

  it('should render editSearch', () => {
    mockProps.showEditSearch = true;

    const { findByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    findByTestId('Edit-Search-Id');
  });

  it('should trigger handleSearchClick', async () => {
    const { getByTestId } = render(
      <InnBusinessLayout serverSideProps={mockServerSideProps} {...mockProps}>
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    const header = getByTestId('Header');
    expect(header).toBeInTheDocument();

    await act(async () => {
      header.click();
    });
  });

  describe('CookieConsentDialog', () => {
    it('renders when no consent cookie and OneTrust flag is disabled', () => {
      const { getByTestId } = render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          serverConsentCookie={false}
          featureToggle={{ [FT_ONE_TRUST_COOKIE_CONSENT]: false }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );
      expect(getByTestId('CookieConsentDialog')).toBeInTheDocument();
    });

    it('does not render when OneTrust flag is enabled', () => {
      const { queryByTestId } = render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          serverConsentCookie={false}
          featureToggle={{ [FT_ONE_TRUST_COOKIE_CONSENT]: true }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );
      expect(queryByTestId('CookieConsentDialog')).not.toBeInTheDocument();
    });

    it('does not render when consent cookie is already set', () => {
      const { queryByTestId } = render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          serverConsentCookie={true}
          featureToggle={{ [FT_ONE_TRUST_COOKIE_CONSENT]: false }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );
      expect(queryByTestId('CookieConsentDialog')).not.toBeInTheDocument();
    });
  });

  describe('Dynatrace consent sync', () => {
    it('syncs from the custom consent cookie when OneTrust is disabled', () => {
      render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          featureToggle={{
            [FT_DYNATRACE_RUM_COOKIE_CONSENT]: true,
            [FT_ONE_TRUST_COOKIE_CONSENT]: false,
          }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );

      expect(syncDynatraceConsentFromCookie).toHaveBeenCalledWith({
        isEnabled: true,
        paths: ['/en-gb', '/gb'],
        domain: undefined,
      });
    });

    it('does not sync from the custom consent cookie when OneTrust is enabled', () => {
      render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          featureToggle={{ [FT_ONE_TRUST_COOKIE_CONSENT]: true }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );

      expect(syncDynatraceConsentFromCookie).not.toHaveBeenCalled();
    });

    it('forwards disabled Dynatrace consent when the feature flag is disabled', () => {
      render(
        <InnBusinessLayout
          serverSideProps={mockServerSideProps}
          {...mockProps}
          featureToggle={{
            [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
            [FT_ONE_TRUST_COOKIE_CONSENT]: false,
          }}
        >
          <p>test</p>
        </InnBusinessLayout>,
        {}
      );

      expect(syncDynatraceConsentFromCookie).toHaveBeenCalledWith({
        isEnabled: false,
        paths: ['/en-gb', '/gb'],
        domain: undefined,
      });
    });
  });

  it('should opt in Main as the mobile scroll container when requested', () => {
    const { getByTestId } = render(
      <InnBusinessLayout
        serverSideProps={mockServerSideProps}
        {...mockProps}
        mainId="pib-mobile-main-scroll"
      >
        <p>test</p>
      </InnBusinessLayout>,
      {}
    );

    const main = getByTestId('Main');

    expect(main).toHaveAttribute('data-main-id', 'pib-mobile-main-scroll');
  });
});
