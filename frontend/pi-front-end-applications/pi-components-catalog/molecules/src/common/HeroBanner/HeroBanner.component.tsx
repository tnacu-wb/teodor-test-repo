import { Box, Flex, Heading, Text, FlexProps } from '@chakra-ui/react';
import { formatAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { ReactNode } from 'react';

interface HeroBannerProps {
  title: string | ReactNode;
  subtitle?: string | ReactNode;
  image: string;
}

export default function HeroBanner({ title, subtitle, image }: Readonly<HeroBannerProps>) {
  return (
    <Flex {...styles.wrapper}>
      <Box {...styles.imageContainer}>
        <Image
          src={formatAssetsUrl(image)}
          alt={'Hero Banner Image'}
          fill
          style={{ objectFit: 'cover' }}
          data-testid="HeroBanner-Image"
        />
      </Box>
      <Box {...styles.contentContainer}>
        <Heading as="h1" {...styles.title} data-testid="HeroBanner-Title">
          {title}
        </Heading>
        {subtitle && (
          <Text {...styles.subtitle} data-testid="HeroBanner-Subtitle">
            {subtitle}
          </Text>
        )}
      </Box>
    </Flex>
  );
}

const styles: Record<string, FlexProps | any> = {
  wrapper: {
    position: 'relative',
    width: '100%',
    height: { base: '180px', md: '224px' },
    align: 'center',
    justify: 'center',
    overflow: 'hidden',
    borderRadius: 0,
    px: {
      base: 0,
      lg: 3,
    },
  },
  imageContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    zIndex: 0,
  },
  contentContainer: {
    position: 'relative',
    zIndex: 1,
    width: 'full',
    maxW: '1228px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    justifyContent: 'center',
    px: { base: '4', lg: '0' },
  },
  title: {
    color: 'white',
    fontSize: { base: '1.8rem', md: '2.5rem' },
    fontWeight: '900',
    textAlign: 'left',
    textShadow: '0 2px 8px rgba(0,0,0,0.5)',
    mb: 0,
  },
  subtitle: {
    color: 'white',
    fontSize: { base: '0.875rem', md: '1rem' },
    fontWeight: '400',
    textAlign: 'left',
    textShadow: '0 2px 8px rgba(0,0,0,0.5)',
    mt: 1,
    maxWidth: '600px',
  },
};
