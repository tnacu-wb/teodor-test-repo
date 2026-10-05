import RoomSuccessNotification from '.';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

describe('RoomSuccessNotification component', () => {
  it('should render the component', () => {
    const { getByText, getByRole } = render(
      <RoomSuccessNotification description="Notification description" dataTestId="notification" />
    );
    expect(getByRole('status')).toBeInTheDocument();
    expect(getByText('Notification description')).toBeInTheDocument();
  });
});
