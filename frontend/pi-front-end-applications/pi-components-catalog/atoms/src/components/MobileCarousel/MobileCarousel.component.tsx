import { Box, BoxProps } from '@chakra-ui/react';
import {
  cloneElement,
  Dispatch,
  JSX,
  ReactElement,
  ReactNode,
  SetStateAction,
  useEffect,
  useState,
} from 'react';
import Slider from 'react-slick';

import CarouselGlobalStyles from '../../theme/components/CarouselGlobalStyles';

export interface Props extends BoxProps {
  children?: ReactNode;
  thumbnails?: readonly JSX.Element[];
  activeSlide: number;
  setActiveSlide: Dispatch<SetStateAction<number>>;
}

export default function MobileCarousel({
  children,
  thumbnails,
  activeSlide,
  setActiveSlide,
}: Readonly<Props>) {
  const [nav1, setNav1] = useState<Slider>();
  const [nav2, setNav2] = useState<Slider>();

  useEffect(() => {
    focusSlider(nav1);
  }, [nav1]);

  return (
    <Box
      data-testid={`mobileCarousel-${activeSlide + 1}/${(children as ReactElement[])?.length}`}
      sx={{
        '& .carousel-thumbnails': {
          marginTop: 'md',
          cursor: 'pointer',
          '.slick-slide': { width: 'calc(100% - 1rem)', padding: '0 0.5rem' },
          '.slick-current > div > *': {
            border: '2px solid var(--chakra-colors-darkGrey1)',
            padding: '2px',
          },
        },
      }}
    >
      <CarouselGlobalStyles />
      <Slider
        asNavFor={nav2}
        ref={(slider: Slider) => setNav1(slider)}
        initialSlide={activeSlide}
        infinite={false}
        arrows={false}
        speed={300}
        afterChange={(index) => setActiveSlide(index)}
      >
        {children}
      </Slider>
      <Slider
        className="carousel-thumbnails"
        asNavFor={nav1}
        ref={(slider: Slider) => setNav2(slider)}
        initialSlide={activeSlide}
        speed={300}
        infinite={false}
        arrows={false}
        slidesToShow={3}
        focusOnSelect={true}
        swipeToSlide={true}
      >
        {thumbnails?.map((thumbnail: ReactElement, index) =>
          cloneElement(thumbnail, { key: index }, null)
        )}
      </Slider>
    </Box>
  );
}

// Function to focus on slider element: keyboard nagviataion for slider works only works when slider element is in focus
function focusSlider(sliderRef: Slider | undefined) {
  if (sliderRef?.innerSlider) {
    const track = sliderRef.innerSlider.list?.querySelector('.slick-track');
    const slide = track?.querySelector('.slick-slide');
    (slide as HTMLElement)?.focus();
  }
}
