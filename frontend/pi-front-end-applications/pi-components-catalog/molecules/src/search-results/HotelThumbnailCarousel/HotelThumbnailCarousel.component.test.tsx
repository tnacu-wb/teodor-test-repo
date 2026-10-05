import { fireEvent, render, screen } from '@testing-library/react';
import * as React from 'react';

import HotelThumbnailCarousel, { getDotIndex } from './HotelThumbnailCarousel.component';

const mockThumbnailImages = [
  {
    imageSrc: 'hotel1.jpg',
    tags: ['exterior'],
  },
  {
    imageSrc: 'hotel2.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel3.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel4.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel5.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel6.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel7.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel8.jpg',
    tags: ['interior'],
  },
  {
    imageSrc: 'hotel9.jpg',
    tags: ['interior'],
  },
];

const mockBrandLogos = {
  hubLogo: 'hub-logo.png',
  zipLogo: 'zip-logo.png',
};

const mockUseMediaQuery = jest.fn();

function mockTranslate(key: string, options?: Record<string, number | string>) {
  switch (key) {
    case 'common.carousel.next':
      return 'Nächstes Bild';
    case 'common.carousel.previous':
      return 'Vorheriges Bild';
    case 'searchresults.list.hotel.carousel.label':
      return `${options?.hotelName} Bildergalerie`;
    case 'searchresults.list.hotel.carousel.showImage':
      return `Bild ${options?.imageNumber} von ${options?.totalImages} anzeigen`;
    default:
      return key;
  }
}

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: () => mockUseMediaQuery(),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: mockTranslate,
  }),
  withTranslation: () => (Component: React.ComponentType) => Component,
}));

jest.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: mockTranslate,
  }),
  withTranslation: () => (Component: React.ComponentType) => Component,
}));

jest.mock(
  'next/image',
  () =>
    function Image({ src, alt }: { src: string; alt: string }) {
      return <img src={src} alt={alt} />;
    }
);

describe('HotelThumbnailCarousel', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseMediaQuery.mockReturnValue([true]);
  });

  it('renders without crashing', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="hub"
        brandLogos={mockBrandLogos}
        testId="hotel-carousel"
        name="Hotel Name"
      />
    );
    expect(screen.getByTestId('hotel-carousel')).toBeInTheDocument();
  });

  it('sets correct alt text for images', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="hub"
        brandLogos={mockBrandLogos}
        testId="hotel-carousel"
        name="Hotel Name"
      />
    );
    const activeSlides = document.querySelectorAll('.slick-slide.slick-active.slick-current');
    const matchingImages = Array.from(activeSlides).filter((slide) =>
      slide.querySelector('img[alt="Hotel Name exterior"]')
    );
    expect(matchingImages).toHaveLength(1);
  });

  it('should use fallback image values when thumbnail image data is incomplete', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={[{ tags: [] }]}
        brand="hub"
        brandLogos={mockBrandLogos}
        testId="hotel-carousel"
        name="Hotel Name"
      />
    );

    expect(screen.getByAltText('Hotel Name exterior')).toBeInTheDocument();
  });

  it('should render without interactive controls when thumbnail images are missing', () => {
    render(
      <HotelThumbnailCarousel
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    expect(screen.getByTestId('SRP-hotel-thumbnail-carousel')).not.toHaveAttribute('role');
    expect(screen.queryByTestId('SRP-hotel-thumbnail-carousel-dots')).not.toBeInTheDocument();
  });

  it('renders HotelBrandLogo with correct props', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="hotel-carousel"
        name="Hotel Name"
      />
    );
    // HotelBrandLogo renders an image with alt containing brand
    expect(screen.getByTestId('srp_hotel-brand-logo')).toBeInTheDocument();
  });

  it('renders custom dots as an overlay on carousel', () => {
    const { getByTestId } = render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    expect(getByTestId('SRP-hotel-thumbnail-carousel-dots')).toBeInTheDocument();
  });

  it('should make the carousel focusable when there are multiple images', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    expect(screen.getByRole('region', { name: 'Hotel Name Bildergalerie' })).toHaveAttribute(
      'tabindex',
      '0'
    );
  });

  it('should trim the carousel label when hotel name is not provided', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
      />
    );

    expect(screen.getByRole('region', { name: 'Bildergalerie' })).toBeInTheDocument();
  });

  it('should render hidden spacer dots when fewer than five dot targets exist', () => {
    mockUseMediaQuery.mockReturnValue([false]);

    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages.slice(0, 2)}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    expect(screen.getByTestId('SRP-hotel-thumbnail-carousel-dots-1')).toHaveAttribute(
      'aria-hidden',
      'true'
    );
    expect(screen.getByTestId('SRP-hotel-thumbnail-carousel-dots-2')).toHaveAttribute(
      'aria-hidden',
      'true'
    );
    expect(screen.getByTestId('SRP-hotel-thumbnail-carousel-dots-3')).toHaveAttribute(
      'aria-hidden',
      'true'
    );
    expect(screen.getAllByRole('button')).toHaveLength(2);
  });

  it('should show and hide carousel arrows when hovered on desktop', () => {
    mockUseMediaQuery.mockReturnValue([false]);

    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    const carousel = screen.getByRole('region', { name: 'Hotel Name Bildergalerie' });
    expect(screen.queryByRole('button', { name: 'Nächstes Bild' })).not.toBeInTheDocument();

    fireEvent.mouseEnter(carousel);
    expect(screen.getByRole('button', { name: 'Nächstes Bild' })).toBeInTheDocument();

    fireEvent.mouseLeave(carousel);
    expect(screen.queryByRole('button', { name: 'Nächstes Bild' })).not.toBeInTheDocument();
  });

  it('should show carousel arrows when focused on desktop', () => {
    mockUseMediaQuery.mockReturnValue([false]);

    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    const carousel = screen.getByRole('region', { name: 'Hotel Name Bildergalerie' });
    expect(screen.queryByRole('button', { name: 'Nächstes Bild' })).not.toBeInTheDocument();

    fireEvent.focus(carousel);

    expect(screen.getByRole('button', { name: 'Nächstes Bild' })).toBeInTheDocument();
  });

  it('should keep arrows visible when focus moves inside the carousel', () => {
    mockUseMediaQuery.mockReturnValue([false]);

    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    const carousel = screen.getByRole('region', { name: 'Hotel Name Bildergalerie' });
    fireEvent.focus(carousel);

    fireEvent.blur(carousel, {
      relatedTarget: screen.getByRole('button', { name: 'Bild 1 von 9 anzeigen' }),
    });

    expect(screen.getByRole('button', { name: 'Nächstes Bild' })).toBeInTheDocument();
  });

  it('should hide arrows when focus leaves the carousel', () => {
    mockUseMediaQuery.mockReturnValue([false]);

    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    const carousel = screen.getByRole('region', { name: 'Hotel Name Bildergalerie' });
    fireEvent.focus(carousel);
    expect(screen.getByRole('button', { name: 'Nächstes Bild' })).toBeInTheDocument();

    fireEvent.blur(carousel);

    expect(screen.queryByRole('button', { name: 'Nächstes Bild' })).not.toBeInTheDocument();
  });

  it('should move carousel images when arrow keys are pressed', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    fireEvent.keyDown(screen.getByRole('region', { name: 'Hotel Name Bildergalerie' }), {
      key: 'ArrowRight',
    });

    expect(screen.getByTestId('carousel-2/9')).toBeInTheDocument();
  });

  it('should move carousel images backwards when the left arrow key is pressed', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    const carousel = screen.getByRole('region', { name: 'Hotel Name Bildergalerie' });
    fireEvent.keyDown(carousel, { key: 'ArrowRight' });
    fireEvent.keyDown(carousel, { key: 'ArrowLeft' });

    expect(screen.getByTestId('carousel-1/9')).toBeInTheDocument();
  });

  it('should ignore arrow keys when the carousel has one image', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages.slice(0, 1)}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    fireEvent.keyDown(screen.getByTestId('SRP-hotel-thumbnail-carousel'), { key: 'ArrowRight' });

    expect(screen.getByTestId('carousel-1/1')).toBeInTheDocument();
  });

  it('should make carousel dots keyboard focusable image selectors', () => {
    render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    fireEvent.click(screen.getByRole('button', { name: 'Bild 2 von 9 anzeigen' }));

    expect(screen.getByTestId('carousel-2/9')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Bild 2 von 9 anzeigen' })).toHaveAttribute(
      'aria-current',
      'true'
    );
  });

  it('should not render custom dots as an overlay on carousel when there is only one image', () => {
    const { queryByTestId } = render(
      <HotelThumbnailCarousel
        thumbnailImages={mockThumbnailImages.slice(0, 1)}
        brand="zip"
        brandLogos={mockBrandLogos}
        testId="SRP-hotel-thumbnail-carousel"
        name="Hotel Name"
      />
    );

    expect(queryByTestId('SRP-hotel-thumbnail-carousel-dots')).not.toBeInTheDocument();
  });
});

describe('getDotIndex', () => {
  it('returns correct index for 2 slides', () => {
    expect(getDotIndex(0, 2)).toBe(0);
    expect(getDotIndex(1, 2)).toBe(4);
  });

  it('returns correct index for 3 slides', () => {
    expect(getDotIndex(0, 3)).toBe(0);
    expect(getDotIndex(1, 3)).toBe(2);
    expect(getDotIndex(2, 3)).toBe(4);
  });

  it('returns correct index for 4 slides', () => {
    expect(getDotIndex(0, 4)).toBe(0);
    expect(getDotIndex(1, 4)).toBe(2);
    expect(getDotIndex(2, 4)).toBe(2);
    expect(getDotIndex(3, 4)).toBe(4);
  });

  it('returns correct index for 5 slides', () => {
    expect(getDotIndex(0, 5)).toBe(0);
    expect(getDotIndex(1, 5)).toBe(1);
    expect(getDotIndex(2, 5)).toBe(2);
    expect(getDotIndex(3, 5)).toBe(3);
    expect(getDotIndex(4, 5)).toBe(4);
  });

  it('returns correct index for more than 5 slides', () => {
    // 6 slides
    expect(getDotIndex(0, 6)).toBe(0);
    expect(getDotIndex(1, 6)).toBe(1);
    expect(getDotIndex(2, 6)).toBe(2);
    expect(getDotIndex(3, 6)).toBe(2);
    expect(getDotIndex(4, 6)).toBe(3);
    expect(getDotIndex(5, 6)).toBe(4);
    // 10 slides
    expect(getDotIndex(0, 10)).toBe(0);
    expect(getDotIndex(1, 10)).toBe(1);
    expect(getDotIndex(2, 10)).toBe(2);
    expect(getDotIndex(7, 10)).toBe(2);
    expect(getDotIndex(8, 10)).toBe(3);
    expect(getDotIndex(9, 10)).toBe(4);
  });
});
