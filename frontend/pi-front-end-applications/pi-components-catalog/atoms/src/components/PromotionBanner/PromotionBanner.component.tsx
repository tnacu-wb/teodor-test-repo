import { Box, Image, Link } from '@chakra-ui/react';
import { AnalyticsData } from '@whitbread-eos/api';
import { formatAssetsUrl, analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

interface HotelInformation {
  bookRestaurantCta: {
    bookingCardCtaLink?: string;
    bookingCardCtaText?: string;
    bookingCardTrackingId?: string;
  };
  restaurant: {
    bookingCardImage?: string;
    bookingCardBackgroundImage?: string;
  };
}
export interface Props {
  data?: HotelInformation;
}

export default function PromotionBanner({ data }: Readonly<Props>) {
  const { bookingCardImage, bookingCardBackgroundImage } = data?.restaurant || {};
  const { bookingCardCtaLink, bookingCardCtaText } = data?.bookRestaurantCta || {};

  useEffect(() => {
    analytics.update({
      restaurantBanner: true,
    });
  }, []);

  const handleClick = () => {
    window.__satelliteLoaded && window._satellite.track('restaurantBannerClicked');
  };

  return (
    <Box
      {...styles.imageWrapper}
      bgImage={`url('${formatAssetsUrl(bookingCardBackgroundImage ?? '')}')`}
    >
      <Image
        src={formatAssetsUrl(bookingCardImage ?? '')}
        fit="fill"
        w="175px"
        m="60px auto 0"
        data-testid={'promotion-banner-image'}
      />
      <Box {...styles.buttonWrapper}>
        <Link
          {...styles.button}
          target="_blank"
          data-testid={'promotion-banner-link'}
          href={bookingCardCtaLink}
          fontSize="18px"
          onClick={handleClick}
        >
          {bookingCardCtaText}
        </Link>
      </Box>
    </Box>
  );
}

const styles = {
  imageWrapper: {
    float: 'left',
    position: 'relative',
    width: '100%',
    height: '214px',
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundBlendMode: 'overlay',
  } as const,
  buttonWrapper: {
    position: 'absolute',
    bottom: '15px',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    padding: {
      mobile: '0 10px',
      xl: '0 23px',
    },
  } as const,
  button: {
    height: '44px',
    width: {
      mobile: '260px',
      xs: '280px',
      sm: '260px',
      lg: '100%',
    },
    backgroundColor: 'var(--chakra-colors-baseWhite)',
    color: 'var(--chakra-colors-btnSecondaryEnabled)',
    fontWeight: '600',
    borderRadius: '4px',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    _hover: {
      textDecoration: 'none',
      boxShadow: '0 4px 8px var(--chakra-colors-lightGrey2)',
      background:
        'var(--chakra-colors-baseWhite) radial-gradient(circle, rgba(81, 30, 98, .15) 1%, var(--chakra-colors-baseWhite) 1%) center / 15000%',
    } as const,
  },
};
