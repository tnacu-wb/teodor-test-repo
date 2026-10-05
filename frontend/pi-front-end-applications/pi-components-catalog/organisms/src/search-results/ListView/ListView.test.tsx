import '@testing-library/jest-dom';
import type { OperaPmsSource, SRVariantType } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../utils/test-utils';
import ListView from './ListView.component';

const mockedPathForURL =
  'hotels/england/greater-london/london/hub-london-tower-bridge.html?ARRdd=24&ARRmm=8&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      asPath: mockedPathForURL,
      query: {
        reservationId: '',
      },
    };
  },
}));

const mockScreenSize = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useScreenSize: () => mockScreenSize(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  InfiniteScroller: ({ children, scrollableTarget }: any) => (
    <div data-testid="InfiniteScroller" data-scrollable-target={scrollableTarget}>
      {children}
    </div>
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockUseTranslation = jest.fn();
const mockUseTranslationResponse = (isvalidContent = true) => {
  return {
    t: (key: string) => {
      if (key === 'search.mlos.ccui.notification') {
        return isvalidContent
          ? 'This hotel has a minimum length of stay restriction in place. Please either add another night or choose a different hotel.'
          : '';
      }
      return '';
    },
  };
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

const pmsSource: OperaPmsSource = 'OPERA';

const mockedHotel = [
  {
    hotelAvailability: {
      lowestRoomRate: {
        currencyCode: 'GBP',
        netTotal: 48.0,
      },
      distance: 9.99,
      unit: 'mile',
      available: true,
      pmsSource: pmsSource,
      limitedAvailability: false,
    },
    name: 'London Edmonton',
    hotelId: 'LONEDM',
    hotelInformation: {
      hotelOpeningDate: '',
      brand: 'PI',
      coordinates: {
        latitude: 51.61348,
        longitude: -0.0466,
      },
      thumbnailImages: [
        {
          imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONEDM/London Edmonton 001.jpg',
          tags: ['exterior'],
        },
      ],
      hotelFacilities: [
        {
          code: 'HAR',
          description: 'Accessible Room',
          icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/HAR.svg',
          isVisible: true,
          name: 'Accessible Room',
          weight: 1,
        },
        {
          code: 'CPF',
          description: 'Free parking',
          icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/CPF.svg',
          isVisible: true,
          name: 'Free parking',
          weight: 1,
        },
        {
          code: 'ACO',
          description: 'Air conditioning',
          icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
          isVisible: true,
          name: 'Air conditioning',
          weight: 5,
        },
      ],
      links: {
        detailsPage: '/england/greater-london/london/london-edmonton',
      },
      messagingFlag: {
        color: '',
        description: '',
        text: '',
      },
    },
  },
];

const mockedmultiSearchParams = {
  arrivalDay: 25,
  arrivalMonth: 11,
  arrivalYear: 2023,
  location: 'London',
  numberOfNights: 4,
  rooms: [{ adultsNumber: 2, childrenNumber: 0, type: 'Double' }],
  bookingChannel: 'WEB',
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  sort: 'DISTANCE',
};

const mockedRoomTypes = ['SB', 'DB'];

const mockedheaderInformation = {
  headerInformation: {
    announcement: {
      text: 'Get all the latest updates on our response to COVID-19',
      type: 'info',
    },
    content: {
      global: {
        brand: {
          hubBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
          zipBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
        },
      },
    },
    results: {
      notifications: {
        errorTitle: 'Oh dear..',
        availabilitiesErrorMessage: 'Something went wrong when we tried to load the results',
      },
    },
  },
};

const mockedPartialTranslations = {
  searchInformation: {
    config: {
      api: {
        initialPageSize: '40',
        lazyLoadPageSize: '10',
        radius: '50',
      },
    },
    content: {
      map: {
        controlText: 'Map View',
        list: 'List view',
      },
      filter: {
        label: {
          restaurant: 'Restaurant',
          airCon: 'Air conditioning',
          chargeableOffsiteParking: 'Chargeable off-site parking',
          freeParking: 'Free parking',
          parking: 'Parking',
          header: 'Filters applied when selected',
          chargeableOnsiteParking: 'Chargeable on-site parking',
          lift: 'Lift access',
          meet: 'Meeting rooms',
          apply: 'Apply filters',
          reset: 'Reset filters',
          facilities: 'Facilities',
        },
        info: {
          lift: 'Some hotels are ground floor only. Please check directly with the hotel (local rate)',
        },
        code: {
          restaurant: 'EAT',
          airCon: 'ACO',
          chargeableOffsiteParking: 'COP',
          freeParking: 'CPF',
          chargeableOnsiteParking: 'CPP',
          lift: 'LFT',
          meet: 'MEE',
        },
      },
      results: {
        menu: {
          listLong: 'List view',
          mapLong: 'Map view',
          distance: 'Distance',
          price: 'Price',
          filtersLong: 'Filter by',
        },
        notifications: {
          fullyBooked:
            'This hotel is fully booked on your chosen dates. Here are some nearby hotels with available rooms.',
          openingSoon:
            'This hotel will be opening soon. Here are some nearby hotels with available rooms.',
          noFilteredHotels: "We couldn't find any hotels that matched your criteria.",
        },
        result: {
          availabilityWarning: 'Last few rooms',
          distanceUnitPlural: 'miles',
          facilities: {
            businessRoom: 'Standard Extra rooms',
            freeParking: 'Free parking',
            noParking: 'No parking available',
            parking: 'Parking',
            premierPlusRoom: 'Premier Plus',
            standardExtraRoom: 'Premier Plus rooms',
          },
          fromLocation: 'from your search',
          fullyBooked: 'Sold out',
          openingOn: 'Opening on',
          openingSoon: 'Open soon',
          priceFrom: 'From',
          viewDetails: 'View details',
        },
      },
      totalHotels: 'Hotels found',
    },
  },
};

const baseProps = {
  isHotelAvailable: { isAvailable: true, isOpeningSoon: false },
  fetchNewHotels: jest.fn(),
  items: mockedHotel,
  hasMore: true,
  resultsMeta: {
    total: 200,
    currentResults: 1,
  },
  partialTranslations: mockedPartialTranslations,
  orderedHotels: mockedHotel,
  roomTypes: mockedRoomTypes,
  multiSearchParams: mockedmultiSearchParams,
  headerInformationData: mockedheaderInformation,
  language: 'en',
  currentPage: 1,
  baseDataTestId: 'ListView',
  variant: 'pi' as SRVariantType,
  featureToggle: {
    isPricePerNightEnabledOnPi: false,
    isPricePerNightEnabledOnBb: false,
    isPricePerNightEnabledOnCcui: false,
  },
};

describe('List hotel view', () => {
  beforeEach(() => {
    mockScreenSize.mockReturnValue({
      isLessThanMd: false,
    });
    mockUseTranslation.mockImplementation(() => {
      return mockUseTranslationResponse();
    });
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render on CCUI the list view for hotels', async () => {
    const { getByText, queryByTestId } = render(<ListView {...baseProps} variant="ccui" />);
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(queryByTestId(`${baseProps.baseDataTestId}-mapView`)).not.toBeInTheDocument();
  });
  it('should render on PI the list view and map view for hotels for medium screens', async () => {
    mockScreenSize.mockReturnValue({
      isLessThanMd: true,
    });
    const { getByText, getByTestId } = render(<ListView {...baseProps} />);
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(getByTestId(`${baseProps.baseDataTestId}-mapView`)).toBeInTheDocument();
  });

  it('should render on BB the list view and map view for hotels for medium screens', async () => {
    mockScreenSize.mockReturnValue({
      isLessThanMd: true,
    });
    const { getByText, getByTestId } = render(<ListView {...baseProps} variant="bb" />);
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(getByTestId(`${baseProps.baseDataTestId}-mapView`)).toBeInTheDocument();
  });
  it('should display is opening soon notification', async () => {
    const { getByText } = render(
      <ListView {...baseProps} isHotelAvailable={{ isAvailable: true, isOpeningSoon: true }} />
    );
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(
      getByText(
        mockedPartialTranslations.searchInformation.content.results.notifications.openingSoon
      )
    ).toBeInTheDocument();
  });
  it('should display fully booked notification', async () => {
    const { getByText } = render(
      <ListView {...baseProps} isHotelAvailable={{ isAvailable: false, isOpeningSoon: false }} />
    );
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(
      getByText(
        mockedPartialTranslations.searchInformation.content.results.notifications.fullyBooked
      )
    ).toBeInTheDocument();
  });
  it('should display MLOS restriction notification', async () => {
    const { getByText } = render(
      <ListView
        {...baseProps}
        isHotelAvailable={{ isAvailable: false, isOpeningSoon: false, hasMlos: true }}
      />
    );
    const { t } = mockUseTranslation();
    expect(getByText('London Edmonton')).toBeInTheDocument();
    expect(getByText(t('search.mlos.ccui.notification'))).toBeInTheDocument();
  });
  it('should display total found hotels from resultsMeta object', async () => {
    const { getByText, getByTestId } = render(<ListView {...baseProps} />);
    expect(getByText('200 Hotels found')).toBeInTheDocument();
    expect(getByTestId('ListView-loading')).toBeInTheDocument();
  });

  it('should display the Accessible Room facility if the user has selected the Accessible roomType', async () => {
    const { getByTestId } = render(<ListView {...baseProps} roomTypes={['DIS']} />);
    expect(getByTestId('Accessible Room')).toBeInTheDocument();
  });

  it('should enable total price for single night when feature is enabled and only 1 night', () => {
    const featureToggle = { ...baseProps.featureToggle, isPricePerNightEnabledOnPi: true };
    const multiSearchParams = { ...baseProps.multiSearchParams, numberOfNights: 1 };
    const resultsMeta = { ...baseProps.resultsMeta, total: 5 };

    const { queryByTestId } = render(
      <ListView
        {...baseProps}
        featureToggle={featureToggle}
        multiSearchParams={multiSearchParams}
        resultsMeta={resultsMeta}
      />
    );
    expect(queryByTestId('ListView-price-per-night-wrapper')).not.toBeInTheDocument();
  });

  it('should enable total price for multi nights when feature is enabled and nights > 1', () => {
    const featureToggle = { ...baseProps.featureToggle, isPricePerNightEnabledOnPi: true };
    const multiSearchParams = { ...baseProps.multiSearchParams, numberOfNights: 3 };
    const resultsMeta = { ...baseProps.resultsMeta, total: 10 };

    const { getByTestId } = render(
      <ListView
        {...baseProps}
        featureToggle={featureToggle}
        multiSearchParams={multiSearchParams}
        resultsMeta={resultsMeta}
      />
    );
    expect(getByTestId('ListView-price-per-night-wrapper')).toBeInTheDocument();
  });

  it('should not enable total price for single night if feature is disabled', () => {
    const featureToggle = { ...baseProps.featureToggle, isPricePerNightEnabledOnPi: false };
    const multiSearchParams = { ...baseProps.multiSearchParams, numberOfNights: 1 };
    const resultsMeta = { ...baseProps.resultsMeta, total: 5 };

    const { queryByTestId } = render(
      <ListView
        {...baseProps}
        featureToggle={featureToggle}
        multiSearchParams={multiSearchParams}
        resultsMeta={resultsMeta}
      />
    );
    expect(queryByTestId('ListView-price-per-night-wrapper')).not.toBeInTheDocument();
  });

  it('should not enable total price for multi nights if nights is 1', () => {
    const featureToggle = { ...baseProps.featureToggle, isPricePerNightEnabledOnPi: true };
    const multiSearchParams = { ...baseProps.multiSearchParams, numberOfNights: 1 };
    const resultsMeta = { ...baseProps.resultsMeta, total: 5 };

    const { queryByTestId } = render(
      <ListView
        {...baseProps}
        featureToggle={featureToggle}
        multiSearchParams={multiSearchParams}
        resultsMeta={resultsMeta}
      />
    );
    expect(queryByTestId('ListView-price-per-night-wrapper')).not.toBeInTheDocument();
  });

  it('should render the mobile map view button when on small screens and variant is PI', () => {
    mockScreenSize.mockReturnValue({ isLessThanMd: true });
    const mockChangeViewType = jest.fn();

    const { getByTestId } = render(
      <ListView {...baseProps} variant="pi" changeViewType={mockChangeViewType} />
    );

    const mapButton = getByTestId('mobile-map-view-button');
    expect(mapButton).toBeInTheDocument();

    mapButton.click();
    expect(mockChangeViewType).toHaveBeenCalled();
  });

  it('should pass scrollableTarget to InfiniteScroller when provided', () => {
    const { getByTestId } = render(
      <ListView {...baseProps} scrollableTarget="pib-mobile-main-scroll" />
    );

    expect(getByTestId('InfiniteScroller')).toHaveAttribute(
      'data-scrollable-target',
      'pib-mobile-main-scroll'
    );
  });
});
