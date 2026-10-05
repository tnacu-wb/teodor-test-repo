import { LOCALES } from '@whitbread-eos/api';

import { render, waitFor, userEvent } from '../../../../../utils/test-utils';
import RoomOccupancyDropdown from './RoomOccupancyDropdown.component';

const mockDefaultRoom = {
  adults: 1,
  children: 0,
  shouldIncludeCot: false,
  roomType: 'Double',
};

const mockOneRoom = {
  roomOne: mockDefaultRoom,
};

const mockTwoRooms = {
  roomOne: mockDefaultRoom,
  roomTwo: mockDefaultRoom,
};

const mockFullRooms = {
  roomOne: mockDefaultRoom,
  roomTwo: mockDefaultRoom,
  roomThree: mockDefaultRoom,
  roomFour: mockDefaultRoom,
};
const mockProps = {
  updateRoom: () => {
    return;
  },
  addRoom: jest.fn(),
  removeRoom: jest.fn(),
  toggleDropdown: jest.fn(),
  formLabels: {},
  rooms: mockOneRoom,
  addRoomIcon: '/',
  icons: {},
  userRole: 'SUPER',
  maxRooms: 9,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: () => {
      return {
        t: (str: string) => str,
        i18n: {
          changeLanguage: () => new Promise(() => true),
        },
      };
    },
  };
});

describe('IB Room Occupancy Dropdown component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.rooms = mockOneRoom;
    mockProps.userRole = 'SUPER';
  });

  it('should render Room Occupancy Dropdown component and click addRoom', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    const addRoomButton = getByTestId('IB-Add-Room-Button');
    expect(addRoomButton).toBeInTheDocument();

    await userEvent.click(addRoomButton);
  });

  it('should render Room Occupancy Dropdown component', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
  });

  it('should render Room Occupancy Dropdown component with self booker', async () => {
    mockProps.userRole = 'SELF';
    const { getByTestId, queryByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();

    await waitFor(() => {
      expect(queryByTestId('IB-Add-Room-Button')).not.toBeInTheDocument();
    });
  });

  it('should render Room Occupancy Dropdown component with all 4 rooms', async () => {
    mockProps.rooms = mockFullRooms;
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
  });

  it('should render Room Occupancy Dropdown component with all 4 rooms and no notification label', async () => {
    mockProps.rooms = mockFullRooms;
    mockProps.notificationIcons = { alert: 'test' };
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
  });

  it('should render Room Occupancy Dropdown component and switch the cot button', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    const includeCotButton = getByTestId('IB-shouldIncludeCot-Switch-1');
    expect(includeCotButton).toBeInTheDocument();

    await userEvent.click(includeCotButton);
  });

  it('should call addRoom when add room button is clicked', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    const addRoomButton = getByTestId('IB-Add-Room-Button');

    await userEvent.click(addRoomButton);

    expect(mockProps.addRoom).toHaveBeenCalled();
  });

  it('should call removeRoom when remove room button is clicked', async () => {
    const props = { ...mockProps, rooms: mockTwoRooms };
    const { getByTestId } = render(<RoomOccupancyDropdown {...props} />);

    const removeRoomButton = getByTestId('IB-Remove-Room-1-Button');

    await userEvent.click(removeRoomButton);

    expect(mockProps.removeRoom).toHaveBeenCalledWith('roomOne');
  });

  it('should focus add room button after removing a room', async () => {
    const props = { ...mockProps, rooms: mockTwoRooms };
    const { getByTestId, rerender } = render(<RoomOccupancyDropdown {...props} />);

    const removeRoomButton = getByTestId('IB-Remove-Room-1-Button');

    await userEvent.click(removeRoomButton);

    // Simulate the rooms state update after removal
    rerender(<RoomOccupancyDropdown {...mockProps} rooms={mockOneRoom} />);

    await waitFor(() => {
      const addRoomButton = getByTestId('IB-Add-Room-Button');
      expect(addRoomButton).toHaveFocus();
    });
  });

  it('should pass isRoomNewlyAdded as true to the last room after clicking add room', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    const addRoomButton = getByTestId('IB-Add-Room-Button');

    await userEvent.click(addRoomButton);

    expect(mockProps.addRoom).toHaveBeenCalled();
  });

  it('should call toggleDropdown when done button is clicked', async () => {
    const { getByTestId } = render(<RoomOccupancyDropdown {...mockProps} />);
    const doneButton = getByTestId('IB-Room-Occupancy-Done-Button');

    await userEvent.click(doneButton);

    expect(mockProps.toggleDropdown).toHaveBeenCalled();
  });
});
