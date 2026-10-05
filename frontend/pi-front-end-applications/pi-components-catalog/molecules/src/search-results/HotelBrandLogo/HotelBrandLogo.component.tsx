import { Box, BoxProps, Image as ChakraImage } from '@chakra-ui/react';
import type { SrpBrand } from '@whitbread-eos/api';
import { formatAssetsUrl } from '@whitbread-eos/utils';
import { useMemo } from 'react';

const ZIP_HOTEL = 'ZIP';
const HUB_HOTEL = 'HUB';
const NOT_PI_HOTEL_BRANDS = [ZIP_HOTEL, HUB_HOTEL];

interface Props {
  brand: string;
  logos: Pick<SrpBrand, 'hubLogo' | 'zipLogo'>;
}

export default function HotelBrandLogo({ brand, logos }: Props) {
  const isNotPiBrand = useMemo(() => checkIfIsNotPiBrand(brand), [brand]);

  return isNotPiBrand ? (
    <Box {...hotelBrandLogoWrapperStyles} data-testid="srp_hotel-brand-logo">
      <ChakraImage
        src={formatAssetsUrl(getBrandLogoURL(brand, logos))}
        alt={brand === 'HUB' ? 'hub' : 'zip'}
      />
    </Box>
  ) : null;
}

export const checkIfIsNotPiBrand = (brand: string) => {
  return NOT_PI_HOTEL_BRANDS.includes(brand?.toUpperCase());
};

export const getBrandLogoURL = (brand: string, logos: Pick<SrpBrand, 'hubLogo' | 'zipLogo'>) => {
  if (brand === 'HUB') {
    return logos.hubLogo;
  }
  return brand === 'ZIP' ? logos.zipLogo : '';
};

const hotelBrandLogoWrapperStyles = {
  position: 'absolute',
  top: 'sm',
  left: 0,
} as BoxProps;
