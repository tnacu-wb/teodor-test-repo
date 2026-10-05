import '@testing-library/jest-dom';
import {
  RoomConfiguration,
  ROOM_TYPE,
  FT_PI_BB_CCUI_ROOMS_DISCLAIMER,
  FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION,
} from '@whitbread-eos/api';
import React from 'react';

import { render, screen, userEvent } from '../../utils/test-utils';
import HotelRoomsComponent from './HotelRooms.component';
import HotelRoomsContainer from './HotelRooms.container';

const mockCookies = {
  bundles: 'class',
};

const mockFlags = {
  release_pi_display_soft_bundles: false,
  release_semantic_typography_tokenization: false,
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useFeatureToggle: () => ({
      ...mockFlags,
    }),
    useSemanticTypography: () => (legacyTypography: object, semanticTypography: object) =>
      mockFlags.release_semantic_typography_tokenization ? semanticTypography : legacyTypography,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useQueryRequest: () => ({
      isLoading: false,
      isError: false,
      data: mockTabsData,
      error: '',
    }),
    useStaticHotelInformation: () => ({
      roomConfiguration: mockTabsData,
      hotelId: 'LONEUS',
      brand: 'PI',
    }),
  };
});

const mockUseRouter = jest.fn(() => {
  return { push: jest.fn() };
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockFacility = {
  weight: 0,
  name: '',
  isVisible: true,
  icon: '',
  description: '',
  code: '',
};

const mockImage = {
  alt: '',
  caption: '',
  iconSrc: '',
  imageSrc: '',
  thumbnailSrc: '',
};

export const mockTabItems = [
  {
    roomType: 'double',
    roomName: 'Standard double',
    roomDescription:
      "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.",
    images: [mockImage],
    facilities: [mockFacility],
  },
  {
    roomType: 'double',
    roomName: 'Premier Plus double',
    roomDescription:
      'Our enhanced room design. Includes Ultimate Wi-Fi, Nespresso machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
    images: [mockImage],
    facilities: [mockFacility],
  },
  {
    roomType: 'accessible',
    roomName: 'Standard accessible',
    roomDescription:
      'Includes adjustable beds (standard wheelchair 480mm height), more space and wider entry bathrooms with lowered baths or wet rooms.',
    images: [mockImage],
    facilities: [mockFacility],
  },
  {
    roomType: 'family',
    roomName: 'Standard family',
    roomDescription: 'Family room description',
    images: [mockImage],
    facilities: [mockFacility],
  },
  {
    roomType: 'single',
    roomName: 'Standard single',
    roomDescription: 'single room description',
    images: [mockImage],
    facilities: [mockFacility],
  },
];

const mockTabsData = {
  tabGroups: [
    {
      groupId: 'double',
      groupName: 'Double',
    },
    {
      groupId: 'accessible',
      groupName: 'Accessible',
    },
    {
      groupId: 'family',
      groupName: 'Family',
    },
    {
      groupId: 'single',
      groupName: 'Single',
    },
  ],
  tabItems: mockTabItems,
};

const mockHotelInventoryResponse = {
  errorHotelInventory: null,
  isLoadingHotelInventory: false,
  isErrorHotelInventory: false,
  dataHotelInventory: {
    hotelInventory: {
      roomTypeInventories: [
        {
          availableCount: 42,
          code: ROOM_TYPE.DOUBLE,
        },
        {
          availableCount: 14,
          code: ROOM_TYPE.FAMILY_QUAD,
        },
        {
          availableCount: 41,
          code: ROOM_TYPE.FAMILY_TRIPLE,
        },
        {
          availableCount: 4,
          code: ROOM_TYPE.LOWERED_DOUBLE,
        },
        {
          availableCount: 1,
          code: ROOM_TYPE.WET_DOUBLE,
        },
        {
          availableCount: 5,
          code: ROOM_TYPE.PREMIER_PLUS,
        },
        {
          availableCount: 5,
          code: ROOM_TYPE.STANDARD,
        },
        {
          availableCount: 0,
          code: ROOM_TYPE.SINGLE,
        },
      ],
    },
  },
};

export const mockRoomTypeInformationResponse = {
  isLoadingRoomTypeInformation: false,
  isErrorRoomTypeInformation: false,
  errorRoomTypeInformation: null,
  dataRoomTypeInformation: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: [ROOM_TYPE.DOUBLE],
          roomCategory: 'Double',
          roomLabel: 'Double Room',
          roomDescription:
            "A super-comfy Hypnos bed, a power shower and free Wi-Fi - our double rooms have everything you'll need for a great night's sleep.",
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
          groupId: 'double',
        },
        {
          roomTypeCode: [ROOM_TYPE.LOWERED_DOUBLE],
          roomCategory: 'Accessible',
          roomLabel: 'Accessible double bedroom with a lowered bath',
          roomDescription:
            'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Bathroom.jpg',
          groupId: 'accessible',
        },
        {
          roomTypeCode: [ROOM_TYPE.WET_DOUBLE],
          roomCategory: 'Accessible',
          roomLabel: 'Accessible double bedroom with level access shower room',
          roomDescription:
            'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Wetroom.jpg',
          groupId: 'accessible',
        },
        {
          roomTypeCode: [ROOM_TYPE.FAMILY_QUAD],
          roomCategory: 'Family',
          roomLabel: 'Quad',
          roomDescription:
            'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
          groupId: 'family',
        },
        {
          roomTypeCode: [ROOM_TYPE.FAMILY_TRIPLE],
          roomCategory: 'Family',
          roomLabel: 'Triple',
          roomDescription:
            'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
          groupId: 'family',
        },
        {
          roomTypeCode: [ROOM_TYPE.PREMIER_PLUS],
          roomCategory: 'Premier Plus',
          roomLabel: 'Premier Plus Room',
          roomDescription:
            'Our enhanced room design. Includes Ultimate Wi-Fi, Nespresso machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
          groupId: 'incorrectlyEntered', // to test for double room 'classes' - roomTypeGroupId = 'double'
        },
        {
          roomTypeCode: [ROOM_TYPE.SINGLE],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription:
            "A super-comfy Hypnos bed, a power shower and free Wi-Fi - our Standard rooms have everything you'll need for a great night's sleep.",
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
          groupId: 'single',
        },
        {
          roomTypeCode: [ROOM_TYPE.STANDARD],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription: 'Compact rooms, designed around you.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
          groupId: 'double',
        },
      ],
    },
  },
};

const roomConfigurationsProps = {
  isLoading: false,
  isError: false,
  error: null,
  data: mockTabsData,
  isPremierInn: false,
  hotelInventoryResponse: mockHotelInventoryResponse,
  roomTypeInformationResponse: mockRoomTypeInformationResponse,
  isLessThanSm: false,
  isLessThanMd: false,
  isLessThanLg: false,
  brand: '',
};

describe('HotelRooms', () => {
  beforeEach(() => {
    mockFlags.release_pi_display_soft_bundles = false;
    mockFlags.release_semantic_typography_tokenization = false;
    mockCookies.bundles = 'false';
  });

  it('should render HotelRooms with soft bundles', () => {
    const { getByTestId } = render(
      <HotelRoomsContainer
        {...roomConfigurationsProps}
        isPremierInn={true}
        isSoftBundlesVisible={true}
      />
    );
    expect(getByTestId('hdp_ourRooms-Section')).toBeInTheDocument();
  });

  it('should render HotelRooms for premierinn', () => {
    const { getByTestId } = render(
      <HotelRoomsContainer {...roomConfigurationsProps} isPremierInn={true} />
    );
    expect(getByTestId('hdp_ourRooms-Section')).toBeInTheDocument();
  });

  it('should render HotelRooms for ccui', () => {
    const { getByTestId } = render(
      <HotelRoomsContainer {...roomConfigurationsProps} isPremierInn={false} />
    );
    expect(getByTestId('hdp_ourRooms-Section')).toBeInTheDocument();
  });

  it('should NOT render HotelRooms tabs if data empty', () => {
    const { queryByTestId } = render(
      <HotelRoomsComponent
        {...roomConfigurationsProps}
        data={{ tabGroups: [], tabItems: [] } as RoomConfiguration}
        isPremierInn
      />
    );
    expect(queryByTestId(/-TabButton/i)).not.toBeInTheDocument();
  });

  it('should render a loading message when isLoading prop is true', () => {
    const { getByText } = render(<HotelRoomsComponent {...roomConfigurationsProps} isLoading />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message when isError prop is true', () => {
    const { getByText } = render(
      <HotelRoomsComponent {...roomConfigurationsProps} error={{ message: 'Error' }} isError />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render hotel rooms', () => {
    const { getByTestId } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    expect(getByTestId('hdp_ourRooms-title')).toBeInTheDocument();
  });

  it('should render room tabs', () => {
    const { getAllByRole } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const tabs = getAllByRole('tab');
    expect(tabs[0]).toHaveTextContent('Double');
    expect(tabs[1]).toHaveTextContent('Accessible');
    expect(tabs[2]).toHaveTextContent('Family');
    expect(tabs[3]).toHaveTextContent('Single');
    expect(tabs.length).toBe(4);
  });

  it('should display tab name without count for Premier Inn', () => {
    const { getAllByRole } = render(
      <HotelRoomsComponent {...roomConfigurationsProps} isPremierInn={true} />
    );
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Double');
    expect(tab[0]).not.toHaveTextContent('Double (52)');
  });

  it('should give total count for Double room', () => {
    const { getAllByRole } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Double (52)'); //52 = DOUBLE 42, PPLDBL 5, DBLWIN: 5
  });

  it('should give total count for Accessible room', () => {
    const { getAllByRole } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const tab = getAllByRole('tab');
    expect(tab[1]).toHaveTextContent('Accessible (5)'); // 5 = LOWDBL 4 + WETDBL 1
  });

  it('should give total count for Family room', () => {
    const { getAllByRole } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const tab = getAllByRole('tab');
    expect(tab[2]).toHaveTextContent('Family (55)'); //55 = FMQUAD 14 + FMTRPL 41
    expect(tab[2]).not.toHaveTextContent('Family (14)');
    expect(tab[2]).not.toHaveTextContent('Family (41)');
  });

  it('should display a tooltip for Family tab, with breakdown of room sub-types', async () => {
    render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const toolTipDescription = screen.queryByText(/Triple/i);
    expect(toolTipDescription).not.toBeInTheDocument();

    userEvent.hover(screen.getByTestId('Family-TabButtonWithToolTipLabel'));

    expect(await screen.findByText(/Triple/i)).toHaveTextContent(
      'hoteldetails.roomtypes.quad (14) hoteldetails.roomtypes.triple (41)'
    );
  });

  it('should display zero in tab for room with no inventory count', () => {
    const { getAllByRole } = render(<HotelRoomsComponent {...roomConfigurationsProps} />);
    const tab = getAllByRole('tab');
    expect(tab[3]).toHaveTextContent('Single (0)'); // 0 = no inventory entry
  });
});

describe('HotelRooms - roomTypeInformation query', () => {
  it('should render loading message for roomTypeInformation query', () => {
    const { getByText, getByTestId } = render(
      <HotelRoomsComponent
        {...roomConfigurationsProps}
        roomTypeInformationResponse={{
          ...roomConfigurationsProps.roomTypeInformationResponse,
          isLoadingRoomTypeInformation: true,
        }}
      />
    );
    expect(getByTestId('room-types-loading-message')).toBeInTheDocument();
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message for roomTypeInformation query', () => {
    const { getByText } = render(
      <HotelRoomsComponent
        {...roomConfigurationsProps}
        roomTypeInformationResponse={{
          ...roomConfigurationsProps.roomTypeInformationResponse,
          isErrorRoomTypeInformation: true,
          errorRoomTypeInformation: { message: 'roomTypeInformation query Error' },
        }}
      />
    );
    expect(getByText('roomTypeInformation query Error')).toBeInTheDocument();
  });
});

describe('HotelRooms - hotelInventory query', () => {
  it('should render loading message for hotelInventory query', () => {
    const { getByText, getByTestId } = render(
      <HotelRoomsComponent
        {...roomConfigurationsProps}
        hotelInventoryResponse={{
          ...roomConfigurationsProps.hotelInventoryResponse,
          isLoadingHotelInventory: true,
        }}
      />
    );
    expect(getByTestId('hotel-inventory-loading-message')).toBeInTheDocument();
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message for hotelInventory query', () => {
    const { getByText } = render(
      <HotelRoomsComponent
        {...roomConfigurationsProps}
        hotelInventoryResponse={{
          ...roomConfigurationsProps.hotelInventoryResponse,
          errorHotelInventory: { message: 'hotelInventory query Error' },
          isErrorHotelInventory: true,
        }}
      />
    );
    expect(getByText('hotelInventory query Error')).toBeInTheDocument();
  });
});

describe('HotelRoomsComponent - renderRoomsDisclaimer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const defaultProps = {
    isLoading: false,
    isError: false,
    data: undefined,
    error: null,
    isPremierInn: false,
    isLessThanSm: false,
    isLessThanMd: false,
    isLessThanLg: false,
    hotelInventoryResponse: undefined,
    roomTypeInformationResponse: undefined,
    isDisplayRates: false,
    brand: '',
  };

  it('should render the rooms disclaimer when the feature toggle is enabled', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: true,
    });
    render(<HotelRoomsComponent {...defaultProps} />);
    expect(screen.getByTestId('hdp_rooms_disclaimer-Alert')).toBeInTheDocument();
  });

  it('should apply semantic typography to the rooms disclaimer description when enabled', async () => {
    mockFlags.release_semantic_typography_tokenization = true;
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: true,
      [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: true,
    });

    render(<HotelRoomsComponent {...defaultProps} />);

    expect(screen.getByTestId('hdp_rooms_disclaimer-AlertDescription')).toHaveStyle({
      fontSize: '14px',
      fontWeight: '400',
      lineHeight: '1.4',
    });
  });

  it('should render the rooms disclaimer when the feature toggle is enabled and soft bundles is active', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: true,
    });
    render(<HotelRoomsComponent {...defaultProps} isSoftBundlesVisible={true} />);
    expect(screen.getByTestId('hdp_rooms_disclaimer-Alert')).toBeInTheDocument();
  });

  it('should not render the rooms disclaimer when the feature toggle is disabled', () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: false,
    });
    render(<HotelRoomsComponent {...defaultProps} />);
    expect(screen.queryByTestId('notification')).not.toBeInTheDocument();
  });
});
