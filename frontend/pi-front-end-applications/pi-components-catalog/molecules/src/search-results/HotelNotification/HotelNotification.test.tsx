import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelNotification from './HotelNotification.component';

const mockedLabels = {
  openingSoon:
    'This hotel is fully booked on your chosen dates. Here are some nearby hotels with available rooms.',
  fullyBooked: 'This hotel will be opening soon. Here are some nearby hotels with available rooms.',
};
describe('SRP - HotelNotification', () => {
  it('should display the Opening soon notification', () => {
    const { getByText } = render(<HotelNotification description={mockedLabels.openingSoon} />);
    expect(getByText(mockedLabels.openingSoon)).toBeInTheDocument();
  });
  it('should display the fully booked notification', () => {
    const { getByText } = render(<HotelNotification description={mockedLabels.fullyBooked} />);
    expect(getByText(mockedLabels.fullyBooked)).toBeInTheDocument();
  });
});
