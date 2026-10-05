import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import WhyUs from './WhyUs.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () =>
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
}));

const mockedData = {
  title: 'Why Premier Inn?',
  description:
    'Is it our bed of dreams, our lip smackingly tasty food, our great value or our amazing team members that guests love so much? We reckon it’s a bit of everything.',
  picture:
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
  whyItems: [
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: 'Happy guests',
      itemDescription: 'Our hotels are rated consistently highly by those who stay with us.',
    },
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: 'Here to serve',
      itemDescription: 'Our famous unlimited breakfast and evening meals at every hotel.',
    },
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: 'Good night guarantee',
      itemDescription: 'A great night’s sleep, or your money back!',
    },
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: 'Zimmerreinigung auf Wunsch',
      itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
    },
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: 'Lorem ipsum dolor',
      itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
    },
    {
      itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
      itemTitle: null,
      itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
    },
  ],
};

describe('Why Us Section', () => {
  it('should render the Why Us Section together with image, title, description', () => {
    const { getByText } = render(<WhyUs data={mockedData} />);
    expect(getByText(mockedData.title)).toBeInTheDocument();
  });

  it('should render the description and image', () => {
    const { getByTestId } = render(<WhyUs data={mockedData} />);
    expect(getByTestId('WhyUs-Description')).toBeInTheDocument();
    expect(getByTestId('WhyUs-Image')).toBeInTheDocument();
  });

  it('should render the items', () => {
    const { getAllByTestId } = render(<WhyUs data={mockedData} />);
    expect(getAllByTestId('WhyUs-Item').length).toBe(6);
  });
});
