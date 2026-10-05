import { Box, BoxProps, Image as ChakraImage } from '@chakra-ui/react';
import Image from 'next/image';
import { ReactElement } from 'react';

export interface Props extends BoxProps {
  svg?: ReactElement;
  src?: string;
  alt?: string;
  useNextImage?: boolean;
}

export default function Icon({ svg, src, alt, useNextImage, ...otherProps }: Readonly<Props>) {
  const height = otherProps.height ?? '4rem';
  const width = otherProps.width ?? '10.75rem';

  if (src && useNextImage) {
    return (
      <Box
        position="relative"
        {...otherProps}
        height={height}
        width={width}
        data-testid="svg-container"
      >
        <Image src={src} alt={alt ?? ''} fill style={{ objectFit: 'contain' }} sizes={'172px'} />
      </Box>
    );
  }

  if (src) {
    return (
      <ChakraImage
        src={src}
        {...(alt !== undefined ? { alt } : {})}
        {...otherProps}
        data-testid="svg-container"
      />
    );
  }

  return (
    <Box {...otherProps} data-testid="svg-container">
      {svg}
    </Box>
  );
}
