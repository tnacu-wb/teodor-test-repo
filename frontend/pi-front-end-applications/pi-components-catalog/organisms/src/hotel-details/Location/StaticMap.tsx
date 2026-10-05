import { Box } from '@chakra-ui/react';
import { theme, LoadingSpinner } from '@whitbread-eos/atoms';
import { useState } from 'react';

interface Props {
  latitude?: number;
  longitude?: number;
  brand?: string;
  apiKey: string;
}

function getFillColorMarker(brand: string): string {
  let fillColor = theme.colors.btnSecondaryEnabled;

  if (brand?.toLowerCase() === 'hub') {
    fillColor = theme.colors.hubPrimary;
  }

  if (brand?.toLowerCase() === 'zip') {
    fillColor = theme.colors.zipPrimary;
  }

  return fillColor;
}

export default function StaticMap({ latitude, longitude, brand, apiKey }: Readonly<Props>) {
  const [isLoading, setIsLoading] = useState(true);
  if (!latitude || !longitude) return null;

  const markerColor = getFillColorMarker(brand as string).replace('#', '0x');

  const url = `https://maps.googleapis.com/maps/api/staticmap
?center=${latitude},${longitude}
&zoom=13
&size=800x400
&scale=2
&style=feature:poi|visibility:simplified
&maptype=roadmap
&markers=size:small|color:${markerColor}|${latitude},${longitude}
&key=${apiKey}`;

  return (
    <Box w="100%" h="100%" display="flex" alignItems="center" justifyContent="center">
      {isLoading && <LoadingSpinner loadingText="Loading..." />}
      <Box
        as="img"
        src={url}
        alt="Hotel location map"
        onLoad={() => setIsLoading(false)}
        onError={() => setIsLoading(false)}
        display={isLoading ? 'none' : 'block'}
        w="100%"
        h="100%"
        objectFit="cover"
      />
    </Box>
  );
}
