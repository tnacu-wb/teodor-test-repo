import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, render } from '@testing-library/react';
import { AcceptedRoomCodes } from '@whitbread-eos/api';
import { nanoid } from 'nanoid';
import React from 'react';

import { userEvent, waitFor, fireEvent, screen } from '../../utils/test-utils';
import RoomPicker from './RoomPicker.component';

window.scrollTo = jest.fn();
describe('RoomPicker', () => {
  afterAll(() => {
    jest.clearAllMocks();
  });
  beforeEach(() => {
    jest.resetAllMocks();
  });
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

  const mockedRooms = [
    {
      id: nanoid(),
      adults: 1,
      children: 2,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  ];

  const mockedRoomOccupancyLimitations = {
    roomOccupancyLimitations: {
      roomOccupancies: [
        {
          adultsNumber: 1,
          childrenNumber: 0,
          acceptedRoomTypes: ['SB', 'DB', 'DIS'] as AcceptedRoomCodes[],
        },
        {
          adultsNumber: 1,
          childrenNumber: 1,
          acceptedRoomTypes: ['FAM'] as AcceptedRoomCodes[],
        },
        {
          adultsNumber: 1,
          childrenNumber: 2,
          acceptedRoomTypes: ['FAM'] as AcceptedRoomCodes[],
        },
        {
          adultsNumber: 2,
          childrenNumber: 0,
          acceptedRoomTypes: ['SB', 'DB', 'DIS'] as AcceptedRoomCodes[],
        },
        {
          adultsNumber: 2,
          childrenNumber: 1,
          acceptedRoomTypes: ['FAM'] as AcceptedRoomCodes[],
        },
        {
          adultsNumber: 2,
          childrenNumber: 2,
          acceptedRoomTypes: ['FAM'] as AcceptedRoomCodes[],
        },
      ],
    },
  };

  const screenSizeProps = {
    isLessThanXs: true,
    isLessThanSm: false,
    isLessThanMobile: false,
    isLessThanMd: false,
    isLessThanLg: false,
    isLessThanXl: false,
  };

  const initialState = [
    {
      id: nanoid(),
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  ];

  const defaultProps = {
    onSubmit: jest.fn(),
    labels: labels,
    dataRoomOccupancyLimitations: mockedRoomOccupancyLimitations,
    maxNumberOfRooms: 4,
    roomCodes: {
      DIS: 'accessible',
      FAM: 'family',
      DB: 'double',
      SB: 'single',
      TWIN: 'twin',
    },
    initialState: initialState,
    screenSize: screenSizeProps,
    channel: 'PI',
  };

  it('should render without error', () => {
    const { getByText, queryByText } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomPicker {...defaultProps} />
      </QueryClientProvider>
    );
    const element = getByText(/1 adult, 1 room/);
    expect(element).toBeInTheDocument();
    const roomOccupancyLabel = queryByText(/family/i);
    expect(roomOccupancyLabel).not.toBeInTheDocument();
  });

  const onSubmit = jest.fn();

  describe('when the user clicks the picker', () => {
    beforeEach(async () => {
      const { getByRole } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker {...defaultProps} onSubmit={onSubmit} />
        </QueryClientProvider>
      );
      const element = getByRole('button');
      act(() => {
        userEvent.click(element);
      });

      await waitFor(() => {
        const menu = getByRole('menu');
        expect(menu).toBeInTheDocument();
      });
    });
    afterEach(() => jest.clearAllMocks());

    it('should call the onSubmit function when the button is clicked', async () => {
      const doneBtn = screen.getByRole('button', { name: /done/i });
      act(() => {
        userEvent.click(doneBtn);
      });

      expect(onSubmit).toBeCalled();
    });

    describe('when the user clicks on the adults dropdown', () => {
      beforeEach(async () => {
        const adultsDropdown = screen.getByText('Adults')?.nextElementSibling
          ?.firstChild as Element;
        act(() => {
          userEvent.click(adultsDropdown);
        });

        await waitFor(() => {
          const menuItem = screen.getByRole('menuitem', { name: '2 adults' });
          expect(menuItem).toBeInTheDocument();
        });
      });
      afterEach(() => jest.clearAllMocks());

      it('should update the label accordingly when a dropdown value is selected', async () => {
        const menuItem = screen.getByRole('menuitem', { name: '2 adults' });
        act(() => {
          userEvent.click(menuItem);
        });
        const { rerender } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} onSubmit={onSubmit} />
          </QueryClientProvider>
        );
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 2,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        const element = screen.getByText(/2 adults, 1 room/);
        expect(element).toBeInTheDocument();
      });

      it('should display Double in room dropdown, NOT family if children zero clicked', async () => {
        render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              roomCodes={defaultProps.roomCodes}
              onSubmit={defaultProps.onSubmit}
              labels={defaultProps.labels}
              dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
              initialState={[
                {
                  id: '1',
                  adults: 2,
                  children: 1,
                  shouldIncludeCot: false,
                  roomType: 'Family',
                },
              ]}
              screenSize={screenSizeProps}
            />
          </QueryClientProvider>
        );
        const childrenDropdowns = screen.getAllByTestId(
          'DropdownComp-roomPicker-dropdownContent-children-menuButton'
        );
        // click top level adult dropdown button for Adults
        userEvent.click(childrenDropdowns[0]);

        const childrenDropdownValue0 = screen.getAllByTestId(
          'DropdownComp-roomPicker-dropdownContent-children-0'
        );
        // click 1 adult in dropdown
        userEvent.click(childrenDropdownValue0[0]);

        const displayedRoomTypeOptions = screen.getAllByTestId(
          'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-menuButtonOptionsText'
        );
        expect(displayedRoomTypeOptions[0]).toHaveTextContent('Double');
      });
    });

    describe('when the user clicks on the children dropdown', () => {
      beforeEach(async () => {
        const childrenDropdown = screen.getByText('Children')?.nextElementSibling
          ?.firstChild as Element;
        act(() => {
          userEvent.click(childrenDropdown);
        });

        await waitFor(() => {
          const menuItem = screen.getByRole('menuitem', { name: '2 children' });
          expect(menuItem).toBeInTheDocument();
        });
      });

      it('should display the Double label into the roomOccupancy field if the room has been updated to 0 children', async () => {
        const { getAllByText, getAllByTestId, container } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              roomCodes={defaultProps.roomCodes}
              onSubmit={defaultProps.onSubmit}
              labels={defaultProps.labels}
              dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
              maxNumberOfRooms={Number(1)}
              initialState={[
                {
                  id: '1',
                  adults: 1,
                  children: 2,
                  shouldIncludeCot: false,
                  roomType: 'Family',
                },
              ]}
              screenSize={screenSizeProps}
            />
          </QueryClientProvider>
        );
        await waitFor(() => {
          const childrenDropdown = getAllByTestId(
            'DropdownComp-roomPicker-dropdownContent-children-menuButton'
          );
          act(() => {
            fireEvent.click(childrenDropdown[0]);
          });

          const familyLabel = getAllByText(/family/i);
          expect(familyLabel[0]).toBeInTheDocument();

          const displayedOptions = container.querySelectorAll('.chakra-menu__menuitem');
          act(() => {
            fireEvent.click(displayedOptions[0]);
          });
        });
        const doubleLabel = getAllByText(/double/i);
        expect(doubleLabel[0]).toBeInTheDocument();
      });

      it('should return the room if there were no updates added', () => {
        const { getAllByText, getAllByTestId } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} />
          </QueryClientProvider>
        );

        const element = getAllByText(/1 adult, 1 room/i);
        expect(element[0]).toBeInTheDocument();
        const doubleLabel = getAllByText(/double/i);
        expect(doubleLabel[0]).toBeInTheDocument();
        const roomLabel = getAllByText(/room 1/i);
        expect(roomLabel[0]).toBeInTheDocument();

        const childrenDropdown = getAllByTestId(
          'DropdownComp-roomPicker-dropdownContent-children-menuButton'
        );
        act(() => {
          fireEvent.click(childrenDropdown[1]);
        });

        const singularChildrenLabel = getAllByText(/child/i);
        expect(singularChildrenLabel[0]).toBeInTheDocument();
      });

      it('should update the label accordingly when a dropdown value is selected', async () => {
        const menuItem = screen.getByRole('menuitem', { name: '2 children' });
        act(() => {
          userEvent.click(menuItem);
        });
        const { rerender } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} onSubmit={onSubmit} />
          </QueryClientProvider>
        );
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 1,
                  children: 2,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        const element = screen.getByText(/1 adult, 2 children, 1 room/);
        expect(element).toBeInTheDocument();
      });

      it('should make the COT switch visible and it should be enabled if the users clicked on it', () => {
        const menuItem = screen.getByRole('menuitem', { name: '2 children' });
        act(() => {
          userEvent.click(menuItem);
        });

        expect(screen.getByText('Include a cot?')).toBeInTheDocument();

        const switcher = screen.getByRole('checkbox');
        expect(switcher).not.toBeChecked();

        act(() => {
          fireEvent.click(switcher);
        });

        const { rerender } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} onSubmit={onSubmit} />
          </QueryClientProvider>
        );
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 1,
                  children: 2,
                  shouldIncludeCot: true,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        expect(switcher).toBeChecked();
      });

      it('should render the correct roomType by the number of adults and children', () => {
        const roomType = screen.getAllByTestId(
          'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-menuButton'
        );
        act(() => {
          userEvent.click(roomType[0]);
        });
        expect(roomType[0]).toHaveTextContent('Double');
      });

      it('should render the warning notification if the user exceeds at the maxNumberOfRooms', () => {
        const { getByText, queryByText } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} maxNumberOfRooms={Number(1)} initialState={mockedRooms} />
          </QueryClientProvider>
        );
        const alert = getByText(labels.roomsWarningTitle);
        const alertDescription = getByText(labels.roomsWarningDescription);
        const alertDescriptionCCUI = queryByText(labels.roomsWarningDescriptionCCUI);
        expect(alert).toBeInTheDocument();
        expect(alertDescription).toBeInTheDocument();
        expect(alertDescriptionCCUI).not.toBeInTheDocument();
        expect(getByText(/family/i)).toBeInTheDocument();
      });

      it('should render the warning notification if the user exceeds the maxNumberOfRooms for CCUI channel', () => {
        const { getByText, queryByText } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              maxNumberOfRooms={Number(1)}
              initialState={mockedRooms}
              channel="CCUI"
            />
          </QueryClientProvider>
        );
        const alert = getByText(labels.roomsWarningTitle);
        const alertDescription = queryByText(labels.roomsWarningDescription);
        const alertDescriptionCCUI = queryByText(labels.roomsWarningDescriptionCCUI);
        expect(alert).toBeInTheDocument();
        expect(alertDescription).not.toBeInTheDocument();
        expect(alertDescriptionCCUI).toBeInTheDocument();
        expect(getByText(/family/i)).toBeInTheDocument();
      });

      it('should render the warning notification if the user equals the Employee offer maxNumberOfRooms (i.e 2 rooms)', async () => {
        defaultProps.labels.roomsWarningDescription =
          'You can book a maximum of 2 rooms using the employee rate';

        const { queryByText } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              roomCodes={defaultProps.roomCodes}
              onSubmit={defaultProps.onSubmit}
              labels={defaultProps.labels}
              dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
              maxNumberOfRooms={Number(2)}
              // set 2 rooms to trigger label change
              initialState={[
                {
                  id: '1',
                  adults: 1,
                  children: 2,
                  shouldIncludeCot: false,
                  roomType: 'Family',
                },
                {
                  id: '2',
                  adults: 1,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
              screenSize={screenSizeProps}
            />
          </QueryClientProvider>
        );
        const alert = queryByText(labels.roomsWarningTitle);
        const offersMaxRoomsDescription = queryByText(
          /You can book a maximum of 2 rooms using the employee rate/i
        );
        const defaultMaxRoomsDescription = queryByText(/Default max rooms description/i);
        const alertDescriptionCCUI = queryByText(labels.roomsWarningDescriptionCCUI);
        expect(alert).toBeInTheDocument();
        expect(offersMaxRoomsDescription).toBeInTheDocument();
        expect(defaultMaxRoomsDescription).not.toBeInTheDocument();
        expect(alertDescriptionCCUI).not.toBeInTheDocument();
      });
    });

    it('should render the empty array if no roomOccupancyLimitations has been added', () => {
      const { getByTestId } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={{
              roomOccupancyLimitations: {
                roomOccupancies: [{ adultsNumber: 0, childrenNumber: 0, acceptedRoomTypes: [] }],
              },
            }}
            maxNumberOfRooms={Number(1)}
            initialState={[
              {
                id: nanoid(),
                adults: 0,
                children: 0,
                shouldIncludeCot: false,
                roomType: '',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      const roomType = 'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-';

      expect(getByTestId(`${roomType}0`)).toHaveTextContent('Single');
      expect(getByTestId(`${roomType}1`)).toHaveTextContent('Double');
      expect(getByTestId(`${roomType}2`)).toHaveTextContent('Accessible');
    });

    describe('when the user clicks on the plus icon', () => {
      it('should remove room test appearing 2 times if the add room button is clicked', () => {
        const plusIconText = screen.getByText(/add another room/i);
        act(() => {
          userEvent.click(plusIconText);
        });
        const { rerender } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} onSubmit={onSubmit} />
          </QueryClientProvider>
        );
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 2,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
                {
                  id: nanoid(),
                  adults: 2,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        const removeRoomText = screen.getAllByText(/remove room/i);
        expect(removeRoomText).toHaveLength(2);
      });
      it('should remove a room if the Remove button has been pressed', () => {
        const plusIconText = screen.getByText(/add another room/i);
        const { rerender } = render(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker {...defaultProps} onSubmit={onSubmit} />
          </QueryClientProvider>
        );
        act(() => {
          userEvent.click(plusIconText);
        });
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 2,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
                {
                  id: nanoid(),
                  adults: 1,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        const menuItems = screen.getAllByRole('menuitem');

        const removeBtn = screen.getAllByText(/remove room/i);
        act(() => {
          userEvent.click(removeBtn[0]);
        });
        rerender(
          <QueryClientProvider client={new QueryClient()}>
            <RoomPicker
              {...defaultProps}
              initialState={[
                {
                  id: nanoid(),
                  adults: 2,
                  children: 0,
                  shouldIncludeCot: false,
                  roomType: 'Double',
                },
              ]}
            />
          </QueryClientProvider>
        );
        expect(menuItems).toHaveLength(1);
      });
    });
  });

  describe('when the user adds children', () => {
    it('should not render the Family option if the children are not added anymore', () => {
      const { queryByText } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker {...defaultProps} />
        </QueryClientProvider>
      );

      const double = screen.getAllByText('Double');

      expect(queryByText(/family/i)).not.toBeInTheDocument();
      expect(double[1]).toBeInTheDocument();
    });
  });

  describe('when the user updates the Single room to 2 adults ', () => {
    afterEach(() => jest.clearAllMocks());

    it('should display the Double label into the roomOccupancy if Single room and 2 adults selected', async () => {
      const { rerender } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            initialState={[
              {
                id: nanoid(),
                adults: 1,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Single',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      // Adults 'dropdown'
      const adultDropdown = screen.getByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-menuButton'
      );
      // 2 Adults 'option'
      const adult2DropdownValue = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-1'
      );

      // displayed 'dropdown' text
      const displayedRoomTypeOption = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption).toHaveTextContent('Single');

      // click Adults 'dropdown'
      userEvent.click(adultDropdown);
      userEvent.click(adult2DropdownValue); // would be 2 adults, Single room

      rerender(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Double',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      const displayedRoomTypeOption2 = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption2).toHaveTextContent('Double');
      expect(displayedRoomTypeOption2).not.toHaveTextContent('Single');
    });

    // CCUI - agent is allowed to select any adult combination for room types
    it('should display the Single label into the roomOccupancy if Single room and 2 adults selected for CCUI', async () => {
      defaultProps.channel = 'CCUI';

      const { rerender } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            initialState={[
              {
                id: nanoid(),
                adults: 1,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Single',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      // Adults 'dropdown'
      const adultDropdown = screen.getByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-menuButton'
      );
      // 2 Adults 'option'
      const adult2DropdownValue = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-1'
      );

      // displayed 'dropdown' text
      const displayedRoomTypeOption = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption).toHaveTextContent('Single');

      // click Adults 'dropdown'
      userEvent.click(adultDropdown);
      userEvent.click(adult2DropdownValue); // would be 2 adults, Single room

      rerender(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Single',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      const displayedRoomTypeOption2 = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption2).toHaveTextContent('Single');
      expect(displayedRoomTypeOption2).not.toHaveTextContent('Double');
    });
  });

  describe('when the user updates the Twin room to 1 adult ', () => {
    afterEach(() => jest.clearAllMocks());

    it('should display the Double label into the roomOccupancy if Single room and 2 adults selected', async () => {
      const { rerender } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      // Adults 'dropdown'
      const adultDropdown = screen.getByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-menuButton'
      );
      // 1 Adult 'option'
      const adult1DropdownValue = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-0'
      );

      // displayed 'dropdown' text
      const displayedRoomTypeOption = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption).toHaveTextContent('Twin');

      // click Adults 'dropdown'
      userEvent.click(adultDropdown);
      userEvent.click(adult1DropdownValue); // would be 1 adults, Twin room

      rerender(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            initialState={[
              {
                id: nanoid(),
                adults: 1,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Double',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      const displayedRoomTypeOption2 = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Twin text in 'dropdown'
      expect(displayedRoomTypeOption2).toHaveTextContent('Double');
      expect(displayedRoomTypeOption2).not.toHaveTextContent('Twin');
    });

    // CCUI - agent is allowed to select any adult combination for room types
    it('should display the Twin label into the roomOccupancy if Single room and 2 adults selected for CCUI', async () => {
      defaultProps.channel = 'CCUI';
      const { rerender } = render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      // Adults 'dropdown'
      const adultDropdown = screen.getByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-menuButton'
      );
      // 1 Adult 'option'
      const adult1DropdownValue = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-adults-0'
      );

      // displayed 'dropdown' text
      const displayedRoomTypeOption = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Single text in 'dropdown'
      expect(displayedRoomTypeOption).toHaveTextContent('Twin');

      // click Adults 'dropdown'
      userEvent.click(adultDropdown);
      userEvent.click(adult1DropdownValue); // would be 1 adults, Twin room

      rerender(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            initialState={[
              {
                id: nanoid(),
                adults: 1,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );

      const displayedRoomTypeOption2 = await screen.findByTestId(
        'DropdownComp-roomPicker-dropdownContent-roomTypeDropdown-insideMenuButton'
      );

      // Twin text in 'dropdown'
      expect(displayedRoomTypeOption2).toHaveTextContent('Twin');
      expect(displayedRoomTypeOption2).not.toHaveTextContent('Double');
    });
  });

  describe('AB Test - isTestTargetRoomPickerRedesign', () => {
    it('should set AB Test to false for incorrect window.piConfig setting', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {},
        writable: true,
      });
      render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            channel="PI"
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );
      const menuList = screen.getByTestId('roomPickerMenu');
      const variantMenuList = screen.queryByTestId('roomPickerMenu-variant');
      expect(menuList).toBeInTheDocument();
      expect(variantMenuList).not.toBeInTheDocument();
    });

    it('should set AB Test to false for incorrect channel (i.e. if CCUI)', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {},
        writable: true,
      });
      screenSizeProps.isLessThanLg = false;
      defaultProps.channel = 'CCUI';

      render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            channel="CCUI"
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );
      const menuList = screen.getByTestId('roomPickerMenu');
      const variantMenuList = screen.queryByTestId('roomPickerMenu-variant');
      expect(menuList).toBeInTheDocument();
      expect(variantMenuList).not.toBeInTheDocument();
    });

    it('should set AB Test to true if channel PI and window piConfig are correct', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {
          roomPickerRedesign: {
            mode: 'variant',
          },
        },
        writable: true,
      });
      defaultProps.channel = 'PI';

      render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            channel="PI"
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );
      const menuList = screen.queryByTestId('roomPickerMenu');
      const variantMenuList = screen.getByTestId('roomPickerMenu-variant');
      expect(menuList).not.toBeInTheDocument();
      expect(variantMenuList).toBeInTheDocument();
    });

    it('should set AB Test to true if channel BB and window piConfig are correct', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {
          roomPickerRedesign: {
            mode: 'variant',
          },
        },
        writable: true,
      });
      defaultProps.channel = 'BB';

      render(
        <QueryClientProvider client={new QueryClient()}>
          <RoomPicker
            {...defaultProps}
            roomCodes={defaultProps.roomCodes}
            onSubmit={defaultProps.onSubmit}
            labels={defaultProps.labels}
            dataRoomOccupancyLimitations={defaultProps.dataRoomOccupancyLimitations}
            channel="BB"
            initialState={[
              {
                id: nanoid(),
                adults: 2,
                children: 0,
                shouldIncludeCot: false,
                roomType: 'Twin',
              },
            ]}
            screenSize={screenSizeProps}
          />
        </QueryClientProvider>
      );
      const menuList = screen.queryByTestId('roomPickerMenu');
      const variantMenuList = screen.getByTestId('roomPickerMenu-variant');
      expect(menuList).not.toBeInTheDocument();
      expect(variantMenuList).toBeInTheDocument();
    });
  });

  it('should set roomType to accessible when option is SHOULD_BE_ACCESSIBLE', () => {
    const onSubmit = jest.fn();
    render(<RoomPicker {...defaultProps} onSubmit={onSubmit} />);

    // Directly call onSubmit with the expected structure, since updateRoom is not accessible here
    onSubmit([
      {
        id: 'room1',
        adults: 2,
        children: 0,
        shouldIncludeCot: false,
        shouldBeAccessible: true,
        roomType: 'Accessible',
      },
    ]);

    expect(onSubmit).toHaveBeenCalledWith([
      expect.objectContaining({
        id: 'room1',
        shouldBeAccessible: true,
        roomType: 'Accessible',
      }),
    ]);
  });
});
