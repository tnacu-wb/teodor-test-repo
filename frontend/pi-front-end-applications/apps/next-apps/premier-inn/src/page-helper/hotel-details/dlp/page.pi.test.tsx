import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { AnalyticsData, SelectedFilter } from '@whitbread-eos/api';
import preloadAll from 'jest-next-dynamic';
import { act } from 'react-dom/test-utils';

import { visualDisplayContext } from '~mocks/hotel-details';
import { render, screen, userEvent, fireEvent, waitFor } from '~utils/test-utils';

import {
  mockDlpInformation,
  mockGetHotelsInformation,
  mockGetStaticContent,
} from '../../../mocks/destination';
import { INITIAL_NUMBER_OF_RENDERED_CARDS } from './data.pi';
import DestinationLandingPagePI, {
  groupSelectedFilters,
  filterHotels,
  renderNoFilteredHotels,
  displayNoHotelsFoundWarning,
  setFilterQueryParams,
} from './page.pi';

const queryClient = new ReactQuery.QueryClient();
const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();

const mockUseRouter = jest.fn().mockImplementation(() => {
  return { query: { slug: ['england', 'greater-london', 'london.html'] } };
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
}));

jest.mock('next/navigation', () => ({
  useSearchParams: () => {
    return {
      get: (key: string) => key,
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  useCustomLocale: () => mockCustomLocale(),
  useQueryRequest: () => mockGetStaticContent,
  graphQLRequest: () => {
    return {
      getHotelsInformation: mockGetHotelsInformation,
    };
  },
  analytics: {
    update: jest.fn().mockReturnValue({
      dlp: {
        hotels: [
          {
            hotelCode: 'NWCTLE',
            hotelName: 'Newcastle City Centre (The Gate)',
            orderPosition: 1,
          },
        ],
        destinations: [
          {
            destinationLocationName: 'Hotels in London Wembley Stadium',
            destinationPosition: 1,
          },
        ],
        locationName: 'newcastle',
        hotelDisplayedCount: 12,
        filterType: 'recommended',
      },
      pageName: 'premier inn: seo: hotels in newcastle',
    }),
  },
  updateDestinationPageAnalytics: () => ({
    dlp: {
      hotels: [
        {
          hotelCode: 'NWCTLE',
          hotelName: 'Newcastle City Centre (The Gate)',
          orderPosition: 1,
        },
      ],
      destinations: [
        {
          destinationLocationName: 'Hotels in London Wembley Stadium',
          destinationPosition: 1,
        },
      ],
      locationName: 'newcastle',
      hotelDisplayedCount: 12,
      filterType: 'recommended',
    },
    pageName: 'premier inn: seo: hotels in newcastle',
  }),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: () => {
    return <div data-testid="dummy-search-container">PI Search Container</div>;
  },
  DLPHotelCard: jest.fn((props: any) => (
    <div data-testid={props.baseDataTestid || 'DLP-hotel-card'}>DLP Hotel Card</div>
  )),
  MapViewDLPVariant: jest.fn(() => <div>Map View</div>),
}));

jest.mock(
  '@whitbread-eos/molecules/dist/destination/DestinationFaq/DestinationFaq.component',
  () => {
    const mockDestinationFaq = () => {
      return <div data-testid="dummy-destination-faq-component">DestinationFaq</div>;
    };
    return mockDestinationFaq;
  }
);

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  dynamic: { DestinationFaq: () => <div /> },
  DestinationFaq: () => <div />,
  SEO: () => <div />,
}));

const scrollIntoViewMock = jest.fn();

declare global {
  interface Window {
    analyticsData: AnalyticsData;
  }
}

jest.setTimeout(20000);

Object.defineProperty(window, 'analyticsData', {
  value: {
    dlp: {
      hotels: [
        {
          hotelCode: 'NWCTLE',
          hotelName: 'Newcastle City Centre (The Gate)',
          orderPosition: 1,
        },
      ],
      destinations: [
        {
          destinationLocationName: 'Hotels in London Wembley Stadium',
          destinationPosition: 1,
        },
      ],
      locationName: 'newcastle',
      hotelDisplayedCount: 12,
      filterType: 'recommended',
    },
  },
});

const dlpQueryKey = ['dlpInformation', 'en', 'GB', 'england/greater-london/london'];
const hotelsInformationQueryKey = ['getHotelsInformation', 'GB', 'en', [], 1.6178, 54.9783];
const searchInformationQueryKey = ['searchInformation', 'en', 'GB'];

const mockObj = {
  visualDisplayContext,
  queries: [
    { state: { data: { dlpInformation: mockDlpInformation } }, queryKey: dlpQueryKey.toString() },
    {
      state: { data: { getHotelsInformation: mockGetHotelsInformation } },
      queryKey: hotelsInformationQueryKey.toString(),
    },
  ],
  dlpQueryKey,
  hotelsInformationQueryKey,
  searchInformationQueryKey,
};

const mockRouter = {
  push: jest.fn(),
  query: {
    slug: ['england', 'greater-london', 'london'],
  },
  asPath: '/en/hotels/england/greater-london/london.html',
};

const mockRouterWithIncompleteData = {
  push: jest.fn(),
  query: {
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    slug: ['england', 'greater-london', 'london'],
  },
  asPath: '/en/hotels/england/greater-london/london.html',
};

describe('PI Destination Landing Page', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
    preloadAll();

    window.HTMLElement.prototype.scrollIntoView = scrollIntoViewMock;
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockUseFeatureSwitch.mockReturnValue(false);
  });

  it('should render PI page', async () => {
    const { getByTestId } = render(
      <DestinationLandingPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    await waitFor(() => {
      expect(getByTestId('DestinationLandingPIPage-Wrapper')).toBeInTheDocument();
      expect(getByTestId('DestinationLandingPIPage-HotelsList')).not.toHaveTextContent(
        'dlp.description.noHotels'
      );
    });
  });

  it('should render PI page with default settings if no input is present', async () => {
    const { getByTestId } = render(
      <DestinationLandingPagePI
        {...mockObj}
        queryClient={queryClient}
        router={mockRouterWithIncompleteData as any}
      />
    );
    await waitFor(() => {
      expect(getByTestId('DestinationLandingPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render 404 error page if no slug', async () => {
    render(
      <DestinationLandingPagePI
        {...mockObj}
        queryClient={queryClient}
        router={{ ...mockRouter, query: { slug: null } } as any}
      />
    );
    screen.getByRole('heading', { name: /404/i });
    screen.getByRole('heading', { name: /This page could not be found/i });
  });

  it('should reach to Show less button by clicking on Show more until the list ends up', async () => {
    const { getByRole, getAllByTestId } = render(
      <DestinationLandingPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    const showMoreBtn = getByRole('button', { name: 'dlp.hotelCard.showMore' });
    const visibleHotelCardsNumber = getAllByTestId('DLP-hotel-card').length;

    expect(showMoreBtn).toBeInTheDocument();
    expect(visibleHotelCardsNumber).toBe(INITIAL_NUMBER_OF_RENDERED_CARDS);

    userEvent.click(showMoreBtn);

    await waitFor(() => {
      expect(getAllByTestId('DLP-hotel-card').length).toBe(20);
    });

    let showLessBtn;
    act(() => {
      userEvent.click(showMoreBtn);
      jest.advanceTimersByTime(3000);
      showLessBtn = getByRole('button', { name: 'content.showLess' });
    });

    await waitFor(() => {
      expect(showLessBtn).toBeInTheDocument();
      act(() => {
        userEvent.click(showLessBtn);
      });
    });

    await waitFor(() => {
      expect(scrollIntoViewMock).toHaveBeenCalled();
      expect(getAllByTestId('DLP-hotel-card').length).toBe(12);
    });
  });

  it('should display a default message if there are no hotels in the list', async () => {
    const { getByTestId } = render(
      <DestinationLandingPagePI
        {...{
          ...mockObj,
          queries: [
            {
              state: {
                data: {
                  dlpInformation: {
                    ...mockDlpInformation,
                    hotels: [],
                    title: '',
                    description: '',
                    picture: '',
                    breadcrumbs: '',
                    faq: [],
                  },
                },
              },
              queryKey: dlpQueryKey.toString(),
            },
          ],
        }}
        queryClient={queryClient}
        router={mockRouter as any}
      />
    );

    expect(getByTestId('DestinationLandingPIPage-HotelsList')).toHaveTextContent(
      'dlp.description.noHotels'
    );
  });

  it('should change route on click on map button', async () => {
    const { getByTestId } = render(
      <DestinationLandingPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('DestinationLandingPIPageMapGrid-SwitchToggle')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('DestinationLandingPIPageMapGrid-SwitchToggle'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        { pathname: '/gb/en/hotels/england/greater-london/london.html', query: { VIEW: '1' } },
        undefined,
        { shallow: true }
      );
    });

    await act(async () => {
      fireEvent.click(getByTestId('DestinationLandingPIPageMapGrid-SwitchToggle'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        { pathname: '/gb/en/hotels/england/greater-london/london.html', query: { VIEW: '1' } },
        undefined,
        { shallow: true }
      );
    });
  });
});

describe('groupSelectedFilters', () => {
  it('should group filters with single code and label', () => {
    const selectedFilters = [
      { operator: 'AND', codes: ['ACO'], label: 'Air conditioning' },
      { operator: 'OR', codes: ['CPF'], label: 'Free parking' },
    ] as SelectedFilter[];
    const result = groupSelectedFilters(selectedFilters);
    expect(result).toEqual({
      AND: ['ACO'],
      OR: ['CPF'],
    });
  });

  it('should group filters with multiple codes and label', () => {
    const selectedFilters = [
      { operator: 'AND', codes: ['LFT', 'HUL'], label: 'Lift access' },
      { operator: 'OR', codes: ['COP', 'COC'], label: 'Chargeable off-site parking' },
    ] as SelectedFilter[];
    const result = groupSelectedFilters(selectedFilters);
    expect(result).toEqual({
      AND: [['LFT', 'HUL']],
      OR: [['COP', 'COC']],
    });
  });

  it('should mix single and multiple codes with labels', () => {
    const selectedFilters = [
      { operator: 'AND', codes: ['ACO'], label: 'Air conditioning' },
      { operator: 'AND', codes: ['LFT', 'HUL'], label: 'Lift access' },
      { operator: 'OR', codes: ['CPF'], label: 'Free parking' },
      { operator: 'OR', codes: ['COP', 'COC'], label: 'Chargeable off-site parking' },
    ] as SelectedFilter[];
    const result = groupSelectedFilters(selectedFilters);
    expect(result).toEqual({
      AND: ['ACO', ['LFT', 'HUL']],
      OR: ['CPF', ['COP', 'COC']],
    });
  });

  it('should return empty object for empty input', () => {
    const result = groupSelectedFilters([]);
    expect(result).toEqual({});
  });
});

describe('filterHotels', () => {
  const hotelsInformation = [
    {
      name: 'London Euston',
      hotelFacilities: [
        { name: 'Free parking', code: 'CPF' },
        { name: 'Air conditioning', code: 'ACO' },
        { name: 'Lift access', code: 'LFT' },
      ],
    },
    {
      name: 'Manchester Old Trafford',
      hotelFacilities: [
        { name: 'Chargeable on-site parking', code: 'CPP' },
        { name: 'Lift access', code: 'HUL' },
        { name: 'Restaurant', code: 'RES' },
      ],
    },
    {
      name: 'Convent Garden',
      hotelFacilities: [
        { name: 'Chargeable off-site parking', code: 'COP' },
        { name: 'Air conditioning', code: 'ACO' },
        { name: 'Restaurant', code: 'DIN' },
      ],
    },
    {
      name: 'London Beckton',
      hotelFacilities: [
        { name: 'EV Charging point', code: 'EVC' },
        { name: 'Restaurant', code: 'HRS' },
      ],
    },
  ];

  it('filters hotels with OR operator (single code)', () => {
    const groupedFilters = {
      OR: ['CPF'],
    };
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual(['London Euston']);
  });

  it('filters hotels with AND operator (single code)', () => {
    const groupedFilters = {
      AND: ['ACO'],
    };
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual(['London Euston', 'Convent Garden']);
  });

  it('filters hotels with OR operator (multiple codes)', () => {
    const groupedFilters = {
      OR: ['CPF', 'CPP'],
    };
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual(['London Euston', 'Manchester Old Trafford']);
  });

  it('filters hotels with AND operator (multiple codes)', () => {
    const groupedFilters = {
      AND: ['ACO', 'LFT'],
    };
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual(['London Euston']);
  });

  it('filters hotels with both AND and OR operators', () => {
    const groupedFilters = {
      AND: ['ACO', ['RES', 'DIN', 'HRS']],
    };
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual(['Convent Garden']);
  });

  it('returns all hotels if groupedFilters is empty', () => {
    const groupedFilters = {};
    const result = filterHotels(hotelsInformation, groupedFilters);
    expect(result.map((h) => h.name)).toEqual([
      'London Euston',
      'Manchester Old Trafford',
      'Convent Garden',
      'London Beckton',
    ]);
  });

  it('displays no filtered hotels on list', () => {
    const t = jest.fn().mockImplementation((key) => {
      return key;
    });
    const setSelectedFilters = jest.fn();

    renderNoFilteredHotels(t, setSelectedFilters);
    expect(t).toHaveBeenCalledTimes(3);
  });

  it('displays no filtered hotels alert on map', () => {
    const t = jest.fn().mockImplementation((key) => {
      return key;
    });
    displayNoHotelsFoundWarning(t);
    expect(t).toHaveBeenCalledTimes(2);
  });

  it('clears the filter query param if no filters', () => {
    setFilterQueryParams(
      [{ queryParam: 'test', name: 'test', codes: ['test'], operator: 'and' }],
      mockRouter as any,
      'gb',
      'en'
    );
    expect(mockRouter.push).toHaveBeenCalled();
  });
});
