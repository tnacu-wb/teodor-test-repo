import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import type { HotelDetailsProps } from './BartHotelDetails.component';
import HotelDetails from './BartHotelDetails.component';

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  t: (value: string) => value,
  data: {
    hotelInformation: {
      brand: 'pi',
      address: 'Address',
      galleryImages: [
        {
          thumbnailSrc:
            'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
          alt: 'Hotel image',
        },
        {
          thumbnailSrc:
            'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
          alt: 'Hotel image',
        },
      ],
      links: {
        detailsPage: '/england/greater-london/london/hub-london-kings-cross',
      },
      parkingDescription:
        'Chargeable on-site parking is available operating on a first come, first served basis at £8 per 24 hours on non-event days and £20 on event days. Parking is managed by Horizon.',
    },
  },
} as HotelDetailsProps;

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQuery: () => mockResponse,
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'gb',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
}));

describe('HotelDetails', () => {
  it('should contain HotelThumbnail, HotelParkingInfo, HotelLocationInfo', () => {
    const { getByTestId, getByText } = render(<HotelDetails {...mockResponse} />);
    expect(getByTestId('hotel-thumbnail')).toBeInTheDocument();
    expect(getByText(mockResponse.data.hotelInformation.parkingDescription)).toBeInTheDocument();
  });

  it('should display an error state', () => {
    mockResponse.isError = true;
    mockResponse.error.message = 'Error message';
    const { getByText } = render(<HotelDetails {...mockResponse} />);
    expect(getByText('Error message')).toBeInTheDocument();
  });

  it('should display a loading state', () => {
    mockResponse.isLoading = true;
    mockResponse.isError = false;
    mockResponse.error.message = '';
    const { getByTestId } = render(<HotelDetails {...mockResponse} />);
    expect(getByTestId('loading-message')).toBeInTheDocument();
  });
});
