import { Menu } from '@chakra-ui/react';

import { render, fireEvent, screen } from '../../utils/test-utils';
import DropdownContent from './DropdownContent.component';

const labels = {
  roomsWarningTitle: 'Max rooms warning title',
  roomsWarningDescription: 'Default max rooms description',
  roomsWarningDescriptionCCUI: 'Only for CCUI notification max rooms description',
  addMoreRoomsLabel: 'Add another room',
  removeRoomButtonLabel: 'Remove room',
  doneButtonLabel: 'Done',
  adult: 'adult',
  adults: 'adults',
  adultsLabel: 'Adults',
  adultsMaxPerRoomLabel: 'Max 2 per room',
  child: 'child',
  children: 'children',
  childrenLabel: 'Children',
  childrenAgeLabel: '2 - 15 years',
  cotLimit: '0 - 2 years',
  cotLabel: 'Include a cot?',
  room: 'room',
  rooms: 'rooms',
  roomLabel: 'room',
  roomTypeLabel: 'Room Type',
  single: 'Single',
  double: 'Double',
  accessible: 'Accessible',
  twin: 'Twin',
  family: 'Family',
};

const defaultProps = {
  setIsOpen: jest.fn(),
  rooms: [
    {
      id: '1',
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      shouldBeAccessible: false,
      roomType: 'Double',
    },
  ],
  addRoom: jest.fn(),
  removeRoom: jest.fn(),
  updateRoom: jest.fn(),
  onSubmit: jest.fn(),
  adultsOptions: [
    { id: 1, label: '1 adult' },
    { id: 2, label: '2 adults' },
  ],
  childrenOptions: [
    { id: 0, label: '0 children' },
    { id: 1, label: '1 child' },
  ],
  labels,
  maxNumberOfRooms: 2,
  dataRoomOccupancyLimitations: {
    roomOccupancyLimitations: {
      roomOccupancies: [],
    },
  },
  roomCodes: { DB: 'double', FAM: 'family', DIS: 'accessible' },
  dataTestId: 'DropdownComp-roomPicker-dropdownContent',
  screenSize: { isLessThanXs: false, isLessThanSm: false, isLessThanMd: false },
  showMultipleRooms: false,
  channel: 'PI',
  className: '',
  noRoomTypeSearch: false,
};

describe('DropdownContent', () => {
  it('renders adults and children dropdowns', () => {
    render(
      <Menu>
        <DropdownContent {...defaultProps} />
      </Menu>
    );
    expect(screen.getByText('Adults')).toBeInTheDocument();
    expect(screen.getByText('Children')).toBeInTheDocument();
  });

  it('renders cot switch and room type dropdown when noRoomTypeSearch is false', () => {
    render(
      <Menu>
        <DropdownContent {...defaultProps} />
      </Menu>
    );
    expect(screen.getByText('Include a cot?')).toBeInTheDocument();
    expect(screen.getByText('Room Type')).toBeInTheDocument();
  });

  it('renders accessible and cot switchers when noRoomTypeSearch is true', () => {
    render(
      <Menu>
        <DropdownContent {...defaultProps} noRoomTypeSearch={true} />
      </Menu>
    );
    expect(
      screen.getByTestId('DropdownComp-roomPicker-dropdownContent-accessibleRoomSwitcher')
    ).toBeInTheDocument();
    expect(
      screen.getByTestId('DropdownComp-roomPicker-dropdownContent-accessibleRoomLabel')
    ).toBeInTheDocument();
    expect(
      screen.getByTestId('DropdownComp-roomPicker-dropdownContent-cotRoomSwitcher')
    ).toBeInTheDocument();
    expect(
      screen.getByTestId('DropdownComp-roomPicker-dropdownContent-cotRoomCheckbox')
    ).toBeInTheDocument();
  });

  it('renders remove room link for each room', () => {
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
            { ...defaultProps.rooms[0], id: '3' },
          ]}
        />
      </Menu>
    );
    const removeLinks = screen.getAllByText('Remove room');
    expect(removeLinks.length).toBe(3); // camerele 2 și 3
  });

  it('calls removeRoom for correct room when remove link is clicked', () => {
    const removeRoom = jest.fn();
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          removeRoom={removeRoom}
        />
      </Menu>
    );
    const removeLinks = screen.getAllByText('Remove room');
    fireEvent.click(removeLinks[1]); // click pe al doilea link
    expect(removeRoom).toHaveBeenCalled();
  });

  it('renders divider for multiple rooms', () => {
    const { container } = render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
        />
      </Menu>
    );
    expect(container.querySelector('.roompicker-divider')).toBeInTheDocument();
  });

  it('renders notification when max rooms reached', () => {
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          maxNumberOfRooms={2}
        />
      </Menu>
    );
    expect(screen.getByText(labels.roomsWarningTitle)).toBeInTheDocument();
    expect(screen.getByText(labels.roomsWarningDescription)).toBeInTheDocument();
  });

  it('renders CCUI description when channel is CCUI', () => {
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          maxNumberOfRooms={2}
          channel="CCUI"
        />
      </Menu>
    );
    expect(screen.getByText(labels.roomsWarningTitle)).toBeInTheDocument();
    expect(screen.getByText(labels.roomsWarningDescriptionCCUI)).toBeInTheDocument();
  });

  it('calls addRoom when add another room is clicked', () => {
    const addRoom = jest.fn();
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          addRoom={addRoom}
          rooms={[{ ...defaultProps.rooms[0], id: '1' }]}
        />
      </Menu>
    );
    fireEvent.click(screen.getByTestId('addMoreRooms'));
    expect(addRoom).toHaveBeenCalled();
  });

  it('calls onSubmit and setIsOpen when Done button is clicked', () => {
    const onSubmit = jest.fn();
    const setIsOpen = jest.fn();
    render(
      <Menu>
        <DropdownContent {...defaultProps} onSubmit={onSubmit} setIsOpen={setIsOpen} />
      </Menu>
    );
    fireEvent.click(screen.getByTestId('doneButton'));
    expect(onSubmit).toHaveBeenCalled();
    expect(setIsOpen).toHaveBeenCalledWith(false);
  });

  it('calls updateRoom when cot switch is toggled', () => {
    const updateRoom = jest.fn();
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          updateRoom={updateRoom}
          rooms={[{ ...defaultProps.rooms[0], shouldIncludeCot: false }]}
          noRoomTypeSearch={true}
        />
      </Menu>
    );
    const cotCheckbox = screen.getByTestId(
      'DropdownComp-roomPicker-dropdownContent-cotRoomCheckbox'
    );
    fireEvent.click(cotCheckbox);
    expect(updateRoom).toHaveBeenCalled();
  });

  it('calls updateRoom when accessible switch is toggled (noRoomTypeSearch)', () => {
    const updateRoom = jest.fn();
    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          updateRoom={updateRoom}
          rooms={[{ ...defaultProps.rooms[0], shouldBeAccessible: false }]}
          noRoomTypeSearch={true}
        />
      </Menu>
    );
    fireEvent.click(
      screen
        .getByTestId('DropdownComp-roomPicker-dropdownContent-accessibleRoomSwitcher')
        .querySelector('input[type="checkbox"]')
    );
    expect(updateRoom).toHaveBeenCalled();
  });

  it('should render cotRoomSwitcher checkbox when noRoomTypeSearch is true', () => {
    const defaultProps = {
      setIsOpen: jest.fn(),
      rooms: [
        {
          id: 'room1',
          adults: 2,
          children: 1,
          shouldIncludeCot: false,
          shouldBeAccessible: false,
          roomType: 'double',
        },
      ],
      addRoom: jest.fn(),
      removeRoom: jest.fn(),
      updateRoom: jest.fn(),
      onSubmit: jest.fn(),
      adultsOptions: [
        { id: 1, label: '1' },
        { id: 2, label: '2' },
      ],
      childrenOptions: [
        { id: 0, label: '0' },
        { id: 1, label: '1' },
      ],
      labels: {
        removeRoomButtonLabel: 'Remove',
        roomsWarningTitle: '',
        roomsWarningDescription: '',
        roomsWarningDescriptionCCUI: '',
        addMoreRoomsLabel: '',
        doneButtonLabel: 'Done',
        adultsLabel: 'Adults',
        adultsMaxPerRoomLabel: '',
        childrenLabel: 'Children',
        childrenAgeLabel: '',
        cotLimit: '',
        cotLabel: 'Cot',
        roomTypeLabel: '',
        roomLabel: 'Room',
        single: '',
        double: '',
        accessible: 'Accessible',
        twin: '',
        family: '',
      },
      maxNumberOfRooms: 2,
      dataRoomOccupancyLimitations: {
        roomOccupancyLimitations: { roomOccupancies: [] },
      },
      roomCodes: {},
      dataTestId: 'dropdown-content',
      screenSize: { isLessThanXs: false, isLessThanSm: false, isLessThanMd: false },
      showMultipleRooms: false,
      channel: '',
      className: '',
      noRoomTypeSearch: true,
    };

    render(
      <Menu>
        <DropdownContent {...defaultProps} />
      </Menu>
    );
    expect(screen.getByTestId('dropdown-content-cotRoomSwitcher')).toBeInTheDocument();
    expect(screen.getByTestId('dropdown-content-cotRoomCheckbox')).toBeInTheDocument();
  });
});

describe('DropdownContent keyboard interactions', () => {
  it('calls tabbingAccessibility and removeRoom on Space key press', () => {
    const removeRoom = jest.fn();
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          removeRoom={removeRoom}
          focusableRefs={focusableRefs}
          tabbingAccessibility={tabbingAccessibilityMock}
        />
      </Menu>
    );

    const removeLink = screen.getByTestId('remove-room-1');

    fireEvent.keyDown(removeLink, { key: ' ' });

    expect(tabbingAccessibilityMock).toHaveBeenCalled();
    expect(removeRoom).toHaveBeenCalledWith('1');
  });

  it('does not call tabbingAccessibility when not provided', () => {
    const removeRoom = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          removeRoom={removeRoom}
          focusableRefs={focusableRefs}
        />
      </Menu>
    );

    const removeLink = screen.getByTestId('remove-room-1');

    fireEvent.keyDown(removeLink, { key: '' });

    expect(removeRoom).not.toHaveBeenCalled();
  });

  it('does not call removeRoom on non-space key', () => {
    const removeRoom = jest.fn();
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          rooms={[
            { ...defaultProps.rooms[0], id: '1' },
            { ...defaultProps.rooms[0], id: '2' },
          ]}
          removeRoom={removeRoom}
          tabbingAccessibility={tabbingAccessibilityMock}
          focusableRefs={focusableRefs}
        />
      </Menu>
    );

    const removeLink = screen.getByTestId('remove-room-1');

    fireEvent.keyDown(removeLink, { key: 'W' });

    expect(tabbingAccessibilityMock).toHaveBeenCalled();
    expect(removeRoom).not.toHaveBeenCalled();
  });

  it('calls tabbingAccessibility on keyDown for accessible checkbox', () => {
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          noRoomTypeSearch={true}
          tabbingAccessibility={tabbingAccessibilityMock}
          focusableRefs={focusableRefs}
          rooms={[
            {
              ...defaultProps.rooms[0],
              id: '1',
              children: 0,
              shouldBeAccessible: false,
            },
          ]}
        />
      </Menu>
    );

    const wrapper = screen.getByTestId(`${defaultProps.dataTestId}-accessibleRoomSwitcher`);

    const checkbox = wrapper.querySelector('input') as HTMLElement;

    fireEvent.keyDown(checkbox, { key: 'Tab' });

    expect(tabbingAccessibilityMock).toHaveBeenCalledWith(
      expect.objectContaining({ key: 'Tab' }),
      focusableRefs
    );
  });

  it('calls tabbingAccessibility on keyDown for cot checkbox', () => {
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          noRoomTypeSearch={true}
          tabbingAccessibility={tabbingAccessibilityMock}
          focusableRefs={focusableRefs}
          rooms={[
            {
              ...defaultProps.rooms[0],
              id: '1',
              children: 0,
              shouldBeAccessible: false,
            },
          ]}
        />
      </Menu>
    );

    const wrapper = screen.getByTestId(`${defaultProps.dataTestId}-cotRoomCheckbox`);

    const checkbox = wrapper.querySelector('input') as HTMLElement;

    fireEvent.keyDown(checkbox, { key: 'Tab' });

    expect(tabbingAccessibilityMock).toHaveBeenCalledWith(
      expect.objectContaining({ key: 'Tab' }),
      focusableRefs
    );
  });

  it('calls tabbingAccessibility and addRoom on Space key press', () => {
    const addRoomMock = jest.fn();
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          addRoom={addRoomMock}
          tabbingAccessibility={tabbingAccessibilityMock}
          focusableRefs={focusableRefs}
          rooms={[{ ...defaultProps.rooms[0], id: '1' }]}
          maxNumberOfRooms={5}
          showMultipleRooms={false}
        />
      </Menu>
    );

    const addRoomButton = screen.getByTestId('addMoreRooms');

    fireEvent.keyDown(addRoomButton, { key: ' ' });

    expect(tabbingAccessibilityMock).toHaveBeenCalledWith(
      expect.objectContaining({ key: ' ' }),
      focusableRefs
    );

    expect(addRoomMock).toHaveBeenCalled();
  });

  it('does not call addRoom on non-space key', () => {
    const tabbingAccessibilityMock = jest.fn();
    const addRoomMock = jest.fn();

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          addRoom={addRoomMock}
          tabbingAccessibility={tabbingAccessibilityMock}
          rooms={[{ ...defaultProps.rooms[0], id: '1' }]}
          maxNumberOfRooms={5}
          showMultipleRooms={false}
        />
      </Menu>
    );

    const addRoomButton = screen.getByTestId('addMoreRooms');

    fireEvent.keyDown(addRoomButton, { key: 'Enter' });

    expect(addRoomMock).not.toHaveBeenCalled();
  });

  it('calls tabbingAccessibility on keyDown for done button', () => {
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = { current: [] as HTMLElement[] };

    render(
      <Menu>
        <DropdownContent
          {...defaultProps}
          tabbingAccessibility={tabbingAccessibilityMock}
          focusableRefs={focusableRefs}
        />
      </Menu>
    );

    const doneButton = screen.getByTestId('doneButton');

    fireEvent.keyDown(doneButton, { key: 'Tab' });

    expect(tabbingAccessibilityMock).toHaveBeenCalledWith(
      expect.objectContaining({ key: 'Tab' }),
      focusableRefs
    );
  });
});
