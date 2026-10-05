/* eslint-disable no-irregular-whitespace */
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HeroSection from './HeroSection.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () =>
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
}));

const mockHeroSectionData = {
  title: 'Hotels in Newcastle',
  description: `Our hotels in Newcastle are close to some of the city’s most famous landmarks, including the Millennium Bridge that stretches across the River Tyne,Newcastle racecourse, boutique shops in nearby Jesmond and Newcastle Airport. There’s great places to relax and unwind along the river on The Quayside or in refreshing green spaces like Leazes Park near the city centre, next door to the home of Newcastle FC, St. James’ Park. Our hotels in Newcastle are close to some of the city’s most famous landmarks, including the Millennium Bridge that stretches across the River Tyne,Newcastle racecourse, boutique shops in nearby Jesmond and Newcastle Airport. There’s great places to relax and unwind along the river on The Quayside or in refreshing green spaces like Leazes Park near the city centre, next door to the home of Newcastle FC, St. James’ Park.`,
  picture:
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
  breadcrumbs: [
    { url: '/gb/en/home.html', title: 'Home' },
    { url: '/gb/en/hotels.html', title: 'Locations' },
    { url: '', title: 'Newcastle' },
  ],
};

const mockShortHeroSectionData = {
  ...mockHeroSectionData,
  description: `Our hotels in Newcastle are close to some of the city’s most famous landmarks, including the Millennium Bridge`,
  picture: '',
};

describe('Hero Section', () => {
  it('should render the Hero Section together with image, title, description', () => {
    const { getByText } = render(<HeroSection data={mockHeroSectionData} />);
    expect(getByText(mockHeroSectionData.title)).toBeInTheDocument();
  });

  it('should not redirect to new page when clicking on breadcrumb link if url is empty', () => {
    const { getAllByText } = render(<HeroSection data={mockHeroSectionData} />);
    expect(getAllByText('Newcastle')[0].closest('a')).not.toHaveAttribute('href');
  });
});

describe('getHeroDescription', () => {
  it('should render the expandable text component if the description exceeds the allowed number of characters', () => {
    const { getByText } = render(<HeroSection data={mockHeroSectionData} />);
    expect(getByText('hoteldetails.readmore')).toBeInTheDocument();
  });

  it('should render the description only without the expandable component', () => {
    const { queryByText } = render(<HeroSection data={mockShortHeroSectionData} />);
    expect(queryByText('hoteldetails.readmore')).not.toBeInTheDocument();
  });
});
