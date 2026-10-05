import type { BoxProps } from '@chakra-ui/react';
import { Box, VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { SITE_BB } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import dynamic from 'next/dynamic';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize from '~hooks/use-screensize';

interface Props {
  children: React.ReactNode;
  showFooter?: boolean;
  containerStyles?: BoxProps;
  mainStyles?: BoxProps;
}

const Header = dynamic(
  async () => {
    const { Header } = await import('@whitbread-eos/organisms');
    return { default: Header };
  },
  {
    ssr: false,
  }
);

const Footer = dynamic(
  async () => {
    const { FooterWrapper } = await import('@whitbread-eos/organisms');
    return { default: FooterWrapper };
  },
  {
    ssr: false,
  }
);

export default function DefaultLayout({
  children,
  showFooter = true,
  containerStyles,
  mainStyles,
}: Readonly<Props>) {
  useSetScreenSize();
  useSetOrientation();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh" height="100vh">
      <Box as="header" w="full">
        <ErrorBoundary isHeaderBoundary={true}>
          <Header variant="business-default" queryClient={queryClient} />
        </ErrorBoundary>
      </Box>
      <Box as="main" w="full" flex="1" {...mainStyles}>
        <Container containerStyles={containerStyles}>{children}</Container>
      </Box>
      {showFooter && (
        <Box w="full">
          <Container>
            <ErrorBoundary isFooterBoundary={true}>
              <Footer isPremierInn={true} site={SITE_BB} />
            </ErrorBoundary>
          </Container>
        </Box>
      )}

      <Box w="full" position="sticky" bottom="0" id="hotel-details-mobile-basket" />
    </VStack>
  );
}
