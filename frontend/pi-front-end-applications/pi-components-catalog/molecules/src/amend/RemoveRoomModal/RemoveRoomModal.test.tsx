import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import { mockedRemoveRoomModalLabels } from '../utilities/mockResponse';
import RemoveRoomModal from './RemoveRoomModal.component';

const mockedOnRemoveRoom = jest.fn();

const initialProps: any = {
  isOpen: true,
  onClose: jest.fn(),
  labels: mockedRemoveRoomModalLabels,
  roomNumber: 2,
  reservationId: '123test',
  onRemoveRoom: mockedOnRemoveRoom,
};

describe('Remove Room Modal', () => {
  beforeEach(function () {
    mockedOnRemoveRoom.mockClear();
  });

  it('should render the RemoveRoomModal component', async () => {
    const { getByRole } = render(<RemoveRoomModal {...initialProps} />);
    await waitFor(() => {
      expect(getByRole('dialog')).toBeVisible();
    });
  });

  it('should call the mutation by clicking the button', async () => {
    const { getByRole } = render(<RemoveRoomModal {...initialProps} />);
    await waitFor(() => {
      const removeButton = getByRole('button', { name: 'Remove Room' });
      fireEvent.click(removeButton);
      expect(mockedOnRemoveRoom).toBeCalledWith(
        initialProps.reservationId,
        initialProps.roomNumber
      );
      expect(getByRole('dialog')).not.toBeVisible();
    });
  });
});
