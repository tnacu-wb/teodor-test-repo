import '@testing-library/jest-dom';
import { type HIRoomType, ROOM_TYPE } from '@whitbread-eos/api';

import { fireEvent, render } from '../../utils/test-utils';
import AccessibleBathroomOptionsComponent from './AccessibleBathroomOptions.component';
import AccessibleBathroomOptionsContainer from './AccessibleBathroomOptions.container';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const accessibleBathroomContainerProps = {
  data: [
    {
      adults: 1,
      children: 0,
      cotRequested: false,
      roomType: 'DIS',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.WET_DOUBLE,
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
        },
      ],
    },
    {
      adults: 2,
      children: 1,
      cotRequested: false,
      roomType: 'FAM',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.FAMILY_TRIPLE,
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
        },
      ],
    },
  ] as HIRoomType[],
  onRoomTypeSelection: jest.fn(),
  onBathroomSelection: jest.fn(),
  bathroomSelections: [],
  roomTypeSelections: [],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 14, code: ROOM_TYPE.FAMILY_QUAD },
          { availableCount: 41, code: ROOM_TYPE.FAMILY_TRIPLE },
          { availableCount: 4, code: ROOM_TYPE.LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.WET_DOUBLE, ROOM_TYPE.FAMILY_TRIPLE],
};

const accessibleBathroomComponentProps = {
  rooms: [
    {
      adults: 1,
      bathroomTypes: [ROOM_TYPE.WET],
      children: 0,
      roomTypes: ['Accessible Double'],
      isRoomAccessible: true,
    },
    {
      adults: 2,
      bathroomTypes: undefined,
      children: 1,
      roomTypes: ['Family'],
      isRoomAccessible: false,
    },
  ],
  onRoomTypeSelection: jest.fn(),
  onBathroomSelection: jest.fn(),
  bathroomSelections: [] as string[],
  roomTypeSelections: [] as string[],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 14, code: ROOM_TYPE.FAMILY_QUAD },
          { availableCount: 41, code: ROOM_TYPE.FAMILY_TRIPLE },
          { availableCount: 4, code: ROOM_TYPE.LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.WET_DOUBLE, ROOM_TYPE.FAMILY_TRIPLE],
};

const premPlusAccessibleBathroomComponentProps = {
  rooms: [
    {
      adults: 1,
      bathroomTypes: [ROOM_TYPE.WET],
      children: 0,
      roomTypes: ['Accessible Double'],
      isRoomAccessible: true,
    },
    {
      adults: 2,
      bathroomTypes: undefined,
      children: 1,
      roomTypes: ['lowered'],
      isRoomAccessible: false,
    },
  ],
  onRoomTypeSelection: jest.fn(),
  onBathroomSelection: jest.fn(),
  bathroomSelections: [] as string[],
  roomTypeSelections: [] as string[],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 14, code: ROOM_TYPE.FAMILY_QUAD },
          { availableCount: 41, code: ROOM_TYPE.FAMILY_TRIPLE },
          { availableCount: 4, code: ROOM_TYPE.LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.WET_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE, ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE],
};

const premPlusAccessibleBathroomContainerProps = {
  data: [
    {
      adults: 1,
      children: 0,
      cotRequested: false,
      roomType: 'DIS',
      rooms: [
        {
          isRoomAccessible: true,
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
          roomClass: 'PP',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
        },
      ],
    },
    {
      isRoomAccessible: true,
      adults: 2,
      children: 1,
      cotRequested: false,
      roomType: 'DIS',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
          roomClass: 'PP',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
        },
      ],
    },
  ] as HIRoomType[],
  onRoomTypeSelection: jest.fn(),
  onBathroomSelection: jest.fn(),
  bathroomSelections: [],
  roomTypeSelections: [],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 14, code: ROOM_TYPE.FAMILY_QUAD },
          { availableCount: 41, code: ROOM_TYPE.FAMILY_TRIPLE },
          { availableCount: 4, code: ROOM_TYPE.LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.WET_DOUBLE },
          { availableCount: 4, code: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE },
          { availableCount: 1, code: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE, ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE],
};

describe('AccessibleBathroomOptions', () => {
  it('should render AccessibleBathroomOptions', () => {
    const { getByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getByTestId('accessible-title')).toBeInTheDocument();
  });

  it('should render nothing if no data is passed', () => {
    const { queryByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} data={undefined} />
    );
    expect(queryByTestId('accessible-title')).toBeNull();
  });

  it('should call onBathroomSelection with "wet" on component mount if bathroomType is wet', () => {
    render(<AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />);
    expect(accessibleBathroomComponentProps.onBathroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.WET
    );
  });

  it('should call onBathroomSelection with "lowered" on component mount if bathroomType is lowered', () => {
    accessibleBathroomComponentProps.rooms[0].bathroomTypes = [ROOM_TYPE.LOWERED];
    render(<AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />);
    expect(accessibleBathroomComponentProps.onBathroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.LOWERED
    );
  });

  it('should call onRoomTypeSelection with "Accessible Double" on component mount if selection is required', () => {
    render(<AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />);
    expect(accessibleBathroomComponentProps.onRoomTypeSelection).toHaveBeenCalledWith(
      0,
      'Accessible Double'
    );
  });

  it('should render choose bathroom title', () => {
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByText('accessible.chooseBathRoomTitle')).toBeInTheDocument();
  });

  it('should render 2 room titles', () => {
    const { getAllByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByText('booking.hotel.summary.room').length).toEqual(2);
  });

  it('should render 1 Adult for the first room', () => {
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByText('1 dashboard.bookings.adult')).toBeInTheDocument();
  });

  it('should render 2 Adults, 1 Child for the second room', () => {
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(
      getByText('2 dashboard.bookings.adults, 1 dashboard.bookings.child')
    ).toBeInTheDocument();
  });

  it('should render 2 Adults, 2 Children if room has 2 adults and 2 children', () => {
    accessibleBathroomComponentProps.rooms[1].children = 2;
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(
      getByText('2 dashboard.bookings.adults, 2 dashboard.bookings.children')
    ).toBeInTheDocument();
  });

  it('should render a room with accessible room selection dropdown', () => {
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByTestId('accessible-bathroom-dropdown').length).toEqual(1);
  });

  it('should render a tooltip for unavailable accessible twin', () => {
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByText('accessible.twin.unavailable')).toBeInTheDocument();
  });

  it('should render a tooltip for unavailable accessible double if roomTypes includes "Accessible Twin"', () => {
    const { getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByText('accessible.twin.unavailable')).toBeInTheDocument();
  });

  it('should render no accessible selection required message for second room', () => {
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(1);
  });

  it('should render no accessible selection required message for DOUBLE room', () => {
    accessibleBathroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.DOUBLE;
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for SINGLE room', () => {
    accessibleBathroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.SINGLE;
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for PPLDBL room', () => {
    accessibleBathroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.PREMIER_PLUS;
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for EXTDBL room', () => {
    accessibleBathroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_EXTRA;
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for BIGWIN room', () => {
    accessibleBathroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_BIGGER;
    const { getAllByTestId } = render(
      <AccessibleBathroomOptionsContainer {...accessibleBathroomContainerProps} />
    );
    expect(getAllByTestId('accessible-no-selection-required').length).toEqual(2);
  });

  it('should render 2 radio buttons for the two bathroom options', () => {
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByRole('radio').length).toEqual(2);
  });

  it('should render an unchecked lowered bathroom and a checked web bathroom radio buttons', () => {
    accessibleBathroomComponentProps.bathroomSelections = [ROOM_TYPE.WET];
    accessibleBathroomComponentProps.roomTypeSelections = ['Accessible Double'];
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
  });

  it('should render a disabled lowered bathroom and an available web bathroom radio buttons', () => {
    accessibleBathroomComponentProps.selectedPMSRoomTypes = [
      ROOM_TYPE.WET_DOUBLE,
      ROOM_TYPE.FAMILY_TRIPLE,
    ];
    accessibleBathroomComponentProps.rooms[0].bathroomTypes = [ROOM_TYPE.WET];
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getAllByRole('radio')[0]).toBeDisabled();
    expect(getAllByRole('radio')[1]).not.toBeDisabled();
  });

  it('should render lowered bathroom radio buttons texts', () => {
    const { getByTestId, getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByTestId('accessible-low-bathroom')).toBeInTheDocument();
    expect(getByText('accessible.loweredBath.text')).toBeInTheDocument();
  });

  it('should render wet bathroom radio buttons texts', () => {
    const { getByTestId, getByText } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByTestId('accessible-wet-room')).toBeInTheDocument();
    expect(getByText('accessible.wetRoom.text')).toBeInTheDocument();
  });

  it('should render call onBathroomSelection on wet radio button click', () => {
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    const wetRoomRadioButton = getAllByRole('radio')[1];
    fireEvent.click(wetRoomRadioButton);
    expect(accessibleBathroomComponentProps.onBathroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.WET
    );
  });

  it('should not render wet bathroom alert notification', () => {
    const { queryByTestId } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(queryByTestId('accessible-wetRoom-Alert')).toBeNull();
  });

  it('should render lowered bathroom alert notification', () => {
    const { getByTestId } = render(
      <AccessibleBathroomOptionsComponent {...accessibleBathroomComponentProps} />
    );
    expect(getByTestId('accessible-lowBath-Alert')).toBeInTheDocument();
  });

  it('should render AccessibleBathroomOptions for premier plus accessible room', () => {
    const { getByTestId } = render(
      <AccessibleBathroomOptionsContainer {...premPlusAccessibleBathroomContainerProps} />
    );
    expect(getByTestId('accessible-title')).toBeInTheDocument();
  });

  it('should render nothing if no data is passed for premier plus accessible room', () => {
    const { queryByTestId } = render(
      <AccessibleBathroomOptionsContainer
        {...premPlusAccessibleBathroomContainerProps}
        data={undefined}
      />
    );
    expect(queryByTestId('accessible-title')).toBeNull();
  });

  it('should render an unchecked lowered bathroom and a checked web bathroom radio buttons for premier plus accessible rooms', () => {
    premPlusAccessibleBathroomComponentProps.bathroomSelections = [ROOM_TYPE.WET];
    premPlusAccessibleBathroomComponentProps.roomTypeSelections = ['Accessible Double'];
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...premPlusAccessibleBathroomComponentProps} />
    );
    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
  });

  it('should render a disabled lowered bathroom and an available web bathroom radio buttons for premier plus accessible rooms', () => {
    premPlusAccessibleBathroomComponentProps.selectedPMSRoomTypes = [
      ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
      ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
    ];
    premPlusAccessibleBathroomComponentProps.rooms[0].bathroomTypes = [ROOM_TYPE.WET];
    const { getAllByRole } = render(
      <AccessibleBathroomOptionsComponent {...premPlusAccessibleBathroomComponentProps} />
    );
    expect(getAllByRole('radio')[0]).toBeDisabled();
    expect(getAllByRole('radio')[1]).not.toBeDisabled();
  });
});
