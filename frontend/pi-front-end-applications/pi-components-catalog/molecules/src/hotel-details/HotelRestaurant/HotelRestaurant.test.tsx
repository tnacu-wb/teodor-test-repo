import '@testing-library/jest-dom';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import { HotelRestaurant } from './HotelRestaurant';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  formatInnerHTMLAssetUrls: () => mockMenus[0].description,
  useStaticHotelInformation: jest.fn(),
}));

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;

export const mockMenus = [
  {
    name: 'Breakfast',
    description:
      'Tuck into our unlimited breakfast from just £9.50*. And don\'t forget, kids eat free when an adult orders a full <a href="/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf" target="_blank">Premier Inn breakfast</a> - what\'s not to love! Our great value, all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a lighter option, with fruit, cereal and freshly baked pastries - and as always, everything in the continental breakfast is included in the Premier Inn Breakfast. We\'ll confirm which breakfast options will be available at your selected hotel during booking. *Prices may vary. At some restaurants our Premier Inn Breakfast is £10.50',
    imageSrc: 'content/dam/global/restaurants/Global/premier-inn-cooked-breakfast.jpg',
    menuSrc: 'content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
    menuLabel: 'Breakfast Menu',
    disclaimer:
      'Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.',
  },
  {
    name: 'Dinner',
    description:
      "Tuck into all your Premier Inn restaurant favourites like chicken tikka curry or a margherita pizza, and there's even a new mac and cheese for the little ones! Then enjoy a luxury sticky toffee pudding or get stuck into a delicious triple chocolate brownie for dessert.",
    imageSrc: 'content/dam/global/restaurants/THY/pi-main-grill.jpg',
    menuSrc: 'content/dam/global/restaurants/Global/thyme_spring_band2_unpriced.pdf',
    menuLabel: 'Dinner menu',
    disclaimer:
      'Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.',
  },
  {
    name: 'Meal Deal',
    description:
      "Fancy saving up to 20% with our tempting Meal Deal offer? Enjoy a delicious two course dinner plus a selected drink*, then wake up and tuck into our famous unlimited all-you-can-eat Premier Inn Breakfast the next day. Plus, up to two under-16s also eat breakfast for free when an adult orders a Me al Deal. We'll confirm at booking if our Meal Deal is available at your selected hotel.",
    imageSrc: 'content/dam/global/restaurants/Global/meal-deal.jpg',
    menuSrc: 'content/dam/global/restaurants/Global/thyme_spring_band2_unpriced.pdf',
    menuLabel: 'Dinner menu',
    disclaimer:
      'Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.',
  },
];

const mockData = {
  name: 'Thyme Bar & Grill',
  description:
    "Tuck into our unlimited breakfast from just £9.50*. And don't forget, kids eat free when an adult orders a full Premier Inn breakfast - what's not to love!",
  logoSrc: 'content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
  menus: mockMenus,
};

const defaultMockReturn = {
  restaurant: mockData,
  isLoading: false,
  isError: false,
  error: null,
};

describe('HotelRestaurant', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(defaultMockReturn);
  });

  it('renders HotelRestaurant with default props', () => {
    const { getByTestId, getByText } = render(<HotelRestaurant />);
    expect(getByText('restaurant.title')).toBeInTheDocument();
    expect(getByTestId('hotel-restaurant-logo')).toBeInTheDocument();
  });

  it('should render HotelRestaurant section', () => {
    const { getByTestId } = render(<HotelRestaurant />);
    expect(getByTestId('hotel-restaurant-section')).toBeInTheDocument();
  });

  it('should handle loading state', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultMockReturn,
      isLoading: true,
    });
    const { getByText } = render(<HotelRestaurant />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should handle error state', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultMockReturn,
      isError: true,
      error: new Error('Test error'),
    });
    const { getByText } = render(<HotelRestaurant />);
    expect(getByText('Test error')).toBeInTheDocument();
  });

  it('should render restaurant title', () => {
    const { getByText } = render(<HotelRestaurant />);
    expect(getByText('restaurant.title')).toBeInTheDocument();
  });

  it('should render restaurant image', () => {
    const { getByTestId } = render(<HotelRestaurant />);
    expect(getByTestId('hotel-restaurant-logo')).toBeInTheDocument();
  });

  it('should render restaurant disclaimer if present in the menu', () => {
    const { getByText } = render(<HotelRestaurant />);
    expect(
      getByText(
        'Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.'
      )
    ).toBeInTheDocument();
  });

  it('should not render restaurant disclaimer if not present in the menu', () => {
    const mockDataNoDisclaimer = {
      name: 'Thyme Bar & Grill',
      description:
        "Tuck into our unlimited breakfast from just £9.50*. And don't forget, kids eat free when an adult orders a full Premier Inn breakfast - what's not to love!",
      logoSrc: 'content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
      menus: [
        {
          name: 'Breakfast',
          description:
            "Tuck into our unlimited breakfast from just £9.50*. And don't forget, kids eat free when an adult orders a full Premier Inn breakfast - what's not to love! Our great value, all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a lighter option, with fruit, cereal and freshly baked pastries - and as always, everything in the continental breakfast is included in the Premier Inn Breakfast. We'll confirm which breakfast options will be available at your selected hotel during booking. *Prices may vary. At some restaurants our Premier Inn Breakfast is £10.50",
          imageSrc: 'content/dam/global/restaurants/Global/premier-inn-cooked-breakfast.jpg',
          menuSrc: 'content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
          menuLabel: 'Breakfast Menu',
          disclaimer: '',
        },
      ],
    };

    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultMockReturn,
      restaurant: mockDataNoDisclaimer,
    });
    const { queryByTestId } = render(<HotelRestaurant />);
    expect(queryByTestId('hdp_restaurants-AlertTitle')).toBeNull();
  });

  it('should return null when no menus', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultMockReturn,
      restaurant: { ...mockData, menus: [] },
    });
    const { container } = render(<HotelRestaurant />);
    expect(container.firstChild).toBeNull();
  });
});
