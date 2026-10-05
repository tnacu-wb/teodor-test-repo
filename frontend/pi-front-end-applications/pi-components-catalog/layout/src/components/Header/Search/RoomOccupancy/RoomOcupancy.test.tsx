import { LOCALES } from '@whitbread-eos/api';

import { render, act, fireEvent, waitFor } from '../../../../utils/test-utils';
import RoomOccupancy from './RoomOccupancy.component';

const mockProps = {
  formLabels: {},
  rooms: {
    roomOne: {
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  },
  updateRoom: () => {
    return;
  },
  addRoom: () => {
    return;
  },
  maxRooms: 8,
  removeRoom: jest.fn(),
  showError: false,
  addRoomIcon: '/',
  icons: {},
  userRole: 'SUPER',
  onOpenChange: () => {
    return;
  },
  isMobile: false,
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

describe('IB Room Occupancy component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.isMobile = false;
  });

  it('should render ROOM OCCUPANCY component', () => {
    const { getByTestId, getByText } = render(<RoomOccupancy {...mockProps} />);
    expect(getByTestId('Room-Occupancy-Button')).toBeInTheDocument();
    expect(getByText('1 content.global.adult, 1 content.global.room')).toBeInTheDocument();
  });

  it('should render ROOM OCCUPANCY component and click to open the dropdown', async () => {
    const { getByTestId } = render(<RoomOccupancy {...mockProps} />);
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });
  });

  it('should render Room Occupancy Dropdown component and switch the cot button', async () => {
    const { getByTestId } = render(<RoomOccupancy {...mockProps} />);
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });
    const includeCotButton = getByTestId('IB-shouldIncludeCot-Switch-1');
    expect(includeCotButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(includeCotButton);
    });
  });

  it('should render Room Occupancy Dropdown component with mobile', async () => {
    mockProps.isMobile = true;
    const { getByTestId } = render(<RoomOccupancy {...mockProps} />);
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Mobile-Content-Wrapper')).toBeInTheDocument();
    });
    const closeButton = getByTestId('Dialog-X-Close-Button');
    expect(closeButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(closeButton);
    });
  });
});
