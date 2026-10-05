import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelSoldOut from './HotelSoldOut.component';

describe('SRP - HotelSoldOut', () => {
  it('should display the Sold out label', () => {
    const { getByText } = render(<HotelSoldOut label="Sold out" testId="SRP-hotel-soldout" />);
    expect(getByText('Sold out')).toBeInTheDocument();
  });
});
