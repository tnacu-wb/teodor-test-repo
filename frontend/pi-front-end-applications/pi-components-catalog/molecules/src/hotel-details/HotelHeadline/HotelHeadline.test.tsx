import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelHeadlineComponent from './HotelHeadline.component';

const hotelHeadlineProps = {
  headline:
    'Just a five-minute walk from Old Street station, close to the best nightlife in Shoreditch',
};

describe('HotelHeadline', () => {
  it('should render HotelHeadline', () => {
    const { getByTestId } = render(<HotelHeadlineComponent {...hotelHeadlineProps} />);
    expect(getByTestId('hdp_hotelHeadline')).toBeInTheDocument();
  });

  it('should render hotel headline text', () => {
    const { getByText } = render(<HotelHeadlineComponent {...hotelHeadlineProps} />);
    expect(getByText(hotelHeadlineProps.headline)).toBeInTheDocument();
  });

  it('should render null if no headline prop is passed', () => {
    const { queryByText } = render(<HotelHeadlineComponent headline="" />);
    expect(queryByText(hotelHeadlineProps.headline)).toBeNull();
  });
});
