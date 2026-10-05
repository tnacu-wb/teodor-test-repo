import '@testing-library/jest-dom';
import type { MessagingFlag } from '@whitbread-eos/api';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import {
  BIGGER_ROOM_CODE,
  NEW_HOTEL_MESSAGING_FLAG_TEXT,
  NEW_ROOMS_MESSAGING_FLAG_TEXT,
  OPENING_SOON_MESSAGING_FLAG_TEXT,
  PREMIER_EXTRA_CODE,
  PREMIER_PLUS_FACILITY_CODE,
  STANDARD_EXTRA_FACILITY_CODE,
} from '../../utils/constants';
import { render } from '../../utils/test-utils';
import HotelBadges, { getColor } from './HotelBadges';

const mockFacilities = [
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 1,
  },
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 3,
  },
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 2,
  },
  {
    code: STANDARD_EXTRA_FACILITY_CODE,
    description: 'Standard Extra',
    icon: '',
    isVisible: true,
    name: 'Standard Extra',
    weight: 2,
  },
  {
    code: STANDARD_EXTRA_FACILITY_CODE,
    description: 'Standard Extra',
    icon: '',
    isVisible: false,
    name: 'Standard Extra',
    weight: 2,
  },
];

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: jest.fn(() => ({
    brand: 'PI',
    hotelFacilities: mockFacilities,
    messagingFlag: { color: '', description: '', text: 'Open hotel' },
    isLoading: false,
    isError: false,
    error: null,
  })),
}));

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;

describe('HotelBadges', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'PI',
      hotelFacilities: mockFacilities,
      messagingFlag: { color: '', description: '', text: 'Open hotel' },
      isLoading: false,
      isError: false,
      error: null,
    });
  });

  it('should render HotelBadge', () => {
    const { getByTestId } = render(
      <HotelBadges hubBadge="/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg" />
    );
    expect(getByTestId('hdp_badgesList')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'PI',
      hotelFacilities: mockFacilities,
      messagingFlag: { color: '', description: '', text: 'Open hotel' },
      isLoading: true,
      isError: false,
      error: null,
    });
    const { getByText } = render(
      <HotelBadges hubBadge="/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg" />
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render the associated error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'PI',
      hotelFacilities: mockFacilities,
      messagingFlag: { color: '', description: '', text: 'Open hotel' },
      isLoading: false,
      isError: true,
      error: { message: 'Unable to load tags.' },
    });
    const { getByText } = render(
      <HotelBadges hubBadge="/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg" />
    );
    expect(getByText('Unable to load tags.')).toBeInTheDocument();
  });

  it('should render the hotel badges', () => {
    const { getAllByRole } = render(
      <HotelBadges hubBadge="/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg" />
    );
    expect(getAllByRole('listitem').length).toBe(5);
  });

  it('should render nothing if the hotel has no badges', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'PI',
      hotelFacilities: [],
      messagingFlag: {} as MessagingFlag,
      isLoading: false,
      isError: false,
      error: null,
    });
    const { queryAllByRole } = render(<HotelBadges hubBadge="" />);
    expect(queryAllByRole('listitem').length).toBe(0);
  });

  it('should render hub badge', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'HUB',
      hotelFacilities: [],
      messagingFlag: {} as MessagingFlag,
      isLoading: false,
      isError: false,
      error: null,
    });
    const { queryAllByRole } = render(
      <HotelBadges hubBadge="/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg" />
    );
    expect(queryAllByRole('listitem').length).toBe(1);
  });

  describe('Flag banner', () => {
    it('should render flag banner badge when hotelFlags.isEnabled is true and flagBanner is set', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        brand: 'PI',
        hotelFacilities: mockFacilities,
        messagingFlag: { color: '', description: '', text: 'Open hotel' },
        isLoading: false,
        isError: false,
        error: null,
        hotelFlags: {
          isEnabled: true,
          flagBanner: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Special Offer',
            backgroundImage: null,
          },
        },
      });
      const { getByText } = render(<HotelBadges hubBadge="" />);
      expect(getByText('Special Offer')).toBeInTheDocument();
    });

    it('should not render flag banner badge when hotelFlags.isEnabled is false', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        brand: 'PI',
        hotelFacilities: [],
        messagingFlag: {} as MessagingFlag,
        isLoading: false,
        isError: false,
        error: null,
        hotelFlags: {
          isEnabled: false,
          flagBanner: {
            text: 'Special Offer',
          },
        },
      });
      const { queryByText } = render(<HotelBadges hubBadge="" />);
      expect(queryByText('Special Offer')).not.toBeInTheDocument();
    });

    it('should not render flag banner badge when hotelFlags.flagBanner is not set', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        brand: 'PI',
        hotelFacilities: [],
        messagingFlag: {} as MessagingFlag,
        isLoading: false,
        isError: false,
        error: null,
        hotelFlags: {
          isEnabled: true,
          flagBanner: null,
        },
      });
      const { queryByTestId } = render(<HotelBadges hubBadge="" />);
      expect(queryByTestId('hdp_badgesList')).not.toBeInTheDocument();
    });

    it('should render flag banner icon when flagBanner.backgroundImage is set', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        brand: 'PI',
        hotelFacilities: [],
        messagingFlag: {} as MessagingFlag,
        isLoading: false,
        isError: false,
        error: null,
        hotelFlags: {
          isEnabled: true,
          flagBanner: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Special Offer',
            backgroundImage: '/images/flag-banner-icon.png',
          },
        },
      });
      const { getByRole } = render(<HotelBadges hubBadge="" />);
      expect(getByRole('img')).toBeInTheDocument();
    });
  });

  it('should return the correct color based on facility code', () => {
    expect(getColor(BIGGER_ROOM_CODE)).toBe('lightPurple');
    expect(getColor(PREMIER_PLUS_FACILITY_CODE)).toBe('primary');
    expect(getColor(NEW_HOTEL_MESSAGING_FLAG_TEXT)).toBe('maroon');
    expect(getColor(NEW_ROOMS_MESSAGING_FLAG_TEXT)).toBe('maroon');
    expect(getColor(OPENING_SOON_MESSAGING_FLAG_TEXT)).toBe('maroon');
    expect(getColor(PREMIER_EXTRA_CODE)).toBe('blue');
    expect(getColor('default')).toBe('darkPink');
  });
});
