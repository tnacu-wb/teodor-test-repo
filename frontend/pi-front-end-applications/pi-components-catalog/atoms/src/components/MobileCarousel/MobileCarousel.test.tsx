import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import MobileCarousel from './MobileCarousel.component';

const mockChangeSlideFunction = jest.fn();

const MobileCarouselWithImages = ({ activeSlide }: { activeSlide: number }) => (
  <MobileCarousel
    activeSlide={activeSlide}
    setActiveSlide={mockChangeSlideFunction}
    thumbnails={[
      <img
        data-testid="firstThumbnail"
        src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg"
      />,
      <img
        data-testid="secondThumbnail"
        src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg"
      />,
      <img
        data-testid="thirdThumbnail"
        src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg"
      />,
      <img
        data-testid="fourthThumbnail"
        src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-4.jpg"
      />,
    ]}
  >
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg" />
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg" />
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg" />
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-4.jpg" />
  </MobileCarousel>
);

describe('MobileCarousel', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<MobileCarouselWithImages activeSlide={0} />);

    expect(getByTestId('mobileCarousel-1/4')).toBeInTheDocument();
  });

  it('should change to the slide associated with the thumbnail', async () => {
    const { getByTestId, rerender, findByTestId } = render(
      <MobileCarouselWithImages activeSlide={0} />
    );

    fireEvent.click(getByTestId('secondThumbnail'));

    rerender(<MobileCarouselWithImages activeSlide={1} />);

    expect(await findByTestId('mobileCarousel-2/4')).toBeInTheDocument();
  });
});
