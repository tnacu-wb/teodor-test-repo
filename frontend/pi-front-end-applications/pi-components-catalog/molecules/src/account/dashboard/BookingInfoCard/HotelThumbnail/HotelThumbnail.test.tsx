import '@testing-library/jest-dom';
import getConfig from 'next/config';

import { render } from '../../../../utils/test-utils';
import type { Props } from './HotelThumbnail.component';
import HotelThumbnailComponent from './HotelThumbnail.component';

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    query: {
      bookingReference: 'AQPR1437',
    },
  }),
}));

const props = {
  alt: 'Exterior at Premier Inn Manchester Old Trafford hotel showing car park PI',
  brand: 'pi',
  url: '1.jpg',
  image: 'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/M/MANOLD/MANOLD',
} as Props;

describe('HotelThumbnailComponent', () => {
  it('it should render the HotelThumbnailComponent component with brand pi', () => {
    const { getByText, getByTestId } = render(<HotelThumbnailComponent {...props} />);
    expect(getByText('dashboard.bookings.hotelDetails')).toBeInTheDocument();
    expect(getByTestId('hotel-thumbnail')).toBeInTheDocument();
    expect(getByTestId('hotel-line')).toBeInTheDocument();
    expect(getByTestId('hotel-line')).toHaveStyle(
      'background-color:btnSecondaryEnabled; color:baseWhite;'
    );
  });

  it('it should render the HotelThumbnailComponent component with brand HUB', () => {
    props.brand = 'HUB';
    const { getByTestId } = render(<HotelThumbnailComponent {...props} />);

    expect(getByTestId('hotel-line')).toHaveStyle('background-color:darkGrey2; color:hubPrimary;');
  });

  it('it should render the HotelThumbnailComponent component with brand ZIP', () => {
    props.brand = 'ZIP';
    const { getByTestId } = render(<HotelThumbnailComponent {...props} />);

    expect(getByTestId('hotel-line')).toHaveStyle('background-color:zipPrimary; color:baseWhite;');
  });

  it('it should render the HotelThumbnailComponent component with brand HPI', () => {
    props.brand = 'HPI';
    const { getByTestId } = render(<HotelThumbnailComponent {...props} />);

    expect(getByTestId('hotel-line')).toHaveStyle('background-color:hubPrimary; color:darkGrey2;');
  });

  it('it should render the HotelThumbnailComponent component with brand ZPI', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
    props.brand = 'ZPI';
    const { getByTestId } = render(<HotelThumbnailComponent {...props} />);

    expect(getByTestId('hotel-line')).toHaveStyle('background-color:zipPrimary;');
  });
});
