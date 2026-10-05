import '@testing-library/jest-dom';
import getConfig from 'next/config';

import { fireEvent, render } from '../../utils/test-utils';
import { mockMenus } from '../HotelRestaurant/HotelRestaurant.test';
import HotelRestaurantMenuContent from './HotelRestaurantMenuContent.component';
import HotelRestaurantMenuDescription from './HotelRestaurantMenuDescription.component';

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  formatInnerHTMLAssetUrls: () => mockMenus[0].description,
}));

describe('HotelRestaurantMenuContent', () => {
  describe('HotelRestaurantMenuContent component', () => {
    it('should render HotelRestaurantMenuContent', () => {
      const { getByAltText, getByText } = render(
        <HotelRestaurantMenuContent menu={mockMenus[0]} />
      );
      expect(getByAltText('Breakfast')).toBeInTheDocument();
      expect(getByText('Breakfast Menu')).toBeInTheDocument();
    });

    it('should not display menu button', () => {
      const mockMenu = {
        ...mockMenus[0],
        menuSrc: '',
      };
      const { queryByText } = render(<HotelRestaurantMenuContent menu={mockMenu} />);
      expect(queryByText('Breakfast Menu')).toBeNull();
    });

    it('should open menu URL', () => {
      (getConfig as jest.Mock).mockImplementation(() => ({
        publicRuntimeConfig: {
          NEXT_IMAGE_UNOPTIMIZED: 'true',
        },
      }));
      const { getByText } = render(<HotelRestaurantMenuContent menu={mockMenus[0]} />);
      const windowOpenSpy = jest.spyOn(window, 'open');
      windowOpenSpy.mockImplementation(jest.fn());
      fireEvent.click(getByText('Breakfast Menu'));
      expect(windowOpenSpy).toHaveBeenCalled();
      windowOpenSpy.mockRestore();
    });
  });

  describe('HotelRestaurantMenuDescription component', () => {
    it('should display collapsible', () => {
      const { getByTestId } = render(
        <HotelRestaurantMenuDescription menuDescription={mockMenus[0].description} />
      );
      expect(getByTestId('menu-description-collapse')).toBeInTheDocument();
    });
  });
});
