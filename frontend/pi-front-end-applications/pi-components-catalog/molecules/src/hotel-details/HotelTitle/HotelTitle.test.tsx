import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelTitle from './HotelTitle.component';

describe('HotelTitle', () => {
  it('should render HotelTitle', () => {
    const { getByTestId } = render(<HotelTitle name="London City (Old Street) hotel" />);
    expect(getByTestId('hdp_hotelTitle')).toBeInTheDocument();
  });

  it('should render hotel title', () => {
    const { getByText } = render(<HotelTitle name="London City (Old Street) hotel" />);
    expect(getByText('London City (Old Street) hotel')).toBeInTheDocument();
  });
});
