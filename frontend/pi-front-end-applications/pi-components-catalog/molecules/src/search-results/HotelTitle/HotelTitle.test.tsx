import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelTitle from './HotelTitle.component';

describe('SRP - HotelTitle', () => {
  it('should display the hotel name', () => {
    const { getByText } = render(
      <HotelTitle title="hub London Tower Bridge" testId="SRP-hotel-title" />
    );
    expect(getByText('hub London Tower Bridge')).toBeInTheDocument();
  });

  it('should render the tooltip if the text exceeds 39 characters', () => {
    const { getByText } = render(
      <HotelTitle
        title="Milton Keynes Central South West (Furzton Lake)"
        testId="DLP-hotel-title"
      />
    );
    expect(getByText('Milton Keynes Central South West (Furzt...')).toBeInTheDocument();
  });
});
