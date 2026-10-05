import type { BoxProps } from '@chakra-ui/react';
import { Box } from '@chakra-ui/react';
import { akamaiImageLoader, isIVMEnabled } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import Link from 'next/link';

export interface Props {
  brand: string;
  image: string;
  url: string;
  alt: string;
  testId?: string;
  imageWrapperStyles?: BoxProps;
}

const imageResponsiveStyles = {
  height: { mobile: '8.44rem', sm: '17.44rem', md: '23.25rem', lg: '12.06rem' },
  width: 'full',
  position: 'relative',
} as BoxProps;

export default function HotelThumbnailComponent({
  image,
  url,
  alt,
  testId = 'hotel-thumbnail',
  brand,
  imageWrapperStyles = {},
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  return (
    <Box
      {...imageResponsiveStyles}
      {...imageWrapperStyles}
      sx={{ '@media print': { width: '20.8125rem', height: '12.0625rem' } }}
    >
      <Image
        src={image}
        alt={alt}
        fill
        data-testid={testId}
        loader={isIVMEnabled() ? akamaiImageLoader : undefined}
      />
      <Box
        {...adjustTextStyles(brand)}
        data-testid={'hotel-line'}
        sx={{
          '@media print': {
            backgroundColor: 'btnSecondaryEnabled',
            color: 'baseWhite',
            opacity: '0.75',
            WebkitPrintColorAdjust: 'exact',
          },
        }}
      >
        <Link href={url} target="_blank">
          {t('dashboard.bookings.hotelDetails')}
        </Link>
      </Box>
    </Box>
  );
}

function adjustTextStyles(brand: string) {
  const textStyles: BoxProps = {
    position: 'absolute',
    color: 'baseWhite',
    bgColor: 'btnSecondaryEnabled',
    opacity: '0.75',
    textAlign: 'center',
    width: '100%',
    bottom: '0',
    cursor: 'pointer',
    fontSize: 'sm',
    lineHeight: '2',
    fontWeight: 'medium',
  };

  switch (brand) {
    case 'HUB': {
      return {
        ...textStyles,
        bgColor: 'darkGrey2',
        color: 'hubPrimary',
      };
    }
    case 'ZIP': {
      return {
        ...textStyles,
        bgColor: 'zipPrimary',
        color: 'baseWhite',
      };
    }
    case 'HPI':
      return {
        ...textStyles,
        color: 'hubPrimary',
        bgColor: 'darkGrey2',
      };
    case 'ZPI':
      return {
        ...textStyles,
        bgColor: 'zipPrimary',
      };
    default:
      return textStyles;
  }
}
