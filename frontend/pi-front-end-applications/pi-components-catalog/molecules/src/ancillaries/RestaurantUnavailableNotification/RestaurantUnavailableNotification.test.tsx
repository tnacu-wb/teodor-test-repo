import '@testing-library/jest-dom';

import { render, waitFor } from '../../utils/test-utils';
import RestaurantUnavailableNotification from './RestaurantUnavailableNotification.component';

describe('RestaurantUnavailableNotification', () => {
  it('should render a <RestaurantUnavailableNotification/> ', async () => {
    const { getByTestId } = render(<RestaurantUnavailableNotification />);
    await waitFor(() => {
      expect(getByTestId('RestaurantUnavailableNotification-Wrapper')).toBeInTheDocument();
    });
  });
});
