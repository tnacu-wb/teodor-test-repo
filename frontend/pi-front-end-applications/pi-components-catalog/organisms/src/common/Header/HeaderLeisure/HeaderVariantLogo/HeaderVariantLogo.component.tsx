import type { BoxProps } from '@chakra-ui/react';
import { Box, Container, Flex, Grid } from '@chakra-ui/react';
import type { HotelBrandType } from '@whitbread-eos/api';
import { Logo } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useCustomLocale } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import {
  containerLogoStyle,
  containerWrapperStyles,
  contentStyles,
  headerWrapperStyles,
} from '../HeaderLeisure.style';

interface Props {
  headerInfoData: any;
  hotelBrand?: HotelBrandType;
  isIcon?: boolean;
  bb?: boolean;
}

export default function HeaderVariantLogo({
  headerInfoData,
  hotelBrand,
  isIcon,
  bb = false,
}: Readonly<Props>) {
  const { language: currentLanguage, country: currentCountry } = useCustomLocale();
  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  return (
    <Box {...customHeaderWrapperStyles} data-testid="common-header-wrapper">
      <Container {...containerWrapperStyles}>
        <Grid {...contentStyles} sx={{ '@media print': { py: 'sm' } }}>
          <Flex {...containerLogoStyle} data-testid="logo-container">
            <Logo
              src={formatAssetsUrl(headerInfoData?.content?.header?.image)}
              href={`${origin}/${currentCountry}/${currentLanguage}/${
                bb ? 'business-booker/home.html' : 'home.html'
              }`}
              variant={isIcon && hotelBrand === 'pi' ? 'pi-icon' : hotelBrand}
            />
          </Flex>
        </Grid>
      </Container>
    </Box>
  );
}

const customHeaderWrapperStyles = {
  ...headerWrapperStyles,
  h: { mobile: 'var(--chakra-space-4xl)', lg: 'var(--chakra-space-6xl)' },
} as BoxProps;
