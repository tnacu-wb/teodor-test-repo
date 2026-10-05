import '@testing-library/jest-dom';
import getConfig from 'next/config';

import { fireEvent, render } from '../../utils/test-utils';
import HotelRoomImages, { RoomImage } from './HotelRoomImages.component';

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

const mockData = {
  images: [
    {
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
      alt: 'Hotel Image Thumbnail 1',
      caption: 'Accessible Bedroom',
      iconSrc: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
    },
    {
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
      alt: 'Hotel Image Thumbnail 2',
      caption: 'This is another text',
    },
    {
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
      alt: 'Hotel Image Thumbnail 3',
      caption: '',
    },
  ],
  thumbnailHeight: {
    xl: '22.563rem',
    lg: '22rem',
    md: '24rem',
    sm: '16.563rem',
    mobile: '10.625rem',
    base: '10.625rem',
  },
};

describe('HotelRoomImages component', () => {
  it('should render the component', async () => {
    const { getByTestId } = render(
      <HotelRoomImages
        images={mockData.images as RoomImage[]}
        thumbnailHeight={mockData.thumbnailHeight}
        isLessThanSm={false}
        isLessThanLg={false}
      />
    );
    expect(getByTestId('room-images')).toBeInTheDocument();
  });

  it('should render the mobile component', async () => {
    const { getByTestId } = render(
      <HotelRoomImages
        images={mockData.images as RoomImage[]}
        thumbnailHeight={mockData.thumbnailHeight}
        isLessThanSm={true}
        isLessThanLg={true}
      />
    );

    expect(getByTestId('room-images__expand')).toBeInTheDocument();
  });

  it('should open the modal when clicking the "View gallery" button', async () => {
    const { getByRole } = render(
      <HotelRoomImages
        images={mockData.images as RoomImage[]}
        thumbnailHeight={mockData.thumbnailHeight}
        isLessThanSm={false}
        isLessThanLg={false}
      />
    );
    fireEvent.click(getByRole('button'));
    expect(getByRole('dialog')).toBeInTheDocument();
    fireEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should open the modal when clicking the Expand image', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
    const { getByTestId } = render(
      <HotelRoomImages
        images={mockData.images as RoomImage[]}
        thumbnailHeight={mockData.thumbnailHeight}
        isLessThanSm={true}
        isLessThanLg={true}
      />
    );

    fireEvent.click(getByTestId('room-images__expand'));
  });
});
