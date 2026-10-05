import type { BoxProps, ContainerProps, FlexProps } from '@chakra-ui/react';
import { Box, Container, Flex, Grid } from '@chakra-ui/react';
import type { HotelBrandType } from '@whitbread-eos/api';
import { Logo } from '@whitbread-eos/atoms';
import { formatAssetsUrl } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import BookingFlowSteps from '../../BookingFlowSteps';
import {
  containerLogoStyle,
  containerWrapperStyles,
  contentStyles,
  headerWrapperStyles,
} from '../HeaderLeisure.style';

interface Props {
  currentLang: string;
  currentCountry: string;
  headerInfoData: any;
  propsStepProgress: { steps: Array<{ id: number; title?: string }>; activeStep: number };
  hotelBrand?: HotelBrandType;
  bb?: boolean;
}

export default function HeaderVariantStep({
  currentLang = 'en',
  currentCountry = 'gb',
  headerInfoData,
  propsStepProgress,
  hotelBrand,
  bb,
}: Readonly<Props>) {
  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  const getHomePath = (isBB: boolean | undefined) => {
    return !isBB
      ? `${origin}/${currentCountry}/${currentLang}/home.html`
      : `${origin}/${currentCountry}/${currentLang}/business-booker/home.html`;
  };

  return (
    <Box {...customHeaderWrapperStyles} data-testid="common-header-wrapper">
      <Container {...customContainerWrapperStyles}>
        <Grid {...contentStyles}>
          <Flex
            {...customContainerLogoStyle}
            data-testid="logo-container"
            sx={{ '@media print': { display: 'none' } }}
          >
            <Logo
              href={getHomePath(bb)}
              src={formatAssetsUrl(headerInfoData?.content?.header?.image)}
              isHeaderLogo={true}
              variant={
                currentLang === 'de' && hotelBrand !== 'hub' && hotelBrand !== 'zip'
                  ? 'pi-simple'
                  : hotelBrand
              }
              transform="scale(1)"
            />
          </Flex>

          <Container
            {...containerLogoBookingFlow}
            data-testid="logo-container"
            sx={{ '@media print': { mb: 'sm' } }}
          >
            <Logo
              src={formatAssetsUrl(headerInfoData?.content?.header?.image)}
              href={getHomePath(bb)}
              variant={hotelBrand === 'pi' ? 'pi-icon' : (`${hotelBrand}-simple` as HotelBrandType)}
            />
          </Container>
          <BookingFlowSteps
            steps={propsStepProgress.steps}
            activeStep={propsStepProgress.activeStep}
          />
        </Grid>
      </Container>
    </Box>
  );
}

const customContainerWrapperStyles = {
  ...containerWrapperStyles,
  pl: { mobile: 'md', md: 'lg', lg: 'xl', xl: '5xl' },
  pr: { mobile: 'md', md: 'lg', lg: 'xl', xl: '5xl' },
} as ContainerProps;

const customContainerLogoStyle = {
  ...containerLogoStyle,
  display: { mobile: 'none', lg: 'flex' },
} as FlexProps;

const customHeaderWrapperStyles = {
  ...headerWrapperStyles,
  h: {
    mobile: 'var(--chakra-space-5xl)',
    sm: 'var(--chakra-space-7xl)',
    md: 'var(--chakra-space-8xl)',
  },
} as BoxProps;

const containerLogoBookingFlow = {
  ...containerLogoStyle,
  display: { mobile: 'flex', lg: 'none' },
  justifyContent: 'flex-start',
  p: 0,
  w: 'auto',
} as FlexProps;
