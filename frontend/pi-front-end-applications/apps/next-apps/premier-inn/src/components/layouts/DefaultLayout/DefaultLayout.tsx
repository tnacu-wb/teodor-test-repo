import type { BoxProps, StackProps } from '@chakra-ui/react';
import { Box, VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { HIRoomRate, SITE_LEISURE, type PromotionBanner } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { PromoCode, SummerPromo } from '@whitbread-eos/molecules';
import { Header, FooterWrapper as Footer } from '@whitbread-eos/organisms';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize from '~hooks/use-screensize';

interface Props {
  children: React.ReactNode;
  showFooter?: boolean;
  showHeader?: boolean;
  showChildren?: boolean;
  containerStyles?: BoxProps;
  mainStyles?: BoxProps;
  showPromoCode?: boolean;
  showPromotionsNotification?: boolean;
  roomRates?: HIRoomRate[];
  showSummerPromo?: boolean;
  promotionBanner?: PromotionBanner;
  showNotification?: boolean;
  wrapperStyles?: StackProps;
  promoCodeFromUrl?: string | null;
  useNextImageForLogo?: boolean;
}

export default function DefaultLayout({
  children,
  showFooter = true,
  showHeader = true,
  showChildren = true,
  containerStyles,
  mainStyles,
  showPromoCode = false,
  showPromotionsNotification = false,
  roomRates,
  showSummerPromo = false,
  promotionBanner = {} as PromotionBanner,
  showNotification = false,
  wrapperStyles,
  promoCodeFromUrl,
  useNextImageForLogo,
}: Readonly<Props>) {
  useSetScreenSize();
  useSetOrientation();
  const queryClient = useQueryClient();

  const renderHeader = () => (
    <Box as="header" w="full">
      <ErrorBoundary isHeaderBoundary={true}>
        {showPromoCode && <PromoCode roomRates={roomRates} promoCodeFromUrl={promoCodeFromUrl} />}
        {showPromotionsNotification && <Box data-testid="promotions-notification" />}
        <Header variant="default" queryClient={queryClient} useNextImage={useNextImageForLogo} />
        {showSummerPromo && (
          <SummerPromo promotionBanner={promotionBanner} showNotification={showNotification} />
        )}
      </ErrorBoundary>
    </Box>
  );

  const renderMain = () => (
    <Box as="main" w="full" flex="1" {...mainStyles}>
      <Container containerStyles={containerStyles}>{children}</Container>
    </Box>
  );

  const renderFooter = () => (
    <Box w="full">
      <Container>
        <ErrorBoundary isFooterBoundary={true}>
          <Footer isPremierInn={true} site={SITE_LEISURE} />
        </ErrorBoundary>
      </Container>
    </Box>
  );

  return (
    <VStack minH="100vh" height="100vh" {...wrapperStyles}>
      {showHeader && renderHeader()}
      {showChildren && renderMain()}
      {showFooter && renderFooter()}
      <Box w="full" position="sticky" bottom="0" id="hotel-details-mobile-basket" />
    </VStack>
  );
}
