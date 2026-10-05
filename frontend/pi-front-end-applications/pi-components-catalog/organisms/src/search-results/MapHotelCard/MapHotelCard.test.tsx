import '@testing-library/jest-dom';
import { SRMultiSearchParamsType } from '@whitbread-eos/api';

import { fireEvent, render, screen } from '../../utils/test-utils';
import {
  mockedDataHotelCard,
  mockedDataHotelCardWithDisabledHotelFlagBanner,
  mockedDataHotelCardWithEmptyHotelFlagBanner,
  mockedDataHotelCardWithHotelFlagBanner,
  mockedHotelInformation,
  mockedPartialTranslationsHotelCard,
} from '../mockResponse';
import MapHotelCard from './MapHotelCard.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

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

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockMultiSearchParams: SRMultiSearchParamsType = {
  arrivalDay: 1,
  arrivalMonth: 1,
  arrivalYear: 1,
  coordinates: '',
  bookingChannel: '',
  location: '',
  numberOfNights: 1,
  placeId: '',
  rooms: [{ type: '', adultsNumber: 1, childrenNumber: 1 }],
  sort: '',
};

const mockedRoomTypes = ['SB', 'DB', 'DIS'];
const defaultProps = {
  data: mockedDataHotelCard,
  partialTranslations: mockedPartialTranslationsHotelCard,
  roomTypes: mockedRoomTypes,
  locale: 'en',
  multiSearchParams: mockMultiSearchParams,
  variant: 'pi',
};

describe('SRP - MapHotelCard', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(() => null),
        setItem: jest.fn(() => null),
      },
      writable: true,
    });
  });
  it(`should render the card with the hotel's name`, () => {
    const { getByText } = render(<MapHotelCard {...defaultProps} />);
    expect(getByText('5 miles from your search')).toBeInTheDocument();
  });
  it(`should render the hotelThumbnail`, () => {
    const { getAllByRole } = render(<MapHotelCard {...defaultProps} />);
    expect(getAllByRole('img')[0]).toBeInTheDocument();
  });
  it(`should render the hotelCard with sold out label`, () => {
    const { getByText } = render(
      <MapHotelCard
        {...defaultProps}
        data={{
          ...mockedDataHotelCard,
          hotelAvailability: {
            ...mockedDataHotelCard.hotelAvailability,
            available: false,
            limitedAvailability: false,
          },
        }}
      />
    );
    expect(getByText('Sold out')).toBeInTheDocument();
  });

  it('should render the hotelCard with Discount applied label (for Employee offer)', () => {
    defaultProps.multiSearchParams.cellCodes = ['EMP01'];
    const { getByText } = render(
      <MapHotelCard
        {...defaultProps}
        data={{
          ...mockedDataHotelCard,
          hotelAvailability: {
            ...mockedDataHotelCard.hotelAvailability,
            cellCode: 'EMP01',
            available: true,
          },
          hotelInformation: {
            ...mockedHotelInformation,
            hotelOpeningDate: '',
          },
        }}
      />
    );
    expect(getByText('Discount applied')).toBeInTheDocument();
  });

  it('should render the hotelCard button', () => {
    const { getByRole } = render(<MapHotelCard {...defaultProps} />);
    expect(getByRole('button')).toBeInTheDocument();
  });

  it('appends the combined_map_tile tracking param to the hotel details link when in split view', () => {
    const { getByRole } = render(<MapHotelCard {...defaultProps} isSplitView />);
    expect(getByRole('link')).toHaveAttribute(
      'href',
      expect.stringContaining('&intcmp=combined_map_tile')
    );
  });

  it('does not append the combined_map_tile tracking param outside of split view', () => {
    const { getByRole } = render(<MapHotelCard {...defaultProps} />);
    expect(getByRole('link')).not.toHaveAttribute(
      'href',
      expect.stringContaining('&intcmp=combined_map_tile')
    );
  });
  it('should render hotel flag banner text and describe the hotel card link when enabled', () => {
    const bannerText = 'New restaurant now open';
    const hotelFlagBannerId = 'SRPMapView-hotel-123-hotel-flag-banner';

    render(<MapHotelCard {...defaultProps} data={mockedDataHotelCardWithHotelFlagBanner} />);

    const banner = screen.getByTestId('SRPMapView-hotel-flag-banner');

    expect(banner).toHaveAttribute('id', hotelFlagBannerId);
    expect(banner).toHaveTextContent(bannerText);
    expect(banner).not.toHaveAttribute('tabindex');
    expect(banner.querySelector('img[alt=""]')).toBeInTheDocument();
    expect(screen.getByRole('link')).toHaveAttribute('aria-describedby', hotelFlagBannerId);
  });

  it('should not render hotel flag banner or describe the hotel card link when there is no banner text', () => {
    render(<MapHotelCard {...defaultProps} data={mockedDataHotelCardWithEmptyHotelFlagBanner} />);

    expect(screen.queryByTestId('SRPMapView-hotel-flag-banner')).not.toBeInTheDocument();
    expect(screen.getByRole('link')).not.toHaveAttribute('aria-describedby');
  });

  it('should not render hotel flag banner or describe the hotel card link when disabled', () => {
    render(
      <MapHotelCard {...defaultProps} data={mockedDataHotelCardWithDisabledHotelFlagBanner} />
    );

    expect(screen.queryByTestId('SRPMapView-hotel-flag-banner')).not.toBeInTheDocument();
    expect(screen.getByRole('link')).not.toHaveAttribute('aria-describedby');
  });

  it('should not render the component if there are not any translations', () => {
    const { getByText } = render(<MapHotelCard {...defaultProps} />);
    expect(getByText('5 miles from your search')).toBeInTheDocument();
  });

  it('Should call localStorage getItem on render for each storage call', () => {
    render(<MapHotelCard {...defaultProps} />);
    expect(window.localStorage.getItem).toHaveBeenCalledTimes(2);
  });

  it('Should call localStorage setItem on click', async () => {
    render(<MapHotelCard {...defaultProps} />);

    const anchorElement = screen.getByRole('link');
    fireEvent.click(anchorElement);

    expect(window.localStorage.setItem).toHaveBeenCalledTimes(2);
    expect(window.localStorage.setItem).toHaveBeenCalledWith(
      'DistanceFromSearch',
      JSON.stringify({ data: { distance: mockedDataHotelCard?.hotelAvailability?.distance } })
    );
    expect(window.localStorage.setItem).toHaveBeenCalledWith(
      'SearchReferrer',
      JSON.stringify({ data: { referrer: mockedPathForURL } })
    );
  });
});
