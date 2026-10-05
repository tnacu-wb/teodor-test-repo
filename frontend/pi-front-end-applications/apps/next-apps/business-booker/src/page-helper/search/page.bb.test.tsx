import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import i18n from 'i18next';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import SearchPageBB from './page.bb';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',

  ns: ['common', 'examples'],
  defaultNS: 'common',

  interpolation: {
    escapeValue: false,
  },

  resources: {
    en: {
      common: {},
      examples: {},
    },
    de: {
      common: {},
      examples: {},
    },
  },
});

const mockUseRouter = jest.fn();
function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];
  if (typeof key === 'string') {
    switch (key) {
      case 'searchInformation':
        return {
          data: { searchInformation: 'search info' },
        };
      case 'hotelAvailabilities':
        return {
          data: { hotelAvailabilities: 'availabilities' },
        };
      case 'GetStaticContent':
        return {
          data: {
            headerInformation: {},
            footer: {},
            labels: {},
          },
        };
      case 'getSearchRules':
        return {
          data: {
            maxNightsLimitation: {
              maxNights: 9,
            },
          },
        };
    }
  }
}
const mockCustomLocale = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));
jest.mock('@whitbread-eos/atoms', () => ({
  LoadingSpinner: () => <div />,
  Icon: () => <div />,
  BritishFlagRounded: () => <div />,
  GermanFlagRounded: () => <div />,
  ErrorBoundary: ({ children }: any) => <div>{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  getSearchParams: () => ({}),
  AgentOverrideModal: () => <div />,
  BBSearchContainer: ({ children }: any) => <div data-testid="Search">{children}</div>,
  SearchResultsBBVariant: ({ children }: any) => (
    <div data-testid="SearchResultsBBVariant">{children}</div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
  setAnalyticsUser: jest.fn(),
  graphQLRequest: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
  useFeatureToggle: jest.fn(() => ({
    release_bb_sort_order_dropdown: false,
  })),
}));
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  setAnalyticsUser: jest.fn(),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  BackToDetails: () => <div />,
  SEO: () => <div></div>,
}));

const queryClient = new ReactQuery.QueryClient();

describe('Search page BB', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
  });
  it('should render Search page BB with SUPER access', async () => {
    const mockRouter = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const accessLevel = BUSINESS_BOOKER_USER_ROLES.SUPER;

    const { getByTestId } = render(
      <I18nextProvider i18n={i18n}>
        <SearchPageBB
          router={mockRouter as any}
          queryClient={queryClient}
          variant="bb"
          accessLevel={accessLevel}
        />
      </I18nextProvider>
    );
    expect(getByTestId('SearchResultsBBVariant')).toBeInTheDocument();
  });
  it('should render Search page BB with accessLevel undefined', async () => {
    const mockRouter = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const accessLevel = BUSINESS_BOOKER_USER_ROLES.STAYER;

    render(
      <I18nextProvider i18n={i18n}>
        <SearchPageBB
          router={mockRouter as any}
          queryClient={queryClient}
          variant="bb"
          accessLevel={accessLevel}
          fallbackSearchPlace={{ PLACEID: '123' }}
        />
      </I18nextProvider>
    );

    expect(mockRouter.push).toHaveBeenCalledWith('/gb/gb/business-booker');
  });
});
