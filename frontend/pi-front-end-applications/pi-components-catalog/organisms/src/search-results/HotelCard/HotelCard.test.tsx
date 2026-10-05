import '@testing-library/jest-dom';
import { FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES, SRMultiSearchParamsType } from '@whitbread-eos/api';

import { fireEvent, render, screen } from '../../utils/test-utils';
import {
  mockedDataHotelCard,
  mockedDataHotelCardWithDisabledHotelFlagBanner,
  mockedDataHotelCardWithHotelFlagBanner,
  mockedHotelInformation,
  mockedPartialTranslationsHotelCard,
} from '../mockResponse';
import HotelCard, { Props } from './HotelCard.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockUseFeatureToggle = jest.fn();
const mockScreenSize = jest.fn();
const mockSemanticTypography = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useScreenSize: () => mockScreenSize(),
  useFeatureToggle: () => mockUseFeatureToggle(),
  useSemanticTypography: () => mockSemanticTypography,
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
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
const defaultProps: Props = {
  data: mockedDataHotelCard,
  partialTranslations: mockedPartialTranslationsHotelCard,
  roomTypes: mockedRoomTypes,
  locale: 'en',
  multiSearchParams: mockMultiSearchParams,
  variant: 'pi',
  pricePerNight: false,
  responsive: { mobile: true, xs: false, sm: false },
};

describe('SRP - HotelCard', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(() => null),
        setItem: jest.fn(() => null),
      },
      writable: true,
    });
    mockScreenSize.mockReturnValue({
      isLessThanMd: false,
    });
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES]: false });
    mockSemanticTypography.mockImplementation((legacyStyles, semanticStyles) =>
      semanticStyles?.textStyle ? semanticStyles : legacyStyles
    );
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render the hotelCard', () => {
    const { getByTestId } = render(<HotelCard {...defaultProps} />);
    expect(getByTestId('SRP-hotel-card')).toBeInTheDocument();
  });

  it('adds a hotelcode attribute matching the hotel id, for analytics tracking', () => {
    const { getByTestId } = render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: false,
          xs: false,
          sm: false,
        }}
        data={{ ...mockedDataHotelCard, hotelId: 'LONCOU' }}
      />
    );
    expect(getByTestId('SRP-hotel-card')).toHaveAttribute('hotelcode', 'LONCOU');
  });

  it('should render the hotelCard for medium size screens', () => {
    const { getByTestId } = render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: false,
          xs: false,
          sm: true,
        }}
      />
    );
    expect(getByTestId('SRP-hotel-card')).toBeInTheDocument();
  });

  it('should render the hotelCard button', () => {
    const { getByTestId } = render(<HotelCard {...defaultProps} />);
    expect(getByTestId('SRP-hotel-button')).toBeInTheDocument();
  });

  it('should render the hotelCard button with view details text', () => {
    render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: false,
          xs: false,
          sm: false,
        }}
      />
    );
    const buttonText = screen.getByText('View details');
    expect(buttonText).toBeInTheDocument();
  });

  it('should render hotel flag banner text and describe the hotel card link when enabled', () => {
    const bannerText = 'New restaurant now open';
    const hotelFlagBannerId = 'SRP-hotel-123-hotel-flag-banner';

    render(<HotelCard {...defaultProps} data={mockedDataHotelCardWithHotelFlagBanner} />);

    const banner = screen.getByTestId('SRP-hotel-flag-banner');

    expect(banner).toHaveAttribute('id', hotelFlagBannerId);
    expect(banner).toHaveTextContent(bannerText);
    expect(banner).not.toHaveAttribute('tabindex');
    expect(banner.querySelector('img[alt=""]')).toBeInTheDocument();
    expect(screen.getByRole('link')).toHaveAttribute('aria-describedby', hotelFlagBannerId);
  });

  it('should not render hotel flag banner or describe the hotel card link when disabled', () => {
    render(<HotelCard {...defaultProps} data={mockedDataHotelCardWithDisabledHotelFlagBanner} />);

    expect(screen.queryByTestId('SRP-hotel-flag-banner')).not.toBeInTheDocument();
    expect(screen.getByRole('link')).not.toHaveAttribute('aria-describedby');
  });

  it('should resolve label-xl semantic typography for the hotel card button', () => {
    render(<HotelCard {...defaultProps} />);

    expect(mockSemanticTypography).toHaveBeenCalledWith(
      expect.any(Object),
      expect.objectContaining({ textStyle: 'label-xl' })
    );
  });

  it('should render the hotelCard with Open soon badge', () => {
    const { getByText } = render(
      <HotelCard
        {...defaultProps}
        data={{
          ...mockedDataHotelCard,
          hotelInformation: {
            ...mockedHotelInformation,
            hotelOpeningDate: new Date().toDateString(),
          },
        }}
      />
    );
    expect(getByText('Open soon')).toBeInTheDocument();
  });

  it('should render the hotelCard with Discount applied label (for Employee offer)', () => {
    defaultProps.multiSearchParams.cellCodes = ['EMP01'];
    const { getByText } = render(
      <HotelCard
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

  it('Should call localStorage getItem on render for each storage call', () => {
    render(<HotelCard {...defaultProps} />);
    expect(window.localStorage.getItem).toHaveBeenCalledTimes(2);
  });

  it('Should call localStorage setItem on click', async () => {
    render(<HotelCard {...defaultProps} />);

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

  it('should render single image thumbnail when SRP multiple image feature flag is disabled', () => {
    const { getByTestId, queryByTestId } = render(<HotelCard {...defaultProps} />);

    expect(getByTestId('SRP-hotel-thumbnail')).toBeInTheDocument();
    expect(queryByTestId('SRP-hotel-thumbnail-carousel')).not.toBeInTheDocument();
  });

  it('should render thumbnail carousel when SRP multiple image feature flag is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES]: true });
    const { getByTestId, queryByTestId } = render(<HotelCard {...defaultProps} />);

    expect(getByTestId('SRP-hotel-thumbnail-carousel')).toBeInTheDocument();
    expect(queryByTestId('SRP-hotel-thumbnail')).not.toBeInTheDocument();
  });
});

describe('responsive layouts', () => {
  beforeEach(() => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES]: false });
    mockSemanticTypography.mockImplementation((legacyStyles, semanticStyles) =>
      semanticStyles?.textStyle ? semanticStyles : legacyStyles
    );
  });
  it('should render the mobile layout for <=374px screens', () => {
    render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: true,
          xs: false,
          sm: false,
        }}
      />
    );

    expect(screen.getByTestId('SRP-hotel-card')).toBeInTheDocument();
  });

  it('should render the compact tablet layout for 576–767px screens', () => {
    render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: false,
          xs: false,
          sm: true,
        }}
      />
    );

    expect(screen.getByTestId('SRP-hotel-button')).toBeInTheDocument();
    expect(screen.queryByText('View details')).not.toBeInTheDocument();

    expect(screen.getByTestId('SRP-hotel-facilities')).toBeInTheDocument();
  });

  it('should render the desktop layout above the tablet breakpoint', () => {
    render(
      <HotelCard
        {...defaultProps}
        responsive={{
          mobile: false,
          xs: false,
          sm: false,
        }}
      />
    );

    expect(screen.getByText('View details')).toBeInTheDocument();
  });
});
