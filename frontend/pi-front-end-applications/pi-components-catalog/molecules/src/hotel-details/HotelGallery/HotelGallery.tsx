import type { FlexboxProps, FlexProps } from '@chakra-ui/react';
import {
  Box,
  Flex,
  Grid,
  GridItem,
  Image as ChakraImage,
  ResponsiveValue,
  Text,
} from '@chakra-ui/react';
import { FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN } from '@whitbread-eos/api';
import { Button, Carousel, Icon, MobileCarousel, ModalVariants } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  akamaiImageLoader,
  isIVMEnabled,
  useSemanticTypography,
  useFeatureToggle,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { useState } from 'react';

import ImageGalleryModal from '../ImageGalleryModal/ImageGalleryModal.component';

interface Props {
  thumbnailSectionHeight?: string;
  isLessThanLg: boolean | undefined;
  eagerLoad?: boolean;
}

export default function HotelGallery({
  thumbnailSectionHeight,
  isLessThanLg,
  eagerLoad,
}: Readonly<Props>) {
  const {
    galleryImages: data,
    isLoading,
    isError,
    error,
    hotelFlags,
  } = useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const [activeThumbnail, setActiveThumbnail] = useState(0);
  const [isModalVisible, setIsModalVisible] = useState(false);
  // Carousel
  const [activeSlide, setActiveSlide] = useState(0);
  const getTypographyProps = useSemanticTypography();

  const imageLoader = isIVMEnabled() ? akamaiImageLoader : undefined;

  const { [FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN]: isImageGalleryRedesignEnabled = false } =
    useFeatureToggle();

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data) {
    return null;
  }

  return (
    <>
      {isLessThanLg ? renderSingleThumbnail() : renderThumbnailsGrid()}
      {isImageGalleryRedesignEnabled ? renderImageGalleryModal() : renderModal()}
    </>
  );

  function renderSingleThumbnail() {
    const singleThumbnailStyles = {
      position: 'relative',
      padding: '0 !important',
      marginLeft: {
        mobile: '-1.9rem !important',
      },
      width: {
        mobile: 'calc(100% + var(--chakra-space-3xl))',
        md: 'calc(100% + var(--chakra-space-4xl))',
      },
      h: { sm: '22.356rem', xs: '18.938rem', mobile: '11.25rem', base: '11.25rem' },
      mb: 'lg',
      cursor: 'pointer',
    } as const;

    if (!data?.length) {
      return null;
    }

    return (
      <Box
        position="relative"
        data-testid="singleThumbnail"
        onClick={() => setIsModalVisible(isImageGalleryRedesignEnabled)}
      >
        <Box {...singleThumbnailStyles}>
          <Carousel activeSlide={activeSlide} setActiveSlide={setActiveSlide} key={activeThumbnail}>
            {renderImages()}
          </Carousel>
          {activeSlide === 0 && renderFlagOverlay()}
        </Box>

        {renderImagesCountBadge()}
        {renderOverlayImageCaption()}
      </Box>
    );
  }

  function renderFlagOverlay() {
    if (!hotelFlags?.isEnabled || !hotelFlags?.flagOverlay) {
      return null;
    }

    const { backgroundColour, textColour, text, backgroundImage } = hotelFlags.flagOverlay;

    const overlayContainerStyles = {
      position: 'absolute',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      pointerEvents: 'none',
    } as const;

    const overlayImageStyles = {
      position: 'absolute',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      objectFit: 'scale-down',
      objectPosition: 'top left',
      paddingLeft: 'var(--chakra-space-md)',
    } as const;

    const overlayTextStyles = {
      position: 'absolute',
      bottom: 'sm',
      left: 'sm',
      fontFamily: 'Sunset-Sans, helvetica, arial, sans-serif',
      fontWeight: 'black',
      fontSize: { base: '4xl', sm: '6xl' },
      lineHeight: { base: '5', sm: '6' },
      letterSpacing: '-0.02em',
      pl: { mobile: 'var(--chakra-space-md)' },
    } as const;

    return (
      <Box {...overlayContainerStyles} backgroundColor={backgroundColour ?? undefined}>
        {backgroundImage && (
          <ChakraImage
            src={formatAssetsUrl(backgroundImage)}
            alt="overlay"
            style={overlayImageStyles}
          />
        )}

        <Text {...overlayTextStyles} color={textColour ?? undefined}>
          {text}
        </Text>
      </Box>
    );
  }

  function renderImagesCountBadge() {
    const imagesCountBadgeStyles = {
      display: 'inline',
      color: 'baseWhite',
      fontSize: 'xs',
      lineHeight: 'md',
      position: 'absolute',
      bottom: 'md',
      right: 0,
      bg: 'rgba(51, 51, 51, 0.75)',
      px: 'sm',
      borderRadius: 4,
    } as const;

    return (
      <Box {...imagesCountBadgeStyles} data-testid="hdp_hotelGalleryImagesCount">
        {activeSlide + 1} / {data?.length}
      </Box>
    );
  }

  function renderOverlayImageCaption() {
    const iconStyles = {
      mr: 'sm',
      height: '1rem',
    };

    const imageCaptionStyles = {
      position: 'absolute',
      bottom: 'md',
      left: 0,
      bg: 'rgba(255, 255, 255, 0.75)',
      px: 'sm',
      borderRadius: 4,
      alignItems: 'center',
      maxWidth: '75%',
    } as FlexboxProps;

    const imageCaptionTextStyles = { fontSize: 'xs', lineHeight: 'md', color: 'darkGrey1' };

    const item = data?.[activeSlide];
    if (!item) {
      return null;
    }

    return (
      <Flex {...imageCaptionStyles} data-testid="carousel_hotel-image-caption">
        {item.iconSrc && <Icon src={formatAssetsUrl(item.iconSrc)} {...iconStyles} />}
        <Text {...imageCaptionTextStyles}>{item.caption}</Text>
      </Flex>
    );
  }

  function renderThumbnailsGrid() {
    const thumbnailGridStyles = {
      templateRows: 'repeat(2, 1fr)',
      templateColumns: 'repeat(4, 1fr)',
      gap: 3,
      w: '100%',
    };

    const firstThreeThumbnails =
      data
        ?.map((item, index) => {
          const sizes =
            index === 0 ? '(max-width: 1280px) 480px, (min-width: 1281px) 514px' : '164px';
          return (
            <Thumbnail
              key={item.alt}
              src={formatAssetsUrl(item?.thumbnailSrc ?? '')}
              alt={item?.alt ?? ''}
              onClick={() => {
                setActiveThumbnail(index);
                setIsModalVisible(true);
                setActiveSlide(index);
              }}
              imageLoader={imageLoader}
              loadWithPriority={eagerLoad ? true : index === 0}
              sizes={sizes}
            />
          );
        })
        ?.slice(0, 3) || [];

    return (
      <Box position="relative" data-testid="thumbnails">
        <Grid {...thumbnailGridStyles} h={thumbnailSectionHeight}>
          <GridItem rowSpan={2} colSpan={3} position="relative">
            {firstThreeThumbnails[0] || <EmptyThumbnail />}
            {renderFlagOverlay()}
          </GridItem>
          <GridItem colSpan={1}>{firstThreeThumbnails[1] || <EmptyThumbnail />}</GridItem>
          <GridItem colSpan={1}>{firstThreeThumbnails[2] || <EmptyThumbnail />}</GridItem>
        </Grid>

        {data && data?.length > 3 && renderSeeAllPhotosButton()}
      </Box>
    );
  }

  function renderSeeAllPhotosButton() {
    const seeAllPhotosButtonStyles = {
      position: 'absolute' as ResponsiveValue<'absolute'>,
      bottom: 'sm',
      right: 'sm',
      width: 'auto',
      height: 'var(--chakra-space-xl)',
      fontSize: 'sm',
    };

    return (
      <Button
        variant="tertiary"
        size="sm"
        data-testid="hdp_hotelGallerySeeAllPhotos"
        {...seeAllPhotosButtonStyles}
        onClick={() => {
          setActiveThumbnail(0);
          setIsModalVisible(true);
          setActiveSlide(0);
        }}
      >
        <Text as="span" {...getTypographyProps({}, seeAllPhotosSemanticTypography)}>
          {t('hoteldetails.seeallphotos')}
        </Text>
      </Button>
    );
  }

  function renderModal() {
    const mobileGalleryThumbnails = data?.map((item) => (
      <ChakraImage
        key={item.alt}
        src={formatAssetsUrl(item?.thumbnailSrc ?? '')}
        alt={item?.alt ?? ''}
      />
    ));

    return (
      <ModalVariants
        isOpen={isModalVisible}
        onClose={() => {
          setIsModalVisible(false);
          setActiveThumbnail(0);
          setActiveSlide(0);
        }}
        dataTestId="hdp_hotelGallery"
        variant="gallery"
        variantProps={{
          title: `${activeSlide + 1}/${renderImages()?.length}`,
        }}
      >
        {isLessThanLg ? (
          <MobileCarousel
            activeSlide={activeSlide}
            setActiveSlide={setActiveSlide}
            key={activeThumbnail}
            thumbnails={mobileGalleryThumbnails}
          >
            {renderImages()}
          </MobileCarousel>
        ) : (
          <Box w="41.775rem" px="lg">
            <Carousel
              activeSlide={activeSlide}
              setActiveSlide={setActiveSlide}
              key={activeThumbnail}
            >
              {renderImages()}
            </Carousel>
          </Box>
        )}
      </ModalVariants>
    );
  }

  function renderImageGalleryModal() {
    return (
      <ImageGalleryModal
        isModalOpen={isModalVisible}
        onModalClose={() => setIsModalVisible(false)}
      />
    );
  }

  function renderImages() {
    const iconStyles = {
      mr: 'sm',
    };

    const captionFlexStyles = {
      position: 'relative',
      alignItems: 'center',
      justify: 'center',
      minH: '2rem',
    } as FlexProps;

    let sizes: string;
    if (!isLessThanLg) {
      sizes = '620px';
    }
    return data?.map((item) => (
      <Box key={item.alt}>
        <Box
          position="relative"
          w="full"
          h={{ sm: '22.356rem', xs: '18.938rem', mobile: '11.25rem', base: '11.25rem' }}
        >
          <Image
            src={formatAssetsUrl(item?.imageSrc ?? '')}
            alt="Hotel Image"
            fill
            style={{ objectFit: 'cover' }}
            priority={false}
            loader={imageLoader}
            sizes={sizes}
          />
        </Box>
        {!isLessThanLg && (
          <Flex {...captionFlexStyles} data-testid="carousel_hotel-image-caption">
            {item.iconSrc && <Icon src={formatAssetsUrl(item.iconSrc)} {...iconStyles} />}
            <Text>{item.caption}</Text>
          </Flex>
        )}
      </Box>
    ));
  }
}

const seeAllPhotosSemanticTypography = {
  textStyle: 'link-s-emphasis',
};

interface ThumbnailProps {
  src: string;
  alt: string;
  loadWithPriority: boolean;
  onClick: () => void;
  imageLoader: typeof akamaiImageLoader | undefined;
  sizes: string | undefined;
}

function Thumbnail({
  src,
  alt,
  onClick,
  loadWithPriority,
  imageLoader,
  sizes = '',
}: Readonly<ThumbnailProps>) {
  const [isHovering, setIsHovering] = useState(false);

  const thumbnailStyes = {
    width: '100%',
    height: '100%',
    position: 'relative' as ResponsiveValue<'relative'>,
    cursor: 'pointer',
  };

  return (
    <Box
      {...thumbnailStyes}
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
      sx={{
        '& img': {
          transition: 'filter 0.3s ease-in-out',
          '&.darken': {
            filter: 'brightness(90%)',
          },
        },
      }}
    >
      <Image
        src={src}
        alt={alt}
        fill
        style={{ objectFit: 'cover' }}
        onClick={onClick}
        className={isHovering ? 'darken' : ''}
        priority={loadWithPriority}
        loader={imageLoader}
        sizes={sizes}
      />
    </Box>
  );
}

function EmptyThumbnail() {
  return <Box data-testid="emptyThumbnail" bgColor="lightGrey4" h="100%" w="100%" />;
}
