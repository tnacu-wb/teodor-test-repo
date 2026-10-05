import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelNameInput from './HotelNameInput.component';

const props = {
  hotelName: 'London Euston',
  onInputChange: jest.fn(),
};

describe('HotelNameInput', () => {
  it('should render the component', () => {
    const { getByPlaceholderText } = render(<HotelNameInput {...props} />);
    expect(getByPlaceholderText('London Euston')).toBeInTheDocument();
  });
});
