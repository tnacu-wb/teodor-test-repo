import '@testing-library/jest-dom';
import { type HIRoomType, ROOM_TYPE, type HIHotelInventoryResponse } from '@whitbread-eos/api';

import { userEvent, render } from '../../utils/test-utils';
import AccessibleRoomTypeOptionsComponent, {
  isSelectionRequired,
  getAccessibleRoomCounts,
  countBarrierFreeAndStdAccessible,
  getAvailableAccessibleroomsPerRoom,
  reSelectionRoomTypesRequired,
  initialRenderWithPreSelections,
  accessiblePrefixes,
  handleRenderWithReSelections,
  updateRoomsSelections,
  type Room,
} from './AccessibleRoomTypeOptions.component';
import AccessibleRoomTypeOptionsContainer from './AccessibleRoomTypeOptions.container';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const accessibleRoomTypeContainerProps = {
  data: [
    {
      adults: 1,
      children: 0,
      cotRequested: false,
      roomType: 'DIS',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.BRFDBL,
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
          specialRequests: ['SING', 'BFRE'],
        },
      ],
    },
    {
      adults: 2,
      children: 1,
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
          specialRequests: ['SING', 'WETR'],
        },
      ],
    },
  ] as HIRoomType[],
  onRoomTypeSelection: jest.fn(),
  onAccessibleRoomSelection: jest.fn(),
  accessibleRoomSelections: [ROOM_TYPE.STANDARD_ACCESSIBLE],
  roomTypeSelections: [],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 3, code: ROOM_TYPE.BRFDBL },
          { availableCount: 3, code: ROOM_TYPE.WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.WET_DOUBLE],
};

const accessibleRoomTypeComponentProps = {
  rooms: [
    {
      adults: 1,
      children: 0,
      roomTypes: ['Accessible Double'],
      accessibleRoomTypes: [ROOM_TYPE.STANDARD_ACCESSIBLE],
      pmsRoomTypes: { str: [ROOM_TYPE.WET_DOUBLE], bfr: [ROOM_TYPE.BRFDBL] },
      mappedRoomTypes: [ROOM_TYPE.WET_DOUBLE, ROOM_TYPE.BRFDBL],
    },
    {
      adults: 2,
      accessibleRoomTypes: undefined,
      children: 1,
      roomTypes: ['Family'],
      pmsRoomTypes: { str: [], bfr: [] },
      mappedRoomTypes: [],
    },
  ],
  onRoomTypeSelection: jest.fn(),
  onAccessibleRoomSelection: jest.fn(),
  accessibleRoomSelections: [] as string[],
  roomTypeSelections: [] as string[],
  hotelInventoryResponse: {
    isLoadingHotelInventory: false,
    isErrorHotelInventory: false,
    dataHotelInventory: {
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 42, code: ROOM_TYPE.DOUBLE },
          { availableCount: 3, code: ROOM_TYPE.BRFDBL },
          { availableCount: 3, code: ROOM_TYPE.WET_DOUBLE },
        ],
      },
    },
    errorHotelInventory: null,
  },
  selectedPMSRoomTypes: [ROOM_TYPE.WET_DOUBLE, ROOM_TYPE.BRFDBL],
};

describe('AccessibleRoomTypeOptions', () => {
  it('should render AccessibleRoomTypeOptions', () => {
    const { getByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getByTestId('choose-roomtype-title')).toBeInTheDocument();
  });

  it('should render nothing if no data is passed', () => {
    const { queryByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} data={undefined} />
    );
    expect(queryByTestId('choose-roomtype-title')).toBeNull();
  });

  it('should call onRoomTypeSelection with "Standard accessible" on component mount if accessibleRoomTypes is accessible', () => {
    render(<AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />);
    expect(accessibleRoomTypeComponentProps.onAccessibleRoomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.STANDARD_ACCESSIBLE
    );
  });
  it('should call onRoomTypeSelection with "Barrier free" on component mount if accessibleRoomTypes is barrierfree', () => {
    accessibleRoomTypeComponentProps.rooms[0].accessibleRoomTypes = [ROOM_TYPE.BARRIER_FREE];
    render(<AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />);
    expect(accessibleRoomTypeComponentProps.onAccessibleRoomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.BARRIER_FREE
    );
  });

  it('should call onRoomTypeSelection with "Accessible Double" on component mount if selection is required', () => {
    render(<AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />);
    expect(accessibleRoomTypeComponentProps.onRoomTypeSelection).toHaveBeenCalledWith(
      0,
      'Accessible Double'
    );
  });

  it('should render choose room type title', () => {
    const { getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getByText('accessible.chooseRoomTypeTitle')).toBeInTheDocument();
  });

  it('should render 2 room titles', () => {
    const { getAllByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getAllByText('booking.hotel.summary.room').length).toEqual(2);
  });

  it('should render 1 Adult for the first room', () => {
    const { getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getByText('1 dashboard.bookings.adult')).toBeInTheDocument();
  });

  it('should render 2 Adults, 1 Child for the second room', () => {
    const { getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(
      getByText('2 dashboard.bookings.adults, 1 dashboard.bookings.child')
    ).toBeInTheDocument();
  });

  it('should render 2 Adults, 2 Children if room has 2 adults and 2 children', () => {
    accessibleRoomTypeComponentProps.rooms[1].children = 2;
    const { getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(
      getByText('2 dashboard.bookings.adults, 2 dashboard.bookings.children')
    ).toBeInTheDocument();
  });
  it('should render no accessible selection required message for second room', () => {
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(1);
  });

  it('should render no accessible selection required message for DOUBLE room', () => {
    accessibleRoomTypeContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.DOUBLE;
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for SINGLE room', () => {
    accessibleRoomTypeContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.SINGLE;
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for PPLDBL room', () => {
    accessibleRoomTypeContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.PREMIER_PLUS;
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for EXTDBL room', () => {
    accessibleRoomTypeContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_EXTRA;
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(2);
  });

  it('should render no accessible selection required message for BIGWIN room', () => {
    accessibleRoomTypeContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_BIGGER;
    const { getAllByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getAllByTestId('accessible-no-room-type-selection-required').length).toEqual(2);
  });

  it('should render 2 radio buttons for the two room type options', () => {
    const { getAllByRole } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getAllByRole('radio').length).toEqual(2);
  });

  it('should render an unchecked accessible room and a barrier free radio buttons', () => {
    const props = { ...accessibleRoomTypeComponentProps };
    props.accessibleRoomSelections = [ROOM_TYPE.BARRIER_FREE];
    props.roomTypeSelections = ['Accessible Double'];
    const { getAllByRole } = render(<AccessibleRoomTypeOptionsComponent {...props} />);
    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
  });

  it('should render a disabled accessible standard room radio button', () => {
    const props = { ...accessibleRoomTypeComponentProps };
    props.selectedPMSRoomTypes = [ROOM_TYPE.BRFDBL];
    props.rooms[0].accessibleRoomTypes = [ROOM_TYPE.BARRIER_FREE];
    const { getAllByRole } = render(<AccessibleRoomTypeOptionsComponent {...props} />);
    expect(getAllByRole('radio')[0]).toBeDisabled();
  });

  it('should render standard accessible radio buttons texts', () => {
    const { getByTestId, getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getByTestId('accessible-standard-room')).toBeInTheDocument();
    expect(getByText('accessible.standardRoom.text')).toBeInTheDocument();
  });

  it('should render standard accessible radio buttons texts', () => {
    const { getByTestId, getByText } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(getByTestId('accessible-barrier-free-room')).toBeInTheDocument();
    expect(getByText('accessible.barrierFree.text')).toBeInTheDocument();
  });

  it('should render call onRoomTypeSelection on barrier free radio button click', () => {
    const { getAllByRole } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );

    const accessibleRoomRadioButton = getAllByRole('radio')[0];
    userEvent.click(accessibleRoomRadioButton);
    expect(accessibleRoomTypeComponentProps.onAccessibleRoomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.BARRIER_FREE
    );
  });

  it('should render call onRoomTypeSelection on standard accessible radio button click', () => {
    const { getAllByRole } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    const accessibleRoomRadioButton = getAllByRole('radio')[1];
    userEvent.click(accessibleRoomRadioButton);
    expect(accessibleRoomTypeComponentProps.onAccessibleRoomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.STANDARD_ACCESSIBLE
    );
  });

  it('should not render accessible room type alert notification', () => {
    const { queryByTestId } = render(
      <AccessibleRoomTypeOptionsComponent {...accessibleRoomTypeComponentProps} />
    );
    expect(queryByTestId('accessible-standardRoom')).toBeNull();
  });

  it('should render accessible room alert notification', () => {
    const props = { ...accessibleRoomTypeComponentProps };
    props.rooms[0].pmsRoomTypes = {
      str: [],
      bfr: [ROOM_TYPE.BRFDBL],
    };
    const { getByTestId } = render(<AccessibleRoomTypeOptionsComponent {...props} />);

    expect(getByTestId('accessible-standardRoom-AlertTitle')).toBeInTheDocument();
  });

  it('should render barrier free room alert notification', () => {
    const props = { ...accessibleRoomTypeComponentProps };
    props.rooms[0].pmsRoomTypes = {
      str: [ROOM_TYPE.STANDARD_ACCESSIBLE],
      bfr: [],
    };
    const { getByTestId } = render(<AccessibleRoomTypeOptionsComponent {...props} />);

    expect(getByTestId('accessible-no-room-type-selection-required')).toBeInTheDocument();
  });

  it('should render AccessibleRoomTypeOptions for premier plus accessible room', () => {
    const { getByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} />
    );
    expect(getByTestId('choose-roomtype-title')).toBeInTheDocument();
  });

  it('should render nothing if no data is passed for premier plus accessible room', () => {
    const { queryByTestId } = render(
      <AccessibleRoomTypeOptionsContainer {...accessibleRoomTypeContainerProps} data={undefined} />
    );
    expect(queryByTestId('choose-roomtype-title')).toBeNull();
  });
});

describe('accessibleRoomUtils', () => {
  describe('isSelectionRequired', () => {
    it('should return true if roomType starts with accessible prefix', () => {
      const room = {
        roomTypes: ['Accessible Double'],
        adults: 2,
        children: 0,
      };
      expect(isSelectionRequired(room as Room, accessiblePrefixes)).toBe(true);
    });

    it('should return false if roomType does not start with accessible prefix', () => {
      const room = {
        roomTypes: ['Family Room'],
        adults: 2,
        children: 0,
      };
      expect(isSelectionRequired(room as Room, accessiblePrefixes)).toBe(false);
    });

    it('should handle empty roomTypes gracefully', () => {
      const room = {
        roomTypes: [],
        adults: 1,
        children: 0,
      };
      expect(isSelectionRequired(room as any, accessiblePrefixes)).toBe(false);
    });
  });

  describe('getAccessibleRoomCounts', () => {
    it('should correctly calculate BFR and STR room totals', () => {
      const hotelInventoryResponse = {
        dataHotelInventory: {
          hotelInventory: {
            roomTypeInventories: [
              { code: ROOM_TYPE.BRFDBL, availableCount: 2 },
              { code: ROOM_TYPE.WET_DOUBLE, availableCount: 3 },
              { code: ROOM_TYPE.DOUBLE, availableCount: 10 },
            ],
          },
        },
      };

      const result = getAccessibleRoomCounts(hotelInventoryResponse as HIHotelInventoryResponse);
      expect(result).toEqual({
        bfrCountInventoryTotal: 2,
        strCountInventoryTotal: 3,
      });
    });

    it('should return zero counts for empty inventory', () => {
      const hotelInventoryResponse = {
        dataHotelInventory: {
          hotelInventory: {
            roomTypeInventories: [],
          },
        },
      };

      expect(getAccessibleRoomCounts(hotelInventoryResponse as any)).toEqual({
        bfrCountInventoryTotal: 0,
        strCountInventoryTotal: 0,
      });
    });

    it('should handle missing hotelInventory gracefully', () => {
      const hotelInventoryResponse = {};
      expect(getAccessibleRoomCounts(hotelInventoryResponse as HIHotelInventoryResponse)).toEqual({
        bfrCountInventoryTotal: 0,
        strCountInventoryTotal: 0,
      });
    });
  });

  describe('countBarrierFreeAndStdAccessible', () => {
    it('should correctly count barrier free and standard accessible', () => {
      const selections = [
        ROOM_TYPE.BARRIER_FREE,
        ROOM_TYPE.STANDARD_ACCESSIBLE,
        ROOM_TYPE.BARRIER_FREE,
      ];
      const initialCounts = { barrierFreeCount: 0, standardAccesssibleCount: 0 };

      const result = countBarrierFreeAndStdAccessible(selections, initialCounts);

      expect(result).toEqual({
        barrierFreeCount: 2,
        standardAccesssibleCount: 1,
      });
    });

    it('should ignore unrecognized room types', () => {
      const selections = ['UNKNOWN_ROOM', ROOM_TYPE.BARRIER_FREE];
      const initialCounts = { barrierFreeCount: 0, standardAccesssibleCount: 0 };

      const result = countBarrierFreeAndStdAccessible(selections, initialCounts);

      expect(result).toEqual({
        barrierFreeCount: 1,
        standardAccesssibleCount: 0,
      });
    });
  });

  describe('getAvailableAccessibleroomsPerRoom', () => {
    it('should calculate available accessible rooms per room', () => {
      const rooms = [
        {
          roomTypes: ['Accessible Double'],
          accessibleRoomTypes: [ROOM_TYPE.STANDARD_ACCESSIBLE],
        },
        {
          roomTypes: ['Accessible Family'],
          accessibleRoomTypes: [ROOM_TYPE.BARRIER_FREE],
        },
        {
          roomTypes: ['Standard Room'],
          accessibleRoomTypes: [],
        },
      ];

      const result = getAvailableAccessibleroomsPerRoom(rooms as Room[]);

      expect(result).toEqual([
        {
          hasAccessibleRoom: true,
          hasBarrierFreeRoom: false,
        },
        {
          hasAccessibleRoom: false,
          hasBarrierFreeRoom: true,
        },
        {
          hasAccessibleRoom: false,
          hasBarrierFreeRoom: false,
        },
      ]);
    });
  });

  describe('reSelectionRoomTypesRequired', () => {
    it('should return false if currentRoomIndex is -1', () => {
      expect(reSelectionRoomTypesRequired(-1)).toBe(false);
    });

    it('should return true if currentRoomIndex is not -1', () => {
      expect(reSelectionRoomTypesRequired(2)).toBe(true);
    });
  });

  describe('initialRenderWithPreSelections', () => {
    it('should call onAccessibleRoomSelection for rooms requiring selection', () => {
      const rooms = [
        {
          roomTypes: ['Accessible Double'],
          accessibleRoomTypes: [],
        },
      ];
      const accessibleRoomSelections = [];
      const availableAccessibleRoomsPerRoom = [
        { hasAccessibleRoom: true, hasBarrierFreeRoom: false },
      ];
      const onAccessibleRoomSelection = jest.fn();
      const onRoomTypeSelection = jest.fn();

      initialRenderWithPreSelections(
        rooms as any,
        accessibleRoomSelections,
        availableAccessibleRoomsPerRoom,
        onAccessibleRoomSelection,
        onRoomTypeSelection
      );

      expect(onAccessibleRoomSelection).toHaveBeenCalledWith(0, ROOM_TYPE.STANDARD_ACCESSIBLE);
      expect(onRoomTypeSelection).toHaveBeenCalledWith(0, 'Accessible Double');
    });

    it('should skip rooms not requiring selection', () => {
      const rooms = [
        {
          roomTypes: ['Standard Room'],
          accessibleRoomTypes: [],
        },
      ];
      const onAccessibleRoomSelection = jest.fn();
      const onRoomTypeSelection = jest.fn();

      initialRenderWithPreSelections(
        rooms as any,
        [],
        [],
        onAccessibleRoomSelection,
        onRoomTypeSelection
      );

      expect(onAccessibleRoomSelection).not.toHaveBeenCalled();
    });
  });

  it('should call setForceRender, setCurrentIndex and onAccessibleRoomSelection on change', () => {
    const setForceRender = jest.fn();
    const setCurrentIndex = jest.fn();
    const onAccessibleRoomSelection = jest.fn();
    const roomIndex = 1;

    // Render a minimal component to simulate the change
    const { getByTestId } = render(
      <input
        data-testid="accessible-radio"
        type="radio"
        onChange={() => {
          setForceRender((prev: number) => prev + 1);
          setCurrentIndex(roomIndex);
          onAccessibleRoomSelection(roomIndex, ROOM_TYPE.STANDARD_ACCESSIBLE);
        }}
      />
    );

    const radioButton = getByTestId('accessible-radio');
    userEvent.click(radioButton);

    expect(setForceRender).toHaveBeenCalled();
    expect(setCurrentIndex).toHaveBeenCalledWith(roomIndex);
    expect(onAccessibleRoomSelection).toHaveBeenCalledWith(
      roomIndex,
      ROOM_TYPE.STANDARD_ACCESSIBLE
    );
  });
});

describe('updateRoomsSelections', () => {
  it('should add STANDARD_ACCESSIBLE when not already present', () => {
    const updatedSelections = [ROOM_TYPE.STANDARD_ACCESSIBLE];
    const updatedRooms = [{ accessibleRoomTypes: [], roomTypes: ['Double'] }];

    updateRoomsSelections(updatedSelections, updatedRooms);

    expect(updatedRooms[0].accessibleRoomTypes).toContain(ROOM_TYPE.STANDARD_ACCESSIBLE);
  });

  it('should add BARRIER_FREE when not already present', () => {
    const updatedSelections = [ROOM_TYPE.BARRIER_FREE];
    const updatedRooms = [{ accessibleRoomTypes: [], roomTypes: ['Twin'] }];

    updateRoomsSelections(updatedSelections, updatedRooms);

    expect(updatedRooms[0].accessibleRoomTypes).toContain(ROOM_TYPE.BARRIER_FREE);
  });

  it('should not duplicate STANDARD_ACCESSIBLE if already present', () => {
    const updatedSelections = [ROOM_TYPE.STANDARD_ACCESSIBLE];
    const updatedRooms = [
      { accessibleRoomTypes: [ROOM_TYPE.STANDARD_ACCESSIBLE], roomTypes: ['Double'] },
    ];

    updateRoomsSelections(updatedSelections, updatedRooms);

    expect(updatedRooms[0].accessibleRoomTypes).toEqual([ROOM_TYPE.STANDARD_ACCESSIBLE]);
  });

  it('should handle multiple rooms with mixed selections', () => {
    const updatedSelections = [ROOM_TYPE.STANDARD_ACCESSIBLE, ROOM_TYPE.BARRIER_FREE];
    const updatedRooms = [
      { accessibleRoomTypes: [], roomTypes: ['Double'] },
      { accessibleRoomTypes: [], roomTypes: ['Twin'] },
    ];

    updateRoomsSelections(updatedSelections, updatedRooms);

    expect(updatedRooms[0].accessibleRoomTypes).toContain(ROOM_TYPE.STANDARD_ACCESSIBLE);
    expect(updatedRooms[1].accessibleRoomTypes).toContain(ROOM_TYPE.BARRIER_FREE);
  });
});

describe('handleRenderWithReSelections', () => {
  const mockOnAccessibleRoomSelection = jest.fn();
  const mockOnRoomTypeSelection = jest.fn();
  const mockSetAvailableAccessibleRoomsPerRoom = jest.fn();

  const baseProps = {
    rooms: [
      { roomTypes: ['Double'], accessibleRoomTypes: [], adults: 2, children: 0 },
      { roomTypes: ['Twin'], accessibleRoomTypes: [], adults: 1, children: 1 },
    ],
    availableAccessibleRoomsPerRoom: [
      { hasAccessibleRoom: true, hasBarrierFreeRoom: true },
      { hasAccessibleRoom: true, hasBarrierFreeRoom: true },
    ],
    hotelInventoryResponse: {
      dataHotelInventory: {
        hotelInventory: {
          roomTypeInventories: [
            { code: ROOM_TYPE.BRFDBL, availableCount: 1 },
            { code: ROOM_TYPE.WET_DOUBLE, availableCount: 1 },
          ],
        },
      },
    },
    initialRoomTypeCounts: { barrierFreeCount: 1, standardAccesssibleCount: 1 },
    setAvailableAccessibleRoomsPerRoom: mockSetAvailableAccessibleRoomsPerRoom,
    onAccessibleRoomSelection: mockOnAccessibleRoomSelection,
    onRoomTypeSelection: mockOnRoomTypeSelection,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should switch STANDARD_ACCESSIBLE to BARRIER_FREE when STR inventory is full', () => {
    const props = {
      ...baseProps,
      accessibleRoomSelections: [ROOM_TYPE.STANDARD_ACCESSIBLE, ROOM_TYPE.STANDARD_ACCESSIBLE],
      currentRoomIndex: 0,
    };

    handleRenderWithReSelections(props as any);

    expect(mockOnAccessibleRoomSelection).toHaveBeenCalledWith(1, ROOM_TYPE.BARRIER_FREE);
    expect(mockOnRoomTypeSelection).toHaveBeenCalledWith(1, 'Twin');
    expect(mockSetAvailableAccessibleRoomsPerRoom).toHaveBeenCalled();
  });

  it('should switch BARRIER_FREE to STANDARD_ACCESSIBLE when BFR inventory is full', () => {
    const props = {
      ...baseProps,
      accessibleRoomSelections: [ROOM_TYPE.BARRIER_FREE, ROOM_TYPE.BARRIER_FREE],
      currentRoomIndex: 0,
    };

    handleRenderWithReSelections(props as any);

    expect(mockOnAccessibleRoomSelection).toHaveBeenCalledWith(1, ROOM_TYPE.STANDARD_ACCESSIBLE);
    expect(mockOnRoomTypeSelection).toHaveBeenCalledWith(1, 'Twin');
    expect(mockSetAvailableAccessibleRoomsPerRoom).toHaveBeenCalled();
  });

  it('should not call callbacks if current selection does not require reselection', () => {
    const props = {
      ...baseProps,
      accessibleRoomSelections: [ROOM_TYPE.STANDARD_ACCESSIBLE, ROOM_TYPE.BARRIER_FREE],
      currentRoomIndex: 0,
    };

    handleRenderWithReSelections(props as any);

    expect(mockOnAccessibleRoomSelection).not.toHaveBeenCalled();
    expect(mockOnRoomTypeSelection).not.toHaveBeenCalled();
    expect(mockSetAvailableAccessibleRoomsPerRoom).toHaveBeenCalled();
  });

  it('should initialize accessibleRoomTypes if undefined', () => {
    const props = {
      ...baseProps,
      rooms: [
        { roomTypes: ['Double'], adults: 2, children: 0 }, // no accessibleRoomTypes
        { roomTypes: ['Twin'], adults: 1, children: 1 }, // no accessibleRoomTypes
      ],
      accessibleRoomSelections: [ROOM_TYPE.BARRIER_FREE, ROOM_TYPE.BARRIER_FREE],
      currentRoomIndex: 0,
    };

    handleRenderWithReSelections(props as any);

    expect(mockOnAccessibleRoomSelection).toHaveBeenCalledWith(1, ROOM_TYPE.STANDARD_ACCESSIBLE);
    expect(mockSetAvailableAccessibleRoomsPerRoom).toHaveBeenCalled();
  });
});
