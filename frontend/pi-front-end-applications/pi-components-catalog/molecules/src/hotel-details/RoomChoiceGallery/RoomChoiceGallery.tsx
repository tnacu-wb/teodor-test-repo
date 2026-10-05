import type { FlexProps } from '@chakra-ui/react';
import {
  Box,
  Flex,
  Grid,
  GridItem,
  Image as ChakraImage,
  ResponsiveValue,
  Text,
} from '@chakra-ui/react';
import {
  ACCESSIBLE_BARRIER_FREE,
  GALLERY_IMAGE_MAPPING,
  isIconForRoomTypes,
} from '@whitbread-eos/api';
import {
  Accessible24,
  AccessibleBarrierFree,
  Button,
  Carousel,
  Icon,
  MobileCarousel,
  ModalVariants,
  Section,
} from '@whitbread-eos/atoms';
import { formatAssetsUrl, akamaiImageLoader, isIVMEnabled } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { ReactElement, useState } from 'react';

const thumbnailHeight = {
  xl: '19.687rem',
  lg: '18.312rem',
  md: '15rem',
  sm: '15rem',
  mobile: '11.25rem',
  base: '11.25rem',
};

interface Props {
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  roomType: string;
}

const loadGalleryImages = (roomType: string, t: (key: string) => string) => {
  const roomConfig = GALLERY_IMAGE_MAPPING.find(
    (config: { type: string }) => config.type === roomType
  );
  return roomConfig
    ? roomConfig.images.map((image: any) => ({
        alt: t(image.titleKey),
        caption: t(image.titleKey),
        imageSrc: t(image.pathKey),
        thumbnailSrc: t(image.pathKey),
        icon: image.icon === ACCESSIBLE_BARRIER_FREE ? <AccessibleBarrierFree /> : <Accessible24 />,
      }))
    : [];
};

export default function RoomChoiceGalleryComponent({
  isLessThanMd,
  isLessThanLg,
  roomType,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [activeThumbnail, setActiveThumbnail] = useState(0);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [activeSlide, setActiveSlide] = useState(0);

  const imageLoader = isIVMEnabled() ? akamaiImageLoader : undefined;

  const galleryImages = loadGalleryImages(roomType, t);

  return (
    <>
      {isLessThanLg ? renderSingleThumbnail(roomType) : renderThumbnailsGrid(roomType)}
      {renderModal()}
    </>
  );

  function renderSingleThumbnail(roomType: string) {
    const singleThumbnailStyles = {
      position: 'relative',
      padding: '0 !important',
      marginLeft: '-2rem !important',
      width: 'calc(100% + var(--chakra-space-3xl))',
      h: { sm: '25.313rem', xs: '18.938rem', mobile: '11.25rem', base: '11.25rem' },
      mb: 'sm',
      cursor: 'pointer',
    } as const;

    const iconStyles = {
      bgColor: 'baseWhite',
      mr: 'sm',
    };

    const captionFlexStyles = {
      position: 'relative',
      alignItems: 'center',
      justify: 'center',
      minH: '2rem',
    } as FlexProps;

    return (
      <Box
        position="relative"
        data-testid={`hdp-${roomType}-singleThumbnails`}
        onClick={() => setIsModalVisible(true)}
      >
        <Box {...singleThumbnailStyles}>
          <Image
            src={formatAssetsUrl(galleryImages[0].thumbnailSrc)}
            alt={galleryImages[0].alt}
            fill
            style={{ objectFit: 'cover', objectPosition: 'center' }}
            loader={imageLoader}
          />
          {renderSeeAllPhotosButton()}
        </Box>
        <Section>
          <Flex {...captionFlexStyles}>
            {isIconForRoomTypes?.[roomType as keyof typeof isIconForRoomTypes] && (
              <Icon svg={galleryImages[0].icon} {...iconStyles} />
            )}
            <Text>{galleryImages[0].caption}</Text>
          </Flex>
        </Section>
      </Box>
    );
  }

  function renderThumbnailsGrid(roomType: string) {
    const thumbnailGridStyles = {
      templateRows: 'repeat(2, 1fr)',
      templateColumns: 'repeat(4, 1fr)',
      gap: 3,
      w: '100%',
      h: thumbnailHeight,
    };

    const firstTwoThumbnails = galleryImages.map((item, index) => (
      <Thumbnail
        key={index}
        src={formatAssetsUrl(item.thumbnailSrc)}
        alt={item.alt}
        caption={item.caption}
        icon={item.icon}
        onClick={() => {
          setActiveThumbnail(index);
          setIsModalVisible(true);
          setActiveSlide(index);
        }}
        loadWithPriority={index === 0}
        roomType={roomType}
      />
    ));

    return (
      <Section>
        <Box maxW="full" position="relative" mb="5xl" data-testid={`hdp-${roomType}-thumbnails`}>
          <Box>
            <Grid {...thumbnailGridStyles}>
              <GridItem rowSpan={3} colSpan={2}>
                {firstTwoThumbnails[0]}
              </GridItem>
              <GridItem rowSpan={3} colSpan={2}>
                {firstTwoThumbnails[1]}
              </GridItem>
            </Grid>
            {renderSeeAllPhotosButton()}
          </Box>
        </Box>
      </Section>
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
        data-testid={`hdp-${roomType}-GallerySeeAllPhotos`}
        {...seeAllPhotosButtonStyles}
        onClick={() => {
          setActiveThumbnail(0);
          setIsModalVisible(true);
          setActiveSlide(0);
        }}
      >
        {t('hoteldetails.seeallphotos')}
      </Button>
    );
  }

  function renderModal() {
    const mobileGalleryThumbnails = galleryImages.map((item) => (
      <ChakraImage key={item.alt} src={formatAssetsUrl(item.thumbnailSrc)} alt={item.alt} />
    ));

    return (
      <ModalVariants
        isOpen={isModalVisible}
        onClose={() => {
          setIsModalVisible(false);
          setActiveThumbnail(0);
          setActiveSlide(0);
        }}
        dataTestId={`hdp-${roomType}-Gallery`}
        variant="gallery"
        variantProps={{
          title: `${activeSlide + 1}/${renderImages().length}`,
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
          <Box w="41.875rem" px="4xl">
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

  function renderImages() {
    const iconStyles = isLessThanMd
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

    const captionFlexStyles = {
      position: 'relative',
      alignItems: 'center',
      justify: 'center',
      minH: '4rem',
    } as FlexProps;

    return galleryImages.map((item) => (
      <Box key={item.alt}>
        <Box
          position="relative"
          w="full"
          h={{ sm: '25.313rem', xs: '18.938rem', mobile: '11.25rem', base: '11.25rem' }}
        >
          <Image
            src={formatAssetsUrl(item.imageSrc)}
            alt="Hotel Image"
            fill
            style={{ objectFit: 'cover' }}
            priority={false}
            loader={imageLoader}
          />
        </Box>
        {isIconForRoomTypes?.[roomType as keyof typeof isIconForRoomTypes] ? (
          <Flex {...captionFlexStyles} data-testid="carousel_accessible-bathroom-image-caption">
            <Icon svg={item.icon} {...iconStyles} />
            <Text>{item.caption}</Text>
          </Flex>
        ) : (
          <Flex {...captionFlexStyles} data-testid="carousel_twin-image-caption">
            <Text>{item.caption}</Text>
          </Flex>
        )}
      </Box>
    ));
  }
}

interface ThumbnailProps {
  src: string;
  alt: string;
  caption?: string;
  icon: ReactElement;
  loadWithPriority: boolean;
  roomType: string;
  onClick: () => void;
}

function Thumbnail({
  src,
  alt,
  caption,
  icon,
  onClick,
  loadWithPriority,
  roomType,
}: Readonly<ThumbnailProps>) {
  const [isHovering, setIsHovering] = useState(false);

  const thumbnailStyles = {
    width: '100%',
    position: 'relative' as ResponsiveValue<'relative'>,
    cursor: 'pointer',
    h: thumbnailHeight,
  };

  const iconStyles = {
    bgColor: 'baseWhite',
    mr: 'sm',
  };

  const captionFlexStyles = {
    position: 'relative',
    alignItems: 'center',
    justify: 'center',
    minH: '4rem',
  } as FlexProps;

  return (
    <Box>
      <Box
        {...thumbnailStyles}
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
          loader={isIVMEnabled() ? akamaiImageLoader : undefined}
        />
      </Box>
      <Flex {...captionFlexStyles}>
        {isIconForRoomTypes?.[roomType as keyof typeof isIconForRoomTypes] && (
          <Icon svg={icon} {...iconStyles} />
        )}
        <Text>{caption}</Text>
      </Flex>
    </Box>
  );
}
