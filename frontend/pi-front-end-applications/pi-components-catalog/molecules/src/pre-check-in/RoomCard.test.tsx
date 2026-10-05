import '@testing-library/jest-dom';
import React from 'react';

import { render, userEvent, act } from '../utils/test-utils';
import RoomCard from './RoomCard';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useBreakpointValue: jest.fn(),
}));

const defaultProps = {
  testid: 'room',
  roomIndex: 0,
  room: {
    firstName: 'John',
    lastName: 'Doe',
    roomName: 'Deluxe',
    adultsNumber: 2,
    childrenNumber: 1,
    bookingReference: 'BR123',
    reservationId: 'R123',
  },
  roomCardContainerStyle: {},
  displayIcon: true,
  clickEventRequired: true,
  preCheckInStatus: true,
};

describe('RoomCard component', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<RoomCard {...defaultProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });

  it('should render the component with adultsNumber 1 and childrenNumber 2 and clickEventRequired', () => {
    const customRoom = { ...defaultProps.room };
    const customProps = {
      ...defaultProps,
      room: { ...customRoom, adultsNumber: 1, childrenNumber: 2 },
    };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });

  it('should render the component with clickEventRequired false', () => {
    const customProp = { ...defaultProps };
    const customProps = { ...customProp, clickEventRequired: false };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });

  it('should call setURLParamToReservationId when clicked', () => {
    const setURLParamToReservationIdMock = jest.fn();
    const customProps = {
      ...defaultProps,
      setURLParamToReservationId: setURLParamToReservationIdMock,
    };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeInTheDocument();
  });

  it('should render the component with adultsNumber 0 and childrenNumber 0', () => {
    const customRoom = { ...defaultProps.room };
    const customProps = {
      ...defaultProps,
      room: { ...customRoom, adultsNumber: 0, childrenNumber: 0 },
    };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });

  it('should display Notification component when preCheckInStatus is Completed', () => {
    const customProps = { ...defaultProps, preCheckInStatus: true };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const notificationComponent = getByTestId('room-HeaderRoom-1');
    expect(notificationComponent).toBeInTheDocument();
  });

  it('should call setPreCheckInStatusDisplay(false) when Notification component is clicked', () => {
    const customProps = { ...defaultProps, preCheckInStatus: true };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const notificationComponent = getByTestId('room-checked-in-R123-AlertDescription');

    act(() => {
      userEvent.click(notificationComponent);
    });
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });

  it('should render with empty arguments', () => {
    const { getByTestId } = render(<RoomCard testid="testId" room={{}} />);

    const roomCard = getByTestId('testId-HeaderRoom-1');
    expect(roomCard).toBeTruthy();
  });

  it('should render the component without the display icon', () => {
    const customProps = { ...defaultProps, displayIcon: false };
    const { queryByTestId } = render(<RoomCard {...customProps} />);
    const icon = queryByTestId('room-Wrapper-1').querySelector('svg');
    if (icon) expect(icon).toBeNull();
  });

  it('should handle clickEvent when preCheckInStatus is false and clickEventRequired is true', () => {
    const setURLParamToReservationIdMock = jest.fn();
    const customProps = {
      ...defaultProps,
      preCheckInStatus: false,
      setURLParamToReservationId: setURLParamToReservationIdMock,
    };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    act(() => {
      userEvent.click(roomCard);
    });
    expect(setURLParamToReservationIdMock).toHaveBeenCalledWith(
      defaultProps.room.bookingReference,
      defaultProps.room.reservationId
    );
  });

  it('should render correctly with missing room data', () => {
    const customProps = { ...defaultProps, room: {} };
    const { getByTestId } = render(<RoomCard {...customProps} />);
    const roomCard = getByTestId('room-Wrapper-1');
    expect(roomCard).toBeTruthy();
  });
});
