import type { ResponsiveValue } from '@chakra-ui/react';
import { Box, Flex } from '@chakra-ui/react';
import { Menu } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import {
  akamaiImageLoader,
  formatAssetsUrl,
  isIVMEnabled,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import Image from 'next/image';

import HotelRestaurantMenuDescription from './HotelRestaurantMenuDescription.component';

interface Props {
  menu: Menu;
  isTabPanel?: boolean;
}

export default function HotelRestaurantMenuContent({ menu, isTabPanel }: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();

  const cardStyles = {
    flexWrap: 'wrap' as const,
    m: isTabPanel ? '0.25rem -1rem -1rem -1rem' : 0,
    pt: isTabPanel ? 0 : 5,
  };

  return (
    <Flex {...cardStyles} data-testid={`${menu.name}-menu`}>
      {renderMenuImage()}
      <Box w={{ sm: 'full', md: '50%' }} data-testid="hdp_restaurantMenuContent">
        {renderMenuDescription()}
        {renderMenuButton()}
      </Box>
    </Flex>
  );

  function renderMenuImage() {
    const imageContainerStyles = {
      position: 'relative' as ResponsiveValue<'relative'>,
      mr: { base: 0, md: 6 },
      mb: 5,
      w: {
        base: 'full',
        md: 'calc(50% - var(--chakra-space-6))',
      },
    };

    return (
      <Box {...imageContainerStyles} data-testid={`${menu.name}-menu-image`}>
        <Image
          src={formatAssetsUrl(menu.imageSrc ?? '')}
          alt={menu.name ?? ''}
          width={400}
          height={250}
          style={{
            width: '100%',
            height: 'auto',
          }}
          loader={isIVMEnabled() ? akamaiImageLoader : undefined}
        />
      </Box>
    );
  }

  function renderMenuDescription() {
    return (
      <Box mb="1.188rem" w="full">
        <HotelRestaurantMenuDescription menuDescription={menu.description ?? ''} />
      </Box>
    );
  }

  function renderMenuButton() {
    const buttonStyles = {
      mb: '1.188rem',
      w: {
        base: 'full',
        mobile: 'full',
        xs: 'full',
        sm: '48.5%',
        md: '37%',
        lg: '32%',
        xl: '32%',
      },
      minWidth: {
        sm: '14.3rem',
        md: '16.6rem',
        lg: '24.5rem',
        xl: '26.25rem',
      },
      sx: {
        ':hover': {
          background: 'inherit',
          boxShadow: 'none',
          color: '#9f78a3',
        },
      },
    };

    if (!menu?.menuSrc) {
      return null;
    }

    return (
      <Button
        size="md"
        variant="tertiary"
        {...buttonStyles}
        {...getTypographyProps({}, menuButtonSemanticTypography)}
        onClick={() => window.open(formatAssetsUrl(menu?.menuSrc ?? ''), '_blank')}
      >
        {menu.menuLabel}
      </Button>
    );
  }
}

const menuButtonSemanticTypography = {
  textStyle: 'body-m-emphasis',
};
