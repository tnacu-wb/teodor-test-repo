import { Box, BoxProps, VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { SITE_LEISURE, PI_FAVICON } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { formatAssetsUrl, getUserAndRoles } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import Head from 'next/head';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize from '~hooks/use-screensize';

interface Props {
  children: React.ReactElement;
  showFooter?: boolean;
  containerStyles?: BoxProps;
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
}: Readonly<Props>) {
  useSetScreenSize();
  useSetOrientation();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh" height="100vH">
      <Head>
        <title>Premier Inn</title>
        <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />
      </Head>
      <Box as="header" w="full">
        <ErrorBoundary isHeaderBoundary={true}>
          <Header variant="agent" queryClient={queryClient} {...getUserAndRoles(children)} />
        </ErrorBoundary>
      </Box>
      <Box as="main" w="full" flex="1">
        <ErrorBoundary>
          <Container containerStyles={containerStyles}>{children}</Container>
        </ErrorBoundary>
      </Box>
      {showFooter && (
        <Box w="full">
          <Container>
            <ErrorBoundary isFooterBoundary={true}>
              <Footer site={SITE_LEISURE} />
            </ErrorBoundary>
          </Container>
        </Box>
      )}
      <Box w="full" position="sticky" bottom="0" id="hotel-details-mobile-basket" />
    </VStack>
  );
}
