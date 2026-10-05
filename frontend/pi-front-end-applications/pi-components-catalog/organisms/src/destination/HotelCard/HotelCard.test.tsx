import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelCard from './HotelCard.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockedData = {
  brand: 'PID',
  distanceFromReference: 12.2567,
  topSectionImages: [
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/Frankfurt Exterior-min.jpeg',
      thumbnailSrc:
        '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/Frankfurt Exterior-min.jpeg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI2-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI2-min.jpg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI4-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI4-min.jpg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI5-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI5-min.jpg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI3-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI3-min.jpg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI1-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/FRAMTI1-min.jpg',
    },
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/desktop/de/hotel details/FRAMTI7.jpg',
      thumbnailSrc: '/content/dam/pi/websites/desktop/de/hotel details/FRAMTI7.jpg',
    },
  ],
  hotelFacilities: [
    {
      code: 'CPP',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/CPP.svg',
      isVisible: true,
      name: 'Chargeable onsite parking',
      weight: 0,
    },
    {
      code: 'ACO',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      isVisible: true,
      name: 'Air conditioning',
      weight: 10,
    },
    {
      code: 'DIN',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIN.svg',
      isVisible: true,
      name: 'Restaurant',
      weight: 20,
    },
    {
      code: 'WIA',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/WIA.svg',
      isVisible: true,
      name: 'Free Wi-Fi',
      weight: 40,
    },
    {
      code: 'DIS',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
      isVisible: true,
      name: 'Accessible',
      weight: 50,
    },
    {
      code: 'LFT',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
      isVisible: true,
      name: 'Lift',
      weight: 90,
    },
    {
      code: 'LUG',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LUG.svg',
      isVisible: true,
      name: 'Luggage facilities',
      weight: 100,
    },
    {
      code: 'EVC',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/EVC.svg',
      isVisible: true,
      name: 'Electronic Vehicle Charging Points',
      weight: 120,
    },
    {
      code: 'ICR',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ICR.svg',
      isVisible: true,
      name: 'Interconnecting rooms',
      weight: 170,
    },
    {
      code: 'PRR',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
      isVisible: true,
      name: 'Premier Plus rooms',
      weight: 70,
    },
  ],
  links: {
    detailsPage: '/germany/hesse/frankfurt/frankfurt-messe',
  },
  name: 'Frankfurt Messe',
  tripAdvisorReviews: {
    reviews: 100,
    rating: 4.3,
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (img: string) => `https://secure2.premierinn.com/${img}`,
}));

const mockedPathForURL = 'hotels/england/greater-london/london';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      asPath: mockedPathForURL,
      query: {
        reservationId: '',
      },
    };
  },
}));

const defaultProps = {
  data: mockedData,
  hideHotelDistance: false,
};

describe('DLP - HotelCard', () => {
  it('should render the hotelCard', () => {
    const { getByTestId } = render(<HotelCard {...defaultProps} />);
    expect(getByTestId('DLP-hotel-card')).toBeInTheDocument();
  });

  it('should render the hotelCard button', async () => {
    const { getByTestId } = render(<HotelCard {...defaultProps} />);
    const hotelCardBtn = getByTestId('DLP-hotel-button');
    expect(hotelCardBtn).toBeInTheDocument();
  });

  it('should render the New hotel badge if the hotel has a messagingFlag with this text', async () => {
    const { getByText } = render(
      <HotelCard
        {...{
          ...defaultProps,
          data: {
            ...mockedData,
            messagingFlag: {
              color: '',
              description: '',
              text: 'New hotel',
            },
          },
        }}
      />
    );
    const hotelCardBtn = getByText('New hotel');
    expect(hotelCardBtn).toBeInTheDocument();
  });

  it('should render the Premier Plus badge if the hotel has a hotelFacility with this name', async () => {
    const { getByText } = render(<HotelCard {...defaultProps} />);
    const hotelCardBtn = getByText('hoteldetails.rates.premierplus');
    expect(hotelCardBtn).toBeInTheDocument();
  });

  it('should render empty string if some of the fields are missing from data object', async () => {
    const { getAllByText } = render(<HotelCard {...{ ...defaultProps, data: {} }} />);

    expect(getAllByText('')[0]).toBeInTheDocument();
  });

  it('should render default image if no hotel image provided from data', () => {
    const { getAllByRole } = render(
      <HotelCard
        {...{
          ...defaultProps,
          data: {
            ...mockedData,
            hotelFacilities: null,
            topSectionImages: [
              {
                imageSrc: null,
              },
            ],
          },
        }}
      />
    );

    const defaultImageSrc =
      '/_next/image?url=https%3A%2F%2Fsecure2.premierinn.com%2Fdlp.hotelCard.img.placeholder&w=3840&q=75';

    expect(getAllByRole('img')[0]).toHaveAttribute('src', defaultImageSrc);
  });

  it('should not render the hotel distance if hideHotelDistance is true', async () => {
    const { queryByTestId } = render(<HotelCard {...defaultProps} hideHotelDistance />);
    const hotelDistanceLabel = queryByTestId('DLP-hotel-distance');
    expect(hotelDistanceLabel).not.toBeInTheDocument();
  });
});
