import { Box, BoxProps } from '@chakra-ui/react';
import type { SrpBrand, ImageThumbnailData } from '@whitbread-eos/api';
import { formatAssetsUrl, akamaiImageLoader, isIVMEnabled } from '@whitbread-eos/utils';
import Image from 'next/image';

import HotelBrandLogo from '../HotelBrandLogo/HotelBrandLogo.component';

interface Props {
  imageData: ImageThumbnailData;
  brand: string;
  brandLogos: Pick<SrpBrand, 'hubLogo' | 'zipLogo'>;
  testId: string;
  styles?: BoxProps;
  eagerLoad?: boolean;
}

export default function HotelThumbnail({
  imageData,
  brand,
  brandLogos,
  testId,
  styles,
  eagerLoad,
}: Readonly<Props>) {
  return (
    <Box position="relative" {...styles} data-testid={testId}>
      <Image
        src={formatAssetsUrl(imageData.imageSrc)}
        alt={imageData.imageAlt}
        fill
        style={{ objectFit: 'fill' }}
        sizes="(max-width: 575px) 575px, (min-width: 576px && max-width: 767px) 120px, (min-width: 768px) 188px"
        loader={isIVMEnabled() ? akamaiImageLoader : undefined}
        priority={!!eagerLoad}
      />
      <HotelBrandLogo logos={brandLogos} brand={brand} />
    </Box>
  );
}
