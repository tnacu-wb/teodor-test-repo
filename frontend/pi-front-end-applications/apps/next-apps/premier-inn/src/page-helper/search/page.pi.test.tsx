import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import i18n from 'i18next';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import SearchPagePI from './page.pi';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',
  ns: ['common', 'examples'],
  defaultNS: 'common',
  interpolation: { escapeValue: false },
  resources: { en: { common: {}, examples: {} }, de: { common: {}, examples: {} } },
});

const mockSuggestions = {
  properties: [
    {
      code: 'WEYGAT',
      brand: 'PI',
      suggestion: 'Weymouth',
      geometry: [Object],
    },
  ],
  managedPlaces: [],
  places: [
    {
      suggestion: 'Weymouth, UK',
      placeId: 'ChIJMzUzYv1XckgRN0df2voX6-s',
    },
  ],
};

const mockUseRouter = jest.fn();
const mockCustomLocale = jest.fn();

const mockRouterPush = jest.fn();
const mockRouter = { push: mockRouterPush, query: {} };
const mockGetSuggestions = jest.fn(() => mockSuggestions);

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Icon: () => <div />,
  LoadingSpinner: () => <div />,
  BritishFlagRounded: () => <div />,
  GermanFlagRounded: () => <div />,
  ErrorBoundary: ({ children }: any) => <div>{children}</div>,
  Notification: () => <div />,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  getSearchParams: () => ({ location: 'Weymouth, UK' }),
  getSearchResultsURLQuery: () => ({}),
  PISearchContainer: ({ children }: any) => <div data-testid="Search">{children}</div>,
  SearchResultsPIVariant: ({ children }: any) => (
    <div data-testid="SearchResultsPIVariant">{children}</div>
  ),
  getFallbackSearchPlace: () => ({ PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6' }),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  BackToDetails: () => <div />,
  SEO: () => <div />,
  HotelOpeningInformation: () => <div />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  usePromotionsNotification: () => ({ showPromotionsNotification: true }),
  useFeatureToggle: jest.fn(() => ({
    release_pi_sort_order_dropdown: false,
  })),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => mockGetSuggestions(),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
}));

const queryClient = new ReactQuery.QueryClient();

describe('Search page PI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ language: 'en', country: 'gb' });
  });

  it('should render Search page PI', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(
      <I18nextProvider i18n={i18n}>
        <SearchPagePI router={mockRouter as any} queryClient={queryClient} />
      </I18nextProvider>
    );

    expect(screen.getByTestId('SearchResultsPIVariant')).toBeInTheDocument();
  });

  it('should update router path when no placeId/coordinates information and recieves fallback place information', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(
      <I18nextProvider i18n={i18n}>
        <SearchPagePI
          router={mockRouter as any}
          queryClient={queryClient}
          fallbackSearchPlace={{ PLACEID: '123' }}
        />
      </I18nextProvider>
    );

    expect(mockRouterPush).toHaveBeenCalledWith(
      expect.objectContaining({
        pathname: expect.any(String),
        query: expect.objectContaining({
          PLACEID: '123',
        }),
        search: undefined,
      }),
      undefined,
      { shallow: true }
    );
  });

  it('should update router path when no placeId/coordinates information and no fallback place inforamtion', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(
      <I18nextProvider i18n={i18n}>
        <SearchPagePI router={mockRouter as any} queryClient={queryClient} />
      </I18nextProvider>
    );

    expect(mockRouterPush).toHaveBeenCalledWith(
      expect.objectContaining({
        pathname: expect.any(String),
        query: expect.objectContaining({
          PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6',
        }),
        search: undefined,
      }),
      undefined,
      { shallow: true }
    );
  });
});
