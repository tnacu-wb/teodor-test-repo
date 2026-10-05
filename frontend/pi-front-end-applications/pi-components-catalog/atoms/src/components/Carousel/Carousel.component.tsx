import { Box, BoxProps, ResponsiveValue } from '@chakra-ui/react';
import { useTranslation } from 'next-i18next';
import {
  Dispatch,
  ForwardedRef,
  forwardRef,
  MouseEvent,
  MouseEventHandler,
  MutableRefObject,
  ReactElement,
  ReactNode,
  SetStateAction,
  useEffect,
  useRef,
} from 'react';
import Slider from 'react-slick';

import { ChevronLeft, ChevronRight } from '../../assets/icons';
import CarouselGlobalStyles from '../../theme/components/CarouselGlobalStyles';
import Icon from '../Icon/Icon.component';

export interface Props extends BoxProps {
  children?: ReactNode;
  activeSlide: number;
  setActiveSlide: Dispatch<SetStateAction<number>>;
  additionalNavButtonStyles?: {
    boxStyles?: BoxProps;
    leftIconStyle?: BoxProps;
    rightIconStyle?: BoxProps;
    leftArrowStyles?: BoxProps;
    rightArrowStyles?: BoxProps;
  };
  focusOnLoad?: boolean;
  infinite?: boolean;
  arrows?: boolean;
}

export default function Carousel({
  children,
  activeSlide,
  setActiveSlide,
  additionalNavButtonStyles,
  focusOnLoad = true,
  infinite = true,
  arrows = true,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const slidesCount = (children as ReactElement[])?.length;

  const prevRef = useRef<HTMLButtonElement>(null);
  const nextRef = useRef<HTMLButtonElement>(null);
  const sliderRef = useRef<Slider | null>(null);

  useEffect(() => {
    // This condition is added to ensure focus is set only when needed. Without this change, it was causing unexpected focus behavior in infinite scroll on SRP
    if (focusOnLoad) {
      if (activeSlide >= slidesCount - 1) {
        prevRef.current?.focus();
        focusSlider(sliderRef);
      }
      if (activeSlide <= 0) {
        nextRef.current?.focus();
        focusSlider(sliderRef);
      }
    }
  }, [activeSlide, focusOnLoad, slidesCount]);

  useEffect(() => {
    focusOnLoad && focusSlider(sliderRef);
  }, [focusOnLoad]);

  useEffect(() => {
    if (activeSlide >= 0 && activeSlide < slidesCount) {
      sliderRef.current?.slickGoTo(activeSlide);
    }
  }, [activeSlide, slidesCount]);

  return (
    <Box
      data-testid={`carousel-${activeSlide + 1}/${slidesCount}`}
      sx={{
        '& .slick-list div': {
          '&:focus, a': {
            outline: 'none',
          },
        },
      }}
    >
      <CarouselGlobalStyles />
      <Slider
        infinite={infinite}
        arrows={arrows}
        lazyLoad="ondemand"
        initialSlide={activeSlide}
        speed={300}
        afterChange={(index) => setActiveSlide(index)}
        ref={sliderRef}
        nextArrow={
          activeSlide < slidesCount - 1 ? (
            <ForwardedCustomNextArrow
              ref={nextRef}
              ariaLabel={t('common.carousel.next')}
              {...(additionalNavButtonStyles || {})}
            />
          ) : (
            <NoArrow />
          )
        }
        prevArrow={
          activeSlide > 0 ? (
            <ForwardedCustomPrevArrow
              ref={prevRef}
              ariaLabel={t('common.carousel.previous')}
              {...(additionalNavButtonStyles || {})}
            />
          ) : (
            <NoArrow />
          )
        }
      >
        {children}
      </Slider>
    </Box>
  );
}

const navButtonStyles = {
  borderRadius: 'full',
  border: '2px solid var(--chakra-colors-baseWhite)',
  h: '2.813rem',
  w: '2.813rem',
  opacity: '75%',
  background: 'darkGrey2',
  zIndex: 1,
  _hover: {
    opacity: 1,
  },
  position: 'absolute' as ResponsiveValue<'absolute'>,
  top: '40%',
  marginRight: '0.7rem',
  marginLeft: '0.7rem',
};

interface CustomArrowProps {
  ariaLabel: string;
  onClick?: MouseEventHandler<HTMLButtonElement>;
  boxStyles?: BoxProps;
  leftIconStyle?: BoxProps;
  rightIconStyle?: BoxProps;
  leftArrowStyles?: BoxProps;
  rightArrowStyles?: BoxProps;
}

function CustomNextArrow(
  { ariaLabel, onClick, boxStyles, rightIconStyle, rightArrowStyles }: CustomArrowProps,
  ref: ForwardedRef<HTMLButtonElement>
) {
  const handleClick = (event: MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    event.stopPropagation();
    onClick?.(event);
  };

  return (
    <Box
      ref={ref}
      as="button"
      type="button"
      onClick={handleClick}
      title={ariaLabel}
      aria-label={ariaLabel}
      _focusVisible={{ outline: '2px solid var(--chakra-colors-primary)', outlineOffset: '2px' }}
      right="xl"
      {...navButtonStyles}
      {...(boxStyles || {})}
      {...(rightArrowStyles || {})}
    >
      <Icon
        svg={<ChevronRight color="var(--chakra-colors-baseWhite)" />}
        transform="translateX(1.6rem) scale(1.5)"
        {...(rightIconStyle || {})}
      />
    </Box>
  );
}
const ForwardedCustomNextArrow = forwardRef(CustomNextArrow);

function CustomPrevArrow(
  { ariaLabel, onClick, boxStyles, leftIconStyle, leftArrowStyles }: CustomArrowProps,
  ref: ForwardedRef<HTMLButtonElement>
) {
  const handleClick = (event: MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    event.stopPropagation();
    onClick?.(event);
  };

  return (
    <Box
      ref={ref}
      as="button"
      type="button"
      onClick={handleClick}
      title={ariaLabel}
      aria-label={ariaLabel}
      _focusVisible={{ outline: '2px solid var(--chakra-colors-primary)', outlineOffset: '2px' }}
      left="xl"
      {...navButtonStyles}
      {...(boxStyles || {})}
      {...(leftArrowStyles || {})}
    >
      <Icon
        svg={<ChevronLeft color="var(--chakra-colors-baseWhite)" />}
        transform="translateX(1.4rem) scale(1.5)"
        {...(leftIconStyle || {})}
      />
    </Box>
  );
}

const ForwardedCustomPrevArrow = forwardRef(CustomPrevArrow);

function NoArrow() {
  return null;
}

// Function to focus on slider element: keyboard nagviataion for slider works only works when slider element is in focus
function focusSlider(sliderRef: MutableRefObject<Slider | null>) {
  if (sliderRef?.current?.innerSlider) {
    const track = sliderRef.current.innerSlider.list?.querySelector('.slick-track');
    const slide = track?.querySelector('.slick-slide');
    (slide as HTMLElement)?.focus();
  }
}
