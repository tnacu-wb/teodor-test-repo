import { Box } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import RoomsRemainingBadgeComponent from './RoomsRemainingBadge';

describe('RoomsTemainingBadge', () => {
  it('should render RoomsRemainigBadge', () => {
    const props = { numberOfRoomsAvailable: 5 };
    const { queryByTestId } = render(<RoomsRemainingBadgeComponent {...props} />);
    expect(queryByTestId('urgencyMessage')).toBeInTheDocument();
  });

  it('should not render RoomsRemainigBadge when the number of rooms is more than 10', () => {
    const props = { numberOfRoomsAvailable: 15 };
    const { queryByTestId } = render(<RoomsRemainingBadgeComponent {...props} />);
    expect(queryByTestId('urgencyMessage')).not.toBeInTheDocument();
  });

  it('should not render RoomsRemainigBadge when the number of rooms is less than 1', () => {
    const props = { numberOfRoomsAvailable: 0 };
    const { queryByTestId } = render(<RoomsRemainingBadgeComponent {...props} />);
    expect(queryByTestId('urgencyMessage')).not.toBeInTheDocument();
  });

  it('should not render RoomsRemainigBadge when the number of rooms is null', () => {
    const props = { numberOfRoomsAvailable: undefined };
    const { queryByTestId } = render(<RoomsRemainingBadgeComponent {...props} />);
    expect(queryByTestId('urgencyMessage')).not.toBeInTheDocument();
  });

  it('should independently show the badge for each room type', () => {
    const rooms = [
      { type: 'Standard', rooms: 6 },
      { type: 'Premier Plus', rooms: 200 },
    ];

    const MockRoomList = () => (
      <Box>
        {rooms.map((room) => (
          <Box key={room.type} data-testid={`${room.type}-room`}>
            <RoomsRemainingBadgeComponent numberOfRoomsAvailable={room.rooms} />
          </Box>
        ))}
      </Box>
    );

    const { queryAllByTestId } = render(<MockRoomList />);
    expect(queryAllByTestId('urgencyMessage')).toHaveLength(1);
  });
});
