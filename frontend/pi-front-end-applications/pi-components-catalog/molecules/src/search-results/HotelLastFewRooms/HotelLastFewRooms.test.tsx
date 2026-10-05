import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelLastFewRooms from './HotelLastFewRooms.component';

const props = {
  label: 'Last few rooms',
  testId: 'SRP-hotel-lastfewrooms',
};

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => Promise.resolve({}),
}));

describe('SRP - HotelLastFewRooms', () => {
  it('should display "Last few rooms" label', () => {
    const { getByText } = render(<HotelLastFewRooms {...props} />);
    expect(getByText('Last few rooms')).toBeInTheDocument();
  });

  it('should not display "Last few rooms" label', () => {
    const { queryAllByText } = render(<HotelLastFewRooms {...props} label="" />);
    expect(queryAllByText(props.label)).toHaveLength(0);
  });
});
