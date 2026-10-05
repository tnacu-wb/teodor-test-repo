import { Box, BoxProps, useMediaQuery } from '@chakra-ui/react';
import type { SrpBrand, ThumbnailImage } from '@whitbread-eos/api';
import { Carousel } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { KeyboardEvent, useMemo, useState } from 'react';

import HotelBrandLogo from '../HotelBrandLogo/HotelBrandLogo.component';

interface Props {
  thumbnailImages?: ThumbnailImage[];
  brand: string;
  brandLogos: Pick<SrpBrand, 'hubLogo' | 'zipLogo'>;
  testId: string;
  styles?: BoxProps;
  name?: string;
}

export default function HotelThumbnailCarousel({
  thumbnailImages,
  brand,
  brandLogos,
  testId,
  styles,
  name,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const [activeSlide, setActiveSlide] = useState(0);
  const carouselImages = useMemo(() => thumbnailImages?.slice(0, 15) ?? [], [thumbnailImages]);
  const totalCarouselImages = carouselImages.length;
  const isCarouselInteractive = totalCarouselImages > 1;
  // Only desktop and larger breakpoints show the arrows on hover/focus.

  const [isMobile] = useMediaQuery('(max-width: 1279px)');

  const [isHovered, setIsHovered] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  // Note: This logic is tailored for 5 dots displayed and specific UI requirements.
  // If the dot display logic changes, this function will need to be updated.
  function renderCustomDots() {
    const showSmallFirst = totalCarouselImages > 5 && activeSlide > 2;
    const showSmallLast = totalCarouselImages > 5 && activeSlide < totalCarouselImages - 3;
    const selectedDot = getDotIndex(activeSlide, totalCarouselImages);

    return (
      <Box {...customDotsWrapperStyles} data-testid={formatDataTestId(testId, 'dots')}>
        {[0, 1, 2, 3, 4].map((i) => {
          const isSelected = i === selectedDot;
          const isSmall = (i === 0 && showSmallFirst) || (i === 4 && showSmallLast);
          const targetSlide = getSlideIndexForDot(i, activeSlide, totalCarouselImages);

          if (targetSlide === undefined) {
            return (
              <Box
                key={i}
                {...getDotStyles(isSelected, isSmall)}
                aria-hidden="true"
                cursor="default"
                data-testid={formatDataTestId(testId, `dots-${i}`)}
                pointerEvents="none"
              />
            );
          }

          return (
            <Box
              as="button"
              key={i}
              type="button"
              aria-current={isSelected ? 'true' : undefined}
              aria-label={t('searchresults.list.hotel.carousel.showImage', {
                imageNumber: targetSlide + 1,
                totalImages: totalCarouselImages,
              })}
              onClick={(event) => {
                event.preventDefault();
                event.stopPropagation();
                setActiveSlide(targetSlide);
              }}
              {...getDotStyles(isSelected, isSmall)}
              data-testid={formatDataTestId(testId, `dots-${i}`)}
            />
          );
        })}
      </Box>
    );
  }

  const handleHover = (hovered: boolean) => setIsHovered(hovered);

  const handleCarouselKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (!isCarouselInteractive) {
      return;
    }

    if (event.key === 'ArrowRight') {
      event.preventDefault();
      event.stopPropagation();
      setActiveSlide((currentSlide) => Math.min(currentSlide + 1, totalCarouselImages - 1));
    }

    if (event.key === 'ArrowLeft') {
      event.preventDefault();
      event.stopPropagation();
      setActiveSlide((currentSlide) => Math.max(currentSlide - 1, 0));
    }
  };

  return (
    <Box
      position="relative"
      {...styles}
      aria-label={
        isCarouselInteractive
          ? t('searchresults.list.hotel.carousel.label', { hotelName: name ?? '' }).trim()
          : undefined
      }
      data-testid={testId}
      role={isCarouselInteractive ? 'region' : undefined}
      tabIndex={isCarouselInteractive ? 0 : undefined}
      _focusVisible={{
        outline: '2px solid var(--chakra-colors-primary)',
        outlineOffset: '2px',
      }}
      onMouseEnter={() => handleHover(true)}
      onMouseLeave={() => handleHover(false)}
      onFocus={() => setIsFocused(true)}
      onBlur={(event) => {
        const nextFocusedElement = event.relatedTarget;
        if (!nextFocusedElement || !event.currentTarget.contains(nextFocusedElement as Node)) {
          setIsFocused(false);
        }
      }}
      onKeyDown={handleCarouselKeyDown}
    >
      <Carousel
        activeSlide={activeSlide}
        setActiveSlide={setActiveSlide}
        additionalNavButtonStyles={additionalNavButtonStyles}
        focusOnLoad={false}
        infinite={false}
        arrows={isMobile || isHovered || isFocused}
      >
        {carouselImages.map((item, index) => (
          <Box position="relative" {...styles} key={index} data-testid={`Box-${index}`}>
            <Image
              src={formatAssetsUrl(item?.imageSrc ?? '')}
              alt={`${name} ${item.tags[0] ?? 'exterior'}`}
              style={{ objectFit: 'cover' }}
              fill
              priority={false}
              sizes="(max-width: 575px) 575px, (min-width: 576px && max-width: 767px) 120px, (min-width: 768px) 188px"
            />
          </Box>
        ))}
      </Carousel>
      <HotelBrandLogo logos={brandLogos} brand={brand} />
      {carouselImages.length > 1 && renderCustomDots()}
    </Box>
  );
}

/**
 * Maps the active slide to a dot index for the custom carousel dots.
 * Note: This logic is tailored for a maximum of 5 dots displayed and specific UI requirements.
 * If the dot display logic changes, this function will need to be updated.
 */
export function getDotIndex(activeSlide: number, totalSlides: number): number {
  if (totalSlides === 2) {
    return activeSlide === 0 ? 0 : 4;
  }
  if (totalSlides === 3) {
    return activeSlide === 0 ? 0 : activeSlide === 1 ? 2 : 4;
  }
  if (totalSlides === 4) {
    if (activeSlide === 0) return 0;
    if (activeSlide === 1 || activeSlide === 2) return 2;
    return 4;
  }
  if (totalSlides === 5) {
    return activeSlide;
  }
  // >5 images
  if (activeSlide === 0) return 0;
  if (activeSlide === 1) return 1;
  if (activeSlide === totalSlides - 2) return 3;
  if (activeSlide === totalSlides - 1) return 4;
  return 2; // middle images
}

function getSlideIndexForDot(
  dotIndex: number,
  activeSlide: number,
  totalSlides: number
): number | undefined {
  if (getDotIndex(activeSlide, totalSlides) === dotIndex) {
    return activeSlide;
  }

  return Array.from({ length: totalSlides }, (_, slideIndex) => slideIndex).find(
    (slideIndex) => getDotIndex(slideIndex, totalSlides) === dotIndex
  );
}

const customDotsWrapperStyles: BoxProps = {
  position: 'absolute',
  bottom: 'sm',
  left: '50%',
  transform: 'translateX(-50%)',
  display: 'flex',
  gap: 'xs',
  zIndex: 99,
  alignItems: 'center',
};

function getDotStyles(isSelected: boolean, isSmall: boolean): BoxProps {
  return {
    display: 'inline-block',
    width: '0.5rem',
    height: '0.5rem',
    borderRadius: '50%',
    border: 0,
    bgColor: isSelected ? 'baseWhite' : 'lightGrey2',
    cursor: 'pointer',
    opacity: isSelected ? 1 : 0.6,
    padding: 0,
    transform: isSmall ? 'scale(0.75)' : 'scale(1)',
    transition: 'background 0.2s, opacity 0.2s, transform 0.2s',
    _focusVisible: {
      outline: '2px solid var(--chakra-colors-primary)',
      outlineOffset: '2px',
    },
  };
}

const additionalNavButtonStyles = {
  boxStyles: {
    h: '1.5rem',
    w: '1.5rem',
    top: { base: '40%', sm: '25%', md: '40%' },
  } as BoxProps,
  leftArrowStyles: {
    left: 'xs',
  } as BoxProps,
  rightArrowStyles: {
    right: 'xs',
  } as BoxProps,
  leftIconStyle: {
    transform: 'translateX(0.3rem) scale(1)',
  } as BoxProps,
  rightIconStyle: {
    transform: 'translateX(0.4rem) scale(1)',
  } as BoxProps,
} as const;
