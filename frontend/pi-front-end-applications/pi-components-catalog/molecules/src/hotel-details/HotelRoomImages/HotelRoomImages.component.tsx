import {
  Box,
  Flex,
  FlexProps,
  Image as ChakraImage,
  ResponsiveValue,
  Text,
} from '@chakra-ui/react';
import { ImageItem } from '@whitbread-eos/api';
import {
  Button,
  Carousel,
  Expand,
  Icon,
  MobileCarousel,
  ModalVariants,
} from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  isStringValid,
  akamaiImageLoader,
  isIVMEnabled,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { useState } from 'react';

type ThumbnailSize =
  | {
      xl: string;
      lg: string;
      md: string;
      sm: string;
      mobile: string;
      base: string;
    }
  | string;

export interface RoomImage {
  alt: string;
  caption: string;
  iconSrc: string;
  imageSrc: string;
  thumbnailSrc: string;
}

interface Props {
  images: ImageItem[] | undefined;
  thumbnailHeight: ThumbnailSize;
  isLessThanSm: boolean | undefined;
  isLessThanLg: boolean | undefined;
  containerRef?: React.RefObject<HTMLElement>;
}

export default function HotelRoomImagesComponent({
  images,
  thumbnailHeight,
  isLessThanSm,
  isLessThanLg,
  containerRef,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [activeThumbnail, setActiveThumbnail] = useState(0);
  const [activeSlide, setActiveSlide] = useState(0);

  const isInContainer = !!containerRef;

  const thumbnailStyles = {
    position: 'relative',
    h: thumbnailHeight,
    w: '100%',
  } as const;
  return (
    <Box data-testid="room-images">
      <Box {...thumbnailStyles}>
        {isStringValid(images?.[images?.length - 1]?.imageSrc) && (
          <Image
            src={formatAssetsUrl(images?.[images?.length - 1]?.imageSrc ?? '')}
            alt={images?.[images.length - 1]?.alt ?? ''}
            fill
            style={{ objectFit: 'cover', objectPosition: 'center' }}
            priority={false}
            loader={isIVMEnabled() ? akamaiImageLoader : undefined}
            sizes="640px"
          />
        )}
        {images && images?.length > 1 && renderCTAButton()}
      </Box>
      {renderModal()}
    </Box>
  );

  function renderCTAButton() {
    const CTAButtonStyles = {
      position: 'absolute' as ResponsiveValue<'absolute'>,
      bottom: 'sm',
      right: 'sm',
      width: 'auto',
      height: 'var(--chakra-space-xl)',
      fontSize: 'sm',
    };

    const CTAIconStyles = {
      position: 'absolute' as ResponsiveValue<'absolute'>,
      top: 'sm',
      right: 'sm',
      width: 'var(--chakra-space-xl)',
      height: 'var(--chakra-space-xl)',
      cursor: 'pointer',
    };

    if (isLessThanLg) {
      return (
        <Box
          {...CTAIconStyles}
          data-testid="room-images__expand"
          onClick={() => {
            setActiveThumbnail(0);
            setIsModalVisible(true);
            setActiveSlide(0);
          }}
        >
          <Expand />
        </Box>
      );
    } else {
      return (
        <Button
          variant="tertiary"
          size="sm"
          data-testid="hdp_roomsViewGallery"
          {...CTAButtonStyles}
          onClick={() => {
            setActiveThumbnail(0);
            setIsModalVisible(true);
            setActiveSlide(0);
          }}
        >
          {t('hoteldetails.viewgallery')}
        </Button>
      );
    }
  }

  function renderModal() {
    const mobileGalleryThumbnails = images?.map((item) => (
      <ChakraImage
        key={item?.alt}
        src={formatAssetsUrl(item?.thumbnailSrc ?? '')}
        alt={item?.alt ?? ''}
      />
    ));
    const sizes = isLessThanLg ? undefined : '640px';
    const imageCarousel = renderImages(sizes);
    return (
      <ModalVariants
        isOpen={isModalVisible}
        onClose={() => {
          setIsModalVisible(false);
          setActiveThumbnail(0);
          setActiveSlide(0);
        }}
        variant="gallery"
        variantProps={{
          title: `${activeSlide + 1}/${imageCarousel?.length}`,
        }}
        {...(containerRef && { portalProps: { containerRef } })}
      >
        {isLessThanLg ? (
          <MobileCarousel
            activeSlide={activeSlide}
            setActiveSlide={setActiveSlide}
            key={activeThumbnail}
            thumbnails={mobileGalleryThumbnails}
          >
            {imageCarousel}
          </MobileCarousel>
        ) : (
          <Box
            w={isInContainer ? '100%' : '41.875rem'}
            px={isInContainer ? { mobile: '1rem', sm: '4xl' } : '4xl'}
          >
            <Carousel
              activeSlide={activeSlide}
              setActiveSlide={setActiveSlide}
              key={activeThumbnail}
            >
              {imageCarousel}
            </Carousel>
          </Box>
        )}
      </ModalVariants>
    );
  }

  function renderImages(sizes: string | undefined) {
    const iconStyles = isLessThanSm
      ? {
          position: 'absolute' as ResponsiveValue<'absolute'>,
          left: 'sm',
          bottom: '6xl',
          bgColor: 'baseWhite',
          borderRadius: '4px',
          p: '6px 6px 6px 8px',
        }
      : {
          mr: 'sm',
        };

    const iconFlexStyles = {
      minH: '4rem',
      alignItems: 'center',
      justify: 'center',
      position: 'relative',
    } as FlexProps;

    return images?.map((item) => (
      <Box key={item?.alt}>
        <Box
          position="relative"
          w="full"
          h={{ sm: '25.313rem', xs: '18.938rem', mobile: '11.25rem', base: '11.25rem' }}
        >
          <Image
            src={formatAssetsUrl(item?.imageSrc ?? '')}
            alt="Hotel Image"
            fill
            sizes={sizes}
            style={{ objectFit: 'cover' }}
            loader={isIVMEnabled() ? akamaiImageLoader : undefined}
          />
        </Box>
        <Flex {...iconFlexStyles} data-testid="carousel_room-image-caption">
          {item?.iconSrc && <Icon src={formatAssetsUrl(item.iconSrc)} {...iconStyles} />}
          <Text>{item?.caption}</Text>
        </Flex>
      </Box>
    ));
  }
}
