import { LOCALES } from '@whitbread-eos/api';

import { render, act, fireEvent, waitFor, userEvent } from '../../../../../../utils/test-utils';
import Room from './Room.component';

const mockProps = {
  room: {
    adults: 2,
    children: 0,
    shouldIncludeCot: false,
    roomType: 'Double',
  },
  roomId: 'testId',
  roomCount: 1,
  hasSeparator: false,
  updateRoom: () => {
    return;
  },
  formLabels: {},
  icons: {},
  roomNumber: 1,
  userRole: 'SUPER',
  removeRoom: () => {
    return;
  },
  roomsCount: 1,
  isRoomNewlyAdded: false,
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

window.HTMLElement.prototype.scrollIntoView = jest.fn();
window.HTMLElement.prototype.releasePointerCapture = jest.fn();
window.HTMLElement.prototype.hasPointerCapture = jest.fn();

describe('IB Room Occupancy component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.room.children = 0;
    mockProps.room.adults = 2;
    mockProps.room.roomType = 'Double';
  });

  it('should render Room component and switch the cot button', async () => {
    const { getByTestId } = render(<Room {...mockProps} />);
    expect(getByTestId('IB-Room-1')).toBeInTheDocument();
  });

  it('should render Room component and switch the cot button', async () => {
    const { getByTestId } = render(<Room {...mockProps} />);
    const includeCotButton = getByTestId('IB-shouldIncludeCot-Switch-1');
    expect(includeCotButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(includeCotButton);
    });
  });

  it('should render Room component with children', async () => {
    mockProps.room.children = 2;
    const { getByTestId } = render(<Room {...mockProps} />);
    expect(getByTestId('IB-Room-1')).toBeInTheDocument();
    expect(getByTestId('IB-RoomOccupancy-Children-Dropdown-1-IB-Select-Trigger')).toHaveTextContent(
      '2'
    );
  });

  it('should render Room component and click on adults trigger', async () => {
    mockProps.room.adults = 1;
    const { getByTestId } = render(<Room {...mockProps} />);
    expect(getByTestId('IB-Room-1')).toBeInTheDocument();
    const adultsTrigger = getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-IB-Select-Trigger');

    await act(async () => {
      fireEvent.click(adultsTrigger);
    });

    await waitFor(() => {
      expect(adultsTrigger).toHaveTextContent('1');
    });
  });

  it('should render Room component and click 1 adult with TWIN room', async () => {
    mockProps.room.roomType = 'Twin';
    const { getByTestId } = render(<Room {...mockProps} />);
    expect(getByTestId('IB-Room-1')).toBeInTheDocument();
    const adultsTrigger = getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-IB-Select-Trigger');

    await act(async () => {
      userEvent.click(adultsTrigger);
    });

    const oneAdultSelection = getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-0-Option');

    await act(async () => {
      userEvent.click(oneAdultSelection);
    });

    expect(adultsTrigger).toBeInTheDocument();
  });

  it('should render Room component and click 2 adults with SINGLE room', async () => {
    mockProps.room.adults = 1;
    mockProps.room.roomType = 'Single';
    const { getByTestId } = render(<Room {...mockProps} />);
    expect(getByTestId('IB-Room-1')).toBeInTheDocument();
    const adultsTrigger = getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-IB-Select-Trigger');

    await act(async () => {
      userEvent.click(adultsTrigger);
    });

    const oneAdultSelection = getByTestId('IB-RoomOccupancy-Adults-Dropdown-1-1-Option');

    await act(async () => {
      userEvent.click(oneAdultSelection);
    });

    expect(adultsTrigger).toBeInTheDocument();
  });

  it('should render remove room button when roomsCount is greater than 1', async () => {
    const props = { ...mockProps, roomsCount: 2 };
    const { getByTestId } = render(<Room {...props} />);
    expect(getByTestId('IB-Remove-Room-1-Button')).toBeInTheDocument();
  });

  it('should not render remove room button when roomsCount is 1', async () => {
    const props = { ...mockProps, roomsCount: 1 };
    const { queryByTestId } = render(<Room {...props} />);
    expect(queryByTestId('IB-Remove-Room-1-Button')).not.toBeInTheDocument();
  });

  it('should focus remove room button when isRoomNewlyAdded is true', async () => {
    const props = { ...mockProps, roomsCount: 2, isRoomNewlyAdded: true };
    const { getByTestId } = render(<Room {...props} />);

    const removeButton = getByTestId('IB-Remove-Room-1-Button');
    await waitFor(() => {
      expect(removeButton).toHaveFocus();
    });
  });

  it('should not focus remove room button when isRoomNewlyAdded is false', async () => {
    const props = { ...mockProps, roomsCount: 2, isRoomNewlyAdded: false };
    const { getByTestId } = render(<Room {...props} />);

    const removeButton = getByTestId('IB-Remove-Room-1-Button');
    expect(removeButton).not.toHaveFocus();
  });

  it('should call removeRoom when remove button is clicked', async () => {
    const removeRoomMock = jest.fn();
    const props = { ...mockProps, roomsCount: 2, removeRoom: removeRoomMock };
    const { getByTestId } = render(<Room {...props} />);

    const removeButton = getByTestId('IB-Remove-Room-1-Button');

    await act(async () => {
      fireEvent.click(removeButton);
    });

    expect(removeRoomMock).toHaveBeenCalledWith('testId');
  });
});
