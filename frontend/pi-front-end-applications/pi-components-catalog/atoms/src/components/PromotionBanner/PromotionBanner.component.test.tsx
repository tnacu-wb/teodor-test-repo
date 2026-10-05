import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import PromotionBanner, { Props } from './PromotionBanner.component';

const mockData: Props = {
  data: {
    restaurant: {
      bookingCardImage: '/content/dam/global/restaurants/booking-card.png',
      bookingCardBackgroundImage: '/content/dam/global/restaurants/BackgroundImageRestaurants.png',
    },
    bookRestaurantCta: {
      bookingCardCtaText: 'Book a table',
      bookingCardCtaLink: '/gb/en/restaurants/the-social/london-heathrow-airport-m4j4/book',
    },
  },
};

describe('PromotionBanner component', () => {
  beforeEach(() => {
    window.__satelliteLoaded = true;
    window._satellite = {
      track: jest.fn(),
    };
    jest.clearAllMocks();
  });
  it('should render promotion banner component correctly', () => {
    const { getByTestId } = render(<PromotionBanner {...mockData} />);
    const promoBannereImage = getByTestId('promotion-banner-image');
    expect(promoBannereImage).toBeInTheDocument();

    const promoBannereLink = getByTestId('promotion-banner-link');
    expect(promoBannereLink).toBeInTheDocument();
    expect(promoBannereLink).toHaveTextContent('Book a table');
  });

  it('should NOT render the text for button', () => {
    const { getByTestId } = render(<PromotionBanner />);

    const promoBannereLink = getByTestId('promotion-banner-link');
    expect(promoBannereLink).toBeInTheDocument();
    expect(promoBannereLink).toHaveTextContent('');
  });

  it('should call _satellite.track with restaurantSelected', () => {
    const { getByTestId } = render(<PromotionBanner />);

    const promoBannereLink = getByTestId('promotion-banner-link');

    fireEvent.click(promoBannereLink);
    expect(window._satellite.track).toHaveBeenCalledWith('restaurantBannerClicked');
  });
});
