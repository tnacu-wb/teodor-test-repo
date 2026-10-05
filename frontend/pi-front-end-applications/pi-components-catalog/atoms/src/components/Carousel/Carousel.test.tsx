import '@testing-library/jest-dom';

import { act, fireEvent, render, waitFor } from '../../utils/test-utils';
import Carousel from './Carousel.component';

const mockChangeSlideFunction = jest.fn((index) => index);
const mockCarouselLabels: Record<string, string> = {
  'common.carousel.next': 'Nächstes Bild',
  'common.carousel.previous': 'Vorheriges Bild',
};

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => mockCarouselLabels[key] ?? key,
  }),
}));

const CarouselWithImages = ({ activeSlide }: { activeSlide: number }) => (
  <Carousel activeSlide={activeSlide} setActiveSlide={mockChangeSlideFunction}>
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg" />
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg" />
    <img src="https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg" />
  </Carousel>
);

describe('Carousel', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<CarouselWithImages activeSlide={0} />);

    expect(getByTestId('carousel-1/3')).toBeInTheDocument();
  });

  it('should change to the next slide', async () => {
    const { rerender, getByRole, getByTestId } = render(<CarouselWithImages activeSlide={0} />);

    expect(getByRole('button', { name: 'Nächstes Bild' })).toHaveAttribute('type', 'button');

    await act(async () => {
      fireEvent.click(getByRole('button', { name: 'Nächstes Bild' }));
    });

    await waitFor(() => {
      expect(mockChangeSlideFunction).toBeCalledWith(1);
    });

    rerender(<CarouselWithImages activeSlide={1} />);

    expect(getByTestId('carousel-2/3')).toBeInTheDocument();
  });

  it('should change to the previous slide', async () => {
    const { getByRole, getByTestId, findByRole, rerender } = render(
      <CarouselWithImages activeSlide={0} />
    );

    await act(async () => {
      fireEvent.click(getByRole('button', { name: 'Nächstes Bild' }));
    });

    await waitFor(() => {
      expect(mockChangeSlideFunction).toBeCalledWith(1);
    });

    rerender(<CarouselWithImages activeSlide={1} />);

    expect(getByTestId('carousel-2/3')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(await findByRole('button', { name: 'Vorheriges Bild' }));
    });

    await waitFor(() => {
      expect(mockChangeSlideFunction).toBeCalledWith(1);
    });

    rerender(<CarouselWithImages activeSlide={0} />);

    expect(getByTestId('carousel-1/3')).toBeInTheDocument();
  });

  it('should not show the Previous button when viewing the first slide', () => {
    const { queryByRole } = render(<CarouselWithImages activeSlide={0} />);
    expect(queryByRole('button', { name: 'Vorheriges Bild' })).not.toBeInTheDocument();
  });

  it('should not show the Next button when viewing the last slide', async () => {
    const { queryByRole } = render(<CarouselWithImages activeSlide={2} />);

    expect(queryByRole('button', { name: 'Nächstes Bild' })).not.toBeInTheDocument();
  });
});
